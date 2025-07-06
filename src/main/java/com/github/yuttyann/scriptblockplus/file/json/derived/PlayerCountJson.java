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
import java.util.function.BiFunction;

/**
 * ScriptBlockPlus PlayerCountJson クラス
 * @author yuttyann44581
 */
@JsonTag(path = "json/playercount")
public final class PlayerCountJson extends TwoJson<ScriptKey, BlockCoords, PlayerCount> {

    /**
     * 要素のインスタンスの生成処理
     */
    private static final BiFunction<ScriptKey, BlockCoords, PlayerCount> INSTANCE = PlayerCount::new;

    /**
     * コンストラクタ
     * @param uuid - ファイルの名前({@link UUID}の文字列)
     */
    public PlayerCountJson(@NotNull String uuid) {
        super(uuid, INSTANCE);
    }

    /**
     * インスタンスを取得します。
     * @param uuid - ファイルの{@link UUID}
     * @return {@link PlayerCountJson} - インスタンス
     */
    @NotNull
    public static PlayerCountJson get(@NotNull UUID uuid) {
        return newJson(PlayerCountJson.class, uuid.toString());
    }

    /**
     * 指定した座標の設定を削除します。
     * @param scriptKey - スクリプトキー
     * @param blockCoords - 座標
     */
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