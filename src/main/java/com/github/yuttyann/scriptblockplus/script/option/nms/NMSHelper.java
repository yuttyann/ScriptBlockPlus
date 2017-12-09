package com.github.yuttyann.scriptblockplus.script.option.nms;

import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.enums.PackageType;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class NMSHelper {

	public static void sendPacket(Player player, Object packet) {
		try {
			Object handle = player.getClass().getMethod("getHandle").invoke(player);
			Object connection = handle.getClass().getField("playerConnection").get(handle);
			Class<?> nmsPacketClass = PackageType.NMS.getClass("Packet");
			connection.getClass().getMethod("sendPacket", nmsPacketClass).invoke(connection, packet);
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
	}

	public static String getChatSerializerName() {
		String chatSerializer;
		if (Utils.isCB183orLater()) {
			chatSerializer = "IChatBaseComponent$ChatSerializer";
		} else {
			chatSerializer = "ChatSerializer";
		}
		return chatSerializer;
	}

	public static String getEnumTitleActionName() {
		String enumTitleAction;
		if (Utils.isCB183orLater()) {
			enumTitleAction = "PacketPlayOutTitle$EnumTitleAction";
		} else {
			enumTitleAction = "EnumTitleAction";
		}
		return enumTitleAction;
	}

	public static Object getEnumField(Class<?> clazz, String name) {
		for (Object field : clazz.getEnumConstants()) {
			if (String.valueOf(field).equals(name)) {
				return field;
			}
		}
		return null;
	}
}