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
package com.github.yuttyann.scriptblockplus.file.json.basic;

import java.util.function.Consumer;

import com.github.yuttyann.scriptblockplus.file.json.BaseElement;
import com.github.yuttyann.scriptblockplus.file.json.BaseJson;
import com.github.yuttyann.scriptblockplus.utils.collection.IntMap;
import com.github.yuttyann.scriptblockplus.utils.collection.IntSingleMap;

import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus SingleJson クラス
 * @param <E> エレメントの型({@link SingleElement}を継承してください。)
 * @author yuttyann44581
 */
public abstract class SingleJson<E extends SingleJson.SingleElement> extends BaseJson<E> {

    /**
     * ScriptBlockPlus SingleElement クラス
     * @author yuttyann44581
     */
    public static abstract class SingleElement extends BaseElement {

        @Override
        @NotNull
        public final Class<? extends BaseElement> getElementType() {
            return SingleElement.class;
        }

        @Override
        public final int hashCode() {
            return 0;
        }
    }

    /**
     * コンストラクタ
     * @param name - ファイルの名前
     * @apiNote
     * <pre>
     * キャッシュ(CacheJson)を利用する場合は、以下の様なコンストラクタを実装してください。
     * private ...Json(&#064;NotNull String name) {
     *     super(name);
     *     etc...
     * }
     * </pre>
     */
    protected SingleJson(@NotNull String name) {
        super(name);
    }

    /**
     * {@link IntMap}&lt;{@link E}&gt;を生成します。
     * @return {@link IntMap}&lt;{@link E}&gt; - マップ
     */
    @Override
    @NotNull
    protected final IntMap<E> createMap() {
        return new IntSingleMap<>();
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
    public E load() {
        var elementMap = getElementMap();
        if (elementMap.isEmpty()) {
            elementMap.put(0, newInstance());
        }
        return elementMap.get(0);
    }

    /**
     * 要素が存在するのか確認します。
     * @return {@code boolean} - 要素が存在する場合は{@code true}
     */
    public boolean has() {
        return !getElementMap().isEmpty();
    }

    /**
     * 要素を削除します。
     */
    public void remove() {
        getElementMap().clear();
    }

    /**
     * 処理を行った後に要素を保存します。
     * @param action - 処理
     */
    public void action(@NotNull Consumer<E> action) {
        action.accept(load());
        saveJson();
    }
}