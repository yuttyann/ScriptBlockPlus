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
import com.github.yuttyann.scriptblockplus.file.json.CacheJson;
import com.github.yuttyann.scriptblockplus.file.json.annotation.JsonTag;
import com.github.yuttyann.scriptblockplus.file.json.basic.TwoJson;
import com.github.yuttyann.scriptblockplus.file.json.element.PlayerCount;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.utils.collection.ReuseIterator;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * ScriptBlockPlus PlayerCountJson クラス
 * @author yuttyann44581
 */
@JsonTag(path = "json/playercount")
public final class PlayerCountJson extends TwoJson<ScriptKey, BlockCoords, PlayerCount> {

    public static final CacheJson CACHE_JSON = new CacheJson(PlayerCountJson.class, PlayerCountJson::new);

    private PlayerCountJson(@NotNull String name) {
        super(name.toString());
    }

    @Override
    @NotNull
    protected PlayerCount newInstance(@NotNull ScriptKey scriptKey, @NotNull BlockCoords location) {
        return new PlayerCount(scriptKey, location);
    }

    @NotNull
    public static PlayerCountJson get(@NotNull UUID uuid) {
        return newJson(uuid.toString(), CACHE_JSON);
    }

    @NotNull
    public static PlayerCountJson get(@NotNull SBPlayer sbPlayer) {
        return get(sbPlayer.getUniqueId());
    }

    @NotNull
    public static PlayerCountJson get(@NotNull OfflinePlayer player) {
        return get(player.getUniqueId());
    }

    public static void removeAll(@NotNull ScriptKey scriptKey, @NotNull BlockCoords blockCoords) {
        removeAll(scriptKey, new ReuseIterator<>(new BlockCoords[] { blockCoords }));
    }

    public static void removeAll(@NotNull ScriptKey scriptKey, @NotNull ReuseIterator<BlockCoords> reuseIterator) {
        var names = getNames(PlayerCountJson.class);
        for (int i = 0, l = names.size(), e = ".json".length(); i < l; i++) {
            var name = names.get(i);
            var index = name.length() - e;
            var countJson = (PlayerCountJson) getCache(name.substring(0, index), CACHE_JSON);
            if (countJson.isEmpty()) {
                continue;
            }
            var removed = false;
            reuseIterator.reset();
            while (reuseIterator.hasNext()) {
                if (countJson.remove(scriptKey, reuseIterator.next())) {
                    removed = true;
                }
            }
            if (removed) {
                countJson.saveJson();
            }
        }
    }
}