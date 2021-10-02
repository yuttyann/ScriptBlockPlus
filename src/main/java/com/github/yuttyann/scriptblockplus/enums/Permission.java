/**
 * ScriptBlockPlus - Allow you to add script to any blocks.
 * Copyright (C) 2021 yuttyann44581
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package com.github.yuttyann.scriptblockplus.enums;

import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
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
    TOOL_SCRIPT_EDITOR("scriptblockplus.tool.scripteditor"),
    TOOL_SCRIPT_VIEWER("scriptblockplus.tool.scriptviewer"),
    TOOL_SCRIPT_MANAGER("scriptblockplus.tool.scriptmanager"),
    TOOL_BLOCK_SELECTOR("scriptblockplus.tool.blockselector"),

    // Minecraft Permissions
    MINECRAFT_COMMAND_SAY("minecraft.command.say"),
    MINECRAFT_COMMAND_TITLE("minecraft.command.title");

    private static final String SBP_PREFIX = "scriptblockplus.";

    private final String node;

    Permission(@NotNull String node) {
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

    public static boolean has(@NotNull Permissible permissible, @NotNull String node) {
        return StringUtils.isNotEmpty(node) && permissible.hasPermission(node);
    }

    public static boolean has(@NotNull Permissible permissible, @NotNull String... nodes) {
        return StreamUtils.anyMatch(nodes, s -> has(permissible, s));
    }

    public static boolean has(@NotNull Permissible permissible, @NotNull ScriptKey scriptKey, boolean isCMDorUse) {
        return Permission.has(permissible, getTypeNode(scriptKey, isCMDorUse));
    }

    @NotNull
    public static String[] getTypeNodes(boolean isCMDorUse) {
        return StreamUtils.toArray(ScriptKey.values(), t -> getTypeNode(t, isCMDorUse), String[]::new);
    }

    @NotNull
    public static String getTypeNode(@NotNull ScriptKey scriptKey, boolean isCMDorUse) {
        return isCMDorUse ? SBP_PREFIX + "command." + scriptKey.getName() : SBP_PREFIX + scriptKey.getName() + ".use";
    }
}