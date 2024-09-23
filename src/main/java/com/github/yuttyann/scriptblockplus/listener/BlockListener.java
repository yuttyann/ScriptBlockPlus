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
import static org.bukkit.block.BlockFace.*;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nullable;

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
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

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

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

/**
 * ScriptBlockPlus BlockListener クラス
 * @author yuttyann44581
 */
public final class BlockListener implements Listener {

    private final Consumer<BukkitTask> CANCEL = b -> { if (b != null) b.cancel(); };
    private final Function<SplitValue, String> SPLIT_VALUE = SplitValue::getValue;
    private final Function<Integer, Set<BukkitTask>> CREATE_SET = b -> new HashSet<>();

    private final IntSet BLOCK_FLAG = new IntOpenHashSet();
    private final Int2ObjectMap<Set<BukkitTask>> LOOP_TASK = new Int2ObjectOpenHashMap<>();

    private int prevSize = ScriptKey.size();
    private ScriptKey[] tempKeys = ScriptKey.values();

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPhysicsEvent(BlockPhysicsEvent event) {
        var block = event.getBlock();
        if (V_1_19.isSupported()) {
            call(block);
            relative(block, UP);
            relative(block, DOWN);
            relative(block, NORTH);
            relative(block, SOUTH);
            relative(block, EAST);
            relative(block, WEST);
        } else {
            search(block);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (V_1_19.isSupported()) {
            final var block = event.getBlock();
            ScriptBlock.getScheduler().run(() -> search(block));
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        if (V_1_19.isSupported()) {
            final var block = event.getBlock();
            ScriptBlock.getScheduler().run(() -> search(block));
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        if (V_1_19.isSupported()) {
            final var block = event.getBlock();
            final var blocks = event.getBlocks();
            ScriptBlock.getScheduler().run(() -> {
                search(block);
                blocks.forEach(this::search);
            });
        }
    }

    private void relative(@NotNull Block block, @NotNull BlockFace face) {
        block = block.getRelative(face);
        if (block.isBlockPowered() || block.isBlockIndirectlyPowered()) {
            call(block);
            if (face != DOWN) {
                call(block.getRelative(UP));
            }
            if (face != UP) {
                call(block.getRelative(DOWN));
            }
            if (face != SOUTH) {
                call(block.getRelative(NORTH));
            }
            if (face != NORTH) {
                call(block.getRelative(SOUTH));
            }
            if (face != WEST) {
                call(block.getRelative(EAST));
            }
            if (face != EAST) {
                call(block.getRelative(WEST));
            }
        }
    }

    private void search(@NotNull Block block) {
        call(block);
        call(block.getRelative(UP));
        call(block.getRelative(DOWN));
        call(block.getRelative(NORTH));
        call(block.getRelative(SOUTH));
        call(block.getRelative(EAST));
        call(block.getRelative(WEST));
    }

    @NotNull
    private void call(@Nullable Block block) {
        if (block == null || ItemUtils.isAIR(block.getType())) {
            return;
        }
        var hash = (block.getX() ^ (block.getZ() << 12)) ^ (block.getY() << 24) ^ block.getWorld().hashCode();
        if (!block.isBlockPowered() && !block.isBlockIndirectlyPowered()) {
            var tasks = LOOP_TASK.get(hash);
            if (tasks != null) {
                tasks.forEach(CANCEL);
            }
            LOOP_TASK.remove(hash);
            BLOCK_FLAG.remove(hash);
        } else if (BLOCK_FLAG.add(hash)) {
            ScriptBlock.getScheduler().asyncRun(() -> onRedstone(block));
        }
        return;
    }

    private synchronized void onRedstone(@NotNull Block block) {
        var blockCoords = (BlockCoords) null;
        for (var scriptKey : getScriptKeys()) {
            var scriptJson = BlockScriptJson.get(scriptKey);
            if (scriptJson.isEmpty()) {
                continue;
            }
            if (blockCoords == null) {
                blockCoords = BlockCoords.of(block);
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
                var rtick = Long.parseLong(filterFirst(values, f -> Repeat.TICK == f.getType()).map(SPLIT_VALUE).orElse("1"));
                var delay = Long.parseLong(filterFirst(values, f -> Repeat.DELAY == f.getType()).map(SPLIT_VALUE).orElse("0"));
                var limit = Integer.parseInt(filterFirst(values, f -> Repeat.LIMIT == f.getType()).map(SPLIT_VALUE).orElse("-1"));
                var rtask = new RepeatTask(limit, repeat, selector, scriptKey, blockCoords);
                LOOP_TASK.computeIfAbsent(blockCoords.hashCode(), CREATE_SET)
                .add(rtask.bukkitTask = ScriptBlock.getScheduler().run(rtask, delay, rtick));
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

            // 非同期で実行する。
            new ScriptRead(true, ScriptBlock.getSBPlayer(player), blockCoords, scriptKey).read(0);
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
    private ScriptKey[] getScriptKeys() {
        return prevSize == ScriptKey.size() ? tempKeys : (this.tempKeys = ScriptKey.values());
    }

    /**
     * ScriptBlockPlus RepeatTask クラス
     * @author yuttyann44581
     */
    private class RepeatTask implements Runnable {
    
        private int count;
        private BukkitTask bukkitTask;

        private final int limit;
        private final Split repeat;
        private final String selector;
        private final ScriptKey scriptKey;
        private final BlockCoords blockCoords;
    
        private RepeatTask(final int limit, @NotNull Split repeat, @NotNull String selector, @NotNull ScriptKey scriptKey, @NotNull BlockCoords blockCoords) {
            this.limit = limit;
            this.repeat = repeat;
            this.selector = selector;
            this.scriptKey = scriptKey;
            this.blockCoords = blockCoords;
        }

        @Override
        public void run() {
            if (limit > -1 && limit <= count++) {
                CANCEL.accept(bukkitTask);
            } else {
                perform(repeat, selector, scriptKey, blockCoords);
            }
        }
    }
}