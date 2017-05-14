package com.github.yuttyann.scriptblockplus.file;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.BlockLocation;
import com.github.yuttyann.scriptblockplus.enums.ClickType;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public class Messages {

	public static final String notVaultMessage = "&cVaultが導入されていないため、プラグインを無効化します。";
	public static final String senderNoPlayerMessage = "§cコマンドはゲーム内から実行してください。";
	public static final String notPermissionMessage = "§cパーミッションが無いため、実行できません。";
	public static final String giveToolMessage = "§aScript Editorが配布されました。";
	public static final String allFileReloadMessage = "§a全てのファイルの再読み込みが完了しました。";
	public static final String notLatestPluginMessage = "§c最新のバージョンが存在しません。";
	public static final String updateErrorMessage = "§cアップデートに失敗しました。";
	public static final String notScriptBlockFileMessage = "§cScriptBlockのデータファイルが見つかりません。";
	public static final String dataMigrStartMessage = "§7ScriptBlockのスクリプトを移行しています....";
	public static final String dataMigrEndMessage = "§bスクリプトの移行が完了しました。";
	public static final String notWorldEditMessage = "&cWorldEditが導入されていないため、実行に失敗しました。";

	private static Messages instance;
	private static YamlConfig messages;
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

	public Messages() {
		messages = Files.getMessages();
		this.scriptCopyMessage = messages.getString("scriptCopyMessage");
		this.scriptPasteMessage = messages.getString("scriptPasteMessage");
		this.scriptCreateMessage = messages.getString("scriptCreateMessage");
		this.scriptAddMessage = messages.getString("scriptAddMessage");
		this.scriptRemoveMessage = messages.getString("scriptRemoveMessage");
		this.worldEditNotSelectionMessage = messages.getString("worldEditNotSelectionMessage");
		this.worldEditPasteMessage = messages.getString("worldEditPasteMessage");
		this.worldEditRemoveMessage = messages.getString("worldEditRemoveMessage");
		this.activeDelayMessage = messages.getString("activeDelayMessage");
		this.activeCooldownMessage = messages.getString("activeCooldownMessage");
		this.succEditDataMessage = messages.getString("succEditDataMessage");
		this.errorEditDataMessage = messages.getString("errorEditDataMessage");
		this.errorScriptCheckMessage = messages.getString("errorScriptCheckMessage");
		this.errorScriptFileCheckMessage = messages.getString("errorScriptFileCheckMessage");
		this.errorScriptExecMessage = messages.getString("errorScriptExecMessage");
		this.errorGroupMessage = messages.getString("errorGroupMessage");
		this.errorHandMessage = messages.getString("errorHandMessage");
		this.errorCostMessage = messages.getString("errorCostMessage");
		this.errorItemMessage = messages.getString("errorItemMessage");
		this.consoleScriptCopyMessage = messages.getString("consoleScriptCopyMessage");
		this.consoleScriptPasteMessage = messages.getString("consoleScriptPasteMessage");
		this.consoleScriptCreateMessage = messages.getString("consoleScriptCreateMessage");
		this.consoleScriptAddMessage = messages.getString("consoleScriptAddMessage");
		this.consoleScriptRemoveMessage = messages.getString("consoleScriptRemoveMessage");
		this.consoleScriptViewMessage = messages.getString("consoleScriptViewMessage");
		this.consoleWorldEditPasteMessage = messages.getString("consoleWorldEditPasteMessage");
		this.consoleWorldEditRemoveMessage = messages.getString("consoleWorldEditRemoveMessage");
		this.consoleSuccScriptExecMessage = messages.getString("consoleSuccScriptExecMessage");
		this.consoleErrorScriptExecMessage = messages.getString("consoleErrorScriptExecMessage");
		this.isConsoleLog = Files.getConfig().getBoolean("ConsoleLog");
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
		errorHandMessage = replace(errorHandMessage, "%itemname%", itemName);
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
		errorItemMessage = replace(errorItemMessage, "%itemname%", itemName);
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

	private static String replace(String text, String search, String replace) {
		return StringUtils.replace(text, search, replace);
	}

	public static void reload() {
		instance = new Messages();
	}
}