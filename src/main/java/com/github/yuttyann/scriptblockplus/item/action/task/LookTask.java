package com.github.yuttyann.scriptblockplus.item.action.task;

import java.util.LinkedHashSet;
import java.util.Set;

import com.github.yuttyann.scriptblockplus.file.json.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.hook.plugin.ProtocolLib;
import com.github.yuttyann.scriptblockplus.raytrace.RayTrace;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.utils.Utils;

import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus LookTask クラス
 * @author yuttyann44581
 */
public class LookTask extends BlockRunnable {

    public static final String KEY = Utils.randomUUID();

    @Override
    public void run(@NotNull SBPlayer sbPlayer) {
        var player = sbPlayer.getPlayer();
        var rayTrace = new RayTrace(player.getWorld());
        var rayResult = rayTrace.rayTraceBlocks(player, getDistance(player));
        if (rayResult == null) {
            sbPlayer.getObjectMap().remove(KEY);
            return;
        }
        var hitBlock = rayResult.getHitBlock();
        var locations = new LinkedHashSet<Location>();
        if (hitBlock != null) {
            destroyEntity(sbPlayer, hitBlock.getLocation(), locations, true);
        }
        var blockFace = rayResult.getHitBlockFace();
        if (blockFace != null) {
            destroyEntity(sbPlayer, hitBlock.getRelative(blockFace).getLocation(), locations, true);
        }
        if (locations.size() > 0) {
            Location first = locations.stream().findFirst().get();
            for (var location : rayTrace.rayTraceBlocks(player, getDistance(player), 0.01D, true)) {
                var blockLocation = location.getBlock().getLocation();
                if (first.equals(blockLocation)) {
                    break;
                }
                if (!locations.contains(blockLocation)) {
                    destroyEntity(sbPlayer, blockLocation, locations, false);
                }
            }
        }
        sbPlayer.getObjectMap().put(KEY, locations);
    }

    private boolean destroyEntity(@NotNull SBPlayer sbPlayer, @NotNull Location location, @NotNull Set<Location> locations, final boolean particle) {
        if (!has(location)) {
            return false;
        }
        try {
            ProtocolLib.GLOW_ENTITY.destroyGlowEntity(sbPlayer, location);
        } finally {
            if (particle) {
                var block = location.getBlock();
                boolean isAIR = block.getType() == Material.AIR;
                spawnParticlesOnBlock(sbPlayer.getPlayer(), block, isAIR ? Color.BLUE : Color.GREEN);
            }
            locations.add(location);
        }
        return true;
    }

    private double getDistance(@NotNull Player player) {
        return player.getGameMode() == GameMode.CREATIVE ? 5.0D : 4.5D;
    }

    private boolean has(@NotNull Location location) {
        for (var scriptKey : ScriptKey.values()) {
            if (BlockScriptJson.has(location, scriptKey)) {
                return true;
            }
        }
        return false;
    }
}