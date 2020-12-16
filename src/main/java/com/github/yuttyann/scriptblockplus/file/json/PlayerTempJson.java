package com.github.yuttyann.scriptblockplus.file.json;

import com.github.yuttyann.scriptblockplus.file.json.element.PlayerTemp;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * ScriptBlockPlus PlayerTempJson クラス
 * @author yuttyann44581
 */
@JsonDirectory(path = "json/playertemp", file = "{id}.json")
public class PlayerTempJson extends Json<PlayerTemp> {

    public PlayerTempJson(@NotNull UUID uuid) {
        super(uuid);
    }

    @Override
    @NotNull
    public PlayerTemp newInstance(@NotNull Object... args) {
        return new PlayerTemp();
    }
}