package com.github.yuttyann.scriptblockplus.script.option.nms;

import java.lang.reflect.Method;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.yuttyann.scriptblockplus.enums.PackageType;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class ActionBar extends BaseOption {

	private static final Class<?>[] PACKET_PARAMS;

	static {
		Class<?> iChatBaseComponentClass = null;
		try {
			iChatBaseComponentClass = PackageType.NMS.getClass("IChatBaseComponent");
		} catch (IllegalArgumentException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		PACKET_PARAMS = new Class<?>[]{iChatBaseComponentClass, byte.class};
	}

	public ActionBar() {
		super("actionbar", "@actionbar:");
	}

	private class Task extends BukkitRunnable {

		private int tick;
		private final int stay;
		private final String message;

		public Task(int stay, String message) {
			this.tick = 0;
			this.stay = stay;
			this.message = message;
		}

		@Override
		public void run() {
			try {
				if (getSBPlayer().isOnline()) {
					if (tick >= stay) {
						sendActionBar(getPlayer(), "");
						cancel();
					} else {
						sendActionBar(getPlayer(), message);
					}
					tick++;
				} else {
					cancel();
				}
			} catch (ReflectiveOperationException e) {
				cancel();
			}
		}
	}

	@Override
	public boolean isValid() throws Exception {
		if (!Utils.isCB18orLater()) {
			throw new UnsupportedOperationException();
		}
		String[] array = StringUtils.split(getOptionValue(), "/");
		String message = StringUtils.replaceColorCode(array[0], true);

		if (array.length > 1) {
			int stay = Integer.parseInt(array[1]);
			new Task(stay, message).runTaskTimer(getPlugin(), 0, 1);
		} else {
			sendActionBar(getPlayer(), message);
		}
		return true;
	}

	private void sendActionBar(Player player, String message) throws ReflectiveOperationException {
		Method a = PackageType.NMS.getMethod(false, "IChatBaseComponent$ChatSerializer", "a", NMSHelper.STRING_PARAM);
		Object component = a.invoke(null, "{\"text\": \"" + message + "\"}");
		NMSHelper.sendPacket(player, newPacketPlayOutChat(component));
	}

	private Object newPacketPlayOutChat(Object component) throws ReflectiveOperationException {
		Object type = (byte) 2;
		if (Utils.isCB112orLater()) {
			Class<?> chatMessageTypeClass = PackageType.NMS.getClass("ChatMessageType");
			type = NMSHelper.getEnumField(chatMessageTypeClass, "GAME_INFO");
		}
		return PackageType.NMS.getConstructor("PacketPlayOutChat", PACKET_PARAMS).newInstance(component, type);
	}
}