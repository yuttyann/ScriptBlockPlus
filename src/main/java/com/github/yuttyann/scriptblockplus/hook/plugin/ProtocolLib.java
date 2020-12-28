package com.github.yuttyann.scriptblockplus.hook.plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Predicate;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.EnumWrappers.ChatType;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import com.github.yuttyann.scriptblockplus.hook.HookPlugin;
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

public class ProtocolLib extends HookPlugin {

    public static final ProtocolLib INSTANCE = new ProtocolLib();

    private static final Team AIR;
    private static final Team BLOCK;
    private static final Multimap<UUID, GlowEntity> GLOW_ENTITIES;

    static {
        // スコアボードを取得
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        // SBP_PLIB_BLUE の登録
        String air = "SBP_PLIB_BLUE";
        if (scoreboard.getTeam(air) != null) {
            AIR = scoreboard.getTeam(air);
        } else {
            AIR = scoreboard.registerNewTeam(air);
            AIR.setColor(ChatColor.BLUE);
            AIR.setOption(Option.COLLISION_RULE, OptionStatus.NEVER);
        }

        // SBP_PLIB_DGREEN の登録
        String block = "SBP_PLIB_DGREEN";
        if (scoreboard.getTeam(block) != null) {
            BLOCK = scoreboard.getTeam(block);
        } else {
            BLOCK = scoreboard.registerNewTeam(block);
            BLOCK.setColor(ChatColor.DARK_GREEN);
            BLOCK.setOption(Option.COLLISION_RULE, OptionStatus.NEVER);
        }

        // ここに表示中のエンティティを保存する
        GLOW_ENTITIES = HashMultimap.create();
    }

    @Override
    @NotNull
    public String getPluginName() {
        return "ProtocolLib";
    }
    
    @NotNull
    public Multimap<UUID, GlowEntity> getGlowEntities() {
        return GLOW_ENTITIES;
    }

    public void sendActionBar(@NotNull Player player, @NotNull String text) throws InvocationTargetException {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        PacketContainer chat = protocolManager.createPacket(PacketType.Play.Server.CHAT);
        if (Utils.isCBXXXorLater("1.12")) {
            chat.getChatTypes().write(0, ChatType.GAME_INFO);
        } else {
            chat.getBytes().write(0, (byte) 2);
        }
        chat.getChatComponents().write(0, WrappedChatComponent.fromJson("{\"text\":\"" + text + "\"}"));
        protocolManager.sendServerPacket(player, chat);
    }

    @NotNull
    public GlowEntity spawnGlowEntity(@NotNull SBPlayer sbPlayer, @NotNull Block block) throws InvocationTargetException {
        // チームを取得
        Team team = block.getType() == Material.AIR ? AIR : BLOCK;

        // エンティティのID
        int id = (int) (Math.random() * Integer.MAX_VALUE);
        UUID uuid = UUID.randomUUID();

        // エンティティのスポーン
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        PacketContainer spawnEntity = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
        spawnEntity.getIntegers().write(0, id);
        spawnEntity.getIntegers().write(1, 44); // MAGMA_CUBE 
        spawnEntity.getUUIDs().write(0, uuid);
        spawnEntity.getDoubles().write(0, block.getX() + 0.5D);
        spawnEntity.getDoubles().write(1, (double) block.getY());
        spawnEntity.getDoubles().write(2, block.getZ() + 0.5D);

        // エンティティのメタデータ
        PacketContainer entityMetadata = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
        entityMetadata.getIntegers().write(0, id);
        setMetadata(entityMetadata);

        // チームに入れる
        team.addEntry(uuid.toString());

        // パケットを送信
        Player player = sbPlayer.getPlayer();
        protocolManager.sendServerPacket(player, spawnEntity);
        protocolManager.sendServerPacket(player, entityMetadata);

        // エンティティのデータをまとめたクラスを返す
        GlowEntity glowEntity = new GlowEntity(id, uuid, team, new Vector(block.getX(), block.getY(), block.getZ()), sbPlayer);
        GLOW_ENTITIES.get(sbPlayer.getUniqueId()).add(glowEntity);
        return glowEntity;
    }

    public void destroyGlowEntity(@NotNull SBPlayer sbPlayer, @NotNull Block block) {
        if (!sbPlayer.isOnline() || !has(sbPlayer, block)) {
            return;
        }
        Predicate<GlowEntity> filter = g -> g.equals(block.getX(), block.getY(), block.getZ());
        GLOW_ENTITIES.get(sbPlayer.getUniqueId()).stream().filter(filter).findFirst().ifPresent(this::destroyGlowEntity);
    }

    public void destroyGlowEntity(@NotNull GlowEntity glowEntity) {
        UUID uuid = glowEntity.getSender().getUniqueId();
        if (!glowEntity.getSender().isOnline() || !GLOW_ENTITIES.get(uuid).contains(glowEntity)) {
            return;
        }
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        PacketContainer destroy = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        destroy.getIntegerArrays().write(0, new int[] { glowEntity.getId() });
        glowEntity.getTeam().removeEntry(glowEntity.getUniqueId().toString());
        try {
            protocolManager.sendServerPacket(glowEntity.getSender().getPlayer(), destroy);
            GLOW_ENTITIES.remove(uuid, glowEntity);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void broadcastDestroy(@NotNull GlowEntity glowEntity) {
        UUID uuid = glowEntity.getSender().getUniqueId();
        if (!GLOW_ENTITIES.get(uuid).contains(glowEntity)) {
            return;
        }
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        PacketContainer destroy = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        destroy.getIntegerArrays().write(0, new int[] { glowEntity.getId() });
        glowEntity.getTeam().removeEntry(glowEntity.getUniqueId().toString());
        protocolManager.broadcastServerPacket(destroy);
        GLOW_ENTITIES.remove(uuid, glowEntity);
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

    public boolean has(SBPlayer sbPlayer, @NotNull Block block) {
        if  (GLOW_ENTITIES.isEmpty()) {
            return false;
        }
        Predicate<GlowEntity> filter = g -> g.equals(block.getX(), block.getY(), block.getZ());
        return GLOW_ENTITIES.get(sbPlayer.getUniqueId()).stream().anyMatch(filter);
    }

    private void setMetadata(@NotNull PacketContainer entityMetadata) {
        WrappedDataWatcher dataWatcher = new WrappedDataWatcher(entityMetadata.getWatchableCollectionModifier().read(0));
        dataWatcher.setObject(createObject(0, Byte.class), (byte) (0x20 + 0x40), true);   // Invisible & Glowing
        dataWatcher.setObject(createObject(4, Boolean.class), true, true);                // Silent
        dataWatcher.setObject(createObject(5, Boolean.class), true, true);                // NoGravity
        dataWatcher.setObject(createObject(14, Byte.class), (byte) 0x01, true);           // NoAI
        dataWatcher.setObject(createObject(15, Integer.class), 2, true);                  // Size
        entityMetadata.getWatchableCollectionModifier().write(0, dataWatcher.getWatchableObjects());
    }

    @NotNull
    private WrappedDataWatcherObject createObject(int index, @NotNull Class<?> clazz) {
        return new WrappedDataWatcherObject(index, Registry.get(clazz));
    }
}