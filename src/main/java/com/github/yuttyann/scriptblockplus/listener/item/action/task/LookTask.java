package com.github.yuttyann.scriptblockplus.listener.item.action.task;

import com.github.yuttyann.scriptblockplus.file.json.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.hook.plugin.ProtocolLib;
import com.github.yuttyann.scriptblockplus.listener.raytrace.RayTrace;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
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
        var result = new RayTrace(player.getWorld()).rayTrace(player, getDistance(player));
        if (result == null) {
            sbPlayer.getObjectMap().remove(KEY);
            return;
        }
        var block = result.getHitBlock();
        var arrays = new Location[2];
        if (block != null && has(block.getLocation())) {
            ProtocolLib.GLOW_ENTITY.destroyGlowEntity(sbPlayer, block);
            spawnParticlesOnBlock(player, block, Color.GREEN);
            arrays[0] = block.getLocation();
        }
        var blockFace = result.getHitBlockFace();
        if (blockFace != null) {
            var relative = block.getRelative(blockFace);
            if (has(relative.getLocation())) {
                boolean isAIR = relative.getType() == Material.AIR;
                ProtocolLib.GLOW_ENTITY.destroyGlowEntity(sbPlayer, relative);
                spawnParticlesOnBlock(player, relative, isAIR ? Color.BLUE : Color.GREEN);
                arrays[1] = relative.getLocation();
            }
        }
        sbPlayer.getObjectMap().put(KEY, arrays);
    }

    private double getDistance(@NotNull Player player) {
        return player.getGameMode() == GameMode.CREATIVE ? 5.0D : 4.5D;
    }

    private boolean has(@NotNull Location location) {
        for (var scriptType : ScriptType.values()) {
            if (BlockScriptJson.has(location, scriptType)) {
                return true;
            }
        }
        return false;
    }
}