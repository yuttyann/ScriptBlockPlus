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
import com.github.yuttyann.scriptblockplus.file.json.basic.ThreeJson;
import com.github.yuttyann.scriptblockplus.file.json.derived.element.PlayerTimer;
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

    private PlayerTimerJson(@NotNull String name) {
        super(name);
    }

    @Override
    @NotNull
    protected PlayerTimer newInstance(@Nullable UUID uuid, @NotNull ScriptKey scriptKey, @NotNull BlockCoords blockCoords) {
        return new PlayerTimer(uuid, scriptKey, blockCoords);
    }

    @NotNull
    public static PlayerTimerJson newJson(@NotNull UUID uuid) {
        return newJson(PlayerTimerJson.class, uuid.toString());
    }

    public static void removeAll(@NotNull ScriptKey scriptKey, @NotNull BlockCoords... blockCoords) {
        var names = getNames(PlayerTimerJson.class);
        var oldUUID = OldCooldown.UUID_OLDCOOLDOWN.toString();
        var reuseIterator = new ReuseIterator<>(blockCoords);
        for (int i = 0, l = names.size(), e = ".json".length(); i < l; i++) {
            var name = names.get(i);
            var index = name.length() - e;
            var timerJson = getCache(PlayerTimerJson.class, name.substring(0, index));
            if (timerJson.isEmpty()) {
                continue;
            }
            var uuid = (UUID) null;
            if (!oldUUID.equals(timerJson.getName())) {
                uuid = UUID.fromString(timerJson.getName());
            }
            var removed = false;
            reuseIterator.reset();
            while (reuseIterator.hasNext()) {
                var next = reuseIterator.next();
                if (timerJson.remove(uuid, scriptKey, next)) {
                    removed = true;
                }
            }
            if (removed) {
                timerJson.saveJson();
            }
        }
    }
}