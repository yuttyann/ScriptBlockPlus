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
package com.github.yuttyann.scriptblockplus.script.option.other;

import com.github.yuttyann.scriptblockplus.file.json.derived.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.file.json.derived.element.BlockScript;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;

/**
 * ScriptBlockPlus Amount オプションクラス
 * @author yuttyann44581
 */
@OptionTag(name = "amount", syntax = "@amount:", description = "<amount>")
public final class Amount extends BaseOption {

    @Override
    protected boolean isValid() throws Exception {
        var scriptJson = BlockScriptJson.get(getScriptKey());
        var blockCoords = getBlockCoords();
        var blockScript = scriptJson.load(blockCoords);

        var amount = blockScript.getSafeValue(BlockScript.AMOUNT).asInt(Integer.parseInt(getOptionValue())) - 1;
        if (amount > 0) {
            blockScript.setValue(BlockScript.AMOUNT, amount);
        } else {
            if (scriptJson.remove(blockCoords)) {
                scriptJson.init(blockCoords);
            }
        }
        scriptJson.saveJson();
        return true;
    }
}