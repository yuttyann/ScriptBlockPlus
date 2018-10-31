package com.github.yuttyann.scriptblockplus.script.option.nms;

import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.enums.reflection.PackageType;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class NMSHelper {

	private static final Class<?> PACKET;

	static {
		Class<?> clazz = null;
		try {
			clazz = PackageType.NMS.getClass("Packet");
		} catch (IllegalArgumentException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		PACKET = clazz;
	}

	public static void sendPacket(Player player, Object packet) {
		try {
			Object handle = PackageType.CB_ENTITY.invokeMethod(player, "CraftPlayer", "getHandle");
			Object connection = PackageType.NMS.getField("EntityPlayer", "playerConnection").get(handle);
			PackageType.NMS.getMethod("PlayerConnection", "sendPacket", PACKET).invoke(connection, packet);
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
	}

	public static String getChatSerializerName() {
		String chatSerializer = "ChatSerializer";
		return Utils.isCBXXXorLater("1.8.3") ? "IChatBaseComponent$" + chatSerializer : chatSerializer;
	}

	public static String getEnumTitleActionName() {
		String enumTitleAction = "EnumTitleAction";
		return Utils.isCBXXXorLater("1.8.3") ? "PacketPlayOutTitle$" + enumTitleAction : enumTitleAction;
	}

	public static Object getEnumField(Class<?> clazz, String name) {
		if (clazz == null || StringUtils.isEmpty(name)) {
			return null;
		}
		for (Object field : clazz.getEnumConstants()) {
			if (String.valueOf(field).equals(name)) {
				return field;
			}
		}
		return null;
	}
}