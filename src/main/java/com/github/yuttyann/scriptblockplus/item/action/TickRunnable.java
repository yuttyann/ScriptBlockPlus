/**
 * ScriptBlockPlus - Allow you to add script to any blocks.
 * Copyright (C) 2021 yuttyann44581
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package com.github.yuttyann.scriptblockplus.item.action;

import java.util.HashSet;
import java.util.Set;

import com.github.yuttyann.scriptblockplus.file.json.derived.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.hook.plugin.ProtocolLib;
import com.github.yuttyann.scriptblockplus.hook.protocol.GlowEntity;
import com.github.yuttyann.scriptblockplus.hook.protocol.GlowEntityPacket;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.raytrace.RayTrace;
import com.github.yuttyann.scriptblockplus.region.CuboidRegionBlocks;
import com.github.yuttyann.scriptblockplus.region.PlayerRegion;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus TickRunnable クラス
 * @author yuttyann44581
 */
public class TickRunnable extends BukkitRunnable {

    private static final String KEY = Utils.randomUUID();
    private static final String KEY_TEMP = Utils.randomUUID();
    private static final boolean HAS_PROTOCOLLIB = ProtocolLib.INSTANCE.has();
    private static final GlowEntityPacket GLOW_ENTITY_PACKET = ProtocolLib.GLOW_ENTITY;

    private int tick = 0;

    @Override
    public final void run() {
        try {
            for (var sbPlayer : ScriptViewer.PLAYERS) {
                tick(sbPlayer, tick);
            }
        } finally {
            if (++tick > 20) {
                this.tick = 0;
            }
        }
    }

    private void tick(@NotNull SBPlayer sbPlayer, int tick) {
        if (!sbPlayer.isOnline()) {
            return;
        }
        if (HAS_PROTOCOLLIB) {
            lookBlocks(sbPlayer);
            if (tick % 5 == 0) {
                sendParticles(sbPlayer, true);
            }
            if (tick % 10 == 0) {
                spawnGlowEntity(sbPlayer);
            }
        } else {
            if (tick % 10 == 0) {
                sendParticles(sbPlayer, false);
            }
        }
    }

    private void lookBlocks(@NotNull SBPlayer sbPlayer) {
        var player = sbPlayer.getPlayer();
        var rayResult = RayTrace.rayTraceBlocks(player, getDistance(player));
        StreamUtils.filterNot(getLocations(sbPlayer, KEY), Set::isEmpty, Set::clear);
        StreamUtils.filterNot(getLocations(sbPlayer, KEY_TEMP), Set::isEmpty, Set::clear);
        if (rayResult == null) {
            return;
        }
        var locations = getLocations(sbPlayer, KEY);
        var tempLocations = getLocations(sbPlayer, KEY_TEMP);
        destroyEntity(sbPlayer, rayResult.getHitBlock().getLocation(), locations);
        destroyEntity(sbPlayer, rayResult.getRelative().getLocation(), locations);
        if (locations.size() > 0) {
            tempLocations.addAll(locations);
            for (var block : RayTrace.rayTraceBlocks(player, getDistance(player), 0.01D, true)) {
                var blockLocation = block.getLocation();
                if (tempLocations.contains(blockLocation)) {
                    break;
                }
                if (!locations.contains(blockLocation)) {
                    destroyEntity(sbPlayer, blockLocation, locations);
                }
            }
        }
    }

    private void spawnGlowEntity(@NotNull SBPlayer sbPlayer) {
        var region = new PlayerRegion(sbPlayer.getPlayer(), 20);
        var blocks = getBlocks(new CuboidRegionBlocks(region));
        var locations = getLocations(sbPlayer, KEY);
        for (var block : blocks) {
            if (locations.size() > 0 && StreamUtils.anyMatch(locations, l -> block.getLocation().equals(l))) {
                continue;
            }
            var location = block.getLocation();
            if (!GLOW_ENTITY_PACKET.has(sbPlayer, location)) {
                var color = GLOW_ENTITY_PACKET.getTeamColor(block);
                GLOW_ENTITY_PACKET.spawnGlowEntity(sbPlayer, location, color);
            }
        }
        var glowEntities = GLOW_ENTITY_PACKET.getEntities();
        for(var glowEntity : glowEntities.get(sbPlayer.getUniqueId()).toArray(GlowEntity[]::new)) {
            if(!StreamUtils.anyMatch(blocks, b -> glowEntity.equals(b.getX(), b.getY(), b.getZ()))) {
                GLOW_ENTITY_PACKET.destroyGlowEntity(glowEntity);
            }
        }
    }

    private void sendParticles(@NotNull SBPlayer sbPlayer, final boolean hasProtocolLib) {
        if (hasProtocolLib) {
            for (var location : getLocations(sbPlayer, KEY_TEMP)) {
                var block = location.getBlock();
                boolean isAIR = block.getType() == Material.AIR;
                spawnParticlesOnBlock(sbPlayer.getPlayer(), block, isAIR ? Color.BLUE : Color.GREEN);
            }
        } else {
            int count = 0;
            var region = new PlayerRegion(sbPlayer.getPlayer(), 10);
            for (var block : getBlocks(new CuboidRegionBlocks(region))) {
                if (count++ < 800) {
                    spawnParticlesOnBlock(sbPlayer.getPlayer(), block, null);
                }
            }
        }
    }

    @NotNull
    private Set<Block> getBlocks(@NotNull CuboidRegionBlocks regionBlocks) {
        var result = new HashSet<Block>();
        var blocks = regionBlocks.getBlocks();
        for (var scriptKey : ScriptKey.values()) {
            var blockScript = new BlockScriptJson(scriptKey).load();
            for (var block : blocks) {
                if (blockScript.has(block.getLocation())) {
                    result.add(block);
                }
            }
        }
        return result;
    }

    @NotNull
    private Set<Location> getLocations(@NotNull SBPlayer sbPlayer, @NotNull String key) {
        Set<Location> locations = sbPlayer.getObjectMap().get(key);
        if (locations == null) {
            sbPlayer.getObjectMap().put(key, locations = new HashSet<Location>());
        }
        return locations;
    }

    private double getDistance(@NotNull Player player) {
        return player.getGameMode() == GameMode.CREATIVE ? 5.0D : 4.5D;
    }

    private boolean destroyEntity(@NotNull SBPlayer sbPlayer, @NotNull Location location, @NotNull Set<Location> locations) {
        if (!BlockScriptJson.has(location)) {
            return false;
        }
        GLOW_ENTITY_PACKET.destroyGlowEntity(sbPlayer, location);
        locations.add(location);
        return true;
    }

    private void spawnParticlesOnBlock(@NotNull Player player, @NotNull Block block, @Nullable Color color) {
        if (color == null) {
            color = block.getType() == Material.AIR ? Color.AQUA : Color.LIME;
        }
        double x = block.getX(), y = block.getY(), z = block.getZ(), a = 1D;
        if (Utils.isCBXXXorLater("1.13")) {
            var dust = new DustOptions(color, 1);
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