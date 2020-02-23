package com.github.yuttyann.scriptblockplus.script.option.nms;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.enums.reflection.PackageType;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NMSHelper {

	private static final Class<?> PACKET;
	private static final Object C_VALUE;
	private static final Constructor<?> PACKET_CONSTRUCTOR;

	static {
		Class<?> packet = null;
		try {
			packet = PackageType.NMS.getClass("Packet");
		} catch (IllegalArgumentException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		PACKET = packet;

		Object value = (byte) 2;
		Class<?> byteOrChatMessageType = null;
		Constructor<?> packetPlayOutChat = null;
		if (Utils.isCraftBukkit()) {
			try {
				if (Utils.isCBXXXorLater("1.12")) {
					value = PackageType.NMS.getEnumValueOf("ChatMessageType", "GAME_INFO");
					byteOrChatMessageType = value.getClass();
				} else {
					byteOrChatMessageType = byte.class;
				}
				Class<?>[] array = { PackageType.NMS.getClass("IChatBaseComponent"), byteOrChatMessageType };
				packetPlayOutChat = PackageType.NMS.getConstructor("PacketPlayOutChat", array);
			} catch (IllegalArgumentException | ReflectiveOperationException e) {
				e.printStackTrace();
			}
		}
		C_VALUE = value;
		PACKET_CONSTRUCTOR = packetPlayOutChat;
	}

	public static void sendPacket(@NotNull Player player, @NotNull Object packet) {
		try {
			Object handle = PackageType.CB_ENTITY.invokeMethod(player, "CraftPlayer", "getHandle");
			Object connection = PackageType.NMS.getField("EntityPlayer", "playerConnection").get(handle);
			PackageType.NMS.getMethod("PlayerConnection", "sendPacket", PACKET).invoke(connection, packet);
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
	}

	public static void sendActionBar(@NotNull SBPlayer sbPlayer, @NotNull String message) throws ReflectiveOperationException {
		if (Utils.isCraftBukkit() && sbPlayer.isOnline()) {
			String chatSerializer = NMSHelper.getChatSerializerName();
			Method a = PackageType.NMS.getMethod(chatSerializer, "a", String.class);
			Object component = a.invoke(null, "{\"text\": \"" + message + "\"}");
			sendPacket(sbPlayer.getPlayer(), PACKET_CONSTRUCTOR.newInstance(component, C_VALUE));
		}
	}

	@NotNull
	public static String getChatSerializerName() {
		String chatSerializer = "ChatSerializer";
		return Utils.isCBXXXorLater("1.8.3") ? "IChatBaseComponent$" + chatSerializer : chatSerializer;
	}

	@NotNull
	public static String getEnumTitleActionName() {
		String enumTitleAction = "EnumTitleAction";
		return Utils.isCBXXXorLater("1.8.3") ? "PacketPlayOutTitle$" + enumTitleAction : enumTitleAction;
	}

	@Nullable
	public static Object getEnumField(@NotNull Class<?> clazz, @NotNull String name) {
		if (!clazz.isEnum() || StringUtils.isEmpty(name)) {
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