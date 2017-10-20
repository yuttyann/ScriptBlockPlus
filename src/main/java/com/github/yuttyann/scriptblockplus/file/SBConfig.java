package com.github.yuttyann.scriptblockplus.file;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.enums.ClickType;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public class SBConfig {

	private static SBConfig instance;

	private boolean updateChecker;
	private boolean autoDownload;
	private boolean overwritePlugin;
	private boolean openChangeLog;
	private boolean consoleLog;
	private boolean leftClick;
	private double leftArmLength;
	private List<String> scriptEditorLore;
	private String toolCommandMessage;
	private String reloadCommandMessage;
	private String checkVerCommandMessage;
	private String dataMigrCommandMessage;
	private String createCommandMessage;
	private String addCommandMessage;
	private String removeCommandMessage;
	private String viewCommandMessage;
	private String worldEditPasteCommandMessage;
	private String worldEditRemoveCommandMessage;
	private String notVaultMessage;
	private String senderNoPlayerMessage;
	private String notPermissionMessage;
	private String giveScriptEditorMessage;
	private String allFileReloadMessage;
	private String notLatestPluginMessage;
	private String notScriptBlockFileMessage;
	private String dataMigrStartMessage;
	private String dataMigrEndMessage;
	private String notWorldEditMessage;
	private String updateDownloadStartMessage;
	private String updateDownloadEndMessage;
	private String updateCheckMessage;
	private String updateErrorMessage;
	private String scriptCopyMessage;
	private String scriptPasteMessage;
	private String scriptCreateMessage;
	private String scriptAddMessage;
	private String scriptRemoveMessage;
	private String worldEditNotSelectionMessage;
	private String worldEditPasteMessage;
	private String worldEditRemoveMessage;
	private String optionFailedToExecuteMessage;
	private String activeDelayMessage;
	private String activeCooldownMessage;
	private String succEditDataMessage;
	private String errorEditDataMessage;
	private String errorScriptCheckMessage;
	private String errorScriptFileCheckMessage;
	private String errorScriptExecMessage;
	private String errorGroupMessage;
	private String errorUPLevelMessage;
	private String errorUNDERLevelMessage;
	private String errorEQUALLevelMessage;
	private String errorHandMessage;
	private String errorItemMessage;
	private String errorCostMessage;
	private String consoleScriptCopyMessage;
	private String consoleScriptPasteMessage;
	private String consoleScriptCreateMessage;
	private String consoleScriptAddMessage;
	private String consoleScriptRemoveMessage;
	private String consoleScriptViewMessage;
	private String consoleWorldEditPasteMessage;
	private String consoleWorldEditRemoveMessage;
	private String consoleSuccScriptExecMessage;
	private String consoleErrorScriptExecMessage;

	private SBConfig() {
		YamlConfig yaml = Files.getConfig();
		this.updateChecker = yaml.getBoolean("UpdateChecker", true);
		this.autoDownload = yaml.getBoolean("AutoDownload", true);
		this.overwritePlugin = yaml.getBoolean("OverwritePlugin", false);
		this.openChangeLog = yaml.getBoolean("OpenChangeLog", true);
		this.consoleLog = yaml.getBoolean("ConsoleLog", true);
		this.leftClick = yaml.getBoolean("LeftClick", true);
		this.leftArmLength = yaml.getDouble("LeftArmLength", 5.22D);

		yaml = Files.getLang();
		this.scriptEditorLore = yaml.getStringList("scriptEditor");
		this.toolCommandMessage = yaml.getString("toolCommandMessage");
		this.reloadCommandMessage = yaml.getString("reloadCommandMessage");
		this.checkVerCommandMessage = yaml.getString("checkVerCommandMessage");
		this.dataMigrCommandMessage = yaml.getString("dataMigrCommandMessage");
		this.createCommandMessage = yaml.getString("createCommandMessage");
		this.addCommandMessage = yaml.getString("addCommandMessage");
		this.removeCommandMessage = yaml.getString("removeCommandMessage");
		this.viewCommandMessage = yaml.getString("viewCommandMessage");
		this.worldEditPasteCommandMessage = yaml.getString("worldEditPasteCommandMessage");
		this.worldEditRemoveCommandMessage = yaml.getString("worldEditRemoveCommandMessage");
		this.notVaultMessage = yaml.getString("notVaultMessage");
		this.senderNoPlayerMessage = yaml.getString("senderNoPlayerMessage");
		this.notPermissionMessage = yaml.getString("notPermissionMessage");
		this.giveScriptEditorMessage = yaml.getString("giveScriptEditorMessage");
		this.allFileReloadMessage = yaml.getString("allFileReloadMessage");
		this.notLatestPluginMessage = yaml.getString("notLatestPluginMessage");
		this.updateErrorMessage = yaml.getString("updateErrorMessage");
		this.notScriptBlockFileMessage = yaml.getString("notScriptBlockFileMessage");
		this.dataMigrStartMessage = yaml.getString("dataMigrStartMessage");
		this.dataMigrEndMessage = yaml.getString("dataMigrEndMessage");
		this.notWorldEditMessage = yaml.getString("notWorldEditMessage");
		this.updateDownloadStartMessage = yaml.getString("updateDownloadStartMessage");
		this.updateDownloadEndMessage = yaml.getString("updateDownloadEndMessage");
		this.updateCheckMessage = yaml.getString("updateCheckMessage");
		this.scriptCopyMessage = yaml.getString("scriptCopyMessage");
		this.scriptPasteMessage = yaml.getString("scriptPasteMessage");
		this.scriptCreateMessage = yaml.getString("scriptCreateMessage");
		this.scriptAddMessage = yaml.getString("scriptAddMessage");
		this.scriptRemoveMessage = yaml.getString("scriptRemoveMessage");
		this.worldEditNotSelectionMessage = yaml.getString("worldEditNotSelectionMessage");
		this.worldEditPasteMessage = yaml.getString("worldEditPasteMessage");
		this.worldEditRemoveMessage = yaml.getString("worldEditRemoveMessage");
		this.optionFailedToExecuteMessage = yaml.getString("optionFailedToExecuteMessage");
		this.activeDelayMessage = yaml.getString("activeDelayMessage");
		this.activeCooldownMessage = yaml.getString("activeCooldownMessage");
		this.succEditDataMessage = yaml.getString("succEditDataMessage");
		this.errorEditDataMessage = yaml.getString("errorEditDataMessage");
		this.errorScriptCheckMessage = yaml.getString("errorScriptCheckMessage");
		this.errorScriptFileCheckMessage = yaml.getString("errorScriptFileCheckMessage");
		this.errorScriptExecMessage = yaml.getString("errorScriptExecMessage");
		this.errorGroupMessage = yaml.getString("errorGroupMessage");
		this.errorUPLevelMessage = yaml.getString("errorUPLevelMessage");
		this.errorUNDERLevelMessage = yaml.getString("errorUNDERLevelMessage");
		this.errorEQUALLevelMessage = yaml.getString("errorEQUALLevelMessage");
		this.errorHandMessage = yaml.getString("errorHandMessage");
		this.errorItemMessage = yaml.getString("errorItemMessage");
		this.errorCostMessage = yaml.getString("errorCostMessage");
		this.consoleScriptCopyMessage = yaml.getString("consoleScriptCopyMessage");
		this.consoleScriptPasteMessage = yaml.getString("consoleScriptPasteMessage");
		this.consoleScriptCreateMessage = yaml.getString("consoleScriptCreateMessage");
		this.consoleScriptAddMessage = yaml.getString("consoleScriptAddMessage");
		this.consoleScriptRemoveMessage = yaml.getString("consoleScriptRemoveMessage");
		this.consoleScriptViewMessage = yaml.getString("consoleScriptViewMessage");
		this.consoleWorldEditPasteMessage = yaml.getString("consoleWorldEditPasteMessage");
		this.consoleWorldEditRemoveMessage = yaml.getString("consoleWorldEditRemoveMessage");
		this.consoleSuccScriptExecMessage = yaml.getString("consoleSuccScriptExecMessage");
		this.consoleErrorScriptExecMessage = yaml.getString("consoleErrorScriptExecMessage");
	}

	public static void reload() {
		instance = new SBConfig();
	}

	public static boolean isUpdateChecker() {
		return instance.updateChecker;
	}

	public static boolean isAutoDownload() {
		return instance.autoDownload;
	}

	public static boolean isOverwritePlugin() {
		return instance.overwritePlugin;
	}

	public static boolean isOpenChangeLog() {
		return instance.openChangeLog;
	}

	public static boolean isConsoleLog() {
		return instance.consoleLog;
	}

	public static boolean isLeftClick() {
		return instance.leftClick;
	}

	public static double getLeftArmLength() {
		return instance.leftArmLength;
	}

	public static List<String> getScriptEditorLore() {
		if (instance.scriptEditorLore == null) {
			return new ArrayList<String>(0);
		}
		return replaceColorCode(instance.scriptEditorLore);
	}

	public static String getToolCommandMessage() {
		return ChatColor.stripColor(instance.toolCommandMessage);
	}

	public static String getReloadCommandMessage() {
		return ChatColor.stripColor(instance.reloadCommandMessage);
	}

	public static String getCheckVerCommandMessage() {
		return ChatColor.stripColor(instance.checkVerCommandMessage);
	}

	public static String getDataMigrCommandMessage() {
		return ChatColor.stripColor(instance.dataMigrCommandMessage);
	}

	public static String getCreateCommandMessage() {
		return ChatColor.stripColor(instance.createCommandMessage);
	}

	public static String getAddCommandMessage() {
		return ChatColor.stripColor(instance.addCommandMessage);
	}

	public static String getRemoveCommandMessage() {
		return ChatColor.stripColor(instance.removeCommandMessage);
	}

	public static String getViewCommandMessage() {
		return ChatColor.stripColor(instance.viewCommandMessage);
	}

	public static String getWorldEditPasteCommandMessage() {
		return ChatColor.stripColor(instance.worldEditPasteCommandMessage);
	}

	public static String getWorldEditRemoveCommandMessage() {
		return ChatColor.stripColor(instance.worldEditRemoveCommandMessage);
	}

	public static String getNotVaultMessage() {
		if (instance.notVaultMessage == null) {
			return null;
		}
		return replaceColorCode(instance.notVaultMessage);
	}

	public static String getSenderNoPlayerMessage() {
		if (instance.senderNoPlayerMessage == null) {
			return null;
		}
		return replaceColorCode(instance.senderNoPlayerMessage);
	}

	public static String getNotPermissionMessage() {
		if (instance.notPermissionMessage == null) {
			return null;
		}
		return replaceColorCode(instance.notPermissionMessage);
	}

	public static String getGiveScriptEditorMessage() {
		if (instance.giveScriptEditorMessage == null) {
			return null;
		}
		return replaceColorCode(instance.giveScriptEditorMessage);
	}

	public static String getAllFileReloadMessage() {
		if (instance.allFileReloadMessage == null) {
			return null;
		}
		return replaceColorCode(instance.allFileReloadMessage);
	}

	public static String getNotLatestPluginMessage() {
		if (instance.notLatestPluginMessage == null) {
			return null;
		}
		return replaceColorCode(instance.notLatestPluginMessage);
	}

	public static String getNotScriptBlockFileMessage() {
		if (instance.notScriptBlockFileMessage == null) {
			return null;
		}
		return replaceColorCode(instance.notScriptBlockFileMessage);
	}

	public static String getDataMigrStartMessage() {
		if (instance.dataMigrStartMessage == null) {
			return null;
		}
		return replaceColorCode(instance.dataMigrStartMessage);
	}

	public static String getDataMigrEndMessage() {
		if (instance.dataMigrEndMessage == null) {
			return null;
		}
		return replaceColorCode(instance.dataMigrEndMessage);
	}

	public static String getNotWorldEditMessage() {
		if (instance.notWorldEditMessage == null) {
			return null;
		}
		return replaceColorCode(instance.notWorldEditMessage);
	}

	public static String getUpdateDownloadStartMessage() {
		if (instance.updateDownloadStartMessage == null) {
			return null;
		}
		return replaceColorCode(instance.updateDownloadStartMessage);
	}

	public static String getUpdateDownloadEndMessage(String fileName, String filePath, String fileSize) {
		if (instance.updateDownloadEndMessage == null) {
			return null;
		}
		String updateDownloadEndMessage = instance.updateDownloadEndMessage;
		updateDownloadEndMessage = replace(updateDownloadEndMessage, "%filename%", fileName);
		updateDownloadEndMessage = replace(updateDownloadEndMessage, "%filepath%", filePath);
		updateDownloadEndMessage = replace(updateDownloadEndMessage, "%filesize%", fileSize);
		return replaceColorCode(updateDownloadEndMessage);
	}

	public static String getUpdateCheckMessages(String pluginName, String latestVersion, List<String> details) {
		if (instance.updateCheckMessage == null) {
			return null;
		}
		String updateCheckMessage = instance.updateCheckMessage;
		updateCheckMessage = replace(updateCheckMessage, "%pluginname%", pluginName);
		updateCheckMessage = replace(updateCheckMessage, "%latestversion%", latestVersion);
		if (updateCheckMessage.indexOf("%details%") != -1) {
			StringBuilder builder = new StringBuilder(details.size());
			for (int i = 0; i < details.size(); i++) {
				String message = details.get(i);
				if (message.startsWith("$")) {
					builder.append("  - ").append(StringUtils.removeStart(message, "$"));
				} else {
					builder.append("・").append(message);
				}
				if (i != (details.size() - 1)) {
					builder.append("|~");
				}
			}
			updateCheckMessage = replace(updateCheckMessage, "%details%", builder.toString());
		}
		return replaceColorCode(updateCheckMessage);
	}

	public static String getUpdateErrorMessage() {
		if (instance.updateErrorMessage == null) {
			return null;
		}
		return replaceColorCode(instance.updateErrorMessage);
	}

	public static String getScriptCopyMessage(ScriptType scriptType) {
		if (instance.scriptCopyMessage == null) {
			return null;
		}
		return replaceColorCode(replace(instance.scriptCopyMessage, "%scripttype%", scriptType.getType()));
	}

	public static String getScriptPasteMessage(ScriptType scriptType) {
		if (instance.scriptPasteMessage == null) {
			return null;
		}
		return replaceColorCode(replace(instance.scriptPasteMessage, "%scripttype%", scriptType.getType()));
	}

	public static String getScriptCreateMessage(ScriptType scriptType) {
		if (instance.scriptCreateMessage == null) {
			return null;
		}
		return replaceColorCode(replace(instance.scriptCreateMessage, "%scripttype%", scriptType.getType()));
	}

	public static String getScriptAddMessage(ScriptType scriptType) {
		if (instance.scriptAddMessage == null) {
			return null;
		}
		return replaceColorCode(replace(instance.scriptAddMessage, "%scripttype%", scriptType.getType()));
	}

	public static String getScriptRemoveMessage(ScriptType scriptType) {
		if (instance.scriptRemoveMessage == null) {
			return null;
		}
		return replaceColorCode(replace(instance.scriptRemoveMessage, "%scripttype%", scriptType.getType()));
	}

	public static String getWorldEditNotSelectionMessage() {
		if (instance.worldEditNotSelectionMessage == null) {
			return null;
		}
		return replaceColorCode(instance.worldEditNotSelectionMessage);
	}

	public static String getWorldEditPasteMessage(ScriptType scriptType) {
		if (instance.worldEditPasteMessage == null) {
			return null;
		}
		return replaceColorCode(replace(instance.worldEditPasteMessage, "%scripttype%", scriptType.getType()));
	}

	public static String getWorldEditRemoveMessage(String scriptType) {
		if (instance.worldEditRemoveMessage == null) {
			return null;
		}
		return replaceColorCode(replace(instance.worldEditRemoveMessage, "%scripttype%", scriptType));
	}

	public static String getOptionFailedToExecuteMessage(Option option, Throwable throwable) {
		if (instance.optionFailedToExecuteMessage == null) {
			return null;
		}
		String optionFailedToExecuteMessage = instance.optionFailedToExecuteMessage;
		optionFailedToExecuteMessage = replace(optionFailedToExecuteMessage, "%option%", option.getClass().getSimpleName());
		optionFailedToExecuteMessage = replace(optionFailedToExecuteMessage, "%cause%", throwable.getClass().getSimpleName());
		return replaceColorCode(optionFailedToExecuteMessage);
	}

	public static String getActiveDelayMessage() {
		if (instance.activeDelayMessage == null) {
			return null;
		}
		return replaceColorCode(instance.activeDelayMessage);
	}

	public static String getActiveCooldownMessage(short hour, byte minute, byte second) {
		if (instance.activeCooldownMessage == null) {
			return null;
		}
		String activeCooldownMessage = instance.activeCooldownMessage;
		activeCooldownMessage = replace(activeCooldownMessage, "%hour%", hour + "");
		activeCooldownMessage = replace(activeCooldownMessage, "%minute%", minute + "");
		activeCooldownMessage = replace(activeCooldownMessage, "%second%", second + "");
		return replaceColorCode(activeCooldownMessage);
	}

	public static String getSuccEditDataMessage(ClickType clickType) {
		if (instance.succEditDataMessage == null) {
			return null;
		}
		String type = replace(clickType.name().toLowerCase(), "_", "-");
		return replaceColorCode(replace(instance.succEditDataMessage, "%clicktype%", type));
	}

	public static String getErrorEditDataMessage() {
		if (instance.errorEditDataMessage == null) {
			return null;
		}
		return replaceColorCode(instance.errorEditDataMessage);
	}

	public static String getErrorScriptCheckMessage() {
		if (instance.errorScriptCheckMessage == null) {
			return null;
		}
		return replaceColorCode(instance.errorScriptCheckMessage);
	}

	public static String getErrorScriptFileCheckMessage() {
		if (instance.errorScriptFileCheckMessage == null) {
			return null;
		}
		return replaceColorCode(instance.errorScriptFileCheckMessage);
	}

	public static String getErrorScriptMessage(ScriptType scriptType) {
		if (instance.errorScriptExecMessage == null) {
			return null;
		}
		return replaceColorCode(replace(instance.errorScriptExecMessage, "%scripttype%", scriptType.getType()));
	}

	public static String getErrorGroupMessage(String group) {
		if (instance.errorGroupMessage == null) {
			return null;
		}
		return replaceColorCode(replace(instance.errorGroupMessage, "%group%", group));
	}

	public static String getErrorUPLevelMessage(int playerLevel, int requiredLevel) {
		if (instance.errorUPLevelMessage == null) {
			return null;
		}
		String errorUPLevelMessage = instance.errorUPLevelMessage;
		errorUPLevelMessage = replace(errorUPLevelMessage, "%type%", "UP");
		errorUPLevelMessage = replace(errorUPLevelMessage, "%playerLevel%", playerLevel + "");
		errorUPLevelMessage = replace(errorUPLevelMessage, "%requiredLevel%", requiredLevel + "");
		return replaceColorCode(errorUPLevelMessage);
	}

	public static String getErrorUNDERLevelMessage(int playerLevel, int requiredLevel) {
		if (instance.errorUNDERLevelMessage == null) {
			return null;
		}
		String errorUNDERLevelMessage = instance.errorUNDERLevelMessage;
		errorUNDERLevelMessage = replace(errorUNDERLevelMessage, "%type%", "UNDER");
		errorUNDERLevelMessage = replace(errorUNDERLevelMessage, "%playerLevel%", playerLevel + "");
		errorUNDERLevelMessage = replace(errorUNDERLevelMessage, "%requiredLevel%", requiredLevel + "");
		return replaceColorCode(errorUNDERLevelMessage);
	}

	public static String getErrorEQUALLevelMessage(int playerLevel, int requiredLevel) {
		if (instance.errorEQUALLevelMessage == null) {
			return null;
		}
		String errorEQUALLevelMessage = instance.errorEQUALLevelMessage;
		errorEQUALLevelMessage = replace(errorEQUALLevelMessage, "%type%", "EQUAL");
		errorEQUALLevelMessage = replace(errorEQUALLevelMessage, "%playerLevel%", playerLevel + "");
		errorEQUALLevelMessage = replace(errorEQUALLevelMessage, "%requiredLevel%", requiredLevel + "");
		return replaceColorCode(errorEQUALLevelMessage);
	}

	public static String getErrorHandMessage(Material material, int id, int amount, short damage, String itemName) {
		if (instance.errorHandMessage == null) {
			return null;
		}
		String errorHandMessage = instance.errorHandMessage;
		errorHandMessage = replace(errorHandMessage, "%material%", material.toString());
		errorHandMessage = replace(errorHandMessage, "%id%", id + "");
		errorHandMessage = replace(errorHandMessage, "%amount%", amount + "");
		errorHandMessage = replace(errorHandMessage, "%damage%", damage + "");
		errorHandMessage = replace(errorHandMessage, "%itemname%", itemName != null ? itemName : material.toString());
		return replaceColorCode(errorHandMessage);
	}

	public static String getErrorItemMessage(Material material, int id, int amount, short damage, String itemName) {
		if (instance.errorItemMessage == null) {
			return null;
		}
		String errorItemMessage = instance.errorItemMessage;
		errorItemMessage = replace(errorItemMessage, "%material%", material.toString());
		errorItemMessage = replace(errorItemMessage, "%id%", id + "");
		errorItemMessage = replace(errorItemMessage, "%amount%", amount + "");
		errorItemMessage = replace(errorItemMessage, "%damage%", damage + "");
		errorItemMessage = replace(errorItemMessage, "%itemname%", itemName != null ? itemName : material.toString());
		return replaceColorCode(errorItemMessage);
	}

	public static String getErrorCostMessage(double cost, double result) {
		if (instance.errorCostMessage == null) {
			return null;
		}
		String errorCostMessage = instance.errorCostMessage;
		errorCostMessage = replace(errorCostMessage, "%cost%", cost + "");
		errorCostMessage = replace(errorCostMessage, "%result%", result + "");
		return replaceColorCode(errorCostMessage);
	}

	public static String getConsoleScriptCopyMessage(String name, ScriptType scriptType, Location location) {
		if (instance.consoleScriptCopyMessage == null || !instance.consoleLog) {
			return null;
		}
		String consoleScriptCopyMessage = instance.consoleScriptCopyMessage;
		consoleScriptCopyMessage = replace(consoleScriptCopyMessage, "%player%", name);
		consoleScriptCopyMessage = replace(consoleScriptCopyMessage, "%scripttype%", scriptType.getType());
		consoleScriptCopyMessage = replace(consoleScriptCopyMessage, "%world%", location.getWorld().getName());
		consoleScriptCopyMessage = replace(consoleScriptCopyMessage, "%coords%", BlockCoords.getCoords(location));
		return replaceColorCode(consoleScriptCopyMessage);
	}

	public static String getConsoleScriptPasteMessage(String name, ScriptType scriptType, Location location) {
		if (instance.consoleScriptPasteMessage == null || !instance.consoleLog) {
			return null;
		}
		String consoleScriptPasteMessage = instance.consoleScriptPasteMessage;
		consoleScriptPasteMessage = replace(consoleScriptPasteMessage, "%player%", name);
		consoleScriptPasteMessage = replace(consoleScriptPasteMessage, "%scripttype%", scriptType.getType());
		consoleScriptPasteMessage = replace(consoleScriptPasteMessage, "%world%", location.getWorld().getName());
		consoleScriptPasteMessage = replace(consoleScriptPasteMessage, "%coords%", BlockCoords.getCoords(location));
		return replaceColorCode(consoleScriptPasteMessage);
	}

	public static String getConsoleScriptCreateMessage(String name, ScriptType scriptType, Location location) {
		if (instance.consoleScriptCreateMessage == null || !instance.consoleLog) {
			return null;
		}
		String consoleScriptCreateMessage = instance.consoleScriptCreateMessage;
		consoleScriptCreateMessage = replace(consoleScriptCreateMessage, "%player%", name);
		consoleScriptCreateMessage = replace(consoleScriptCreateMessage, "%scripttype%", scriptType.getType());
		consoleScriptCreateMessage = replace(consoleScriptCreateMessage, "%world%", location.getWorld().getName());
		consoleScriptCreateMessage = replace(consoleScriptCreateMessage, "%coords%", BlockCoords.getCoords(location));
		return replaceColorCode(consoleScriptCreateMessage);
	}

	public static String getConsoleScriptAddMessage(String name, ScriptType scriptType, Location location) {
		if (instance.consoleScriptAddMessage == null || !instance.consoleLog) {
			return null;
		}
		String consoleScriptAddMessage = instance.consoleScriptAddMessage;
		consoleScriptAddMessage = replace(consoleScriptAddMessage, "%player%", name);
		consoleScriptAddMessage = replace(consoleScriptAddMessage, "%scripttype%", scriptType.getType());
		consoleScriptAddMessage = replace(consoleScriptAddMessage, "%world%", location.getWorld().getName());
		consoleScriptAddMessage = replace(consoleScriptAddMessage, "%coords%", BlockCoords.getCoords(location));
		return replaceColorCode(consoleScriptAddMessage);
	}

	public static String getConsoleScriptRemoveMessage(String name, ScriptType scriptType, Location location) {
		if (instance.consoleScriptRemoveMessage == null || !instance.consoleLog) {
			return null;
		}
		String consoleScriptRemoveMessage = instance.consoleScriptRemoveMessage;
		consoleScriptRemoveMessage = replace(consoleScriptRemoveMessage, "%player%", name);
		consoleScriptRemoveMessage = replace(consoleScriptRemoveMessage, "%scripttype%", scriptType.getType());
		consoleScriptRemoveMessage = replace(consoleScriptRemoveMessage, "%world%", location.getWorld().getName());
		consoleScriptRemoveMessage = replace(consoleScriptRemoveMessage, "%coords%", BlockCoords.getCoords(location));
		return replaceColorCode(consoleScriptRemoveMessage);
	}

	public static String getConsoleScriptViewMessage(String name, ScriptType scriptType, Location location) {
		if (instance.consoleScriptViewMessage == null || !instance.consoleLog) {
			return null;
		}
		String consoleScriptViewMessage = instance.consoleScriptViewMessage;
		consoleScriptViewMessage = replace(consoleScriptViewMessage, "%player%", name);
		consoleScriptViewMessage = replace(consoleScriptViewMessage, "%scripttype%", scriptType.getType());
		consoleScriptViewMessage = replace(consoleScriptViewMessage, "%world%", location.getWorld().getName());
		consoleScriptViewMessage = replace(consoleScriptViewMessage, "%coords%", BlockCoords.getCoords(location));
		return replaceColorCode(consoleScriptViewMessage);
	}

	public static String getConsoleWorldEditPasteMessage(ScriptType scriptType, Location min, Location max) {
		if (instance.consoleWorldEditPasteMessage == null || !instance.consoleLog) {
			return null;
		}
		String world = min.getWorld().getName();
		String minCoords = BlockCoords.getCoords(min);
		String maxCoords = BlockCoords.getCoords(max);
		String consoleWorldEditPasteMessage = instance.consoleWorldEditPasteMessage;
		consoleWorldEditPasteMessage = replace(consoleWorldEditPasteMessage, "%scripttype%", scriptType.getType());
		consoleWorldEditPasteMessage = replace(consoleWorldEditPasteMessage, "%world%", world);
		consoleWorldEditPasteMessage = replace(consoleWorldEditPasteMessage, "%mincoords%", minCoords);
		consoleWorldEditPasteMessage = replace(consoleWorldEditPasteMessage, "%maxcoords%", maxCoords);
		return replaceColorCode(consoleWorldEditPasteMessage);
	}

	public static String getConsoleWorldEditRemoveMessage(String scriptType, Location min, Location max) {
		if (instance.consoleWorldEditRemoveMessage == null || !instance.consoleLog) {
			return null;
		}
		String world = min.getWorld().getName();
		String minCoords = BlockCoords.getCoords(min);
		String maxCoords = BlockCoords.getCoords(max);
		String consoleWorldEditRemoveMessage = instance.consoleWorldEditRemoveMessage;
		consoleWorldEditRemoveMessage = replace(consoleWorldEditRemoveMessage, "%scripttype%", scriptType);
		consoleWorldEditRemoveMessage = replace(consoleWorldEditRemoveMessage, "%world%", world);
		consoleWorldEditRemoveMessage = replace(consoleWorldEditRemoveMessage, "%mincoords%", minCoords);
		consoleWorldEditRemoveMessage = replace(consoleWorldEditRemoveMessage, "%maxcoords%", maxCoords);
		return replaceColorCode(consoleWorldEditRemoveMessage);
	}

	public static String getConsoleSuccScriptExecMessage(String name, ScriptType scriptType, Location location) {
		if (instance.consoleSuccScriptExecMessage == null || !instance.consoleLog) {
			return null;
		}
		String consoleSuccScriptExecMessage = instance.consoleSuccScriptExecMessage;
		consoleSuccScriptExecMessage = replace(consoleSuccScriptExecMessage, "%player%", name);
		consoleSuccScriptExecMessage = replace(consoleSuccScriptExecMessage, "%scripttype%", scriptType.getType());
		consoleSuccScriptExecMessage = replace(consoleSuccScriptExecMessage, "%world%", location.getWorld().getName());
		consoleSuccScriptExecMessage = replace(consoleSuccScriptExecMessage, "%coords%", BlockCoords.getCoords(location));
		return replaceColorCode(consoleSuccScriptExecMessage);
	}

	public static String getConsoleErrorScriptExecMessage(String name, ScriptType scriptType, Location location) {
		if (instance.consoleErrorScriptExecMessage == null || !instance.consoleLog) {
			return null;
		}
		String consoleErrorScriptExecMessage = instance.consoleErrorScriptExecMessage;
		consoleErrorScriptExecMessage = replace(consoleErrorScriptExecMessage, "%player%", name);
		consoleErrorScriptExecMessage = replace(consoleErrorScriptExecMessage, "%scripttype%", scriptType.getType());
		consoleErrorScriptExecMessage = replace(consoleErrorScriptExecMessage, "%world%", location.getWorld().getName());
		consoleErrorScriptExecMessage = replace(consoleErrorScriptExecMessage, "%coords%", BlockCoords.getCoords(location));
		return replaceColorCode(consoleErrorScriptExecMessage);
	}

	private static String replaceColorCode(String text) {
		return replace(text, "&", "§");
	}

	private static List<String> replaceColorCode(List<String> list) {
		if (list == null || list.size() == 0) {
			return null;
		}
		for (int i = 0; i < list.size(); i++) {
			list.set(i, replaceColorCode(list.get(i)));
		}
		return list;
	}

	private static String replace(String source, String search, String replace) {
		return StringUtils.replace(source, search, replace);
	}
}