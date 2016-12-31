package com.github.yuttyann.scriptblockplus.util;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.github.yuttyann.scriptblockplus.Main;
import com.github.yuttyann.scriptblockplus.PlayerSelector;

public class Utils {

	public static boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().startsWith("windows");
	}

	public static double getJavaVersion() {
		return Double.parseDouble(System.getProperty("java.specification.version"));
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

	private static Boolean isUpperVersion_v175;
	private static Boolean isUpperVersion_v178;
	private static Boolean isUpperVersion_v19;

	public static boolean isUpperVersion_v175() {
		if (isUpperVersion_v175 == null) {
			isUpperVersion_v175 = isUpperVersion(getVersion(), "1.7.5");
		}
		return isUpperVersion_v175;
	}

	public static boolean isUpperVersion_v178() {
		if (isUpperVersion_v178 == null) {
			isUpperVersion_v178 = isUpperVersion(getVersion(), "1.7.8");
		}
		return isUpperVersion_v178;
	}

	public static boolean isUpperVersion_v19() {
		if (isUpperVersion_v19 == null) {
			isUpperVersion_v19 = isUpperVersion(getVersion(), "1.9");
		}
		return isUpperVersion_v19;
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

	public static String sendPluginMessage(Object msg) {
		if (msg == null) {
			return null;
		}
		String result = "[" + Main.instance.getPluginName() + "] " + msg.toString();
		Bukkit.getConsoleSender().sendMessage(result);
		return result;
	}

	public static String sendPluginMessage(CommandSender sender, Object msg) {
		if (msg == null) {
			return null;
		}
		String message = msg.toString();
		String result = "[" + Main.instance.getPluginName() + "] " + message;
		String colorcode = "";
		for (ChatColor color : ChatColor.values()) {
			if (message.startsWith(color.toString())) {
				colorcode = color.toString();
				break;
			}
		}
		sender.sendMessage(colorcode + result);
		return result;
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
		if(isUpperVersion_v19()) {
			return player.getInventory().getItemInMainHand();
		} else {
			return player.getInventory().getItemInHand();
		}
	}

	@SuppressWarnings("deprecation")
	public static ItemStack setItemInHand(Player player, ItemStack item) {
		if(isUpperVersion_v19()) {
			player.getInventory().setItemInMainHand(item);
		} else {
			player.getInventory().setItemInHand(item);
		}
		return getItemInHand(player);
	}

	public static String getTimeFormat(String format) {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	public static String getName(UUID uuid) {
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
			if (isUpperVersion_v175()) {
				uuid = ((OfflinePlayer) player).getUniqueId();
			} else {
				try {
					uuid = UUIDFetcher.getUniqueId(name);
				} catch (Exception e) {}
			}
		}
		return uuid;
	}

	public static void dispatchCommand(CommandSender sender, Location location, String command) {
		if (command.startsWith("/")) {
			command = command.substring(1);
		}
		String pattern = PlayerSelector.getCommandBlockPattern(command);
		if (pattern != null) {
			if (location == null) {
				if (sender instanceof Player) {
					location = ((Player) sender).getLocation().clone();
				} else if (sender instanceof BlockCommandSender) {
					location = ((BlockCommandSender) sender).getBlock().getLocation().clone();
				}
			}
			Player[] players = PlayerSelector.getPlayers(location, pattern);
			if (players != null) {
				for (Player player : players) {
					Bukkit.dispatchCommand(sender, command.replace(pattern, player.getName()));
				}
			}
		} else {
			Bukkit.dispatchCommand(sender, command);
		}
	}

	public static Player getOnlinePlayer(Object uuid_or_name) {
		boolean isUUID = false;
		if (uuid_or_name instanceof UUID) {
			isUUID = true;
		} else if (uuid_or_name instanceof String) {
			try {
				if (((String) uuid_or_name).contains("-")) {
					uuid_or_name = fromString(uuid_or_name.toString());
				} else {
					uuid_or_name = UUID.fromString(uuid_or_name.toString());
				}
				isUUID = true;
			} catch (Exception e) {
				e.printStackTrace();
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
			try {
				if (((String) uuid_or_name).contains("-")) {
					uuid_or_name = fromString(uuid_or_name.toString());
				} else {
					uuid_or_name = UUID.fromString(uuid_or_name.toString());
				}
				isUUID = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Object value;
		for (OfflinePlayer player : getOfflinePlayers()) {
			if (isUUID) {
				if (isUpperVersion_v175()) {
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

	public static UUID fromString(String id) {
		return UUID.fromString(id.substring(0, 8) + "-" + id.substring(8, 12) + "-" + id.substring(12, 16) + "-" + id.substring(16, 20) + "-" + id.substring(20, 32));
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