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
 * ScriptBlockPlus TwoJson クラス
 * @param <A> 引数1の型
 * @param <B> 引数2の型
 * @param <E> エレメントの型({@link TwoElement}を継承してください。)
 * @author yuttyann44581
 */
public abstract class TwoJson<A, B, E extends TwoJson.TwoElement<A, B>> extends BaseJson<E> {

    /**
     * ScriptBlockPlus TwoElement クラス
     * @param <A> 引数1の型
     * @param <B> 引数2の型
     * @author yuttyann44581
     */
    public static abstract class TwoElement<A, B> extends BaseElement {

        /**
         * 引数{@link A}を取得します。
         * <p>
         * コンストラクタ宣言時に渡す引数と同じでなければなりません。
         * @return {@link A} - 引数1
         */
        @NotNull
        protected abstract A getA();

        /**
         * 引数{@link B}を取得します。
         * <p>
         * コンストラクタ宣言時に渡す引数と同じでなければなりません。
         * @return {@link B} - 引数2
         */
        @NotNull
        protected abstract B getB();

        /**
         * 引数が一致するのか比較します。
         * @param a - 引数1
         * @param b - 引数2
         * @return {@link boolean} - 要素が存在する場合は{@code true}
         */
        public boolean isElement(@NotNull A a, @NotNull B b) {
            return compare(getA(), a) && compare(getB(), b);
        }

        /**
         * ハッシュコードを生成します。
         * @return {@link int} - ハッシュコード
         */
        @Override
        public final int hashCode() {
            return TwoJson.hash(getA(), getB());
        }

        @Override
        @NotNull
        public final Class<? extends BaseElement> getElementType() {
            return TwoElement.class;
        }
    }

    /**
     * コンストラクタ
     * @param name - ファイルの名前
     */
    protected TwoJson(@NotNull String name) {
        super(name);
    }

    /**
     * インスタンスを生成します。
     * @param a - 引数1
     * @param b - 引数2
     * @return {@link E} - インスタンス
     */
    @NotNull
    protected abstract E newInstance(@NotNull A a, @NotNull B b);

    /**
     * 要素を取得します。
     * @param a - 引数1
     * @param b - 引数2
     * @return {@link E} - 要素
     */
    @NotNull
    public final E load(@NotNull A a, @NotNull B b) {
        int hash = hash(a, b);
        var element = getElementMap().get(hash);
        if (element == null) {
            getElementMap().put(hash, element = newInstance(a, b));
        } else if (!element.isElement(a, b)) {
            var objectHash = Integer.valueOf(hash);
            if ((element = subGet(objectHash, e -> e.isElement(a, b))) == null) {
                subPut(objectHash, element = newInstance(a, b));
            }
        }
        return element;
    }

    /**
     * 要素を取得します。
     * <p>
     * 要素が存在しなかった場合は、{@code null}を返します。
     * @param a - 引数1
     * @param b - 引数2
     * @return {@link E} - 要素
     */
    @Nullable
    public final E fastLoad(@NotNull A a, @NotNull B b) {
        int hash = hash(a, b);
        var element = getElementMap().get(hash);
        if (element == null) {
            return null;
        } else if (!element.isElement(a, b) && isSubNotEmpty()) {
            element = subGet(hash, e -> e.isElement(a, b));
        }
        return element;
    }

    /**
     * 要素が存在するのか確認します。
     * @param a - 引数1
     * @param b - 引数2
     * @return {@link boolean} - 要素が存在する場合は{@code true}
     */
    public final boolean has(@NotNull A a, @NotNull B b) {
        return fastLoad(a, b) != null;
    }

    /**
     * 一致する要素を削除します。
     * @param a - 引数1
     * @param b - 引数2
     * @return {@link boolean} - 削除に成功した場合は{@code true}
     */
    public final boolean remove(@NotNull A a, @NotNull B b) {
        int hash = hash(a, b);
        var element = getElementMap().get(hash);
        if (element == null) {
            return false;
        }
        if (element.isElement(a, b)) {
            getElementMap().remove(hash);
            if (isSubNotEmpty()) {
                subMapFirstShift(hash);
            }
        } else if (isSubNotEmpty()) {
            return subRemove(hash, e -> e.isElement(a, b));
        }
        return true;
    }

    /**
     * 処理を行った後に要素を保存します。
     * @param action - 処理
     * @param a - 引数1
     * @param b - 引数2
     */
    public final void action(@NotNull Consumer<E> action, @NotNull A a, @NotNull B b) {
        action.accept(load(a, b));
        saveJson();
    }

    private static int hash(@NotNull Object a, @NotNull Object b) {
        int hash = 1;
        int prime = 31;
        hash = prime * hash + Objects.hashCode(a);
        hash = prime * hash + Objects.hashCode(b);
        return hash;
    }
}