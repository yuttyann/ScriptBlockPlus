package com.github.yuttyann.scriptblockplus.listener.item.action.task;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

import com.github.yuttyann.scriptblockplus.hook.plugin.GlowEntity;
import com.github.yuttyann.scriptblockplus.hook.plugin.ProtocolLib;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.region.CuboidRegionBlocks;
import com.github.yuttyann.scriptblockplus.region.PlayerRegion;
import com.github.yuttyann.scriptblockplus.region.Region;
import com.github.yuttyann.scriptblockplus.utils.Utils;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public class GlowTask extends BlockRunnable {

    public static final String KEY = Utils.randomUUID();

    private static final Location[] EMPTY_ARRAY = new Location[0];

    @Override
    public void run(@NotNull SBPlayer sbPlayer) {
        Region region = new PlayerRegion(sbPlayer.getPlayer(), 20);
        Set<Block> blocks = getBlocks(new CuboidRegionBlocks(region));
        try {
            Location[] array = sbPlayer.getObjectMap().get(LookTask.KEY, EMPTY_ARRAY);
            for (Block block : blocks) {
                if (array.length > 0 && Stream.of(array).anyMatch(l -> block.getLocation().equals(l))) {
                    continue;
                }
                if (!ProtocolLib.INSTANCE.has(sbPlayer, block)) {
                    ProtocolLib.INSTANCE.spawnGlowEntity(sbPlayer, block);
                }
            }
            Collection<GlowEntity> glowEntities = ProtocolLib.INSTANCE.getGlowEntities().get(sbPlayer.getUniqueId());
            for(GlowEntity glowEntity : glowEntities.toArray(new GlowEntity[0])) {
                if(!blocks.stream().anyMatch(b -> glowEntity.equals(b.getX(), b.getY(), b.getZ()))) {
                    ProtocolLib.INSTANCE.destroyGlowEntity(glowEntity);
                }
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}