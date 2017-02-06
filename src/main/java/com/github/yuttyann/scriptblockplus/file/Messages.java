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
		instance.activeCooldownMessage = StringUtils.replace(instance.activeCooldownMessage, "%hour%", hour + "");
		instance.activeCooldownMessage = StringUtils.replace(instance.activeCooldownMessage, "%minute%", minute + "");
		instance.activeCooldownMessage = StringUtils.replace(instance.activeCooldownMessage, "%second%", second + "");
		return replaceColorCode(instance.activeCooldownMessage);
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
		instance.errorHandMessage = StringUtils.replace(instance.errorHandMessage, "%material%", material.toString());
		instance.errorHandMessage = StringUtils.replace(instance.errorHandMessage, "%id%", id + "");
		instance.errorHandMessage = StringUtils.replace(instance.errorHandMessage, "%amount%", amount + "");
		instance.errorHandMessage = StringUtils.replace(instance.errorHandMessage, "%damage%", damage + "");
		instance.errorHandMessage = StringUtils.replace(instance.errorHandMessage, "%itemname%", itemName);
		return replaceColorCode(instance.errorHandMessage);
	}

	public static String getErrorCostMessage(double cost, double result) {
		if (instance.errorCostMessage == null) {
			return null;
		}
		instance.errorCostMessage = StringUtils.replace(instance.errorCostMessage, "%cost%", cost + "");
		instance.errorCostMessage = StringUtils.replace(instance.errorCostMessage, "%result%", result + "");
		return replaceColorCode(instance.errorCostMessage);
	}

	public static String getErrorItemMessage(Material material, int id, int amount, short damage, String itemName) {
		if (instance.errorItemMessage == null) {
			return null;
		}
		instance.errorItemMessage = StringUtils.replace(instance.errorItemMessage, "%material%", material.toString());
		instance.errorItemMessage = StringUtils.replace(instance.errorItemMessage, "%id%", id + "");
		instance.errorItemMessage = StringUtils.replace(instance.errorItemMessage, "%amount%", amount + "");
		instance.errorItemMessage = StringUtils.replace(instance.errorItemMessage, "%damage%", damage + "");
		instance.errorItemMessage = StringUtils.replace(instance.errorItemMessage, "%itemname%", itemName);
		return replaceColorCode(instance.errorItemMessage);
	}

	public static String getConsoleScriptCopyMessage(Player player, ScriptType scriptType, World world, String coords) {
		if (instance.consoleScriptCopyMessage == null || !instance.isConsoleLog) {
			return null;
		}
		instance.consoleScriptCopyMessage = StringUtils.replace(instance.consoleScriptCopyMessage, "%player%", player.getName());
		instance.consoleScriptCopyMessage = StringUtils.replace(instance.consoleScriptCopyMessage, "%scripttype%", scriptType.toString());
		instance.consoleScriptCopyMessage = StringUtils.replace(instance.consoleScriptCopyMessage, "%world%", world.getName());
		instance.consoleScriptCopyMessage = StringUtils.replace(instance.consoleScriptCopyMessage, "%coords%", coords);
		return replaceColorCode(instance.consoleScriptCopyMessage);
	}

	public static String getConsoleScriptPasteMessage(Player player, ScriptType scriptType, World world, String coords) {
		if (instance.consoleScriptPasteMessage == null || !instance.isConsoleLog) {
			return null;
		}
		instance.consoleScriptPasteMessage = StringUtils.replace(instance.consoleScriptPasteMessage, "%player%", player.getName());
		instance.consoleScriptPasteMessage = StringUtils.replace(instance.consoleScriptPasteMessage, "%scripttype%", scriptType.toString());
		instance.consoleScriptPasteMessage = StringUtils.replace(instance.consoleScriptPasteMessage, "%world%", world.getName());
		instance.consoleScriptPasteMessage = StringUtils.replace(instance.consoleScriptPasteMessage, "%coords%", coords);
		return replaceColorCode(instance.consoleScriptPasteMessage);
	}

	public static String getConsoleScriptCreateMessage(Player player, ScriptType scriptType, World world, String coords) {
		if (instance.consoleScriptCreateMessage == null || !instance.isConsoleLog) {
			return null;
		}
		instance.consoleScriptCreateMessage = StringUtils.replace(instance.consoleScriptCreateMessage, "%player%", player.getName());
		instance.consoleScriptCreateMessage = StringUtils.replace(instance.consoleScriptCreateMessage, "%scripttype%", scriptType.toString());
		instance.consoleScriptCreateMessage = StringUtils.replace(instance.consoleScriptCreateMessage, "%world%", world.getName());
		instance.consoleScriptCreateMessage = StringUtils.replace(instance.consoleScriptCreateMessage, "%coords%", coords);
		return replaceColorCode(instance.consoleScriptCreateMessage);
	}

	public static String getConsoleScriptAddMessage(Player player, ScriptType scriptType, World world, String coords) {
		if (instance.consoleScriptAddMessage == null || !instance.isConsoleLog) {
			return null;
		}
		instance.consoleScriptAddMessage = StringUtils.replace(instance.consoleScriptAddMessage, "%player%", player.getName());
		instance.consoleScriptAddMessage = StringUtils.replace(instance.consoleScriptAddMessage, "%scripttype%", scriptType.toString());
		instance.consoleScriptAddMessage = StringUtils.replace(instance.consoleScriptAddMessage, "%world%", world.getName());
		instance.consoleScriptAddMessage = StringUtils.replace(instance.consoleScriptAddMessage, "%coords%", coords);
		return replaceColorCode(instance.consoleScriptAddMessage);
	}

	public static String getConsoleScriptRemoveMessage(Player player, ScriptType scriptType, World world, String coords) {
		if (instance.consoleScriptRemoveMessage == null || !instance.isConsoleLog) {
			return null;
		}
		instance.consoleScriptRemoveMessage = StringUtils.replace(instance.consoleScriptRemoveMessage, "%player%", player.getName());
		instance.consoleScriptRemoveMessage = StringUtils.replace(instance.consoleScriptRemoveMessage, "%scripttype%", scriptType.toString());
		instance.consoleScriptRemoveMessage = StringUtils.replace(instance.consoleScriptRemoveMessage, "%world%", world.getName());
		instance.consoleScriptRemoveMessage = StringUtils.replace(instance.consoleScriptRemoveMessage, "%coords%", coords);
		return replaceColorCode(instance.consoleScriptRemoveMessage);
	}

	public static String getConsoleScriptViewMessage(Player player, ScriptType scriptType, World world, String coords) {
		if (instance.consoleScriptViewMessage == null || !instance.isConsoleLog) {
			return null;
		}
		instance.consoleScriptViewMessage = StringUtils.replace(instance.consoleScriptViewMessage, "%player%", player.getName());
		instance.consoleScriptViewMessage = StringUtils.replace(instance.consoleScriptViewMessage, "%scripttype%", scriptType.toString());
		instance.consoleScriptViewMessage = StringUtils.replace(instance.consoleScriptViewMessage, "%world%", world.getName());
		instance.consoleScriptViewMessage = StringUtils.replace(instance.consoleScriptViewMessage, "%coords%", coords);
		return replaceColorCode(instance.consoleScriptViewMessage);
	}

	public static String getConsoleWorldEditPasteMessage(ScriptType scriptType, Location min, Location max) {
		if (instance.consoleWorldEditPasteMessage == null || !instance.isConsoleLog) {
			return null;
		}
		String world = min.getWorld().getName();
		String minCoords = BlockLocation.fromLocation(min).getCoords();
		String maxCoords = BlockLocation.fromLocation(max).getCoords();
		instance.consoleWorldEditPasteMessage = StringUtils.replace(instance.consoleWorldEditPasteMessage, "%scripttype%", scriptType.toString());
		instance.consoleWorldEditPasteMessage = StringUtils.replace(instance.consoleWorldEditPasteMessage, "%world%", world);
		instance.consoleWorldEditPasteMessage = StringUtils.replace(instance.consoleWorldEditPasteMessage, "%mincoords%", minCoords);
		instance.consoleWorldEditPasteMessage = StringUtils.replace(instance.consoleWorldEditPasteMessage, "%maxcoords%", maxCoords);
		return replaceColorCode(instance.consoleWorldEditPasteMessage);
	}

	public static String getConsoleWorldEditRemoveMessage(String scriptType, Location min, Location max) {
		if (instance.consoleWorldEditRemoveMessage == null) {
			return null;
		}
		String world = min.getWorld().getName();
		String minCoords = BlockLocation.fromLocation(min).getCoords();
		String maxCoords = BlockLocation.fromLocation(max).getCoords();
		instance.consoleWorldEditRemoveMessage = StringUtils.replace(instance.consoleWorldEditRemoveMessage, "%scripttype%", scriptType.toString());
		instance.consoleWorldEditRemoveMessage = StringUtils.replace(instance.consoleWorldEditRemoveMessage, "%world%", world);
		instance.consoleWorldEditRemoveMessage = StringUtils.replace(instance.consoleWorldEditRemoveMessage, "%mincoords%", minCoords);
		instance.consoleWorldEditRemoveMessage = StringUtils.replace(instance.consoleWorldEditRemoveMessage, "%maxcoords%", maxCoords);
		return replaceColorCode(instance.consoleWorldEditRemoveMessage);
	}

	public static String getConsoleSuccScriptExecMessage(Player player, ScriptType scriptType, World world, String coords) {
		if (instance.consoleSuccScriptExecMessage == null || !instance.isConsoleLog) {
			return null;
		}
		instance.consoleSuccScriptExecMessage = StringUtils.replace(instance.consoleSuccScriptExecMessage, "%player%", player.getName());
		instance.consoleSuccScriptExecMessage = StringUtils.replace(instance.consoleSuccScriptExecMessage, "%scripttype%", scriptType.toString());
		instance.consoleSuccScriptExecMessage = StringUtils.replace(instance.consoleSuccScriptExecMessage, "%world%", world.getName());
		instance.consoleSuccScriptExecMessage = StringUtils.replace(instance.consoleSuccScriptExecMessage, "%coords%", coords);
		return replaceColorCode(instance.consoleSuccScriptExecMessage);
	}

	public static String getConsoleErrorScriptExecMessage(Player player, ScriptType scriptType, World world, String coords) {
		if (instance.consoleErrorScriptExecMessage == null || !instance.isConsoleLog) {
			return null;
		}
		instance.consoleErrorScriptExecMessage = StringUtils.replace(instance.consoleErrorScriptExecMessage, "%player%", player.getName());
		instance.consoleErrorScriptExecMessage = StringUtils.replace(instance.consoleErrorScriptExecMessage, "%scripttype%", scriptType.toString());
		instance.consoleErrorScriptExecMessage = StringUtils.replace(instance.consoleErrorScriptExecMessage, "%world%", world.getName());
		instance.consoleErrorScriptExecMessage = StringUtils.replace(instance.consoleErrorScriptExecMessage, "%coords%", coords);
		return replaceColorCode(instance.consoleErrorScriptExecMessage);
	}

	private static String replaceColorCode(String text) {
		return StringUtils.replace(text, "&", "§");
	}

	public static void reload() {
		instance = new Messages();
	}
}