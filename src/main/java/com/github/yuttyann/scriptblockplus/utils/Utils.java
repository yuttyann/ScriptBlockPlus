package com.github.yuttyann.scriptblockplus.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.PluginYaml;

public class Utils {

	private static final String REGEX_H = "[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}";
	private static final String REGEX_NH = "[0-9a-f]{8}[0-9a-f]{4}[1-5][0-9a-f]{3}[89ab][0-9a-f]{3}[0-9a-f]{12}";

	private static String serverVersion;
	private static Boolean isWindows;
	private static Boolean isCB175orLaterCache;
	private static Boolean isCB178orLaterCache;
	private static Boolean isCB18orLaterCache;
	private static Boolean isCB19orLaterCache;

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

	public static void callEvent(Event event) {
		Bukkit.getServer().getPluginManager().callEvent(event);
	}

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
		return getVersionInt(version) >= getVersionInt(target);
	}

	public static int getVersionInt(String version) {
		String[] array = StringUtils.split(version, ".");
		int result = (Integer.parseInt(array[0]) * 100000) + (Integer.parseInt(array[1]) * 1000);
		if (array.length == 3) {
			result += Integer.parseInt(array[2]);
		}
		return result;
	}

	public static String getVersion() {
		if (serverVersion == null) {
			String version = Bukkit.getBukkitVersion();
			serverVersion = version.substring(0, version.indexOf("-"));
		}
		return serverVersion;
	}

	public static String getRandomColor() {
		return ChatColor.getByChar(Integer.toHexString(new Random().nextInt(16))).toString();
	}

	public static void sendPluginMessage(Object msg) {
		sendPluginMessage(Bukkit.getConsoleSender(), msg);
	}

	public static void sendPluginMessage(CommandSender sender, Object msg) {
		if (msg == null) {
			return;
		}
		String message = msg.toString();
		String color = "";
		if (sender instanceof Player) {
			for (ChatColor ccolor : ChatColor.values()) {
				if (message.startsWith(ccolor.toString())) {
					color = ccolor.toString();
					break;
				}
			}
		}
		String prefix = "";
		if (Files.getConfig().getBoolean("MessagePrefix")) {
			prefix = "[" + PluginYaml.getName() + "] ";
		}
		if (message.contains("\\n")) {
			String[] newLine = StringUtils.split(message, "\n");
			for(int i = 0, l = newLine.length ; i < l ; i++) {
				sender.sendMessage(color + prefix + newLine[i]);
			}
		} else {
			sender.sendMessage(color + prefix + message);
		}
	}

	@SuppressWarnings("deprecation")
	public static void updateInventory(Player player) {
		player.updateInventory();
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

	public static String getItemName(ItemStack item) {
		if (item == null || item.getType() == Material.AIR
			|| !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
			return null;
		}
		return item.getItemMeta().getDisplayName();
	}

	public static boolean checkItem(ItemStack item, Material material, String name) {
		return item != null && item.getType() == material
				&& item.hasItemMeta() && item.getItemMeta().hasDisplayName()
				&& item.getItemMeta().getDisplayName().equals(name);
	}

	public static World getWorld(String name) {
		World world = null;
		if (Bukkit.getWorld(name) != null) {
			world = Bukkit.getWorld(name);
		} else if (new File(world + "/level.dat").exists()) {
			world = Bukkit.createWorld(WorldCreator.name(name));
		}
		return world;
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
		boolean isCB175orLater = isCB175orLater();
		for (OfflinePlayer player : getOfflinePlayers()) {
			if (isUUID) {
				if (isCB175orLater) {
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

	public static boolean isUUID(String uuid) {
		return uuid.matches(REGEX_H) || uuid.matches(REGEX_NH);
	}

	public static UUID fromString(String uuid) {
		try {
			if (uuid.matches(REGEX_NH)) {
				return UUID.fromString(uuid.substring(0, 8) + "-" + uuid.substring(8, 12) + "-" + uuid.substring(12, 16) + "-" + uuid.substring(16, 20) + "-" + uuid.substring(20, 32));
			}
			return UUID.fromString(uuid);
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<Player> getOnlinePlayers() {
		Object players = Bukkit.getOnlinePlayers();
		if (players instanceof Collection) {
			return new ArrayList<Player>((Collection<? extends Player>) players);
		}
		return new ArrayList<Player>(Arrays.asList((Player[]) players));
	}

	public static ArrayList<OfflinePlayer> getOfflinePlayers() {
		return new ArrayList<OfflinePlayer>(Arrays.asList(Bukkit.getOfflinePlayers()));
	}
}