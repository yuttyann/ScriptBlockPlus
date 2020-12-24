package com.github.yuttyann.scriptblockplus.command;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.ActionType;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.enums.reflection.PackageType;
import com.github.yuttyann.scriptblockplus.file.Json;
import com.github.yuttyann.scriptblockplus.file.SBFiles;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.config.YamlConfig;
import com.github.yuttyann.scriptblockplus.file.json.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.file.json.element.BlockScript;
import com.github.yuttyann.scriptblockplus.file.json.element.ScriptParam;
import com.github.yuttyann.scriptblockplus.listener.item.ItemAction;
import com.github.yuttyann.scriptblockplus.manager.OptionManager;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.region.CuboidRegionPaste;
import com.github.yuttyann.scriptblockplus.region.CuboidRegionRemove;
import com.github.yuttyann.scriptblockplus.region.Region;
import com.github.yuttyann.scriptblockplus.script.SBClipboard;
import com.github.yuttyann.scriptblockplus.script.ScriptEdit;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.utils.*;
import com.google.common.base.Charsets;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ScriptBlockPlus ScriptBlockPlusCommand コマンドクラス
 * @author yuttyann44581
 */
public final class ScriptBlockPlusCommand extends BaseCommand {

    public ScriptBlockPlusCommand(@NotNull ScriptBlock plugin) {
        super(plugin);
    }

    @Override
    public boolean isAliases() {
        return true;
    }

    @NotNull
    @Override
    public CommandData[] getUsages() {
        String[] typeNodes = Permission.getTypeNodes(true);
        return new CommandData[] {
                new CommandData(SBConfig.TOOL_COMMAND.getValue(), Permission.COMMAND_TOOL.getNode()),
                new CommandData(SBConfig.RELOAD_COMMAND.getValue(), Permission.COMMAND_RELOAD.getNode()),
                new CommandData(SBConfig.BACKUP_COMMAND.getValue(), Permission.COMMAND_BACKUP.getNode()),
                new CommandData(SBConfig.CHECKVER_COMMAND.getValue(), Permission.COMMAND_CHECKVER.getNode()),
                new CommandData(SBConfig.DATAMIGR_COMMAND.getValue(), Permission.COMMAND_DATAMIGR.getNode()),
                new CommandData(SBConfig.EXPORT_COMMAND.getValue(), Permission.COMMAND_EXPORT.getNode()),
                new CommandData(SBConfig.CREATE_COMMAND.getValue(), typeNodes),
                new CommandData(SBConfig.ADD_COMMAND.getValue(), typeNodes),
                new CommandData(SBConfig.REMOVE_COMMAND.getValue(), typeNodes),
                new CommandData(SBConfig.VIEW_COMMAND.getValue(), typeNodes),
                new CommandData(SBConfig.RUN_COMMAND.getValue(), typeNodes),
                new CommandData(SBConfig.SELECTOR_PASTE_COMMAND.getValue(), Permission.COMMAND_SELECTOR.getNode()),
                new CommandData(SBConfig.SELECTOR_REMOVE_COMMAND.getValue(), Permission.COMMAND_SELECTOR.getNode())
        };
    }

