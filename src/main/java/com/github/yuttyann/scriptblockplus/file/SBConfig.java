package com.github.yuttyann.scriptblockplus.file;

import com.github.yuttyann.scriptblockplus.file.config.ConfigKey;
import com.github.yuttyann.scriptblockplus.file.config.CustomKey;

import java.util.ArrayList;
import java.util.List;

import static com.github.yuttyann.scriptblockplus.file.config.ConfigKeys.*;

public final class SBConfig {

	// Boolean Values
	public static final ConfigKey<Boolean> SBP_API_VERSION = booleanKey("SBP-API-Version", true);
	public static final ConfigKey<Boolean> UPDATE_CHECKER = booleanKey("UpdateChecker", true);
	public static final ConfigKey<Boolean> AUTO_DOWNLOAD = booleanKey("AutoDownload", true);
	public static final ConfigKey<Boolean> OPEN_CHANGE_LOG = booleanKey("OpenChangeLog", true);
	public static final ConfigKey<Boolean> CONSOLE_LOG = booleanKey("ConsoleLog", false);
	public static final ConfigKey<Boolean> SORT_SCRIPTS = booleanKey("SortScripts", true);
	public static final ConfigKey<Boolean> OPTION_PERMISSION = booleanKey("OptionPermission", false);
	public static final ConfigKey<Boolean> COMMAND_SELECTOR = booleanKey("CommandSelector", false);
	public static final ConfigKey<Boolean> ACTIONS_INTERACT_LEFT = booleanKey("Actions.InteractLeft", true);
	public static final ConfigKey<Boolean> ACTIONS_INTERACT_RIGHT = booleanKey("Actions.InteractRight", true);


	// String Values
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
	public static final ConfigKey<String> SCRIPTS_BACKUP = stringKey("ScriptsBackupMessage", "");
	public static final ConfigKey<String> ERROR_SCRIPTS_BACKUP = stringKey("ErrorScriptsBackupMessage", "");
	public static final ConfigKey<String> DATAMIGR_START = stringKey("DataMigrStartMessage", "");
	public static final ConfigKey<String> DATAMIGR_END = stringKey("DataMigrEndMessage", "");
	public static final ConfigKey<String> UPDATE_DOWNLOAD_START = stringKey("UpdateDownloadStartMessage", "");
	public static final ConfigKey<String> ERROR_UPDATE = stringKey("ErrorUpdateMessage", "");
	public static final ConfigKey<String> NOT_SELECTION = stringKey("NotSelectionMessage", "");
	public static final ConfigKey<String> ACTIVE_DELAY = stringKey("ActiveDelayMessage", "");
	public static final ConfigKey<String> ERROR_ACTION_DATA = stringKey("ErrorActionDataMessage", "");
	public static final ConfigKey<String> ERROR_SCRIPT_CHECK = stringKey("ErrorScriptCheckMessage", "");
	public static final ConfigKey<String> ERROR_SCRIPT_FILE_CHECK = stringKey("ErrorScriptFileCheckMessage", "");


	// List Values
	public static final ConfigKey<List<String>> BLOCK_SELECTOR = stringListKey("BlockSelector", new ArrayList<>());
	public static final ConfigKey<List<String>> SCRIPT_EDITOR = stringListKey("ScriptEditor", new ArrayList<>());


	// CustomText Values
	public static final CustomKey EXPORT_START = customStringKey("ExportStartMessage", "", c -> {
		c.setReplaceKeys("%name%");
	});

	public static final CustomKey EXPORT_END = customStringKey("ExportEndMessage", "", c -> {
		c.setReplaceKeys("%name%");
	});

	public static final CustomKey UPDATE_DOWNLOAD_END = customStringKey("UpdateDownloadEndMessage", "", c -> {
		c.setReplaceKeys("%filename%", "%filepath%", "%filesize%");
	});

	public static final CustomKey UPDATE_CHECK = customStringKey("UpdateCheckMessage", "", c -> {
		c.setReplaceKeys("%pluginname%", "%latestversion%", "%details%");
	});

	public static final CustomKey SCRIPT_COPY = customStringKey("ScriptCopyMessage", "", c -> {
		c.setReplaceKeys("%scripttype%");
	});
	public static final CustomKey SCRIPT_PASTE = customStringKey("ScriptPasteMessage", "", c -> {
		c.setReplaceKeys("%scripttype%");
	});
	public static final CustomKey SCRIPT_CREATE = customStringKey("ScriptCreateMessage", "", c -> {
		c.setReplaceKeys("%scripttype%");
	});

	public static final CustomKey SCRIPT_ADD = customStringKey("ScriptAddMessage", "", c -> {
		c.setReplaceKeys("%scripttype%");
	});

	public static final CustomKey SCRIPT_REMOVE = customStringKey("ScriptRemoveMessage", "", c -> {
		c.setReplaceKeys("%scripttype%");
	});

