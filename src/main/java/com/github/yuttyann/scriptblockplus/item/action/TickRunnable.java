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

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.enums.TeamColor;
import com.github.yuttyann.scriptblockplus.file.json.derived.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.hook.plugin.ProtocolLib;
import com.github.yuttyann.scriptblockplus.hook.protocol.GlowEntity;
import com.github.yuttyann.scriptblockplus.hook.protocol.GlowEntityPacket;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.raytrace.RayTrace;
import com.github.yuttyann.scriptblockplus.region.CuboidRegionIterator;
import com.github.yuttyann.scriptblockplus.region.PlayerRegion;
import com.github.yuttyann.scriptblockplus.region.Region;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils.ThrowableConsumer;

import org.bukkit.Color;
import org.bukkit.GameMode;
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
                if (sbPlayer.isOnline()) {
                    tick(sbPlayer, tick);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (++tick > 20) {
                this.tick = 0;
            }
        }
    }

    private void tick(@NotNull SBPlayer sbPlayer, int tick) throws Exception {
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

    private void lookBlocks(@NotNull SBPlayer sbPlayer) throws Exception {
        var player = sbPlayer.getPlayer();
        var rayResult = RayTrace.rayTraceBlocks(player, getDistance(player));
        StreamUtils.filterNot(getBlockCoords(sbPlayer, KEY), Set::isEmpty, Set::clear);
        StreamUtils.filterNot(getBlockCoords(sbPlayer, KEY_TEMP), Set::isEmpty, Set::clear);
        if (rayResult == null) {
            return;
        }
        var blocks = getBlockCoords(sbPlayer, KEY);
        var tempBlocks = getBlockCoords(sbPlayer, KEY_TEMP);
        destroyEntity(sbPlayer, BlockCoords.of(rayResult.getHitBlock()), blocks);
        destroyEntity(sbPlayer, BlockCoords.of(rayResult.getRelative()), blocks);
        if (blocks.size() > 0) {
            tempBlocks.addAll(blocks);
            for (var block : RayTrace.rayTraceBlocks(player, getDistance(player), 0.01D, true)) {
                var blockCoords = BlockCoords.of(block);
                if (tempBlocks.contains(blockCoords)) {
                    break;
                }
                if (!blocks.contains(blockCoords)) {
                    destroyEntity(sbPlayer, blockCoords, blocks);
                }
            }
        }
    }

    private void spawnGlowEntity(@NotNull SBPlayer sbPlayer) throws Exception {
        var region = new PlayerRegion(sbPlayer.getPlayer(), 15);
        var lookBlocks = getBlockCoords(sbPlayer, KEY);
        forEach(region, b -> {
            if (lookBlocks.size() > 0 && StreamUtils.anyMatch(lookBlocks, l -> l.equals(b))) {
                return;
            }
            GLOW_ENTITY_PACKET.spawnGlowEntity(sbPlayer, b, getTeamColor(b.getBlock()));
        });
        var entities = GLOW_ENTITY_PACKET.getEntities();
        if (entities.isEmpty()) {
            return;
        }
        var glowEntities = entities.get(sbPlayer.getUniqueId());
        if (glowEntities.isEmpty()) {
            return;
        }
        var min = region.getMinimumPoint();
        var max = region.getMaximumPoint();
        var iterator = glowEntities.iterator();
        while (iterator.hasNext()) {
            var glowEntity = iterator.next();
            if (!inRange(glowEntity, min, max)) {
                iterator.remove();
                GLOW_ENTITY_PACKET.destroyGlowEntity(glowEntity);
            }
        }
    }

    private void sendParticles(@NotNull SBPlayer sbPlayer, final boolean hasProtocolLib) throws Exception {
        if (hasProtocolLib) {
            for (var blockCoords : getBlockCoords(sbPlayer, KEY_TEMP)) {
                var block = blockCoords.getBlock();
                spawnParticlesOnBlock(sbPlayer.getPlayer(), block, Utils.isAIR(block.getType()) ? Color.BLUE : Color.GREEN);
            }
        } else {
            var count = new int[] { 0 };
            forEach(new PlayerRegion(sbPlayer.getPlayer(), 10), b -> {
                if (count[0]++ < 800) {
                    spawnParticlesOnBlock(sbPlayer.getPlayer(), b.getBlock(), null);
                }
            });
        }
    }

    @NotNull
    private Set<BlockCoords> getBlockCoords(@NotNull SBPlayer sbPlayer, @NotNull String key) {
        Set<BlockCoords> blockCoords = sbPlayer.getObjectMap().get(key);
        if (blockCoords == null) {
            sbPlayer.getObjectMap().put(key, blockCoords = new HashSet<BlockCoords>());
        }
        return blockCoords;
    }

    @NotNull
    private TeamColor getTeamColor(@NotNull Block block) {
        return Utils.isAIR(block.getType()) ? TeamColor.BLUE : TeamColor.GREEN;
    }

    private boolean inRange(@NotNull GlowEntity glowEntity, @NotNull BlockCoords min, @NotNull BlockCoords max) {
        if (glowEntity.getX() < min.getX() || glowEntity.getX() > max.getX()) {
            return false;
        }
        if (glowEntity.getY() < min.getY() || glowEntity.getY() > max.getY()) {
            return false;
        }
        if (glowEntity.getZ() < min.getZ() || glowEntity.getZ() > max.getZ()) {
            return false;
        }
        return true;
    }

    @NotNull
    private void forEach(@NotNull Region region, @NotNull ThrowableConsumer<BlockCoords> action) throws Exception {
        try {
            var iterator = new CuboidRegionIterator(region);
            for (var scriptKey : ScriptKey.iterable()) {
                var scriptJson = BlockScriptJson.get(scriptKey);
                if (!scriptJson.has()) {
                    continue;
                }
                var blockScript = scriptJson.load();
                while (iterator.hasNext()) {
                    var blockCoords = iterator.next();
                    if (blockScript.has(blockCoords)) {
                        action.accept(blockCoords);
                    }
                }
                iterator.reset();
            }
        } catch (Exception e) {
            throw e;
        }
    }

    private boolean destroyEntity(@NotNull SBPlayer sbPlayer, @NotNull BlockCoords blockCoords, @NotNull Set<BlockCoords> blocks) throws Exception {
        if (!BlockScriptJson.has(blockCoords)) {
            return false;
        }
        GLOW_ENTITY_PACKET.destroyGlowEntity(sbPlayer, blockCoords);
        blocks.add(blockCoords);
        return true;
    }

    private double getDistance(@NotNull Player player) {
        return player.getGameMode() == GameMode.CREATIVE ? 5.0D : 4.5D;
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