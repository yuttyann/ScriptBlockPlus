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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.splittype.Filter;
import com.github.yuttyann.scriptblockplus.enums.splittype.Repeat;
import com.github.yuttyann.scriptblockplus.file.json.derived.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.file.json.derived.element.BlockScript;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.utils.ItemUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import com.github.yuttyann.scriptblockplus.selector.CommandSelector;
import com.github.yuttyann.scriptblockplus.selector.split.Split;
import com.github.yuttyann.scriptblockplus.selector.split.SplitValue;

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

import static com.github.yuttyann.scriptblockplus.utils.StreamUtils.*;

/**
 * ScriptBlockPlus BlockListener クラス
 * @author yuttyann44581
 */
public final class BlockListener implements Listener {

    private final Consumer<BukkitTask> CANCEL = b -> { if (b != null) b.cancel(); };
    private final Function<SplitValue, String> SPLIT_VALUE = SplitValue::getValue;
    private final Function<BlockCoords, Set<BukkitTask>> CREATE_SET = b -> new HashSet<>();

    private final Set<BlockCoords> REDSTONE_FLAG = new HashSet<>();
    private final Map<BlockCoords, Set<BukkitTask>> LOOP_TASK = new HashMap<>();

    private final BlockFace[] FACES = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN };

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPhysicsEvent(BlockPhysicsEvent event) {
        var block = event.getBlock();
        if (Utils.isCBXXXorLater("1.19")) {
            call(block);
            for (int i = 0, l = FACES.length; i < l; i++) {
                var relative = block;
                do {
                    search(relative = relative.getRelative(FACES[i]));
                } while (relative.isBlockPowered() || relative.isBlockIndirectlyPowered());
            }
        } else {
            search(block);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (Utils.isCBXXXorLater("1.19")) {
            var block = event.getBlock();
            ScriptBlock.getScheduler().run(() -> search(block));
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        if (Utils.isCBXXXorLater("1.19")) {
            var block = event.getBlock();
            ScriptBlock.getScheduler().run(() -> search(block));
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        if (Utils.isCBXXXorLater("1.19")) {
            var block = event.getBlock(); var blocks = event.getBlocks();
            ScriptBlock.getScheduler().run(() -> { search(block); blocks.forEach(this::search); });
        }
    }

    private void search(@NotNull Block block) {
        call(block);
        for (var BlockFace : FACES) {
            call(block.getRelative(BlockFace));
        }
    }

    @NotNull
    private void call(@Nullable Block block) {
        if (block == null || ItemUtils.isAIR(block.getType())) {
            return;
        }
        var blockCoords = BlockCoords.of(block);
        if (!block.isBlockPowered() && !block.isBlockIndirectlyPowered()) {
            LOOP_TASK.getOrDefault(blockCoords, Collections.emptySet()).forEach(CANCEL);
            LOOP_TASK.remove(blockCoords);
            REDSTONE_FLAG.remove(blockCoords);
        } else if (!REDSTONE_FLAG.contains(blockCoords)) {
            REDSTONE_FLAG.add(blockCoords);
            onRedstone(block, blockCoords);
        }
        return;
    }

    private void onRedstone(@NotNull Block block, @NotNull BlockCoords blockCoords) {
        for (var scriptKey : ScriptKey.iterable()) {
            var scriptJson = BlockScriptJson.get(scriptKey);
            if (scriptJson.isEmpty()) {
                continue;
            }
            var blockScript = scriptJson.fastLoad(blockCoords);
            if (blockScript == null) {
                continue;
            }
            var valueHolder = blockScript.getValue(BlockScript.SELECTOR);
            if (valueHolder.isEmpty()) {
                continue;
            }
            var selector = valueHolder.get().asString();
            if (StringUtils.isEmpty(selector) || !CommandSelector.has(selector)) {
                continue;
            }
            var repeat = new Split(selector, "repeat", "{", "}");
            if (repeat.length() > 0) {
                var values = repeat.getValues(Repeat.values());
                var rtick = Long.parseLong(filterFirst(values, f -> Repeat.TICK == f.getType()).map(SPLIT_VALUE).orElse("1"));
                var delay = Long.parseLong(filterFirst(values, f -> Repeat.DELAY == f.getType()).map(SPLIT_VALUE).orElse("0"));
                var limit = Integer.parseInt(filterFirst(values, f -> Repeat.LIMIT == f.getType()).map(SPLIT_VALUE).orElse("-1"));
                var rtask = new RepeatTask(limit, repeat, selector, scriptKey, blockCoords);
                LOOP_TASK.computeIfAbsent(blockCoords, CREATE_SET).add(rtask.bukkitTask = ScriptBlock.getScheduler().asyncRun(rtask, delay, rtick));
            } else {
                ScriptBlock.getScheduler().asyncRun(() -> perform(repeat, selector, scriptKey, blockCoords));
            }
        }
    }

    private synchronized void perform(@NotNull Split repeat, @NotNull String selector, @NotNull ScriptKey scriptKey, @NotNull BlockCoords blockCoords) {
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