package com.github.yuttyann.scriptblockplus.listener.item.action.task;

import java.util.stream.Stream;

import com.github.yuttyann.scriptblockplus.hook.plugin.ProtocolLib;
import com.github.yuttyann.scriptblockplus.hook.protocol.GlowEntity;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.region.CuboidRegionBlocks;
import com.github.yuttyann.scriptblockplus.region.PlayerRegion;
import com.github.yuttyann.scriptblockplus.utils.Utils;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus GlowTask クラス
 * @author yuttyann44581
 */
public class GlowTask extends BlockRunnable {

    public static final String KEY = Utils.randomUUID();

    private static final Location[] EMPTY_ARRAY = new Location[0];

    @Override
    public void run(@NotNull SBPlayer sbPlayer) {
        var region = new PlayerRegion(sbPlayer.getPlayer(), 20);
        var blocks = getBlocks(new CuboidRegionBlocks(region));
        var arrays = sbPlayer.getObjectMap().get(LookTask.KEY, EMPTY_ARRAY);
        for (var block : blocks) {
            if (arrays.length > 0 && Stream.of(arrays).anyMatch(l -> block.getLocation().equals(l))) {
                continue;
            }
            if (!ProtocolLib.GLOW_ENTITY.has(sbPlayer, block)) {
                ProtocolLib.GLOW_ENTITY.spawnGlowEntity(sbPlayer, block);
            }
        }
        var glowEntities = ProtocolLib.GLOW_ENTITY.getEntities().get(sbPlayer.getUniqueId());
        for(var glowEntity : glowEntities.toArray(GlowEntity[]::new)) {
            if(!blocks.stream().anyMatch(b -> glowEntity.equals(b.getX(), b.getY(), b.getZ()))) {
                ProtocolLib.GLOW_ENTITY.destroyGlowEntity(glowEntity);
            }
        }
    }
}