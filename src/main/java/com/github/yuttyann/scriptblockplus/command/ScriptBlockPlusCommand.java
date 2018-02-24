package com.github.yuttyann.scriptblockplus.command;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.ClickType;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.file.yaml.YamlConfig;
import com.github.yuttyann.scriptblockplus.manager.OptionManager;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.Clipboard;
import com.github.yuttyann.scriptblockplus.script.ScriptData;
import com.github.yuttyann.scriptblockplus.script.ScriptEdit;
import com.github.yuttyann.scriptblockplus.script.hook.HookPlugins;
import com.github.yuttyann.scriptblockplus.script.hook.WorldEditSelection;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import com.sk89q.worldedit.bukkit.selections.Selection;

public final class ScriptBlockPlusCommand extends BaseCommand {

	private static final Permission[] SCRIPT_PERMISSIONS = {
		Permission.COMMAND_INTERACT,
		Permission.COMMAND_BREAK,
		Permission.COMMAND_WALK
	};

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
		return new CommandData[] {
			new CommandData(SBConfig.getToolCommandMessage(), Permission.COMMAND_TOOL),
			new CommandData(SBConfig.getReloadCommandMessage(), Permission.COMMAND_RELOAD),
			new CommandData(SBConfig.getCheckVerCommandMessage(), Permission.COMMAND_CHECKVER),
			new CommandData(SBConfig.getDataMigrCommandMessage(), Permission.COMMAND_DATAMIGR),
			new CommandData(SBConfig.getCreateCommandMessage(), SCRIPT_PERMISSIONS),
			new CommandData(SBConfig.getAddCommandMessage(), SCRIPT_PERMISSIONS),
			new CommandData(SBConfig.getRemoveCommandMessage(), SCRIPT_PERMISSIONS),
			new CommandData(SBConfig.getViewCommandMessage(), SCRIPT_PERMISSIONS),
			new CommandData(SBConfig.getWorldEditPasteCommandMessage(), Permission.COMMAND_WORLDEDIT),
			new CommandData(SBConfig.getWorldEditRemoveCommandMessage(), Permission.COMMAND_WORLDEDIT)
		};
	}

	@Override
	public boolean runCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 1) {
			if (equals(args[0], "tool")) {
				return doTool(sender, args);
			} else if (equals(args[0], "reload")) {
				return doReload(sender, args);
			} else if (equals(args[0], "checkver")) {
				return doCheckVer(sender, args);
			} else if (equals(args[0], "datamigr")) {
				return doDataMigr(sender, args);
			}
		} else if (args.length == 2) {
			if (equals(args[0], "interact", "break", "walk") && equals(args[1], "remove", "view")) {
				return setClickDataA(sender, args);
			} else if (equals(args[0], "worldedit") && equals(args[1], "remove")) {
				return doWorldEditRemove(sender, args);
			}
		} else if (args.length >= 3) {
			if (args.length <= 4 && equals(args[0], "worldedit") && equals(args[1], "paste")) {
				return doWorldEditPaste(sender, args);
			} else if (equals(args[0], "interact", "break", "walk") && equals(args[1], "create", "add")) {
				return setClickDataB(sender, args);
			}
		}
		return false;
	}

	private boolean doTool(CommandSender sender, String[] args) {
		if (!hasPermission(sender, Permission.COMMAND_TOOL)) {
			return false;
		}
		Player player = (Player) sender;
		ItemStack item = new ItemStack(Material.BLAZE_ROD);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§dScript Editor");
		meta.setLore(SBConfig.getScriptEditorLore());
		item.setItemMeta(meta);
		player.getInventory().addItem(item);
		Utils.updateInventory(player);
		Utils.sendMessage(player, SBConfig.getGiveScriptEditorMessage());
		return true;
	}

	private boolean doReload(CommandSender sender, String[] args) {
		if (!hasPermission(sender, Permission.COMMAND_RELOAD, false)) {
			return false;
		}
		Files.reload();
		setUsage(getUsages());
		ScriptBlock.getInstance().getMapManager().loadAllScripts();
		Utils.sendMessage(sender, SBConfig.getAllFileReloadMessage());
		return true;
	}

	private boolean doCheckVer(CommandSender sender, String[] args) {
		if (!hasPermission(sender, Permission.COMMAND_CHECKVER, false)) {
			return false;
		}
		ScriptBlock.getInstance().checkUpdate(sender, true);
		return true;
	}

	private boolean doDataMigr(CommandSender sender, String[] args) {
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
			Utils.sendMessage(player, SBConfig.getDataMigrStartMessage());
			String time = Utils.getFormatTime();
			YamlConfig scriptFile;
			if (interactFile.exists()) {
				scriptFile = YamlConfig.load(getPlugin(), interactFile, false);
				saveScript(player, time, scriptFile, ScriptType.INTERACT);
			}
			if (walkFile.exists()) {
				scriptFile = YamlConfig.load(getPlugin(), walkFile, false);
				saveScript(player, time, scriptFile, ScriptType.WALK);
			}
			Utils.sendMessage(player, SBConfig.getDataMigrEndMessage());
		}
		return true;
	}

	private void saveScript(Player player, String time, YamlConfig scriptFile, ScriptType scriptType) {
		ScriptData scriptData = new ScriptData(null, scriptType);
		String uuid = player.getUniqueId().toString();
		for (String world : scriptFile.getKeys(false)) {
			World tWorld = Utils.getWorld(world);
			for (String coords : scriptFile.getKeys(world, false)) {
				List<String> scripts = scriptFile.getStringList(world + "." + coords);
				if (scripts.size() > 0 && scripts.get(0).startsWith("Author:")) {
					scripts.remove(0);
				}
				for (int i = 0; i < scripts.size(); i++) {
					String script = scripts.get(i);
					if (script.contains("@cooldown:")) {
						scripts.set(i, StringUtils.replace(script, "@cooldown:", "@oldcooldown:"));
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

	private boolean setClickDataA(CommandSender sender, String[] args) {
		ScriptType scriptType = ScriptType.valueOf(args[0].toUpperCase());
		if (!hasPermission(sender, Permission.valueOf("COMMAND_" + scriptType.name()))) {
			return false;
		}
		SBPlayer sbPlayer = SBPlayer.fromPlayer((Player) sender);
		if (sbPlayer.hasScriptLine() || sbPlayer.hasClickAction()) {
			Utils.sendMessage(sbPlayer, SBConfig.getErrorEditDataMessage());
			return true;
		}
		ClickType clickType = ClickType.valueOf(args[1].toUpperCase());
		sbPlayer.setClickAction(clickType.createKey(scriptType));
		Utils.sendMessage(sbPlayer, SBConfig.getSuccEditDataMessage(scriptType, clickType));
		return true;
	}

	private boolean setClickDataB(CommandSender sender, String[] args) {
		ScriptType scriptType = ScriptType.valueOf(args[0].toUpperCase());
		if (!hasPermission(sender, Permission.valueOf("COMMAND_" + scriptType.name()))) {
			return false;
		}
		SBPlayer sbPlayer = SBPlayer.fromPlayer((Player) sender);
		if (sbPlayer.hasScriptLine() || sbPlayer.hasClickAction()) {
			Utils.sendMessage(sbPlayer, SBConfig.getErrorEditDataMessage());
			return true;
		}
		String script = StringUtils.createString(args, 2).trim();
		if (!checkScript(script)) {
			Utils.sendMessage(sbPlayer, SBConfig.getErrorScriptCheckMessage());
			return true;
		}
		sbPlayer.setScriptLine(script);
		ClickType clickType = ClickType.valueOf(args[1].toUpperCase());
		sbPlayer.setClickAction(clickType.createKey(scriptType));
		Utils.sendMessage(sbPlayer, SBConfig.getSuccEditDataMessage(scriptType, clickType));
		return true;
	}

	private boolean doWorldEditRemove(CommandSender sender, String[] args) {
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
		List<Block> blocks = weSelection.getBlocks(selection);
		StringBuilder builder = new StringBuilder();
		for (ScriptType scriptType : ScriptType.values()) {
			if (!Files.getScriptFile(scriptType).exists()) {
				continue;
			}
			boolean isFirst = true;
			ScriptEdit scriptEdit = new ScriptEdit(scriptType);
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
			String[] answers = {
				hasPermission(sender, Permission.COMMAND_TOOL, "tool"),
				hasPermission(sender, Permission.COMMAND_RELOAD, "reload"),
				hasPermission(sender, Permission.COMMAND_CHECKVER, "checkver"),
				hasPermission(sender, Permission.COMMAND_DATAMIGR, "datamigr"),
				hasPermission(sender, Permission.COMMAND_INTERACT, "interact"),
				hasPermission(sender, Permission.COMMAND_BREAK, "break"),
				hasPermission(sender, Permission.COMMAND_WALK, "walk"),
				hasPermission(sender, Permission.COMMAND_WORLDEDIT , "worldedit")
			};
			StreamUtils.filterForEach(answers, s -> s != null && s.startsWith(prefix), emptyList::add);
		} else if (args.length == 2) {
			if (equals(args[0], "worldedit")) {
				if (Permission.COMMAND_WORLDEDIT.has(sender)) {
					String prefix = args[1].toLowerCase();
					String[] answers = new String[]{"paste", "remove"};
					StreamUtils.filterForEach(answers, s -> s.startsWith(prefix), emptyList::add);
				}
			} else if (equals(args[0], "interact", "break", "walk")) {
				if (Permission.valueOf("COMMAND_" + args[0].toUpperCase()).has(sender)) {
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
				if (equals(args[0], "interact", "break", "walk") && equals(args[1], "create", "add")) {
					if (Permission.valueOf("COMMAND_" + args[0].toUpperCase()).has(sender)) {
						String prefix = args[args.length - 1].toLowerCase();
						String[] answers = getOptionSyntaxs();
						StreamUtils.filterForEach(answers, s -> s.startsWith(prefix), s -> emptyList.add(s.trim()));
					}
				}
			}
		}
	}

	private String hasPermission(CommandSender sender, Permission permission, String source) {
		return StringUtils.isNotEmpty(permission.getNode()) && permission.has(sender) ? source : null;
	}

	private String[] getOptionSyntaxs() {
		List<Option> options = new OptionManager().getTempOptions();
		String[] syntaxs = new String[options.size()];
		for (int i = 0; i < syntaxs.length; i++) {
			syntaxs[i] = options.get(i).getSyntax();
		}
		Arrays.sort(syntaxs);
		return syntaxs;
	}

	private boolean checkScript(String scriptLine) {
		try {
			int successCount = 0;
			List<Option> options = new OptionManager().getTempOptions();
			List<String> scripts = StringUtils.getScripts(scriptLine);
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