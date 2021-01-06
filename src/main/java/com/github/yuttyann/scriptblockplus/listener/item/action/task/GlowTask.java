package com.github.yuttyann.scriptblockplus.listener.item.action.task;

import java.util.Collections;

import com.github.yuttyann.scriptblockplus.hook.plugin.ProtocolLib;
import com.github.yuttyann.scriptblockplus.hook.protocol.GlowEntity;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.region.CuboidRegionBlocks;
import com.github.yuttyann.scriptblockplus.region.PlayerRegion;
import com.github.yuttyann.scriptblockplus.utils.Utils;

import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus GlowTask クラス
 * @author yuttyann44581
 */
public class GlowTask extends BlockRunnable {

    public static final String KEY = Utils.randomUUID();

    @Override
    public void run(@NotNull SBPlayer sbPlayer) {
        var region = new PlayerRegion(sbPlayer.getPlayer(), 20);
        var blocks = getBlocks(new CuboidRegionBlocks(region));
        var locations = sbPlayer.getObjectMap().get(LookTask.KEY, Collections.emptySet());
        var glowEntityPacket = ProtocolLib.GLOW_ENTITY;
        for (var block : blocks) {
            if (locations.size() > 0 && locations.stream().anyMatch(l -> block.getLocation().equals(l))) {
                continue;
            }
            var location = block.getLocation();
            if (!glowEntityPacket.has(sbPlayer, location)) {
                var color = glowEntityPacket.getTeamColor(block);
                glowEntityPacket.spawnGlowEntity(sbPlayer, location, color);
            }
        }
        var glowEntities = glowEntityPacket.getEntities().get(sbPlayer.getUniqueId());
        for(var glowEntity : glowEntities.toArray(GlowEntity[]::new)) {
            if(!blocks.stream().anyMatch(b -> glowEntity.equals(b.getX(), b.getY(), b.getZ()))) {
                glowEntityPacket.destroyGlowEntity(glowEntity);
            }
        }
    }
}