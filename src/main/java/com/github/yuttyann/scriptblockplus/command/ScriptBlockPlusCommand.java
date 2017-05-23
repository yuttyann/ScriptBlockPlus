package com.github.yuttyann.scriptblockplus.command;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ProxiedCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.yuttyann.scriptblockplus.BlockLocation;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.Updater;
import com.github.yuttyann.scriptblockplus.command.help.CommandData;
import com.github.yuttyann.scriptblockplus.command.help.CommandHelp;
import com.github.yuttyann.scriptblockplus.enums.ClickType;
import com.github.yuttyann.scriptblockplus.enums.Metadata;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.Lang;
import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.file.ScriptData;
import com.github.yuttyann.scriptblockplus.manager.MapManager;
import com.github.yuttyann.scriptblockplus.manager.ScriptFileManager;
import com.github.yuttyann.scriptblockplus.manager.ScriptReadManager;
import com.github.yuttyann.scriptblockplus.metadata.SBMetadata;
import com.github.yuttyann.scriptblockplus.metadata.ScriptFile;
import com.github.yuttyann.scriptblockplus.option.OptionPrefix;
import com.github.yuttyann.scriptblockplus.option.hook.HookPlugins;
import com.github.yuttyann.scriptblockplus.option.hook.WorldEditSelection;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class ScriptBlockPlusCommand implements TabExecutor {

	private ScriptBlock plugin;
	private MapManager mapManager;

	public ScriptBlockPlusCommand(ScriptBlock plugin) {
		this.plugin = plugin;
		this.mapManager = plugin.getMapManager();
		CommandHelp help = new CommandHelp();
		help.put(
			"scriptblockplus",
			new CommandData("tool - " + Lang.getToolCommandMessage(), Permission.SCRIPTBLOCKPLUS_COMMAND_TOOL),
			new CommandData("reload - " + Lang.getReloadCommandMessage(), Permission.SCRIPTBLOCKPLUS_COMMAND_RELOAD),
			new CommandData("checkver - " + Lang.getCheckVerCommandMessage(), Permission.SCRIPTBLOCKPLUS_COMMAND_CHECKVER),
			new CommandData("datamigr - " + Lang.getDataMigrCommandMessage(), Permission.SCRIPTBLOCKPLUS_COMMAND_DATAMIGR),
			new CommandData("<scripttype> create <options> - " + Lang.getCreateCommandMessage()).addPermissions(
				Permission.SCRIPTBLOCKPLUS_COMMAND_INTERACT,
				Permission.SCRIPTBLOCKPLUS_COMMAND_BREAK,
				Permission.SCRIPTBLOCKPLUS_COMMAND_WALK
			),
			new CommandData("<scripttype> add <options> - " + Lang.getAddCommandMessage()).addPermissions(
				Permission.SCRIPTBLOCKPLUS_COMMAND_INTERACT,
				Permission.SCRIPTBLOCKPLUS_COMMAND_BREAK,
				Permission.SCRIPTBLOCKPLUS_COMMAND_WALK
			),
			new CommandData("<scripttype> remove - " + Lang.getRemoveCommandMessage()).addPermissions(
				Permission.SCRIPTBLOCKPLUS_COMMAND_INTERACT,
				Permission.SCRIPTBLOCKPLUS_COMMAND_BREAK,
				Permission.SCRIPTBLOCKPLUS_COMMAND_WALK
			),
			new CommandData("<scripttype> view - " + Lang.getViewCommandMessage()).addPermissions(
				Permission.SCRIPTBLOCKPLUS_COMMAND_INTERACT,
				Permission.SCRIPTBLOCKPLUS_COMMAND_BREAK,
				Permission.SCRIPTBLOCKPLUS_COMMAND_WALK
			),
			new CommandData("worldedit paste <pasteonair> [overwrite] - " + Lang.getWorldEditPasteCommandMessage(), Permission.SCRIPTBLOCKPLUS_COMMAND_WORLDEDIT),
			new CommandData("worldedit remove - " + Lang.getWorldEditRemoveCommandMessage(), Permission.SCRIPTBLOCKPLUS_COMMAND_WORLDEDIT)
		);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (Utils.isCB18orLater() && sender instanceof ProxiedCommandSender) {
			CommandSender pxSender = ((ProxiedCommandSender) sender).getCallee();
			if (pxSender instanceof Player) {
				sender = pxSender;
			}
		}
		if (!(sender instanceof Player)) {
			Utils.sendPluginMessage(plugin, Lang.getSenderNoPlayerMessage());
			return true;
		}
		Player player = (Player) sender;
		if (args.length == 1) {
			if (equals(args[0], "tool")) {
				if (!Permission.has(Permission.SCRIPTBLOCKPLUS_COMMAND_TOOL, player)) {
					Utils.sendPluginMessage(plugin, player, Lang.getNotPermissionMessage());
					return true;
				}
				giveScriptEditor(player);
				return true;
			}
			if (equals(args[0], "reload")) {
				if (!Permission.has(Permission.SCRIPTBLOCKPLUS_COMMAND_RELOAD, player)) {
					Utils.sendPluginMessage(plugin, player, Lang.getNotPermissionMessage());
					return true;
				}
				Files.reload(plugin);
				mapManager.loadAllScripts();
				Utils.sendPluginMessage(plugin, player, Lang.getAllFileReloadMessage());
				return true;
			}
			if (equals(args[0], "checkver")) {
				if (!Permission.has(Permission.SCRIPTBLOCKPLUS_COMMAND_CHECKVER, player)) {
					Utils.sendPluginMessage(plugin, player, Lang.getNotPermissionMessage());
					return true;
				}
				Updater updater = plugin.getUpdater();
				try {
					updater.load();
					if (!updater.check(player)) {
						player.sendMessage(Lang.getNotLatestPluginMessage());
					}
				} catch (Exception e) {
					player.sendMessage(Lang.getUpdateErrorMessage());
				}
				return true;
			}
			if (equals(args[0], "datamigr")) {
				if (!Permission.has(Permission.SCRIPTBLOCKPLUS_COMMAND_DATAMIGR, player)) {
					Utils.sendPluginMessage(plugin, player, Lang.getNotPermissionMessage());
					return true;
				}
				sbDataMigr(player);
				return true;
			}
		}
		if (args.length == 2) {
			if (equals(args[0], "interact", "break", "walk") && equals(args[1], "remove", "view")) {
				if (!Permission.has(Permission.SCRIPTBLOCKPLUS_COMMAND_WALK, player)) {
					Utils.sendPluginMessage(plugin, player, Lang.getNotPermissionMessage());
					return true;
				}
				setClickMeta(player, ClickType.valueOf(args[0].toUpperCase() + "_" + args[1].toUpperCase()));
				return true;
			}
			if (equals(args[0], "worldedit") && equals(args[1], "remove")) {
				if (!Permission.has(Permission.SCRIPTBLOCKPLUS_COMMAND_WORLDEDIT, player)) {
					Utils.sendPluginMessage(plugin, player, Lang.getNotPermissionMessage());
					return true;
				}
				scriptWERemove(player);
				return true;
			}
		}
		if (args.length > 2) {
			if (args.length == 3 && equals(args[0], "worldedit") && equals(args[1], "paste")) {
				if (!Permission.has(Permission.SCRIPTBLOCKPLUS_COMMAND_WORLDEDIT, player)) {
					Utils.sendPluginMessage(plugin, player, Lang.getNotPermissionMessage());
					return true;
				}
				scriptWEPaste(player, false, Boolean.parseBoolean(args[2]));
				return true;
			}
			if (args.length == 4 && equals(args[0], "worldedit") && equals(args[1], "paste")) {
				if (!Permission.has(Permission.SCRIPTBLOCKPLUS_COMMAND_WORLDEDIT, player)) {
					Utils.sendPluginMessage(plugin, player, Lang.getNotPermissionMessage());
					return true;
				}
				scriptWEPaste(player, Boolean.parseBoolean(args[2]), Boolean.parseBoolean(args[3]));
				return true;
			}
			if (equals(args[0], "interact", "break", "walk") && equals(args[1], "create", "add")) {
				if (!Permission.has(Permission.SCRIPTBLOCKPLUS_COMMAND_WALK, player)) {
					Utils.sendPluginMessage(plugin, player, Lang.getNotPermissionMessage());
					return true;
				}
				setClickMeta(player, args, ClickType.valueOf(args[0].toUpperCase() + "_" + args[1].toUpperCase()));
				return true;
			}
		}
		CommandHelp.sendHelpMessage(plugin, sender, command, false);
		return true;
	}

	private void giveScriptEditor(Player player) {
		ItemStack item = new ItemStack(Material.BLAZE_ROD);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§dScript Editor");
		meta.setLore(Lang.getScriptEditorLore());
		item.setItemMeta(meta);
		player.getInventory().addItem(item);
		Utils.updateInventory(player);
		Utils.sendPluginMessage(plugin, player, Lang.getGiveScriptEditorMessage());
	}

	private void sbDataMigr(Player player) {
		File interactFile = new File(Files.FILE_PATHS[6]);
		File walkFile = new File(Files.FILE_PATHS[7]);
		boolean interactExists = interactFile.exists();
		boolean walkExists = walkFile.exists();
		if (!interactExists && !walkExists) {
			Utils.sendPluginMessage(plugin, player, Lang.getNotScriptBlockFileMessage());
			return;
		}
		Utils.sendPluginMessage(plugin, player, Lang.getDataMigrStartMessage());
		SBConfig config;
		if (interactExists) {
			config = SBConfig.loadConfiguration(interactFile);
			saveScript(player, config, ScriptType.INTERACT);
		}
		if (walkExists) {
			config = SBConfig.loadConfiguration(walkFile);
			saveScript(player, config, ScriptType.WALK);
		}
		Utils.sendPluginMessage(plugin, player, Lang.getDataMigrEndMessage());
	}

	private void saveScript(Player player, SBConfig config, ScriptType type) {
		ScriptData scriptData = new ScriptData(plugin, null, type);
		for (String world : config.getKeys()) {
			World bWorld = Utils.getWorld(world);
			for (String coords : config.getKeys(world)) {
				List<String> scripts = config.getStringList(world + "." + coords, null);
				if (scripts.size() > 0 && scripts.get(0).startsWith("Author:")) {
					scripts.remove(0);
				}
				String[] array = StringUtils.split(coords, ",");
				scriptData.setBlockLocation(new BlockLocation(
					bWorld,
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
		mapManager.loadScripts(scriptData.getScriptFile(), scriptData.getScriptType());
	}

	private void setClickMeta(Player player, ClickType clickType) {
		if (SBMetadata.has(player, Metadata.PLAYERCLICK, Metadata.SCRIPTTEXT)) {
			Utils.sendPluginMessage(plugin, player, Lang.getErrorEditDataMessage());
			return;
		}
		SBMetadata.getPlayerClick().set(player, clickType, true);
		Utils.sendPluginMessage(plugin, player, Lang.getSuccEditDataMessage(clickType));
	}

	private void setClickMeta(Player player, String[] args, ClickType clickType) {
		if (SBMetadata.has(player, Metadata.PLAYERCLICK, Metadata.SCRIPTTEXT)) {
			Utils.sendPluginMessage(plugin, player, Lang.getErrorEditDataMessage());
			return;
		}
		String script = StringUtils.createString(args, 2).trim();
		ScriptReadManager readManager = new ScriptReadManager(plugin, null, null);
		if (!readManager.checkScript(script)) {
			Utils.sendPluginMessage(plugin, player, Lang.getErrorScriptCheckMessage());
			return;
		}
		SBMetadata.getPlayerClick().set(player, clickType, true);
		SBMetadata.getScriptText().set(player, clickType, script);
		Utils.sendPluginMessage(plugin, player, Lang.getSuccEditDataMessage(clickType));
	}

	private void scriptWEPaste(Player player, boolean pasteonair, boolean overwrite) {
		ScriptFile scriptFile = SBMetadata.getScriptFile();
		if (!scriptFile.hasAll(player)) {
			Utils.sendPluginMessage(plugin, player, Lang.getErrorScriptFileCheckMessage());
			return;
		}
		if (!HookPlugins.hasWorldEdit()) {
			Utils.sendPluginMessage(plugin, player, Lang.getNotWorldEditMessage());
			return;
		}
		WorldEditSelection selectionAPI = HookPlugins.getWorldEditSelection();
		Selection selection = selectionAPI.getSelection(player);
		if (selection == null) {
			Utils.sendPluginMessage(plugin, player, Lang.getWorldEditNotSelectionMessage());
			return;
		}
		ScriptFileManager fileManager = scriptFile.get(player);
		for (Block block : selectionAPI.getSelectionBlocks(selection)) {
			if (!pasteonair && (block == null || block.getType() == Material.AIR)) {
				continue;
			}
			fileManager.scriptWEPaste(player, BlockLocation.fromLocation(block.getLocation()), overwrite);
		}
		fileManager.save();
		SBMetadata.remove(player, Metadata.SCRIPTFILE);
		ScriptType scriptType = scriptFile.get(player).getScriptType();
		Utils.sendPluginMessage(plugin, player, Lang.getWorldEditPasteMessage(scriptType));
		Utils.sendPluginMessage(plugin, Lang.getConsoleWorldEditPasteMessage(scriptType, selection.getMinimumPoint(), selection.getMaximumPoint()));
	}

	private void scriptWERemove(Player player) {
		if (!HookPlugins.hasWorldEdit()) {
			Utils.sendPluginMessage(plugin, player, Lang.getNotWorldEditMessage());
			return;
		}
		WorldEditSelection selectionAPI = HookPlugins.getWorldEditSelection();
		Selection selection = selectionAPI.getSelection(player);
		if (selection == null) {
			Utils.sendPluginMessage(plugin, player, Lang.getWorldEditNotSelectionMessage());
			return;
		}
		ScriptFileManager interact = new ScriptFileManager(plugin, null, ScriptType.INTERACT);
		ScriptFileManager break_ = new ScriptFileManager(plugin, null, ScriptType.BREAK);
		ScriptFileManager walk = new ScriptFileManager(plugin, null, ScriptType.WALK);
		boolean isInteract = false, isBreak = false, isWalk = false;
		for (Block block : selectionAPI.getSelectionBlocks(selection)) {
			BlockLocation location = BlockLocation.fromLocation(block.getLocation());
			interact.setBlockLocation(location);
			if (interact.checkPath()) {
				interact.scriptWERemove(player);
				if (!isInteract) {
					isInteract = true;
				}
			}
			break_.setBlockLocation(location);
			if (break_.checkPath()) {
				break_.scriptWERemove(player);
				if (!isBreak) {
					isBreak = true;
				}
			}
			walk.setBlockLocation(location);
			if (walk.checkPath()) {
				walk.scriptWERemove(player);
				if (!isWalk) {
					isWalk = true;
				}
			}
		}
		StringBuilder type = new StringBuilder();
		if (isInteract) {
			Files.getInteract().save();
			type.append(ScriptType.INTERACT.toString());
		}
		if (isBreak) {
			Files.getBreak().save();
			if (type.length() != 0) {
				type.append(", ");
			}
			type.append(ScriptType.BREAK.toString());
		}
		if (isWalk) {
			Files.getWalk().save();
			if (type.length() != 0) {
				type.append(", ");
			}
			type.append(ScriptType.WALK.toString());
		}
		if (type.length() == 0) {
			Utils.sendPluginMessage(plugin, player, Lang.getErrorScriptFileCheckMessage());
			return;
		}
		Utils.sendPluginMessage(plugin, player, Lang.getWorldEditRemoveMessage(type.toString()));
		Utils.sendPluginMessage(plugin, Lang.getConsoleWorldEditRemoveMessage(type.toString(), selection.getMinimumPoint(), selection.getMaximumPoint()));
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 1) {
			String[] args_ = {
				perm(sender, "tool", Permission.SCRIPTBLOCKPLUS_COMMAND_TOOL),
				perm(sender, "reload", Permission.SCRIPTBLOCKPLUS_COMMAND_RELOAD),
				perm(sender, "checkver", Permission.SCRIPTBLOCKPLUS_COMMAND_CHECKVER),
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
		if (args.length == 2 && !equals(args[0], "tool", "datamigr", "reload")) {
			String[] args_;
			Permission perm = null;
			if (equals(args[0], "worldedit")) {
				args_ = new String[]{"paste", "remove"};
				perm = Permission.SCRIPTBLOCKPLUS_COMMAND_WORLDEDIT;
			} else {
				if (equals(args[0], "interact")) {
					perm = Permission.SCRIPTBLOCKPLUS_COMMAND_INTERACT;
				} else if (equals(args[0], "break")) {
					perm = Permission.SCRIPTBLOCKPLUS_COMMAND_BREAK;
				} else if (equals(args[0], "walk")) {
					perm = Permission.SCRIPTBLOCKPLUS_COMMAND_WALK;
				}
				args_ = new String[]{"create", "add", "remove", "view"};
			}
			if ((args_ = perm(sender, args_, perm)) != null) {
				String prefix = args[1].toLowerCase();
				List<String> commands = new ArrayList<String>();
				for (String c : args_) {
					if (c.startsWith(prefix)) {
						commands.add(c);
					}
				}
				return commands;
			}
		}
		if (args.length == 3 && ((equals(args[0], "worldedit") && equals(args[1], "paste"))
			|| ((equals(args[0], "interact", "break", "walk")) && (equals(args[1], "create", "add"))))) {
			String[] args_;
			Permission perm = null;
			if (equals(args[0], "worldedit") && equals(args[1], "paste")) {
				args_ = new String[]{"true", "false"};
				perm = Permission.SCRIPTBLOCKPLUS_COMMAND_WORLDEDIT;
			} else {
				if (equals(args[0], "interact")) {
					perm = Permission.SCRIPTBLOCKPLUS_COMMAND_INTERACT;
				} else if (equals(args[0], "break")) {
					perm = Permission.SCRIPTBLOCKPLUS_COMMAND_BREAK;
				} else if (equals(args[0], "walk")) {
					perm = Permission.SCRIPTBLOCKPLUS_COMMAND_WALK;
				}
				args_ = OptionPrefix.PREFIXS;
			}
			if ((args_ = perm(sender, args_, perm)) != null) {
				String prefix = args[2].toLowerCase();
				List<String> commands = new ArrayList<String>();
				for (String c : args_) {
					if (c.startsWith(prefix)) {
						commands.add(c.trim());
					}
				}
				return commands;
			}
		}
		if (args.length == 4 && equals(args[0], "worldedit") && equals(args[1], "paste")) {
			Permission perm = Permission.SCRIPTBLOCKPLUS_COMMAND_WORLDEDIT;
			String[] args_ = new String[]{"true", "false"};
			if ((args_ = perm(sender, args_, perm)) != null) {
				String prefix = args[3].toLowerCase();
				List<String> commands = new ArrayList<String>();
				for (String c : args_) {
					if (c.startsWith(prefix)) {
						commands.add(c);
					}
				}
				return commands;
			}
		}
		return new ArrayList<String>();
	}

	private String perm(CommandSender sender, String command, Object permission) {
		if (permission != null && sender.hasPermission(getNode(permission))) {
			return command;
		}
		return null;
	}

	private String[] perm(CommandSender sender, String[] commands, Object permission) {
		if (permission != null && sender.hasPermission(getNode(permission))) {
			return commands;
		}
		return null;
	}

	private String getNode(Object permission) {
		return permission instanceof Permission ? ((Permission) permission).getNode() : permission.toString();
	}

	private boolean equals(String source, String... anothers) {
		for (String another : anothers) {
			if (source.equalsIgnoreCase(another)) {
				return true;
			}
		}
		return false;
	}
}