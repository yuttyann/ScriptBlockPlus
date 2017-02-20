package com.github.yuttyann.scriptblockplus.command;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.yuttyann.scriptblockplus.BlockLocation;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.command.help.CommandData;
import com.github.yuttyann.scriptblockplus.command.help.CommandHelp;
import com.github.yuttyann.scriptblockplus.enums.ClickType;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.file.Configuration;
import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.Messages;
import com.github.yuttyann.scriptblockplus.file.ScriptData;
import com.github.yuttyann.scriptblockplus.hook.HookPlugins;
import com.github.yuttyann.scriptblockplus.hook.WorldEditSelection;
import com.github.yuttyann.scriptblockplus.manager.MapManager;
import com.github.yuttyann.scriptblockplus.manager.MetadataManager;
import com.github.yuttyann.scriptblockplus.manager.MetadataManager.Click;
import com.github.yuttyann.scriptblockplus.manager.MetadataManager.Script;
import com.github.yuttyann.scriptblockplus.manager.MetadataManager.ScriptFile;
import com.github.yuttyann.scriptblockplus.manager.ScriptFileManager;
import com.github.yuttyann.scriptblockplus.manager.ScriptReadManager;
import com.github.yuttyann.scriptblockplus.option.OptionPrefix;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class ScriptBlockPlusCommand extends OptionPrefix implements TabExecutor {

	private ScriptBlock plugin;
	private MapManager mapManager;

	public ScriptBlockPlusCommand(ScriptBlock plugin) {
		this.plugin = plugin;
		this.mapManager = plugin.getMapManager();
		CommandHelp help = new CommandHelp();
		help.putCommands(
			"scriptblockplus",
			new CommandData(Permission.SCRIPTBLOCKPLUS_COMMAND_TOOL),      //tool
			new CommandData(Permission.SCRIPTBLOCKPLUS_COMMAND_RELOAD),    //reload
			new CommandData(Permission.SCRIPTBLOCKPLUS_COMMAND_DATAMIGR),  //datamigr
			new CommandData().addPermissions(                              //<scripttype>_create
				Permission.SCRIPTBLOCKPLUS_COMMAND_INTERACT,
				Permission.SCRIPTBLOCKPLUS_COMMAND_BREAK,
				Permission.SCRIPTBLOCKPLUS_COMMAND_WALK
			),
			new CommandData().addPermissions(                              //<scripttype>_add
				Permission.SCRIPTBLOCKPLUS_COMMAND_INTERACT,
				Permission.SCRIPTBLOCKPLUS_COMMAND_BREAK,
				Permission.SCRIPTBLOCKPLUS_COMMAND_WALK
			),
			new CommandData().addPermissions(                              //<scripttype>_remove
				Permission.SCRIPTBLOCKPLUS_COMMAND_INTERACT,
				Permission.SCRIPTBLOCKPLUS_COMMAND_BREAK,
				Permission.SCRIPTBLOCKPLUS_COMMAND_WALK
			),
			new CommandData().addPermissions(                              //<scripttype>_view
				Permission.SCRIPTBLOCKPLUS_COMMAND_INTERACT,
				Permission.SCRIPTBLOCKPLUS_COMMAND_BREAK,
				Permission.SCRIPTBLOCKPLUS_COMMAND_WALK
			),
			new CommandData(Permission.SCRIPTBLOCKPLUS_COMMAND_WORLDEDIT), //worldedit_paste
			new CommandData(Permission.SCRIPTBLOCKPLUS_COMMAND_WORLDEDIT)  //worldedit_remove
		);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			Utils.sendPluginMessage(Messages.senderNoPlayerMessage);
			return true;
		}
		Player player = (Player) sender;
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("tool")) {
				if (!Permission.has(Permission.SCRIPTBLOCKPLUS_COMMAND_TOOL, player)) {
					Utils.sendPluginMessage(player, Messages.notPermissionMessage);
					return true;
				}
				giveTool(player);
				return true;
			}
			if (args[0].equalsIgnoreCase("reload")) {
				if (!Permission.has(Permission.SCRIPTBLOCKPLUS_COMMAND_RELOAD, player)) {
					Utils.sendPluginMessage(player, Messages.notPermissionMessage);
					return true;
				}
				Files.reload(plugin);
				mapManager.reloadAllScripts();
				Utils.sendPluginMessage(player, Messages.allFileReloadMessage);
				return true;
			}
			if (args[0].equalsIgnoreCase("datamigr")) {
				if (!Permission.has(Permission.SCRIPTBLOCKPLUS_COMMAND_DATAMIGR, player)) {
					Utils.sendPluginMessage(player, Messages.notPermissionMessage);
					return true;
				}
				sbDataMigr(player);
				return true;
			}
		}
		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("interact")
				&& (args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("view"))) {
				if (!Permission.has(Permission.SCRIPTBLOCKPLUS_COMMAND_INTERACT, player)) {
					Utils.sendPluginMessage(player, Messages.notPermissionMessage);
					return true;
				}
				ClickType type;
				if (args[1].equalsIgnoreCase("remove")) {
					type = ClickType.INTERACT_REMOVE;
				} else {
					type = ClickType.INTERACT_VIEW;
				}
				setClickMeta(player, type);
				return true;
			}
			if (args[0].equalsIgnoreCase("break")
				&& (args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("view"))) {
				if (!Permission.has(Permission.SCRIPTBLOCKPLUS_COMMAND_BREAK, player)) {
					Utils.sendPluginMessage(player, Messages.notPermissionMessage);
					return true;
				}
				ClickType type;
				if (args[1].equalsIgnoreCase("remove")) {
					type = ClickType.BREAK_REMOVE;
				} else {
					type = ClickType.BREAK_VIEW;
				}
				setClickMeta(player, type);
				return true;
			}
			if (args[0].equalsIgnoreCase("walk")
				&& (args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("view"))) {
				if (!Permission.has(Permission.SCRIPTBLOCKPLUS_COMMAND_WALK, player)) {
					Utils.sendPluginMessage(player, Messages.notPermissionMessage);
					return true;
				}
				ClickType type;
				if (args[1].equalsIgnoreCase("remove")) {
					type = ClickType.WALK_REMOVE;
				} else {
					type = ClickType.WALK_VIEW;
				}
				setClickMeta(player, type);
				return true;
			}
			if (args[0].equalsIgnoreCase("worldedit") && args[1].equalsIgnoreCase("remove")) {
				if (!Permission.has(Permission.SCRIPTBLOCKPLUS_COMMAND_WORLDEDIT, player)) {
					Utils.sendPluginMessage(player, Messages.notPermissionMessage);
					return true;
				}
				scriptWERemove(player);
				return true;
			}
		}
		if (args.length > 2) {
			if (args.length == 3 && args[0].equalsIgnoreCase("worldedit") && args[1].equalsIgnoreCase("paste")) {
				if (!Permission.has(Permission.SCRIPTBLOCKPLUS_COMMAND_WORLDEDIT, player)) {
					Utils.sendPluginMessage(player, Messages.notPermissionMessage);
					return true;
				}
				scriptWEPaste(player, false, Boolean.parseBoolean(args[2]));
				return true;
			}
			if (args.length == 4 && args[0].equalsIgnoreCase("worldedit") && args[1].equalsIgnoreCase("paste")) {
				if (!Permission.has(Permission.SCRIPTBLOCKPLUS_COMMAND_WORLDEDIT, player)) {
					Utils.sendPluginMessage(player, Messages.notPermissionMessage);
					return true;
				}
				scriptWEPaste(player, Boolean.parseBoolean(args[2]), Boolean.parseBoolean(args[3]));
				return true;
			}
			if (args[0].equalsIgnoreCase("interact")
				&& (args[1].equalsIgnoreCase("create") || args[1].equalsIgnoreCase("add"))) {
				if (!Permission.has(Permission.SCRIPTBLOCKPLUS_COMMAND_INTERACT, player)) {
					Utils.sendPluginMessage(player, Messages.notPermissionMessage);
					return true;
				}
				ClickType type;
				if (args[1].equalsIgnoreCase("create")) {
					type = ClickType.INTERACT_CREATE;
				} else {
					type = ClickType.INTERACT_ADD;
				}
				setClickMeta(player, args, type);
				return true;
			}
			if (args[0].equalsIgnoreCase("break")
				&& (args[1].equalsIgnoreCase("create") || args[1].equalsIgnoreCase("add"))) {
				if (!Permission.has(Permission.SCRIPTBLOCKPLUS_COMMAND_BREAK, player)) {
					Utils.sendPluginMessage(player, Messages.notPermissionMessage);
					return true;
				}
				ClickType type;
				if (args[1].equalsIgnoreCase("create")) {
					type = ClickType.BREAK_CREATE;
				} else {
					type = ClickType.BREAK_ADD;
				}
				setClickMeta(player, args, type);
				return true;
			}
			if (args[0].equalsIgnoreCase("walk")
				&& (args[1].equalsIgnoreCase("create") || args[1].equalsIgnoreCase("add"))) {
				if (!Permission.has(Permission.SCRIPTBLOCKPLUS_COMMAND_WALK, player)) {
					Utils.sendPluginMessage(player, Messages.notPermissionMessage);
					return true;
				}
				ClickType type;
				if (args[1].equalsIgnoreCase("create")) {
					type = ClickType.WALK_CREATE;
				} else {
					type = ClickType.WALK_ADD;
				}
				setClickMeta(player, args, type);
				return true;
			}
		}
		CommandHelp.sendHelpMessage(sender, command, false);
		return true;
	}

	private void giveTool(Player player) {
		ItemStack item = new ItemStack(Material.BLAZE_ROD);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§dScript Editor");
		meta.setLore(Arrays.asList(
			"§aスクリプトをコピー、ペーストするツールです。",
			"§6左クリック: §e種類\"interact\"のスクリプトをコピーします。",
			"§6右クリック: §e種類\"break\"のスクリプトをコピーします。",
			"§6シフトand左クリック: §e種類\"walk\"のスクリプトをコピーします。",
			"§6シフトand右クリック: §eスクリプトをペーストします。")
		);
		item.setItemMeta(meta);
		player.getInventory().addItem(item);
		Utils.updateInventory(player);
		Utils.sendPluginMessage(player, Messages.giveToolMessage);
	}

	private void sbDataMigr(Player player) {
		File interactFile = new File("plugins/ScriptBlock/BlocksData/interact_Scripts.yml");
		File walkFile = new File("plugins/ScriptBlock/BlocksData/walk_Scripts.yml");
		boolean interactExists = interactFile.exists();
		boolean walkExists = walkFile.exists();
		if (!interactExists && !walkExists) {
			Utils.sendPluginMessage(player, Messages.notScriptBlockFileMessage);
			return;
		}
		Utils.sendPluginMessage(player, Messages.dataMigrStartMessage);
		Configuration config;
		if (interactExists) {
			config = Configuration.loadConfiguration(interactFile);
			ScriptData scriptData = new ScriptData(plugin, (BlockLocation) null, ScriptType.INTERACT);
			for (String world : config.getKeys()) {
				World temp = Utils.getWorld(world);
				for (String coords : config.getKeys(world)) {
					List<String> scripts = config.getStringList(world + "." + coords, null);
					if (scripts.size() > 0 && scripts.get(0).startsWith("Author:")) {
						scripts.remove(0);
					}
					String[] array = StringUtils.split(coords, ",");
					scriptData.setBlockLocation(new BlockLocation(
						temp,
						Integer.parseInt(array[0]),
						Integer.parseInt(array[1]),
						Integer.parseInt(array[2]))
					);
					scriptData.setAuthor(player);
					scriptData.setLastEdit();
					scriptData.setScripts(scripts);
				}
			}
			scriptData.save();
			mapManager.reloadScripts(scriptData.getScriptFile(), scriptData.getScriptType());
		}
		if (walkExists) {
			config = Configuration.loadConfiguration(walkFile);
			ScriptData scriptData = new ScriptData(plugin, (BlockLocation) null, ScriptType.WALK);
			for (String world : config.getKeys()) {
				World temp = Utils.getWorld(world);
				for (String coords : config.getKeys(world)) {
					List<String> scripts = config.getStringList(world + "." + coords, null);
					if (scripts.size() > 0 && scripts.get(0).startsWith("Author:")) {
						scripts.remove(0);
					}
					String[] array = StringUtils.split(coords, ",");
					scriptData.setBlockLocation(new BlockLocation(
						temp,
						Integer.parseInt(array[0]),
						Integer.parseInt(array[1]),
						Integer.parseInt(array[2]))
					);
					scriptData.setAuthor(player);
					scriptData.setLastEdit();
					scriptData.setScripts(scripts);
				}
			}
			scriptData.save();
			mapManager.reloadScripts(scriptData.getScriptFile(), scriptData.getScriptType());
		}
		Utils.sendPluginMessage(player, Messages.dataMigrEndMessage);
	}

	private void setClickMeta(Player player, ClickType clickType) {
		if (MetadataManager.hasAllMetadata(player)) {
			Utils.sendPluginMessage(player, Messages.getErrorEditDataMessage());
			return;
		}
		Click.setMetadata(plugin, player, clickType, true);
		Utils.sendPluginMessage(player, Messages.getSuccEditDataMessage(clickType));
	}

	private void setClickMeta(Player player, String[] args, ClickType clickType) {
		if (MetadataManager.hasAllMetadata(player)) {
			Utils.sendPluginMessage(player, Messages.getErrorEditDataMessage());
			return;
		}
		String script = StringUtils.createString(args, 2).trim();
		ScriptReadManager readManager = new ScriptReadManager(plugin, null, null);
		readManager.reset();
		if (!readManager.checkScript(script)) {
			Utils.sendPluginMessage(player, Messages.getErrorScriptCheckMessage());
			return;
		}
		Click.setMetadata(plugin, player, clickType, true);
		Script.setMetadata(plugin, player, clickType, script);
		Utils.sendPluginMessage(player, Messages.getSuccEditDataMessage(clickType));
	}

	private void scriptWERemove(Player player) {
		if (!HookPlugins.hasWorldEdit()) {
			Utils.sendPluginMessage(player, Messages.notWorldEditMessage);
			return;
		}
		WorldEditSelection selectionAPI = HookPlugins.getWorldEditSelection();
		Selection selection = selectionAPI.getSelection(player);
		if (selection == null) {
			Utils.sendPluginMessage(player, Messages.getWorldEditNotSelectionMessage());
			return;
		}
		MetadataManager.removeAllMetadata(plugin, player);
		boolean isInteract = false, isBreak = false, isWalk = false;
		for (Block block : selectionAPI.getSelectionBlocks(selection)) {
			BlockLocation location = BlockLocation.fromLocation(block.getLocation());
			ScriptFileManager interact = new ScriptFileManager(plugin, location, ScriptType.INTERACT);
			ScriptFileManager break_ = new ScriptFileManager(plugin, location, ScriptType.BREAK);
			ScriptFileManager walk = new ScriptFileManager(plugin, location, ScriptType.WALK);
			if (interact.checkPath()) {
				interact.scriptWERemove(player);
				if (!isInteract) {
					isInteract = true;
				}
			}
			if (break_.checkPath()) {
				break_.scriptWERemove(player);
				if (!isBreak) {
					isBreak = true;
				}
			}
			if (walk.checkPath()) {
				walk.scriptWERemove(player);
				if (!isWalk) {
					isWalk = true;
				}
			}
		}
		String type = "";
		if (isInteract) {
			Files.getInteract().save();
			type = ScriptType.INTERACT.toString();
		}
		if (isBreak) {
			Files.getBreak().save();
			if (type.length() == 0) {
				type = ScriptType.BREAK.toString();
			} else {
				type += ", " + ScriptType.BREAK.toString();
			}
		}
		if (isWalk) {
			Files.getWalk().save();
			if (type.length() == 0) {
				type = ScriptType.WALK.toString();
			} else {
				type += ", " + ScriptType.WALK.toString();
			}
		}
		if (type.length() == 0) {
			Utils.sendPluginMessage(player, Messages.getErrorScriptFileCheckMessage());
			return;
		}
		Utils.sendPluginMessage(player, Messages.getWorldEditRemoveMessage(type));
		Utils.sendPluginMessage(Messages.getConsoleWorldEditRemoveMessage(type, selection.getMinimumPoint(), selection.getMaximumPoint()));
	}

	private void scriptWEPaste(Player player, boolean pasteonair, boolean overwrite) {
		if (!ScriptFile.hasAllMetadata(player)) {
			Utils.sendPluginMessage(player, Messages.getErrorScriptFileCheckMessage());
			return;
		}
		if (!HookPlugins.hasWorldEdit()) {
			Utils.sendPluginMessage(player, Messages.notWorldEditMessage);
			return;
		}
		WorldEditSelection selectionAPI = HookPlugins.getWorldEditSelection();
		Selection selection = selectionAPI.getSelection(player);
		if (selection == null) {
			Utils.sendPluginMessage(player, Messages.getWorldEditNotSelectionMessage());
			return;
		}
		MetadataManager.removeAllMetadata(plugin, player);
		ScriptFileManager fileManager = ScriptFile.getMetadata(player);
		for (Block block : selectionAPI.getSelectionBlocks(selection)) {
			if (!pasteonair && (block == null || block.getType() == Material.AIR)) {
				continue;
			}
			fileManager.scriptWEPaste(player, BlockLocation.fromLocation(block.getLocation()), overwrite);
		}
		fileManager.save();
		ScriptType scriptType = ScriptFile.getMetadata(player).getScriptType();
		Utils.sendPluginMessage(player, Messages.getWorldEditPasteMessage(scriptType));
		Utils.sendPluginMessage(Messages.getConsoleWorldEditPasteMessage(scriptType, selection.getMinimumPoint(), selection.getMaximumPoint()));
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 1) {
			String[] args_ = {
				perm(sender, "tool", Permission.SCRIPTBLOCKPLUS_COMMAND_TOOL),
				perm(sender, "reload", Permission.SCRIPTBLOCKPLUS_COMMAND_RELOAD),
				perm(sender, "datamigr", Permission.SCRIPTBLOCKPLUS_COMMAND_DATAMIGR),
				perm(sender, "interact", Permission.SCRIPTBLOCKPLUS_COMMAND_INTERACT),
				perm(sender, "break", Permission.SCRIPTBLOCKPLUS_COMMAND_BREAK),
				perm(sender, "walk", Permission.SCRIPTBLOCKPLUS_COMMAND_WALK),
				perm(sender, "worldedit", Permission.SCRIPTBLOCKPLUS_COMMAND_WORLDEDIT)
			};
			String prefix = args[0].toLowerCase();
			List<String> commands = new ArrayList<String>();
			for (String c : args_) {
				if (c != null && c.startsWith(prefix)) {
					commands.add(c);
				}
			}
			if (!commands.isEmpty()) {
				return commands;
			}
		}
		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("tool") || args[0].equalsIgnoreCase("datamigr")
					|| args[0].equalsIgnoreCase("reload")) {
				return null;
			}
			Permission perm;
			String[] args_;
			if (args[0].equalsIgnoreCase("worldedit")) {
				perm = Permission.SCRIPTBLOCKPLUS_COMMAND_WORLDEDIT;
				args_ = new String[]{"paste", "remove"};
			} else {
				if (args[0].equalsIgnoreCase("interact")) {
					perm = Permission.SCRIPTBLOCKPLUS_COMMAND_INTERACT;
				} else if (args[0].equalsIgnoreCase("break")) {
					perm = Permission.SCRIPTBLOCKPLUS_COMMAND_BREAK;
				} else {
					perm = Permission.SCRIPTBLOCKPLUS_COMMAND_WALK;
				}
				args_ = new String[]{"create", "add", "remove", "view"};
			}
			if ((args_ = perm(sender, args_, perm)) == null) {
				return null;
			}
			String prefix = args[1].toLowerCase();
			List<String> commands = new ArrayList<String>();
			for (String c : args_) {
				if (c.startsWith(prefix)) {
					commands.add(c);
				}
			}
			return commands;
		}
		if (args.length == 3) {
			if (args[0].equalsIgnoreCase("tool") || args[0].equalsIgnoreCase("reload")
					|| args[0].equalsIgnoreCase("datamigr") || args[1].equalsIgnoreCase("remove")
					|| args[1].equalsIgnoreCase("view")) {
				return null;
			}
			Permission perm;
			String[] args_;
			if (args[1].equalsIgnoreCase("paste")) {
				perm = Permission.SCRIPTBLOCKPLUS_COMMAND_WORLDEDIT;
				args_ = new String[]{"true", "false"};
			} else {
				if (args[0].equalsIgnoreCase("interact")) {
					perm = Permission.SCRIPTBLOCKPLUS_COMMAND_INTERACT;
				} else if (args[0].equalsIgnoreCase("break")) {
					perm = Permission.SCRIPTBLOCKPLUS_COMMAND_BREAK;
				} else {
					perm = Permission.SCRIPTBLOCKPLUS_COMMAND_WALK;
				}
				args_ = PREFIXS;
			}
			if ((args_ = perm(sender, args_, perm)) == null) {
				return null;
			}
			String prefix = args[2].toLowerCase();
			List<String> commands = new ArrayList<String>();
			for (String c : args_) {
				if (c.startsWith(prefix)) {
					commands.add(c.trim());
				}
			}
			return commands;
		}
		if (args.length == 4 && args[0].equalsIgnoreCase("worldedit") && args[1].equalsIgnoreCase("paste")) {
			Permission perm = Permission.SCRIPTBLOCKPLUS_COMMAND_WORLDEDIT;
			String[] args_ = new String[]{"true", "false"};;
			if ((args_ = perm(sender, args_, perm)) == null) {
				return null;
			}
			String prefix = args[3].toLowerCase();
			List<String> commands = new ArrayList<String>();
			for (String c : args_) {
				if (c.startsWith(prefix)) {
					commands.add(c);
				}
			}
			return commands;
		}
		return null;
	}

	private String perm(CommandSender sender, String command, Object permission) {
		if (permission == null) {
			return null;
		}
		if (sender.hasPermission(getNode(permission))) {
			return command;
		}
		return null;
	}

	private String[] perm(CommandSender sender, String[] commands, Object permission) {
		if (permission == null) {
			return null;
		}
		if (sender.hasPermission(getNode(permission))) {
			return commands;
		}
		return null;
	}

	private String getNode(Object permission) {
		return (permission instanceof Permission ? ((Permission) permission).getNode() : permission.toString());
	}
}