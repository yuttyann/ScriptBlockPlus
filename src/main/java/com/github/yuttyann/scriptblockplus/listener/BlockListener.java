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