package com.github.yuttyann.scriptblockplus.file.json;

import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * ScriptBlockPlus PlayerTemp クラス
 * @author yuttyann44581
 */
public class PlayerTemp extends Json<PlayerTempInfo> {

    public PlayerTemp(@NotNull UUID uuid) {
        super(uuid);
    }

    @NotNull
    public PlayerTempInfo getInfo() {
        int hash = uuid.hashCode();
        PlayerTempInfo info = StreamUtils.fOrElse(list, p -> p.hashCode() == hash, null);
        if (info == null) {
            list.add(info = new PlayerTempInfo(uuid));
        }
        return info;
    }
}