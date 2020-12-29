package com.github.yuttyann.scriptblockplus.hook.protocol;

import java.util.stream.Stream;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.hook.plugin.ProtocolLib;

import org.bukkit.entity.Player;

/**
 * ScriptBlockPlus EntityActionListener クラス
 * @author yuttyann44581
 */
public class EntityActionListener extends PacketAdapter {

    public EntityActionListener() {
        super(ScriptBlock.getInstance(), PacketType.Play.Client.USE_ENTITY);
    }

    public void register() {
        ProtocolLibrary.getProtocolManager().addPacketListener(this);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        Player player = event.getPlayer();
        PacketContainer packet = event.getPacket();
        if(player != null && packet.getEntityUseActions().read(0) != null) {
            int id = packet.getIntegers().read(0);
            Stream<GlowEntity> glowEntities = ProtocolLib.GLOW_ENTITY.getEntities().get(player.getUniqueId()).stream();
            glowEntities.filter(g -> g.getId() == id).findFirst().ifPresent(g -> event.setCancelled(true));
        }
    }
}