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
import com.github.yuttyann.scriptblockplus.utils.version.McVersion;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

/**
 * ScriptBlockPlus BlockListener クラス
 * @author yuttyann44581
 */
public final class BlockListener implements Listener {

    private static final boolean MC_1_19 = McVersion.V_1_19.isSupported();
    private static final BlockFace[] GENERAL_FACES = { UP, DOWN, NORTH, SOUTH, EAST, WEST };
    private static final Function<SplitValue, String> SPLIT_MAPPER = SplitValue::getValue;

    private final Object2ObjectMap<Block, Set<BukkitTask>> repeatTasks = new Object2ObjectOpenHashMap<>();

    private final Plugin plugin;
    private final Set<Block> checkedBlocks, poweredBlocks;

    public BlockListener(@NotNull Plugin plugin) {
        this.plugin = plugin;
        this.checkedBlocks = new ObjectOpenHashSet<>();
        this.poweredBlocks = new ObjectOpenHashSet<>();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPhysicsEvent(BlockPhysicsEvent event) {
        final var block = event.getBlock();
        if (checkedBlocks.contains(block)) return;
        getScheduler().runTaskAsynchronously(plugin, () -> {
            synchronized (checkedBlocks) {
                if (MC_1_19 ? deepScanBlocks(block) : scanBlocks(block)) cleanup();
            }
        });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (MC_1_19) {
            final var block = event.getBlock();
            if (checkedBlocks.contains(block)) return;
            getScheduler().runTaskAsynchronously(plugin, () -> {
                synchronized (checkedBlocks) {
                    if (scanBlocks(block)) cleanup();
                }
            });
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (MC_1_19) {
            final var block = event.getBlock();
            if (checkedBlocks.contains(block)) return;
            getScheduler().runTaskAsynchronously(plugin, () -> {
                synchronized (checkedBlocks) {
                    if (scanBlocks(block)) cleanup();
                }
            });
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        if (MC_1_19) {
            var blocks = new ObjectArrayList<>(event.getBlocks());
            blocks.add(event.getBlock());
            blocks.removeIf(checkedBlocks::contains);
            if (blocks.isEmpty()) return;
            getScheduler().runTaskAsynchronously(plugin, () -> {
                synchronized (checkedBlocks) {
                    var scanned = false;
                    for (var block : blocks) {
                        if (scanBlocks(block)) scanned = true;
                    }
                    if (scanned) cleanup();
                }
            });
        }
    }

    private boolean scanBlocks(@NotNull Block block) {
        if (checkedBlocks.add(block)) {
            if (isBlockPowered(block)) callRedstone(block);
            for (var face : GENERAL_FACES) {
                var relative = block.getRelative(face);
                if (checkedBlocks.add(block) && isBlockPowered(relative)) callRedstone(relative);
            }
            return true;
        }
        return false;
    }


    private boolean deepScanBlocks(@NotNull Block block) {
        if (checkedBlocks.add(block)) {
            if (isBlockPowered(block)) callRedstone(block);
            for (var face1 : GENERAL_FACES) {
                block = block.getRelative(face1);
                if (checkedBlocks.add(block) && isBlockPowered(block)) {
                    callRedstone(block);
                    for (var face2 : GENERAL_FACES) {
                        var relative = block.getRelative(face2);
                        if (checkedBlocks.add(relative) && isBlockPowered(relative)) callRedstone(relative);
                    }
                }
            }
            return true;
        }
        return false;
    }

    private boolean isBlockPowered(@NotNull Block block) {
        if (block.isBlockPowered() || block.isBlockIndirectlyPowered()) {
            return true;
        } else {
            getScheduler().runTask(plugin, () -> {
                var tasks = repeatTasks.remove(block);
                if (tasks != null) tasks.forEach(BukkitTask::cancel);
            });
            poweredBlocks.remove(block);
            return false;
        }
    }

    private void cleanup() {
        getScheduler().runTaskLater(plugin, checkedBlocks::clear, 1L);
    }

    private void callRedstone(@NotNull Block block) {
        if (poweredBlocks.add(block)) {
            getScheduler().runTask(plugin, () -> {
                if (block == null || ItemUtils.isAIR(block.getType())) return;
                onRedstone(block);
            });
        }
    }

    private void onRedstone(@NotNull Block block) {
        var blockCoords = BlockCoords.of(block);
        for (var scriptKey : ScriptKey.values()) {
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