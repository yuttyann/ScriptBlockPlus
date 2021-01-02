package com.github.yuttyann.scriptblockplus.hook.protocol;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;
import java.util.function.Predicate;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import com.github.yuttyann.scriptblockplus.enums.reflection.PackageType;
import com.github.yuttyann.scriptblockplus.hook.plugin.ProtocolLib;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus GlowEntityPacket クラス
 * @author yuttyann44581
 */
public class GlowEntityPacket {

    private static final Team AIR;
    private static final Team BLOCK;

    static {
        // スコアボードを取得
        var scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        // SBP_PLIB_BLUE の登録
        var blue = "SBP_PLIB_BLUE";
        if (scoreboard.getTeam(blue) != null) {
            AIR = scoreboard.getTeam(blue);
        } else {
            AIR = scoreboard.registerNewTeam(blue);
            if (Utils.isCBXXXorLater("1.12")) {
                AIR.setColor(ChatColor.BLUE);
            }
            AIR.setPrefix(ChatColor.BLUE.toString());
            AIR.setSuffix(ChatColor.BLUE.toString());
            AIR.setOption(Option.COLLISION_RULE, OptionStatus.NEVER);
        }

        // SBP_PLIB_DGREEN の登録
        var darkGreen = "SBP_PLIB_DGREEN";
        if (scoreboard.getTeam(darkGreen) != null) {
            BLOCK = scoreboard.getTeam(darkGreen);
        } else {
            BLOCK = scoreboard.registerNewTeam(darkGreen);
            if (Utils.isCBXXXorLater("1.12")) {
                BLOCK.setColor(ChatColor.DARK_GREEN);
            }
            BLOCK.setPrefix(ChatColor.DARK_GREEN.toString());
            BLOCK.setSuffix(ChatColor.DARK_GREEN.toString());
            BLOCK.setOption(Option.COLLISION_RULE, OptionStatus.NEVER);
        }

        // エンティティのクリックを検知
        StreamUtils.ifAction(ProtocolLib.INSTANCE.has(), () -> new EntityActionListener().register());
    }

    private final int TYPE_ID = PackageType.getMagmaCubeId();
    private final int SIZE_ID = PackageType.getSlimeSizeId();
    private final Multimap<UUID, GlowEntity> GLOW_ENTITIES = HashMultimap.create();

    @NotNull
    public Multimap<UUID, GlowEntity> getEntities() {
        return GLOW_ENTITIES;
    }

    @NotNull
    public GlowEntity spawnGlowEntity(@NotNull SBPlayer sbPlayer, @NotNull Block block) {
        // エンティティのID
        int id = EntityCount.next();
        var uuid = UUID.randomUUID();

        // チームを取得、エンティティの登録
        var team = block.getType() == Material.AIR ? AIR : BLOCK;
        team.addEntry(uuid.toString());

        // パケットを送信
        try {
            var protocolManager = ProtocolLibrary.getProtocolManager();
            protocolManager.sendServerPacket(sbPlayer.getPlayer(), createEntity(id, uuid, block));
            protocolManager.sendServerPacket(sbPlayer.getPlayer(), createMetadata(id));
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        // エンティティのデータをまとめたクラスを返す
        var vector = new Vector(block.getX(), block.getY(), block.getZ());
        var glowEntity = new GlowEntity(id, uuid, team, vector, sbPlayer);
        GLOW_ENTITIES.get(sbPlayer.getUniqueId()).add(glowEntity);
        return glowEntity;
    }

    public void destroyGlowEntity(@NotNull SBPlayer sbPlayer, @NotNull Block block) {
        if (!has(sbPlayer, block)) {
            return;
        }
        Predicate<GlowEntity> filter = g -> g.equals(block.getX(), block.getY(), block.getZ());
        GLOW_ENTITIES.get(sbPlayer.getUniqueId()).stream().filter(filter).findFirst().ifPresent(this::destroyGlowEntity);
    }

    public void destroyGlowEntity(@NotNull GlowEntity glowEntity) {
        var uuid = glowEntity.getSender().getUniqueId();
        if (!GLOW_ENTITIES.get(uuid).contains(glowEntity)) {
            return;
        }
        try {
            var player = glowEntity.getSender().getPlayer();
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, createDestroy(glowEntity.getId()));
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } finally {
            GLOW_ENTITIES.remove(uuid, glowEntity);
            glowEntity.getTeam().removeEntry(glowEntity.getUniqueId().toString());
        }
    }

