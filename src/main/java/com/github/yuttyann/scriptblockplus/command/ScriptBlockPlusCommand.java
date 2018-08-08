package com.github.yuttyann.scriptblockplus.command;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.ActionType;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.file.yaml.YamlConfig;
import com.github.yuttyann.scriptblockplus.manager.OptionManager.OptionList;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.Clipboard;
import com.github.yuttyann.scriptblockplus.script.ScriptData;
import com.github.yuttyann.scriptblockplus.script.ScriptEdit;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.script.ScriptType.SBPermission;
import com.github.yuttyann.scriptblockplus.script.hook.HookPlugins;
import com.github.yuttyann.scriptblockplus.script.hook.WorldEditSelection;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.FileUtils;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import com.sk89q.worldedit.bukkit.selections.Selection;

public final class ScriptBlockPlusCommand extends BaseCommand {

	public ScriptBlockPlusCommand(ScriptBlock plugin) {
		super(plugin);
	}

	@Override
	public String getCommandName() {
		return "scriptblockplus";
	}

	@Override
	public boolean isAliases() {
		return true;
	}

	@Override
	public CommandData[] getUsages() {
		String[] typeNodes = SBPermission.getNodes(true);
		return new CommandData[] {
			new CommandData(SBConfig.getToolCommandMessage(), Permission.COMMAND_TOOL.getNode()),
			new CommandData(SBConfig.getReloadCommandMessage(), Permission.COMMAND_RELOAD.getNode()),
			new CommandData(SBConfig.getBackupCommandMessage(), Permission.COMMAND_BACKUP.getNode()),
			new CommandData(SBConfig.getCheckVerCommandMessage(), Permission.COMMAND_CHECKVER.getNode()),
			new CommandData(SBConfig.getDataMigrCommandMessage(), Permission.COMMAND_DATAMIGR.getNode()),
			new CommandData(SBConfig.getCreateCommandMessage(), typeNodes),
			new CommandData(SBConfig.getAddCommandMessage(), typeNodes),
			new CommandData(SBConfig.getRemoveCommandMessage(), typeNodes),
			new CommandData(SBConfig.getViewCommandMessage(), typeNodes),
			new CommandData(SBConfig.getWorldEditPasteCommandMessage(), Permission.COMMAND_WORLDEDIT.getNode()),
			new CommandData(SBConfig.getWorldEditRemoveCommandMessage(), Permission.COMMAND_WORLDEDIT.getNode())
		};
	}

