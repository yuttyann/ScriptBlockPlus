package com.github.yuttyann.scriptblockplus.file;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.BlockLocation;
import com.github.yuttyann.scriptblockplus.enums.ClickType;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class Lang {

	private static Lang instance;
	private static YamlConfig lang;
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
	private String updateErrorMessage;
	private String notScriptBlockFileMessage;
	private String dataMigrStartMessage;
	private String dataMigrEndMessage;
	private String notWorldEditMessage;
	private String updateDownloadStartMessage;
	private List<String> updateDownloadEndMessages;
	private List<String> updateCheckMessages;
	private List<String> updateErrorMessages;
	private String scriptCopyMessage;
	private String scriptPasteMessage;
	private String scriptCreateMessage;
	private String scriptAddMessage;
	private String scriptRemoveMessage;
	private String worldEditNotSelectionMessage;
	private String worldEditPasteMessage;
	private String worldEditRemoveMessage;
	private String activeDelayMessage;
	private String activeCooldownMessage;
	private String succEditDataMessage;
	private String errorEditDataMessage;
	private String errorScriptCheckMessage;
	private String errorScriptFileCheckMessage;
	private String errorScriptExecMessage;
	private String errorGroupMessage;
	private String errorHandMessage;
	private String errorCostMessage;
	private String errorItemMessage;
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
	private boolean isConsoleLog;

	public Lang() {
		lang = Files.getLang();
		this.scriptEditorLore = lang.getStringList("ScriptEditor");
		this.toolCommandMessage = lang.getString("toolCommandMessage");
		this.reloadCommandMessage = lang.getString("reloadCommandMessage");
		this.checkVerCommandMessage = lang.getString("checkVerCommandMessage");
		this.dataMigrCommandMessage = lang.getString("dataMigrCommandMessage");
		this.createCommandMessage = lang.getString("createCommandMessage");
		this.addCommandMessage = lang.getString("addCommandMessage");
		this.removeCommandMessage = lang.getString("removeCommandMessage");
		this.viewCommandMessage = lang.getString("viewCommandMessage");
		this.worldEditPasteCommandMessage = lang.getString("worldEditPasteCommandMessage");
		this.worldEditRemoveCommandMessage = lang.getString("worldEditRemoveCommandMessage");
		this.notVaultMessage = lang.getString("notVaultMessage");
		this.senderNoPlayerMessage = lang.getString("senderNoPlayerMessage");
		this.notPermissionMessage = lang.getString("notPermissionMessage");
		this.giveScriptEditorMessage = lang.getString("giveScriptEditorMessage");
		this.allFileReloadMessage = lang.getString("allFileReloadMessage");
		this.notLatestPluginMessage = lang.getString("notLatestPluginMessage");
		this.updateErrorMessage = lang.getString("updateErrorMessage");
		this.notScriptBlockFileMessage = lang.getString("notScriptBlockFileMessage");
		this.dataMigrStartMessage = lang.getString("dataMigrStartMessage");
		this.dataMigrEndMessage = lang.getString("dataMigrEndMessage");
		this.notWorldEditMessage = lang.getString("notWorldEditMessage");
		this.updateDownloadStartMessage = lang.getString("updateDownloadStartMessage");
		this.updateDownloadEndMessages = lang.getStringList("updateDownloadEndMessages");
		this.updateCheckMessages = lang.getStringList("updateCheckMessages");
		this.updateErrorMessages = lang.getStringList("updateErrorMessages");
		this.scriptCopyMessage = lang.getString("scriptCopyMessage");
		this.scriptPasteMessage = lang.getString("scriptPasteMessage");
		this.scriptCreateMessage = lang.getString("scriptCreateMessage");
		this.scriptAddMessage = lang.getString("scriptAddMessage");
		this.scriptRemoveMessage = lang.getString("scriptRemoveMessage");
		this.worldEditNotSelectionMessage = lang.getString("worldEditNotSelectionMessage");
		this.worldEditPasteMessage = lang.getString("worldEditPasteMessage");
		this.worldEditRemoveMessage = lang.getString("worldEditRemoveMessage");
		this.activeDelayMessage = lang.getString("activeDelayMessage");
		this.activeCooldownMessage = lang.getString("activeCooldownMessage");
		this.succEditDataMessage = lang.getString("succEditDataMessage");
		this.errorEditDataMessage = lang.getString("errorEditDataMessage");
		this.errorScriptCheckMessage = lang.getString("errorScriptCheckMessage");
		this.errorScriptFileCheckMessage = lang.getString("errorScriptFileCheckMessage");
		this.errorScriptExecMessage = lang.getString("errorScriptExecMessage");
		this.errorGroupMessage = lang.getString("errorGroupMessage");
		this.errorHandMessage = lang.getString("errorHandMessage");
		this.errorCostMessage = lang.getString("errorCostMessage");
		this.errorItemMessage = lang.getString("errorItemMessage");
		this.consoleScriptCopyMessage = lang.getString("consoleScriptCopyMessage");
		this.consoleScriptPasteMessage = lang.getString("consoleScriptPasteMessage");
		this.consoleScriptCreateMessage = lang.getString("consoleScriptCreateMessage");
		this.consoleScriptAddMessage = lang.getString("consoleScriptAddMessage");
		this.consoleScriptRemoveMessage = lang.getString("consoleScriptRemoveMessage");
		this.consoleScriptViewMessage = lang.getString("consoleScriptViewMessage");
		this.consoleWorldEditPasteMessage = lang.getString("consoleWorldEditPasteMessage");
		this.consoleWorldEditRemoveMessage = lang.getString("consoleWorldEditRemoveMessage");
		this.consoleSuccScriptExecMessage = lang.getString("consoleSuccScriptExecMessage");
		this.consoleErrorScriptExecMessage = lang.getString("consoleErrorScriptExecMessage");
		this.isConsoleLog = Files.getConfig().getBoolean("ConsoleLog");
	}

	public static List<String> getScriptEditorLore() {
		if (instance.scriptEditorLore == null) {
			return null;
		}
		return replaceColorCode(instance.scriptEditorLore);
	}

	public static String getToolCommandMessage() {
		if (instance.toolCommandMessage == null) {
			return null;
		}
		return ChatColor.stripColor(instance.toolCommandMessage);
	}

	public static String getReloadCommandMessage() {
		if (instance.reloadCommandMessage == null) {
			return null;
		}
		return ChatColor.stripColor(instance.reloadCommandMessage);
	}

	public static String getCheckVerCommandMessage() {
		if (instance.checkVerCommandMessage == null) {
			return null;
		}
		return ChatColor.stripColor(instance.checkVerCommandMessage);
	}

	public static String getDataMigrCommandMessage() {
		if (instance.dataMigrCommandMessage == null) {
			return null;
		}
		return ChatColor.stripColor(instance.dataMigrCommandMessage);
	}

	public static String getCreateCommandMessage() {
		if (instance.createCommandMessage == null) {
			return null;
		}
		return ChatColor.stripColor(instance.createCommandMessage);
	}

	public static String getAddCommandMessage() {
		if (instance.addCommandMessage == null) {
			return null;
		}
		return ChatColor.stripColor(instance.addCommandMessage);
	}

	public static String getRemoveCommandMessage() {
		if (instance.removeCommandMessage == null) {
			return null;
		}
		return ChatColor.stripColor(instance.removeCommandMessage);
	}

	public static String getViewCommandMessage() {
		if (instance.viewCommandMessage == null) {
			return null;
		}
		return ChatColor.stripColor(instance.viewCommandMessage);
	}

	public static String getWorldEditPasteCommandMessage() {
		if (instance.worldEditPasteCommandMessage == null) {
			return null;
		}
		return ChatColor.stripColor(instance.worldEditPasteCommandMessage);
	}

	public static String getWorldEditRemoveCommandMessage() {
		if (instance.worldEditRemoveCommandMessage == null) {
			return null;
		}
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

	public static String getUpdateErrorMessage() {
		if (instance.updateErrorMessage == null) {
			return null;
		}
		return replaceColorCode(instance.updateErrorMessage);
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

	public static List<String> getUpdateDownloadEndMessages(String fileName, String filePath, String fileSize) {
		if (instance.updateDownloadEndMessages == null) {
			return null;
		}
		List<String> updateDownloadEndMessages = replaceColorCode(instance.updateDownloadEndMessages);
		for (int i = 0; i < updateDownloadEndMessages.size(); i++) {
			String message = updateDownloadEndMessages.get(i);
			message = replace(message, "%filename%", fileName);
			message = replace(message, "%filepath%", filePath);
			message = replace(message, "%filesize%", fileSize);
			updateDownloadEndMessages.set(i, message);
		}
		return updateDownloadEndMessages;
	}

	public static List<String> getUpdateCheckMessages(String pluginName, String latestVersion, String[] details) {
		if (instance.updateCheckMessages == null) {
			return null;
		}
		List<String> updateCheckMessages = replaceColorCode(instance.updateCheckMessages);
		for (int i = 0; i < updateCheckMessages.size(); i++) {
			String message = updateCheckMessages.get(i);
			message = replace(message, "%pluginname%", pluginName);
			message = replace(message, "%latestversion%", latestVersion);
			if (message.contains("%details%")) {
				StringBuilder builder = new StringBuilder();
				String color = Utils.getStartColor(message).toString();
				for (int k = 0, l = details.length; k < l; k++) {
					builder.append(color);
					if (details[k].startsWith("$")) {
						builder.append("  - ").append(StringUtils.removeStart(details[k], "$"));
					} else {
						builder.append("・").append(details[k]);
					}
					if (k != (l - 1)) {
						builder.append("\\n");
					}
				}
				message = replace(message, "%details%", builder.toString());
			}
			updateCheckMessages.set(i, message);
		}
		return updateCheckMessages;
	}

	public static List<String> getUpdateErrorMessages(String pluginName, String latestVersion) {
		if (instance.updateErrorMessages == null) {
			return null;
		}
		List<String> updateErrorMessages = replaceColorCode(instance.updateErrorMessages);
		for (int i = 0; i < updateErrorMessages.size(); i++) {
			String message = updateErrorMessages.get(i);
			message = replace(message, "%pluginname%", pluginName);
			message = replace(message, "%latestversion%", latestVersion);
			updateErrorMessages.set(i, message);
		}
		return updateErrorMessages;
	}

	public static String getScriptCopyMessage(ScriptType scriptType) {
		if (instance.scriptCopyMessage == null) {
			return null;
		}
		return replaceColorCode(replace(instance.scriptCopyMessage, "%scripttype%", scriptType.toString()));
	}

	public static String getScriptPasteMessage(ScriptType scriptType) {
		if (instance.scriptPasteMessage == null) {
			return null;
		}
		return replaceColorCode(replace(instance.scriptPasteMessage, "%scripttype%", scriptType.toString()));
	}

	public static String getScriptCreateMessage(ScriptType scriptType) {
		if (instance.scriptCreateMessage == null) {
			return null;
		}
		return replaceColorCode(replace(instance.scriptCreateMessage, "%scripttype%", scriptType.toString()));
	}

	public static String getScriptAddMessage(ScriptType scriptType) {
		if (instance.scriptAddMessage == null) {
			return null;
		}
		return replaceColorCode(replace(instance.scriptAddMessage, "%scripttype%", scriptType.toString()));
	}

	public static String getScriptRemoveMessage(ScriptType scriptType) {
		if (instance.scriptRemoveMessage == null) {
			return null;
		}
		return replaceColorCode(replace(instance.scriptRemoveMessage, "%scripttype%", scriptType.toString()));
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
		return replaceColorCode(replace(instance.worldEditPasteMessage, "%scripttype%", scriptType.toString()));
	}

	public static String getWorldEditRemoveMessage(String scriptType) {
		if (instance.worldEditRemoveMessage == null) {
			return null;
		}
		return replaceColorCode(replace(instance.worldEditRemoveMessage, "%scripttype%", scriptType.toString()));
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
		return replaceColorCode(replace(instance.errorScriptExecMessage, "%scripttype%", scriptType.toString()));
	}

	public static String getErrorGroupMessage(String group) {
		if (instance.errorGroupMessage == null) {
			return null;
		}
		return replaceColorCode(replace(instance.errorGroupMessage, "%group%", group));
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

	public static String getErrorCostMessage(double cost, double result) {
		if (instance.errorCostMessage == null) {
			return null;
		}
		String errorCostMessage = instance.errorCostMessage;
		errorCostMessage = replace(errorCostMessage, "%cost%", cost + "");
		errorCostMessage = replace(errorCostMessage, "%result%", result + "");
		return replaceColorCode(errorCostMessage);
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

	public static String getConsoleScriptCopyMessage(Player player, ScriptType scriptType, World world, String coords) {
		if (instance.consoleScriptCopyMessage == null || !instance.isConsoleLog) {
			return null;
		}
		String consoleScriptCopyMessage = instance.consoleScriptCopyMessage;
		consoleScriptCopyMessage = replace(consoleScriptCopyMessage, "%player%", player.getName());
		consoleScriptCopyMessage = replace(consoleScriptCopyMessage, "%scripttype%", scriptType.toString());
		consoleScriptCopyMessage = replace(consoleScriptCopyMessage, "%world%", world.getName());
		consoleScriptCopyMessage = replace(consoleScriptCopyMessage, "%coords%", coords);
		return replaceColorCode(consoleScriptCopyMessage);
	}

	public static String getConsoleScriptPasteMessage(Player player, ScriptType scriptType, World world, String coords) {
		if (instance.consoleScriptPasteMessage == null || !instance.isConsoleLog) {
			return null;
		}
		String consoleScriptPasteMessage = instance.consoleScriptPasteMessage;
		consoleScriptPasteMessage = replace(consoleScriptPasteMessage, "%player%", player.getName());
		consoleScriptPasteMessage = replace(consoleScriptPasteMessage, "%scripttype%", scriptType.toString());
		consoleScriptPasteMessage = replace(consoleScriptPasteMessage, "%world%", world.getName());
		consoleScriptPasteMessage = replace(consoleScriptPasteMessage, "%coords%", coords);
		return replaceColorCode(consoleScriptPasteMessage);
	}

	public static String getConsoleScriptCreateMessage(Player player, ScriptType scriptType, World world, String coords) {
		if (instance.consoleScriptCreateMessage == null || !instance.isConsoleLog) {
			return null;
		}
		String consoleScriptCreateMessage = instance.consoleScriptCreateMessage;
		consoleScriptCreateMessage = replace(consoleScriptCreateMessage, "%player%", player.getName());
		consoleScriptCreateMessage = replace(consoleScriptCreateMessage, "%scripttype%", scriptType.toString());
		consoleScriptCreateMessage = replace(consoleScriptCreateMessage, "%world%", world.getName());
		consoleScriptCreateMessage = replace(consoleScriptCreateMessage, "%coords%", coords);
		return replaceColorCode(consoleScriptCreateMessage);
	}

	public static String getConsoleScriptAddMessage(Player player, ScriptType scriptType, World world, String coords) {
		if (instance.consoleScriptAddMessage == null || !instance.isConsoleLog) {
			return null;
		}
		String consoleScriptAddMessage = instance.consoleScriptAddMessage;
		consoleScriptAddMessage = replace(consoleScriptAddMessage, "%player%", player.getName());
		consoleScriptAddMessage = replace(consoleScriptAddMessage, "%scripttype%", scriptType.toString());
		consoleScriptAddMessage = replace(consoleScriptAddMessage, "%world%", world.getName());
		consoleScriptAddMessage = replace(consoleScriptAddMessage, "%coords%", coords);
		return replaceColorCode(consoleScriptAddMessage);
	}

	public static String getConsoleScriptRemoveMessage(Player player, ScriptType scriptType, World world, String coords) {
		if (instance.consoleScriptRemoveMessage == null || !instance.isConsoleLog) {
			return null;
		}
		String consoleScriptRemoveMessage = instance.consoleScriptRemoveMessage;
		consoleScriptRemoveMessage = replace(consoleScriptRemoveMessage, "%player%", player.getName());
		consoleScriptRemoveMessage = replace(consoleScriptRemoveMessage, "%scripttype%", scriptType.toString());
		consoleScriptRemoveMessage = replace(consoleScriptRemoveMessage, "%world%", world.getName());
		consoleScriptRemoveMessage = replace(consoleScriptRemoveMessage, "%coords%", coords);
		return replaceColorCode(consoleScriptRemoveMessage);
	}

	public static String getConsoleScriptViewMessage(Player player, ScriptType scriptType, World world, String coords) {
		if (instance.consoleScriptViewMessage == null || !instance.isConsoleLog) {
			return null;
		}
		String consoleScriptViewMessage = instance.consoleScriptViewMessage;
		consoleScriptViewMessage = replace(consoleScriptViewMessage, "%player%", player.getName());
		consoleScriptViewMessage = replace(consoleScriptViewMessage, "%scripttype%", scriptType.toString());
		consoleScriptViewMessage = replace(consoleScriptViewMessage, "%world%", world.getName());
		consoleScriptViewMessage = replace(consoleScriptViewMessage, "%coords%", coords);
		return replaceColorCode(consoleScriptViewMessage);
	}

	public static String getConsoleWorldEditPasteMessage(ScriptType scriptType, Location min, Location max) {
		if (instance.consoleWorldEditPasteMessage == null || !instance.isConsoleLog) {
			return null;
		}
		String world = min.getWorld().getName();
		String minCoords = BlockLocation.fromLocation(min).getCoords();
		String maxCoords = BlockLocation.fromLocation(max).getCoords();
		String consoleWorldEditPasteMessage = instance.consoleWorldEditPasteMessage;
		consoleWorldEditPasteMessage = replace(consoleWorldEditPasteMessage, "%scripttype%", scriptType.toString());
		consoleWorldEditPasteMessage = replace(consoleWorldEditPasteMessage, "%world%", world);
		consoleWorldEditPasteMessage = replace(consoleWorldEditPasteMessage, "%mincoords%", minCoords);
		consoleWorldEditPasteMessage = replace(consoleWorldEditPasteMessage, "%maxcoords%", maxCoords);
		return replaceColorCode(consoleWorldEditPasteMessage);
	}

	public static String getConsoleWorldEditRemoveMessage(String scriptType, Location min, Location max) {
		if (instance.consoleWorldEditRemoveMessage == null || !instance.isConsoleLog) {
			return null;
		}
		String world = min.getWorld().getName();
		String minCoords = BlockLocation.fromLocation(min).getCoords();
		String maxCoords = BlockLocation.fromLocation(max).getCoords();
		String consoleWorldEditRemoveMessage = instance.consoleWorldEditRemoveMessage;
		consoleWorldEditRemoveMessage = replace(consoleWorldEditRemoveMessage, "%scripttype%", scriptType.toString());
		consoleWorldEditRemoveMessage = replace(consoleWorldEditRemoveMessage, "%world%", world);
		consoleWorldEditRemoveMessage = replace(consoleWorldEditRemoveMessage, "%mincoords%", minCoords);
		consoleWorldEditRemoveMessage = replace(consoleWorldEditRemoveMessage, "%maxcoords%", maxCoords);
		return replaceColorCode(consoleWorldEditRemoveMessage);
	}

	public static String getConsoleSuccScriptExecMessage(Player player, ScriptType scriptType, World world, String coords) {
		if (instance.consoleSuccScriptExecMessage == null || !instance.isConsoleLog) {
			return null;
		}
		String consoleSuccScriptExecMessage = instance.consoleSuccScriptExecMessage;
		consoleSuccScriptExecMessage = replace(consoleSuccScriptExecMessage, "%player%", player.getName());
		consoleSuccScriptExecMessage = replace(consoleSuccScriptExecMessage, "%scripttype%", scriptType.toString());
		consoleSuccScriptExecMessage = replace(consoleSuccScriptExecMessage, "%world%", world.getName());
		consoleSuccScriptExecMessage = replace(consoleSuccScriptExecMessage, "%coords%", coords);
		return replaceColorCode(consoleSuccScriptExecMessage);
	}

	public static String getConsoleErrorScriptExecMessage(Player player, ScriptType scriptType, World world, String coords) {
		if (instance.consoleErrorScriptExecMessage == null || !instance.isConsoleLog) {
			return null;
		}
		String consoleErrorScriptExecMessage = instance.consoleErrorScriptExecMessage;
		consoleErrorScriptExecMessage = replace(consoleErrorScriptExecMessage, "%player%", player.getName());
		consoleErrorScriptExecMessage = replace(consoleErrorScriptExecMessage, "%scripttype%", scriptType.toString());
		consoleErrorScriptExecMessage = replace(consoleErrorScriptExecMessage, "%world%", world.getName());
		consoleErrorScriptExecMessage = replace(consoleErrorScriptExecMessage, "%coords%", coords);
		return replaceColorCode(consoleErrorScriptExecMessage);
	}

	private static String replaceColorCode(String text) {
		return replace(text, "&", "§");
	}

	private static List<String> replaceColorCode(List<String> list) {
		for (int i = 0; i < list.size(); i++) {
			list.set(i, replace(list.get(i), "&", "§"));
		}
		return list;
	}

	private static String replace(String text, String search, String replace) {
		return StringUtils.replace(text, search, replace);
	}

	public static void reload() {
		instance = new Lang();
	}
}