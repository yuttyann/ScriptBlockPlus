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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.enums.ActionType;
import com.github.yuttyann.scriptblockplus.file.yaml.YamlConfig;
import com.github.yuttyann.scriptblockplus.region.CuboidRegionBlocks;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

/*
　情報量が多すぎるのと、綺麗には見えないため今後作り直す予定です。
 */
public final class SBConfig {

	private static final Map<String, Object> DATAS = new HashMap<>();

	public static void reload() {
		reloadConfig();
		reloadLang();
	}

	public static void reloadConfig() {
		YamlConfig yaml = Files.getConfig();
		put(yaml, "SBP-API-Version", true);
		put(yaml, "UpdateChecker", true);
		put(yaml, "AutoDownload", true);
		put(yaml, "OpenChangeLog", true);
		put(yaml, "Language", "en");
		put(yaml, "ConsoleLog", false);
		put(yaml, "SortScripts", true);
		put(yaml, "OptionPermission", false);
		put(yaml, "CommandSelector", false);
		put(yaml, "Actions.InteractLeft", true);
		put(yaml, "Actions.InteractRight", true);
	}

	public static void reloadLang() {
		YamlConfig yaml = Files.getLang();
		put(yaml, "BlockSelector");
		put(yaml, "ScriptEditor");
		put(yaml, "ToolCommandMessage");
		put(yaml, "ReloadCommandMessage");
		put(yaml, "CheckVerCommandMessage");
		put(yaml, "BackupCommandMessage");
		put(yaml, "DataMigrCommandMessage");
		put(yaml, "ExportCommandMessage");
		put(yaml, "CreateCommandMessage");
		put(yaml, "AddCommandMessage");
		put(yaml, "RemoveCommandMessage");
		put(yaml, "ViewCommandMessage");
		put(yaml, "RunCommandMessage");
		put(yaml, "SelectorPasteCommandMessage");
		put(yaml, "SelectorRemoveCommandMessage");
		put(yaml, "NotVaultMessage");
		put(yaml, "SenderNoPlayerMessage");
		put(yaml, "NotPermissionMessage");
		put(yaml, "GiveToolMessage");
		put(yaml, "AllFileReloadMessage");
		put(yaml, "ScriptsBackupMessage");
		put(yaml, "ErrorScriptsBackupMessage");
		put(yaml, "NotLatestPluginMessage");
		put(yaml, "NotScriptBlockFileMessage");
		put(yaml, "DataMigrStartMessage");
		put(yaml, "DataMigrEndMessage");
		put(yaml, "ExportStartMessage");
		put(yaml, "ExportEndMessage");
		put(yaml, "UpdateDownloadStartMessage");
		put(yaml, "UpdateDownloadEndMessage");
		put(yaml, "UpdateCheckMessage");
		put(yaml, "UpdateErrorMessage");
		put(yaml, "ScriptCopyMessage");
		put(yaml, "ScriptPasteMessage");
		put(yaml, "ScriptCreateMessage");
		put(yaml, "ScriptAddMessage");
		put(yaml, "ScriptRemoveMessage");
		put(yaml, "NotSelectionMessage");
		put(yaml, "SelectorPos1Message");
		put(yaml, "SelectorPos2Message");
		put(yaml, "SelectorPasteMessage");
		put(yaml, "SelectorRemoveMessage");
		put(yaml, "OptionFailedToExecuteMessage");
		put(yaml, "ActiveDelayMessage");
		put(yaml, "ActiveCooldownMessage");
		put(yaml, "SuccActionDataMessage");
		put(yaml, "ErrorActionDataMessage");
		put(yaml, "ErrorScriptCheckMessage");
		put(yaml, "ErrorScriptFileCheckMessage");
		put(yaml, "ErrorScriptExecMessage");
		put(yaml, "ErrorGroupMessage");
		put(yaml, "ErrorHandMessage");
		put(yaml, "ErrorItemMessage");
		put(yaml, "ErrorCostMessage");
		put(yaml, "ConsoleScriptCopyMessage");
		put(yaml, "ConsoleScriptPasteMessage");
		put(yaml, "ConsoleScriptCreateMessage");
		put(yaml, "ConsoleScriptAddMessage");
		put(yaml, "ConsoleScriptRemoveMessage");
		put(yaml, "ConsoleScriptViewMessage");
		put(yaml, "ConsoleSelectorPasteMessage");
		put(yaml, "ConsoleSelectorPasteMessage");
		put(yaml, "ConsoleSuccScriptExecMessage");
		put(yaml, "ConsoleErrorScriptExecMessage");
	}

