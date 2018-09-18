package com.github.yuttyann.scriptblockplus.file;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.text.StrBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.enums.ActionType;
import com.github.yuttyann.scriptblockplus.file.yaml.YamlConfig;
import com.github.yuttyann.scriptblockplus.region.CuboidRegionBlocks;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public final class SBConfig {

	private static final Map<String, Object> DATAS = new HashMap<>();

	public static void reload() {
		reloadConfig();
		reloadLang();
	}

	public static void reloadConfig() {
		YamlConfig yaml = Files.getConfig();
		if (yaml == null) {
			throw new NullPointerException();
		}
		put(yaml, "updateChecker", true);
		put(yaml, "autoDownload", true);
		put(yaml, "openChangeLog", true);
		put(yaml, "language", "en");
		put(yaml, "consoleLog", false);
		put(yaml, "sortScripts", true);
		put(yaml, "optionPermission", false);
		put(yaml, "clickActions.interactLeft", true);
		put(yaml, "clickActions.interactRight", true);
	}

	public static void reloadLang() {
		YamlConfig yaml = Files.getLang();
		if (yaml == null) {
			throw new NullPointerException();
		}
		put(yaml, "blockSelector");
		put(yaml, "scriptEditor");
		put(yaml, "toolCommandMessage");
		put(yaml, "reloadCommandMessage");
		put(yaml, "checkVerCommandMessage");
		put(yaml, "backupCommandMessage");
		put(yaml, "dataMigrCommandMessage");
		put(yaml, "createCommandMessage");
		put(yaml, "addCommandMessage");
		put(yaml, "removeCommandMessage");
		put(yaml, "viewCommandMessage");
		put(yaml, "selectorPasteCommandMessage");
		put(yaml, "selectorRemoveCommandMessage");
		put(yaml, "notVaultMessage");
		put(yaml, "senderNoPlayerMessage");
		put(yaml, "notPermissionMessage");
		put(yaml, "giveToolMessage");
		put(yaml, "allFileReloadMessage");
		put(yaml, "scriptsBackupMessage");
		put(yaml, "errorScriptsBackupMessage");
		put(yaml, "notLatestPluginMessage");
		put(yaml, "notScriptBlockFileMessage");
		put(yaml, "dataMigrStartMessage");
		put(yaml, "dataMigrEndMessage");
		put(yaml, "updateDownloadStartMessage");
		put(yaml, "updateDownloadEndMessage");
		put(yaml, "updateCheckMessage");
		put(yaml, "updateErrorMessage");
		put(yaml, "scriptCopyMessage");
		put(yaml, "scriptPasteMessage");
		put(yaml, "scriptCreateMessage");
		put(yaml, "scriptAddMessage");
		put(yaml, "scriptRemoveMessage");
		put(yaml, "notSelectionMessage");
		put(yaml, "selectorPos1Message");
		put(yaml, "selectorPos2Message");
		put(yaml, "selectorPasteMessage");
		put(yaml, "selectorRemoveMessage");
		put(yaml, "optionFailedToExecuteMessage");
		put(yaml, "activeDelayMessage");
		put(yaml, "activeCooldownMessage");
		put(yaml, "succActionDataMessage");
		put(yaml, "errorEditDataMessage");
		put(yaml, "errorScriptCheckMessage");
		put(yaml, "errorScriptFileCheckMessage");
		put(yaml, "errorScriptExecMessage");
		put(yaml, "errorGroupMessage");
		put(yaml, "errorHandMessage");
		put(yaml, "errorItemMessage");
		put(yaml, "errorCostMessage");
		put(yaml, "consoleScriptCopyMessage");
		put(yaml, "consoleScriptPasteMessage");
		put(yaml, "consoleScriptCreateMessage");
		put(yaml, "consoleScriptAddMessage");
		put(yaml, "consoleScriptRemoveMessage");
		put(yaml, "consoleScriptViewMessage");
		put(yaml, "consoleSelectorPasteMessage");
		put(yaml, "consoleSelectorPasteMessage");
		put(yaml, "consoleSuccScriptExecMessage");
		put(yaml, "consoleErrorScriptExecMessage");
	}

	public static Map<String, Object> getDatas() {
		return Collections.unmodifiableMap(DATAS);
	}

	public static boolean isUpdateChecker() {
		return get("updateChecker");
	}

	public static boolean isAutoDownload() {
		return get("autoDownload");
	}

	public static boolean isOpenChangeLog() {
		return get("openChangeLog");
	}

	public static String getLanguage() {
		return get("language");
	}

	public static boolean isConsoleLog() {
		return get("consoleLog");
	}

	public static boolean isSortScripts() {
		return get("sortScripts");
	}

	public static boolean isOptionPermission() {
		return get("optionPermission");
	}

	public static boolean isLeftClick() {
		return get("clickActions.interactLeft");
	}

	public static boolean isRightClick() {
		return get("clickActions.interactRight");
	}

	public static List<String> getBlockSelectorLore() {
		List<String> list = get("blockSelector") == null ? new ArrayList<>() : new ArrayList<>(get("blockSelector"));
		for (int i = 0; i < list.size(); i++) {
			list.set(i, replaceColorCode(list.get(i)));
		}
		return list;
	}

	public static List<String> getScriptEditorLore(ScriptType scriptType) {
		List<String> list = get("scriptEditor") == null ? new ArrayList<>() : new ArrayList<>(get("scriptEditor"));
		for (int i = 0; i < list.size(); i++) {
			list.set(i, replaceColorCode(replace(list.get(i), "%scripttype%", scriptType.name())));
		}
		return list;
	}

	public static String getToolCommandMessage() {
		return ChatColor.stripColor(get("toolCommandMessage"));
	}

	public static String getReloadCommandMessage() {
		return ChatColor.stripColor(get("reloadCommandMessage"));
	}

	public static String getBackupCommandMessage() {
		return ChatColor.stripColor(get("backupCommandMessage"));
	}

	public static String getCheckVerCommandMessage() {
		return ChatColor.stripColor(get("checkVerCommandMessage"));
	}

	public static String getDataMigrCommandMessage() {
		return ChatColor.stripColor(get("dataMigrCommandMessage"));
	}

	public static String getCreateCommandMessage() {
		return ChatColor.stripColor(get("createCommandMessage"));
	}

	public static String getAddCommandMessage() {
		return ChatColor.stripColor(get("addCommandMessage"));
	}

	public static String getRemoveCommandMessage() {
		return ChatColor.stripColor(get("removeCommandMessage"));
	}

	public static String getViewCommandMessage() {
		return ChatColor.stripColor(get("viewCommandMessage"));
	}

	public static String getSelectorPasteCommandMessage() {
		return ChatColor.stripColor(get("selectorPasteCommandMessage"));
	}

	public static String getSelectorRemoveCommandMessage() {
		return ChatColor.stripColor(get("selectorRemoveCommandMessage"));
	}

	public static String getNotVaultMessage() {
		return replaceColorCode(get("notVaultMessage"));
	}

	public static String getSenderNoPlayerMessage() {
		return replaceColorCode(get("senderNoPlayerMessage"));
	}

	public static String getNotPermissionMessage() {
		return replaceColorCode(get("notPermissionMessage"));
	}

	public static String getGiveToolMessage() {
		return replaceColorCode(get("giveToolMessage"));
	}

	public static String getAllFileReloadMessage() {
		return replaceColorCode(get("allFileReloadMessage"));
	}

	public static String getNotLatestPluginMessage() {
		return replaceColorCode(get("notLatestPluginMessage"));
	}

	public static String getNotScriptBlockFileMessage() {
		return replaceColorCode(get("notScriptBlockFileMessage"));
	}

	public static String getScriptsBackupMessage() {
		return replaceColorCode(get("scriptsBackupMessage"));
	}

	public static String getErrorScriptsBackupMessage() {
		return replaceColorCode(get("errorScriptsBackupMessage"));
	}

	public static String getDataMigrStartMessage() {
		return replaceColorCode(get("dataMigrStartMessage"));
	}

	public static String getDataMigrEndMessage() {
		return replaceColorCode(get("dataMigrEndMessage"));
	}

	public static String getUpdateDownloadStartMessage() {
		return replaceColorCode(get("updateDownloadStartMessage"));
	}

	public static String getUpdateDownloadEndMessage(String fileName, String filePath, String fileSize) {
		String message = get("updateDownloadEndMessage");
		message = replace(message, "%filename%", fileName);
		message = replace(message, "%filepath%", filePath);
		message = replace(message, "%filesize%", fileSize);
		return replaceColorCode(message);
	}

	public static String getUpdateCheckMessages(String pluginName, String latestVersion, List<String> details) {
		String message = get("updateCheckMessage");
		message = replace(message, "%pluginname%", pluginName);
		message = replace(message, "%latestversion%", latestVersion);
		if (message.indexOf("%details%") >= 0) {
			StrBuilder builder = new StrBuilder(details.size());
			for (int i = 0; i < details.size(); i++) {
				String info = details.get(i);
				boolean isTree = info.startsWith("$");
				info = isTree ? info.substring(1) : info;
				builder.append(isTree ? "  - " : "・").append(info).append(i == (details.size() - 1) ? "" : "|~");
			}
			message = replace(message, "%details%", builder.toString());
		}
		return replaceColorCode(message);
	}

	public static String getErrorUpdateMessage() {
		return replaceColorCode(get("errorUpdateMessage"));
	}

	public static String getScriptCopyMessage(ScriptType scriptType) {
		return replaceColorCode(replace(get("scriptCopyMessage"), "%scripttype%", scriptType.getType()));
	}

	public static String getScriptPasteMessage(ScriptType scriptType) {
		return replaceColorCode(replace(get("scriptPasteMessage"), "%scripttype%", scriptType.getType()));
	}

	public static String getScriptCreateMessage(ScriptType scriptType) {
		return replaceColorCode(replace(get("scriptCreateMessage"), "%scripttype%", scriptType.getType()));
	}

	public static String getScriptAddMessage(ScriptType scriptType) {
		return replaceColorCode(replace(get("scriptAddMessage"), "%scripttype%", scriptType.getType()));
	}

	public static String getScriptRemoveMessage(ScriptType scriptType) {
		return replaceColorCode(replace(get("scriptRemoveMessage"), "%scripttype%", scriptType.getType()));
	}

	public static String getNotSelectionMessage() {
		return replaceColorCode(get("notSelectionMessage"));
	}

	public static String getSelectorPos1Message(Location pos1) {
		String message = get("selectorPos1Message");
		message = replace(message, "%world%", pos1.getWorld());
		message = replace(message, "%coords%", BlockCoords.getCoords(pos1));
		return replaceColorCode(message);
	}

	public static String getSelectorPos2Message(Location pos2) {
		String message = get("selectorPos2Message");
		message = replace(message, "%world%", pos2.getWorld());
		message = replace(message, "%coords%", BlockCoords.getCoords(pos2));
		return replaceColorCode(message);
	}

	public static String getSelectorPasteMessage(ScriptType scriptType, CuboidRegionBlocks regionBlocks) {
		String message = get("selectorPasteMessage");
		message = replace(message, "%scripttype%", scriptType.getType());
		message = replace(message, "%blockcount%", regionBlocks.getCount());
		return replaceColorCode(message);
	}

	public static String getSelectorRemoveMessage(String scriptType, CuboidRegionBlocks regionBlocks) {
		String message = get("selectorRemoveMessage");
		message = replace(message, "%scripttype%", scriptType);
		message = replace(message, "%blockcount%", regionBlocks.getCount());
		return replaceColorCode(message);
	}

	public static String getOptionFailedToExecuteMessage(Option option, Throwable throwable) {
		String message = get("optionFailedToExecuteMessage");
		String throwableMessage = throwable.getMessage() == null ? "" : " (" + throwable.getMessage() + ")";
		message = replace(message, "%option%", option.getName());
		message = replace(message, "%cause%", throwable.getClass().getSimpleName() + throwableMessage);
		return replaceColorCode(message);
	}

	public static String getActiveDelayMessage() {
		return replaceColorCode(get("activeDelayMessage"));
	}

	public static String getActiveCooldownMessage(short hour, byte minute, byte second) {
		String message = get("activeCooldownMessage");
		message = replace(message, "%hour%", hour);
		message = replace(message, "%minute%", minute);
		message = replace(message, "%second%", second);
		return replaceColorCode(message);
	}

	public static String getSuccActionDataMessage(ScriptType scriptType, ActionType actionType) {
		String type = scriptType.getType() + "-"  + actionType.name().toLowerCase();
		return replaceColorCode(replace(get("succActionDataMessage"), "%actiontype%", type));
	}

	public static String getErrorEditDataMessage() {
		return replaceColorCode(get("errorEditDataMessage"));
	}

	public static String getErrorScriptCheckMessage() {
		return replaceColorCode(get("errorScriptCheckMessage"));
	}

	public static String getErrorScriptFileCheckMessage() {
		return replaceColorCode(get("errorScriptFileCheckMessage"));
	}

	public static String getErrorScriptMessage(ScriptType scriptType) {
		return replaceColorCode(replace(get("errorScriptExecMessage"), "%scripttype%", scriptType.getType()));
	}

	public static String getErrorGroupMessage(String group) {
		return replaceColorCode(replace(get("errorGroupMessage"), "%group%", group));
	}

	public static String getErrorHandMessage(Material type, int amount, int damage, String name) {
		String message = get("errorHandMessage");
		message = replace(message, "%material%", type.toString());
		message = replace(message, "%amount%", amount);
		message = replace(message, "%damage%", damage);
		message = replace(message, "%itemname%", StringUtils.isNotEmpty(name) ? name : type.toString());
		return replaceColorCode(message);
	}

	public static String getErrorItemMessage(Material type, int amount, int damage, String name) {
		String message = get("errorItemMessage");
		message = replace(message, "%material%", type.toString());
		message = replace(message, "%amount%", amount);
		message = replace(message, "%damage%", damage);
		message = replace(message, "%itemname%", StringUtils.isNotEmpty(name) ? name : type.toString());
		return replaceColorCode(message);
	}

	public static String getErrorCostMessage(double cost, double result) {
		String message = get("errorCostMessage");
		message = replace(message, "%cost%", cost);
		message = replace(message, "%result%", result);
		return replaceColorCode(message);
	}

	public static String getConsoleScriptCopyMessage(String name, ScriptType scriptType, Location location) {
		if (!isConsoleLog()) {
			return null;
		}
		String message = get("consoleScriptCopyMessage");
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
		String message = get("consoleScriptPasteMessage");
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
		String message = get("consoleScriptCreateMessage");
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
		String message = get("consoleScriptAddMessage");
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
		String message = get("consoleScriptRemoveMessage");
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
		String message = get("consoleScriptViewMessage");
		message = replace(message, "%player%", name);
		message = replace(message, "%scripttype%", scriptType.getType());
		message = replace(message, "%world%", location.getWorld().getName());
		message = replace(message, "%coords%", BlockCoords.getCoords(location));
		return replaceColorCode(message);
	}

	public static String getConsoleSelectorPasteMessage(ScriptType scriptType, CuboidRegionBlocks regionBlocks) {
		if (!isConsoleLog()) {
			return null;
		}
		String message = get("consoleSelectorPasteMessage");
		message = replace(message, "%scripttype%", scriptType.getType());
		message = replace(message, "%blockcount%", regionBlocks.getCount());
		message = replace(message, "%world%", regionBlocks.getWorld().getName());
		message = replace(message, "%mincoords%", BlockCoords.getCoords(regionBlocks.getMinimumPoint()));
		message = replace(message, "%maxcoords%", BlockCoords.getCoords(regionBlocks.getMinimumPoint()));
		return replaceColorCode(message);
	}

	public static String getConsoleSelectorRemoveMessage(String scriptType, CuboidRegionBlocks regionBlocks) {
		if (!isConsoleLog()) {
			return null;
		}
		String message = get("consoleSelectorRemoveMessage");
		message = replace(message, "%scripttype%", scriptType);
		message = replace(message, "%blockcount%", regionBlocks.getCount());
		message = replace(message, "%world%", regionBlocks.getWorld().getName());
		message = replace(message, "%mincoords%", BlockCoords.getCoords(regionBlocks.getMinimumPoint()));
		message = replace(message, "%maxcoords%", BlockCoords.getCoords(regionBlocks.getMinimumPoint()));
		return replaceColorCode(message);
	}

	public static String getConsoleSuccScriptExecMessage(String name, ScriptType scriptType, Location location) {
		if (!isConsoleLog()) {
			return null;
		}
		String message = get("consoleSuccScriptExecMessage");
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
		String message = get("message");
		message = replace(message, "%player%", name);
		message = replace(message, "%scripttype%", scriptType.getType());
		message = replace(message, "%world%", location.getWorld().getName());
		message = replace(message, "%coords%", BlockCoords.getCoords(location));
		return replaceColorCode(message);
	}

	private static void put(YamlConfig yaml, String key) {
		put(yaml, key, null);
	}

	private static void put(YamlConfig yaml, String key, Object def) {
		DATAS.put(key, def == null ? yaml.get(key) : yaml.get(key, def));
	}

	private static <T> T get(String key) {
		return (T) DATAS.get(key);
	}

	private static String replaceColorCode(String source) {
		return StringUtils.replaceColorCode(source, false);
	}

	private static String replace(String source, String search, Object replace) {
		return StringUtils.replace(source, search, replace == null ? null : replace.toString());
	}
}