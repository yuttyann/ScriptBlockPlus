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
		return replaceColorCode(StringUtils.replace(instance.scriptCopyMessage, "%scripttype%", scriptType.toString()));
	}

	public static String getScriptPasteMessage(ScriptType scriptType) {
		if (instance.scriptPasteMessage == null) {
			return null;
		}
		return replaceColorCode(StringUtils.replace(instance.scriptPasteMessage, "%scripttype%", scriptType.toString()));
	}

	public static String getScriptCreateMessage(ScriptType scriptType) {
		if (instance.scriptCreateMessage == null) {
			return null;
		}
		return replaceColorCode(StringUtils.replace(instance.scriptCreateMessage, "%scripttype%", scriptType.toString()));
	}

	public static String getScriptAddMessage(ScriptType scriptType) {
		if (instance.scriptAddMessage == null) {
			return null;
		}
		return replaceColorCode(StringUtils.replace(instance.scriptAddMessage, "%scripttype%", scriptType.toString()));
	}

	public static String getScriptRemoveMessage(ScriptType scriptType) {
		if (instance.scriptRemoveMessage == null) {
			return null;
		}
		return replaceColorCode(StringUtils.replace(instance.scriptRemoveMessage, "%scripttype%", scriptType.toString()));
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
		return replaceColorCode(StringUtils.replace(instance.worldEditPasteMessage, "%scripttype%", scriptType.toString()));
	}

	public static String getWorldEditRemoveMessage(String scriptType) {
		if (instance.worldEditRemoveMessage == null) {
			return null;
		}
		return replaceColorCode(StringUtils.replace(instance.worldEditRemoveMessage, "%scripttype%", scriptType.toString()));
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
		activeCooldownMessage = StringUtils.replace(activeCooldownMessage, "%hour%", hour + "");
		activeCooldownMessage = StringUtils.replace(activeCooldownMessage, "%minute%", minute + "");
		activeCooldownMessage = StringUtils.replace(activeCooldownMessage, "%second%", second + "");
		return replaceColorCode(activeCooldownMessage);
	}

	public static String getSuccEditDataMessage(ClickType clickType) {
		if (instance.succEditDataMessage == null) {
			return null;
		}
		String type = StringUtils.replace(clickType.name().toLowerCase(), "_", "-");
		return replaceColorCode(StringUtils.replace(instance.succEditDataMessage, "%clicktype%", type));
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
		return replaceColorCode(StringUtils.replace(instance.errorScriptExecMessage, "%scripttype%", scriptType.toString()));
	}

	public static String getErrorGroupMessage(String group) {
		if (instance.errorGroupMessage == null) {
			return null;
		}
		return replaceColorCode(StringUtils.replace(instance.errorGroupMessage, "%group%", group));
	}

	public static String getErrorHandMessage(Material material, int id, int amount, short damage, String itemName) {
		if (instance.errorHandMessage == null) {
			return null;
		}
		String errorHandMessage = instance.errorHandMessage;
		errorHandMessage = StringUtils.replace(errorHandMessage, "%material%", material.toString());
		errorHandMessage = StringUtils.replace(errorHandMessage, "%id%", id + "");
		errorHandMessage = StringUtils.replace(errorHandMessage, "%amount%", amount + "");
		errorHandMessage = StringUtils.replace(errorHandMessage, "%damage%", damage + "");
		errorHandMessage = StringUtils.replace(errorHandMessage, "%itemname%", itemName);
		return replaceColorCode(errorHandMessage);
	}

	public static String getErrorCostMessage(double cost, double result) {
		if (instance.errorCostMessage == null) {
			return null;
		}
		String errorCostMessage = instance.errorCostMessage;
		errorCostMessage = StringUtils.replace(errorCostMessage, "%cost%", cost + "");
		errorCostMessage = StringUtils.replace(errorCostMessage, "%result%", result + "");
		return replaceColorCode(errorCostMessage);
	}

	public static String getErrorItemMessage(Material material, int id, int amount, short damage, String itemName) {
		if (instance.errorItemMessage == null) {
			return null;
		}
		String errorItemMessage = instance.errorItemMessage;
		errorItemMessage = StringUtils.replace(errorItemMessage, "%material%", material.toString());
		errorItemMessage = StringUtils.replace(errorItemMessage, "%id%", id + "");
		errorItemMessage = StringUtils.replace(errorItemMessage, "%amount%", amount + "");
		errorItemMessage = StringUtils.replace(errorItemMessage, "%damage%", damage + "");
		errorItemMessage = StringUtils.replace(errorItemMessage, "%itemname%", itemName);
		return replaceColorCode(errorItemMessage);
	}

	public static String getConsoleScriptCopyMessage(Player player, ScriptType scriptType, World world, String coords) {
		if (instance.consoleScriptCopyMessage == null || !instance.isConsoleLog) {
			return null;
		}
		String consoleScriptCopyMessage = instance.consoleScriptCopyMessage;
		consoleScriptCopyMessage = StringUtils.replace(consoleScriptCopyMessage, "%player%", player.getName());
		consoleScriptCopyMessage = StringUtils.replace(consoleScriptCopyMessage, "%scripttype%", scriptType.toString());
		consoleScriptCopyMessage = StringUtils.replace(consoleScriptCopyMessage, "%world%", world.getName());
		consoleScriptCopyMessage = StringUtils.replace(consoleScriptCopyMessage, "%coords%", coords);
		return replaceColorCode(consoleScriptCopyMessage);
	}

	public static String getConsoleScriptPasteMessage(Player player, ScriptType scriptType, World world, String coords) {
		if (instance.consoleScriptPasteMessage == null || !instance.isConsoleLog) {
			return null;
		}
		String consoleScriptPasteMessage = instance.consoleScriptPasteMessage;
		consoleScriptPasteMessage = StringUtils.replace(consoleScriptPasteMessage, "%player%", player.getName());
		consoleScriptPasteMessage = StringUtils.replace(consoleScriptPasteMessage, "%scripttype%", scriptType.toString());
		consoleScriptPasteMessage = StringUtils.replace(consoleScriptPasteMessage, "%world%", world.getName());
		consoleScriptPasteMessage = StringUtils.replace(consoleScriptPasteMessage, "%coords%", coords);
		return replaceColorCode(consoleScriptPasteMessage);
	}

	public static String getConsoleScriptCreateMessage(Player player, ScriptType scriptType, World world, String coords) {
		if (instance.consoleScriptCreateMessage == null || !instance.isConsoleLog) {
			return null;
		}
		String consoleScriptCreateMessage = instance.consoleScriptCreateMessage;
		consoleScriptCreateMessage = StringUtils.replace(consoleScriptCreateMessage, "%player%", player.getName());
		consoleScriptCreateMessage = StringUtils.replace(consoleScriptCreateMessage, "%scripttype%", scriptType.toString());
		consoleScriptCreateMessage = StringUtils.replace(consoleScriptCreateMessage, "%world%", world.getName());
		consoleScriptCreateMessage = StringUtils.replace(consoleScriptCreateMessage, "%coords%", coords);
		return replaceColorCode(consoleScriptCreateMessage);
	}

	public static String getConsoleScriptAddMessage(Player player, ScriptType scriptType, World world, String coords) {
		if (instance.consoleScriptAddMessage == null || !instance.isConsoleLog) {
			return null;
		}
		String consoleScriptAddMessage = instance.consoleScriptAddMessage;
		consoleScriptAddMessage = StringUtils.replace(consoleScriptAddMessage, "%player%", player.getName());
		consoleScriptAddMessage = StringUtils.replace(consoleScriptAddMessage, "%scripttype%", scriptType.toString());
		consoleScriptAddMessage = StringUtils.replace(consoleScriptAddMessage, "%world%", world.getName());
		consoleScriptAddMessage = StringUtils.replace(consoleScriptAddMessage, "%coords%", coords);
		return replaceColorCode(consoleScriptAddMessage);
	}

	public static String getConsoleScriptRemoveMessage(Player player, ScriptType scriptType, World world, String coords) {
		if (instance.consoleScriptRemoveMessage == null || !instance.isConsoleLog) {
			return null;
		}
		String consoleScriptRemoveMessage = instance.consoleScriptRemoveMessage;
		consoleScriptRemoveMessage = StringUtils.replace(consoleScriptRemoveMessage, "%player%", player.getName());
		consoleScriptRemoveMessage = StringUtils.replace(consoleScriptRemoveMessage, "%scripttype%", scriptType.toString());
		consoleScriptRemoveMessage = StringUtils.replace(consoleScriptRemoveMessage, "%world%", world.getName());
		consoleScriptRemoveMessage = StringUtils.replace(consoleScriptRemoveMessage, "%coords%", coords);
		return replaceColorCode(consoleScriptRemoveMessage);
	}

	public static String getConsoleScriptViewMessage(Player player, ScriptType scriptType, World world, String coords) {
		if (instance.consoleScriptViewMessage == null || !instance.isConsoleLog) {
			return null;
		}
		String consoleScriptViewMessage = instance.consoleScriptViewMessage;
		consoleScriptViewMessage = StringUtils.replace(consoleScriptViewMessage, "%player%", player.getName());
		consoleScriptViewMessage = StringUtils.replace(consoleScriptViewMessage, "%scripttype%", scriptType.toString());
		consoleScriptViewMessage = StringUtils.replace(consoleScriptViewMessage, "%world%", world.getName());
		consoleScriptViewMessage = StringUtils.replace(consoleScriptViewMessage, "%coords%", coords);
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
		consoleWorldEditPasteMessage = StringUtils.replace(consoleWorldEditPasteMessage, "%scripttype%", scriptType.toString());
		consoleWorldEditPasteMessage = StringUtils.replace(consoleWorldEditPasteMessage, "%world%", world);
		consoleWorldEditPasteMessage = StringUtils.replace(consoleWorldEditPasteMessage, "%mincoords%", minCoords);
		consoleWorldEditPasteMessage = StringUtils.replace(consoleWorldEditPasteMessage, "%maxcoords%", maxCoords);
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
		consoleWorldEditRemoveMessage = StringUtils.replace(consoleWorldEditRemoveMessage, "%scripttype%", scriptType.toString());
		consoleWorldEditRemoveMessage = StringUtils.replace(consoleWorldEditRemoveMessage, "%world%", world);
		consoleWorldEditRemoveMessage = StringUtils.replace(consoleWorldEditRemoveMessage, "%mincoords%", minCoords);
		consoleWorldEditRemoveMessage = StringUtils.replace(consoleWorldEditRemoveMessage, "%maxcoords%", maxCoords);
		return replaceColorCode(consoleWorldEditRemoveMessage);
	}

	public static String getConsoleSuccScriptExecMessage(Player player, ScriptType scriptType, World world, String coords) {
		if (instance.consoleSuccScriptExecMessage == null || !instance.isConsoleLog) {
			return null;
		}
		String consoleSuccScriptExecMessage = instance.consoleSuccScriptExecMessage;
		consoleSuccScriptExecMessage = StringUtils.replace(consoleSuccScriptExecMessage, "%player%", player.getName());
		consoleSuccScriptExecMessage = StringUtils.replace(consoleSuccScriptExecMessage, "%scripttype%", scriptType.toString());
		consoleSuccScriptExecMessage = StringUtils.replace(consoleSuccScriptExecMessage, "%world%", world.getName());
		consoleSuccScriptExecMessage = StringUtils.replace(consoleSuccScriptExecMessage, "%coords%", coords);
		return replaceColorCode(consoleSuccScriptExecMessage);
	}

	public static String getConsoleErrorScriptExecMessage(Player player, ScriptType scriptType, World world, String coords) {
		if (instance.consoleErrorScriptExecMessage == null || !instance.isConsoleLog) {
			return null;
		}
		String consoleErrorScriptExecMessage = instance.consoleErrorScriptExecMessage;
		consoleErrorScriptExecMessage = StringUtils.replace(consoleErrorScriptExecMessage, "%player%", player.getName());
		consoleErrorScriptExecMessage = StringUtils.replace(consoleErrorScriptExecMessage, "%scripttype%", scriptType.toString());
		consoleErrorScriptExecMessage = StringUtils.replace(consoleErrorScriptExecMessage, "%world%", world.getName());
		consoleErrorScriptExecMessage = StringUtils.replace(consoleErrorScriptExecMessage, "%coords%", coords);
		return replaceColorCode(consoleErrorScriptExecMessage);
	}

	private static String replaceColorCode(String text) {
		return StringUtils.replace(text, "&", "§");
	}

	public static void reload() {
		instance = new Messages();
	}
}