	public static Map<String, Object> getDatas() {
		return Collections.unmodifiableMap(DATAS);
	}

	public static boolean isSBPAPIVersion() {
		return get("SBP-API-Version");
	}

	public static boolean isUpdateChecker() {
		return get("UpdateChecker");
	}

	public static boolean isAutoDownload() {
		return get("AutoDownload");
	}

	public static boolean isOpenChangeLog() {
		return get("OpenChangeLog");
	}

	@NotNull
	public static String getLanguage() {
		return get("Language");
	}

	public static boolean isConsoleLog() {
		return get("ConsoleLog");
	}

	public static boolean isSortScripts() {
		return get("SortScripts");
	}

	public static boolean isOptionPermission() {
		return get("OptionPermission");
	}

	public static boolean isCommandSelector() {
		return get("CommandSelector");
	}

	public static boolean isLeftClick() {
		return get("Actions.InteractLeft");
	}

	public static boolean isRightClick() {
		return get("Actions.InteractRight");
	}

	@NotNull
	public static List<String> getBlockSelectorLore() {
		List<String> list = get("BlockSelector") == null ? new ArrayList<>() : new ArrayList<>(get("BlockSelector"));
		for (int i = 0; i < list.size(); i++) {
			list.set(i, replaceColorCode(list.get(i)));
		}
		return list;
	}

	@NotNull
	public static List<String> getScriptEditorLore(ScriptType scriptType) {
		List<String> list = get("ScriptEditor") == null ? new ArrayList<>() : new ArrayList<>(get("ScriptEditor"));
		for (int i = 0; i < list.size(); i++) {
			list.set(i, replaceColorCode(replace(list.get(i), "%scripttype%", scriptType.name())));
		}
		return list;
	}

	@Nullable
	public static String getToolCommandMessage() {
		return ChatColor.stripColor(get("ToolCommandMessage"));
	}

	@Nullable
	public static String getReloadCommandMessage() {
		return ChatColor.stripColor(get("ReloadCommandMessage"));
	}

	@Nullable
	public static String getBackupCommandMessage() {
		return ChatColor.stripColor(get("BackupCommandMessage"));
	}

	@Nullable
	public static String getCheckVerCommandMessage() {
		return ChatColor.stripColor(get("CheckVerCommandMessage"));
	}

	@Nullable
	public static String getDataMigrCommandMessage() {
		return ChatColor.stripColor(get("DataMigrCommandMessage"));
	}

	@Nullable
	public static String getExportCommandMessage() {
		return ChatColor.stripColor(get("ExportCommandMessage"));
	}

	@Nullable
	public static String getCreateCommandMessage() {
		return ChatColor.stripColor(get("CreateCommandMessage"));
	}

	@Nullable
	public static String getAddCommandMessage() {
		return ChatColor.stripColor(get("AddCommandMessage"));
	}

	@Nullable
	public static String getRemoveCommandMessage() {
		return ChatColor.stripColor(get("RemoveCommandMessage"));
	}

	@Nullable
	public static String getViewCommandMessage() {
		return ChatColor.stripColor(get("ViewCommandMessage"));
	}

	@Nullable
	public static String getRunCommandMessage() {
		return ChatColor.stripColor(get("RunCommandMessage"));
	}

