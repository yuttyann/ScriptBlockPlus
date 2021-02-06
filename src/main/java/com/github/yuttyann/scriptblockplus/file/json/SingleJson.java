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

import java.io.File;
import java.util.List;
import java.util.function.Consumer;

import com.github.yuttyann.scriptblockplus.utils.collection.SingleList;

import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus SingleJson クラス
 * @param <E> エレメントの型
 * @author yuttyann44581
 */
public abstract class SingleJson<E extends BaseElement> extends BaseJson<E> {

    /**
     * コンストラクタ
     * <p>
     * 必ずシリアライズ、デシリアライズ化が可能なファイルを指定してください。
     * @param json - JSONのファイル
     */
    protected SingleJson(@NotNull File json) {
        super(json);
    }

    /**
     * コンストラクタ
     * @param name - ファイルの名前
     */
    protected SingleJson(@NotNull String name) {
        super(name);
    }

    /**
     * {@link SingleList}&lt;{@link E}&gt;を生成します。
     * @return {@link SingleList}&lt;{@link E}&gt; - リスト
     */
    @Override
    @NotNull
    protected final List<E> createList() {
        return new SingleList<>();
    }

    /**
     * インスタンスを生成します。
     * @return {@link E} - インスタンス
     */
    @NotNull
    protected abstract E newInstance();

    /**
     * 要素を取得します。
     * @return {@link E} - 要素
     */
    @NotNull
    public final E load() {
        if (list.isEmpty()) {
            list.add(newInstance());
        }
        return list.get(0);
    }

    /**
     * 要素が存在するのか確認します。
     * @return {@link boolean} - 要素が存在する場合は{@code true}
     */
    public final boolean has() {
        return !list.isEmpty();
    }

    /**
     * 要素を削除します。
     */
    public final void remove() {
        list.clear();
    }

    /**
     * 処理を行った後に要素を保存します。
     * @param action - 処理
     */
    public final void action(@NotNull Consumer<E> action) {
        action.accept(load());
        saveFile();
    }
}