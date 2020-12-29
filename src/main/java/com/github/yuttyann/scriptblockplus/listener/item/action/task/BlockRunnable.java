package com.github.yuttyann.scriptblockplus.listener.item.action.task;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.github.yuttyann.scriptblockplus.file.json.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.file.json.element.BlockScript;
import com.github.yuttyann.scriptblockplus.listener.item.action.ScriptViewer;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.region.CuboidRegionBlocks;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.utils.Utils;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus BlockRunnable クラス
 * @author yuttyann44581
 */
public abstract class BlockRunnable extends BukkitRunnable {

    @Override
    public final void run() {
        for (UUID uuid : ScriptViewer.PLAYERS) {
            SBPlayer sbPlayer = SBPlayer.fromUUID(uuid);
            if (sbPlayer.isOnline()) {
                run(sbPlayer);
            }
        }
    }

    protected abstract void run(@NotNull SBPlayer player);

    @NotNull
    public Set<Block> getBlocks(@NotNull CuboidRegionBlocks regionBlocks) {
        Set<Block> set = new HashSet<>();
        Set<Block> blocks = regionBlocks.getBlocks();
        for (ScriptType scriptType : ScriptType.values()) {
            BlockScript blockScript = new BlockScriptJson(scriptType).load();
            for (Block block : blocks) {
                if (blockScript.has(block.getLocation())) {
                    set.add(block);
                }
            }
        }
        return set;
    }

    public void spawnParticlesOnBlock(@NotNull Player player, @NotNull Block block, @Nullable Color color) {
        if (color == null) {
            color = block.getType() == Material.AIR ? Color.AQUA : Color.LIME;
        }
        double x = block.getX(), y = block.getY(), z = block.getZ(), a = 1;
        if (Utils.isCBXXXorLater("1.13")) {
            DustOptions dust = new DustOptions(color, 1);
            player.spawnParticle(Particle.REDSTONE, x, y, z, 0, 0, 0, 0, dust);
            player.spawnParticle(Particle.REDSTONE, x + a, y, z, 0, 0, 0, 0, dust);
            player.spawnParticle(Particle.REDSTONE, x + a, y, z + a, 0, 0, 0, 0, dust);
            player.spawnParticle(Particle.REDSTONE, x, y, z + a, 0, 0, 0, 0, dust);
            player.spawnParticle(Particle.REDSTONE, x, y + a, z, 0, 0, 0, 0, dust);
            player.spawnParticle(Particle.REDSTONE, x + a, y + a, z, 0, 0, 0, 0, dust);
            player.spawnParticle(Particle.REDSTONE, x + a, y + a, z + a, 0, 0, 0, 0, dust);
            player.spawnParticle(Particle.REDSTONE, x, y + a, z + a, 0, 0, 0, 0, dust);
        } else {
            double r = (color.getRed() - 0.0001) / 255D;
            double g = (color.getGreen() - 0.0001) / 255D;
            double b = (color.getBlue() - 0.0001) / 255D;
            player.spawnParticle(Particle.REDSTONE, x, y, z, 0, r, g, b, 1);
            player.spawnParticle(Particle.REDSTONE, x + a, y, z, 0, r, g, b, 1);
            player.spawnParticle(Particle.REDSTONE, x + a, y, z + a, 0, r, g, b, 1);
            player.spawnParticle(Particle.REDSTONE, x, y, z + a, 0, r, g, b, 1);
            player.spawnParticle(Particle.REDSTONE, x, y + a, z, 0, r, g, b, 1);
            player.spawnParticle(Particle.REDSTONE, x + a, y + a, z, 0, r, g, b, 1);
            player.spawnParticle(Particle.REDSTONE, x + a, y + a, z + a, 0, r, g, b, 1);
            player.spawnParticle(Particle.REDSTONE, x, y + a, z + a, 0, r, g, b, 1);
        }
    }
}