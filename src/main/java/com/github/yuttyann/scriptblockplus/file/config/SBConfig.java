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
package com.github.yuttyann.scriptblockplus.file.config;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.region.CuboidRegionIterator;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static com.github.yuttyann.scriptblockplus.file.config.ConfigKeys.*;
import static com.github.yuttyann.scriptblockplus.utils.StringUtils.*;

/**
 * ScriptBlockPlus SBConfig クラス
 * @author yuttyann44581
 */
public final class SBConfig {

    /* List Keys */
    public static final ConfigKey<List<String>> SCRIPT_EDITOR = stringListKey("ScriptEditor", Collections.emptyList());
    public static final ConfigKey<List<String>> SCRIPT_VIEWER = stringListKey("ScriptViewer", Collections.emptyList());
    public static final ConfigKey<List<String>> SCRIPT_MANAGER = stringListKey("ScriptManager", Collections.emptyList());
    public static final ConfigKey<List<String>> BLOCK_SELECTOR = stringListKey("BlockSelector", Collections.emptyList());


    /* Integer Keys */
    public static final ConfigKey<Integer> FORMAT_LIMIT = integerKey("FormatLimit", 100000);


    /* Boolean Keys */
    public static final ConfigKey<Boolean> CACHE_ALL_JSON = booleanKey("CacheAllJson", false);
    public static final ConfigKey<Boolean> CONSOLE_LOG = booleanKey("ConsoleLog", false);
    public static final ConfigKey<Boolean> SORT_SCRIPTS = booleanKey("SortScripts", true);
    public static final ConfigKey<Boolean> OPTION_HELP = booleanKey("OptionHelp", true);
    public static final ConfigKey<Boolean> OPTION_PERMISSION = booleanKey("OptionPermission", false);
    public static final ConfigKey<Boolean> ACTIONS_INTERACT_LEFT = booleanKey("Actions.InteractLeft", true);
    public static final ConfigKey<Boolean> ACTIONS_INTERACT_RIGHT = booleanKey("Actions.InteractRight", true);


    /* String Keys */
    // CustomGUI System
    public static final ConfigKey<String> GUI_SYS_INPUT = stringKey("CustomGUI.System.Input", "");
    public static final ConfigKey<String> GUI_SYS_RESET = stringKey("CustomGUI.System.Reset", "");
    public static final ConfigKey<String> GUI_SYS_INPLAYER = stringKey("CustomGUI.System.InPlayer", "");
    public static final ConfigKey<String> GUI_SYS_OVERFLOW = stringKey("CustomGUI.System.Overflow", "");
    public static final ConfigKey<String> GUI_SYS_SEARCHGUI = stringKey("CustomGUI.System.SearchGUI", "");
    public static final ConfigKey<String> GUI_SYS_SETTINGGUI = stringKey("CustomGUI.System.SettingGUI", "");
    public static final ConfigKey<String> GUI_SYS_TOOLBOXGUI = stringKey("CustomGUI.System.ToolBoxGUI", "");
    
    // CustomGUI Item SearchGUI
    public static final ConfigKey<String> GUI_SEARCH_NEXT = stringKey("CustomGUI.Item.SearchGUI.Next", "");
    public static final ConfigKey<String> GUI_SEARCH_PREV = stringKey("CustomGUI.Item.SearchGUI.Prev", "");
    public static final ConfigKey<String> GUI_SEARCH_RESET = stringKey("CustomGUI.Item.SearchGUI.Reset", "");
    public static final ConfigKey<String> GUI_SEARCH_SETTING = stringKey("CustomGUI.Item.SearchGUI.Setting", "");
    public static final ConfigKey<String> GUI_SEARCH_SCRIPTKEY = stringKey("CustomGUI.Item.SearchGUI.Scriptkey", "");
    public static final ConfigKey<String> GUI_SEARCH_SCRIPT = stringKey("CustomGUI.Item.SearchGUI.Script", "");
    public static final ConfigKey<String> GUI_SEARCH_TIME = stringKey("CustomGUI.Item.SearchGUI.Time", "");
    public static final ConfigKey<String> GUI_SEARCH_COORDS = stringKey("CustomGUI.Item.SearchGUI.Coords", "");
    public static final ConfigKey<String> GUI_SEARCH_NAMETAG = stringKey("CustomGUI.Item.SearchGUI.NameTag", "");

