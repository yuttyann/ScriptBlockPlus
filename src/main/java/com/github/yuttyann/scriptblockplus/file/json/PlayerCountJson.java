package com.github.yuttyann.scriptblockplus.file.json;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.file.Json;
import com.github.yuttyann.scriptblockplus.file.json.annotation.JsonOptions;
import com.github.yuttyann.scriptblockplus.file.json.element.PlayerCount;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
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
@JsonOptions(path = "json/playercount", file = "{id}.json", classes = { Location.class, ScriptType.class })
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
        return new PlayerCount(BlockCoords.getFullCoords((Location) args[0]), (ScriptType) args[1]);
    }

    public static void clear(@NotNull Location location, @NotNull ScriptType scriptType) {
        clear(Sets.newHashSet(location), scriptType);
    }

    public static void clear(@NotNull Set<Location> locations, @NotNull ScriptType scriptType) {
        Object[] args = { (Location) null, scriptType };
        for (String id : Json.getNameList(PlayerCountJson.class)) {
            Json<PlayerCount> json = new PlayerCountJson(UUID.fromString(id));
            if (!json.exists()) {
                continue;
            }
            boolean modifiable = false;
            for (Location location : locations) {
                args[0] = location;
                if (!json.has(args)) {
                    continue;
                }
                PlayerCount playerCount = json.load(args);
                if (playerCount.getAmount() > 0) {
                    modifiable = true;
                    json.remove(playerCount);
                }
            }
            if (modifiable) {
                json.saveFile();
            }
        }
    }
}