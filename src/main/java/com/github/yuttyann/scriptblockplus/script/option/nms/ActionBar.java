package com.github.yuttyann.scriptblockplus.script.option.nms;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.yuttyann.scriptblockplus.enums.reflection.PackageType;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class ActionBar extends BaseOption {

	private static final Object C_VALUE;
	private static final Constructor<?> PACKET_CONSTRUCTOR;

	static {
		Object value = (byte) 2;
		Class<?> byteOrChatMessageType = null;
		Constructor<?> packetPlayOutChat = null;
		try {
			if (Utils.isCBXXXorLater("1.12")) {
				value = PackageType.NMS.getEnumValueOf("ChatMessageType", "GAME_INFO");
				byteOrChatMessageType = value.getClass();
			} else {
				byteOrChatMessageType = byte.class;
			}
		} catch (IllegalArgumentException | ReflectiveOperationException e) {
			e.printStackTrace();
		}
		try {
			Class<?>[] array = { PackageType.NMS.getClass("IChatBaseComponent"), byteOrChatMessageType };
			packetPlayOutChat = PackageType.NMS.getConstructor("PacketPlayOutChat", array);
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
		C_VALUE = value;
		PACKET_CONSTRUCTOR = packetPlayOutChat;
	}

	public ActionBar() {
		super("actionbar", "@actionbar:");
	}

	@Override
	public Option newInstance() {
		return new ActionBar();
	}

	@Override
	protected boolean isValid() throws Exception {
		String[] array = StringUtils.split(getOptionValue(), "/");
		String message = StringUtils.replaceColorCode(array[0], true);

		if (array.length > 1) {
			int stay = Integer.parseInt(array[1]);
			new Task(stay, message).runTaskTimer(getPlugin(), 0, 1);
		} else {
			sendActionBar(getSBPlayer(), message);
		}
		return true;
	}

	private class Task extends BukkitRunnable {

		private final int stay;
		private final String message;

		private int tick;

		private Task(int stay, String message) {
			this.tick = 0;
			this.stay = stay + 1;
			this.message = message;
		}

		@Override
		public void run() {
			try {
				if (tick++ >= stay) {
					sendActionBar(getSBPlayer(), "");
					cancel();
				} else {
					sendActionBar(getSBPlayer(), message);
				}
			} catch (ReflectiveOperationException e) {
				cancel();
			}
		}
	}

	private void sendActionBar(SBPlayer sbPlayer, String message) throws ReflectiveOperationException {
		if (sbPlayer.isOnline()) {
			Player player = sbPlayer.getPlayer();
			String chatSerializer = NMSHelper.getChatSerializerName();
			Method a = PackageType.NMS.getMethod(chatSerializer, "a", String.class);
			Object component = a.invoke(null, "{\"text\": \"" + message + "\"}");
			NMSHelper.sendPacket(player, PACKET_CONSTRUCTOR.newInstance(component, C_VALUE));
		}
	}
}