    // CustomGUI Item SettingGUI
    public static final ConfigKey<String> GUI_SETTING_DELETE = stringKey("CustomGUI.Item.SettingGUI.Delete", "");
    public static final ConfigKey<String> GUI_SETTING_CLOSE = stringKey("CustomGUI.Item.SettingGUI.Close", "");
    public static final ConfigKey<String> GUI_SETTING_COPY = stringKey("CustomGUI.Item.SettingGUI.Copy", "");
    public static final ConfigKey<String> GUI_SETTING_PASTE = stringKey("CustomGUI.Item.SettingGUI.Paste", "");
    public static final ConfigKey<String> GUI_SETTING_EXECUTE = stringKey("CustomGUI.Item.SettingGUI.Execute", "");
    public static final ConfigKey<String> GUI_SETTING_TELEPORT = stringKey("CustomGUI.Item.SettingGUI.Teleport", "");
    public static final ConfigKey<String> GUI_SETTING_INFO = stringKey("CustomGUI.Item.SettingGUI.Info", "");
    public static final ConfigKey<String> GUI_SETTING_REDSTONE = stringKey("CustomGUI.Item.SettingGUI.Redstone", "");
    public static final ConfigKey<String> GUI_SETTING_SCRIPT = stringKey("CustomGUI.Item.SettingGUI.Script", "");
    public static final ConfigKey<String> GUI_SETTING_NAMETAG = stringKey("CustomGUI.Item.SettingGUI.NameTag", "");

    // Message
    public static final ConfigKey<String> TOOL_COMMAND = stringKey("ToolCommandMessage", "");
    public static final ConfigKey<String> RELOAD_COMMAND = stringKey("ReloadCommandMessage", "");
    public static final ConfigKey<String> BACKUP_COMMAND = stringKey("BackupCommandMessage", "");
    public static final ConfigKey<String> DATAMIGR_COMMAND = stringKey("DatamigrCommandMessage", "");
    public static final ConfigKey<String> CREATE_COMMAND = stringKey("CreateCommandMessage", "");
    public static final ConfigKey<String> ADD_COMMAND = stringKey("AddCommandMessage", "");
    public static final ConfigKey<String> REMOVE_COMMAND = stringKey("RemoveCommandMessage", "");
    public static final ConfigKey<String> VIEW_COMMAND = stringKey("ViewCommandMessage", "");
    public static final ConfigKey<String> NAMETAG_COMMAND = stringKey("NameTagCommandMessage", "");
    public static final ConfigKey<String> REDSTONE_COMMAND = stringKey("RedstoneCommandMessage", "");
    public static final ConfigKey<String> RUN_COMMAND = stringKey("RunCommandMessage", "");
    public static final ConfigKey<String> SELECTOR_PASTE_COMMAND = stringKey("SelectorPasteCommandMessage", "");
    public static final ConfigKey<String> SELECTOR_REMOVE_COMMAND = stringKey("SelectorRemoveCommandMessage", "");
    public static final ConfigKey<String> SENDER_NO_PLAYER = stringKey("SenderNoPlayerMessage", "");
    public static final ConfigKey<String> NOT_PERMISSION = stringKey("NotPermissionMessage", "");
    public static final ConfigKey<String> GIVE_TOOL = stringKey("GiveToolMessage", "");
    public static final ConfigKey<String> ALL_FILE_RELOAD = stringKey("AllFileReloadMessage", "");
    public static final ConfigKey<String> NOT_SCRIPT_BLOCK_FILE = stringKey("NotScriptBlockFileMessage", "");
    public static final ConfigKey<String> PLUGIN_BACKUP = stringKey("PluginBackupMessage", "");
    public static final ConfigKey<String> ERROR_PLUGIN_BACKUP = stringKey("ErrorPluginBackupMessage", "");
    public static final ConfigKey<String> DATAMIGR_START = stringKey("DataMigrStartMessage", "");
    public static final ConfigKey<String> DATAMIGR_END = stringKey("DataMigrEndMessage", "");
    public static final ConfigKey<String> NOT_SELECTION = stringKey("NotSelectionMessage", "");
    public static final ConfigKey<String> SCRIPT_VIEWER_START = stringKey("ScriptViewerStartMessage", "");
    public static final ConfigKey<String> SCRIPT_VIEWER_STOP = stringKey("ScriptViewerStopMessage", "");
    public static final ConfigKey<String> ACTIVE_DELAY = stringKey("ActiveDelayMessage", "");
    public static final ConfigKey<String> ERROR_ACTION_DATA = stringKey("ErrorActionDataMessage", "");
    public static final ConfigKey<String> ERROR_SCRIPT_CHECK = stringKey("ErrorScriptCheckMessage", "");
    public static final ConfigKey<String> ERROR_SCRIPT_FILE_CHECK = stringKey("ErrorScriptFileCheckMessage", "");


