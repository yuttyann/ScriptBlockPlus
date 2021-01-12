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
package com.github.yuttyann.scriptblockplus.hook.plugin;

import java.lang.reflect.InvocationTargetException;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
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

    private ProtocolLib() { }

    @Override
    @NotNull
    public String getPluginName() {
        return "ProtocolLib";
    }

    public void sendActionBar(@NotNull Player player, @NotNull String text) throws InvocationTargetException {
        var protocolManager = ProtocolLibrary.getProtocolManager();
        var chat = protocolManager.createPacket(PacketType.Play.Server.CHAT);
        if (Utils.isCBXXXorLater("1.12")) {
            chat.getChatTypes().write(0, ChatType.GAME_INFO);
        } else {
            chat.getBytes().write(0, (byte) 2);
        }
        chat.getChatComponents().write(0, WrappedChatComponent.fromJson("{\"text\":\"" + text + "\"}"));
        protocolManager.sendServerPacket(player, chat);
    }
}