package com.github.yuttyann.scriptblockplus.listener.item.action.task;

import com.github.yuttyann.scriptblockplus.file.json.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.hook.plugin.ProtocolLib;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.utils.Utils;

import org.bukkit.Color;
import org.bukkit.FluidCollisionMode;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;

public class LookTask extends BlockRunnable {

    public static final String KEY = Utils.randomUUID();

    @Override
    public void run(@NotNull SBPlayer sbPlayer) {
        Player player = sbPlayer.getPlayer();
        double distance = player.getGameMode() == GameMode.CREATIVE ? 5.0D : 4.5D;
        RayTraceResult result = player.rayTraceBlocks(distance, FluidCollisionMode.NEVER);
        if (result == null) {
            sbPlayer.getObjectMap().remove(KEY);
            return;
        }
        Block block = result.getHitBlock();
        Location[] array = { (Location) null, (Location) null };
        if (block != null && has(block.getLocation())) {
            ProtocolLib.INSTANCE.destroyGlowEntity(sbPlayer, block);
            spawnParticlesOnBlock(player, block, Color.GREEN);
            array[0] = block.getLocation();
        }
        BlockFace blockFace = result.getHitBlockFace();
        if (blockFace != null) {
            Block relative = block.getRelative(blockFace);
            if (has(relative.getLocation())) {
                boolean isAIR = relative.getType() == Material.AIR;
                ProtocolLib.INSTANCE.destroyGlowEntity(sbPlayer, relative);
                spawnParticlesOnBlock(player, relative, isAIR ? Color.BLUE : Color.GREEN);
                array[1] = relative.getLocation();
            }
        }
        sbPlayer.getObjectMap().put(KEY, array);
    }

    private boolean has(@NotNull Location location) {
        for (ScriptType scriptType : ScriptType.values()) {
            if (BlockScriptJson.has(location, scriptType)) {
                return true;
            }
        }
        return false;
    }
}