	public static final CustomKey SELECTOR_POS1 = customStringKey("SelectorPos1Message", "", c -> {
		c.setReplaceKeys("%world%", "%coords%");
	});

	public static final CustomKey SELECTOR_POS2 = customStringKey("SelectorPos2Message", "", c -> {
		c.setReplaceKeys("%world%", "%coords%");
	});

	public static final CustomKey SELECTOR_PASTE = customStringKey("SelectorPasteMessage", "", c -> {
		c.setReplaceKeys("%scripttype%", "%blockcount%");
	});

	public static final CustomKey SELECTOR_REMOVE = customStringKey("SelectorRemoveMessage", "", c -> {
		c.setReplaceKeys("%scripttype%", "%blockcount%");
	});

	public static final CustomKey OPTION_FAILED_TO_EXECUTE = customStringKey("OptionFailedToExecuteMessage", "", c -> {
		c.setReplaceKeys("%option%", "%cause%");
	});

	public static final CustomKey ACTIVE_COOLDOWN = customStringKey("ActiveCooldownMessage", "", c -> {
		c.setReplaceKeys("%hour%", "%minute%", "%second%");
	});

	public static final CustomKey SUCCESS_ACTION_DATA = customStringKey("SuccActionDataMessage", "", c -> {
		c.setReplaceKeys("%actiontype%");
	});

	public static final CustomKey ERROR_SCRIPT_EXECUTE = customStringKey("ErrorScriptExecMessage", "", c -> {
		c.setReplaceKeys("%scripttype%");
	});

	public static final CustomKey ERROR_GROUP = customStringKey("ErrorGroupMessage", "", c -> {
		c.setReplaceKeys("%group%");
	});

	public static final CustomKey ERROR_HAND = customStringKey("ErrorHandMessage", "", c -> {
		c.setReplaceKeys("%material%", "%amount%", "%damage%", "%itemname%");
	});

	public static final CustomKey ERROR_ITEM = customStringKey("ErrorItemMessage", "", c -> {
		c.setReplaceKeys("%material%", "%amount%", "%damage%", "%itemname%");
	});

	public static final CustomKey ERROR_COST = customStringKey("ErrorCostMessage", "", c -> {
		c.setReplaceKeys("%cost%", "%result%");
	});

	public static final CustomKey CONSOLE_SCRIPT_COPY = customStringKey("ConsoleScriptCopyMessage", "", c -> {
		c.setReplaceKeys("%player%", "%scripttype%", "%world%", "%coords%");
	});

	public static final CustomKey CONSOLE_SCRIPT_PASTE = customStringKey("ConsoleScriptPasteMessage", "", c -> {
		c.setReplaceKeys("%player%", "%scripttype%", "%world%", "%coords%");
	});

	public static final CustomKey CONSOLE_SCRIPT_CREATE = customStringKey("ConsoleScriptCreateMessage", "", c -> {
		c.setReplaceKeys("%player%", "%scripttype%", "%world%", "%coords%");
	});

	public static final CustomKey CONSOLE_SCRIPT_ADD = customStringKey("ConsoleScriptAddMessage", "", c -> {
		c.setReplaceKeys("%player%", "%scripttype%", "%world%", "%coords%");
	});

	public static final CustomKey CONSOLE_SCRIPT_REMOVE = customStringKey("ConsoleScriptRemoveMessage", "", c -> {
		c.setReplaceKeys("%player%", "%scripttype%", "%world%", "%coords%");
	});

	public static final CustomKey CONSOLE_SCRIPT_VIEW = customStringKey("ConsoleScriptViewMessage", "", c -> {
		c.setReplaceKeys("%player%", "%scripttype%", "%world%", "%coords%");
	});

	public static final CustomKey CONSOLE_SELECTOR_PASTE = customStringKey("ConsoleSelectorPasteMessage", "", c -> {
		c.setReplaceKeys("%scripttype%", "%blockcount%", "%world%", "%mincoords%", "%maxcoords%");
	});

	public static final CustomKey CONSOLE_SELECTOR_REMOVE = customStringKey("ConsoleSelectorRemoveMessage", "", c -> {
		c.setReplaceKeys("%scripttype%", "%blockcount%", "%world%", "%mincoords%", "%maxcoords%");
	});

	public static final CustomKey CONSOLE_SUCCESS_SCRIPT_EXECUTE = customStringKey("ConsoleSuccScriptExecMessage", "", c -> {
		c.setReplaceKeys("%player%", "%scripttype%", "%world%", "%coords%");
	});

	public static final CustomKey CONSOLE_ERROR_SCRIPT_EXECUTE = customStringKey("ConsoleErrorScriptExecMessage", "", c -> {
		c.setReplaceKeys("%player%", "%scripttype%", "%world%", "%coords%");
	});
}