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
import com.github.yuttyann.scriptblockplus.file.json.BaseJson;
import com.github.yuttyann.scriptblockplus.file.json.MultiJson;
import com.github.yuttyann.scriptblockplus.file.json.annotation.JsonTag;
import com.github.yuttyann.scriptblockplus.file.json.element.PlayerCount;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.google.common.collect.Sets;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * ScriptBlockPlus PlayerCountJson クラス
 * @author yuttyann44581
 */
@JsonTag(path = "json/playercount")
public class PlayerCountJson extends MultiJson<PlayerCount> {

    public PlayerCountJson(@NotNull UUID uuid) {
        super(uuid);
    }

    public PlayerCountJson(@NotNull SBPlayer sbPlayer) {
        super(sbPlayer.getUniqueId());
    }

    public PlayerCountJson(@NotNull OfflinePlayer offlinePlayer) {
        super(offlinePlayer.getUniqueId());
    }

    @Override
    @NotNull
    protected PlayerCount newInstance(@NotNull Object... args) {
        return new PlayerCount((String) args[0], (ScriptKey) args[1]);
    }

    @NotNull
    public PlayerCount load(@NotNull Location location, @NotNull ScriptKey scriptKey) {
        return super.load(BlockCoords.getFullCoords(location), scriptKey);
    }

    public void action(@NotNull Consumer<PlayerCount> action, @NotNull Location location, @NotNull ScriptKey scriptKey) {
        super.action(action, BlockCoords.getFullCoords(location), scriptKey);
    }

    public static void clear(@NotNull Location location, @NotNull ScriptKey scriptKey) {
        clear(Sets.newHashSet(location), scriptKey);
    }

    public static void clear(@NotNull Set<Location> locations, @NotNull ScriptKey scriptKey) {
        var args = new Object[] { (String) null, scriptKey };
        for (String id : BaseJson.getNames(PlayerCountJson.class)) {
            var countJson = new PlayerCountJson(UUID.fromString(id));
            if (!countJson.exists()) {
                continue;
            }
            boolean modifiable = false;
            for (var location : locations) {
                args[0] = BlockCoords.getFullCoords(location);
                if (!countJson.has(args)) {
                    continue;
                }
                var playerCount = countJson.load(args);
                if (playerCount.getAmount() > 0) {
                    modifiable = true;
                    countJson.remove(playerCount);
                }
            }
            if (modifiable) {
                countJson.saveFile();
            }
        }
    }
}