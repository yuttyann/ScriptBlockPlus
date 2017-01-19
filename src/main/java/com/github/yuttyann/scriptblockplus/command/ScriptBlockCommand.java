package com.github.yuttyann.scriptblockplus.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.yuttyann.scriptblockplus.collplugin.CollPlugins;
import com.github.yuttyann.scriptblockplus.collplugin.WorldEditSelection;
import com.github.yuttyann.scriptblockplus.command.help.CommandData;
import com.github.yuttyann.scriptblockplus.command.help.CommandHelp;
import com.github.yuttyann.scriptblockplus.enums.ClickType;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.Messages;
import com.github.yuttyann.scriptblockplus.manager.EditManager;
import com.github.yuttyann.scriptblockplus.manager.MapManager;
import com.github.yuttyann.scriptblockplus.manager.MetadataManager;
import com.github.yuttyann.scriptblockplus.manager.MetadataManager.Click;
import com.github.yuttyann.scriptblockplus.manager.MetadataManager.Edit;
import com.github.yuttyann.scriptblockplus.manager.MetadataManager.Script;
import com.github.yuttyann.scriptblockplus.manager.ScriptFileManager;
import com.github.yuttyann.scriptblockplus.manager.ScriptManager;
import com.github.yuttyann.scriptblockplus.option.OptionPrefix;
import com.github.yuttyann.scriptblockplus.utils.BlockLocation;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class ScriptBlockCommand extends OptionPrefix implements TabExecutor {

	public ScriptBlockCommand() {
		CommandHelp help = new CommandHelp();
		help.putCommands("scriptblockplus",
			new CommandData(Permission.SCRIPTBLOCKPLUS_COMMAND_TOOL),      //tool
			new CommandData(Permission.SCRIPTBLOCKPLUS_COMMAND_RELOAD),    //reload
			new CommandData(Permission.SCRIPTBLOCKPLUS_COMMAND_INTERACT),  //interact_create
			new CommandData(Permission.SCRIPTBLOCKPLUS_COMMAND_INTERACT),  //interact_add
			new CommandData(Permission.SCRIPTBLOCKPLUS_COMMAND_INTERACT),  //interact_remove
			new CommandData(Permission.SCRIPTBLOCKPLUS_COMMAND_INTERACT),  //interact_view
			new CommandData(Permission.SCRIPTBLOCKPLUS_COMMAND_WALK),      //walk_create
			new CommandData(Permission.SCRIPTBLOCKPLUS_COMMAND_WALK),      //walk_add
			new CommandData(Permission.SCRIPTBLOCKPLUS_COMMAND_WALK),      //walk_remove
			new CommandData(Permission.SCRIPTBLOCKPLUS_COMMAND_WALK),      //walk_view
			new CommandData(Permission.SCRIPTBLOCKPLUS_COMMAND_WORLDEDIT), //worldedit_paste
			new CommandData(Permission.SCRIPTBLOCKPLUS_COMMAND_WORLDEDIT)  //worldedit_remove
		);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			Utils.sendPluginMessage("§cコマンドはゲーム内から実行してください。");
			return true;
		}
		Player player = (Player) sender;
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("tool")) {
				if (!Permission.has(Permission.SCRIPTBLOCKPLUS_COMMAND_TOOL, player)) {
					Utils.sendPluginMessage(player, "§cパーミッションが無いため、実行できません。");
					return true;
				}
				giveTool(player);
				return true;
			}
			if (args[0].equalsIgnoreCase("reload")) {
				if (!Permission.has(Permission.SCRIPTBLOCKPLUS_COMMAND_RELOAD, player)) {
					Utils.sendPluginMessage(player, "§cパーミッションが無いため、実行できません。");
					return true;
				}
				Files.reload();
				MapManager.putAllScripts();
				Utils.sendPluginMessage(player, "§a全てのファイルの再読み込みが完了しました。");
				return true;
			}
		}
		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("interact") && args[1].equalsIgnoreCase("remove")) {
				if (!Permission.has(Permission.SCRIPTBLOCKPLUS_COMMAND_INTERACT, player)) {
					Utils.sendPluginMessage(player, "§cパーミッションが無いため、実行できません。");
					return true;
				}
				setClickMeta(player, ClickType.INTERACT_REMOVE);
				return true;
			}
			if (args[0].equalsIgnoreCase("interact") && args[1].equalsIgnoreCase("view")) {
				if (!Permission.has(Permission.SCRIPTBLOCKPLUS_COMMAND_INTERACT, player)) {
					Utils.sendPluginMessage(player, "§cパーミッションが無いため、実行できません。");
					return true;
				}
				setClickMeta(player, ClickType.INTERACT_VIEW);
				return true;
			}
			if (args[0].equalsIgnoreCase("walk") && args[1].equalsIgnoreCase("remove")) {
				if (!Permission.has(Permission.SCRIPTBLOCKPLUS_COMMAND_WALK, player)) {
					Utils.sendPluginMessage(player, "§cパーミッションが無いため、実行できません。");
					return true;
				}
				setClickMeta(player, ClickType.WALK_REMOVE);
				return true;
			}
			if (args[0].equalsIgnoreCase("walk") && args[1].equalsIgnoreCase("view")) {
				if (!Permission.has(Permission.SCRIPTBLOCKPLUS_COMMAND_WALK, player)) {
					Utils.sendPluginMessage(player, "§cパーミッションが無いため、実行できません。");
					return true;
				}
				setClickMeta(player, ClickType.WALK_VIEW);
				return true;
			}
			if (args[0].equalsIgnoreCase("worldedit") && args[1].equalsIgnoreCase("paste")) {
				if (!Permission.has(Permission.SCRIPTBLOCKPLUS_COMMAND_WORLDEDIT, player)) {
					Utils.sendPluginMessage(player, "§cパーミッションが無いため、実行できません。");
					return true;
				}
				scriptWEPaste(player, true);
				return true;
			}
			if (args[0].equalsIgnoreCase("worldedit") && args[1].equalsIgnoreCase("remove")) {
				if (!Permission.has(Permission.SCRIPTBLOCKPLUS_COMMAND_WORLDEDIT, player)) {
					Utils.sendPluginMessage(player, "§cパーミッションが無いため、実行できません。");
					return true;
				}
				scriptWERemove(player);
				return true;
			}
		}
		if (args.length == 3) {
			if (args[0].equalsIgnoreCase("worldedit") && args[1].equalsIgnoreCase("paste")) {
				if (!Permission.has(Permission.SCRIPTBLOCKPLUS_COMMAND_WORLDEDIT, player)) {
					Utils.sendPluginMessage(player, "§cパーミッションが無いため、実行できません。");
					return true;
				}
				scriptWEPaste(player, Boolean.parseBoolean(args[2]));
				return true;
			}
		}
		if (args.length > 2) {
			if (args[0].equalsIgnoreCase("interact") && args[1].equalsIgnoreCase("create")) {
				if (!Permission.has(Permission.SCRIPTBLOCKPLUS_COMMAND_INTERACT, player)) {
					Utils.sendPluginMessage(player, "§cパーミッションが無いため、実行できません。");
					return true;
				}
				setClickMeta(player, args, ClickType.INTERACT_CREATE);
				return true;
			}
			if (args[0].equalsIgnoreCase("interact") && args[1].equalsIgnoreCase("add")) {
				if (!Permission.has(Permission.SCRIPTBLOCKPLUS_COMMAND_INTERACT, player)) {
					Utils.sendPluginMessage(player, "§cパーミッションが無いため、実行できません。");
					return true;
				}
				setClickMeta(player, args, ClickType.INTERACT_ADD);
				return true;
			}
			if (args[0].equalsIgnoreCase("walk") && args[1].equalsIgnoreCase("create")) {
				if (!Permission.has(Permission.SCRIPTBLOCKPLUS_COMMAND_WALK, player)) {
					Utils.sendPluginMessage(player, "§cパーミッションが無いため、実行できません。");
					return true;
				}
				setClickMeta(player, args, ClickType.WALK_CREATE);
				return true;
			}
			if (args[0].equalsIgnoreCase("walk") && args[1].equalsIgnoreCase("add")) {
				if (!Permission.has(Permission.SCRIPTBLOCKPLUS_COMMAND_WALK, player)) {
					Utils.sendPluginMessage(player, "§cパーミッションが無いため、実行できません。");
					return true;
				}
				setClickMeta(player, args, ClickType.WALK_ADD);
				return true;
			}
		}
		CommandHelp.sendHelpMessage(sender, command);
		return true;
	}

	private void giveTool(Player player) {
		ItemStack item = new ItemStack(Material.BLAZE_ROD);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§dScript Editor");
		meta.setLore(Arrays.asList(
			"§aスクリプトをコピー、削除するツールです。",
			"§6左クリック: §e種類\"interact\"のスクリプトをコピーします。",
			"§6右クリック: §e種類\"walk\"のスクリプトをコピーします。",
			"§6シフトand左クリック: §eスクリプトをペーストします。",
			"§6シフトand右クリック: §eスクリプトを削除します。")
		);
		item.setItemMeta(meta);
		player.getInventory().addItem(item);
		Utils.sendPluginMessage(player, "§aScript Editorが配布されました。");
	}

	private void setClickMeta(Player player, ClickType clickType) {
		if (MetadataManager.hasAllMetadata(player)) {
			Utils.sendPluginMessage(player, Messages.getErrorEditDataMessage());
			return;
		}
		Click.setMetadata(player, clickType, true);
		Utils.sendPluginMessage(player, Messages.getSuccEditDataMessage(clickType));
	}

	private void setClickMeta(Player player, String[] args, ClickType clickType) {
		if (MetadataManager.hasAllMetadata(player)) {
			Utils.sendPluginMessage(player, Messages.getErrorEditDataMessage());
			return;
		}
		String script = Utils.stringBuilder(args, 2).trim();
		ScriptManager manager = new ScriptManager(null, null);
		manager.reset();
		if (!manager.checkScript(script)) {
			Utils.sendPluginMessage(player, Messages.getErrorScriptCheckMessage());
			return;
		}
		Click.setMetadata(player, clickType, true);
		Script.setMetadata(player, clickType, script);
		Utils.sendPluginMessage(player, Messages.getSuccEditDataMessage(clickType));
	}

	private void scriptWERemove(Player player) {
		if (!CollPlugins.hasWorldEdit()) {
			Utils.sendPluginMessage(player, Messages.getNotWorldEditMessage());
			return;
		}
		WorldEditSelection selectionAPI = CollPlugins.getWorldEditSelection();
		Selection selection = selectionAPI.getSelection(player);
		if (selection == null) {
			Utils.sendPluginMessage(player, Messages.getWorldEditNotSelectionMessage());
			return;
		}
		MetadataManager.removeAllMetadata(player);
		Location min = selection.getMinimumPoint();
		Location max = selection.getMaximumPoint();
		List<Block> blocks = selectionAPI.getSelectionBlocks(selection);
		boolean isInteract = false;
		boolean isWalk = false;
		BlockLocation location;
		ScriptFileManager interact;
		ScriptFileManager walk;
		for (Block block : blocks) {
			location = BlockLocation.fromLocation(block.getLocation());
			interact = new ScriptFileManager(location, ScriptType.INTERACT);
			if (interact.checkPath()) {
				interact.scriptWERemove(player);
				if (!isInteract) {
					isInteract = true;
				}
			}
			walk = new ScriptFileManager(location, ScriptType.WALK);
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
		Utils.sendPluginMessage(Messages.getConsoleWorldEditRemoveMessage(type, min, max));
	}

	private void scriptWEPaste(Player player, boolean overwrite) {
		if (!Edit.hasAllMetadata(player)) {
			Utils.sendPluginMessage(player, Messages.getErrorScriptFileCheckMessage());
			return;
		}
		if (!CollPlugins.hasWorldEdit()) {
			Utils.sendPluginMessage(player, Messages.getNotWorldEditMessage());
			return;
		}
		WorldEditSelection selectionAPI = CollPlugins.getWorldEditSelection();
		Selection selection = selectionAPI.getSelection(player);
		if (selection == null) {
			Utils.sendPluginMessage(player, Messages.getWorldEditNotSelectionMessage());
			return;
		}
		Location min = selection.getMinimumPoint();
		Location max = selection.getMaximumPoint();
		List<Block> blocks = selectionAPI.getSelectionBlocks(selection);
		MetadataManager.removeAllMetadata(player);
		EditManager edit = Edit.getMetadata(player);
		for (Block block : blocks) {
			if (block == null || block.getType() == Material.AIR) {
				continue;
			}
			edit.scriptWEPaste(player, BlockLocation.fromLocation(block.getLocation()), overwrite);
		}
		edit.save();
		ScriptType scriptType = Edit.getMetadata(player).getScriptType();
		Utils.sendPluginMessage(player, Messages.getWorldEditPasteMessage(scriptType));
		Utils.sendPluginMessage(Messages.getConsoleWorldEditPasteMessage(scriptType, min, max));
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 1) {
			String[] args_ = {
				perm(sender, "tool", Permission.SCRIPTBLOCKPLUS_COMMAND_TOOL),
				perm(sender, "reload", Permission.SCRIPTBLOCKPLUS_COMMAND_RELOAD),
				perm(sender, "interact", Permission.SCRIPTBLOCKPLUS_COMMAND_INTERACT),
				perm(sender, "walk", Permission.SCRIPTBLOCKPLUS_COMMAND_WALK),
				perm(sender, "worldedit", Permission.SCRIPTBLOCKPLUS_COMMAND_WORLDEDIT)
			};
			String prefix = args[0].toLowerCase();
			ArrayList<String> commands = new ArrayList<String>();
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
			if (args[0].equalsIgnoreCase("tool") || args[0].equalsIgnoreCase("reload")) {
				return null;
			}
			String node;
			String[] args_;
			if (args[0].equalsIgnoreCase("worldedit")) {
				node = Permission.SCRIPTBLOCKPLUS_COMMAND_WORLDEDIT.getNode();
				args_ = new String[]{"paste", "remove"};
			} else {
				if (args[0].equalsIgnoreCase("interact")) {
					node = Permission.SCRIPTBLOCKPLUS_COMMAND_INTERACT.getNode();
				} else {
					node = Permission.SCRIPTBLOCKPLUS_COMMAND_WALK.getNode();
				}
				args_ = new String[]{"create", "add", "remove", "view"};
			}
			if ((args_ = perm(sender, args_, node)) == null) {
				return null;
			}
			String prefix = args[1].toLowerCase();
			ArrayList<String> commands = new ArrayList<String>();
			for (String c : args_) {
				if (c.startsWith(prefix)) {
					commands.add(c);
				}
			}
			return commands;
		}
		if (args.length == 3) {
			if (args[0].equalsIgnoreCase("tool") || args[0].equalsIgnoreCase("reload")
					|| args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("view")) {
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
				} else {
					perm = Permission.SCRIPTBLOCKPLUS_COMMAND_WALK;
				}
				args_ = PREFIXS;
			}
			if ((args_ = perm(sender, args_, perm)) == null) {
				return null;
			}
			String prefix = args[2].toLowerCase();
			ArrayList<String> commands = new ArrayList<String>();
			for (String c : args_) {
				if (c.startsWith(prefix)) {
					commands.add(c.trim());
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