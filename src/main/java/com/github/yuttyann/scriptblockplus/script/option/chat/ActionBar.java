package com.github.yuttyann.scriptblockplus.script.option.chat;

import com.github.yuttyann.scriptblockplus.enums.reflection.PackageType;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class ActionBar extends BaseOption {

	public ActionBar() {
		super("actionbar", "@actionbar:");
	}

	@NotNull
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

	private void sendActionBar(@NotNull SBPlayer sbPlayer, @NotNull String message) throws ReflectiveOperationException {
		Player player = sbPlayer.getPlayer();
		if (Utils.isCBXXXorLater("1.11")) {
			executeCommand(player, "title " + player.getName() + " actionbar " + "{\"text\":\"" + message + "\"}", true);
		} else if (Utils.isCraftBukkit() || Utils.isPaper()) {
			String chatSerializer = "IChatBaseComponent$ChatSerializer";
			Method a = PackageType.NMS.getMethod(chatSerializer, "a", String.class);
			Object component = a.invoke(null, "{\"text\": \"" + message + "\"}");
			Class<?>[] array = { PackageType.NMS.getClass("IChatBaseComponent"), byte.class };
			Constructor<?> packetPlayOutChat = PackageType.NMS.getConstructor("PacketPlayOutChat", array);
			Object handle = PackageType.CB_ENTITY.invokeMethod(player, "CraftPlayer", "getHandle");
			Object connection = PackageType.NMS.getField("EntityPlayer", "playerConnection").get(handle);
			Object packet = packetPlayOutChat.newInstance(component, (byte) 2);
			Class<?> packetClass = PackageType.NMS.getClass("Packet");
			PackageType.NMS.getMethod("PlayerConnection", "sendPacket", packetClass).invoke(connection, packet);
		} else {
			throw new UnsupportedOperationException("Unsupported server. | Supported Servers <PaperMC, CraftBukkit, Spigot>");
		}
	}

	private class Task extends BukkitRunnable {

		final int stay;
		final String message;

		int tick;

		Task(int stay, @NotNull String message) {
			this.tick = 0;
			this.stay = stay + 1;
			this.message = message;
		}

		@Override
		public void run() {
			try {
				if (!getSBPlayer().isOnline() || tick++ >= stay) {
					sendActionBar(getSBPlayer(), "");
					cancel();
				} else {
					sendActionBar(getSBPlayer(), message);
				}
			} catch (Exception e) {
				cancel();
			}
		}
	}
}