    @Override
    public boolean runCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 1) {
            if (equals(args[0], "tool")) {
                return doTool(sender);
            } else if (equals(args[0], "reload")) {
                return doReload(sender);
            } else if (equals(args[0], "backup")) {
                try {
                    return doBackup(sender);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
                return doSelector(sender, args);
            } else if (equals(args[0], "selector") && equals(args[1], "paste")) {
                return doSelector(sender, args);
            }
        } else if (args.length > 2) {
            if (args.length < 5 && equals(args[0], "selector") && equals(args[1], "paste")) {
                return doSelector(sender, args);
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
        SBConfig.EXPORT_START.replace(type).send(sender);
        String path = "export/" + type.toLowerCase() + "_v" + Utils.getServerVersion() + "_.txt";
        File file = new File(getPlugin().getDataFolder(), path);
        File parent = file.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        new Thread(() -> {
            try (
                OutputStream os = new FileOutputStream(file);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, Charsets.UTF_8))
            ) {
                for (Enum<?> e : isSound ? Sound.values() : Material.values()) {
                    writer.write(e.name());
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                SBConfig.EXPORT_END.replace(type).send(sender);
            }
        }).start();
        return true;
    }

    private boolean doTool(@NotNull CommandSender sender) {
        if (!hasPermission(sender, Permission.COMMAND_TOOL)) {
            return false;
        }
        Player player = (Player) sender;
        ItemAction.getItems().forEach(i -> player.getInventory().addItem(i.getItem()));
        Utils.updateInventory(player);
        SBConfig.GIVE_TOOL.send(player);
        return true;
    }

    private boolean doReload(@NotNull CommandSender sender) {
        if (!hasPermission(sender, Permission.COMMAND_RELOAD, false)) {
            return false;
        }
        SBFiles.reload();
        NameFetcher.clear();
        PackageType.clear();
        setUsage(getUsages());
        SBConfig.ALL_FILE_RELOAD.send(sender);
        return true;
    }

    private boolean doBackup(@NotNull CommandSender sender) throws IOException {
        if (!hasPermission(sender, Permission.COMMAND_BACKUP, false)) {
            return false;
        }
        File dataFolder = ScriptBlock.getInstance().getDataFolder();
        if (!dataFolder.exists() || FileUtils.isEmpty(dataFolder)) {
            SBConfig.ERROR_PLUGIN_BACKUP.send(sender);
            return true;
        }
        File backup = new File(dataFolder, "backup");
        Path source = dataFolder.toPath();
        Path target = new File(backup, Utils.getFormatTime("yyyy-MM-dd HH-mm-ss")).toPath();

        // フォルダをコピー（再帰）
        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {

            @Override
            @NotNull
            public FileVisitResult visitFile(@NotNull Path path, @NotNull BasicFileAttributes attributes) throws IOException {
                if (!path.toString().contains(SBFiles.S + "backup" + SBFiles.S)) {
                    Path targetFile = target.resolve(source.relativize(path));
                    Path parentDir = targetFile.getParent();
                    Files.createDirectories(parentDir);
                    Files.copy(path, targetFile, StandardCopyOption.REPLACE_EXISTING);
                }
                return FileVisitResult.CONTINUE;
            }
        });

        SBConfig.PLUGIN_BACKUP.send(sender);
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
        String path = "plugins" + SBFiles.S + "ScriptBlock" + SBFiles.S + "BlocksData" + SBFiles.S;
        File interactFile = new File(path + "interact_Scripts.yml");
        File walkFile = new File(path + "walk_Scripts.yml");
        if (!walkFile.exists() && !interactFile.exists()) {
            SBConfig.NOT_SCRIPT_BLOCK_FILE.send(sender);
        } else {
            SBConfig.DATAMIGR_START.send(sender);
            UUID uuid = ((Player) sender).getUniqueId();
            new Thread(() -> {
                if (interactFile.exists()) {
                    convart(uuid, interactFile, ScriptType.INTERACT);
                }
                if (walkFile.exists()) {
                    convart(uuid, walkFile, ScriptType.WALK);
                }
                SBConfig.DATAMIGR_END.send(sender);
            }).start();
        }
        return true;
    }

    private void convart(@NotNull UUID uuid, @NotNull File file, @NotNull ScriptType scriptType) {
        Json<BlockScript> json = new BlockScriptJson(scriptType);
        BlockScript blockScript = json.load();
        YamlConfig scriptFile = YamlConfig.load(getPlugin(), file, false);
        for (String name : scriptFile.getKeys()) {
            World world = Utils.getWorld(name);
            for (String coords : scriptFile.getKeys(name)) {
                List<String> script = scriptFile.getStringList(name + "." + coords);
                if (script.size() > 0 && script.get(0).startsWith("Author:")) {
                    script.remove(0);
                }
                script.replaceAll(s -> StringUtils.replace(s, "@cooldown:", "@oldcooldown:"));
                ScriptParam scriptParam = blockScript.get(BlockCoords.fromString(world, coords));
                scriptParam.getAuthor().add(uuid);
                scriptParam.setLastEdit(Utils.getFormatTime());
                scriptParam.setScript(script);
            }
        }
        json.saveFile();
    }

    private boolean doRun(@NotNull CommandSender sender, @NotNull String[] args) {
        ScriptType scriptType = ScriptType.valueOf(args[0].toUpperCase());
        if (!isPlayer(sender) || !Permission.has(sender, scriptType, true)) {
            return false;
        }
        Player player = (Player) sender;
        World world = Utils.getWorld(args[2]);
        int x = Integer.parseInt(args[3]);
        int y = Integer.parseInt(args[4]);
        int z = Integer.parseInt(args[5]);
        Location location = new Location(world, x, y, z);
        ScriptBlock.getInstance().getAPI().read(player, location, scriptType, 0);
        return true;
    }

    private boolean setAction(@NotNull CommandSender sender, @NotNull String[] args) {
        ScriptType scriptType = ScriptType.valueOf(args[0].toUpperCase());
        if (!isPlayer(sender) || !Permission.has(sender, scriptType, true)) {
            return false;
        }
        SBPlayer sbPlayer = SBPlayer.fromPlayer((Player) sender);
        if (sbPlayer.getScriptLine().isPresent() || sbPlayer.getScriptEdit().isPresent()) {
            SBConfig.ERROR_ACTION_DATA.send(sbPlayer);
            return true;
        }
        if (args.length > 2) {
            String script = StringUtils.createString(args, 2).trim();
            if (!isScripts(script)) {
                SBConfig.ERROR_SCRIPT_CHECK.send(sbPlayer);
                return true;
            }
            sbPlayer.setScriptLine(script);
        }
        ActionType actionType = ActionType.valueOf(args[1].toUpperCase());
        sbPlayer.setScriptEdit(new ScriptEdit(actionType, scriptType));
        SBConfig.SUCCESS_ACTION_DATA.replace(scriptType.type() + "-" + actionType.name().toLowerCase()).send(sbPlayer);
        return true;
    }

    private boolean doSelector(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!hasPermission(sender, Permission.COMMAND_SELECTOR)) {
            return false;
        }
        SBPlayer sbPlayer = SBPlayer.fromPlayer((Player) sender);
        Region region = sbPlayer.getRegion();
        if (!region.hasPositions()) {
            SBConfig.NOT_SELECTION.send(sender);
            return true;
        }
        if (equals(args[1], "paste")) {
            if (!sbPlayer.getSBClipboard().isPresent()) {
                SBConfig.ERROR_SCRIPT_FILE_CHECK.send(sender);
                return true;
            }
            SBClipboard clipboard = sbPlayer.getSBClipboard().get();
            try {
                boolean pasteonair = args.length > 2 && Boolean.parseBoolean(args[2]);
                boolean overwrite = args.length > 3 && Boolean.parseBoolean(args[3]);
                CuboidRegionPaste regionPaste = new CuboidRegionPaste(clipboard, region).paste(pasteonair, overwrite);
                String scriptType = regionPaste.getScriptType().type();
                SBConfig.SELECTOR_PASTE.replace(scriptType, regionPaste.getCuboidRegionBlocks().getCount()).send(sbPlayer);
                SBConfig.CONSOLE_SELECTOR_PASTE.replace(scriptType, regionPaste.getCuboidRegionBlocks()).console();
            } finally {
                sbPlayer.setSBClipboard(null);
            }
        } else {
            CuboidRegionRemove regionRemove = new CuboidRegionRemove(region).remove();
            Set<ScriptType> scriptTypes = regionRemove.getScriptTypes();
            if (scriptTypes.size() == 0) {
                SBConfig.ERROR_SCRIPT_FILE_CHECK.send(sender);
            } else {
                String types = scriptTypes.stream().map(ScriptType::type).collect(Collectors.joining(", "));
                SBConfig.SELECTOR_REMOVE.replace(types, regionRemove.getCuboidRegionBlocks().getCount()).send(sbPlayer);
                SBConfig.CONSOLE_SELECTOR_REMOVE.replace(types, regionRemove.getCuboidRegionBlocks()).console();
            }
        }
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
                    String[] answers = { "sound", "material" };
                    StreamUtils.fForEach(answers, s -> s.startsWith(prefix), empty::add);
                }
            } else if (equals(args[0], "selector")) {
                if (Permission.COMMAND_SELECTOR.has(sender)) {
                    String prefix = args[1].toLowerCase();
                    String[] answers = { "paste", "remove" };
                    StreamUtils.fForEach(answers, s -> s.startsWith(prefix), empty::add);
                }
            } else if (equals(args[0], ScriptType.types())) {
                if (Permission.has(sender, ScriptType.valueOf(args[0].toUpperCase()), true)) {
                    String prefix = args[1].toLowerCase();
                    String[] answers = { "create", "add", "remove", "view", "run" };
                    StreamUtils.fForEach(answers, s -> s.startsWith(prefix), empty::add);
                }
            }
        } else if (args.length > 2) {
            if (args.length == 3 && equals(args[0], "selector") && equals(args[1], "paste")) {
                if (Permission.COMMAND_SELECTOR.has(sender)) {
                    String prefix = args[2].toLowerCase();
                    String[] answers = { "true", "false" };
                    StreamUtils.fForEach(answers, s -> s.startsWith(prefix), empty::add);
                }
            } else if (args.length == 4 && equals(args[0], "selector") && equals(args[1], "paste")) {
                if (Permission.COMMAND_SELECTOR.has(sender)) {
                    String prefix = args[3].toLowerCase();
                    String[] answers = { "true", "false" };
                    StreamUtils.fForEach(answers, s -> s.startsWith(prefix), empty::add);
                }
            } else if (equals(args[0], ScriptType.types())) {
                if (Permission.has(sender, ScriptType.valueOf(args[0].toUpperCase()), true)) {
                    if (args.length == 3 && equals(args[1], "run")) {
                        List<World> worlds = Bukkit.getWorlds();
                        String prefix = args[args.length - 1].toLowerCase();
                        String[] answers = StreamUtils.toArray(worlds, World::getName, new String[worlds.size()]);
                        StreamUtils.fForEach(answers, s -> s.startsWith(prefix), empty::add);
                    } else if (equals(args[1], "create", "add")) {
                        String prefix = args[args.length - 1].toLowerCase();
                        String[] answers = OptionManager.getSyntaxs();
                        Arrays.sort(answers);
                        StreamUtils.fForEach(answers, s -> s.startsWith(prefix), s -> empty.add(s.trim()));
                    }
                }
            }
        }
    }

    @NotNull
    private Set<String> setCommandPermissions(@NotNull CommandSender sender, @NotNull Set<String> set) {
        StreamUtils.filter(set, s -> Permission.COMMAND_TOOL.has(sender), s -> s.add("tool"));
        StreamUtils.filter(set, s -> Permission.COMMAND_RELOAD.has(sender), s -> s.add("reload"));
        StreamUtils.filter(set, s -> Permission.COMMAND_BACKUP.has(sender), s -> s.add("backup"));
        StreamUtils.filter(set, s -> Permission.COMMAND_CHECKVER.has(sender), s -> s.add("checkver"));
        StreamUtils.filter(set, s -> Permission.COMMAND_DATAMIGR.has(sender), s -> s.add("datamigr"));
        StreamUtils.filter(set, s -> Permission.COMMAND_EXPORT.has(sender), s -> s.add("export"));
        StreamUtils.filter(set, s -> Permission.COMMAND_SELECTOR.has(sender), s -> s.add("selector"));
        StreamUtils.fForEach(ScriptType.values(), s -> Permission.has(sender, s, true), s -> set.add(s.type()));
        return set;
    }

    private boolean isScripts(@NotNull String scriptLine) {
        try {
            int[] success = { 0 };
            List<String> scripts = StringUtils.getScripts(scriptLine);
            StreamUtils.fForEach(scripts, OptionManager::has, o -> success[0]++);
            if (success[0] == 0 || success[0] != scripts.size()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}