    /* Functions (Private) */
    private static Function<ReplaceKey, String> FUNCTION_SCRIPT_TYPE = r -> {
        return replace(r.getValue(), "%scriptkey%", r.getArgument(0, ScriptKey.class).getName());
    };

    private static Function<ReplaceKey, String> FUNCTION_OPTION_FAILED = r -> {
        var throwable = r.getArgument(1, Throwable.class);
        var value = r.getValue();
        value = replace(value, "%option%", r.getArgument(0, Option.class).getName());
        value = replace(value, "%cause%", throwable.getClass().getSimpleName() + (throwable.getMessage() == null ? "" : " \"" + throwable.getMessage() + "\""));
        return value;
    };

    private static Function<ReplaceKey, String> FUNCTION_ITEM = r -> {
        var material = r.getArgument(0, Material.class);
        var damage = r.getArgument(2, Integer.class);
        var amount = r.getArgument(1, Integer.class);
        var name = r.getArgument(3, String.class);
        var lore = r.getArgument(4, String.class);
        var value = r.getValue();
        value = replace(value, "%material%", material);
        value = replace(value, "%amount%", amount);
        value = replace(value, "%damage%", damage == -1 ? "ALL" : damage);
        value = replace(value, "%name%", name);
        value = replace(value, "%lore%", lore);
        return value;
    };

    private static Function<ReplaceKey, String> FUNCTION_CONSOLE_SCRIPT = r -> {
        var value = r.getValue();
        value = replace(value, "%scriptkey%", r.getArgument(0, ScriptKey.class).getName());
        value = replace(value, "%world%", r.getArgument(1, BlockCoords.class).getWorld().getName());
        value = replace(value, "%coords%", r.getArgument(1, BlockCoords.class).getCoords());
        return value;
    };

    private static Function<ReplaceKey, String> FUNCTION_CONSOLE_SELECTOR = r -> {
        var iterator = r.getArgument(1, CuboidRegionIterator.class);
        var value = r.getValue();
        value = replace(value, "%scriptkey%", r.getArgument(0, String.class));
        value = replace(value, "%blockcount%", iterator.getVolume());
        value = replace(value, "%world%", iterator.getWorld().getName());
        value = replace(value, "%mincoords%", iterator.getMinimumPoint().getCoords());
        value = replace(value, "%maxcoords%", iterator.getMinimumPoint().getCoords());
        return value;
    };


    /* Replace Keys */

    /**
     * Parameter: {@link ScriptKey} scriptKey
     */
    public static final ReplaceKey SCRIPT_COPY = replaceKey("ScriptCopyMessage", "", FUNCTION_SCRIPT_TYPE);

    /**
     * Parameter: {@link ScriptKey} scriptKey
     */
    public static final ReplaceKey SCRIPT_PASTE = replaceKey("ScriptPasteMessage", "", FUNCTION_SCRIPT_TYPE);

    /**
     * Parameter: {@link ScriptKey} scriptKey
     */
    public static final ReplaceKey SCRIPT_CREATE = replaceKey("ScriptCreateMessage", "", FUNCTION_SCRIPT_TYPE);

    /**
     * Parameter: {@link ScriptKey} scriptKey
     */
    public static final ReplaceKey SCRIPT_ADD = replaceKey("ScriptAddMessage", "", FUNCTION_SCRIPT_TYPE);

    /**
     * Parameter: {@link ScriptKey} scriptKey
     */
    public static final ReplaceKey SCRIPT_REMOVE = replaceKey("ScriptRemoveMessage", "", FUNCTION_SCRIPT_TYPE);

    /**
     * Parameter: {@link ScriptKey} scriptKey
     */
    public static final ReplaceKey SCRIPT_NAMETAG = replaceKey("ScriptNameTagMessage", "", FUNCTION_SCRIPT_TYPE);

    /**
     * Parameter: {@link ScriptKey} scriptKey
     */
    public static final ReplaceKey SCRIPT_REDSTONE = replaceKey("ScriptRedstoneMessage", "", FUNCTION_SCRIPT_TYPE);

    /**
     * Parameter: {@link String} worldName, {@link String} coords
     */
    public static final ReplaceKey SELECTOR_POS1 = replaceKey("SelectorPos1Message", "", "%world%", "%coords%");

