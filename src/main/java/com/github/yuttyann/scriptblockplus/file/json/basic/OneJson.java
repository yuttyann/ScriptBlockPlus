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

import java.util.Objects;
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
         * 引数{@link A}を取得します。
         * <p>
         * コンストラクタ宣言時に渡す引数と同じでなければなりません。
         * @return {@link A} - 引数
         */
        @Nullable
        protected abstract A getA();

        /**
         * 引数が一致するのか比較します。
         * @param a - 引数
         * @return {@code boolean} - 要素が存在する場合は{@code true}
         */
        public boolean isElement(@Nullable A a) {
            return compare(getA(), a);
        }

        /**
         * ハッシュコードを生成します。
         * @return {@code int} - ハッシュコード
         */
        @Override
        public final int hashCode() {
            return OneJson.hash(getA());
        }

        @Override
        @NotNull
        public final Class<? extends BaseElement> getElementType() {
            return OneElement.class;
        }
    }

    /**
     * コンストラクタ
     * @apiNote
     * <pre>
     * 実装例です。
     * // キャッシュ(CacheJson)を利用する場合は、
     * // コンストラクタの引数を『 String name 』のみにしてください。
     * // また、上記の方法で実装する場合は、修飾子を『 private 』にすることを推奨します。
     * private xxxJson(&#064;NotNull String name) {
     *     super(name);
     * }
     * </pre>
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
    protected abstract E newInstance(@Nullable A a);

    /**
     * 要素を取得します。
     * @param a - 引数
     * @return {@link E} - 要素
     */
    @NotNull
    public final E load(@Nullable A a) {
        int hash = hash(a);
        var element = getElementMap().get(hash);
        if (element == null) {
            getElementMap().put(hash, element = newInstance(a));
        } else if (!element.isElement(a)) {
            var subHash = Integer.valueOf(hash);
            if ((element = subGet(subHash, e -> e.isElement(a))) == null) {
                subPut(subHash, element = newInstance(a));
            }
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
    public final E fastLoad(@Nullable A a) {
        int hash = hash(a);
        var element = getElementMap().get(hash);
        if (element == null) {
            return null;
        } else if (!element.isElement(a) && isSubNotEmpty()) {
            element = subGet(hash, e -> e.isElement(a));
        }
        return element;
    }

    /**
     * 要素が存在するのか確認します。
     * @param a - 引数
     * @return {@code boolean} - 要素が存在する場合は{@code true}
     */
    public final boolean has(@Nullable A a) {
        return fastLoad(a) != null;
    }

    /**
     * 一致する要素を削除します。
     * @param a - 引数
     * @return {@code boolean} - 削除に成功した場合は{@code true}
     */
    public final boolean remove(@Nullable A a) {
        int hash = hash(a);
        var element = getElementMap().get(hash);
        if (element == null) {
            return false;
        }
        if (element.isElement(a)) {
            getElementMap().remove(hash);
            if (isSubNotEmpty()) {
                subMapFirstShift(hash);
            }
        } else if (isSubNotEmpty()) {
            return subRemove(hash, e -> e.isElement(a));
        }
        return true;
    }

    /**
     * 処理を行った後に要素を保存します。
     * @param action - 処理
     * @param a - 引数
     */
    public final void action(@NotNull Consumer<E> action, @Nullable A a) {
        action.accept(load(a));
        saveJson();
    }

    private static int hash(@Nullable Object a) {
        return Objects.hashCode(a);
    }
}