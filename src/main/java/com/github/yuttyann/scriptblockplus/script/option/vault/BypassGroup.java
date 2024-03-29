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
package com.github.yuttyann.scriptblockplus.script.option.vault;

import com.github.yuttyann.scriptblockplus.enums.CommandLog;
import com.github.yuttyann.scriptblockplus.hook.plugin.VaultPermission;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;
import com.github.yuttyann.scriptblockplus.utils.Utils;

/**
 * ScriptBlockPlus BypassGroup オプションクラス
 * @author yuttyann44581
 */
@OptionTag(name = "bypassgroup", syntax = "@bypassGROUP:", description = "[world]</group> <command>")
public final class BypassGroup extends BaseOption {

    @Override
    protected Result isValid() throws Exception {
        var vaultPermission = VaultPermission.INSTANCE;
        if (!vaultPermission.isEnabled() || vaultPermission.isSuperPerms()) {
            throw new UnsupportedOperationException("Invalid function");
        }
        var player = getSBPlayer().toPlayer();
        var value = getOptionValue();
        int index = value.indexOf(" ");
        var slash = split(value.substring(0, index), '/', true);
        var world = slash.size() > 1 ? slash.get(0) : null;
        var group = slash.size() > 1 ? slash.get(1) : slash.get(0);
        var command = escape(value.substring(index + 1, value.length()));
        return CommandLog.supplier(player.getWorld(), () -> {    
            if (vaultPermission.playerInGroup(world, player, group)) {
                return toResult(Utils.dispatchCommand(player, getLocation(), command));
            } else {
                try {
                    vaultPermission.playerAddGroup(world, player, group);
                    return toResult(Utils.dispatchCommand(player, getLocation(), command));
                } finally {
                    vaultPermission.playerRemoveGroup(world, player, group);
                }
            }
        });
    }
}