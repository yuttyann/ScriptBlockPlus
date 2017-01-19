package com.github.yuttyann.scriptblockplus.utils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.github.yuttyann.scriptblockplus.file.PluginYaml;

public class Utils {

	private static Boolean isWindows;

	public static boolean isWindows() {
		if (isWindows == null) {
			isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
		}
		return isWindows;
	}

	public static boolean isPluginEnabled(String plugin) {
		return Bukkit.getServer().getPluginManager().isPluginEnabled(plugin);
	}

	public static Plugin getPlugin(String plugin) {
		return Bukkit.getServer().getPluginManager().getPlugin(plugin);
	}

	public static void disablePlugin(Plugin plugin) {
		Bukkit.getServer().getPluginManager().disablePlugin(plugin);
	}

	private static Boolean isCB175orLaterCache;
	private static Boolean isCB178orLaterCache;
	private static Boolean isCB18orLaterCache;
	private static Boolean isCB19orLaterCache;

	public static boolean isCB175orLater() {
		if (isCB175orLaterCache == null) {
			isCB175orLaterCache = isUpperVersion(getVersion(), "1.7.5");
		}
		return isCB175orLaterCache;
	}

	public static boolean isCB178orLater() {
		if (isCB178orLaterCache == null) {
			isCB178orLaterCache = isUpperVersion(getVersion(), "1.7.8");
		}
		return isCB178orLaterCache;
	}

	public static boolean isCB18orLater() {
		if (isCB18orLaterCache == null) {
			isCB18orLaterCache = isUpperVersion(getVersion(), "1.8");
		}
		return isCB18orLaterCache;
	}

	public static boolean isCB19orLater() {
		if (isCB19orLaterCache == null) {
			isCB19orLaterCache = isUpperVersion(getVersion(), "1.9");
		}
		return isCB19orLaterCache;
	}

	public static boolean isUpperVersion(String version, String target) {
		return versionInt(version.split("\\.")) >= versionInt(target.split("\\."));
	}

	public static int versionInt(String[] versions) {
		if (versions.length < 3) {
			versions = new String[]{versions[0], versions[1], "0"};
		}
		if (versions[1].length() == 1) {
			versions[1] = "0" + versions[1];
		}
		if (versions[2].length() == 1) {
			versions[2] = "0" + versions[2];
		}
		versions[2] = "0" + versions[2];
		return Integer.parseInt(versions[0]) * 100000 + Integer.parseInt(versions[1]) * 1000 + Integer.parseInt(versions[2]);
	}

	public static String getVersion() {
		String version = Bukkit.getServer().getVersion();
		version = version.split("\\(")[1];
		version = version.substring(4, version.length() - 1);
		return version;
	}

	public static String getRandomColor() {
		return ChatColor.getByChar(Integer.toHexString(new Random().nextInt(16))).toString();
	}

	public static void sendPluginMessage(Object msg) {
		if (msg == null) {
			return;
		}
		String message = msg.toString();
		String prefix = "[" + PluginYaml.getName() + "] ";
		if (message.contains("\\n")) {
			String[] newLine = message.split("\\\\n");
			ConsoleCommandSender sender = Bukkit.getConsoleSender();
			for(int i = 0, l = newLine.length ; i < l ; i++) {
				sender.sendMessage(prefix + newLine[i]);
			}
		} else {
			Bukkit.getConsoleSender().sendMessage(prefix + message);
		}
	}

	public static void sendPluginMessage(CommandSender sender, Object msg) {
		if (msg == null) {
			return;
		}
		String message = msg.toString();
		String colorCode = "";
		if (sender instanceof Player) {
			for (ChatColor color : ChatColor.values()) {
				if (message.startsWith(color.toString())) {
					colorCode = color.toString();
					break;
				}
			}
		}
		String prefix = "[" + PluginYaml.getName() + "] ";
		if (message.contains("\\n")) {
			String[] newLine = message.split("\\\\n");
			for(int i = 0, l = newLine.length ; i < l ; i++) {
				sender.sendMessage(colorCode + prefix + newLine[i]);
			}
		} else {
			sender.sendMessage(colorCode + prefix + message);
		}
	}

