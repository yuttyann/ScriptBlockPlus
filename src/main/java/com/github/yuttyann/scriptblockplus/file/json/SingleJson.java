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
package com.github.yuttyann.scriptblockplus.file.json;

import java.util.UUID;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus SingleJson クラス
 * @param <T> シリアライズ化を行う値の型
 * @author yuttyann44581
 */
public abstract class SingleJson<T> extends BaseJson<T> {

    /**
     * コンストラクタ
     * @param uuid - ファイルの名前
     */
    public SingleJson(@NotNull UUID uuid) {
        super(uuid);
    }

    /**
     * コンストラクタ
     * @param id - ファイルの名前
     */
    public SingleJson(@NotNull String id) {
        super(id);
    }

    /**
     * インスタンスを生成します。
     * @return {@link T} - インスタンス
     */
    @NotNull
    protected abstract T newInstance();

    /**
     * 要素を取得します。
     * @return {@link T} - 要素
     */
    @NotNull
    public final T load() {
        if (list.isEmpty()) {
            list.add(newInstance());
        }
        return list.get(0);
    }

    /**
     * 要素が存在するのか確認します。
     * @return {@link boolean} - 要素が存在する場合はtrue
     */
    public final boolean has() {
        return !list.isEmpty();
    }

    /**
     * 処理を行った後に要素を保存します。
     * @param action - 処理
     */
    public final void action(@NotNull Consumer<T> action) {
        action.accept(load());
        saveFile();
    }
}