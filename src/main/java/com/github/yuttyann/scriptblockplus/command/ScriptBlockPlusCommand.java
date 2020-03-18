package com.github.yuttyann.scriptblockplus.command;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.ActionType;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.enums.reflection.PackageType;
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
import com.github.yuttyann.scriptblockplus.utils.*;
import com.google.common.base.Charsets;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class ScriptBlockPlusCommand extends BaseCommand {

	public ScriptBlockPlusCommand(@NotNull ScriptBlock plugin) {
		super(plugin);
	}

	@NotNull
	@Override
	public String getCommandName() {
		return "ScriptBlockPlus";
	}

	@Override
	public boolean isAliases() {
		return true;
	}

	@NotNull
	@Override
	public CommandData[] getUsages() {
		String[] typeNodes = SBPermission.getNodes(true);
		return new CommandData[] {
				new CommandData(SBConfig.TOOL_COMMAND.get(), Permission.COMMAND_TOOL.getNode()),
				new CommandData(SBConfig.RELOAD_COMMAND.get(), Permission.COMMAND_RELOAD.getNode()),
				new CommandData(SBConfig.BACKUP_COMMAND.get(), Permission.COMMAND_BACKUP.getNode()),
				new CommandData(SBConfig.CHECKVER_COMMAND.get(), Permission.COMMAND_CHECKVER.getNode()),
				new CommandData(SBConfig.DATAMIGR_COMMAND.get(), Permission.COMMAND_DATAMIGR.getNode()),
				new CommandData(SBConfig.EXPORT_COMMAND.get(), Permission.COMMAND_EXPORT.getNode()),
				new CommandData(SBConfig.CREATE_COMMAND.get(), typeNodes),
				new CommandData(SBConfig.ADD_COMMAND.get(), typeNodes),
				new CommandData(SBConfig.REMOVE_COMMAND.get(), typeNodes),
				new CommandData(SBConfig.VIEW_COMMAND.get(), typeNodes),
				new CommandData(SBConfig.RUN_COMMAND.get(), typeNodes),
				new CommandData(SBConfig.SELECTOR_PASTE_COMMAND.get(), Permission.COMMAND_SELECTOR.getNode()),
				new CommandData(SBConfig.SELECTOR_REMOVE_COMMAND.get(), Permission.COMMAND_SELECTOR.getNode())
		};
	}

	@Override
	public boolean runCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
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

	private boolean doExport(@NotNull CommandSender sender, @NotNull String[] args) {
		if (!hasPermission(sender, Permission.COMMAND_EXPORT, false)) {
			return false;
		}
		boolean isSound = args[1].equalsIgnoreCase("sound");
		String type = isSound ? "Sound" : "Material";
		Utils.sendMessage(sender, SBConfig.EXPORT_START.replace(type).toString(true));
		new Thread(() -> {
			String version = Utils.getServerVersion();
			File file = new File(getPlugin().getDataFolder(), "export/" + type.toLowerCase() + "_v" + version + "_.txt");
			write(file, isSound ? Sound.values() : Material.values());
			Utils.sendMessage(sender, SBConfig.EXPORT_END.replace(type).toString(true));
		}).start();
		return true;
	}

	private void write(@NotNull File file, @NotNull Enum<?>[] values) {
		File parent = file.getParentFile();
		if (!parent.exists()) {
			parent.mkdirs();
		}
		try (
				OutputStream os = new FileOutputStream(file);
				OutputStreamWriter osw = new OutputStreamWriter(os, Charsets.UTF_8);
				BufferedWriter writer = new BufferedWriter(osw)
			) {
			for (Enum<?> t : values) {
				writer.write(t.name());
				writer.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean doTool(@NotNull CommandSender sender, @NotNull String[] args) {
		if (!hasPermission(sender, Permission.COMMAND_TOOL)) {
			return false;
		}
		Player player = (Player) sender;
		player.getInventory().addItem(ItemUtils.getBlockSelector());
		player.getInventory().addItem(ItemUtils.getScriptEditor(ScriptType.INTERACT));
		Utils.updateInventory(player);
		SBConfig.GIVE_TOOL.send(player, true);
		return true;
	}

	private boolean doReload(@NotNull CommandSender sender) {
		if (!hasPermission(sender, Permission.COMMAND_RELOAD, false)) {
			return false;
		}
		Files.reload();
		NameFetcher.clear();
		PackageType.clear();
		setUsage(getUsages());
		ScriptBlock.getInstance().getMapManager().loadAllScripts();
		SBConfig.ALL_FILE_RELOAD.send(sender, true);
		return true;
	}

	private boolean doBackup(@NotNull CommandSender sender) {
		if (!hasPermission(sender, Permission.COMMAND_BACKUP, false)) {
			return false;
		}
		File dataFolder = Files.getConfig().getDataFolder();
		File scripts = new File(dataFolder, "scripts");
		if (!scripts.exists() || FileUtils.isEmpty(scripts)) {
			SBConfig.ERROR_SCRIPTS_BACKUP.send(sender, true);
			return true;
		}
		File backup = new File(scripts, "backup");
		String formatTime = Utils.getFormatTime("yyyy-MM-dd HH-mm-ss");
		FileUtils.copyDirectory(scripts, new File(backup, "scripts " + formatTime), File::isDirectory);
		SBConfig.SCRIPTS_BACKUP.send(sender, true);
		return true;
	}

	private boolean doCheckVer(@NotNull CommandSender sender) {
		if (!hasPermission(sender, Permission.COMMAND_CHECKVER, false)) {
			return false;
		}
		ScriptBlock.getInstance().checkUpdate(sender, true);
		return true;
	}

	private boolean doDataMigr(@NotNull CommandSender sender) {
		if (!hasPermission(sender, Permission.COMMAND_DATAMIGR)) {
			return false;
		}
		String path = "plugins/ScriptBlock/BlocksData/";
		File interactFile = new File(path + "interact_Scripts.yml");
		File walkFile = new File(path + "walk_Scripts.yml");
		Player player = (Player) sender;
		if (!walkFile.exists() && !interactFile.exists()) {
			SBConfig.NOT_SCRIPT_BLOCK_FILE.send(sender, true);
		} else {
			String time = Utils.getFormatTime();
			String uuid = player.getUniqueId().toString();
			SBConfig.DATAMIGR_START.send(sender, true);
			new Thread(() -> {
				if (interactFile.exists()) {
					saveScript(uuid, time, YamlConfig.load(getPlugin(), interactFile, false), ScriptType.INTERACT);
				}
				if (walkFile.exists()) {
					saveScript(uuid, time, YamlConfig.load(getPlugin(), walkFile, false), ScriptType.WALK);
				}
				SBConfig.DATAMIGR_END.send(sender, true);
			}).start();
		}
		return true;
	}

	private void saveScript(@NotNull String uuid, @NotNull String time, @NotNull YamlConfig scriptFile, @NotNull ScriptType scriptType) {
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

	private boolean doRun(@NotNull CommandSender sender, @NotNull String[] args) {
		ScriptType scriptType = ScriptType.valueOf(args[0].toUpperCase());
		if (!isPlayer(sender) || !SBPermission.has(sender, scriptType, true)) {
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

	private boolean setAction(@NotNull CommandSender sender, @NotNull String[] args) {
		ScriptType scriptType = ScriptType.valueOf(args[0].toUpperCase());
		if (!isPlayer(sender) || !SBPermission.has(sender, scriptType, true)) {
			return false;
		}
		SBPlayer sbPlayer = SBPlayer.fromPlayer((Player) sender);
		if (sbPlayer.hasScriptLine() || sbPlayer.hasActionType()) {
			SBConfig.ERROR_ACTION_DATA.send(sbPlayer, true);
			return true;
		}
		if (args.length > 2) {
			String script = StringUtils.createString(args, 2).trim();
			if (!checkScript(script)) {
				SBConfig.ERROR_SCRIPT_CHECK.send(sbPlayer, true);
				return true;
			}
			sbPlayer.setScriptLine(script);
		}
		ActionType actionType = ActionType.valueOf(args[1].toUpperCase());
		sbPlayer.setActionType(actionType.getKey(scriptType));
		String type = scriptType.getType() + "-" + actionType.name().toLowerCase();
		SBConfig.SUCCESS_ACTION_DATA.replace(type).send(sbPlayer, true);
		return true;
	}

	private boolean doSelectorRemove(@NotNull CommandSender sender) {
		if (!hasPermission(sender, Permission.COMMAND_SELECTOR)) {
			return false;
		}
		Player player = (Player) sender;
		Region region = SBPlayer.fromPlayer(player).getRegion();
		if (!region.hasPositions()) {
			SBConfig.NOT_SELECTION.send(sender, true);
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
			SBConfig.ERROR_SCRIPT_FILE_CHECK.send(sender, true);
		} else {
			String types = builder.toString();
			String count = "" + regionBlocks.getCount();
			String world = regionBlocks.getWorld().getName();
			String min = BlockCoords.getCoords(regionBlocks.getMinimumPoint());
			String max = BlockCoords.getCoords(regionBlocks.getMaximumPoint());
			SBConfig.SELECTOR_REMOVE.replace(types, count).send(player, true);
			SBConfig.CONSOLE_SELECTOR_REMOVE.replace(types, count, world, min, max).console(true);
		}
		return true;
	}

	private boolean doSelectorPaste(@NotNull CommandSender sender, @NotNull String[] args) {
		if (!hasPermission(sender, Permission.COMMAND_SELECTOR)) {
			return false;
		}
		SBPlayer sbPlayer = SBPlayer.fromPlayer((Player) sender);
		if (!sbPlayer.hasClipboard()) {
			SBConfig.ERROR_SCRIPT_FILE_CHECK.send(sender, true);
			return true;
		}
		Region region = sbPlayer.getRegion();
		if (!region.hasPositions()) {
			SBConfig.NOT_SELECTION.send(sender, true);
			return true;
		}
		boolean pasteonair = args.length > 2 && Boolean.parseBoolean(args[2]);
		boolean overwrite = args.length > 3 && Boolean.parseBoolean(args[3]);
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

		String type = clipboard.getScriptType().getType();
		String count = "" + regionBlocks.getCount();
		String world = regionBlocks.getWorld().getName();
		String min = BlockCoords.getCoords(regionBlocks.getMinimumPoint());
		String max = BlockCoords.getCoords(regionBlocks.getMaximumPoint());
		SBConfig.SELECTOR_PASTE.replace(type, count).send(sbPlayer, true);
		SBConfig.CONSOLE_SELECTOR_PASTE.replace(type, count, world, min, max).console(true);
		return true;
	}

	@Override
	public void tabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args, @NotNull List<String> empty) {
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
						String[] answers = StreamUtils.toArray(worlds, World::getName, new String[worlds.size()]);
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
}