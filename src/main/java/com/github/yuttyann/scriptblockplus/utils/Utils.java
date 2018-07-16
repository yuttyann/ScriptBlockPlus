package com.github.yuttyann.scriptblockplus.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.parser.ParseException;

import com.github.yuttyann.scriptblockplus.commandblock.FakeCommandBlock;

public class Utils {

	private static final String SERVER_VERSION = getServerVersion();
	private static final Map<String, Boolean> VC_CACHE_MAP = new HashMap<>();

	public static String getServerVersion() {
		if (SERVER_VERSION == null) {
			String version = Bukkit.getBukkitVersion();
			return version.substring(0, version.indexOf("-"));
		}
		return SERVER_VERSION;
	}

	public static boolean isCBXXXorLater(String version) {
		Boolean result = VC_CACHE_MAP.get(version);
		if (result == null) {
			result = isUpperVersion(getServerVersion(), version);
			VC_CACHE_MAP.put(version, result);
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
		if (StringUtils.isNotEmpty(message) && sender instanceof Player && ((Player) sender).isOnline()) {
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

	public static boolean dispatchCommand(CommandSender sender, Location location, String command) {
		Validate.notNull(sender, "Sender cannot be null");
		Validate.notNull(command, "Command cannot be null");
		if (FakeCommandBlock.isCommandPattern(command)) {
			if (location == null) {
				if (sender instanceof Player) {
					location = ((Player) sender).getLocation().clone();
				} else if (sender instanceof BlockCommandSender) {
					location = ((BlockCommandSender) sender).getBlock().getLocation().clone();
				} else if (sender instanceof CommandMinecart) {
					location = ((CommandMinecart) sender).getLocation().clone();
				}
			}
			Validate.notNull(location, "Location is an invalid value");
			return FakeCommandBlock.getListener().executeCommand(sender, location, command);
		} else {
			return Bukkit.dispatchCommand(sender, command.startsWith("/") ? command.substring(1) : command);
		}
	}

	@SuppressWarnings("deprecation")
	public static void updateInventory(Player player) {
		player.updateInventory();
	}

	public static Material getMaterial(int id) {
		Material material = null;
		if (!isCBXXXorLater("1.13")) {
			try {
				Class<?>[] emptyClass = ArrayUtils.EMPTY_CLASS_ARRAY;
				Object[] emptyObject = ArrayUtils.EMPTY_OBJECT_ARRAY;
				material = (Material) Material.class.getMethod("getMaterial", emptyClass).invoke(null, emptyObject);
			} catch (ReflectiveOperationException e) {
				e.printStackTrace();
			}
		}
		return material;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getItemInMainHand(Player player) {
		PlayerInventory inventory = player.getInventory();
		return isCBXXXorLater("1.9") ? inventory.getItemInMainHand() : inventory.getItemInHand();
	}

	public static ItemStack getItemInOffHand(Player player) {
		return isCBXXXorLater("1.9") ? player.getInventory().getItemInOffHand() : null;
	}

	public static ItemStack[] getHandItems(Player player) {
		return new ItemStack[]{Utils.getItemInMainHand(player), Utils.getItemInOffHand(player)};
	}

	public static String getItemName(ItemStack item, String def) {
		if (item == null || item.getType() == Material.AIR) {
			return def;
		}
		ItemMeta meta = item.getItemMeta();
		return meta == null ? def : meta.hasDisplayName() ? meta.getDisplayName() : def;
	}

	public static boolean checkItem(ItemStack item, Material material, String itemName) {
		return item == null ? false : item.getType() == material && Objects.equals(getItemName(item, null), itemName);
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
	public static Player getPlayer(String name) {
		return Bukkit.getPlayer(name);
	}

	public static Player getPlayer(UUID uuid) {
		if (isCBXXXorLater("1.7.5")) {
			return Bukkit.getPlayer(uuid);
		}
		for (Player player : getOnlinePlayers()) {
			if (player.getUniqueId().equals(uuid)) {
				return player;
			}
		}
		return null;
	}

	public static OfflinePlayer getOfflinePlayer(UUID uuid) {
		if (isCBXXXorLater("1.7.5")) {
			OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
			return player == null || !player.hasPlayedBefore() ? null : player;
		} else {
			String name = getName(uuid);
			for (OfflinePlayer offline : Bukkit.getOfflinePlayers()) {
				if (offline.getName().equals(name)) {
					return offline;
				}
			}
		}
		return null;
	}

	public static String getName(UUID uuid) {
		if (uuid != null) {
			try {
				OfflinePlayer player = null;
				if (isCBXXXorLater("1.7.5")) {
					player = Bukkit.getOfflinePlayer(uuid);
				}
				return player == null || !player.hasPlayedBefore() ? ProfileFetcher.getName(uuid) : player.getName();
			} catch (ParseException | IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static UUID getUniqueId(OfflinePlayer player) {
		if (player.hasPlayedBefore()) {
			try {
				return isCBXXXorLater("1.7.5") ? player.getUniqueId() : ProfileFetcher.getUniqueId(player.getName());
			} catch (ParseException | IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static List<Player> getOnlinePlayers() {
		Object[] emptyArray = ArrayUtils.EMPTY_OBJECT_ARRAY;
		try {
			Method method = Bukkit.class.getMethod("getOnlinePlayers", ArrayUtils.EMPTY_CLASS_ARRAY);
			if (method.getReturnType() == Collection.class) {
				return new ArrayList<>((Collection<? extends Player>) method.invoke(null, emptyArray));
			} else {
				return new ArrayList<>(Arrays.asList((Player[]) method.invoke(null, emptyArray)));
			}
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}
}