	@Nullable
	public static String getSelectorPasteCommandMessage() {
		return ChatColor.stripColor(get("SelectorPasteCommandMessage"));
	}

	@Nullable
	public static String getSelectorRemoveCommandMessage() {
		return ChatColor.stripColor(get("SelectorRemoveCommandMessage"));
	}

	@Nullable
	public static String getNotVaultMessage() {
		return replaceColorCode(get("NotVaultMessage"));
	}

	@Nullable
	public static String getSenderNoPlayerMessage() {
		return replaceColorCode(get("SenderNoPlayerMessage"));
	}

	@Nullable
	public static String getNotPermissionMessage() {
		return replaceColorCode(get("NotPermissionMessage"));
	}

	@Nullable
	public static String getGiveToolMessage() {
		return replaceColorCode(get("GiveToolMessage"));
	}

	@Nullable
	public static String getAllFileReloadMessage() {
		return replaceColorCode(get("AllFileReloadMessage"));
	}

	@Nullable
	public static String getNotLatestPluginMessage() {
		return replaceColorCode(get("NotLatestPluginMessage"));
	}

	@Nullable
	public static String getNotScriptBlockFileMessage() {
		return replaceColorCode(get("NotScriptBlockFileMessage"));
	}

	@Nullable
	public static String getScriptsBackupMessage() {
		return replaceColorCode(get("ScriptsBackupMessage"));
	}

	@Nullable
	public static String getErrorScriptsBackupMessage() {
		return replaceColorCode(get("ErrorScriptsBackupMessage"));
	}

	@Nullable
	public static String getDataMigrStartMessage() {
		return replaceColorCode(get("DataMigrStartMessage"));
	}

	@Nullable
	public static String getDataMigrEndMessage() {
		return replaceColorCode(get("DataMigrEndMessage"));
	}

	@Nullable
	public static String getExportStartMessage(@NotNull String name) {
		return replaceColorCode(replace(get("ExportStartMessage"), "%name%", name));
	}

	@Nullable
	public static String getExportEndMessage(@NotNull String name) {
		return replaceColorCode(replace(get("ExportEndMessage"), "%name%", name));
	}

	@Nullable
	public static String getUpdateDownloadStartMessage() {
		return replaceColorCode(get("UpdateDownloadStartMessage"));
	}

	@Nullable
	public static String getUpdateDownloadEndMessage(@NotNull String fileName, @NotNull String filePath, @NotNull String fileSize) {
		String message = get("UpdateDownloadEndMessage");
		message = replace(message, "%filename%", fileName);
		message = replace(message, "%filepath%", filePath);
		message = replace(message, "%filesize%", fileSize);
		return replaceColorCode(message);
	}

	@Nullable
	public static String getUpdateCheckMessages(@NotNull String pluginName, @NotNull String latestVersion, @NotNull List<String> details) {
		String message = get("UpdateCheckMessage");
		message = replace(message, "%pluginname%", pluginName);
		message = replace(message, "%latestversion%", latestVersion);
		if (message.indexOf("%details%") >= 0) {
			StrBuilder builder = new StrBuilder(details.size());
			for (int i = 0; i < details.size(); i++) {
				boolean isTree = details.get(i).startsWith("$");
				String info = StringUtils.removeStart(details.get(i), "$");
				builder.append(isTree ? "  - " : "・").append(info).append(i == (details.size() - 1) ? "" : "|~");
			}
			message = replace(message, "%details%", builder.toString());
		}
		return replaceColorCode(message);
	}

	@Nullable
	public static String getErrorUpdateMessage() {
		return replaceColorCode(get("ErrorUpdateMessage"));
	}

	@Nullable
	public static String getScriptCopyMessage(@NotNull ScriptType scriptType) {
		return replaceColorCode(replace(get("ScriptCopyMessage"), "%scripttype%", scriptType.getType()));
	}