	@Override
	public boolean runCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 1) {
			if (equals(args[0], "tool")) {
				return doTool(sender, args);
			} else if (equals(args[0], "reload")) {
				return doReload(sender);
			} else if (equals(args[0], "backup")) {
				return doBackup(sender);
			} else if (equals(args[0], "checkver")) {
				return doCheckVer(sender);
			} else if (equals(args[0], "datamigr")) {
				return doDataMigr(sender);
			}
		} else if (args.length == 2) {
			if (equals(args[0], ScriptType.types()) && equals(args[1], "remove", "view")) {
				return setData(sender, args);
			} else if (equals(args[0], "worldedit") && equals(args[1], "remove")) {
				return doWorldEditRemove(sender);
			}
		} else if (args.length >= 3) {
			if (args.length <= 4 && equals(args[0], "worldedit") && equals(args[1], "paste")) {
				return doWorldEditPaste(sender, args);
			} else if (equals(args[0], ScriptType.types()) && equals(args[1], "create", "add")) {
				return setData(sender, args);
			}
		}
		return false;
	}

	private boolean doTool(CommandSender sender, String[] args) {
		if (!hasPermission(sender, Permission.COMMAND_TOOL)) {
			return false;
		}
		Player player = (Player) sender;
		player.getInventory().addItem(Utils.getScriptEditor(ScriptType.INTERACT));
		Utils.updateInventory(player);
		Utils.sendMessage(player, SBConfig.getGiveScriptEditorMessage());
		return true;
	}

	private boolean doReload(CommandSender sender) {
		if (!hasPermission(sender, Permission.COMMAND_RELOAD, false)) {
			return false;
		}
		Files.reload();
		setUsage(getUsages());
		ScriptBlock.getInstance().getMapManager().loadAllScripts();
		Utils.sendMessage(sender, SBConfig.getAllFileReloadMessage());
		return true;
	}

	private boolean doBackup(CommandSender sender) {
		if (!hasPermission(sender, Permission.COMMAND_BACKUP, false)) {
			return false;
		}
		File dataFolder = Files.getConfig().getDataFolder();
		File scripts = new File(dataFolder, "scripts");
		if (!scripts.exists() || FileUtils.isEmpty(scripts)) {
			Utils.sendMessage(sender, SBConfig.getErrorScriptsBackupMessage());
			return true;
		}
		File backup = new File(dataFolder, "backup");
		String formatTime = Utils.getFormatTime("yyyy-MM-dd HH-mm-ss");
		FileUtils.copyDirectory(scripts, new File(backup, "scripts " + formatTime));
		Utils.sendMessage(sender, SBConfig.getScriptsBackupMessage());
		return true;
	}

	private boolean doCheckVer(CommandSender sender) {
		if (!hasPermission(sender, Permission.COMMAND_CHECKVER, false)) {
			return false;
		}
		ScriptBlock.getInstance().checkUpdate(sender, true);
		return true;
	}

	private boolean doDataMigr(CommandSender sender) {
		if (!hasPermission(sender, Permission.COMMAND_DATAMIGR)) {
			return false;
		}
		String path = "plugins/ScriptBlock/BlocksData/";
		File interactFile = new File(path + "interact_Scripts.yml");
		File walkFile = new File(path + "walk_Scripts.yml");
		Player player = (Player) sender;
		if (!walkFile.exists() && !interactFile.exists()) {
			Utils.sendMessage(player, SBConfig.getNotScriptBlockFileMessage());
		} else {
			String time = Utils.getFormatTime();
			String uuid = player.getUniqueId().toString();
			Utils.sendMessage(player, SBConfig.getDataMigrStartMessage());
			new Thread(() -> {
				YamlConfig scriptFile;
				if (interactFile.exists()) {
					scriptFile = YamlConfig.load(getPlugin(), interactFile, false);
					saveScript(uuid, time, scriptFile, ScriptType.INTERACT);
				}
				if (walkFile.exists()) {
					scriptFile = YamlConfig.load(getPlugin(), walkFile, false);
					saveScript(uuid, time, scriptFile, ScriptType.WALK);
				}
				Utils.sendMessage(player, SBConfig.getDataMigrEndMessage());
			}).start();
		}
		return true;
	}

	private void saveScript(String uuid, String time, YamlConfig scriptFile, ScriptType scriptType) {
		ScriptData scriptData = new ScriptData(null, scriptType);
		for (String world : scriptFile.getKeys(false)) {
			World tWorld = Utils.getWorld(world);
			for (String coords : scriptFile.getKeys(world, false)) {
				List<String> scripts = scriptFile.getStringList(world + "." + coords);
				if (scripts.size() > 0 && scripts.get(0).startsWith("Author:")) {
					scripts.remove(0);
				}
				for (int i = 0; i < scripts.size(); i++) {
					String scriptLine = scripts.get(i);
					if (scriptLine.contains("@cooldown:")) {
						scripts.set(i, StringUtils.replace(scriptLine, "@cooldown:", "@oldcooldown:"));
					}
				}
				String[] array = StringUtils.split(coords, ",");
				scriptData.setLocation(new Location(
					tWorld,
					Integer.parseInt(array[0]),
					Integer.parseInt(array[1]),
					Integer.parseInt(array[2]))
				);
				scriptData.setAuthor(uuid);
				scriptData.setLastEdit(time);
				scriptData.setScripts(scripts);
			}
		}
		scriptData.save();
		ScriptBlock.getInstance().getMapManager().loadScripts(scriptFile, scriptType);
	}

	private boolean setData(CommandSender sender, String[] args) {
		ScriptType scriptType = ScriptType.valueOf(args[0].toUpperCase());
		if (!SBPermission.has(sender, scriptType, true)) {
			return false;
		}
		SBPlayer sbPlayer = SBPlayer.fromPlayer((Player) sender);
		if (sbPlayer.hasScriptLine() || sbPlayer.hasActionType()) {
			Utils.sendMessage(sbPlayer, SBConfig.getErrorEditDataMessage());
			return true;
		}
		if (args.length > 2) {
			String script = StringUtils.createString(args, 2).trim();
			if (!checkScript(script)) {
				Utils.sendMessage(sbPlayer, SBConfig.getErrorScriptCheckMessage());
				return true;
			}
			sbPlayer.setScriptLine(script);
		}
		ActionType actionType = ActionType.valueOf(args[1].toUpperCase());
		sbPlayer.setActionType(actionType.createKey(scriptType));
		Utils.sendMessage(sbPlayer, SBConfig.getSuccEditDataMessage(scriptType, actionType));
		return true;
	}

	private boolean doWorldEditRemove(CommandSender sender) {
		if (!hasPermission(sender, Permission.COMMAND_WORLDEDIT)) {
			return false;
		}
		Player player = (Player) sender;
		if (!HookPlugins.hasWorldEdit()) {
			Utils.sendMessage(player, SBConfig.getNotWorldEditMessage());
			return true;
		}
		WorldEditSelection weSelection = HookPlugins.getWorldEditSelection();
		Selection selection = weSelection.getSelection(player);
		if (selection == null) {
			Utils.sendMessage(player, SBConfig.getWorldEditNotSelectionMessage());
			return true;
		}
		StringBuilder builder = new StringBuilder();
		Set<Block> blocks = weSelection.getBlocks(selection);
		for (ScriptType scriptType : ScriptType.values()) {
			if (!Files.getScriptFile(scriptType).exists()) {
				continue;
			}
			boolean isFirst = true;
			ScriptEdit scriptEdit = new ScriptEdit(scriptType);
			for (Block block : blocks) {
				if (scriptEdit.weRemove(block.getLocation()) && isFirst && !(isFirst = false)) {
					builder.append(scriptType.getType()).append(builder.length() == 0 ? "" : ", ");
				}
			}
			scriptEdit.save();
		}
		if (builder.length() == 0) {
			Utils.sendMessage(player, SBConfig.getErrorScriptFileCheckMessage());
		} else {
			String types = builder.toString();
			Utils.sendMessage(player, SBConfig.getWorldEditRemoveMessage(types));
			Utils.sendMessage(SBConfig.getConsoleWorldEditRemoveMessage(types, selection.getMinimumPoint(), selection.getMaximumPoint()));
		}
		return true;
	}

	private boolean doWorldEditPaste(CommandSender sender, String[] args) {
		if (!hasPermission(sender, Permission.COMMAND_WORLDEDIT)) {
			return false;
		}
		SBPlayer sbPlayer = SBPlayer.fromPlayer((Player) sender);
		if (!sbPlayer.hasClipboard()) {
			Utils.sendMessage(sbPlayer, SBConfig.getErrorScriptFileCheckMessage());
			return true;
		}
		if (!HookPlugins.hasWorldEdit()) {
			Utils.sendMessage(sbPlayer, SBConfig.getNotWorldEditMessage());
			return true;
		}
		WorldEditSelection weSelection = HookPlugins.getWorldEditSelection();
		Selection selection = weSelection.getSelection(sbPlayer.getPlayer());
		if (selection == null) {
			Utils.sendMessage(sbPlayer, SBConfig.getWorldEditNotSelectionMessage());
			return true;
		}
		boolean pasteonair = args.length > 2 ? Boolean.parseBoolean(args[2]) : false;
		boolean overwrite = args.length > 3 ? Boolean.parseBoolean(args[3]) : false;
		Clipboard clipboard = sbPlayer.getClipboard();
		for (Block block : weSelection.getBlocks(selection)) {
			if (!pasteonair && (block == null || block.getType() == Material.AIR)) {
				continue;
			}
			clipboard.wePaste(block.getLocation(), overwrite, false);
		}
		clipboard.save();
		sbPlayer.setClipboard(null);
		Utils.sendMessage(sbPlayer, SBConfig.getWorldEditPasteMessage(clipboard.getScriptType()));
		Utils.sendMessage(SBConfig.getConsoleWorldEditPasteMessage(clipboard.getScriptType(), selection.getMinimumPoint(), selection.getMaximumPoint()));
		return true;
	}

	@Override
	public void tabComplete(CommandSender sender, Command command, String label, String[] args, List<String> emptyList) {
		if (args.length == 1) {
			String prefix = args[0].toLowerCase();
			Set<String> set = setCommandPermissions(sender, new LinkedHashSet<>());
			StreamUtils.filterForEach(set, s -> StringUtils.startsWith(s, prefix), emptyList::add);
		} else if (args.length == 2) {
			if (equals(args[0], "worldedit")) {
				if (Permission.COMMAND_WORLDEDIT.has(sender)) {
					String prefix = args[1].toLowerCase();
					String[] answers = new String[]{"paste", "remove"};
					StreamUtils.filterForEach(answers, s -> s.startsWith(prefix), emptyList::add);
				}
			} else if (equals(args[0], ScriptType.types())) {
				if (SBPermission.has(sender, ScriptType.valueOf(args[0].toUpperCase()), true)) {
					String prefix = args[1].toLowerCase();
					String[] answers = new String[]{"create", "add", "remove", "view"};
					StreamUtils.filterForEach(answers, s -> s.startsWith(prefix), emptyList::add);
				}
			}
		} else if (args.length > 2) {
			if (args.length == 3 && equals(args[0], "worldedit") && equals(args[1], "paste")) {
				if (Permission.COMMAND_WORLDEDIT.has(sender)) {
					String prefix = args[2].toLowerCase();
					String[] answers = new String[]{"true", "false"};
					StreamUtils.filterForEach(answers, s -> s.startsWith(prefix), emptyList::add);
				}
			} else if (args.length == 4 && equals(args[0], "worldedit") && equals(args[1], "paste")) {
				if (Permission.COMMAND_WORLDEDIT.has(sender)) {
					String prefix = args[3].toLowerCase();
					String[] answers = new String[]{"true", "false"};
					StreamUtils.filterForEach(answers, s -> s.startsWith(prefix), emptyList::add);
				}
			} else {
				if (equals(args[0], ScriptType.types()) && equals(args[1], "create", "add")) {
					if (SBPermission.has(sender, ScriptType.valueOf(args[0].toUpperCase()), true)) {
						String prefix = args[args.length - 1].toLowerCase();
						String[] answers = OptionList.getSyntaxs();
						Arrays.sort(answers);
						StreamUtils.filterForEach(answers, s -> s.startsWith(prefix), s -> emptyList.add(s.trim()));
					}
				}
			}
		}
	}

	private Set<String> setCommandPermissions(CommandSender sender, Set<String> set) {
		set.add(hasPermission(sender, Permission.COMMAND_TOOL, "tool"));
		set.add(hasPermission(sender, Permission.COMMAND_RELOAD, "reload"));
		set.add(hasPermission(sender, Permission.COMMAND_BACKUP, "backup"));
		set.add(hasPermission(sender, Permission.COMMAND_CHECKVER, "checkver"));
		set.add(hasPermission(sender, Permission.COMMAND_DATAMIGR, "datamigr"));

		StreamUtils.filterForEach(ScriptType.values(),
				s -> SBPermission.has(sender, s, true), s -> set.add(s.getType()));

		set.add(hasPermission(sender, Permission.COMMAND_WORLDEDIT , "worldedit"));
		return set;
	}

	private String hasPermission(CommandSender sender, Permission permission, String source) {
		return StringUtils.isNotEmpty(permission.getNode()) && permission.has(sender) ? source : null;
	}

	private boolean checkScript(String scriptLine) {
		try {
			List<Option> options = OptionList.getList();
			List<String> scripts = StringUtils.getScripts(scriptLine);
			int successCount = 0;
			for (String script : scripts) {
				for (Option option : options) {
					if (option.isOption(script)) {
						successCount++;
					}
				}
			}
			if (successCount == 0 || successCount != scripts.size()) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}