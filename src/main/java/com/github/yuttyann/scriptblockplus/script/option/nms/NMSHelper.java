package com.github.yuttyann.scriptblockplus.script.option.nms;

import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.enums.PackageType;

class NMSHelper {

	static final Class<?>[] STRING_PARAM = {String.class};

	static void sendPacket(Player player, Object packet) {
		try {
			Object handle = player.getClass().getMethod("getHandle").invoke(player);
			Object connection = handle.getClass().getField("playerConnection").get(handle);
			Class<?> nmsPacketClass = PackageType.NMS.getClass("Packet");
			connection.getClass().getMethod("sendPacket", nmsPacketClass).invoke(connection, packet);
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
	}

	static Object getEnumField(Class<?> clazz, String name) {
		for (Object field : clazz.getEnumConstants()) {
			if (name.equals(String.valueOf(field))) {
				return field;
			}
		}
		return null;
	}
}