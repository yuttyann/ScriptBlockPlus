package com.github.yuttyann.scriptblockplus.file.config;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.region.CuboidRegionBlocks;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import org.apache.commons.lang.text.StrBuilder;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static com.github.yuttyann.scriptblockplus.file.config.ConfigKeys.*;
import static com.github.yuttyann.scriptblockplus.utils.StringUtils.*;

/**
 * ScriptBlockPlus SBConfig クラス
 * @author yuttyann44581
 */
public final class SBConfig {

    // List Keys
    public static final ConfigKey<List<String>> BLOCK_SELECTOR = stringListKey("BlockSelector", new ArrayList<>());
    public static final ConfigKey<List<String>> SCRIPT_EDITOR = stringListKey("ScriptEditor", new ArrayList<>());
    public static final ConfigKey<List<String>> SCRIPT_VIEWER = stringListKey("ScriptViewer", new ArrayList<>());


    // Boolean Keys
    public static final ConfigKey<Boolean> UPDATE_CHECKER = booleanKey("UpdateChecker", true);
    public static final ConfigKey<Boolean> AUTO_DOWNLOAD = booleanKey("AutoDownload", true);
    public static final ConfigKey<Boolean> OPEN_CHANGE_LOG = booleanKey("OpenChangeLog", true);
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
    public static final ConfigKey<String> EXPORT_COMMAND = stringKey("ExportCommandMessage", "");
    public static final ConfigKey<String> CREATE_COMMAND = stringKey("CreateCommandMessage", "");
    public static final ConfigKey<String> ADD_COMMAND = stringKey("AddCommandMessage", "");
    public static final ConfigKey<String> REMOVE_COMMAND = stringKey("RemoveCommandMessage", "");
    public static final ConfigKey<String> VIEW_COMMAND = stringKey("ViewCommandMessage", "");
    public static final ConfigKey<String> RUN_COMMAND = stringKey("RunCommandMessage", "");
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
        s = replace(s, "%pluginname%", r.getArg(0, String.class));
        s = replace(s, "%latestversion%", r.getArg(1, String.class));
        if (s.contains("%details%")) {
            @SuppressWarnings("unchecked")
            List<String> l = r.getArg(2, List.class);
            StrBuilder b = new StrBuilder(l.size());
            for (int i = 0; i < l.size(); i++) {
                boolean isTree = l.get(i).startsWith("$");
                String info = removeStart(l.get(i), "$");
                b.append(isTree ? "  - " : "・").append(info).append(i == (l.size() - 1) ? "" : "|~");
            }
            s = replace(s, "%details%", b.toString());
        }
        return s;
    };

    private static Function<ReplaceKey, String> FUNCTION_SCRIPT_TYPE = r -> {
        return replace(r.getValue(), "%scripttype%", r.getArg(0, ScriptType.class).type());
    };

    private static Function<ReplaceKey, String> FUNCTION_OPTION_FAILED = r -> {
        var t = r.getArg(1, Throwable.class);
        var s = r.getValue();
        s = replace(s, "%option%", r.getArg(0, Option.class).getName());
        s = replace(s, "%cause%", t.getClass().getSimpleName() + (t.getMessage() == null ? "" : " \"" + t.getMessage() + "\""));
        return s;
    };

    private static Function<ReplaceKey, String> FUNCTION_ITEM = r -> {
        var m = r.getArg(0, Material.class);
        var n = r.getArg(3, String.class);
        var s = r.getValue();
        s = replace(s, "%material%", String.valueOf(m));
        s = replace(s, "%amount%", r.getArg(1, Integer.class));
        s = replace(s, "%damage%", r.getArg(2, Integer.class));
        s = replace(s, "%itemname%", isEmpty(n) ? String.valueOf(m) : n);
        return s;
    };

    private static Function<ReplaceKey, String> FUNCTION_CONSOLE_SCRIPT = r -> {
        var l = r.getArg(1, Location.class);
        var s = r.getValue();
        s = replace(s, "%player%", r.getArg(0, String.class));
        s = replace(s, "%scripttype%", r.getArg(2, ScriptType.class).type());
        s = replace(s, "%world%", Objects.requireNonNull(l.getWorld()).getName());
        s = replace(s, "%coords%", BlockCoords.getCoords(l));
        return s;
    };

    private static Function<ReplaceKey, String> FUNCTION_CONSOLE_SELECTOR = r -> {
        var c = r.getArg(1, CuboidRegionBlocks.class);
        var s = r.getValue();
        s = replace(s, "%scripttype%", r.getArg(0, String.class));
        s = replace(s, "%blockcount%", c.getCount());
        s = replace(s, "%world%", c.getWorld().getName());
        s = replace(s, "%mincoords%", BlockCoords.getCoords(c.getMinimumPoint()));
        s = replace(s, "%maxcoords%", BlockCoords.getCoords(c.getMinimumPoint()));
        return s;
    };


    // Replace Keys
    /**
     * Parameter: {@link String} name
     */
    public static final ReplaceKey EXPORT_START = replaceKey(stringKey("ExportStartMessage", ""), "%name%");

    /**
     * Parameter: {@link String} name
     */
    public static final ReplaceKey EXPORT_END = replaceKey(stringKey("ExportEndMessage", ""), "%name%");

    /**
     * Parameter: {@link String} fileName, {@link String} filePath, {@link String} fileSize
     */
    public static final ReplaceKey UPDATE_DOWNLOAD_END = replaceKey(stringKey("UpdateDownloadEndMessage", ""), "%filename%", "%filepath%", "%filesize%");

    /**
     * Parameter: {@link String} pluginName, {@link String} latestVersion, {@link List}&lt;{@link String}&gt; details
     */
    public static final ReplaceKey UPDATE_CHECK = replaceKey(stringKey("UpdateCheckMessage", ""), FUNCTION_UPDATE_CHECK);

    /**
     * Parameter: {@link ScriptType} scriptType
     */
    public static final ReplaceKey SCRIPT_COPY = replaceKey(stringKey("ScriptCopyMessage", ""), FUNCTION_SCRIPT_TYPE);

    /**
     * Parameter: {@link ScriptType} scriptType
     */
    public static final ReplaceKey SCRIPT_PASTE = replaceKey(stringKey("ScriptPasteMessage", ""), FUNCTION_SCRIPT_TYPE);

    /**
     * Parameter: {@link ScriptType} scriptType
     */
    public static final ReplaceKey SCRIPT_CREATE = replaceKey(stringKey("ScriptCreateMessage", ""), FUNCTION_SCRIPT_TYPE);

    /**
     * Parameter: {@link ScriptType} scriptType
     */
    public static final ReplaceKey SCRIPT_ADD = replaceKey(stringKey("ScriptAddMessage", ""), FUNCTION_SCRIPT_TYPE);

    /**
     * Parameter: {@link ScriptType} scriptType
     */
    public static final ReplaceKey SCRIPT_REMOVE = replaceKey(stringKey("ScriptRemoveMessage", ""), FUNCTION_SCRIPT_TYPE);

    /**
     * Parameter: {@link String} world, {@link String} coords
     */
    public static final ReplaceKey SELECTOR_POS1 = replaceKey(stringKey("SelectorPos1Message", ""), "%world%", "%coords%");

    /**
     * Parameter: {@link String} world, {@link String} coords
     */
    public static final ReplaceKey SELECTOR_POS2 = replaceKey(stringKey("SelectorPos2Message", ""), "%world%", "%coords%");

    /**
     * Parameter: {@link String} scriptType, {@link Integer} blockCount
     */
    public static final ReplaceKey SELECTOR_PASTE = replaceKey(stringKey("SelectorPasteMessage", ""), "%scripttype%", "%blockcount%");

    /**
     * Parameter: {@link String} scriptType, {@link Integer} blockCount
     */
    public static final ReplaceKey SELECTOR_REMOVE = replaceKey(stringKey("SelectorRemoveMessage", ""), "%scripttype%", "%blockcount%");

    /**
     * Parameter: {@link Option} option, {@link Throwable} throwable
     */
    public static final ReplaceKey OPTION_FAILED_TO_EXECUTE = replaceKey(stringKey("OptionFailedToExecuteMessage", ""), FUNCTION_OPTION_FAILED);

    /**
     * Parameter: {@link Integer} hour, {@link Integer} minute, {@link Integer} second
     */
    public static final ReplaceKey ACTIVE_COOLDOWN = replaceKey(stringKey("ActiveCooldownMessage", ""), "%hour%", "%minute%", "%second%");

    /**
     * Parameter: {@link String} scriptType-actionType
     */
    public static final ReplaceKey SUCCESS_ACTION_DATA = replaceKey(stringKey("SuccActionDataMessage", ""), "%actiontype%");

    /**
     * Parameter: {@link ScriptType} scriptType
     */
    public static final ReplaceKey ERROR_SCRIPT_EXECUTE = replaceKey(stringKey("ErrorScriptExecMessage", ""), FUNCTION_SCRIPT_TYPE);

    /**
     * Parameter: {@link String} group
     */
    public static final ReplaceKey ERROR_GROUP = replaceKey(stringKey("ErrorGroupMessage", ""), "%group%");

    /**
     * Parameter: {@link Material} type, {@link Integer} amount, {@link Integer} damage, {@link String} itemName
     */
    public static final ReplaceKey ERROR_HAND = replaceKey(stringKey("ErrorHandMessage", ""), FUNCTION_ITEM);

    /**
     * Parameter: {@link Material} type, {@link Integer} amount, {@link Integer} damage, {@link String} itemName
     */
    public static final ReplaceKey ERROR_ITEM = replaceKey(stringKey("ErrorItemMessage", ""), FUNCTION_ITEM);

    /**
     * Parameter: {@link Double} cost, {@link Double} result
     */
    public static final ReplaceKey ERROR_COST = replaceKey(stringKey("ErrorCostMessage", ""), "%cost%", "%result%");

    /**
     * Parameter: {@link String} playerName, {@link Location} location, {@link ScriptType} scriptType
     */
    public static final ReplaceKey CONSOLE_SCRIPT_COPY = replaceKey(stringKey("ConsoleScriptCopyMessage", ""), FUNCTION_CONSOLE_SCRIPT);

    /**
     * Parameter: {@link String} playerName, {@link Location} location, {@link ScriptType} scriptType
     */
    public static final ReplaceKey CONSOLE_SCRIPT_PASTE = replaceKey(stringKey("ConsoleScriptPasteMessage", ""), FUNCTION_CONSOLE_SCRIPT);

    /**
     * Parameter: {@link String} playerName, {@link Location} location, {@link ScriptType} scriptType
     */
    public static final ReplaceKey CONSOLE_SCRIPT_CREATE = replaceKey(stringKey("ConsoleScriptCreateMessage", ""), FUNCTION_CONSOLE_SCRIPT);

    /**
     * Parameter: {@link String} playerName, {@link Location} location, {@link ScriptType} scriptType
     */
    public static final ReplaceKey CONSOLE_SCRIPT_ADD = replaceKey(stringKey("ConsoleScriptAddMessage", ""), FUNCTION_CONSOLE_SCRIPT);

    /**
     * Parameter: {@link String} playerName, {@link Location} location, {@link ScriptType} scriptType
     */
    public static final ReplaceKey CONSOLE_SCRIPT_REMOVE = replaceKey(stringKey("ConsoleScriptRemoveMessage", ""), FUNCTION_CONSOLE_SCRIPT);

    /**
     * Parameter: {@link String} playerName, {@link Location} location, {@link ScriptType} scriptType
     */
    public static final ReplaceKey CONSOLE_SCRIPT_VIEW = replaceKey(stringKey("ConsoleScriptViewMessage", ""), FUNCTION_CONSOLE_SCRIPT);

    /**
     * Parameter: {@link ScriptType} scriptType, {@link CuboidRegionBlocks} regionBlocks
     */
    public static final ReplaceKey CONSOLE_SELECTOR_PASTE = replaceKey(stringKey("ConsoleSelectorPasteMessage", ""), FUNCTION_CONSOLE_SELECTOR);

    /**
     * Parameter: {@link ScriptType} scriptType, {@link CuboidRegionBlocks} regionBlocks
     */
    public static final ReplaceKey CONSOLE_SELECTOR_REMOVE = replaceKey(stringKey("ConsoleSelectorRemoveMessage", ""), FUNCTION_CONSOLE_SELECTOR);

    /**
     * Parameter: {@link String} playerName, {@link Location} location, {@link ScriptType} scriptType
     */
    public static final ReplaceKey CONSOLE_SUCCESS_SCRIPT_EXECUTE = replaceKey(stringKey("ConsoleSuccScriptExecMessage", ""), FUNCTION_CONSOLE_SCRIPT);

    /**
     * Parameter: {@link String} playerName, {@link Location} location, {@link ScriptType} scriptType
     */
    public static final ReplaceKey CONSOLE_ERROR_SCRIPT_EXECUTE = replaceKey(stringKey("ConsoleErrorScriptExecMessage", ""), FUNCTION_CONSOLE_SCRIPT);
}