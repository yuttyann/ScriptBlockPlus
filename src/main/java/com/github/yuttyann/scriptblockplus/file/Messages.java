package com.github.yuttyann.scriptblockplus.file;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.manager.MetadataManager.ClickType;
import com.github.yuttyann.scriptblockplus.manager.OptionManager.ScriptType;
import com.github.yuttyann.scriptblockplus.util.BlockLocation;

public class Messages {

	private static Messages instance;
	private static Yaml messages;
	private String scriptCopyMessage;
	private String scriptPasteMessage;
	private String scriptCreateMessage;
	private String scriptAddMessage;
	private String scriptRemoveMessage;
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
		instance = this;
		messages = Files.getMessages();
		this.scriptCopyMessage = messages.getString("scriptCopyMessage");
		this.scriptPasteMessage = messages.getString("scriptPasteMessage");
		this.scriptCreateMessage = messages.getString("scriptCreateMessage");
		this.scriptAddMessage = messages.getString("scriptAddMessage");
		this.scriptRemoveMessage = messages.getString("scriptRemoveMessage");
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
		return replaceColorCode(instance.scriptCopyMessage.replace("%scripttype%", scriptType.getString()));
	}

	public static String getScriptPasteMessage(ScriptType scriptType) {
		if (instance.scriptPasteMessage == null) {
			return null;
		}
		return replaceColorCode(instance.scriptPasteMessage.replace("%scripttype%", scriptType.getString()));
	}

	public static String getScriptCreateMessage(ScriptType scriptType) {
		if (instance.scriptCreateMessage == null) {
			return null;
		}
		return replaceColorCode(instance.scriptCreateMessage.replace("%scripttype%", scriptType.getString()));
	}

	public static String getScriptAddMessage(ScriptType scriptType) {
		if (instance.scriptAddMessage == null) {
			return null;
		}
		return replaceColorCode(instance.scriptAddMessage.replace("%scripttype%", scriptType.getString()));
	}

	public static String getScriptRemoveMessage(ScriptType scriptType) {
		if (instance.scriptRemoveMessage == null) {
			return null;
		}
		return replaceColorCode(instance.scriptRemoveMessage.replace("%scripttype%", scriptType.getString()));
	}

	public static String getWorldEditPasteMessage(ScriptType scriptType) {
		if (instance.worldEditPasteMessage == null) {
			return null;
		}
		return replaceColorCode(instance.worldEditPasteMessage.replace("%scripttype%", scriptType.getString()));
	}

	public static String getWorldEditRemoveMessage(String scriptType) {
		if (instance.worldEditRemoveMessage == null) {
			return null;
		}
		return replaceColorCode(instance.worldEditRemoveMessage.replace("%scripttype%", scriptType));
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
		return replaceColorCode(instance.activeCooldownMessage.replace("%hour%", hour + "").replace("%minute%", minute + "").replace("%second%", second + ""));
	}

	public static String getSuccEditDataMessage(ClickType clickType) {
		if (instance.succEditDataMessage == null) {
			return null;
		}
		String type = "";
		switch (clickType) {
		case INTERACT_CREATE:
			type = "interact-create";
			break;
		case INTERACT_ADD:
			type = "interact-add";
			break;
		case INTERACT_REMOVE:
			type = "interact-remove";
			break;
		case INTERACT_VIEW:
			type = "interact-view";
			break;
		case WALK_CREATE:
			type = "walk-create";
			break;
		case WALK_ADD:
			type = "walk-add";
			break;
		case WALK_REMOVE:
			type = "walk-remove";
			break;
		case WALK_VIEW:
			type = "walk-view";
			break;
		}
		return replaceColorCode(instance.succEditDataMessage.replace("%clicktype%", type));
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
		return replaceColorCode(instance.errorScriptExecMessage.replace("%scripttype%", scriptType.getString()));
	}

	public static String getErrorGroupMessage(String group) {
		if (instance.errorGroupMessage == null) {
			return null;
		}
		return replaceColorCode(instance.errorGroupMessage.replace("%group%", group));
	}

	public static String getErrorCostMessage(double cost, double result) {
		if (instance.errorCostMessage == null) {
			return null;
		}
		return replaceColorCode(instance.errorCostMessage.replace("%cost%", cost + "").replace("%result%", result + ""));
	}

	public static String getErrorItemMessage(Material material, int id, int amount, short damage) {
		if (instance.errorItemMessage == null) {
			return null;
		}
		return replaceColorCode(instance.errorItemMessage.replace("%material%", material.toString()).replace("%id%", id + "").replace("%amount%", amount + "").replace("%damage%", damage + ""));
	}

	public static String getConsoleScriptCopyMessage(Player player, ScriptType scriptType, World world, String coords) {
		if (instance.consoleScriptCopyMessage == null || !instance.isConsoleLog) {
			return null;
		}
		return replaceColorCode(instance.consoleScriptCopyMessage.replace("%player%", player.getName()).replace("%scripttype%", scriptType.getString()).replace("%world%", world.getName()).replace("%coords%", coords));
	}

	public static String getConsoleScriptPasteMessage(Player player, ScriptType scriptType, World world, String coords) {
		if (instance.consoleScriptPasteMessage == null || !instance.isConsoleLog) {
			return null;
		}
		return replaceColorCode(instance.consoleScriptPasteMessage.replace("%player%", player.getName()).replace("%scripttype%", scriptType.getString()).replace("%world%", world.getName()).replace("%coords%", coords));
	}

	public static String getConsoleScriptCreateMessage(Player player, ScriptType scriptType, World world, String coords) {
		if (instance.consoleScriptCreateMessage == null || !instance.isConsoleLog) {
			return null;
		}
		return replaceColorCode(instance.consoleScriptCreateMessage.replace("%player%", player.getName()).replace("%scripttype%", scriptType.getString()).replace("%world%", world.getName()).replace("%coords%", coords));
	}

	public static String getConsoleScriptAddMessage(Player player, ScriptType scriptType, World world, String coords) {
		if (instance.consoleScriptAddMessage == null || !instance.isConsoleLog) {
			return null;
		}
		return replaceColorCode(instance.consoleScriptAddMessage.replace("%player%", player.getName()).replace("%scripttype%", scriptType.getString()).replace("%world%", world.getName()).replace("%coords%", coords));
	}

	public static String getConsoleScriptRemoveMessage(Player player, ScriptType scriptType, World world, String coords) {
		if (instance.consoleScriptRemoveMessage == null || !instance.isConsoleLog) {
			return null;
		}
		return replaceColorCode(instance.consoleScriptRemoveMessage.replace("%player%", player.getName()).replace("%scripttype%", scriptType.getString()).replace("%world%", world.getName()).replace("%coords%", coords));
	}

	public static String getConsoleScriptViewMessage(Player player, ScriptType scriptType, World world, String coords) {
		if (instance.consoleScriptViewMessage == null || !instance.isConsoleLog) {
			return null;
		}
		return replaceColorCode(instance.consoleScriptViewMessage.replace("%player%", player.getName()).replace("%scripttype%", scriptType.getString()).replace("%world%", world.getName()).replace("%coords%", coords));
	}

	public static String getConsoleWorldEditPasteMessage(ScriptType scriptType, Location min, Location max) {
		if (instance.consoleWorldEditPasteMessage == null || !instance.isConsoleLog) {
			return null;
		}
		String world = min.getWorld().getName();
		String minCoords = BlockLocation.fromLocation(min).getCoords();
		String maxCoords = BlockLocation.fromLocation(max).getCoords();
		return replaceColorCode(instance.consoleWorldEditPasteMessage.replace("%scripttype%", scriptType.getString()).replace("%world%", world).replace("%mincoords%", minCoords).replace("%maxcoords%", maxCoords));
	}

	public static String getConsoleWorldEditRemoveMessage(String scriptType, Location min, Location max) {
		if (instance.consoleWorldEditRemoveMessage == null) {
			return null;
		}
		String world = min.getWorld().getName();
		String minCoords = BlockLocation.fromLocation(min).getCoords();
		String maxCoords = BlockLocation.fromLocation(max).getCoords();
		return replaceColorCode(instance.consoleWorldEditRemoveMessage.replace("%scripttype%", scriptType).replace("%world%", world).replace("%mincoords%", minCoords).replace("%maxcoords%", maxCoords));
	}

	public static String getConsoleSuccScriptExecMessage(Player player, ScriptType scriptType, World world, String coords) {
		if (instance.consoleSuccScriptExecMessage == null || !instance.isConsoleLog) {
			return null;
		}
		return replaceColorCode(instance.consoleSuccScriptExecMessage.replace("%player%", player.getName()).replace("%scripttype%", scriptType.getString()).replace("%world%", world.getName()).replace("%coords%", coords));
	}

	public static String getConsoleErrorScriptExecMessage(Player player, ScriptType scriptType, World world, String coords) {
		if (instance.consoleErrorScriptExecMessage == null || !instance.isConsoleLog) {
			return null;
		}
		return replaceColorCode(instance.consoleErrorScriptExecMessage.replace("%player%", player.getName()).replace("%scripttype%", scriptType.getString()).replace("%world%", world.getName()).replace("%coords%", coords));
	}

	public static void reload() {
		instance = new Messages();
	}

	private static String replaceColorCode(String str) {
		return str.replace("&", "§");
	}
}
