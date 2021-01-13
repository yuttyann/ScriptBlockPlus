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

import java.util.HashSet;
import java.util.Set;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.file.json.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.hook.CommandSelector;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;

public class BlockListener implements Listener {

    private static final Set<String> REDSTONE_FLAG = new HashSet<>();

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        var location = event.getBlock().getLocation();
        var fullCoords = BlockCoords.getFullCoords(location);
        if (!event.getBlock().isBlockIndirectlyPowered()) {
            REDSTONE_FLAG.remove(fullCoords);
            return;
        }
        if (REDSTONE_FLAG.contains(fullCoords)) {
            return;
        }
        for (var scriptKey : ScriptKey.values()) {
            var scriptJson = new BlockScriptJson(scriptKey);
            if (!scriptJson.exists()) {
                continue;
            }
            var blockScript = scriptJson.load();
            if (!blockScript.has(location)) {
                continue;
            }
            var selector = blockScript.get(location).getSelector();
            if (StringUtils.isEmpty(selector) || !CommandSelector.INSTANCE.has(selector)) {
                continue;
            }
            for (var target : CommandSelector.INSTANCE.getTargets(location, selector)) {
                if (!(target instanceof Player)) {
                    continue;
                }
                REDSTONE_FLAG.add(fullCoords);
                new ScriptRead((Player) target, location, scriptKey).read(0);
            }
        }
    }
}