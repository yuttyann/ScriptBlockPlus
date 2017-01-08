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

import com.github.yuttyann.scriptblockplus.Permission;
import com.github.yuttyann.scriptblockplus.Prefix;
import com.github.yuttyann.scriptblockplus.collplugin.CollPlugins;
import com.github.yuttyann.scriptblockplus.collplugin.WorldEditAPI;
import com.github.yuttyann.scriptblockplus.command.help.CommandHelp;
import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.Messages;
import com.github.yuttyann.scriptblockplus.manager.EditManager;
import com.github.yuttyann.scriptblockplus.manager.MapManager;
import com.github.yuttyann.scriptblockplus.manager.MetadataManager;
import com.github.yuttyann.scriptblockplus.manager.MetadataManager.Click;
import com.github.yuttyann.scriptblockplus.manager.MetadataManager.ClickType;
import com.github.yuttyann.scriptblockplus.manager.MetadataManager.Edit;
import com.github.yuttyann.scriptblockplus.manager.MetadataManager.Script;
import com.github.yuttyann.scriptblockplus.manager.OptionManager.ScriptType;
import com.github.yuttyann.scriptblockplus.manager.ScriptFileManager;
import com.github.yuttyann.scriptblockplus.manager.ScriptManager;
import com.github.yuttyann.scriptblockplus.util.BlockLocation;
import com.github.yuttyann.scriptblockplus.util.Utils;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class ScriptBlockCommand extends Prefix implements TabExecutor {

	public ScriptBlockCommand() {
		CommandHelp help = new CommandHelp();
		help.put("scriptblockplus",
			help.getView().setAll("tool - 実行者にスクリプト編集ツールを配布します。", Permission.SCRIPTBLOCKPLUS_COMMAND_RELOAD, true),
			help.getView().setAll("reload - 全てのファイルの再読み込みを行います。", Permission.SCRIPTBLOCKPLUS_COMMAND_RELOAD, true),
			help.getView().setAll("interact create <options> - 実行後クリックしたブロックにスクリプトを設定します。", Permission.SCRIPTBLOCKPLUS_COMMAND_INTERACT, true),
			help.getView().setAll("interact add <options> - 実行後クリックしたブロックにスクリプトを追加します。", Permission.SCRIPTBLOCKPLUS_COMMAND_INTERACT, true),
			help.getView().setAll("interact remove - 実行後クリックしたブロックのスクリプトを削除します。", Permission.SCRIPTBLOCKPLUS_COMMAND_INTERACT, true),
			help.getView().setAll("interact view - 実行後クリックしたブロックのスクリプトを表示します。", Permission.SCRIPTBLOCKPLUS_COMMAND_INTERACT, true),
			help.getView().setAll("walk create <options> - 実行後クリックしたブロックにスクリプトを設定します。", Permission.SCRIPTBLOCKPLUS_COMMAND_WALK, true),
			help.getView().setAll("walk add <options> - 実行後クリックしたブロックにスクリプトを追加します。", Permission.SCRIPTBLOCKPLUS_COMMAND_WALK, true),
			help.getView().setAll("walk remove - 実行後クリックしたブロックのスクリプトを削除します。", Permission.SCRIPTBLOCKPLUS_COMMAND_WALK, true),
			help.getView().setAll("walk view - 実行後クリックしたブロックのスクリプトを表示します。", Permission.SCRIPTBLOCKPLUS_COMMAND_WALK, true),
			help.getView().setAll("worldedit paste - WorldEditで選択した範囲にスクリプトをペーストします。", Permission.SCRIPTBLOCKPLUS_COMMAND_WORLDEDIT, true),
			help.getView().setAll("worldedit remove - WorldEditで選択した範囲のスクリプトを削除します。", Permission.SCRIPTBLOCKPLUS_COMMAND_WORLDEDIT, true),
			help.getView().setAll("§c※スクリプトをペーストするためには編集ツールでコピーしておく必要があります。", Permission.SCRIPTBLOCKPLUS_COMMAND_WORLDEDIT, false)
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
				if (MetadataManager.hasAllMetadata(player)) {
					Utils.sendPluginMessage(player, Messages.getErrorEditDataMessage());
					return true;
				}
				Click.setMetadata(player, ClickType.INTERACT_REMOVE, true);
				Utils.sendPluginMessage(player, Messages.getSuccEditDataMessage(ClickType.INTERACT_REMOVE));
				return true;
			}
			if (args[0].equalsIgnoreCase("interact") && args[1].equalsIgnoreCase("view")) {
				if (!Permission.has(Permission.SCRIPTBLOCKPLUS_COMMAND_INTERACT, player)) {
					Utils.sendPluginMessage(player, "§cパーミッションが無いため、実行できません。");
					return true;
				}
				if (MetadataManager.hasAllMetadata(player)) {
					Utils.sendPluginMessage(player, Messages.getErrorEditDataMessage());
					return true;
				}
				Click.setMetadata(player, ClickType.INTERACT_VIEW, true);
				Utils.sendPluginMessage(player, Messages.getSuccEditDataMessage(ClickType.INTERACT_VIEW));
				return true;
			}
			if (args[0].equalsIgnoreCase("walk") && args[1].equalsIgnoreCase("remove")) {
				if (!Permission.has(Permission.SCRIPTBLOCKPLUS_COMMAND_WALK, player)) {
					Utils.sendPluginMessage(player, "§cパーミッションが無いため、実行できません。");
					return true;
				}
				if (MetadataManager.hasAllMetadata(player)) {
					Utils.sendPluginMessage(player, Messages.getErrorEditDataMessage());
					return true;
				}
				Click.setMetadata(player, ClickType.WALK_REMOVE, true);
				Utils.sendPluginMessage(player, Messages.getSuccEditDataMessage(ClickType.WALK_REMOVE));
				return true;
			}
			if (args[0].equalsIgnoreCase("walk") && args[1].equalsIgnoreCase("view")) {
				if (!Permission.has(Permission.SCRIPTBLOCKPLUS_COMMAND_WALK, player)) {
					Utils.sendPluginMessage(player, "§cパーミッションが無いため、実行できません。");
					return true;
				}
				if (MetadataManager.hasAllMetadata(player)) {
					Utils.sendPluginMessage(player, Messages.getErrorEditDataMessage());
					return true;
				}
				Click.setMetadata(player, ClickType.WALK_VIEW, true);
				Utils.sendPluginMessage(player, Messages.getSuccEditDataMessage(ClickType.WALK_VIEW));
				return true;
			}
			if (args[0].equalsIgnoreCase("worldedit") && args[1].equalsIgnoreCase("paste")) {
				if (!Permission.has(Permission.SCRIPTBLOCKPLUS_COMMAND_WORLDEDIT, player)) {
					Utils.sendPluginMessage(player, "§cパーミッションが無いため、実行できません。");
					return true;
				}
				if (!Edit.hasAllMetadata(player)) {
					Utils.sendPluginMessage(player, Messages.getErrorScriptFileCheckMessage());
					return true;
				}
				if (!CollPlugins.hasWorldEdit()) {
					Utils.sendPluginMessage(player, "§cWorldEditが導入されていないため、実行に失敗しました。");
					return true;
				}
				WorldEditAPI api = CollPlugins.getWorldEditAPI();
				Selection selection = api.getSelection(player);
				if (selection == null) {
					Utils.sendPluginMessage(player, "§cWorldEditで座標を選択してください。");
					return true;
				}
				Location min = selection.getMinimumPoint();
				Location max = selection.getMaximumPoint();
				List<Block> blocks = api.getSelectionBlocks(selection);
				MetadataManager.removeAllMetadata(player);
				EditManager edit = Edit.getMetadata(player);
				for (Block block : blocks) {
					if (block == null || block.getType() == Material.AIR) {
						continue;
					}
					edit.scriptWEPaste(player, new BlockLocation(block.getLocation()));
				}
				edit.save();
				ScriptType scriptType = Edit.getMetadata(player).getScriptType();
				Utils.sendPluginMessage(player, Messages.getWorldEditPasteMessage(scriptType));
				Utils.sendPluginMessage(Messages.getConsoleWorldEditPasteMessage(scriptType, min, max));
				return true;
			}
			if (args[0].equalsIgnoreCase("worldedit") && args[1].equalsIgnoreCase("remove")) {
				if (!Permission.has(Permission.SCRIPTBLOCKPLUS_COMMAND_WORLDEDIT, player)) {
					Utils.sendPluginMessage(player, "§cパーミッションが無いため、実行できません。");
					return true;
				}
				if (!CollPlugins.hasWorldEdit()) {
					Utils.sendPluginMessage(player, "§cWorldEditが導入されていないため、実行に失敗しました。");
					return true;
				}
				WorldEditAPI api = CollPlugins.getWorldEditAPI();
				Selection selection = api.getSelection(player);
				if (selection == null) {
					Utils.sendPluginMessage(player, "§cWorldEditで座標を選択してください。");
					return true;
				}
				Location min = selection.getMinimumPoint();
				Location max = selection.getMaximumPoint();
				List<Block> blocks = api.getSelectionBlocks(selection);
				MetadataManager.removeAllMetadata(player);
				boolean isInteract = false;
				boolean isWalk = false;
				for (Block block : blocks) {
					BlockLocation location = new BlockLocation(block.getLocation());
					ScriptFileManager interact = new ScriptFileManager(location, ScriptType.INTERACT);
					if (interact.checkPath()) {
						interact.scriptWERemove(player);
						if (!isInteract) {
							isInteract = true;
						}
					}
					ScriptFileManager walk = new ScriptFileManager(location, ScriptType.WALK);
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
					type += ScriptType.INTERACT.getString();
				}
				if (isWalk) {
					Files.getWalk().save();
					if (type.equals("")) {
						type = ScriptType.WALK.getString();
					} else {
						type += ", " + ScriptType.WALK.getString();
					}
				}
				if (type.equals("")) {
					Utils.sendPluginMessage(player, Messages.getErrorScriptFileCheckMessage());
					return true;
				}
				Utils.sendPluginMessage(player, Messages.getWorldEditRemoveMessage(type));
				Utils.sendPluginMessage(Messages.getConsoleWorldEditRemoveMessage(type, min, max));
				return true;
			}
		}
		if (args.length > 2) {
			if (args[0].equalsIgnoreCase("interact") && args[1].equalsIgnoreCase("create")) {
				if (!Permission.has(Permission.SCRIPTBLOCKPLUS_COMMAND_INTERACT, player)) {
					Utils.sendPluginMessage(player, "§cパーミッションが無いため、実行できません。");
					return true;
				}
				if (MetadataManager.hasAllMetadata(player)) {
					Utils.sendPluginMessage(player, Messages.getErrorEditDataMessage());
					return true;
				}
				String script = Utils.stringBuilder(args, 2).trim();
				ScriptManager manager = new ScriptManager(null, null);
				manager.reset();
				if (!manager.checkScript(script)) {
					Utils.sendPluginMessage(player, Messages.getErrorScriptCheckMessage());
					return true;
				}
				Click.setMetadata(player, ClickType.INTERACT_CREATE, true);
				Script.setMetadata(player, ClickType.INTERACT_CREATE, script);
				Utils.sendPluginMessage(player, Messages.getSuccEditDataMessage(ClickType.INTERACT_CREATE));
				return true;
			}
			if (args[0].equalsIgnoreCase("interact") && args[1].equalsIgnoreCase("add")) {
				if (!Permission.has(Permission.SCRIPTBLOCKPLUS_COMMAND_INTERACT, player)) {
					Utils.sendPluginMessage(player, "§cパーミッションが無いため、実行できません。");
					return true;
				}
				if (MetadataManager.hasAllMetadata(player)) {
					Utils.sendPluginMessage(player, Messages.getErrorEditDataMessage());
					return true;
				}
				String script = Utils.stringBuilder(args, 2).trim();
				ScriptManager manager = new ScriptManager(null, null);
				manager.reset();
				if (!manager.checkScript(script)) {
					Utils.sendPluginMessage(player, Messages.getErrorScriptCheckMessage());
					return true;
				}
				Click.setMetadata(player, ClickType.INTERACT_ADD, true);
				Script.setMetadata(player, ClickType.INTERACT_ADD, script);
				Utils.sendPluginMessage(player, Messages.getSuccEditDataMessage(ClickType.INTERACT_ADD));
				return true;
			}
			if (args[0].equalsIgnoreCase("walk") && args[1].equalsIgnoreCase("create")) {
				if (!Permission.has(Permission.SCRIPTBLOCKPLUS_COMMAND_WALK, player)) {
					Utils.sendPluginMessage(player, "§cパーミッションが無いため、実行できません。");
					return true;
				}
				if (MetadataManager.hasAllMetadata(player)) {
					Utils.sendPluginMessage(player, Messages.getErrorEditDataMessage());
					return true;
				}
				String script = Utils.stringBuilder(args, 2).trim();
				ScriptManager manager = new ScriptManager(null, null);
				manager.reset();
				if (!manager.checkScript(script)) {
					Utils.sendPluginMessage(player, Messages.getErrorScriptCheckMessage());
					return true;
				}
				Click.setMetadata(player, ClickType.WALK_CREATE, true);
				Script.setMetadata(player, ClickType.WALK_CREATE, script);
				Utils.sendPluginMessage(player, Messages.getSuccEditDataMessage(ClickType.WALK_CREATE));
				return true;
			}
			if (args[0].equalsIgnoreCase("walk") && args[1].equalsIgnoreCase("add")) {
				if (!Permission.has(Permission.SCRIPTBLOCKPLUS_COMMAND_WALK, player)) {
					Utils.sendPluginMessage(player, "§cパーミッションが無いため、実行できません。");
					return true;
				}
				if (MetadataManager.hasAllMetadata(player)) {
					Utils.sendPluginMessage(player, Messages.getErrorEditDataMessage());
					return true;
				}
				String script = Utils.stringBuilder(args, 2).trim();
				ScriptManager manager = new ScriptManager(null, null);
				manager.reset();
				if (!manager.checkScript(script)) {
					Utils.sendPluginMessage(player, Messages.getErrorScriptCheckMessage());
					return true;
				}
				Click.setMetadata(player, ClickType.WALK_ADD, true);
				Script.setMetadata(player, ClickType.WALK_ADD, script);
				Utils.sendPluginMessage(player, Messages.getSuccEditDataMessage(ClickType.WALK_ADD));
				return true;
			}
		}
		CommandHelp.sendHelpMessage(sender, command);
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 1) {
			String prefix = args[0].toLowerCase();
			ArrayList<String> commands = new ArrayList<String>();
			for (String c : new String[]{"tool", "reload", "interact", "walk", "worldedit"}) {
				if (c.startsWith(prefix)) {
					commands.add(c);
				}
			}
			return commands;
		}
		if (args.length == 2) {
			String arg0 = args[0];
			if (arg0.equalsIgnoreCase("tool") || arg0.equalsIgnoreCase("reload")) {
				return null;
			}
			ArrayList<String> commands = new ArrayList<String>();
			String prefix = args[1].toLowerCase();
			String[] args_;
			if (arg0.equalsIgnoreCase("worldedit")) {
				args_ = new String[]{"paste", "remove"};
			} else {
				args_ = new String[]{"create", "add", "remove", "view"};
			}
			for (String c : args_) {
				if (c.startsWith(prefix)) {
					commands.add(c);
				}
			}
			return commands;
		}
		if (args.length == 3) {
			String arg1 = args[1];
			if (arg1.equalsIgnoreCase("remove") || arg1.equalsIgnoreCase("view")) {
				return null;
			}
			ArrayList<String> commands = new ArrayList<String>();
			String prefix = args[2].toLowerCase();
			for (String c : prefixs) {
				if (c.startsWith(prefix)) {
					commands.add(c.trim());
				}
			}
			return commands;
		}
		return null;
	}
}
