package com.github.yuttyann.scriptblockplus.utils;

import java.io.File;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

import com.github.yuttyann.scriptblockplus.PlayerSelector;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.file.Files;

public class Utils {

	private static Boolean isWindows;
	private static Boolean isCB175orLaterCache;
	private static Boolean isCB178orLaterCache;
	private static Boolean isCB18orLaterCache;
	private static Boolean isCB19orLaterCache;
	private static String serverVersion;

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
			isCB175orLaterCache = isUpperVersion(getServerVersion(), "1.7.5");
		}
		return isCB175orLaterCache;
	}

	public static boolean isCB178orLater() {
		if (isCB178orLaterCache == null) {
			isCB178orLaterCache = isUpperVersion(getServerVersion(), "1.7.8");
		}
		return isCB178orLaterCache;
	}

	public static boolean isCB18orLater() {
		if (isCB18orLaterCache == null) {
			isCB18orLaterCache = isUpperVersion(getServerVersion(), "1.8");
		}
		return isCB18orLaterCache;
	}

	public static boolean isCB19orLater() {
		if (isCB19orLaterCache == null) {
			isCB19orLaterCache = isUpperVersion(getServerVersion(), "1.9");
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

	public static String getServerVersion() {
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
		sendPluginMessage(ScriptBlock.getInstance(), Bukkit.getConsoleSender(), msg);
	}

	public static void sendPluginMessage(CommandSender sender, Object msg) {
		sendPluginMessage(ScriptBlock.getInstance(), sender, msg);
	}

	public static void sendPluginMessage(Plugin plugin, CommandSender sender, Object msg) {
		if (msg == null) {
			return;
		}
		String message = msg.toString();
		String prefix = getStartColor(message).toString();
		if (Files.getConfig().getBoolean("MessagePrefix")) {
			prefix += "[" + plugin.getName() + "] ";
		}
		if (message.contains("\\n")) {
			String[] newLine = StringUtils.split(message, "\\n");
			for(int i = 0, l = newLine.length; i < l; i++) {
				sender.sendMessage(prefix + newLine[i]);
			}
		} else {
			sender.sendMessage(prefix + message);
		}
	}

	public static void dispatchCommand(Player player, String command, Location location) {
		if (command.startsWith("/")) {
			command = command.substring(1);
		}
		String pattern = PlayerSelector.getCommandBlockPattern(command);
		if (pattern != null) {
			Player[] players = PlayerSelector.getPlayers(location, pattern);
			if (players != null) {
				for (Player p : players) {
					Bukkit.dispatchCommand(player, StringUtils.replace(command, pattern, p.getName()));
				}
			}
		} else {
			Bukkit.dispatchCommand(player, command);
		}
	}

	public static ChatColor getStartColor(String text) {
		for (ChatColor color : ChatColor.values()) {
			if (text.startsWith(color.toString())) {
				return color;
			}
		}
		return ChatColor.WHITE;
	}

	@SuppressWarnings("deprecation")
	public static void updateInventory(Player player) {
		player.updateInventory();
	}

	@SuppressWarnings("deprecation")
	public static String getId(String source) {
		if (source.matches("\\A[-]?[0-9]+\\z")) {
			return source;
		}
		return String.valueOf(Material.getMaterial(source.toUpperCase()).getId());
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getItemInHand(Player player) {
		PlayerInventory inventory = player.getInventory();
		if(isCB19orLater()) {
			return inventory.getItemInMainHand();
		} else {
			return inventory.getItemInHand();
		}
	}

	public static String getItemName(ItemStack item) {
		if (item == null || item.getType() == Material.AIR) {
			return null;
		}
		return (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) ? item.getItemMeta().getDisplayName() : null;
	}

	public static boolean checkItem(ItemStack item, Material material, String name) {
		String itemName = getItemName(item);
		return item != null && item.getType() == material && itemName != null && itemName.equals(name);
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

	public static String getName(UUID uuid) {
		OfflinePlayer player = null;
		try {
			boolean isCB175orLater = isCB175orLater();
			for (OfflinePlayer offline : Bukkit.getOfflinePlayers()) {
				if (uuid.equals(isCB175orLater ? offline.getUniqueId() : getUniqueId(offline.getName()))) {
					player = offline;
					break;
				}
			}
			return player != null ? player.getName() : ProfileFetcher.getName(uuid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static UUID getUniqueId(String name) {
		OfflinePlayer player = null;
		try {
			for (OfflinePlayer offline : Bukkit.getOfflinePlayers()) {
				if (name.equals(offline.getName())) {
					player = offline;
					break;
				}
			}
			if (player != null) {
				return isCB175orLater() ? player.getUniqueId() : ProfileFetcher.getUniqueId(player.getName());
			}
			return ProfileFetcher.getUniqueId(name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean isUUID(String uuid) {
		return uuid.matches("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}");
	}
}