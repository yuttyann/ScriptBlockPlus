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

import com.github.yuttyann.scriptblockplus.command.BaseCommand;
import com.github.yuttyann.scriptblockplus.command.CommandUsage;
import com.github.yuttyann.scriptblockplus.command.SubCommand;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.file.SBFiles;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.json.BaseJson;
import com.github.yuttyann.scriptblockplus.file.json.CacheJson;
import com.github.yuttyann.scriptblockplus.item.gui.CustomGUI;
import com.github.yuttyann.scriptblockplus.item.gui.UserWindow;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus ReloadCommand コマンドクラス
 * @author yuttyann44581
 */
public class ReloadCommand extends SubCommand {

    private final Permission PERMISSION = Permission.COMMAND_RELOAD;

    public ReloadCommand(@NotNull BaseCommand baseCommand) {
        super(baseCommand);
    }

    @Override
    @NotNull
    protected List<String> getNames() {
        return Arrays.asList("reload");
    }

    @Override
    @NotNull
    protected List<CommandUsage> getUsages() {
        return Arrays.asList(new CommandUsage(SBConfig.RELOAD_COMMAND.getValue(), PERMISSION));
    }

    @Override
    protected boolean runCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label) {
        if (!hasPermission(sender, PERMISSION, false)) {
            return false;
        }
        SBFiles.reload();
        BaseJson.clear();
        CacheJson.loading();
        UserWindow.closeAll();
        CustomGUI.reload();
        SBConfig.ALL_FILE_RELOAD.send(sender);
        return true;
    }
}