package com.github.yuttyann.scriptblockplus.utils;

import java.io.File;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.yuttyann.scriptblockplus.player.SBPlayer;

public class Utils {

	private static String serverVersion;
	private static Boolean isCB175orLaterCache;
	private static Boolean isCB178orLaterCache;
	private static Boolean isCB18orLaterCache;
	private static Boolean isCB19orLaterCache;

	public static String getServerVersion() {
		if (serverVersion == null) {
			String version = Bukkit.getBukkitVersion();
			serverVersion = version.substring(0, version.indexOf("-"));
		}
		return serverVersion;
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

	public static boolean isUpperVersion(String source, String target) {
		return getVersionInt(source) >= getVersionInt(target);
	}

	public static int getVersionInt(String source) {
		String[] array = StringUtils.split(source, ".");
		int result = (Integer.parseInt(array[0]) * 100000) + (Integer.parseInt(array[1]) * 1000);
		if (array.length == 3) {
			result += Integer.parseInt(array[2]);
		}
		return result;
	}

	public static void sendMessage(String message) {
		sendMessage(Bukkit.getConsoleSender(), message);
	}

	public static void sendMessage(SBPlayer sbPlayer, String message) {
		if (!sbPlayer.isOnline()) {
			return;
		}
		sendMessage((CommandSender) sbPlayer, message);
	}

	public static void sendMessage(CommandSender sender, String message) {
		if (sender == null || StringUtils.isEmpty(message)) {
			return;
		}
		String color = "";
		for (String line : StringUtils.split(message, "|~")) {
			sender.sendMessage(line = (color + line));
			if (line.indexOf('§') != -1) {
				color = StringUtils.getColors(line);
			}
		}
	}

	@SuppressWarnings("deprecation")
	public static void updateInventory(Player player) {
		player.updateInventory();
	}

	public static ItemStack getItemInMainHand(Player player) {
		PlayerInventory inventory = player.getInventory();
		if(isCB19orLater()) {
			return inventory.getItemInMainHand();
		} else {
			@SuppressWarnings("deprecation")
			ItemStack item = inventory.getItemInHand();
			return item;
		}
	}

	public static ItemStack getItemInOffHand(Player player) {
		if(isCB19orLater()) {
			return player.getInventory().getItemInOffHand();
		}
		return null;
	}

	public static ItemStack[] getHandItems(Player player) {
		return new ItemStack[]{Utils.getItemInMainHand(player), Utils.getItemInOffHand(player)};
	}

	public static String getItemName(ItemStack item, String def) {
		if (item == null || item.getType() == Material.AIR) {
			return def;
		}
		ItemMeta meta = item.getItemMeta();
		return meta != null && meta.hasDisplayName() ? meta.getDisplayName() : def;
	}

	public static boolean checkItem(ItemStack item, Material material, String itemName) {
		return item != null && item.getType() == material && Objects.equals(getItemName(item, null), itemName);
	}

	public static World getWorld(String name) {
		World world = Bukkit.getWorld(name);
		if (world == null) {
			File file = new File(Bukkit.getWorldContainer(), name + "/level.dat");
			if (file.exists() && file.getParentFile().isDirectory()) {
				world = Bukkit.createWorld(WorldCreator.name(name));
			}
		}
		return world;
	}

	public static Player getPlayer(UUID uuid) {
		if (Utils.isCB175orLater()) {
			return Bukkit.getPlayer(uuid);
		}
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getUniqueId().equals(uuid)) {
				return player;
			}
		}
		return null;
	}

	public static OfflinePlayer getOfflinePlayer(UUID uuid) {
		OfflinePlayer player = null;
		if (Utils.isCB175orLater()) {
			player = Bukkit.getOfflinePlayer(uuid);
		} else {
			String name = getName(uuid);
			for (OfflinePlayer offline : Bukkit.getOfflinePlayers()) {
				if (offline.getName().equals(name)) {
					return player;
				}
			}
		}
		if (player == null || !player.hasPlayedBefore()) {
			return null;
		}
		return player;
	}

	public static String getName(UUID uuid) {
		try {
			if (!isCB175orLater()) {
				return NameFetcher.getName(uuid);
			}
			OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
			if (player == null || !player.hasPlayedBefore()) {
				return NameFetcher.getName(uuid);
			}
			return player.getName();
		} catch (Exception e) {
			return null;
		}
	}
}