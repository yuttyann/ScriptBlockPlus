package com.github.yuttyann.scriptblockplus.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.yuttyann.scriptblockplus.Main;
import com.github.yuttyann.scriptblockplus.PlayerSelector;
import com.github.yuttyann.scriptblockplus.file.Yaml;

public class Utils {

	public static boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().startsWith("windows");
	}

	public static boolean isWorld(String world) {
		return new File(world + "/level.dat").exists();
	}

	public static boolean isPluginEnabled(String plugin) {
		return Bukkit.getServer().getPluginManager().isPluginEnabled(plugin);
	}

	public static boolean isUpperVersion(String target) {
		return versionInt(getVersion().split("\\.")) >= versionInt(target.split("\\."));
	}

	public static boolean isUpperVersion(String version, String target) {
		return versionInt(version.split("\\.")) >= versionInt(target.split("\\."));
	}

	private static Boolean isUpperVersion_v175;
	private static Boolean isUpperVersion_v178;
	private static Boolean isUpperVersion_v18;
	private static Boolean isUpperVersion_v19;

	public static boolean isUpperVersion_v175() {
		if (isUpperVersion_v175 == null) {
			isUpperVersion_v175 = isUpperVersion("1.7.5");
		}
		return isUpperVersion_v175;
	}

	public static boolean isUpperVersion_v178() {
		if (isUpperVersion_v178 == null) {
			isUpperVersion_v178 = isUpperVersion("1.7.8");
		}
		return isUpperVersion_v178;
	}

	public static boolean isUpperVersion_v18() {
		if (isUpperVersion_v18 == null) {
			isUpperVersion_v18 = isUpperVersion("1.8");
		}
		return isUpperVersion_v18;
	}

	public static boolean isUpperVersion_v19() {
		if (isUpperVersion_v19 == null) {
			isUpperVersion_v19 = isUpperVersion("1.9");
		}
		return isUpperVersion_v19;
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

	public static Random getRandom() {
		return new Random();
	}

	public static String getLineFeedCode() {
		return "\n";
	}

	public static String sendPluginMessage(Object msg) {
		String result = "[" + Main.instance.getPluginName() + "] " + msg.toString();
		Bukkit.getConsoleSender().sendMessage(result);
		return result;
	}

	public static String sendPluginMessage(CommandSender sender, Object msg) {
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

	public static String getTime(String format) {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	public static String getVersion() {
		String version = Bukkit.getServer().getVersion();
		version = version.split("\\(")[1];
		version = version.substring(4, version.length() - 1);
		return version;
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

	public static Set<String> getConfigSection(Yaml yaml, String path, boolean key) {
		return yaml.getConfigurationSection(path).getKeys(key);
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

	@SuppressWarnings("deprecation")
	public static Player getPlayer(String name) {
		return Bukkit.getPlayer(name);
	}

	public static Player getOnlinePlayer(Object uuid_or_name) {
		boolean isUUID = false;
		if (uuid_or_name instanceof UUID) {
			isUUID = true;
		} else if (uuid_or_name instanceof String) {
			try {
				uuid_or_name = UUID.fromString(uuid_or_name.toString());
				isUUID = true;
			} catch (Exception e) {}
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
				uuid_or_name = UUID.fromString(uuid_or_name.toString());
				isUUID = true;
			} catch (Exception e) {}
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

	public static ArrayList<String> getTextList(File file) {
		FileReader fileReader = null;
		BufferedReader buReader = null;
		try {
			fileReader = new FileReader(file);
			buReader = new BufferedReader(fileReader);
			ArrayList<String> list = new ArrayList<String>();
			String line;
			while ((line = buReader.readLine()) != null) {
				list.add(line);
			}
			return list;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (buReader != null) {
				try {
					buReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fileReader != null) {
				try {
					fileReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return new ArrayList<String>();
	}

	public static ArrayList<String> getTextList(String url) {
		InputStream input = null;
		InputStreamReader inReader = null;
		BufferedReader buReader = null;
		try {
			input = new URL(url).openStream();
			inReader = new InputStreamReader(input);
			buReader = new BufferedReader(inReader);
			String line;
			ArrayList<String> list = new ArrayList<String>();
			while ((line = buReader.readLine()) != null) {
				list.add(line);
			}
			return list;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (buReader != null) {
				try {
					buReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (inReader != null) {
				try {
					inReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return new ArrayList<String>();
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
