package com.github.yuttyann.scriptblockplus.command;

import java.io.File;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.Updater;
import com.github.yuttyann.scriptblockplus.command.help.CommandData;
import com.github.yuttyann.scriptblockplus.enums.ClickType;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.file.YamlConfig;
import com.github.yuttyann.scriptblockplus.manager.OptionManager;
import com.github.yuttyann.scriptblockplus.script.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.ScriptData;
import com.github.yuttyann.scriptblockplus.script.ScriptEdit;
import com.github.yuttyann.scriptblockplus.script.ScriptEdit.Clipboard;
import com.github.yuttyann.scriptblockplus.script.hook.HookPlugins;
import com.github.yuttyann.scriptblockplus.script.hook.WorldEditSelection;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class ScriptBlockPlusCommand extends BaseCommand {

	public ScriptBlockPlusCommand(ScriptBlock plugin) {
		super(plugin, "scriptblockplus", true,
			new CommandData(
				SBConfig.getToolCommandMessage(),
				Permission.COMMAND_TOOL
			),
			new CommandData(
				SBConfig.getReloadCommandMessage(),
				Permission.COMMAND_RELOAD
			),
			new CommandData(
				SBConfig.getCheckVerCommandMessage(),
				Permission.COMMAND_CHECKVER
			),
			new CommandData(
				SBConfig.getDataMigrCommandMessage(),
				Permission.COMMAND_DATAMIGR
			),
			new CommandData(
				SBConfig.getCreateCommandMessage(),
				Permission.COMMAND_INTERACT,
				Permission.COMMAND_BREAK,
				Permission.COMMAND_WALK
			),
			new CommandData(
				SBConfig.getAddCommandMessage(),
				Permission.COMMAND_INTERACT,
				Permission.COMMAND_BREAK,
				Permission.COMMAND_WALK
			),
			new CommandData(
				SBConfig.getRemoveCommandMessage(),
				Permission.COMMAND_INTERACT,
				Permission.COMMAND_BREAK,
				Permission.COMMAND_WALK
			),
			new CommandData(
				SBConfig.getViewCommandMessage(),
				Permission.COMMAND_INTERACT,
				Permission.COMMAND_BREAK,
				Permission.COMMAND_WALK
			),
			new CommandData(
				SBConfig.getWorldEditPasteCommandMessage(),
				Permission.COMMAND_WORLDEDIT
			),
			new CommandData(
				SBConfig.getWorldEditRemoveCommandMessage(),
				Permission.COMMAND_WORLDEDIT
			)
		);
	}

	@Override
	public boolean runCommand(CommandSender sender, String label, String[] args) {
		if (args.length == 1) {
			if (equals(args[0], "tool") && hasPermission(sender, Permission.COMMAND_TOOL)) {
				giveScriptEditor((Player) sender);
				return true;
			} else if (equals(args[0], "reload") && hasPermission(sender, Permission.COMMAND_RELOAD, false)) {
				Files.reload();
				getMapManager().loadAllScripts();
				Utils.sendMessage(sender, SBConfig.getAllFileReloadMessage());
				return true;
			} else if (equals(args[0], "checkver") && hasPermission(sender, Permission.COMMAND_CHECKVER, false)) {
				Updater updater = getPlugin().getUpdater();
				try {
					updater.load();
					if (!updater.execute(sender)) {
						Utils.sendMessage(sender, SBConfig.getNotLatestPluginMessage());
					}
				} catch (Exception e) {
					Utils.sendMessage(sender, SBConfig.getUpdateFailMessage());
				}
				return true;
			} else if (equals(args[0], "datamigr") && hasPermission(sender, Permission.COMMAND_DATAMIGR)) {
				sbDataMigr((Player) sender);
				return true;
			}
		} else if (args.length == 2) {
			if (equals(args[0], "interact", "break", "walk") && equals(args[1], "remove", "view")) {
				ScriptType scriptType = ScriptType.valueOf(args[0].toUpperCase());
				if (hasPermission(sender, Permission.valueOf("COMMAND_" + scriptType.name()))) {
					setClickData(SBPlayer.get(sender), ClickType.valueOf(args[1].toUpperCase()), scriptType);
				}
				return true;
			} else if (equals(args[0], "worldedit")
					&& equals(args[1], "remove") && hasPermission(sender, Permission.COMMAND_WORLDEDIT)) {
				scriptWERemove((Player) sender);
				return true;
			}
		} else if (args.length > 2) {
			if (args.length == 3 && equals(args[0], "worldedit")
					&& equals(args[1], "paste") && hasPermission(sender, Permission.COMMAND_WORLDEDIT)) {
				scriptWEPaste(SBPlayer.get(sender), false, Boolean.parseBoolean(args[2]));
				return true;
			} else if (args.length == 4 && equals(args[0], "worldedit")
					&& equals(args[1], "paste") && hasPermission(sender, Permission.COMMAND_WORLDEDIT)) {
				scriptWEPaste(SBPlayer.get(sender), Boolean.parseBoolean(args[2]), Boolean.parseBoolean(args[3]));
				return true;
			} else if (equals(args[0], "interact", "break", "walk") && equals(args[1], "create", "add")) {
				ScriptType scriptType = ScriptType.valueOf(args[0].toUpperCase());
				if (hasPermission(sender, Permission.valueOf("COMMAND_" + scriptType.name()))) {
					ClickType clickType = ClickType.valueOf(args[1].toUpperCase());
					String script = StringUtils.createString(args, 2).trim();
					setClickScriptData(SBPlayer.get(sender), script, clickType, scriptType);
				}
				return true;
			}
		}
		return false;
	}

	private void giveScriptEditor(Player player) {
		ItemStack item = new ItemStack(Material.BLAZE_ROD);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§dScript Editor");
		meta.setLore(SBConfig.getScriptEditorLore());
		item.setItemMeta(meta);
		player.getInventory().addItem(item);
		Utils.updateInventory(player);
		Utils.sendMessage(player, SBConfig.getGiveScriptEditorMessage());
	}

	private void sbDataMigr(Player player) {
		String path = "plugins/ScriptBlock/BlocksData/";
		File interactFile = new File(path + "interact_Scripts.yml");
		File walkFile = new File(path + "walk_Scripts.yml");
		boolean interactExists = interactFile.exists();
		boolean walkExists = walkFile.exists();
		if (!interactExists && !walkExists) {
			Utils.sendMessage(player, SBConfig.getNotScriptBlockFileMessage());
			return;
		}
		Utils.sendMessage(player, SBConfig.getDataMigrStartMessage());
		YamlConfig scriptFile;
		if (interactExists) {
			scriptFile = YamlConfig.load(getPlugin(), interactFile, false);
			saveScript(player, scriptFile, ScriptType.INTERACT);
		} else if (walkExists) {
			scriptFile = YamlConfig.load(getPlugin(), walkFile, false);
			saveScript(player, scriptFile, ScriptType.WALK);
		}
		Utils.sendMessage(player, SBConfig.getDataMigrEndMessage());
	}

	private void saveScript(Player player, YamlConfig scriptFile, ScriptType type) {
		ScriptData scriptData = new ScriptData(null, type);
		for (String world : scriptFile.getKeys(false)) {
			World tWorld = Utils.getWorld(world);
			for (String coords : scriptFile.getKeys(world, false)) {
				List<String> scripts = scriptFile.getStringList(world + "." + coords);
				if (scripts.size() > 0 && scripts.get(0).startsWith("Author:")) {
					scripts.remove(0);
				}
				String[] array = StringUtils.split(coords, ",");
				scriptData.setLocation(new Location(
					tWorld,
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
		getMapManager().loadScripts(scriptData.getScriptFile(), scriptData.getScriptType());
	}

	private void setClickData(SBPlayer sbPlayer, ClickType clickType, ScriptType scriptType) {
		if (sbPlayer.hasScript() || sbPlayer.hasClickAction()) {
			Utils.sendMessage(sbPlayer, SBConfig.getErrorEditDataMessage());
			return;
		}
		sbPlayer.setClickAction(clickType.createKey(scriptType));
		Utils.sendMessage(sbPlayer, SBConfig.getSuccEditDataMessage(clickType));
	}

	private void setClickScriptData(SBPlayer sbPlayer, String script, ClickType clickType, ScriptType scriptType) {
		if (sbPlayer.hasScript() || sbPlayer.hasClickAction()) {
			Utils.sendMessage(sbPlayer, SBConfig.getErrorEditDataMessage());
			return;
		}
		if (!checkScript(script)) {
			Utils.sendMessage(sbPlayer, SBConfig.getErrorScriptCheckMessage());
			return;
		}
		sbPlayer.setScript(script);
		sbPlayer.setClickAction(clickType.createKey(scriptType));
		Utils.sendMessage(sbPlayer, SBConfig.getSuccEditDataMessage(clickType));
	}

	private void scriptWEPaste(SBPlayer sbPlayer, boolean pasteonair, boolean overwrite) {
		if (!sbPlayer.hasClipboard()) {
			Utils.sendMessage(sbPlayer, SBConfig.getErrorScriptFileCheckMessage());
			return;
		}
		if (!HookPlugins.hasWorldEdit()) {
			Utils.sendMessage(sbPlayer, SBConfig.getNotWorldEditMessage());
			return;
		}
		WorldEditSelection weSelection = HookPlugins.getWorldEditSelection();
		Selection selection = weSelection.getSelection(sbPlayer.getPlayer());
		if (selection == null) {
			Utils.sendMessage(sbPlayer, SBConfig.getWorldEditNotSelectionMessage());
			return;
		}
		Clipboard clipboard = sbPlayer.getClipboard();
		for (Block block : weSelection.getBlocks(selection)) {
			if (!pasteonair && (block == null || block.getType() == Material.AIR)) {
				continue;
			}
			clipboard.wePaste(sbPlayer, block.getLocation(), overwrite);
		}
		clipboard.save();
		sbPlayer.setClipboard(null);
		ScriptType scriptType = clipboard.getScriptType();
		Utils.sendMessage(sbPlayer, SBConfig.getWorldEditPasteMessage(scriptType));
		Utils.sendMessage(SBConfig.getConsoleWorldEditPasteMessage(scriptType, selection.getMinimumPoint(), selection.getMaximumPoint()));
	}

	private void scriptWERemove(Player player) {
		if (!HookPlugins.hasWorldEdit()) {
			Utils.sendMessage(player, SBConfig.getNotWorldEditMessage());
			return;
		}
		WorldEditSelection weSelection = HookPlugins.getWorldEditSelection();
		Selection selection = weSelection.getSelection(player);
		if (selection == null) {
			Utils.sendMessage(player, SBConfig.getWorldEditNotSelectionMessage());
			return;
		}
		List<Block> blocks = weSelection.getBlocks(selection);
		StringBuilder builder = new StringBuilder(ScriptType.values().length);
		for (ScriptType scriptType : ScriptType.values()) {
			if (!Files.getScriptFile(scriptType).exists()) {
				continue;
			}
			boolean isFirst = true;
			ScriptEdit scriptEdit = new ScriptEdit(null, scriptType);
			for (Block block : blocks) {
				if (scriptEdit.weRemove(block.getLocation()) && isFirst && !(isFirst = false)) {
					if (builder.length() != 0) {
						builder.append(", ");
					}
					builder.append(scriptType.getType());
				}
			}
			scriptEdit.save();
		}
		if (builder.length() == 0) {
			Utils.sendMessage(player, SBConfig.getErrorScriptFileCheckMessage());
		} else {
			Utils.sendMessage(player, SBConfig.getWorldEditRemoveMessage(builder.toString()));
			Utils.sendMessage(SBConfig.getConsoleWorldEditRemoveMessage(builder.toString(), selection.getMinimumPoint(), selection.getMaximumPoint()));
		}
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String label, String[] args, List<String> emptyList) {
		if (args.length == 1) {
			String prefix = args[0].toLowerCase();
			String[] answer = {
				permString(sender, Permission.COMMAND_TOOL, "tool"),
				permString(sender, Permission.COMMAND_RELOAD, "reload"),
				permString(sender, Permission.COMMAND_CHECKVER, "checkver"),
				permString(sender, Permission.COMMAND_DATAMIGR, "datamigr"),
				permString(sender, Permission.COMMAND_INTERACT, "interact"),
				permString(sender, Permission.COMMAND_BREAK, "break"),
				permString(sender, Permission.COMMAND_WALK, "walk"),
				permString(sender, Permission.COMMAND_WORLDEDIT , "worldedit")
			};
			StreamUtils.filterForEach(answer, s -> s != null && s.startsWith(prefix), emptyList::add);
		} else if (args.length == 2 /*&& equals(args[0], "tool", "checkver", "datamigr", "reload")*/ ) {
			if (equals(args[0], "worldedit")) {
				if (Permission.COMMAND_WORLDEDIT.has(sender)) {
					String prefix = args[1].toLowerCase();
					String[] answer = new String[]{"paste", "remove"};
					StreamUtils.filterForEach(answer, s -> s.startsWith(prefix), emptyList::add);
				}
			} else if (equals(args[0], "interact", "break", "walk")) {
				if (Permission.valueOf("COMMAND_" + args[0].toUpperCase()).has(sender)) {
					String prefix = args[1].toLowerCase();
					String[] answer = new String[]{"create", "add", "remove", "view"};
					StreamUtils.filterForEach(answer, s -> s.startsWith(prefix), emptyList::add);
				}
			}
		} else if (args.length == 3) {
			if (equals(args[0], "worldedit") && equals(args[1], "paste")) {
				if (Permission.COMMAND_WORLDEDIT.has(sender)) {
					String prefix = args[2].toLowerCase();
					String[] answer = new String[]{"true", "false"};
					StreamUtils.filterForEach(answer, s -> s.startsWith(prefix), emptyList::add);
				}
			} else if (equals(args[0], "interact", "break", "walk") && equals(args[1], "create", "add")) {
				if (Permission.valueOf("COMMAND_" + args[0].toUpperCase()).has(sender)) {
					String prefix = args[2].toLowerCase();
					String[] answer = getOptionPrefixs();
					StreamUtils.filterForEach(answer, s -> s.startsWith(prefix), s -> emptyList.add(s.trim()));
				}
			}
		} else if (args.length == 4 && equals(args[0], "worldedit") && equals(args[1], "paste")) {
			if (Permission.COMMAND_WORLDEDIT.has(sender)) {
				String prefix = args[3].toLowerCase();
				String[] answer = new String[]{"true", "false"};
				StreamUtils.filterForEach(answer, s -> s.startsWith(prefix), emptyList::add);
			}
		}
		return emptyList;
	}

	private String[] getOptionPrefixs() {
		Option[] options = new OptionManager().newInstances();
		String[] prefixs = new String[options.length];
		for (int i = 0; i < prefixs.length; i++) {
			prefixs[i] = options[i].getPrefix();
		}
		return prefixs;
	}

	private boolean checkScript(String scriptLine) {
		try {
			List<String> scripts = StringUtils.getScripts(scriptLine);
			int result = 0;
			Option[] options = new OptionManager().newInstances();
			for (int i = 0; i < scripts.size(); i++) {
				for (Option option : options) {
					if (scripts.get(i).startsWith(option.getPrefix())) {
						result++;
					}
				}
			}
			if (result != scripts.size()) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}