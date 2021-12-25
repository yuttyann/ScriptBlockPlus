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
package com.github.yuttyann.scriptblockplus.script.endprocess;

import com.github.yuttyann.scriptblockplus.file.json.derived.PlayerCountJson;
import com.github.yuttyann.scriptblockplus.file.json.derived.element.PlayerCount;
import com.github.yuttyann.scriptblockplus.script.SBRead;

import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus EndPlayerCount エンドプロセスクラス
 * @author yuttyann44581
 */
public class EndPlayerCount implements EndProcess {

    @Override
    public void success(@NotNull SBRead sbRead) {
        PlayerCountJson.get(sbRead.getSBPlayer().getUniqueId()).action(PlayerCount::add, sbRead.getScriptKey(), sbRead.getBlockCoords());
    }

    @Override
    public void failed(@NotNull SBRead sbRead) { }
}