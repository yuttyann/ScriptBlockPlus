package com.github.yuttyann.scriptblockplus.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.yuttyann.scriptblockplus.Permission;
import com.github.yuttyann.scriptblockplus.command.help.CommandHelp;
import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.Messages;
import com.github.yuttyann.scriptblockplus.manager.MetadataManager;
import com.github.yuttyann.scriptblockplus.manager.MetadataManager.Click;
import com.github.yuttyann.scriptblockplus.manager.MetadataManager.ClickType;
import com.github.yuttyann.scriptblockplus.manager.MetadataManager.Script;
import com.github.yuttyann.scriptblockplus.manager.ScriptManager;
import com.github.yuttyann.scriptblockplus.util.Utils;

public class ScriptBlockCommand implements TabExecutor {

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
			help.getView().setAll("walk view - 実行後クリックしたブロックのスクリプトを表示します。", Permission.SCRIPTBLOCKPLUS_COMMAND_WALK, true)
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
					"§6シフト&左クリック: §eスクリプトをペーストします。",
					"§6シフト&右クリック: §eスクリプトを削除します。")
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
				ScriptManager manager = new ScriptManager();
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
				ScriptManager manager = new ScriptManager();
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
				ScriptManager manager = new ScriptManager();
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
				ScriptManager manager = new ScriptManager();
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
			for (String c : new String[]{"tool", "reload", "interact", "walk"}) {
				if (c.startsWith(prefix)) {
					commands.add(c);
				}
			}
			return commands;
		}
		if (args.length == 2) {
			String prefix = args[1].toLowerCase();
			ArrayList<String> commands = new ArrayList<String>();
			for (String c : new String[]{"create", "add", "remove", "view"}) {
				if (c.startsWith(prefix)) {
					commands.add(c);
				}
			}
			return commands;
		}
		return null;
	}
}
