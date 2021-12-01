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

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.file.json.annotation.JsonTag;
import com.github.yuttyann.scriptblockplus.file.json.basic.TwoJson;
import com.github.yuttyann.scriptblockplus.file.json.derived.element.PlayerCount;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.utils.collection.ReuseIterator;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * ScriptBlockPlus PlayerCountJson クラス
 * @author yuttyann44581
 */
@JsonTag(path = "json/playercount")
public final class PlayerCountJson extends TwoJson<ScriptKey, BlockCoords, PlayerCount> {

    public PlayerCountJson(@NotNull String name) {
        super(name);
    }

    @Override
    @NotNull
    protected PlayerCount newInstance(@NotNull ScriptKey scriptKey, @NotNull BlockCoords location) {
        return new PlayerCount(scriptKey, location);
    }

    @NotNull
    public static PlayerCountJson newJson(@NotNull UUID uuid) {
        return newJson(PlayerCountJson.class, uuid.toString());
    }

    public static void removeAll(@NotNull ScriptKey scriptKey, @NotNull BlockCoords... blockCoords) {
        var names = getNames(PlayerCountJson.class);
        var iterator = new ReuseIterator<>(blockCoords);
        for (int i = 0, l = names.size(), e = ".json".length(); i < l; i++) {
            var name = names.get(i);
            var index = name.length() - e;
            var countJson = newJson(PlayerCountJson.class, name.substring(0, index));
            if (countJson.isEmpty()) {
                continue;
            }
            var removed = false;
            iterator.reset();
            while (iterator.hasNext()) {
                if (countJson.remove(scriptKey, iterator.next())) {
                    removed = true;
                }
            }
            if (removed) {
                countJson.saveJson();
            }
        }
    }
}