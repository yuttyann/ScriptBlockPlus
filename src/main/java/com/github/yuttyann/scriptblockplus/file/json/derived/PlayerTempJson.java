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
package com.github.yuttyann.scriptblockplus.file.json.derived;

import com.github.yuttyann.scriptblockplus.file.json.SingleJson;
import com.github.yuttyann.scriptblockplus.file.json.annotation.JsonTag;
import com.github.yuttyann.scriptblockplus.file.json.element.PlayerTemp;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * ScriptBlockPlus PlayerTempJson クラス
 * @author yuttyann44581
 */
@JsonTag(path = "json/playertemp")
public class PlayerTempJson extends SingleJson<PlayerTemp> {

    public PlayerTempJson(@NotNull UUID uuid) {
        super(uuid);
    }

    @Override
    @NotNull
    protected PlayerTemp newInstance() {
        return new PlayerTemp();
    }
}