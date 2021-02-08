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
package com.github.yuttyann.scriptblockplus.file.json.multi;

import java.io.File;
import java.util.function.Consumer;

import com.github.yuttyann.scriptblockplus.file.json.BaseElement;
import com.github.yuttyann.scriptblockplus.file.json.BaseJson;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus OneJson クラス
 * @param <A> 引数の型
 * @param <E> エレメントの型({@link OneElement}を継承してください。)
 * @author yuttyann44581
 */
public abstract class OneJson<A, E extends OneJson.OneElement<A>> extends BaseJson<E> {

    /**
     * ScriptBlockPlus TwoElement クラス
     * @param <A> 引数の型
     * @author yuttyann44581
     */
    public static abstract class OneElement<A> extends BaseElement {

        /**
         * 引数が一致するのか比較します。
         * @param a - 引数
         * @return {@link boolean} - 要素が存在する場合は{@code true}
         */
        public abstract boolean isElement(@NotNull A a);
    }

    /**
     * コンストラクタ
     * <p>
     * 必ずシリアライズ、デシリアライズ化が可能なファイルを指定してください。
     * @param json - JSONのファイル
     */
    protected OneJson(@NotNull File json) {
        super(json);
    }

    /**
     * コンストラクタ
     * @param name - ファイルの名前
     */
    protected OneJson(@NotNull String name) {
        super(name);
    }

    /**
     * インスタンスを生成します。
     * @param a - 引数
     * @return {@link E} - インスタンス
     */
    @NotNull
    protected abstract E newInstance(@NotNull A a);

    /**
     * 要素を取得します。
     * @param a - 引数
     * @return {@link E} - 要素
     */
    @NotNull
    public final E load(@NotNull A a) {
        E element = fastLoad(a);
        if (element == null) {
            list.add(element = newInstance(a));
        }
        return element;
    }

    /**
     * 要素を取得します。
     * <p>
     * 要素が存在しなかった場合は、{@code null}を返します。
     * @param a - 引数
     * @return {@link E} - 要素
     */
    @Nullable
    public final E fastLoad(@NotNull A a) {
        for (int i = 0, l = list.size(); i < l; i++) {
            E element = list.get(i);
            if (element.isElement(a)) {
                return element;
            }
        }
        return null;
    }

    /**
     * 要素が存在するのか確認します。
     * @param a - 引数
     * @return {@link boolean} - 要素が存在する場合は{@code true}
     */
    public final boolean has(@NotNull A a) {
        return fastLoad(a) != null;
    }

    /**
     * 要素を削除します。
     * @param a - 引数
     * @return {@link boolean} - 削除に成功した場合は{@code true}
     */
    public final boolean remove(@NotNull A a) {
        for (int i = 0, l = list.size(); i < l; i++) {
            E element = list.get(i);
            if (element.isElement(a)) {
                list.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * 処理を行った後に要素を保存します。
     * @param action - 処理
     * @param a - 引数
     */
    public final void action(@NotNull Consumer<E> action, @NotNull A a) {
        action.accept(load(a));
        saveFile();
    }
}