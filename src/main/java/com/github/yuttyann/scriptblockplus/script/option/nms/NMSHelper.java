package com.github.yuttyann.scriptblockplus.script.option.nms;

import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.enums.PackageType;

class NMSHelper {

	static final Class<?>[] BYTE_PARAM = {Byte.class};
	static final Class<?>[] SHORT_PARAM = {Short.class};
	static final Class<?>[] INTEGER_PARAM = {Integer.class};
	static final Class<?>[] LONG_PARAM = {Long.class};
	static final Class<?>[] FLOAT_PARAM = {Float.class};
	static final Class<?>[] DOUBLE_PARAM = {Double.class};
	static final Class<?>[] BOOLEAN_PARAM = {Boolean.class};
	static final Class<?>[] STRING_PARAM = {String.class};
	static final Class<?>[] CHARACTER_PARAM = {Character.class};

	public static void sendPacket(Player player, Object packet) {
		try {
			Object handle = player.getClass().getMethod("getHandle").invoke(player);
			Object connection = handle.getClass().getField("playerConnection").get(handle);
			Class<?> nmsPacketClass = PackageType.NMS.getClass("Packet");
			connection.getClass().getMethod("sendPacket", nmsPacketClass).invoke(connection, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Object getEnumField(Class<?> clazz, String name) {
		for (Object field : clazz.getEnumConstants()) {
			if (name.equals(String.valueOf(field))) {
				return field;
			}
		}
		return null;
	}
}