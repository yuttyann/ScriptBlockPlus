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
import java.util.function.Predicate;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.enums.TeamColor;
import com.github.yuttyann.scriptblockplus.enums.server.NetMinecraft;
import com.github.yuttyann.scriptblockplus.file.json.derived.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.hook.nms.GlowEntity;
import com.github.yuttyann.scriptblockplus.hook.nms.GlowEntityPacket;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.raytrace.RayTrace;
import com.github.yuttyann.scriptblockplus.region.CuboidRegionIterator;
import com.github.yuttyann.scriptblockplus.region.PlayerRegion;
import com.github.yuttyann.scriptblockplus.region.Region;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.utils.ItemUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils.ThrowableConsumer;
import com.google.common.base.Predicates;

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

    private static final int PLAYER_RANGE = 15;
    private static final int PARTICLE_RANGE = 10;
    private static final int PARTICLE_LIMIT = 800;

    private static final String KEY = Utils.randomUUID();
    private static final String KEY_TEMP = Utils.randomUUID();

    public static final GlowEntityPacket GLOW_ENTITY = new GlowEntityPacket();

    private int tick = 0;

    @Override
    public final void run() {
        try {
            for (var sbPlayer : ScriptViewer.PLAYERS) {
                if (sbPlayer.isOnline()) {
                    tick(sbPlayer);
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

    private void tick(@NotNull SBPlayer sbPlayer) throws Exception {
        if (NetMinecraft.hasNMS()) {
            lookBlocks(sbPlayer);
            if (tick % 5 == 0) {
                for (var blockCoords : getBlockCoords(sbPlayer, KEY_TEMP)) {
                    var block = blockCoords.getBlock();
                    spawnParticlesOnBlock(sbPlayer.getPlayer(), block, ItemUtils.isAIR(block.getType()) ? Color.BLUE : Color.GREEN);
                }
            }
            if (tick % 10 == 0) {
                spawnGlowEntity(sbPlayer);
            }
        } else {
            if (tick % 10 == 0) {
                var count = new int[] { 0 };
                var player = sbPlayer.getPlayer();
                forEach(new PlayerRegion(player, PARTICLE_RANGE), Predicates.alwaysTrue(), b -> {
                    if (count[0]++ < PARTICLE_LIMIT) {
                        spawnParticlesOnBlock(player, b.getBlock(), null);
                    }
                });
            }
        }
    }

    private void lookBlocks(@NotNull SBPlayer sbPlayer) throws Exception {
        var player = sbPlayer.getPlayer();
        var rayResult = RayTrace.rayTraceBlocks(player, getDistance(player));
        clearSet(getBlockCoords(sbPlayer, KEY));
        clearSet(getBlockCoords(sbPlayer, KEY_TEMP));
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
        var region = new PlayerRegion(sbPlayer.getPlayer(), PLAYER_RANGE);
        var lookBlocks = getBlockCoords(sbPlayer, KEY);
        forEach(region, b -> !lookBlocks.contains(b), b -> {
            GLOW_ENTITY.spawn(sbPlayer, b, getTeamColor(b.getBlock()));
        });
        var entities = GLOW_ENTITY.getEntities();
        if (entities.isEmpty()) {
            return;
        }
        var glowEntities = entities.get(sbPlayer.getUniqueId());
        if (glowEntities.isEmpty()) {
            return;
        }
        var min = region.getMinimumPoint();
        var max = region.getMaximumPoint();
        var iterator = glowEntities.iterable().iterator();
        while (iterator.hasNext()) {
            var glowEntity = iterator.next().value();
            if (!inRange(glowEntity, min, max)) {
                iterator.remove();
                GLOW_ENTITY.destroy(glowEntity);
            }
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
        return ItemUtils.isAIR(block.getType()) ? TeamColor.BLUE : TeamColor.GREEN;
    }

    private double getDistance(@NotNull Player player) {
        return player.getGameMode() == GameMode.CREATIVE ? 5.0D : 4.5D;
    }

    private boolean inRange(@NotNull GlowEntity glowEntity, @NotNull BlockCoords min, @NotNull BlockCoords max) {
        var blockCoords = glowEntity.getBlockCoords();
        if (blockCoords.getX() < min.getX() || blockCoords.getX() > max.getX()) {
            return false;
        }
        if (blockCoords.getY() < min.getY() || blockCoords.getY() > max.getY()) {
            return false;
        }
        if (blockCoords.getZ() < min.getZ() || blockCoords.getZ() > max.getZ()) {
            return false;
        }
        return true;
    }

    private void clearSet(@NotNull Set<BlockCoords> set) {
        if (set.size() > 0) {
            set.clear();
        }
    }

    @NotNull
    private void forEach(@NotNull Region region, @NotNull Predicate<BlockCoords> filter, @NotNull ThrowableConsumer<BlockCoords> action) throws Exception {
        var iterator = new CuboidRegionIterator(region);
        for (var scriptKey : ScriptKey.iterable()) {
            var scriptJson = BlockScriptJson.get(scriptKey);
            if (scriptJson.isEmpty()) {
                continue;
            }
            while (iterator.hasNext()) {
                var blockCoords = iterator.next();
                if (filter.test(blockCoords) && scriptJson.has(blockCoords)) {
                    action.accept(blockCoords);
                }
            }
            iterator.reset();
        }
    }

    private void destroyEntity(@NotNull SBPlayer sbPlayer, @NotNull BlockCoords blockCoords, @NotNull Set<BlockCoords> blocks) throws Exception {
        if (BlockScriptJson.contains(blockCoords)) {
            GLOW_ENTITY.destroy(sbPlayer, blockCoords);
            blocks.add(blockCoords);
        }
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