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
package com.github.yuttyann.scriptblockplus.file.json;

import com.github.yuttyann.scriptblockplus.file.Json;
import com.github.yuttyann.scriptblockplus.file.json.annotation.JsonOptions;
import com.github.yuttyann.scriptblockplus.file.json.element.PlayerTemp;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * ScriptBlockPlus PlayerTempJson クラス
 * @author yuttyann44581
 */
@JsonOptions(path = "json/playertemp", file = "{id}.json")
public class PlayerTempJson extends Json<PlayerTemp> {

    public PlayerTempJson(@NotNull UUID uuid) {
        super(uuid);
    }

    @Override
    @NotNull
    public PlayerTemp newInstance(@NotNull Object[] args) {
        return new PlayerTemp();
    }
}