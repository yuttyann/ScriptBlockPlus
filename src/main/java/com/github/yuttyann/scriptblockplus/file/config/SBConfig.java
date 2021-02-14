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

    // List Keys
    public static final ConfigKey<List<String>> BLOCK_SELECTOR = stringListKey("BlockSelector", Collections.emptyList());
    public static final ConfigKey<List<String>> SCRIPT_EDITOR = stringListKey("ScriptEditor", Collections.emptyList());
    public static final ConfigKey<List<String>> SCRIPT_VIEWER = stringListKey("ScriptViewer", Collections.emptyList());

    // Integer Keys
    public static final ConfigKey<Integer> FORMAT_LIMIT = integerKey("FormatLimit", 100000);

    // Boolean Keys
    public static final ConfigKey<Boolean> UPDATE_CHECKER = booleanKey("UpdateChecker", true);
    public static final ConfigKey<Boolean> AUTO_DOWNLOAD = booleanKey("AutoDownload", true);
    public static final ConfigKey<Boolean> OPEN_CHANGE_LOG = booleanKey("OpenChangeLog", true);
    public static final ConfigKey<Boolean> CACHE_ALL_JSON = booleanKey("CacheAllJson", false);
    public static final ConfigKey<Boolean> CONSOLE_LOG = booleanKey("ConsoleLog", false);
    public static final ConfigKey<Boolean> SORT_SCRIPTS = booleanKey("SortScripts", true);
    public static final ConfigKey<Boolean> OPTION_PERMISSION = booleanKey("OptionPermission", false);
    public static final ConfigKey<Boolean> ACTIONS_INTERACT_LEFT = booleanKey("Actions.InteractLeft", true);
    public static final ConfigKey<Boolean> ACTIONS_INTERACT_RIGHT = booleanKey("Actions.InteractRight", true);

    // String Keys
    public static final ConfigKey<String> LANGUAGE = stringKey("Language", "en");
    public static final ConfigKey<String> TOOL_COMMAND = stringKey("ToolCommandMessage", "");
    public static final ConfigKey<String> RELOAD_COMMAND = stringKey("ReloadCommandMessage", "");
    public static final ConfigKey<String> BACKUP_COMMAND = stringKey("BackupCommandMessage", "");
    public static final ConfigKey<String> CHECKVER_COMMAND = stringKey("CheckVerCommandMessage", "");
    public static final ConfigKey<String> DATAMIGR_COMMAND = stringKey("DatamigrCommandMessage", "");
    public static final ConfigKey<String> CREATE_COMMAND = stringKey("CreateCommandMessage", "");
    public static final ConfigKey<String> ADD_COMMAND = stringKey("AddCommandMessage", "");
    public static final ConfigKey<String> REMOVE_COMMAND = stringKey("RemoveCommandMessage", "");
    public static final ConfigKey<String> VIEW_COMMAND = stringKey("ViewCommandMessage", "");
    public static final ConfigKey<String> RUN_COMMAND = stringKey("RunCommandMessage", "");
    public static final ConfigKey<String> REDSTONE_COMMAND = stringKey("RedstoneCommandMessage", "");
    public static final ConfigKey<String> SELECTOR_PASTE_COMMAND = stringKey("SelectorPasteCommandMessage", "");
    public static final ConfigKey<String> SELECTOR_REMOVE_COMMAND = stringKey("SelectorRemoveCommandMessage", "");
    public static final ConfigKey<String> NOT_VAULT = stringKey("NotVaultMessage", "");
    public static final ConfigKey<String> SENDER_NO_PLAYER = stringKey("SenderNoPlayerMessage", "");
    public static final ConfigKey<String> NOT_PERMISSION = stringKey("NotPermissionMessage", "");
    public static final ConfigKey<String> GIVE_TOOL = stringKey("GiveToolMessage", "");
    public static final ConfigKey<String> ALL_FILE_RELOAD = stringKey("AllFileReloadMessage", "");
    public static final ConfigKey<String> NOT_LATEST_PLUGIN = stringKey("NotLatestPluginMessage", "");
    public static final ConfigKey<String> NOT_SCRIPT_BLOCK_FILE = stringKey("NotScriptBlockFileMessage", "");
    public static final ConfigKey<String> PLUGIN_BACKUP = stringKey("PluginBackupMessage", "");
    public static final ConfigKey<String> ERROR_PLUGIN_BACKUP = stringKey("ErrorPluginBackupMessage", "");
    public static final ConfigKey<String> DATAMIGR_START = stringKey("DataMigrStartMessage", "");
    public static final ConfigKey<String> DATAMIGR_END = stringKey("DataMigrEndMessage", "");
    public static final ConfigKey<String> UPDATE_DOWNLOAD_START = stringKey("UpdateDownloadStartMessage", "");
    public static final ConfigKey<String> ERROR_UPDATE = stringKey("ErrorUpdateMessage", "");
    public static final ConfigKey<String> NOT_SELECTION = stringKey("NotSelectionMessage", "");
    public static final ConfigKey<String> SCRIPT_VIEWER_START = stringKey("ScriptViewerStartMessage", "");
    public static final ConfigKey<String> SCRIPT_VIEWER_STOP = stringKey("ScriptViewerStopMessage", "");
    public static final ConfigKey<String> ACTIVE_DELAY = stringKey("ActiveDelayMessage", "");
    public static final ConfigKey<String> ERROR_ACTION_DATA = stringKey("ErrorActionDataMessage", "");
    public static final ConfigKey<String> ERROR_SCRIPT_CHECK = stringKey("ErrorScriptCheckMessage", "");
    public static final ConfigKey<String> ERROR_SCRIPT_FILE_CHECK = stringKey("ErrorScriptFileCheckMessage", "");


    // Functions (Private)
    private static Function<ReplaceKey, String> FUNCTION_UPDATE_CHECK = r -> {
        var s = r.getValue();
        s = replace(s, "%name%", r.getArgment(0, String.class));
        s = replace(s, "%version%", r.getArgment(1, String.class));
        if (s.contains("%details%")) {
            @SuppressWarnings("unchecked")
            var l = (List<String>) r.getArgment(2, List.class);
            var b = new StringBuilder(l.size());
            for (int i = 0; i < l.size(); i++) {
                var info = removeStart(l.get(i), "$");
                var tree = l.get(i).startsWith("$");
                b.append(tree ? "  - " : "・").append(info).append(i == (l.size() - 1) ? "" : "|~");
            }
            s = replace(s, "%details%", b.toString());
        }
        return s;
    };

    private static Function<ReplaceKey, String> FUNCTION_SCRIPT_TYPE = r -> {
        return replace(r.getValue(), "%scriptkey%", r.getArgment(0, ScriptKey.class).getName());
    };

    private static Function<ReplaceKey, String> FUNCTION_OPTION_FAILED = r -> {
        var t = r.getArgment(1, Throwable.class);
        var s = r.getValue();
        s = replace(s, "%option%", r.getArgment(0, Option.class).getName());
        s = replace(s, "%cause%", t.getClass().getSimpleName() + (t.getMessage() == null ? "" : " \"" + t.getMessage() + "\""));
        return s;
    };

    private static Function<ReplaceKey, String> FUNCTION_ITEM = r -> {
        var m = r.getArgment(0, Material.class);
        var n = r.getArgment(3, String.class);
        var s = r.getValue();
        s = replace(s, "%material%", String.valueOf(m));
        s = replace(s, "%amount%", r.getArgment(1, Integer.class));
        s = replace(s, "%damage%", r.getArgment(2, Integer.class));
        s = replace(s, "%name%", isEmpty(n) ? String.valueOf(m) : n);
        return s;
    };

    private static Function<ReplaceKey, String> FUNCTION_CONSOLE_SCRIPT = r -> {
        var s = r.getValue();
        s = replace(s, "%scriptkey%", r.getArgment(0, ScriptKey.class).getName());
        s = replace(s, "%world%", r.getArgment(1, BlockCoords.class).getWorld().getName());
        s = replace(s, "%coords%", r.getArgment(1, BlockCoords.class).getCoords());
        return s;
    };

    private static Function<ReplaceKey, String> FUNCTION_CONSOLE_SELECTOR = r -> {
        var c = r.getArgment(1, CuboidRegionIterator.class);
        var s = r.getValue();
        s = replace(s, "%scriptkey%", r.getArgment(0, String.class));
        s = replace(s, "%blockcount%", c.getVolume());
        s = replace(s, "%world%", c.getWorld().getName());
        s = replace(s, "%mincoords%", c.getMinimumPoint().getCoords());
        s = replace(s, "%maxcoords%", c.getMinimumPoint().getCoords());
        return s;
    };


    // Replace Keys
    /**
     * Parameter: {@link String} name, {@link String} path, {@link String} size
     */
    public static final ReplaceKey UPDATE_DOWNLOAD_END = replaceKey("UpdateDownloadEndMessage", "", "%name%", "%path%", "%size%");

    /**
     * Parameter: {@link String} name, {@link String} version, {@link List}&lt;{@link String}&gt; details
     */
    public static final ReplaceKey UPDATE_CHECK = replaceKey("UpdateCheckMessage", "", FUNCTION_UPDATE_CHECK);

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
    public static final ReplaceKey SCRIPT_REDSTONE_ENABLE = replaceKey("ScriptRedstoneEnableMessage", "", FUNCTION_SCRIPT_TYPE);

    /**
     * Parameter: {@link ScriptKey} scriptKey
     */
    public static final ReplaceKey SCRIPT_REDSTONE_DISABLE = replaceKey("ScriptRedstoneDisableMessage", "", FUNCTION_SCRIPT_TYPE);

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