package com.github.yuttyann.scriptblockplus.utils;

import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.hook.CommandSelector;
import com.github.yuttyann.scriptblockplus.hook.plugin.PsudoCommand;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.yuttyann.scriptblockplus.utils.StringUtils.*;

/**
 * ScriptBlockPlus Utils クラス
 * @author yuttyann44581
 */
public final class Utils {

	private static final String SERVER_VERSION = getServerVersion();
	private static final Map<String, Boolean> VC_CACHE = new HashMap<>();

	@NotNull
	public static String randomUUID() {
		return UUID.randomUUID().toString();
	}

	@NotNull
	public static <T extends Plugin> T getPlugin(@NotNull Class<? extends Plugin> plugin) {
		Plugin[] plugins = Bukkit.getPluginManager().getPlugins();
		Predicate<? super Plugin> classEquals = p -> p.getClass().equals(plugin);
		return (T) Stream.of(plugins).filter(classEquals).findFirst().orElseThrow(NullPointerException::new);
	}

	@NotNull
	public static String getPluginName(@NotNull Plugin plugin) {
		return plugin.getName() + " v" + plugin.getDescription().getVersion();
	}

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
		if (isNotEmpty(source) && isNotEmpty(target)) {
			return getVersionInt(source) >= getVersionInt(target);
		}
		return false;
	}

	public static int getVersionInt(@NotNull String source) {
		String[] array = split(source, ".");
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
		if (isNotEmpty(message)) {
			message = replace(setColor(message), "\\n", "|~");
			String color = "";
			for (String line : split(message, "|~")) {
				sender.sendMessage(line = (color + line));
				if (line.indexOf('§') > -1) {
					color = getColors(line);
				}
			}
		}
	}

	public static boolean dispatchCommand(@NotNull CommandSender sender, @NotNull String command) {
		Validate.notNull(sender, "Sender cannot be null");
		Validate.notNull(command, "Command cannot be null");
		if (sender instanceof SBPlayer && ((SBPlayer) sender).isOnline()) {
			sender = Objects.requireNonNull(((SBPlayer) sender).getPlayer());
		}
		command = command.startsWith("/") ? command.substring(1) : command;
		if (CommandSelector.INSTANCE.has(command) && (isCBXXXorLater("1.13.2") || PsudoCommand.INSTANCE.has())) {
			int succsess = 0;
			List<StringBuilder> list = CommandSelector.INSTANCE.build(sender, command);
			for (StringBuilder builder : list) {
				if (Bukkit.dispatchCommand(sender, builder.toString())) {
					succsess++;
				}
			}
			return list.size() == succsess;
		}
		return Bukkit.dispatchCommand(sender, command);
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