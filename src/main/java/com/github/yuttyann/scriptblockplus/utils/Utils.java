package com.github.yuttyann.scriptblockplus.utils;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.CommandLog;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.hook.CommandSelector;
import com.github.yuttyann.scriptblockplus.hook.plugin.PsudoCommand;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Supplier;

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
		return new SimpleDateFormat(pattern).format(new Date());
	}

	public static void sendColorMessage(@NotNull CommandSender sender, @Nullable String message) {
		if (isEmpty(message)) {
			return;
		}
		message = replace(setColor(message), "\\n", "|~");
		String color = "";
		for (String line : split(message, "|~")) {
			sender.sendMessage(line = (color + line));
			if (line.indexOf('§') > -1) {
				color = getColors(line);
			}
		}
	}

	public static boolean tempPerm(@NotNull SBPlayer sbPlayer, @NotNull Permission permission, @NotNull Supplier<Boolean> supplier) {
		return sbPlayer.isOnline() && CommandLog.supplier(sbPlayer.getWorld(), () -> {
			if (sbPlayer.hasPermission(permission.getNode())) {
				return supplier.get();
			} else {
				PermissionAttachment attachment = sbPlayer.addAttachment(ScriptBlock.getInstance());
				try {
					attachment.setPermission(permission.getNode(), true);
					return supplier.get();
				} finally {
					attachment.unsetPermission(permission.getNode());
				}
			}
		});
	}

	public static boolean dispatchCommand(@NotNull CommandSender sender, @NotNull String command) {
		command = command.startsWith("/") ? command.substring(1) : command;
		CommandSender commandSender = sender instanceof SBPlayer ? ((SBPlayer) sender).getPlayer() : sender;
		if (CommandSelector.INSTANCE.has(command) && (isCBXXXorLater("1.13.2") || PsudoCommand.INSTANCE.has())) {
			List<String> commands = CommandSelector.INSTANCE.build(commandSender, command);
			return commands.stream().allMatch(s -> Bukkit.dispatchCommand(commandSender, s));
		}
		return Bukkit.dispatchCommand(commandSender, command);
	}

	@NotNull
	public static World getWorld(@NotNull String name) {
		World world = Bukkit.getWorld(name);
		if (world == null) {
			File file = new File(Bukkit.getWorldContainer(), name + "/level.dat");
			if (file.exists()) {
				world = Bukkit.createWorld(WorldCreator.name(name));
			}
		}
		return Objects.requireNonNull(world);
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