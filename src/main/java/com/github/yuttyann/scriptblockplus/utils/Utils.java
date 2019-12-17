package com.github.yuttyann.scriptblockplus.utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;

import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.selector.CommandSelector;

public final class Utils {

	private static final String SERVER_VERSION = getServerVersion();
	private static final Map<String, Boolean> VC_CACHE = new HashMap<>();

	public static String getServerVersion() {
		if (SERVER_VERSION == null) {
			String version = Bukkit.getBukkitVersion();
			return version.substring(0, version.indexOf("-"));
		}
		return SERVER_VERSION;
	}

	public static boolean isCraftBukkit() {
		return Bukkit.getName().equals("CraftBukkit");
	}

	public static boolean isCBXXXorLater(String version) {
		Boolean result = VC_CACHE.get(version);
		if (result == null) {
			VC_CACHE.put(version, result = isUpperVersion(getServerVersion(), version));
		}
		return result;
	}

	public static boolean isUpperVersion(String source, String target) {
		if (StringUtils.isNotEmpty(source) && StringUtils.isNotEmpty(target)) {
			return getVersionInt(source) >= getVersionInt(target);
		}
		return false;
	}

	public static int getVersionInt(String source) {
		String[] array = StringUtils.split(source, ".");
		int result = (Integer.parseInt(array[0]) * 100000) + (Integer.parseInt(array[1]) * 1000);
		if (array.length == 3) {
			result += Integer.parseInt(array[2]);
		}
		return result;
	}

	public static String getFormatTime() {
		return getFormatTime("yyyy/MM/dd HH:mm:ss");
	}

	public static String getFormatTime(String pattern) {
		Validate.notNull(pattern, "Pattern cannot be null");
		return new SimpleDateFormat(pattern).format(new Date());
	}

	public static void sendMessage(String message) {
		sendMessage(Bukkit.getConsoleSender(), message);
	}

	public static void sendMessage(CommandSender sender, String message) {
		if (StringUtils.isNotEmpty(message)) {
			message = StringUtils.replace(message, "\\n", "|~");
			String color = "";
			for (String line : StringUtils.split(message, "|~")) {
				sender.sendMessage(line = (color + line));
				if (line.indexOf('§') > -1) {
					color = StringUtils.getColors(line);
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	public static boolean dispatchCommand(CommandSender sender, Location location, String command) {
		Validate.notNull(sender, "Sender cannot be null");
		Validate.notNull(command, "Command cannot be null");
		boolean isCommandSelector = isCraftBukkit() && SBConfig.isCommandSelector();
		if (isCommandSelector && (isCBXXXorLater("1.13") || CommandSelector.isCommandPattern(command))) {
			if (sender instanceof SBPlayer) {
				sender = ((SBPlayer) sender).getPlayer();
			}
			if (location == null) {
				if (sender instanceof Player) {
					location = ((Player) sender).getLocation().clone();
				} else if (sender instanceof BlockCommandSender) {
					location = ((BlockCommandSender) sender).getBlock().getLocation().clone();
				} else if (sender instanceof CommandMinecart) {
					location = ((CommandMinecart) sender).getLocation().clone();
				} else {
					location = new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
				}
			}
			try {
				return CommandSelector.getListener().executeCommand(sender, location, command);
			} catch (Exception e) {
				e.printStackTrace();
				return Bukkit.dispatchCommand(sender, command.startsWith("/") ? command.substring(1) : command);
			}
		} else {
			return Bukkit.dispatchCommand(sender, command.startsWith("/") ? command.substring(1) : command);
		}
	}

	public static World getWorld(String name) {
		Validate.notNull(name, "Name cannot be null");
		World world = Bukkit.getWorld(name);
		if (world == null) {
			File file = new File(Bukkit.getWorldContainer(), name + "/level.dat");
			if (file.exists()) {
				world = Bukkit.createWorld(WorldCreator.name(name));
			}
		}
		return world;
	}

	@SuppressWarnings("deprecation")
	public static void updateInventory(Player player) {
		player.updateInventory();
	}

	public static Player getPlayer(String name) {
		if (StringUtils.isEmpty(name)) {
			return null;
		}
		return StreamUtils.fOrElse(Bukkit.getOnlinePlayers(), p -> name.equals(p.getName()), null);
	}

	public static OfflinePlayer getOfflinePlayer(UUID uuid) {
		OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
		return player == null || !player.hasPlayedBefore() ? null : player;
	}

	public static String getName(UUID uuid) {
		try {
			OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
			return player == null || !player.hasPlayedBefore() ? NameFetcher.getName(uuid) : player.getName();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}