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
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

/**
 * ScriptBlockPlus BypassPerm オプションクラス
 * @author yuttyann44581
 */
@OptionTag(name = "bypass_perm", syntax = "@bypassPERM:")
public class BypassPerm extends BaseOption {

    @Override
    protected boolean isValid() throws Exception {
        var vaultPermission = VaultPermission.INSTANCE;
        if (!vaultPermission.isEnabled() || vaultPermission.isSuperPerms()) {
            throw new UnsupportedOperationException();
        }
        var array = StringUtils.split(getOptionValue(), '/');
        if (array.length < 2) {
            throw new IllegalArgumentException("Insufficient parameters");
        }
        var player = getPlayer();
        var command = StringUtils.setColor(array[0]);
        return CommandLog.supplier(player.getWorld(), () -> {
            var world = array.length > 2 ? array[1] : null;
            var permission = array.length > 2 ? array[2] : array[1];
            if (vaultPermission.playerHas(world, player, permission)) {
                return Utils.dispatchCommand(player, getLocation(), command);
            } else {
                try {
                    vaultPermission.playerAdd(world, player, permission);
                    return Utils.dispatchCommand(player, getLocation(), command);
                } finally {
                    vaultPermission.playerRemove(world, player, permission);
                }
            }
        });
    }
}