	public static String stringBuilder(String[] args, Integer integer) {
		StringBuilder builder = new StringBuilder();
		for (int i = integer; i < args.length; i++) {
			if (i > integer) {
				builder.append(" ");
			}
			builder.append(args[i]);
		}
		return builder.toString();
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getItemInHand(Player player) {
		if(isCB19orLater()) {
			return player.getInventory().getItemInMainHand();
		} else {
			return player.getInventory().getItemInHand();
		}
	}

	@SuppressWarnings("deprecation")
	public static ItemStack setItemInHand(Player player, ItemStack item) {
		if(isCB19orLater()) {
			player.getInventory().setItemInMainHand(item);
		} else {
			player.getInventory().setItemInHand(item);
		}
		return getItemInHand(player);
	}

	public static World getWorld(String name) {
		World world = null;
		if (Bukkit.getWorld(name) != null) {
			world = Bukkit.getWorld(name);
		} else if (isWorld(name)) {
			world = Bukkit.createWorld(WorldCreator.name(name));
		}
		return world;
	}

	public static boolean isWorld(String world) {
		return new File(world + "/level.dat").exists();
	}

	public static String getDateFormat(String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(new Date());
	}

	public static String getName(String uuid) {
		String name = null;
		Object player = null;
		if ((player = getOnlinePlayer(uuid)) != null) {
			name = ((Player) player).getName();
		} else if ((player = getOfflinePlayer(uuid)) != null) {
			name = ((OfflinePlayer) player).getName();
		}
		return name;
	}

	public static UUID getUniqueId(String name) {
		UUID uuid = null;
		Object player = null;
		if ((player = getOnlinePlayer(name)) != null) {
			uuid = ((Player) player).getUniqueId();
		} else if ((player = getOfflinePlayer(name)) != null) {
			if (isCB175orLater()) {
				uuid = ((OfflinePlayer) player).getUniqueId();
			} else {
				try {
					uuid = UUIDFetcher.getUniqueId(name);
				} catch (Exception e) {}
			}
		}
		return uuid;
	}

	public static Player getOnlinePlayer(Object uuid_or_name) {
		boolean isUUID = false;
		if (uuid_or_name instanceof UUID) {
			isUUID = true;
		} else if (uuid_or_name instanceof String) {
			String str = uuid_or_name.toString();
			if (isUUID(str)) {
				uuid_or_name = fromString(str);
				isUUID = true;
			}
		}
		Object value;
		for (Player player : getOnlinePlayers()) {
			if (isUUID) {
				value = player.getUniqueId();
			} else {
				value = player.getName();
			}
			if (value.equals(uuid_or_name)) {
				return player;
			}
		}
		return null;
	}

	public static OfflinePlayer getOfflinePlayer(Object uuid_or_name) {
		boolean isUUID = false;
		if (uuid_or_name instanceof UUID) {
			isUUID = true;
		} else if (uuid_or_name instanceof String) {
			String str = uuid_or_name.toString();
			if (isUUID(str)) {
				uuid_or_name = fromString(str);
				isUUID = true;
			}
		}
		Object value;
		for (OfflinePlayer player : getOfflinePlayers()) {
			if (isUUID) {
				if (isCB175orLater()) {
					value = player.getUniqueId();
				} else {
					value = getUniqueId(player.getName());
				}
			} else {
				value = player.getName();
			}
			if (value.equals(uuid_or_name)) {
				return player;
			}
		}
		return null;
	}

	private static final String REGEX_H = "[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}";
	private static final String REGEX_NH = "[0-9a-f]{8}[0-9a-f]{4}[1-5][0-9a-f]{3}[89ab][0-9a-f]{3}[0-9a-f]{12}";

	public static boolean isUUID(String uuid) {
		return uuid.matches(REGEX_H) || uuid.matches(REGEX_NH);
	}

	public static UUID fromString(String uuid) {
		if (uuid.matches(REGEX_NH)) {
			return UUID.fromString(uuid.substring(0, 8) + "-" + uuid.substring(8, 12) + "-" + uuid.substring(12, 16) + "-" + uuid.substring(16, 20) + "-" + uuid.substring(20, 32));
		}
		return UUID.fromString(uuid);
	}

	public static ArrayList<Player> getOnlinePlayers() {
		try {
			return getOnlinePlayers_Method();
		} catch (Exception e) {
			ArrayList<Player> players = new ArrayList<Player>();
			for (Player player : Bukkit.getOnlinePlayers()) {
				players.add(player);
			}
			return players;
		}
	}

	public static ArrayList<OfflinePlayer> getOfflinePlayers() {
		try {
			return getOfflinePlayers_Method();
		} catch (Exception e) {
			ArrayList<OfflinePlayer> players = new ArrayList<OfflinePlayer>();
			for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
				players.add(player);
			}
			return players;
		}
	}

	@SuppressWarnings("unchecked")
	private static ArrayList<Player> getOnlinePlayers_Method() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		try {
			if (Bukkit.class.getMethod("getOnlinePlayers", new Class<?>[0]).getReturnType() == Collection.class) {
				Collection<?> temp = ((Collection<?>) Bukkit.class.getMethod("getOnlinePlayers", new Class<?>[0]).invoke(null, new Object[0]));
				return new ArrayList<Player>((Collection<? extends Player>) temp);
			} else {
				Player[] temp = ((Player[]) Bukkit.class.getMethod("getOnlinePlayers", new Class<?>[0]).invoke(null, new Object[0]));
				ArrayList<Player> players = new ArrayList<Player>();
				for (Player t : temp) {
					players.add(t);
				}
				return players;
			}
		} catch (NoSuchMethodException e) {
			throw e;
		} catch (InvocationTargetException e) {
			throw e;
		} catch (IllegalAccessException e) {
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	private static ArrayList<OfflinePlayer> getOfflinePlayers_Method() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		try {
			if (Bukkit.class.getMethod("getOfflinePlayers", new Class<?>[0]).getReturnType() == Collection.class) {
				Collection<?> temp = ((Collection<?>) Bukkit.class.getMethod("getOfflinePlayers", new Class<?>[0]).invoke(null, new Object[0]));
				return new ArrayList<OfflinePlayer>((Collection<? extends OfflinePlayer>) temp);
			} else {
				OfflinePlayer[] temp = ((OfflinePlayer[]) Bukkit.class.getMethod("getOfflinePlayers", new Class<?>[0]).invoke(null, new Object[0]));
				ArrayList<OfflinePlayer> players = new ArrayList<OfflinePlayer>();
				for (OfflinePlayer t : temp) {
					players.add(t);
				}
				return players;
			}
		} catch (NoSuchMethodException e) {
			throw e;
		} catch (InvocationTargetException e) {
			throw e;
		} catch (IllegalAccessException e) {
			throw e;
		}
	}
}