/**
 * ScriptBlockPlus - Allow you to add script to any blocks.
 * Copyright (C) 2021 yuttyann44581
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package com.github.yuttyann.scriptblockplus.command.subcommand;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.command.BaseCommand;
import com.github.yuttyann.scriptblockplus.command.CommandUsage;
import com.github.yuttyann.scriptblockplus.command.SubCommand;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.file.SBFile;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.config.YamlConfig;
import com.github.yuttyann.scriptblockplus.file.json.derived.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus DatamigrCommand コマンドクラス
 * @author yuttyann44581
 */
public class DatamigrCommand extends SubCommand {

    private final Permission PERMISSION = Permission.COMMAND_DATAMIGR;

    public DatamigrCommand(@NotNull BaseCommand baseCommand) {
        super(baseCommand);
    }

    @Override
    @NotNull
    protected List<String> getNames() {
        return Arrays.asList("datamigr");
    }

    @Override
    @NotNull
    protected List<CommandUsage> getUsages() {
        return Arrays.asList(new CommandUsage(SBConfig.DATAMIGR_COMMAND.getValue(), PERMISSION));
    }

    @Override
    protected boolean runCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label) {
        if (!hasPermission(sender, PERMISSION)) {
            return false;
        }
        var path = "plugins/ScriptBlock/BlocksData/";
        var interactFile = new SBFile(path + "interact_Scripts.yml");
        var walkFile = new SBFile(path + "walk_Scripts.yml");
        if (!walkFile.exists() && !interactFile.exists()) {
            SBConfig.NOT_SCRIPT_BLOCK_FILE.send(sender);
        } else {
            SBConfig.DATAMIGR_START.send(sender);
            var uuid = ((Player) sender).getUniqueId();
            ScriptBlock.getScheduler().asyncRun(() -> {
                try {
                    convert(uuid, interactFile, ScriptKey.INTERACT);
                    convert(uuid, walkFile, ScriptKey.WALK);
                } finally {
                    SBConfig.DATAMIGR_END.send(sender);
                }
            });
        }
        return true;
    }

    private void convert(@NotNull UUID uuid, @NotNull File file, @NotNull ScriptKey scriptKey) {
        if (!file.exists()) {
            return;
        }
        var date = new Date();
        var scriptFile = YamlConfig.load(ScriptBlock.getInstance(), file, false);
        var scriptJson = BlockScriptJson.get(scriptKey);
        for (var name : scriptFile.getKeys()) {
            var world = Utils.getWorld(name);
            for (var coords : scriptFile.getKeys(name)) {
                var options = scriptFile.getStringList(name + "." + coords);
                options.replaceAll(s -> StringUtils.replace(s, "@cooldown:", "@oldcooldown:"));
                if (options.size() > 0 && options.get(0).startsWith("Author:")) {
                    options.remove(0);
                }
                var blockScript = scriptJson.load(BlockCoords.fromString(world, coords));
                blockScript.getAuthors().add(uuid);
                blockScript.setLastEdit(date);
                blockScript.setScripts(options);
            }
        }
        scriptJson.saveJson();
    }
}