	@Nullable
	public static String getScriptPasteMessage(@NotNull ScriptType scriptType) {
		return replaceColorCode(replace(get("ScriptPasteMessage"), "%scripttype%", scriptType.getType()));
	}

	@Nullable
	public static String getScriptCreateMessage(@NotNull ScriptType scriptType) {
		return replaceColorCode(replace(get("ScriptCreateMessage"), "%scripttype%", scriptType.getType()));
	}

	@Nullable
	public static String getScriptAddMessage(@NotNull ScriptType scriptType) {
		return replaceColorCode(replace(get("ScriptAddMessage"), "%scripttype%", scriptType.getType()));
	}

	@Nullable
	public static String getScriptRemoveMessage(@NotNull ScriptType scriptType) {
		return replaceColorCode(replace(get("ScriptRemoveMessage"), "%scripttype%", scriptType.getType()));
	}

	@Nullable
	public static String getNotSelectionMessage() {
		return replaceColorCode(get("NotSelectionMessage"));
	}

	@Nullable
	public static String getSelectorPos1Message(@NotNull Location pos1) {
		String message = get("SelectorPos1Message");
		message = replace(message, "%world%", pos1.getWorld());
		message = replace(message, "%coords%", BlockCoords.getCoords(pos1));
		return replaceColorCode(message);
	}

	@Nullable
	public static String getSelectorPos2Message(@NotNull Location pos2) {
		String message = get("SelectorPos2Message");
		message = replace(message, "%world%", pos2.getWorld());
		message = replace(message, "%coords%", BlockCoords.getCoords(pos2));
		return replaceColorCode(message);
	}

	@Nullable
	public static String getSelectorPasteMessage(@NotNull ScriptType scriptType, @NotNull CuboidRegionBlocks regionBlocks) {
		String message = get("SelectorPasteMessage");
		message = replace(message, "%scripttype%", scriptType.getType());
		message = replace(message, "%blockcount%", regionBlocks.getCount());
		return replaceColorCode(message);
	}

	@Nullable
	public static String getSelectorRemoveMessage(@NotNull String scriptType, @NotNull CuboidRegionBlocks regionBlocks) {
		String message = get("SelectorRemoveMessage");
		message = replace(message, "%scripttype%", scriptType);
		message = replace(message, "%blockcount%", regionBlocks.getCount());
		return replaceColorCode(message);
	}

	@Nullable
	public static String getOptionFailedToExecuteMessage(@NotNull Option option, @NotNull Throwable throwable) {
		String message = get("OptionFailedToExecuteMessage");
		String throwableMessage = throwable.getMessage() == null ? "" : " \"" + throwable.getMessage() + "\"";
		message = replace(message, "%option%", option.getName());
		message = replace(message, "%cause%", throwable.getClass().getSimpleName() + throwableMessage);
		return replaceColorCode(message);
	}

	@Nullable
	public static String getActiveDelayMessage() {
		return replaceColorCode(get("ActiveDelayMessage"));
	}

	@Nullable
	public static String getActiveCooldownMessage(short hour, byte minute, byte second) {
		String message = get("ActiveCooldownMessage");
		message = replace(message, "%hour%", hour);
		message = replace(message, "%minute%", minute);
		message = replace(message, "%second%", second);
		return replaceColorCode(message);
	}

	@Nullable
	public static String getSuccActionDataMessage(@NotNull ScriptType scriptType, @NotNull ActionType actionType) {
		String type = scriptType.getType() + "-" + actionType.name().toLowerCase();
		return replaceColorCode(replace(get("SuccActionDataMessage"), "%actiontype%", type));
	}

	@Nullable
	public static String getErrorActionDataMessage() {
		return replaceColorCode(get("ErrorActionDataMessage"));
	}

	@Nullable
	public static String getErrorScriptCheckMessage() {
		return replaceColorCode(get("ErrorScriptCheckMessage"));
	}

	@Nullable
	public static String getErrorScriptFileCheckMessage() {
		return replaceColorCode(get("ErrorScriptFileCheckMessage"));
	}

