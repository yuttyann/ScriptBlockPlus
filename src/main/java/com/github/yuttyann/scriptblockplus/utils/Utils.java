package com.github.yuttyann.scriptblockplus.utils;

import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.selector.CommandSelector;
import org.apache.commons.lang.Validate;
import org.bukkit.*;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * ScriptBlockPlus Utils クラス
 * @author yuttyann44581
 */
public final class Utils {

	private static final String SERVER_VERSION = getServerVersion();
	private static final Map<String, Boolean> VC_CACHE = new HashMap<>();

	@NotNull
	public static String getServerVersion() {
		if (SERVER_VERSION == null) {
			String version = Bukkit.getBukkitVersion();
			return version.substring(0, version.indexOf("-"));
		}
		return SERVER_VERSION;
	}

	public static boolean isPlatform() {
		return SBConfig.PLATFORMS.getValue().contains(Bukkit.getName());
	}

	public static boolean isCBXXXorLater(@NotNull String version) {
		Boolean result = VC_CACHE.get(version);
		if (result == null) {
			VC_CACHE.put(version, result = isUpperVersion(getServerVersion(), version));
		}
		return result;
	}

	public static boolean isUpperVersion(@NotNull String source, @NotNull String target) {
		if (StringUtils.isNotEmpty(source) && StringUtils.isNotEmpty(target)) {
			return getVersionInt(source) >= getVersionInt(target);
		}
		return false;
	}

	public static int getVersionInt(@NotNull String source) {
		String[] array = StringUtils.split(source, ".");
		int result = (Integer.parseInt(array[0]) * 100000) + (Integer.parseInt(array[1]) * 1000);
		if (array.length == 3) {
			result += Integer.parseInt(array[2]);
		}
		return result;
	}

	@NotNull
	public static String getFormatTime() {
		return getFormatTime("yyyy/MM/dd HH:mm:ss");
	}

	@NotNull
	public static String getFormatTime(@NotNull String pattern) {
		Validate.notNull(pattern, "Pattern cannot be null");
		return new SimpleDateFormat(pattern).format(new Date());
	}

	public static void sendColorMessage(@NotNull CommandSender sender, @Nullable String message) {
		message = StringUtils.setColor(message, true);
		message = StringUtils.replace(message, "\\n", "|~");
		String color = "";
		for (String line : StringUtils.split(message, "|~")) {
			sender.sendMessage(line = (color + line));
			if (line.indexOf('§') > -1) {
				color = StringUtils.getColors(line);
			}
		}
	}

	@SuppressWarnings("deprecation")
	public static boolean dispatchCommand(@NotNull CommandSender sender, @Nullable Location location, @NotNull String command) {
		Validate.notNull(sender, "Sender cannot be null");
		Validate.notNull(command, "Command cannot be null");
		boolean isCommandSelector = isPlatform() && SBConfig.COMMAND_SELECTOR.getValue();
		if (isCommandSelector && (isCBXXXorLater("1.13") || CommandSelector.isCommandPattern(command))) {
			if (location == null) {
				if (sender instanceof Player) {
					location = ((Player) sender).getLocation().clone();
				} else if (sender instanceof SBPlayer) {
					location = ((SBPlayer) sender).getLocation().clone();
				} else if (sender instanceof BlockCommandSender) {
					location = ((BlockCommandSender) sender).getBlock().getLocation().clone();
				} else if (sender instanceof CommandMinecart) {
					location = ((CommandMinecart) sender).getLocation().clone();
				}
			}
			if (location != null) {
				return CommandSelector.getListener().executeCommand(sender, location, command);
			}
		}
		return Bukkit.dispatchCommand(sender, command.startsWith("/") ? command.substring(1) : command);
	}

	@Nullable
	public static World getWorld(@NotNull String name) {
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

	@NotNull
	public static Set<OfflinePlayer> getAllPlayers() {
		Set<OfflinePlayer> players = new HashSet<>(Bukkit.getOnlinePlayers());
		Collections.addAll(players, Bukkit.getOfflinePlayers());
		return players;
	}

	@SuppressWarnings("deprecation")
	public static void updateInventory(@NotNull Player player) {
		player.updateInventory();
	}

	@Nullable
	public static String getName(@NotNull UUID uuid) {
		try {
			OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
			return !player.hasPlayedBefore() ? NameFetcher.getName(uuid) : player.getName();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}