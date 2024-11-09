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
import static com.github.yuttyann.scriptblockplus.utils.version.McVersion.*;
import static org.bukkit.Bukkit.*;
import static org.bukkit.block.BlockFace.*;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
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
import com.github.yuttyann.scriptblockplus.utils.ItemUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;

/**
 * ScriptBlockPlus BlockListener クラス
 * @author yuttyann44581
 */
public final class BlockListener implements Listener {

    private static final long CACHE_EXPIRY_TIME = 300L;
    private static final boolean MC_1_19 = V_1_19.isSupported(), MC_1_13 = V_1_13.isUnSupported();
    private static final BlockFace[] GENERAL_FACES = { UP, DOWN, NORTH, SOUTH, EAST, WEST };
    private static final Function<SplitValue, String> SPLIT_MAPPER = SplitValue::getValue;

    private final Plugin plugin;
    private final ObjectSet<Block> checkedBlocks, poweredBlocks;
    private final Object2LongMap<Block> poweredCache;
    private final Object2ObjectMap<Block, Set<BukkitTask>> repeatTasks;

    public BlockListener(@NotNull Plugin plugin) {
        this.plugin = plugin;
        this.checkedBlocks = new ObjectOpenHashSet<>(16, 0.5F);
        this.poweredBlocks = new ObjectOpenHashSet<>();
        this.poweredCache = new Object2LongOpenHashMap<>(16, 0.5F);
        this.repeatTasks = new Object2ObjectOpenHashMap<>();
        poweredCache.defaultReturnValue(-1);
        schedulePoweredCacheCleanup();
        scheduleCheckedBlocksCleanup();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPhysicsEvent(BlockPhysicsEvent event) {
        if (MC_1_19) {
            deepScanBlocks(event.getBlock());
        } else {
            scanBlocks(event.getBlock());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (MC_1_19) {
            scanBlocks(event.getBlock());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (MC_1_19) {
            scanBlocks(event.getBlock());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        if (MC_1_19) {
            scanBlocks(event.getBlock());
            for (var block : event.getBlocks()) scanBlocks(block);
        }
    }

    private void scanBlocks(@NotNull Block block) {
        if (checkedBlocks.add(block)) {
            for (var face : GENERAL_FACES) if (checkedBlocks.add(block.getRelative(face)));
        }
    }

    private void deepScanBlocks(@NotNull Block block) {
        if (checkedBlocks.add(block)) {
            for (var face1 : GENERAL_FACES) {
                if (checkedBlocks.add(block = block.getRelative(face1))) {
                    for (var face2 : GENERAL_FACES) {
                        if (checkedBlocks.add(block.getRelative(face2)));
                    }
                }
            }
        }
    }

    private boolean isBlockPowered(@NotNull Block block, long currentTime) {
        if (isBlockEmpty(block) || !isBlockPoweredCached(block, currentTime)) {
            if (poweredBlocks.remove(block)) {
                var tasks = repeatTasks.remove(block);
                if (tasks != null) tasks.forEach(BukkitTask::cancel);
            }
            return false;
        }
        return true;
    }

    private boolean isBlockPoweredCached(@Nullable Block block, long currentTime) {
        if (isValidPoweredCache(block, currentTime)) {
            return true;
        }
        if (block.isBlockPowered() || block.isBlockIndirectlyPowered()) {
            poweredCache.put(block, currentTime);
            return true;
        } else {
            poweredCache.removeLong(block);
            return false;
        }
    }

    private boolean isValidPoweredCache(@Nullable Block block, long currentTime) {
        var cacheTime = poweredCache.getLong(block);
        return cacheTime != -1 && (currentTime - cacheTime) < CACHE_EXPIRY_TIME;
    }

    private boolean isBlockEmpty(@NotNull Block block) {
        return MC_1_13 ? block.isEmpty() : ItemUtils.isAIR(block.getType());
    }

    private void scheduleCheckedBlocksCleanup() {
        getScheduler().runTaskTimer(plugin, () -> {
            if (checkedBlocks.isEmpty()) return;
            var currentTime = System.currentTimeMillis();
            var iterator = checkedBlocks.iterator();
            while (iterator.hasNext()) {
                var block = iterator.next();
                iterator.remove();
                if (isBlockPowered(block, currentTime)) callRedstone(block);
            }
        }, 2L, 2L);
    }

    private void schedulePoweredCacheCleanup() {
        getScheduler().runTaskTimer(plugin, () -> {
            if (poweredCache.isEmpty()) return;
            var currentTime = System.currentTimeMillis();
            var iterator = poweredCache.keySet().iterator();
            while (iterator.hasNext()) {
                if (!isValidPoweredCache(iterator.next(), currentTime)) {
                    iterator.remove();
                }
            }
        }, 6000L, 6000L);
    }

    private void callRedstone(@NotNull Block block) {
        if (poweredBlocks.add(block)) {
            getScheduler().runTask(plugin, () -> onRedstone(block));
        }
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
                var repeatTask = new BukkitRunnable() {

                    private int index;
                    private BukkitTask task;

                    @Override
                    public void run() {
                        if (limit > -1 && limit <= index++) {
                            var tasks = repeatTasks.remove(block);
                            if (tasks != null) tasks.remove(task);
                            cancel();
                        } else {
                            perform(repeat, selector, scriptKey, blockCoords);
                        }
                    }
                };
                var bukkitTask = (repeatTask.task = repeatTask.runTaskTimer(plugin, delay, period));
                if (bukkitTask.isCancelled()) {
                    return;
                }
                repeatTasks.compute(block, (k, v) -> {
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
}