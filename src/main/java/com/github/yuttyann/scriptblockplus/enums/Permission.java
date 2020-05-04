package com.github.yuttyann.scriptblockplus.enums;

import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus Permission 列挙型
 * @author yuttyann44581
 */
public enum Permission {
	COMMAND_TOOL("scriptblockplus.command.tool"),
	COMMAND_RELOAD("scriptblockplus.command.reload"),
	COMMAND_BACKUP("scriptblockplus.command.backup"),
	COMMAND_CHECKVER("scriptblockplus.command.checkver"),
	COMMAND_DATAMIGR("scriptblockplus.command.datamigr"),
	COMMAND_EXPORT("scriptblockplus.command.export"),
	/*
	COMMAND_INTERACT("scriptblockplus.command.interact"),
	COMMAND_BREAK("scriptblockplus.command.break"),
	COMMAND_WALK("scriptblockplus.command.walk"),
	*/
	COMMAND_SELECTOR("scriptblockplus.command.selector"),
	/*
	INTERACT_USE("scriptblockplus.interact.use"),
	BREAK_USE("scriptblockplus.break.use"),
	WALK_USE("scriptblockplus.walk.use"),
	*/
	TOOL_BLOCK_SELECTOR("scriptblockplus.tool.blockselector"),
	TOOL_SCRIPT_EDITOR("scriptblockplus.tool.scripteditor"),
	TOOL_SCRIPT_VIEWER("scriptblockplus.tool.scriptviewer");

	private static final String SBP_PREFIX = "scriptblockplus.";

	private final String node;

	private Permission(@NotNull String node) {
		this.node = node;
	}

	@NotNull
	public String getNode() {
		return node;
	}

	@Override
	public String toString() {
		return node;
	}

	public boolean has(@NotNull Permissible permissible) {
		return has(permissible, node);
	}

	public static boolean has(@NotNull Permissible permissible, @NotNull String permission) {
		return StringUtils.isNotEmpty(permission) && permissible.hasPermission(permission);
	}

	public static boolean has(@NotNull Permissible permissible, @NotNull ScriptType scriptType, boolean isCMDorUse) {
		return Permission.has(permissible, getTypeNode(scriptType, isCMDorUse));
	}

	@NotNull
	public static String[] getTypeNodes(boolean isCMDorUse) {
		return ScriptType.toArray(t -> getTypeNode(t, isCMDorUse), new String[ScriptType.size()]);
	}

	@NotNull
	public static String getTypeNode(@NotNull ScriptType scriptType, boolean isCMDorUse) {
		return isCMDorUse ? SBP_PREFIX + "command." + scriptType.type() : SBP_PREFIX + scriptType.type() + ".use";
	}
}