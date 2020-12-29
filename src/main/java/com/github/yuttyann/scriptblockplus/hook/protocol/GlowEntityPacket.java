package com.github.yuttyann.scriptblockplus.hook.protocol;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Predicate;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
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
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
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
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        // SBP_PLIB_BLUE の登録
        String air = "SBP_PLIB_BLUE";
        if (scoreboard.getTeam(air) != null) {
            AIR = scoreboard.getTeam(air);
        } else {
            AIR = scoreboard.registerNewTeam(air);
            if (Utils.isCBXXXorLater("1.12")) {
                AIR.setColor(ChatColor.BLUE);
            }
            AIR.setPrefix(ChatColor.BLUE.toString());
            AIR.setSuffix(ChatColor.BLUE.toString());
            AIR.setOption(Option.COLLISION_RULE, OptionStatus.NEVER);
        }

        // SBP_PLIB_DGREEN の登録
        String block = "SBP_PLIB_DGREEN";
        if (scoreboard.getTeam(block) != null) {
            BLOCK = scoreboard.getTeam(block);
        } else {
            BLOCK = scoreboard.registerNewTeam(block);
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

    private final int TYPE_ID = getTypeId();
    private final int SIZE_ID = getSizeId();
    private final Multimap<UUID, GlowEntity> GLOW_ENTITIES = HashMultimap.create();

    @NotNull
    public Multimap<UUID, GlowEntity> getEntities() {
        return GLOW_ENTITIES;
    }

    @NotNull
    public GlowEntity spawnGlowEntity(@NotNull SBPlayer sbPlayer, @NotNull Block block) {
        // エンティティのID
        int id = EntityCount.next();
        UUID uuid = UUID.randomUUID();

        // チームを取得、エンティティの登録
        Team team = block.getType() == Material.AIR ? AIR : BLOCK;
        team.addEntry(uuid.toString());

        // パケットを送信
        try {
            ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
            protocolManager.sendServerPacket(sbPlayer.getPlayer(), createEntity(id, uuid, block));
            protocolManager.sendServerPacket(sbPlayer.getPlayer(), createMetadata(id));
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        // エンティティのデータをまとめたクラスを返す
        Vector vector = new Vector(block.getX(), block.getY(), block.getZ());
        GlowEntity glowEntity = new GlowEntity(id, uuid, team, vector, sbPlayer);
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
        UUID uuid = glowEntity.getSender().getUniqueId();
        if (!GLOW_ENTITIES.get(uuid).contains(glowEntity)) {
            return;
        }
        try {
            Player player = glowEntity.getSender().getPlayer();
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, createDestroy(glowEntity.getId()));
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } finally {
            GLOW_ENTITIES.remove(uuid, glowEntity);
            glowEntity.getTeam().removeEntry(glowEntity.getUniqueId().toString());
        }
    }

    public void broadcastDestroy(@NotNull GlowEntity glowEntity) {
        UUID uuid = glowEntity.getSender().getUniqueId();
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
        UUID uuid = sbPlayer.getUniqueId();
        Collection<GlowEntity> glowEntities = GLOW_ENTITIES.get(uuid);
        if (!glowEntities.isEmpty()) {
            StreamUtils.forEach(glowEntities.toArray(new GlowEntity[0]), this::destroyGlowEntity);
            GLOW_ENTITIES.removeAll(uuid);
        }
    }

    public void removeAll() {
        Collection<GlowEntity> glowEntities = GLOW_ENTITIES.values();
        if (!glowEntities.isEmpty()) {
            StreamUtils.forEach(glowEntities.toArray(new GlowEntity[0]), this::broadcastDestroy);
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
        double x = block.getX() + 0.5D, y = block.getY(), z = block.getZ() + 0.5D;
        PacketType packetType = PacketType.Play.Server.SPAWN_ENTITY_LIVING;
        PacketContainer spawnEntity = ProtocolLibrary.getProtocolManager().createPacket(packetType);
        spawnEntity.getUUIDs().write(0, uuid);
        spawnEntity.getIntegers().write(0, id).write(1, TYPE_ID);
        spawnEntity.getDoubles().write(0, x).write(1, y).write(2, z);
        return spawnEntity;
    }

    private PacketContainer createMetadata(final int id) {
        PacketType packetType = PacketType.Play.Server.ENTITY_METADATA;
        PacketContainer entityMetadata = ProtocolLibrary.getProtocolManager().createPacket(packetType);
        entityMetadata.getIntegers().write(0, id);
        WrappedDataWatcher dataWatcher = new WrappedDataWatcher(entityMetadata.getWatchableCollectionModifier().read(0));
        dataWatcher.setObject(createObject(0, Byte.class), (byte) (0x20 + 0x40)); // Invisible & Glowing
        dataWatcher.setObject(createObject(SIZE_ID, Integer.class), 2);           // Size
        entityMetadata.getWatchableCollectionModifier().write(0, dataWatcher.getWatchableObjects());
        return entityMetadata;
    }

    @NotNull
    private PacketContainer createDestroy(final int id) {
        PacketContainer destroy = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        destroy.getIntegerArrays().write(0, new int[] { id });
        return destroy;
    }

    @NotNull
    private WrappedDataWatcherObject createObject(final int index, @NotNull Class<?> clazz) {
        return new WrappedDataWatcherObject(index, Registry.get(clazz));
    }

    private int getTypeId() {
        if (Utils.isCBXXXorLater("1.16")) {
            return 44;
        } else if (Utils.isCBXXXorLater("1.15")) {
            return 41;
        } else if (Utils.isCBXXXorLater("1.14")) {
            return 40;
        } else if (Utils.isCBXXXorLater("1.13")) {
            return 38;
        } else {
            return 62;
        }
    }

    private int getSizeId() {
        if (Utils.isCBXXXorLater("1.15")) {
            return 15;
        } else if (Utils.isCBXXXorLater("1.14")) {
            return 14;
        } else if (Utils.isCBXXXorLater("1.10")) {
            return 12;
        } else {
            return 11;
        }
    }
}