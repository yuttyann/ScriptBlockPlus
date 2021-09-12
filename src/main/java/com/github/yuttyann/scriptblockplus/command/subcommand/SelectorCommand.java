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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.github.yuttyann.scriptblockplus.command.BaseCommand;
import com.github.yuttyann.scriptblockplus.command.CommandUsage;
import com.github.yuttyann.scriptblockplus.command.SubCommand;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.region.CuboidRegionPaste;
import com.github.yuttyann.scriptblockplus.region.CuboidRegionRemove;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus SelectorCommand コマンドクラス
 * @author yuttyann44581
 */
public class SelectorCommand extends SubCommand {

    private final Permission PERMISSION = Permission.COMMAND_SELECTOR;

    public SelectorCommand(@NotNull BaseCommand baseCommand) {
        super(baseCommand);
    }

    @Override
    @NotNull
    protected List<String> getNames() {
        return Arrays.asList("selector");
    }

    @Override
    @NotNull
    protected List<CommandUsage> getUsages() {
        return Arrays.asList(
                    new CommandUsage(SBConfig.SELECTOR_PASTE_COMMAND.getValue(), PERMISSION),
                    new CommandUsage(SBConfig.SELECTOR_REMOVE_COMMAND.getValue(), PERMISSION));
    }

    @Override
    protected boolean runCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label) {
        if (!hasPermission(sender, PERMISSION)) {
            return false;
        }
        var player = (Player) sender;
        var region = SBPlayer.fromPlayer(player).getRegion();
        if (!region.hasPositions()) {
            SBConfig.NOT_SELECTION.send(sender);
            return true;
        }
        if (compare(1, "paste")) {
            var sbPlayer = SBPlayer.fromPlayer(player);
            if (!sbPlayer.getSBClipboard().isPresent()) {
                SBConfig.ERROR_SCRIPT_FILE_CHECK.send(sender);
                return true;
            }
            boolean pasteonair = Boolean.valueOf(args(2)), overwrite = Boolean.valueOf(args(3));
            try {
                var sbClipboard = sbPlayer.getSBClipboard().get();
                var regionPaste = new CuboidRegionPaste(region, sbClipboard).paste(pasteonair, overwrite);
                var scriptKeyName = regionPaste.getScriptKey().getName();
                SBConfig.SELECTOR_PASTE.replace(scriptKeyName, regionPaste.getVolume()).send(sbPlayer);
                SBConfig.CONSOLE_SELECTOR_PASTE.replace(scriptKeyName, regionPaste.iterator()).console();
            } finally {
                sbPlayer.setSBClipboard(null);
            }
            return true;
        } else if (compare(1, "remove")) {
            var regionRemove = new CuboidRegionRemove(region).remove();
            var scriptKeys = regionRemove.getScriptKeys();
            if (scriptKeys.size() == 0) {
                SBConfig.ERROR_SCRIPT_FILE_CHECK.send(sender);
            } else {
                var types = scriptKeys.stream().map(ScriptKey::getName).collect(Collectors.joining(", "));
                SBConfig.SELECTOR_REMOVE.replace(types, regionRemove.getVolume()).send(player);
                SBConfig.CONSOLE_SELECTOR_REMOVE.replace(types, regionRemove.iterator()).console();
            }
            return true;
        }
        return false;
    }
}
