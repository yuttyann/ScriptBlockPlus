package com.github.yuttyann.scriptblockplus.command;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.ActionType;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.file.yaml.YamlConfig;
import com.github.yuttyann.scriptblockplus.manager.OptionManager.OptionList;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.region.CuboidRegionBlocks;
import com.github.yuttyann.scriptblockplus.region.Region;
import com.github.yuttyann.scriptblockplus.script.SBClipboard;
import com.github.yuttyann.scriptblockplus.script.ScriptData;
import com.github.yuttyann.scriptblockplus.script.ScriptEdit;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.script.ScriptType.SBPermission;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.FileUtils;
import com.github.yuttyann.scriptblockplus.utils.ItemUtils;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import com.google.common.base.Charsets;

public final class ScriptBlockPlusCommand extends BaseCommand {

	public ScriptBlockPlusCommand(ScriptBlock plugin) {
		super(plugin);
	}

	@Override
	public String getName() {
		return "ScriptBlockPlus";
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
				new CommandData(SBConfig.getExportCommandMessage(), Permission.COMMAND_EXPORT.getNode()),
				new CommandData(SBConfig.getCreateCommandMessage(), typeNodes),
				new CommandData(SBConfig.getAddCommandMessage(), typeNodes),
				new CommandData(SBConfig.getRemoveCommandMessage(), typeNodes),
				new CommandData(SBConfig.getViewCommandMessage(), typeNodes),
				new CommandData(SBConfig.getRunCommandMessage(), typeNodes),
				new CommandData(SBConfig.getSelectorPasteCommandMessage(), Permission.COMMAND_SELECTOR.getNode()),
				new CommandData(SBConfig.getSelectorRemoveCommandMessage(), Permission.COMMAND_SELECTOR.getNode())
		};
	}

	@Override
	public boolean runCommand(CommandSender sender, Command command, String label, String[] args) {
		// viewArgs(sender, args);
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
			if (equals(args[0], "export") && equals(args[1], "sound", "material")) {
				return doExport(sender, args);
			} if (equals(args[0], ScriptType.types()) && equals(args[1], "remove", "view")) {
				return setAction(sender, args);
			} else if (equals(args[0], "selector") && equals(args[1], "remove")) {
				return doSelectorRemove(sender);
			} else if (equals(args[0], "selector") && equals(args[1], "paste")) {
				return doSelectorPaste(sender, args);
			}
		} else if (args.length > 2) {
			if (args.length < 5 && equals(args[0], "selector") && equals(args[1], "paste")) {
				return doSelectorPaste(sender, args);
			} else if (equals(args[0], ScriptType.types())) {
				if (args.length == 6 && equals(args[1], "run")) {
					return doRun(sender, args);
				} else if (equals(args[1], "create", "add")) {
					return setAction(sender, args);
				}
			}
		}
		return false;
	}

	private boolean doExport(CommandSender sender, String[] args) {
		if (!hasPermission(sender, Permission.COMMAND_EXPORT, false)) {
			return false;
		}
		boolean isSound = args[1].equalsIgnoreCase("sound");
		String type = isSound ? "Sound" : "Material";
		Utils.sendMessage(sender, SBConfig.getExportStartMessage(type));
		new Thread(() -> {
			String version = Utils.getServerVersion();
			File file = new File(getPlugin().getDataFolder(), "export/" + type.toLowerCase() + "_v" + version + "_.txt");
			write(file, isSound ? Sound.values() : Material.values());
			Utils.sendMessage(sender, SBConfig.getExportEndMessage(type));
		}).start();
		return true;
	}

	private void write(File file, Enum<?>[] values) {
		File parent = file.getParentFile();
		if (!parent.exists()) {
			parent.mkdirs();
		}
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), Charsets.UTF_8));
			for (Enum<?> t : values) {
				writer.write(t.name());
				writer.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.flush();
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private boolean doTool(CommandSender sender, String[] args) {
		if (!hasPermission(sender, Permission.COMMAND_TOOL)) {
			return false;
		}
		Player player = (Player) sender;
		player.getInventory().addItem(ItemUtils.getBlockSelector());
		player.getInventory().addItem(ItemUtils.getScriptEditor(ScriptType.INTERACT));
		Utils.updateInventory(player);
		Utils.sendMessage(player, SBConfig.getGiveToolMessage());
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
		File backup = new File(scripts, "backup");
		String formatTime = Utils.getFormatTime("yyyy-MM-dd HH-mm-ss");
		FileUtils.copyDirectory(scripts, new File(backup, "scripts " + formatTime), f -> f.isDirectory());
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
				if (interactFile.exists()) {
					saveScript(uuid, time, YamlConfig.load(getPlugin(), interactFile, false), ScriptType.INTERACT);
				}
				if (walkFile.exists()) {
					saveScript(uuid, time, YamlConfig.load(getPlugin(), walkFile, false), ScriptType.WALK);
				}
				Utils.sendMessage(player, SBConfig.getDataMigrEndMessage());
			}).start();
		}
		return true;
	}

	private void saveScript(String uuid, String time, YamlConfig scriptFile, ScriptType scriptType) {
		ScriptData scriptData = new ScriptData(null, scriptType);
		for (String world : scriptFile.getKeys()) {
			World tWorld = Utils.getWorld(world);
			for (String coords : scriptFile.getKeys(world)) {
				List<String> scripts = scriptFile.getStringList(world + "." + coords);
				if (scripts.size() > 0 && scripts.get(0).startsWith("Author:")) {
					scripts.remove(0);
				}
				for (int i = 0; i < scripts.size(); i++) {
					if (scripts.get(i).contains("@cooldown:")) {
						scripts.set(i, StringUtils.replace(scripts.get(i), "@cooldown:", "@oldcooldown:"));
					}
				}
				scriptData.setLocation(BlockCoords.fromString(tWorld, coords));
				scriptData.setAuthor(uuid);
				scriptData.setLastEdit(time);
				scriptData.setScripts(scripts);
			}
		}
		scriptData.save();
		ScriptBlock.getInstance().getMapManager().loadScripts(scriptFile, scriptType);
	}

	private boolean doRun(CommandSender sender, String[] args) {
		ScriptType scriptType = ScriptType.valueOf(args[0].toUpperCase());
		if (!SBPermission.has(sender, scriptType, true)) {
			return false;
		}
		Player player = (Player) sender;
		World world = Utils.getWorld(args[2]);
		int x = Integer.parseInt(args[3]);
		int y = Integer.parseInt(args[4]);
		int z = Integer.parseInt(args[5]);
		Location location = new Location(world, x, y, z);
		ScriptBlock.getInstance().getAPI().scriptRead(player, location, scriptType, 0);
		return true;
	}

	private boolean setAction(CommandSender sender, String[] args) {
		ScriptType scriptType = ScriptType.valueOf(args[0].toUpperCase());
		if (!SBPermission.has(sender, scriptType, true)) {
			return false;
		}
		SBPlayer sbPlayer = SBPlayer.fromPlayer((Player) sender);
		if (sbPlayer.hasScriptLine() || sbPlayer.hasActionType()) {
			Utils.sendMessage(sbPlayer, SBConfig.getErrorActionDataMessage());
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
		sbPlayer.setActionType(actionType.getKey(scriptType));
		Utils.sendMessage(sbPlayer, SBConfig.getSuccActionDataMessage(scriptType, actionType));
		return true;
	}

	private boolean doSelectorRemove(CommandSender sender) {
		if (!hasPermission(sender, Permission.COMMAND_SELECTOR)) {
			return false;
		}
		Player player = (Player) sender;
		Region region = SBPlayer.fromPlayer(player).getRegion();
		if (!region.hasPositions()) {
			Utils.sendMessage(player, SBConfig.getNotSelectionMessage());
			return true;
		}
		StringBuilder builder = new StringBuilder();
		CuboidRegionBlocks regionBlocks = new CuboidRegionBlocks(region);
		Set<Block> blocks = regionBlocks.getBlocks();
		for (ScriptType scriptType : ScriptType.values()) {
			if (!Files.getScriptFile(scriptType).exists()) {
				continue;
			}
			boolean isFirst = true;
			ScriptEdit scriptEdit = new ScriptEdit(scriptType);
			for (Block block : blocks) {
				if (scriptEdit.lightRemove(block.getLocation()) && isFirst && !(isFirst = false)) {
					builder.append(builder.length() == 0 ? "" : ", ").append(scriptType.getType());
				}
			}
			scriptEdit.save();
		}
		if (builder.length() == 0) {
			Utils.sendMessage(player, SBConfig.getErrorScriptFileCheckMessage());
		} else {
			String types = builder.toString();
			Utils.sendMessage(player, SBConfig.getSelectorRemoveMessage(types, regionBlocks));
			Utils.sendMessage(SBConfig.getConsoleSelectorRemoveMessage(types, regionBlocks));
		}
		return true;
	}

	private boolean doSelectorPaste(CommandSender sender, String[] args) {
		if (!hasPermission(sender, Permission.COMMAND_SELECTOR)) {
			return false;
		}
		SBPlayer sbPlayer = SBPlayer.fromPlayer((Player) sender);
		if (!sbPlayer.hasClipboard()) {
			Utils.sendMessage(sbPlayer, SBConfig.getErrorScriptFileCheckMessage());
			return true;
		}
		Region region = sbPlayer.getRegion();
		if (!region.hasPositions()) {
			Utils.sendMessage(sbPlayer, SBConfig.getNotSelectionMessage());
			return true;
		}
		boolean pasteonair = args.length > 2 ? Boolean.parseBoolean(args[2]) : false;
		boolean overwrite = args.length > 3 ? Boolean.parseBoolean(args[3]) : false;
		SBClipboard clipboard = sbPlayer.getClipboard();
		CuboidRegionBlocks regionBlocks = new CuboidRegionBlocks(region);
		for (Block block : regionBlocks.getBlocks()) {
			if (!pasteonair && (block == null || block.getType() == Material.AIR)) {
				continue;
			}
			clipboard.lightPaste(block.getLocation(), overwrite, false);
		}
		clipboard.save();
		sbPlayer.setClipboard(null);
		Utils.sendMessage(sbPlayer, SBConfig.getSelectorPasteMessage(clipboard.getScriptType(), regionBlocks));
		Utils.sendMessage(SBConfig.getConsoleSelectorPasteMessage(clipboard.getScriptType(), regionBlocks));
		return true;
	}

	@Override
	public void tabComplete(CommandSender sender, Command command, String label, String[] args, List<String> empty) {
		if (args.length == 1) {
			String prefix = args[0].toLowerCase();
			Set<String> set = setCommandPermissions(sender, new LinkedHashSet<>());
			StreamUtils.fForEach(set, s -> StringUtils.startsWith(s, prefix), empty::add);
		} else if (args.length == 2) {
			if (equals(args[0], "export")) {
				if (Permission.COMMAND_EXPORT.has(sender)) {
					String prefix = args[1].toLowerCase();
					String[] answers = new String[] { "sound", "material" };
					StreamUtils.fForEach(answers, s -> s.startsWith(prefix), empty::add);
				}
			} else if (equals(args[0], "selector")) {
				if (Permission.COMMAND_SELECTOR.has(sender)) {
					String prefix = args[1].toLowerCase();
					String[] answers = new String[] { "paste", "remove" };
					StreamUtils.fForEach(answers, s -> s.startsWith(prefix), empty::add);
				}
			} else if (equals(args[0], ScriptType.types())) {
				if (SBPermission.has(sender, ScriptType.valueOf(args[0].toUpperCase()), true)) {
					String prefix = args[1].toLowerCase();
					String[] answers = new String[] { "create", "add", "remove", "view", "run" };
					StreamUtils.fForEach(answers, s -> s.startsWith(prefix), empty::add);
				}
			}
		} else if (args.length > 2) {
			if (args.length == 3 && equals(args[0], "selector") && equals(args[1], "paste")) {
				if (Permission.COMMAND_SELECTOR.has(sender)) {
					String prefix = args[2].toLowerCase();
					String[] answers = new String[] { "true", "false" };
					StreamUtils.fForEach(answers, s -> s.startsWith(prefix), empty::add);
				}
			} else if (args.length == 4 && equals(args[0], "selector") && equals(args[1], "paste")) {
				if (Permission.COMMAND_SELECTOR.has(sender)) {
					String prefix = args[3].toLowerCase();
					String[] answers = new String[] { "true", "false" };
					StreamUtils.fForEach(answers, s -> s.startsWith(prefix), empty::add);
				}
			} else if (equals(args[0], ScriptType.types())) {
				if (SBPermission.has(sender, ScriptType.valueOf(args[0].toUpperCase()), true)) {
					if (args.length == 3 && equals(args[1], "run")) {
						List<World> worlds = Bukkit.getWorlds();
						String prefix = args[args.length - 1].toLowerCase();
						String[] answers = StreamUtils.toArray(worlds, w -> w.getName(), new String[worlds.size()]);
						StreamUtils.fForEach(answers, s -> s.startsWith(prefix), empty::add);
					} else if (equals(args[1], "create", "add")) {
						String prefix = args[args.length - 1].toLowerCase();
						String[] answers = OptionList.getSyntaxs();
						Arrays.sort(answers);
						StreamUtils.fForEach(answers, s -> s.startsWith(prefix), s -> empty.add(s.trim()));
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
		set.add(hasPermission(sender, Permission.COMMAND_EXPORT, "export"));

		StreamUtils.fForEach(ScriptType.values(), s -> hasPermission(sender, s), s -> set.add(s.getType()));

		set.add(hasPermission(sender, Permission.COMMAND_SELECTOR, "selector"));
		return set;
	}

	private boolean hasPermission(Permissible permissible, ScriptType scriptType) {
		return SBPermission.has(permissible, scriptType, true);
	}

	private String hasPermission(CommandSender sender, Permission permission, String name) {
		return StringUtils.isNotEmpty(permission.getNode()) && permission.has(sender) ? name : null;
	}

	private boolean checkScript(String scriptLine) {
		try {
			List<Option> options = OptionList.getList();
			List<String> scripts = StringUtils.getScripts(scriptLine);
			int[] success = { 0 };
			scripts.forEach(s -> StreamUtils.fForEach(options, o -> o.isOption(s), o -> success[0]++));
			if (success[0] == 0 || success[0] != scripts.size()) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/*
	private void viewArgs(CommandSender sender, String[] args) {
		Utils.sendMessage(sender, "Length: " + args.length);
		for (int i = 0; i < args.length; i++) {
			Utils.sendMessage(sender, "[" + i + "] = " + args[i]);
		}
	}
	*/
}