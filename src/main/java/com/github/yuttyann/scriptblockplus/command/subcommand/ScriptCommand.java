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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
                new CommandUsage(SBConfig.RUN_COMMAND.getValue(), PERMISSIONS),
                new CommandUsage(SBConfig.REDSTONE_COMMAND.getValue(), PERMISSIONS));
    }

    @Override
    protected boolean runCommand(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        var scriptKey = ScriptKey.valueOf(args[0]);
        if (!isPlayer(sender) || !Permission.has(sender, scriptKey, true)) {
            return false;
        }
        if (range(args, 6) && compare(args, 1, "run")) {
            int x = Integer.parseInt(args[3]), y = Integer.parseInt(args[4]), z = Integer.parseInt(args[5]);
            new ScriptRead((Player) sender, BlockCoords.of(Utils.getWorld(args[2]), x, y, z), scriptKey).read(0);
            return true;
        } else if (compare(args, 1, "create", "add", "remove", "view", "redstone")) {
            var sbPlayer = SBPlayer.fromPlayer((Player) sender);
            if (sbPlayer.getScriptEdit().isPresent()) {
                SBConfig.ERROR_ACTION_DATA.send(sbPlayer);
                return true;
            }
            var actionKey = ActionKey.valueOf(get(args, 1).toUpperCase(Locale.ROOT));
            var scriptEdit = new ScriptEdit(scriptKey, actionKey);
            switch (actionKey) {
                case CREATE:
                case ADD:
                    if (range(args, 2)) {
                        return false;
                    }
                    var script = StringUtils.createString(args, 2).trim();
                    if (!isScripts(script)) {
                        SBConfig.ERROR_SCRIPT_CHECK.send(sbPlayer);
                        return true;
                    }
                    scriptEdit.setValue(script);
                    break;
                case REDSTONE:
                    if (range(args, 2)) {
                        return false;
                    }
                    if (compare(args, 2, "true")) {
                        var selector = StringUtils.createString(args, 3).trim();
                        scriptEdit.setValue(selector.startsWith("@s") || !CommandSelector.has(selector) ? "@p" : selector);
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
