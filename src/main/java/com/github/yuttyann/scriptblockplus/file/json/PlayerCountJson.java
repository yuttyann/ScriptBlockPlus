package com.github.yuttyann.scriptblockplus.file.json;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.file.Json;
import com.github.yuttyann.scriptblockplus.file.json.annotation.JsonOptions;
import com.github.yuttyann.scriptblockplus.file.json.element.PlayerCount;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.google.common.collect.Sets;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * ScriptBlockPlus PlayerCountJson クラス
 * @author yuttyann44581
 */
@JsonOptions(path = "json/playercount", file = "{id}.json", classes = { Location.class, ScriptKey.class })
public class PlayerCountJson extends Json<PlayerCount> {

    public PlayerCountJson(@NotNull UUID uuid) {
        super(uuid);
    }

    @Override
    protected int hashCode(@NotNull Object[] args) {
        return Objects.hash(BlockCoords.getFullCoords((Location) args[0]), args[1]);
    }

    @Override
    @NotNull
    public PlayerCount newInstance(@NotNull Object[] args) {
        return new PlayerCount(BlockCoords.getFullCoords((Location) args[0]), (ScriptKey) args[1]);
    }

    public static void clear(@NotNull Location location, @NotNull ScriptKey scriptKey) {
        clear(Sets.newHashSet(location), scriptKey);
    }

    public static void clear(@NotNull Set<Location> locations, @NotNull ScriptKey scriptKey) {
        var args = new Object[] { (Location) null, scriptKey };
        for (String id : Json.getNames(PlayerCountJson.class)) {
            var countJson = new PlayerCountJson(UUID.fromString(id));
            if (!countJson.exists()) {
                continue;
            }
            boolean modifiable = false;
            for (var location : locations) {
                args[0] = location;
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