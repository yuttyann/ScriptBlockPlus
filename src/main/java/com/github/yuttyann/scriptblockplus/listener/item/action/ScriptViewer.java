package com.github.yuttyann.scriptblockplus.listener.item.action;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.listener.item.ItemAction;
import com.github.yuttyann.scriptblockplus.manager.MapManager;
import com.github.yuttyann.scriptblockplus.player.PlayerData;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.region.CuboidRegionBlocks;
import com.github.yuttyann.scriptblockplus.region.PlayerRegion;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.utils.ItemUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus ScriptViewer クラス
 * @author yuttyann44581
 */
public class ScriptViewer extends ItemAction {

    private static final String KEY_VIEWER = PlayerData.createRandomId("ScriptViewer");

    static {
        new Task().runTaskTimer(ScriptBlock.getInstance(), 0L, 8L);
    }

    public ScriptViewer() {
        super(ItemUtils.getScriptViewer());
    }

    @Override
    public boolean run() {
        SBPlayer sbPlayer = SBPlayer.fromPlayer(player);
        switch (action) {
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
                sbPlayer.getObjectMap().put(KEY_VIEWER, true);
                SBConfig.SCRIPT_VIEWER_START.send(player);
                break;
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                sbPlayer.getObjectMap().put(KEY_VIEWER, false);
                SBConfig.SCRIPT_VIEWER_STOP.send(player);
                break;
            default:
        }
        return true;
    }

    private static class Task extends BukkitRunnable {

        @Override
        public void run() {
            MapManager mapManager = ScriptBlock.getInstance().getMapManager();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!SBPlayer.fromPlayer(player).getObjectMap().getBoolean(KEY_VIEWER)) {
                    continue;
                }
                new CuboidRegionBlocks(new PlayerRegion(player, 15)).forEach(b -> {
                    Block block = b.getBlock(player.getWorld());
                    for (ScriptType scriptType : ScriptType.values()) {
                        if (mapManager.containsCoords(block.getLocation(), scriptType)) {
                            spawnParticlesOnBlock(player, block, block.getType()== Material.AIR ? Color.AQUA : Color.LIME);
                        }
                    }
                });
            }
        }

        private void spawnParticlesOnBlock(@NotNull Player player, @NotNull Block block, @NotNull Color color) {
            double x = block.getX();
            double y = block.getY();
            double z = block.getZ();
            if (Utils.isCBXXXorLater("1.13")) {
                Particle.DustOptions dust = new Particle.DustOptions(color, 1);
                player.spawnParticle(Particle.REDSTONE, x, y, z, 0, 0, 0, 0, dust);
                player.spawnParticle(Particle.REDSTONE, x + 1, y, z, 0, 0, 0, 0, dust);
                player.spawnParticle(Particle.REDSTONE, x + 1, y, z + 1, 0, 0, 0, 0, dust);
                player.spawnParticle(Particle.REDSTONE, x, y, z + 1, 0, 0, 0, 0, dust);
                player.spawnParticle(Particle.REDSTONE, x, y + 1, z, 0, 0, 0, 0, dust);
                player.spawnParticle(Particle.REDSTONE, x + 1, y + 1, z, 0, 0, 0, 0, dust);
                player.spawnParticle(Particle.REDSTONE, x + 1, y + 1, z + 1, 0, 0, 0, 0, dust);
                player.spawnParticle(Particle.REDSTONE, x, y + 1, z + 1, 0, 0, 0, 0, dust);
            } else {
                double r = (color.getRed() - 0.0001) / 255D;
                double g = (color.getGreen() - 0.0001) / 255D;
                double b = (color.getBlue() - 0.0001) / 255D;
                player.spawnParticle(Particle.REDSTONE, x, y, z, 0, r, g, b, 1);
                player.spawnParticle(Particle.REDSTONE, x + 1, y, z, 0, r, g, b, 1);
                player.spawnParticle(Particle.REDSTONE, x + 1, y, z + 1, 0, r, g, b, 1);
                player.spawnParticle(Particle.REDSTONE, x, y, z + 1, 0, r, g, b, 1);
                player.spawnParticle(Particle.REDSTONE, x, y + 1, z, 0, r, g, b, 1);
                player.spawnParticle(Particle.REDSTONE, x + 1, y + 1, z, 0, r, g, b, 1);
                player.spawnParticle(Particle.REDSTONE, x + 1, y + 1, z + 1, 0, r, g, b, 1);
                player.spawnParticle(Particle.REDSTONE, x, y + 1, z + 1, 0, r, g, b, 1);
            }
        }
    }
}