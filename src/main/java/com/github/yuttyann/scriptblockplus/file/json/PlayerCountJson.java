package com.github.yuttyann.scriptblockplus.file.json;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.file.json.element.PlayerCount;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

/**
 * ScriptBlockPlus PlayerCountJson クラス
 * @author yuttyann44581
 */
@JsonDirectory(path = "json/playercount", file = "{id}.json")
public class PlayerCountJson extends Json<PlayerCount> {

    public PlayerCountJson(@NotNull UUID uuid) {
        super(uuid);
    }

    public static void clearCounts(@NotNull Location location, @NotNull ScriptType scriptType) {
        for (String id : Json.getIdList(PlayerCountJson.class)) {
            Json<PlayerCount> json = new PlayerCountJson(UUID.fromString(id));
            StreamUtils.filter(json.load(location, scriptType), p -> p.getAmount() > 0, json::remove);
        }
    }

    @Override
    protected int hashCode(@NotNull Object... args) {
        return Objects.hash(BlockCoords.getFullCoords((Location) args[0]), args[1]);
    }

    @Override
    @NotNull
    public PlayerCount newInstance(@NotNull Object... args) {
        return new PlayerCount(BlockCoords.getFullCoords((Location) args[0]), (ScriptType) args[1]);
    }
}