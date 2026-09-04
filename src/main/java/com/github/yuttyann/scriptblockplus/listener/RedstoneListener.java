/**
 * ScriptBlockPlus - Allow you to add script to any blocks.
 * Copyright (C) 2021 yuttyann44581
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package com.github.yuttyann.scriptblockplus.listener;

import static com.github.yuttyann.scriptblockplus.utils.StreamUtils.*;
import static org.bukkit.Bukkit.*;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.LongSupplier;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.splittype.Filter;
import com.github.yuttyann.scriptblockplus.enums.splittype.Repeat;
import com.github.yuttyann.scriptblockplus.file.json.derived.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.file.json.derived.element.BlockScript;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.selector.CommandSelector;
import com.github.yuttyann.scriptblockplus.selector.split.Split;
import com.github.yuttyann.scriptblockplus.selector.split.SplitValue;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.version.McVersion;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

/**
 * ScriptBlockPlus RedstoneListener クラス<p>
 * 検知キューと通電状態は、Bukkit の同期イベント・同期タスクからのみ操作します。
 * @author yuttyann44581
 */
public final class RedstoneListener implements Listener {

    private static final Function<SplitValue, String> SPLIT_MAPPER = SplitValue::getValue;

    private static final int[][] SCAN_OFFSETS = createScanOffsets(1);
    private static final int[][] DEEP_SCAN_OFFSETS = createScanOffsets(2);
    private static final int MAX_BLOCKS_PER_FLUSH = 4096;
    private static final long MAX_FLUSH_NANOS = 2_000_000L;
    private static final long SCAN_DELAY_TICKS = 2L;
    private static final long CONTINUE_DELAY_TICKS = 1L;
    private static final int POWER_LOOKUP_RADIUS = 3;

    private final Plugin plugin;
    private final LongSupplier nanoTime;
    private final ObjectLinkedOpenHashSet<BlockKey> queuedBlocks;
    private final Object2IntOpenHashMap<BlockKey> queuedOrigins;
    private final ArrayDeque<ObjectLinkedOpenHashSet<BlockKey>> resumedScans;
    private final Map<UUID, Long2ObjectMap<ObjectLinkedOpenHashSet<BlockKey>>> deferredBlocks;
    private final Map<UUID, Long2ObjectMap<Set<BlockKey>>> poweredBlocks;
    private final Map<BlockKey, Set<BukkitTask>> repeatTasks;
    private boolean scanScheduled;
    private boolean flushing;
    private boolean resumeNext = true;

    private final Consumer<BlockPhysicsEvent> blockPhysics;
    private final Consumer<BlockPlaceEvent> blockPlace;
    private final Consumer<BlockBreakEvent> blockBreak;
    private final Consumer<BlockPistonExtendEvent> blockPistonExtend;
    private final Consumer<BlockPistonRetractEvent> blockPistonRetract;


    public RedstoneListener(@NotNull Plugin plugin) {
        this(plugin, System::nanoTime);
    }