    public void broadcastDestroy(@NotNull GlowEntity glowEntity) {
        var uuid = glowEntity.getSender().getUniqueId();
        if (!GLOW_ENTITIES.get(uuid).contains(glowEntity)) {
            return;
        }
        try {
            ProtocolLibrary.getProtocolManager().broadcastServerPacket(createDestroy(glowEntity.getId()));
        } finally {
            GLOW_ENTITIES.remove(uuid, glowEntity);
            glowEntity.getTeam().removeEntry(glowEntity.getUniqueId().toString());
        }
    }

    public void destroyAll(@NotNull SBPlayer sbPlayer) {
        var glowEntities = GLOW_ENTITIES.get(sbPlayer.getUniqueId());
        if (!glowEntities.isEmpty()) {
            StreamUtils.forEach(glowEntities.toArray(GlowEntity[]::new), this::destroyGlowEntity);
            GLOW_ENTITIES.removeAll(sbPlayer.getUniqueId());
        }
    }

    public void removeAll() {
        var glowEntities = GLOW_ENTITIES.values();
        if (!glowEntities.isEmpty()) {
            StreamUtils.forEach(glowEntities.toArray(GlowEntity[]::new), this::broadcastDestroy);
            GLOW_ENTITIES.clear();
        }
    }

    public boolean has(@NotNull SBPlayer sbPlayer, @NotNull Block block) {
        if (GLOW_ENTITIES.isEmpty()) {
            return false;
        }
        Predicate<GlowEntity> filter = g -> g.equals(block.getX(), block.getY(), block.getZ());
        return GLOW_ENTITIES.get(sbPlayer.getUniqueId()).stream().anyMatch(filter);
    }

    private PacketContainer createEntity(final int id, @NotNull UUID uuid, @NotNull Block block) {
        var packetType = PacketType.Play.Server.SPAWN_ENTITY_LIVING;
        var spawnEntity = ProtocolLibrary.getProtocolManager().createPacket(packetType);
        double x = block.getX() + 0.5D, y = block.getY(), z = block.getZ() + 0.5D;
        spawnEntity.getUUIDs().write(0, uuid);
        spawnEntity.getIntegers().write(0, id).write(1, TYPE_ID);
        spawnEntity.getDoubles().write(0, x).write(1, y).write(2, z);
        return spawnEntity;
    }

    private PacketContainer createMetadata(final int id) {
        var packetType = PacketType.Play.Server.ENTITY_METADATA;
        var entityMetadata = ProtocolLibrary.getProtocolManager().createPacket(packetType);
        entityMetadata.getIntegers().write(0, id);
        var dataWatcher = new WrappedDataWatcher(entityMetadata.getWatchableCollectionModifier().read(0));
        dataWatcher.setObject(createObject(0, Byte.class), (byte) (0x20 + 0x40)); // Invisible & Glowing
        dataWatcher.setObject(createObject(SIZE_ID, Integer.class), 2);           // Size
        entityMetadata.getWatchableCollectionModifier().write(0, dataWatcher.getWatchableObjects());
        return entityMetadata;
    }

    @NotNull
    private PacketContainer createDestroy(final int id) {
        var destroy = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        destroy.getIntegerArrays().write(0, new int[] { id });
        return destroy;
    }

    @NotNull
    private WrappedDataWatcherObject createObject(final int index, @NotNull Class<?> clazz) {
        return new WrappedDataWatcherObject(index, Registry.get(clazz));
    }
}