    /**
     * Parameter: {@link String} worldName, {@link String} coords
     */
    public static final ReplaceKey SELECTOR_POS2 = replaceKey("SelectorPos2Message", "", "%world%", "%coords%");

    /**
     * Parameter: {@link String} scriptKeyName, {@link Integer} blockCount
     */
    public static final ReplaceKey SELECTOR_PASTE = replaceKey("SelectorPasteMessage", "", "%scriptkey%", "%blockcount%");

    /**
     * Parameter: {@link String} scriptKeyName, {@link Integer} blockCount
     */
    public static final ReplaceKey SELECTOR_REMOVE = replaceKey("SelectorRemoveMessage", "", "%scriptkey%", "%blockcount%");

    /**
     * Parameter: {@link Option} option, {@link Throwable} throwable
     */
    public static final ReplaceKey OPTION_FAILED_TO_EXECUTE = replaceKey("OptionFailedToExecuteMessage", "", FUNCTION_OPTION_FAILED);

    /**
     * Parameter: {@link Integer} hour, {@link Integer} minute, {@link Integer} second
     */
    public static final ReplaceKey ACTIVE_COOLDOWN = replaceKey("ActiveCooldownMessage", "", "%hour%", "%minute%", "%second%");

    /**
     * Parameter: {@link String} scriptKey-actionKey
     */
    public static final ReplaceKey SUCCESS_ACTION_DATA = replaceKey("SuccActionDataMessage", "", "%action%");

    /**
     * Parameter: {@link ScriptKey} scriptKey
     */
    public static final ReplaceKey ERROR_SCRIPT_EXECUTE = replaceKey("ErrorScriptExecMessage", "", FUNCTION_SCRIPT_TYPE);

    /**
     * Parameter: {@link String} group
     */
    public static final ReplaceKey ERROR_GROUP = replaceKey("ErrorGroupMessage", "", "%group%");

    /**
     * Parameter: {@link Material} material, {@link Integer} amount, {@link Integer} damage, {@link String} name
     */
    public static final ReplaceKey ERROR_HAND = replaceKey("ErrorHandMessage", "", FUNCTION_ITEM);

    /**
     * Parameter: {@link Material} material, {@link Integer} amount, {@link Integer} damage, {@link String} name
     */
    public static final ReplaceKey ERROR_ITEM = replaceKey("ErrorItemMessage", "", FUNCTION_ITEM);

    /**
     * Parameter: {@link Double} cost, {@link Double} result
     */
    public static final ReplaceKey ERROR_COST = replaceKey("ErrorCostMessage", "", "%cost%", "%result%");

    /**
     * Parameter: {@link ScriptKey} scriptKey, {@link Location} location
     */
    public static final ReplaceKey CONSOLE_SCRIPT_EDIT = replaceKey("ConsoleScriptEditMessage", "", FUNCTION_CONSOLE_SCRIPT);

    /**
     * Parameter: {@link ScriptKey} scriptKey, {@link Location} location
     */
    public static final ReplaceKey CONSOLE_SCRIPT_VIEW = replaceKey("ConsoleScriptViewMessage", "", FUNCTION_CONSOLE_SCRIPT);

    /**
     * Parameter: {@link ScriptKey} scriptKey, {@link Location} location
     */
    public static final ReplaceKey CONSOLE_SUCCESS_SCRIPT_EXECUTE = replaceKey("ConsoleSuccScriptExecMessage", "", FUNCTION_CONSOLE_SCRIPT);

    /**
     * Parameter: {@link ScriptKey} scriptKey, {@link Location} location
     */
    public static final ReplaceKey CONSOLE_ERROR_SCRIPT_EXECUTE = replaceKey("ConsoleErrorScriptExecMessage", "", FUNCTION_CONSOLE_SCRIPT);

    /**
     * Parameter: {@link String} scriptKeyName, {@link CuboidRegionIterator} iterator
     */
    public static final ReplaceKey CONSOLE_SELECTOR_PASTE = replaceKey("ConsoleSelectorPasteMessage", "", FUNCTION_CONSOLE_SELECTOR);

    /**
     * Parameter: {@link String} scriptKeyNames, {@link CuboidRegionIterator} iterator
     */
    public static final ReplaceKey CONSOLE_SELECTOR_REMOVE = replaceKey("ConsoleSelectorRemoveMessage", "", FUNCTION_CONSOLE_SELECTOR);
}