    RedstoneListener(@NotNull Plugin plugin, @NotNull LongSupplier nanoTime) {
        this.plugin = plugin;
        this.nanoTime = nanoTime;
        this.queuedBlocks = new ObjectLinkedOpenHashSet<>(64);
        this.queuedOrigins = new Object2IntOpenHashMap<>(64);
        this.resumedScans = new ArrayDeque<>();
        this.deferredBlocks = new Object2ObjectOpenHashMap<>();
        this.poweredBlocks = new Object2ObjectOpenHashMap<>();
        this.repeatTasks = new ConcurrentHashMap<>();
        if (McVersion.V_1_19.isSupported()) {
            this.blockPhysics = event -> queueDeepScan(event.getBlock());
            this.blockPlace = event -> queueScan(event.getBlock());
            this.blockBreak = event -> queueScan(event.getBlock());
            this.blockPistonExtend = this::queuePistonExtend;
            this.blockPistonRetract = this::queuePistonRetract;
        } else {
            this.blockPhysics = event -> queueScan(event.getBlock());
            this.blockPlace = event -> {};
            this.blockBreak = event -> {};
            this.blockPistonExtend = event -> {};
            this.blockPistonRetract = event -> {};
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockPhysics(@NotNull BlockPhysicsEvent event) {
        blockPhysics.accept(event);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockPlace(@NotNull BlockPlaceEvent event) {
        blockPlace.accept(event);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockBreak(@NotNull BlockBreakEvent event) {
        blockBreak.accept(event);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockPistonExtend(@NotNull BlockPistonExtendEvent event) {
        blockPistonExtend.accept(event);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockPistonRetract(@NotNull BlockPistonRetractEvent event) {
        blockPistonRetract.accept(event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockRedstone(@NotNull BlockRedstoneEvent event) {
        var blockKey = blockKey(event.getBlock());
        if (event.getNewCurrent() > 0) {
            if (addPoweredBlock(blockKey)) onRedstone(event.getBlock());
        } else {
            removePoweredBlock(blockKey);
        }
    }

    private void queuePistonExtend(@NotNull BlockPistonExtendEvent event) {
        queuePistonScan(event.getBlock(), event.getBlocks(), event.getDirection());
    }

    private void queuePistonRetract(@NotNull BlockPistonRetractEvent event) {
        queuePistonScan(event.getBlock(), event.getBlocks(), event.getDirection());
    }

    private void queuePistonScan(@NotNull Block piston, @NotNull List<Block> blocks, @NotNull BlockFace direction) {
        var worldId = piston.getWorld().getUID();
        queueOffsets(blockKey(worldId, piston.getX(), piston.getY(), piston.getZ()), 1);
        for (var block : blocks) {
            int x = block.getX(), y = block.getY(), z = block.getZ();
            queueOffsets(blockKey(worldId, x, y, z), 1);
            queueOffsets(blockKey(worldId, x + direction.getModX(), y + direction.getModY(), z + direction.getModZ()), 1);
        }
        scheduleFlush(SCAN_DELAY_TICKS);
    }

    private void queueScan(@NotNull Block block) {
        queueOffsets(blockKey(block), 1);
        scheduleFlush(SCAN_DELAY_TICKS);
    }

    private void queueDeepScan(@NotNull Block block) {
        queueOffsets(blockKey(block), 2);
        scheduleFlush(SCAN_DELAY_TICKS);
    }

    private void queueOffsets(@NotNull BlockKey origin, int radius) {
        if (!flushing) {
            if (queuedOrigins.getInt(origin) >= radius) return;
            queuedOrigins.put(origin, radius);
        }
        queuedBlocks.add(origin);
        var offsets = radius == 1 ? SCAN_OFFSETS : DEEP_SCAN_OFFSETS;
        for (int i = 1; i < offsets.length; i++) {
            var offset = offsets[i];
            queuedBlocks.add(blockKey(origin.worldId, origin.x + offset[0], origin.y + offset[1], origin.z + offset[2]));
        }
    }

    private void scheduleFlush(long delay) {
        if (!scanScheduled && hasQueuedBlocks()) {
            getScheduler().runTaskLater(plugin, this::flushQueuedBlocks, delay);
            scanScheduled = true;
        }
    }

    private void flushQueuedBlocks() {
        var started = nanoTime.getAsLong();
        try {
            queuedOrigins.clear();
            flushing = true;
            var scanned = 0;
            while (hasQueuedBlocks() && scanned < MAX_BLOCKS_PER_FLUSH) {
                if (scanned > 0 && nanoTime.getAsLong() - started >= MAX_FLUSH_NANOS) {
                    break;
                }
                scanBlock(pollQueuedBlock());
                scanned++;
            }
        } finally {
            flushing = false;
            scanScheduled = false;
            scheduleFlush(CONTINUE_DELAY_TICKS);
        }
    }

    private boolean hasQueuedBlocks() {
        return !queuedBlocks.isEmpty() || !resumedScans.isEmpty();
    }

    @NotNull
    private BlockKey pollQueuedBlock() {
        if (resumedScans.isEmpty() || (!queuedBlocks.isEmpty() && !resumeNext)) {
            resumeNext = true;
            return queuedBlocks.removeFirst();
        }
        resumeNext = false;
        var resumed = resumedScans.getFirst();
        var blockKey = resumed.removeFirst();
        if (resumed.isEmpty()) {
            resumedScans.removeFirst();
        }
        queuedBlocks.remove(blockKey);
        return blockKey;
    }

    private void scanBlock(@NotNull BlockKey blockKey) {
        var world = Bukkit.getWorld(blockKey.worldId);
        if (world == null || !world.isChunkLoaded(blockKey.x >> 4, blockKey.z >> 4)) {
            removeDeferredBlock(blockKey);
            removePoweredBlock(blockKey);
            return;
        }
        var block = world.getBlockAt(blockKey.x, blockKey.y, blockKey.z);
        if (block.isEmpty()) {
            removeDeferredBlock(blockKey);
            removePoweredBlock(blockKey);
            return;
        }
        if (!isPowerAreaLoaded(world, blockKey.x, blockKey.z)) {
            var chunks = deferredBlocks.computeIfAbsent(blockKey.worldId, id -> new Long2ObjectOpenHashMap<>());
            chunks.computeIfAbsent(blockKey.chunkKey(), key -> new ObjectLinkedOpenHashSet<>()).add(blockKey);
            return;
        }
        removeDeferredBlock(blockKey);
        if (!isPowered(block)) {
            removePoweredBlock(blockKey);
        } else if (addPoweredBlock(blockKey)) {
            onRedstone(block);
        }
    }

    private boolean isPowerAreaLoaded(@NotNull World world, int x, int z) {
        for (int chunkX = (x - POWER_LOOKUP_RADIUS) >> 4, maxX = (x + POWER_LOOKUP_RADIUS) >> 4; chunkX <= maxX; chunkX++) {
            for (int chunkZ = (z - POWER_LOOKUP_RADIUS) >> 4, maxZ = (z + POWER_LOOKUP_RADIUS) >> 4; chunkZ <= maxZ; chunkZ++) {
                if ((chunkX != (x >> 4) || chunkZ != (z >> 4)) && !world.isChunkLoaded(chunkX, chunkZ)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isPowered(@NotNull Block block) {
        return block.isBlockPowered() || block.isBlockIndirectlyPowered();
    }

    private boolean addPoweredBlock(@NotNull BlockKey blockKey) {
        var chunks = poweredBlocks.computeIfAbsent(blockKey.worldId, id -> new Long2ObjectOpenHashMap<>());
        return chunks.computeIfAbsent(blockKey.chunkKey(), key -> new ObjectOpenHashSet<>()).add(blockKey);
    }

    private void removePoweredBlock(@NotNull BlockKey blockKey) {
        var chunks = poweredBlocks.get(blockKey.worldId);
        if (chunks != null) {
            var blocks = chunks.get(blockKey.chunkKey());
            if (blocks != null) {
                blocks.remove(blockKey);
                if (blocks.isEmpty()) chunks.remove(blockKey.chunkKey());
            }
            if (chunks.isEmpty()) poweredBlocks.remove(blockKey.worldId);
        }
        cancelRepeatTasks(blockKey);
    }

    private void cancelRepeatTasks(@NotNull BlockKey blockKey) {
        var tasks = repeatTasks.remove(blockKey);
        if (tasks != null) {
            tasks.forEach(BukkitTask::cancel);
        }
    }

    private void finishRepeatTask(@NotNull BlockKey blockKey, @NotNull BukkitTask task) {
        repeatTasks.computeIfPresent(blockKey, (key, tasks) -> {
            tasks.remove(task);
            return tasks.isEmpty() ? null : tasks;
        });
        task.cancel();
    }

    private void removeDeferredBlock(@NotNull BlockKey blockKey) {
        var chunks = deferredBlocks.get(blockKey.worldId);
        if (chunks == null) return;
        var blocks = chunks.get(blockKey.chunkKey());
        if (blocks != null) {
            blocks.remove(blockKey);
            if (blocks.isEmpty()) chunks.remove(blockKey.chunkKey());
        }
        if (chunks.isEmpty()) deferredBlocks.remove(blockKey.worldId);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkLoad(@NotNull ChunkLoadEvent event) {
        var worldId = event.getWorld().getUID();
        var chunks = deferredBlocks.get(worldId);
        if (chunks == null) return;
        int x = event.getChunk().getX(), z = event.getChunk().getZ();
        for (int chunkX = x - 1; chunkX <= x + 1; chunkX++) {
            for (int chunkZ = z - 1; chunkZ <= z + 1; chunkZ++) {
                var blocks = chunks.remove(chunkKey(chunkX, chunkZ));
                if (blocks != null && !blocks.isEmpty()) {
                    resumedScans.addLast(blocks);
                }
            }
        }
        if (chunks.isEmpty()) deferredBlocks.remove(worldId);
        scheduleFlush(SCAN_DELAY_TICKS);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChunkUnload(@NotNull ChunkUnloadEvent event) {
        var worldId = event.getWorld().getUID();
        var key = chunkKey(event.getChunk().getX(), event.getChunk().getZ());
        var deferred = deferredBlocks.get(worldId);
        if (deferred != null) {
            deferred.remove(key);
            if (deferred.isEmpty()) deferredBlocks.remove(worldId);
        }
        var powered = poweredBlocks.get(worldId);
        if (powered != null) {
            var blocks = powered.remove(key);
            if (powered.isEmpty()) poweredBlocks.remove(worldId);
            if (blocks != null) blocks.forEach(this::cancelRepeatTasks);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorldUnload(@NotNull WorldUnloadEvent event) {
        var worldId = event.getWorld().getUID();
        deferredBlocks.remove(worldId);
        var chunks = poweredBlocks.remove(worldId);
        if (chunks != null) {
            chunks.values().forEach(blocks -> blocks.forEach(this::cancelRepeatTasks));
        }
    }

    @NotNull
    private static int[][] createScanOffsets(int radius) {
        var offsets = new java.util.ArrayList<int[]>();
        offsets.add(new int[] { 0, 0, 0 });
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    int distance = Math.abs(x) + Math.abs(y) + Math.abs(z);
                    if (distance > 0 && distance <= radius) offsets.add(new int[] { x, y, z });
                }
            }
        }
        return offsets.toArray(new int[offsets.size()][]);
    }

    private static long chunkKey(int x, int z) {
        return ((long) x << 32) | (z & 0xffffffffL);
    }

    private void onRedstone(@NotNull Block block) {
        var blockCoords = BlockCoords.of(block);
        for (var scriptKey : ScriptKey.iterable()) {
            var scriptJson = BlockScriptJson.get(scriptKey);
            if (scriptJson.isEmpty()) {
                continue;
            }
            var blockScript = scriptJson.fastLoad(blockCoords);
            if (blockScript == null || !blockScript.hasValues()) {
                continue;
            }
            var selector = blockScript.getSafeValue(BlockScript.SELECTOR).asString();
            if (selector.isEmpty() || !CommandSelector.has(selector)) {
                continue;
            }
            var repeat = new Split(selector, "repeat", "{", "}");
            if (repeat.length() > 0) {
                var values = repeat.getValues(Repeat.values());
                var period = Long.parseLong(filterFirst(values, f -> Repeat.TICK == f.getType()).map(SPLIT_MAPPER).orElse("1"));
                var delay = Long.parseLong(filterFirst(values, f -> Repeat.DELAY == f.getType()).map(SPLIT_MAPPER).orElse("0"));
                var limit = Integer.parseInt(filterFirst(values, f -> Repeat.LIMIT == f.getType()).map(SPLIT_MAPPER).orElse("-1"));
                var blockKey = blockKey(block);
                var repeatTask = new BukkitRunnable() {

                    private int index;
                    private BukkitTask task;

                    @Override
                    public void run() {
                        if (limit > -1 && limit <= index++) {
                            finishRepeatTask(blockKey, task);
                        } else {
                            perform(repeat, selector, scriptKey, blockCoords);
                        }
                    }
                };
                var bukkitTask = (repeatTask.task = repeatTask.runTaskTimer(plugin, delay, period));
                if (bukkitTask.isCancelled()) {
                    return;
                }
                repeatTasks.compute(blockKey, (k, v) -> {
                    if (v == null) {
                        return ObjectOpenHashSet.of(bukkitTask);
                    } else {
                        v.add(bukkitTask);
                        return v;
                    }
                });
            } else {
                perform(repeat, selector, scriptKey, blockCoords);
            }
        }
    }

    private void perform(@NotNull Split repeat, @NotNull String selector, @NotNull ScriptKey scriptKey, @NotNull BlockCoords blockCoords) {
        var filter = new Split(selector, "filter", "{", "}", repeat.length());
        var target = new Split(selector, "@", 1, "[", "]", repeat.length() + filter.length());
        var values = filter.getValues(Filter.values());
        var index = new AtomicInteger();
        for (var entity : CommandSelector.getTargets(Bukkit.getConsoleSender(), blockCoords.toLocation(), target.toString())) {
            if (!(entity instanceof Player)) {
                continue;
            }
            var player = (Player) entity;
            if (!allMatch(values, s -> has(s, player, index.get()))) {
                continue;
            }
            index.incrementAndGet();
            new ScriptRead(ScriptBlock.getSBPlayer(player), blockCoords, scriptKey).read(0);
        }
    }

    private boolean has(@NotNull SplitValue splitValue, @NotNull Player player, int index) {
        var value = splitValue.getValue();
        if (StringUtils.isEmpty(value)) {
            return false;
        }
        switch ((Filter) splitValue.getType()) {
            case OP:
                return Boolean.parseBoolean(value) ? player.isOp() : !player.isOp();
            case PERM:
                return player.hasPermission(value);
            case LIMIT:
                return index < Integer.parseInt(value);
            default:
                return false;
        }
    }

    @NotNull
    private BlockKey blockKey(@NotNull Block block) {
        return blockKey(block.getWorld().getUID(), block.getX(), block.getY(), block.getZ());
    }

    @NotNull
    private BlockKey blockKey(@NotNull UUID worldId, int x, int y, int z) {
        var hashCode = worldId.hashCode();
        hashCode = 31 * hashCode + x;
        hashCode = 31 * hashCode + y;
        hashCode = 31 * hashCode + z;
        return new BlockKey(worldId, x, y, z, hashCode);
    }

    private static final class BlockKey {

        private final UUID worldId;
        private final int x;
        private final int y;
        private final int z;
        private final int hash;

        private BlockKey(@NotNull UUID worldId, int x, int y, int z, int hash) {
            this.worldId = worldId;
            this.x = x;
            this.y = y;
            this.z = z;
            this.hash = hash;
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof BlockKey)) return false;
            var other = (BlockKey) obj;
            return x == other.x && y == other.y && z == other.z && worldId.equals(other.worldId);
        }

        private long chunkKey() {
            return RedstoneListener.chunkKey(x >> 4, z >> 4);
        }
    }
}
