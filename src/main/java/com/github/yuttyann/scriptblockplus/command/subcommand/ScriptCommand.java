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
import java.util.Locale;
import java.util.Optional;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.command.BaseCommand;
import com.github.yuttyann.scriptblockplus.command.CommandUsage;
import com.github.yuttyann.scriptblockplus.command.SubCommand;
import com.github.yuttyann.scriptblockplus.enums.ActionKey;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.manager.OptionManager;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.ScriptEdit;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.selector.CommandSelector;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static java.lang.Integer.parseInt;

/**
 * ScriptBlockPlus ScriptCommand コマンドクラス
 * @author yuttyann44581
 */
public class ScriptCommand extends SubCommand {

    private final String[] PERMISSIONS = Permission.getTypeNodes(true);

    public ScriptCommand(@NotNull BaseCommand baseCommand) {
        super(baseCommand);
    }

    @Override
    @NotNull
    protected List<String> getNames() {
        return Arrays.asList(ScriptKey.types());
    }

    @Override
    @NotNull
    protected List<CommandUsage> getUsages() {
        return Arrays.asList(
                new CommandUsage(SBConfig.CREATE_COMMAND.getValue(), PERMISSIONS),
                new CommandUsage(SBConfig.ADD_COMMAND.getValue(), PERMISSIONS),
                new CommandUsage(SBConfig.REMOVE_COMMAND.getValue(), PERMISSIONS),
                new CommandUsage(SBConfig.VIEW_COMMAND.getValue(), PERMISSIONS),
                new CommandUsage(SBConfig.NAMETAG_COMMAND.getValue(), PERMISSIONS),
                new CommandUsage(SBConfig.REDSTONE_COMMAND.getValue(), PERMISSIONS),
                new CommandUsage(SBConfig.RUN_COMMAND.getValue(), PERMISSIONS));
    }

    @Override
    protected boolean runCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label) {
        var scriptKey = ScriptKey.valueOf(args(0));
        if (!Permission.has(sender, scriptKey, true)) {
            return false;
        }
        if (range(6, 7) && compare(1, "run")) {
            int i = length() == 6 ? 0 : 1;
            if (i == 0 && !isPlayer(sender)) {
                return false;
            }
            var target = Optional.ofNullable(i == 1 ? Bukkit.getPlayerExact(args(2)) : (Player) sender);
            var blockCoords = BlockCoords.of(Utils.getWorld(args(2 + i)), parseInt(args(3 + i)), parseInt(args(4 + i)), parseInt(args(5 + i)));
            target.ifPresent(p -> new ScriptRead(SBPlayer.fromPlayer(p), blockCoords, scriptKey).read(0));
            return true;
        } else if (compare(1, "create", "add", "remove", "redstone", "nametag", "view") && isPlayer(sender)) {
            var sbPlayer = SBPlayer.fromPlayer((Player) sender);
            if (sbPlayer.getScriptEdit().isPresent()) {
                SBConfig.ERROR_ACTION_DATA.send(sbPlayer);
                return true;
            }
            var actionKey = ActionKey.valueOf(args(1).toUpperCase(Locale.ROOT));
            var scriptEdit = new ScriptEdit(scriptKey, actionKey);
            switch (actionKey) {
                case CREATE: case ADD:
                    if (range(2)) {
                        return false;
                    }
                    var script = StringUtils.createString(args(), 2).trim();
                    if (!isScripts(script)) {
                        SBConfig.ERROR_SCRIPT_CHECK.send(sbPlayer);
                        return true;
                    }
                    scriptEdit.setValue(script);
                    break;
                case NAMETAG:
                    scriptEdit.setValue(range(2) ? (String) null : StringUtils.createString(args(), 2).trim());
                    break;
                case REDSTONE:
                    if (!range(2)) {
                        var selector = StringUtils.createString(args(), 3).trim();
                        if (!CommandSelector.has(selector)) {
                            selector = selector.isEmpty() ? "@p" : selector + " @p";
                        }
                        scriptEdit.setValue(selector);
                    }
                    break;
                default:
            }
            sbPlayer.setScriptEdit(scriptEdit);
            SBConfig.SUCCESS_ACTION_DATA.replace(scriptKey.getName() + "-" + actionKey.getName()).send(sbPlayer);
            return true;
        }
        return false;
    }

    private boolean isScripts(@NotNull String scriptLine) {
        try {
            var count = new int[] { 0 };
            var parse = StringUtils.parseScript(scriptLine);
            StreamUtils.fForEach(parse, OptionManager::has, o -> count[0]++);
            if (count[0] == 0 || count[0] != parse.size()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
