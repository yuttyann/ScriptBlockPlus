package com.github.yuttyann.scriptblockplus.hook.plugin;

import com.github.yuttyann.scriptblockplus.hook.HookPlugin;
import com.github.yuttyann.scriptblockplus.script.option.other.Calculation;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * ScriptBlockPlus PsudoCommand クラス
 * @author yuttyann44581
 */
public final class PsudoCommand extends HookPlugin {

	public static final PsudoCommand INSTANCE = new PsudoCommand();

	private final static Method METHOD_GET_TARGETS = _getTargets();

	@Override
	@NotNull
	public String getPluginName() {
		return "PsudoCommand";
	}

	@NotNull
	public Entity[] getTargets(@NotNull CommandSender sender, @NotNull String selector) {
		if (Utils.isCBXXXorLater("1.13.2")) {
			return Bukkit.selectEntities(sender, selector).toArray(new Entity[0]);
		}
		try {
			return METHOD_GET_TARGETS == null ? new Entity[] { } : (Entity[]) METHOD_GET_TARGETS.invoke(null, sender, selector);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return new Entity[] { };
	}

	public int getIntRelative(@NotNull String target, @NotNull String relative, @NotNull Entity entity) {
		int number = 0;
		if (StringUtils.isNotEmpty(target) && Calculation.REALNUMBER_PATTERN.matcher(target).matches()) {
			number = Integer.parseInt(target);
		}
		switch (relative) {
			case "x":
				return entity.getLocation().getBlockX() + number;
			case "y":
				return entity.getLocation().getBlockY() + number;
			case "z":
				return entity.getLocation().getBlockZ() + number;
			default:
				return 0;
		}
	}

	@Nullable
	private static Method _getTargets() {
		try {
			Class<?> commandUtils = Class.forName("me.zombie_striker.psudocommands.CommandUtils");
			return commandUtils.getMethod("getTargets", CommandSender.class, String.class);
		} catch (ClassNotFoundException | NoSuchMethodException e) {
			return null;
		}
	}
}