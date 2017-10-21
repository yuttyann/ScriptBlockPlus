package com.github.yuttyann.scriptblockplus.file;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.enums.ClickType;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public final class SBConfig {

	private static final Map<String, Object> datas = new HashMap<String, Object>();

	private SBConfig() {
		throw new AssertionError();
	}

	public static void reload() {
		reloadConfig();
		reloadLang();
	}

	public static void reloadConfig() {
		YamlConfig yaml = Files.getConfig();
		if (yaml == null) {
			throw new NullPointerException();
		}
		datas.put("updateChecker", yaml.getBoolean("updateChecker", true));
		datas.put("autoDownload", yaml.getBoolean("autoDownload", true));
		datas.put("openChangeLog", yaml.getBoolean("openChangeLog", true));
		datas.put("language", yaml.getString("language", "en"));
		datas.put("consoleLog", yaml.getBoolean("consoleLog", true));
		datas.put("leftClick", yaml.getBoolean("leftClick", true));
		datas.put("leftArmLength", yaml.getDouble("leftArmLength", 5.22D));
	}

	public static void reloadLang() {
		YamlConfig yaml = Files.getLang();
		if (yaml == null) {
			throw new NullPointerException();
		}
		datas.put("scriptEditorLore", yaml.getStringList("scriptEditor"));
		datas.put("toolCommandMessage", yaml.getString("toolCommandMessage"));
		datas.put("reloadCommandMessage", yaml.getString("reloadCommandMessage"));
		datas.put("checkVerCommandMessage", yaml.getString("checkVerCommandMessage"));
		datas.put("dataMigrCommandMessage", yaml.getString("dataMigrCommandMessage"));
		datas.put("createCommandMessage", yaml.getString("createCommandMessage"));
		datas.put("addCommandMessage", yaml.getString("addCommandMessage"));
		datas.put("removeCommandMessage", yaml.getString("removeCommandMessage"));
		datas.put("viewCommandMessage", yaml.getString("viewCommandMessage"));
		datas.put("worldEditPasteCommandMessage", yaml.getString("worldEditPasteCommandMessage"));
		datas.put("worldEditRemoveCommandMessage", yaml.getString("worldEditRemoveCommandMessage"));
		datas.put("notVaultMessage", yaml.getString("notVaultMessage"));
		datas.put("senderNoPlayerMessage", yaml.getString("senderNoPlayerMessage"));
		datas.put("notPermissionMessage", yaml.getString("notPermissionMessage"));
		datas.put("giveScriptEditorMessage", yaml.getString("giveScriptEditorMessage"));
		datas.put("allFileReloadMessage", yaml.getString("allFileReloadMessage"));
		datas.put("notLatestPluginMessage", yaml.getString("notLatestPluginMessage"));
		datas.put("updateErrorMessage", yaml.getString("updateErrorMessage"));
		datas.put("notScriptBlockFileMessage", yaml.getString("notScriptBlockFileMessage"));
		datas.put("dataMigrStartMessage", yaml.getString("dataMigrStartMessage"));
		datas.put("dataMigrEndMessage", yaml.getString("dataMigrEndMessage"));
		datas.put("notWorldEditMessage", yaml.getString("notWorldEditMessage"));
		datas.put("updateDownloadStartMessage", yaml.getString("updateDownloadStartMessage"));
		datas.put("updateDownloadEndMessage", yaml.getString("updateDownloadEndMessage"));
		datas.put("updateCheckMessage", yaml.getString("updateCheckMessage"));
		datas.put("scriptCopyMessage", yaml.getString("scriptCopyMessage"));
		datas.put("scriptPasteMessage", yaml.getString("scriptPasteMessage"));
		datas.put("scriptCreateMessage", yaml.getString("scriptCreateMessage"));
		datas.put("scriptAddMessage", yaml.getString("scriptAddMessage"));
		datas.put("scriptRemoveMessage", yaml.getString("scriptRemoveMessage"));
		datas.put("worldEditNotSelectionMessage", yaml.getString("worldEditNotSelectionMessage"));
		datas.put("worldEditPasteMessage", yaml.getString("worldEditPasteMessage"));
		datas.put("worldEditRemoveMessage", yaml.getString("worldEditRemoveMessage"));
		datas.put("optionFailedToExecuteMessage", yaml.getString("optionFailedToExecuteMessage"));
		datas.put("activeDelayMessage", yaml.getString("activeDelayMessage"));
		datas.put("activeCooldownMessage", yaml.getString("activeCooldownMessage"));
		datas.put("succEditDataMessage", yaml.getString("succEditDataMessage"));
		datas.put("errorEditDataMessage", yaml.getString("errorEditDataMessage"));
		datas.put("errorScriptCheckMessage", yaml.getString("errorScriptCheckMessage"));
		datas.put("errorScriptFileCheckMessage", yaml.getString("errorScriptFileCheckMessage"));
		datas.put("errorScriptExecMessage", yaml.getString("errorScriptExecMessage"));
		datas.put("errorGroupMessage", yaml.getString("errorGroupMessage"));
		datas.put("errorUPLevelMessage", yaml.getString("errorUPLevelMessage"));
		datas.put("errorUNDERLevelMessage", yaml.getString("errorUNDERLevelMessage"));
		datas.put("errorEQUALLevelMessage", yaml.getString("errorEQUALLevelMessage"));
		datas.put("errorHandMessage", yaml.getString("errorHandMessage"));
		datas.put("errorItemMessage", yaml.getString("errorItemMessage"));
		datas.put("errorCostMessage", yaml.getString("errorCostMessage"));
		datas.put("consoleScriptCopyMessage", yaml.getString("consoleScriptCopyMessage"));
		datas.put("consoleScriptPasteMessage", yaml.getString("consoleScriptPasteMessage"));
		datas.put("consoleScriptCreateMessage", yaml.getString("consoleScriptCreateMessage"));
		datas.put("consoleScriptAddMessage", yaml.getString("consoleScriptAddMessage"));
		datas.put("consoleScriptRemoveMessage", yaml.getString("consoleScriptRemoveMessage"));
		datas.put("consoleScriptViewMessage", yaml.getString("consoleScriptViewMessage"));
		datas.put("consoleWorldEditPasteMessage", yaml.getString("consoleWorldEditPasteMessage"));
		datas.put("consoleWorldEditRemoveMessage", yaml.getString("consoleWorldEditRemoveMessage"));
		datas.put("consoleSuccScriptExecMessage", yaml.getString("consoleSuccScriptExecMessage"));
		datas.put("consoleErrorScriptExecMessage", yaml.getString("consoleErrorScriptExecMessage"));
	}

	public static Map<String, Object> getDatas() {
		return Collections.unmodifiableMap(datas);
	}

	public static boolean isUpdateChecker() {
		return getBoolean("updateChecker");
	}

	public static boolean isAutoDownload() {
		return getBoolean("autoDownload");
	}

	public static boolean isOpenChangeLog() {
		return getBoolean("openChangeLog");
	}

	public static String getLanguage() {
		return getString("language");
	}

	public static boolean isConsoleLog() {
		return getBoolean("consoleLog");
	}

	public static boolean isLeftClick() {
		return getBoolean("leftClick");
	}

	public static double getLeftArmLength() {
		return getDouble("leftArmLength");
	}

	public static List<String> getScriptEditorLore() {
		return replaceColorCode(getStringList("scriptEditorLore"));
	}

	public static String getToolCommandMessage() {
		return ChatColor.stripColor(getString("toolCommandMessage"));
	}

	public static String getReloadCommandMessage() {
		return ChatColor.stripColor(getString("reloadCommandMessage"));
	}

	public static String getCheckVerCommandMessage() {
		return ChatColor.stripColor(getString("checkVerCommandMessage"));
	}

	public static String getDataMigrCommandMessage() {
		return ChatColor.stripColor(getString("dataMigrCommandMessage"));
	}

	public static String getCreateCommandMessage() {
		return ChatColor.stripColor(getString("createCommandMessage"));
	}

	public static String getAddCommandMessage() {
		return ChatColor.stripColor(getString("addCommandMessage"));
	}

	public static String getRemoveCommandMessage() {
		return ChatColor.stripColor(getString("removeCommandMessage"));
	}

	public static String getViewCommandMessage() {
		return ChatColor.stripColor(getString("viewCommandMessage"));
	}

	public static String getWorldEditPasteCommandMessage() {
		return ChatColor.stripColor(getString("worldEditPasteCommandMessage"));
	}

	public static String getWorldEditRemoveCommandMessage() {
		return ChatColor.stripColor(getString("worldEditRemoveCommandMessage"));
	}

	public static String getNotVaultMessage() {
		return replaceColorCode(getString("notVaultMessage"));
	}

	public static String getSenderNoPlayerMessage() {
		return replaceColorCode(getString("senderNoPlayerMessage"));
	}

	public static String getNotPermissionMessage() {
		return replaceColorCode(getString("notPermissionMessage"));
	}

	public static String getGiveScriptEditorMessage() {
		return replaceColorCode(getString("giveScriptEditorMessage"));
	}

	public static String getAllFileReloadMessage() {
		return replaceColorCode(getString("allFileReloadMessage"));
	}

	public static String getNotLatestPluginMessage() {
		return replaceColorCode(getString("notLatestPluginMessage"));
	}

	public static String getNotScriptBlockFileMessage() {
		return replaceColorCode(getString("notScriptBlockFileMessage"));
	}

	public static String getDataMigrStartMessage() {
		return replaceColorCode(getString("dataMigrStartMessage"));
	}

	public static String getDataMigrEndMessage() {
		return replaceColorCode(getString("dataMigrEndMessage"));
	}

	public static String getNotWorldEditMessage() {
		return replaceColorCode(getString("notWorldEditMessage"));
	}

	public static String getUpdateDownloadStartMessage() {
		return replaceColorCode(getString("updateDownloadStartMessage"));
	}

	public static String getUpdateDownloadEndMessage(String fileName, String filePath, String fileSize) {
		String message = getString("updateDownloadEndMessage");
		message = replace(message, "%filename%", fileName);
		message = replace(message, "%filepath%", filePath);
		message = replace(message, "%filesize%", fileSize);
		return replaceColorCode(message);
	}

	public static String getUpdateCheckMessages(String pluginName, String latestVersion, List<String> details) {
		String message = getString("updateCheckMessage");
		message = replace(message, "%pluginname%", pluginName);
		message = replace(message, "%latestversion%", latestVersion);
		if (message.indexOf("%details%") != -1) {
			StringBuilder builder = new StringBuilder(details.size());
			for (int i = 0; i < details.size(); i++) {
				String info = details.get(i);
				if (info.startsWith("$")) {
					builder.append("  - ").append(StringUtils.removeStart(info, "$"));
				} else {
					builder.append("・").append(info);
				}
				if (i != (details.size() - 1)) {
					builder.append("|~");
				}
			}
			message = replace(message, "%details%", builder.toString());
		}
		return replaceColorCode(message);
	}

	public static String getUpdateErrorMessage() {
		return replaceColorCode(getString("updateErrorMessage"));
	}

	public static String getScriptCopyMessage(ScriptType scriptType) {
		return replaceColorCode(replace(getString("scriptCopyMessage"), "%scripttype%", scriptType.getType()));
	}

	public static String getScriptPasteMessage(ScriptType scriptType) {
		return replaceColorCode(replace(getString("scriptPasteMessage"), "%scripttype%", scriptType.getType()));
	}

	public static String getScriptCreateMessage(ScriptType scriptType) {
		return replaceColorCode(replace(getString("scriptCreateMessage"), "%scripttype%", scriptType.getType()));
	}

	public static String getScriptAddMessage(ScriptType scriptType) {
		return replaceColorCode(replace(getString("scriptAddMessage"), "%scripttype%", scriptType.getType()));
	}

	public static String getScriptRemoveMessage(ScriptType scriptType) {
		return replaceColorCode(replace(getString("scriptRemoveMessage"), "%scripttype%", scriptType.getType()));
	}

	public static String getWorldEditNotSelectionMessage() {
		return replaceColorCode(getString("worldEditNotSelectionMessage"));
	}

	public static String getWorldEditPasteMessage(ScriptType scriptType) {
		return replaceColorCode(replace(getString("worldEditPasteMessage"), "%scripttype%", scriptType.getType()));
	}

	public static String getWorldEditRemoveMessage(String scriptType) {
		return replaceColorCode(replace(getString("worldEditRemoveMessage"), "%scripttype%", scriptType));
	}

	public static String getOptionFailedToExecuteMessage(Option option, Throwable throwable) {
		String message = getString("optionFailedToExecuteMessage");
		message = replace(message, "%option%", option.getClass().getSimpleName());
		message = replace(message, "%cause%", throwable.getClass().getSimpleName());
		return replaceColorCode(message);
	}

	public static String getActiveDelayMessage() {
		return replaceColorCode(getString("activeDelayMessage"));
	}

	public static String getActiveCooldownMessage(short hour, byte minute, byte second) {
		String message = getString("activeCooldownMessage");
		message = replace(message, "%hour%", hour + "");
		message = replace(message, "%minute%", minute + "");
		message = replace(message, "%second%", second + "");
		return replaceColorCode(message);
	}

	public static String getSuccEditDataMessage(ScriptType scriptType, ClickType clickType) {
		String type = scriptType.getType() + "-"  + clickType.name().toLowerCase();
		return replaceColorCode(replace(getString("succEditDataMessage"), "%clicktype%", type));
	}

	public static String getErrorEditDataMessage() {
		return replaceColorCode(getString("errorEditDataMessage"));
	}

	public static String getErrorScriptCheckMessage() {
		return replaceColorCode(getString("errorScriptCheckMessage"));
	}

	public static String getErrorScriptFileCheckMessage() {
		return replaceColorCode(getString("errorScriptFileCheckMessage"));
	}

	public static String getErrorScriptMessage(ScriptType scriptType) {
		return replaceColorCode(replace(getString("errorScriptExecMessage"), "%scripttype%", scriptType.getType()));
	}

	public static String getErrorGroupMessage(String group) {
		return replaceColorCode(replace(getString("errorGroupMessage"), "%group%", group));
	}

	public static String getErrorUPLevelMessage(int playerLevel, int requiredLevel) {
		String message = getString("errorUPLevelMessage");
		message = replace(message, "%type%", "UP");
		message = replace(message, "%playerLevel%", playerLevel + "");
		message = replace(message, "%requiredLevel%", requiredLevel + "");
		return replaceColorCode(message);
	}

	public static String getErrorUNDERLevelMessage(int playerLevel, int requiredLevel) {
		String message = getString("errorUNDERLevelMessage");
		message = replace(message, "%type%", "UNDER");
		message = replace(message, "%playerLevel%", playerLevel + "");
		message = replace(message, "%requiredLevel%", requiredLevel + "");
		return replaceColorCode(message);
	}

	public static String getErrorEQUALLevelMessage(int playerLevel, int requiredLevel) {
		String message = getString("errorEQUALLevelMessage");
		message = replace(message, "%type%", "EQUAL");
		message = replace(message, "%playerLevel%", playerLevel + "");
		message = replace(message, "%requiredLevel%", requiredLevel + "");
		return replaceColorCode(message);
	}

	public static String getErrorHandMessage(Material material, int id, int amount, short damage, String itemName) {
		String message = getString("errorHandMessage");
		message = replace(message, "%material%", material.toString());
		message = replace(message, "%id%", id + "");
		message = replace(message, "%amount%", amount + "");
		message = replace(message, "%damage%", damage + "");
		message = replace(message, "%itemname%", itemName != null ? itemName : material.toString());
		return replaceColorCode(message);
	}

	public static String getErrorItemMessage(Material material, int id, int amount, short damage, String itemName) {
		String message = getString("errorItemMessage");
		message = replace(message, "%material%", material.toString());
		message = replace(message, "%id%", id + "");
		message = replace(message, "%amount%", amount + "");
		message = replace(message, "%damage%", damage + "");
		message = replace(message, "%itemname%", itemName != null ? itemName : material.toString());
		return replaceColorCode(message);
	}

	public static String getErrorCostMessage(double cost, double result) {
		String message = getString("errorCostMessage");
		message = replace(message, "%cost%", cost + "");
		message = replace(message, "%result%", result + "");
		return replaceColorCode(message);
	}

	public static String getConsoleScriptCopyMessage(String name, ScriptType scriptType, Location location) {
		if (!isConsoleLog()) {
			return null;
		}
		String message = getString("consoleScriptCopyMessage");
		message = replace(message, "%player%", name);
		message = replace(message, "%scripttype%", scriptType.getType());
		message = replace(message, "%world%", location.getWorld().getName());
		message = replace(message, "%coords%", BlockCoords.getCoords(location));
		return replaceColorCode(message);
	}

	public static String getConsoleScriptPasteMessage(String name, ScriptType scriptType, Location location) {
		if (!isConsoleLog()) {
			return null;
		}
		String message = getString("consoleScriptPasteMessage");
		message = replace(message, "%player%", name);
		message = replace(message, "%scripttype%", scriptType.getType());
		message = replace(message, "%world%", location.getWorld().getName());
		message = replace(message, "%coords%", BlockCoords.getCoords(location));
		return replaceColorCode(message);
	}

	public static String getConsoleScriptCreateMessage(String name, ScriptType scriptType, Location location) {
		if (!isConsoleLog()) {
			return null;
		}
		String message = getString("consoleScriptCreateMessage");
		message = replace(message, "%player%", name);
		message = replace(message, "%scripttype%", scriptType.getType());
		message = replace(message, "%world%", location.getWorld().getName());
		message = replace(message, "%coords%", BlockCoords.getCoords(location));
		return replaceColorCode(message);
	}

	public static String getConsoleScriptAddMessage(String name, ScriptType scriptType, Location location) {
		if (!isConsoleLog()) {
			return null;
		}
		String message = getString("consoleScriptAddMessage");
		message = replace(message, "%player%", name);
		message = replace(message, "%scripttype%", scriptType.getType());
		message = replace(message, "%world%", location.getWorld().getName());
		message = replace(message, "%coords%", BlockCoords.getCoords(location));
		return replaceColorCode(message);
	}

	public static String getConsoleScriptRemoveMessage(String name, ScriptType scriptType, Location location) {
		if (!isConsoleLog()) {
			return null;
		}
		String message = getString("consoleScriptRemoveMessage");
		message = replace(message, "%player%", name);
		message = replace(message, "%scripttype%", scriptType.getType());
		message = replace(message, "%world%", location.getWorld().getName());
		message = replace(message, "%coords%", BlockCoords.getCoords(location));
		return replaceColorCode(message);
	}

	public static String getConsoleScriptViewMessage(String name, ScriptType scriptType, Location location) {
		if (!isConsoleLog()) {
			return null;
		}
		String message = getString("consoleScriptViewMessage");
		message = replace(message, "%player%", name);
		message = replace(message, "%scripttype%", scriptType.getType());
		message = replace(message, "%world%", location.getWorld().getName());
		message = replace(message, "%coords%", BlockCoords.getCoords(location));
		return replaceColorCode(message);
	}

	public static String getConsoleWorldEditPasteMessage(ScriptType scriptType, Location min, Location max) {
		if (!isConsoleLog()) {
			return null;
		}
		String world = min.getWorld().getName();
		String minCoords = BlockCoords.getCoords(min);
		String maxCoords = BlockCoords.getCoords(max);
		String message = getString("consoleWorldEditPasteMessage");
		message = replace(message, "%scripttype%", scriptType.getType());
		message = replace(message, "%world%", world);
		message = replace(message, "%mincoords%", minCoords);
		message = replace(message, "%maxcoords%", maxCoords);
		return replaceColorCode(message);
	}

	public static String getConsoleWorldEditRemoveMessage(String scriptType, Location min, Location max) {
		if (!isConsoleLog()) {
			return null;
		}
		String world = min.getWorld().getName();
		String minCoords = BlockCoords.getCoords(min);
		String maxCoords = BlockCoords.getCoords(max);
		String message = getString("consoleWorldEditRemoveMessage");
		message = replace(message, "%scripttype%", scriptType);
		message = replace(message, "%world%", world);
		message = replace(message, "%mincoords%", minCoords);
		message = replace(message, "%maxcoords%", maxCoords);
		return replaceColorCode(message);
	}

	public static String getConsoleSuccScriptExecMessage(String name, ScriptType scriptType, Location location) {
		if (!isConsoleLog()) {
			return null;
		}
		String message = getString("consoleSuccScriptExecMessage");
		message = replace(message, "%player%", name);
		message = replace(message, "%scripttype%", scriptType.getType());
		message = replace(message, "%world%", location.getWorld().getName());
		message = replace(message, "%coords%", BlockCoords.getCoords(location));
		return replaceColorCode(message);
	}

	public static String getConsoleErrorScriptExecMessage(String name, ScriptType scriptType, Location location) {
		if (!isConsoleLog()) {
			return null;
		}
		String message = getString("message");
		message = replace(message, "%player%", name);
		message = replace(message, "%scripttype%", scriptType.getType());
		message = replace(message, "%world%", location.getWorld().getName());
		message = replace(message, "%coords%", BlockCoords.getCoords(location));
		return replaceColorCode(message);
	}

	private static List<String> getStringList(String key) {
		@SuppressWarnings("unchecked")
		List<String> list = (List<String>) datas.get(key);
		return list;
	}

	private static String getString(String key) {
		return (String) datas.get(key);
	}

	private static Boolean getBoolean(String key) {
		return (boolean) datas.get(key);
	}

	private static double getDouble(String key) {
		return (double) datas.get(key);
	}

	private static String replaceColorCode(String text) {
		return replace(text, "&", "§");
	}

	private static List<String> replaceColorCode(List<String> list) {
		if (list == null || list.size() == 0) {
			return list;
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