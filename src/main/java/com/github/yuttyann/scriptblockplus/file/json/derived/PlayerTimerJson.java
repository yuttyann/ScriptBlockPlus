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
import com.github.yuttyann.scriptblockplus.file.json.basic.ThreeJson;
import com.github.yuttyann.scriptblockplus.file.json.element.PlayerTimer;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.script.option.time.OldCooldown;
import com.github.yuttyann.scriptblockplus.utils.collection.ReuseIterator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * ScriptBlockPlus PlayerTimerJson クラス
 * @author yuttyann44581
 */
@JsonTag(path = "json/playertimer")
public final class PlayerTimerJson extends ThreeJson<UUID, ScriptKey, BlockCoords, PlayerTimer> {

    public static final CacheJson CACHE_JSON = new CacheJson(PlayerTimerJson.class, PlayerTimerJson::new);

    private PlayerTimerJson(@NotNull String name) {
        super(name);
    }

    @Override
    @NotNull
    protected PlayerTimer newInstance(@Nullable UUID uuid, @NotNull ScriptKey scriptKey, @NotNull BlockCoords blockCoords) {
        return new PlayerTimer(uuid, scriptKey, blockCoords);
    }

    @NotNull
    public static PlayerTimerJson get(@NotNull UUID uuid) {
        return newJson(uuid.toString(), CACHE_JSON);
    }

    public static void removeAll(@NotNull ScriptKey scriptKey, @NotNull BlockCoords blockCoords) {
        removeAll(scriptKey, new ReuseIterator<>(new BlockCoords[] { blockCoords }));
    }

    public static void removeAll(@NotNull ScriptKey scriptKey, @NotNull ReuseIterator<BlockCoords> reuseIterator) {
        var names = getNames(PlayerTimerJson.class);
        var oldUUID = OldCooldown.UUID_OLDCOOLDOWN.toString();
        for (int i = 0, l = names.size(), e = ".json".length(); i < l; i++) {
            var name = names.get(i);
            var index = name.length() - e;
            var timerJson = (PlayerTimerJson) getCache(name.substring(0, index), CACHE_JSON);
            if (timerJson.isEmpty()) {
                continue;
            }
            var uuid = (UUID) null;
            if (!oldUUID.equals(timerJson.getName())) {
                uuid = UUID.fromString(timerJson.getName());
            }
            System.out.println("UUID: " + (uuid == null ? "null" : uuid));
            var removed = false;
            reuseIterator.reset();
            while (reuseIterator.hasNext()) {
                var blockCoords = reuseIterator.next();
                if (timerJson.remove(uuid, scriptKey, blockCoords)) {
                    removed = true;
                }
            }
            if (removed) {
                timerJson.saveJson();
            }
        }
    }
}