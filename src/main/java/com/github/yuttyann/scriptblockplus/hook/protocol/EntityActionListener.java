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
package com.github.yuttyann.scriptblockplus.hook.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.hook.plugin.ProtocolLib;

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
        var player = event.getPlayer();
        var packet = event.getPacket();
        if(player != null && packet.getEntityUseActions().read(0) != null) {
            int id = packet.getIntegers().read(0);
            var glowEntities = ProtocolLib.GLOW_ENTITY.getEntities().get(player.getUniqueId()).stream();
            glowEntities.filter(g -> g.getId() == id).findFirst().ifPresent(g -> event.setCancelled(true));
        }
    }
}