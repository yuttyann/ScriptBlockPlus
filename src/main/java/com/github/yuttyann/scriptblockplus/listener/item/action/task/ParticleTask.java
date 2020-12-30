package com.github.yuttyann.scriptblockplus.listener.item.action.task;

import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.region.CuboidRegionBlocks;
import com.github.yuttyann.scriptblockplus.region.PlayerRegion;

import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus ParticleTask クラス
 * @author yuttyann44581
 */
public class ParticleTask extends BlockRunnable {
    
    private static final int LIMIT = 800;

    @Override
    public void run(@NotNull SBPlayer sbPlayer) {
        int count = 0;
        var region = new PlayerRegion(sbPlayer.getPlayer(), 10);
        for (var block : getBlocks(new CuboidRegionBlocks(region))) {
            if (count++ < LIMIT) {
                spawnParticlesOnBlock(sbPlayer.getPlayer(), block, null);
            }
        }
    }
}
