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

import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.hook.plugin.VaultEconomy;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;
import com.github.yuttyann.scriptblockplus.utils.Utils;

/**
 * ScriptBlockPlus MoneyCost オプションクラス
 * @author yuttyann44581
 */
@OptionTag(name = "moneycost", syntax = "$cost:", description = "<price>")
public final class MoneyCost extends BaseOption {

    public static final String KEY = Utils.randomUUID();

    @Override
    protected boolean isValid() throws Exception {
        var vaultEconomy = VaultEconomy.INSTANCE;
        if (!vaultEconomy.isEnabled()) {
            throw new UnsupportedOperationException("Invalid function");
        }
        var player = getSBPlayer().toPlayer();
        double cost = Double.parseDouble(getOptionValue());
        if (vaultEconomy.has(player, cost)) {
            vaultEconomy.withdrawPlayer(player, cost);
            getTempMap().put(KEY, cost + (getTempMap().has(KEY) ? getTempMap().getDouble(KEY) : 0.0D));
            return true;
        }
        sendMessage(SBConfig.ERROR_COST.replace(cost, cost - vaultEconomy.getBalance(player)));
        return false;
    }
}