	@Nullable
	public static String getErrorScriptMessage(@NotNull ScriptType scriptType) {
		return replaceColorCode(replace(get("ErrorScriptExecMessage"), "%scripttype%", scriptType.getType()));
	}

	@Nullable
	public static String getErrorGroupMessage(@NotNull String group) {
		return replaceColorCode(replace(get("ErrorGroupMessage"), "%group%", group));
	}

	@Nullable
	public static String getErrorHandMessage(@NotNull Material type, int amount, int damage, @Nullable String name) {
		String message = get("ErrorHandMessage");
		message = replace(message, "%material%", type.toString());
		message = replace(message, "%amount%", amount);
		message = replace(message, "%damage%", damage);
		message = replace(message, "%itemname%", StringUtils.isNotEmpty(name) ? name : type.toString());
		return replaceColorCode(message);
	}

	@Nullable
	public static String getErrorItemMessage(@NotNull Material type, int amount, int damage, @Nullable String name) {
		String message = get("ErrorItemMessage");
		message = replace(message, "%material%", String.valueOf(type));
		message = replace(message, "%amount%", amount);
		message = replace(message, "%damage%", damage);
		message = replace(message, "%itemname%", StringUtils.isNotEmpty(name) ? name : String.valueOf(type));
		return replaceColorCode(message);
	}

	@Nullable
	public static String getErrorCostMessage(double cost, double result) {
		String message = get("ErrorCostMessage");
		message = replace(message, "%cost%", cost);
		message = replace(message, "%result%", result);
		return replaceColorCode(message);
	}

	@Nullable
	public static String getConsoleScriptCopyMessage(@NotNull String name, @NotNull ScriptType scriptType, @NotNull Location location) {
		if (!isConsoleLog()) {
			return null;
		}
		String message = get("ConsoleScriptCopyMessage");
		message = replace(message, "%player%", name);
		message = replace(message, "%scripttype%", scriptType.getType());
		message = replace(message, "%world%", location.getWorld().getName());
		message = replace(message, "%coords%", BlockCoords.getCoords(location));
		return replaceColorCode(message);
	}

	@Nullable
	public static String getConsoleScriptPasteMessage(@NotNull String name, @NotNull ScriptType scriptType, @NotNull Location location) {
		if (!isConsoleLog()) {
			return null;
		}
		String message = get("ConsoleScriptPasteMessage");
		message = replace(message, "%player%", name);
		message = replace(message, "%scripttype%", scriptType.getType());
		message = replace(message, "%world%", location.getWorld().getName());
		message = replace(message, "%coords%", BlockCoords.getCoords(location));
		return replaceColorCode(message);
	}

	@Nullable
	public static String getConsoleScriptCreateMessage(String name, ScriptType scriptType, Location location) {
		if (!isConsoleLog()) {
			return null;
		}
		String message = get("ConsoleScriptCreateMessage");
		message = replace(message, "%player%", name);
		message = replace(message, "%scripttype%", scriptType.getType());
		message = replace(message, "%world%", location.getWorld().getName());
		message = replace(message, "%coords%", BlockCoords.getCoords(location));
		return replaceColorCode(message);
	}

