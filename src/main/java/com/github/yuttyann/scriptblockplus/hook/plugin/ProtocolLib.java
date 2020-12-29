package com.github.yuttyann.scriptblockplus.hook.plugin;

import java.lang.reflect.InvocationTargetException;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.EnumWrappers.ChatType;
import com.github.yuttyann.scriptblockplus.hook.HookPlugin;
import com.github.yuttyann.scriptblockplus.hook.protocol.GlowEntityPacket;
import com.github.yuttyann.scriptblockplus.utils.Utils;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus ProtocolLib クラス
 * @author yuttyann44581
 */
public class ProtocolLib extends HookPlugin {

    public static final ProtocolLib INSTANCE = new ProtocolLib();
    public static final GlowEntityPacket GLOW_ENTITY = new GlowEntityPacket();

    @Override
    @NotNull
    public String getPluginName() {
        return "ProtocolLib";
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
}