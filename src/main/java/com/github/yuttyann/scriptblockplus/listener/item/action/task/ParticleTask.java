package com.github.yuttyann.scriptblockplus.listener.item.action.task;

import java.util.Set;

import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.region.CuboidRegionBlocks;
import com.github.yuttyann.scriptblockplus.region.PlayerRegion;
import com.github.yuttyann.scriptblockplus.region.Region;

import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public class ParticleTask extends BlockRunnable {
    
    private static final int LIMIT = 800;

    @Override
    public void run(@NotNull SBPlayer sbPlayer) {
        int count = 0;
        Region region = new PlayerRegion(sbPlayer.getPlayer(), 10);
        Set<Block> blocks = getBlocks(new CuboidRegionBlocks(region));
        for (Block block : blocks) {
            if (count++ < LIMIT) {
                spawnParticlesOnBlock(sbPlayer.getPlayer(), block, null);
            }
        }
    }
}