	@Nullable
	public static String getConsoleScriptAddMessage(@NotNull String name, @NotNull ScriptType scriptType, @NotNull Location location) {
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

	@Nullable
	public static String getConsoleScriptRemoveMessage(@NotNull String name, @NotNull ScriptType scriptType, @NotNull Location location) {
		if (!isConsoleLog()) {
			return null;
		}
		String message = get("ConsoleScriptRemoveMessage");
		message = replace(message, "%player%", name);
		message = replace(message, "%scripttype%", scriptType.getType());
		message = replace(message, "%world%", location.getWorld().getName());
		message = replace(message, "%coords%", BlockCoords.getCoords(location));
		return replaceColorCode(message);
	}

	@Nullable
	public static String getConsoleScriptViewMessage(@NotNull String name, @NotNull ScriptType scriptType, @NotNull Location location) {
		if (!isConsoleLog()) {
			return null;
		}
		String message = get("ConsoleScriptViewMessage");
		message = replace(message, "%player%", name);
		message = replace(message, "%scripttype%", scriptType.getType());
		message = replace(message, "%world%", location.getWorld().getName());
		message = replace(message, "%coords%", BlockCoords.getCoords(location));
		return replaceColorCode(message);
	}

	@Nullable
	public static String getConsoleSelectorPasteMessage(@NotNull ScriptType scriptType, @NotNull CuboidRegionBlocks regionBlocks) {
		if (!isConsoleLog()) {
			return null;
		}
		String message = get("ConsoleSelectorPasteMessage");
		message = replace(message, "%scripttype%", scriptType.getType());
		message = replace(message, "%blockcount%", regionBlocks.getCount());
		message = replace(message, "%world%", regionBlocks.getWorld().getName());
		message = replace(message, "%mincoords%", BlockCoords.getCoords(regionBlocks.getMinimumPoint()));
		message = replace(message, "%maxcoords%", BlockCoords.getCoords(regionBlocks.getMinimumPoint()));
		return replaceColorCode(message);
	}

	@Nullable
	public static String getConsoleSelectorRemoveMessage(@NotNull String scriptType, @NotNull CuboidRegionBlocks regionBlocks) {
		if (!isConsoleLog()) {
			return null;
		}
		String message = get("ConsoleSelectorRemoveMessage");
		message = replace(message, "%scripttype%", scriptType);
		message = replace(message, "%blockcount%", regionBlocks.getCount());
		message = replace(message, "%world%", regionBlocks.getWorld().getName());
		message = replace(message, "%mincoords%", BlockCoords.getCoords(regionBlocks.getMinimumPoint()));
		message = replace(message, "%maxcoords%", BlockCoords.getCoords(regionBlocks.getMinimumPoint()));
		return replaceColorCode(message);
	}

	@Nullable
	public static String getConsoleSuccScriptExecMessage(@NotNull String name, @NotNull ScriptType scriptType, @NotNull Location location) {
		if (!isConsoleLog()) {
			return null;
		}
		String message = get("ConsoleSuccScriptExecMessage");
		message = replace(message, "%player%", name);
		message = replace(message, "%scripttype%", scriptType.getType());
		message = replace(message, "%world%", location.getWorld().getName());
		message = replace(message, "%coords%", BlockCoords.getCoords(location));
		return replaceColorCode(message);
	}

	@Nullable
	public static String getConsoleErrorScriptExecMessage(@NotNull String name, @NotNull ScriptType scriptType, @NotNull Location location) {
		if (!isConsoleLog()) {
			return null;
		}
		String message = get("ConsoleErrorScriptExecMessage");
		message = replace(message, "%player%", name);
		message = replace(message, "%scripttype%", scriptType.getType());
		message = replace(message, "%world%", location.getWorld().getName());
		message = replace(message, "%coords%", BlockCoords.getCoords(location));
		return replaceColorCode(message);
	}

	private static void put(@NotNull YamlConfig yaml, @NotNull String key) {
		put(yaml, key, null);
	}

	private static void put(@NotNull YamlConfig yaml, @NotNull String key, @Nullable Object def) {
		DATAS.put(key, def == null ? yaml.get(key) : yaml.get(key, def));
	}

	@Nullable
	private static <T> T get(@NotNull String key) {
		return (T) DATAS.get(key);
	}

	@Nullable
	private static String replaceColorCode(@Nullable String source) {
		return StringUtils.replaceColorCode(source, false);
	}

	@NotNull
	private static String replace(@NotNull String source, @NotNull String search, @Nullable Object replace) {
		return StringUtils.replace(source, search, replace == null ? null : replace.toString());
	}
}