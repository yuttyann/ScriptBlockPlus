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

import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus MultiJson クラス
 * @param <T> シリアライズ化を行う値の型
 * @author yuttyann44581
 */
public abstract class MultiJson<T> extends BaseJson<T> {

    /**
     * コンストラクタ
     * @param uuid - ファイルの名前
     */
    public MultiJson(@NotNull UUID uuid) {
        super(uuid);
    }

    /**
     * コンストラクタ
     * @param name - ファイルの名前
     */
    public MultiJson(@NotNull String name) {
        super(name);
    }

    /**
     * インスタンスを生成します。
     * <p>
     * {@link MultiJson#load(Object[] args)}へ渡した値がそのまま渡されます。
     * @apiNote
     * <pre>
     * キャストを行った引数を、コンストラクタに渡してください。
     * &#064;Override
     * protected Example newInstance(Object... args) {
     *     return new Example((Test1) args[0], (Test2) args[1]);
     * }
     * </pre>
     * @param args - 引数
     * @return {@link T} - インスタンス
     */
    @NotNull
    protected abstract T newInstance(@NotNull Object... args);

    /**
     * 要素を取得します。
     * @apiNote
     * <pre>
     * 継承を行わずに、明確な引数を実装したメソッドを作成してください。
     * public Example load(Test1 test1, Test2 test2) {
     *     return super.load(test1, test2);
     * }
     * </pre>
     * @param args - 引数
     * @return {@link T} - 要素
     */
    @NotNull
    protected final T load(@NotNull Object... args) {
        int hash = Objects.hash(args);
        for (T t : list) {
            if (t.hashCode() == hash) {
                return t;
            }
        }
        T instance = newInstance(args);
        list.add(instance);
        return instance;
    }

    /**
     * 要素が存在するのか確認します。
     * @apiNote
     * <pre>
     * 継承を行わずに、明確な引数を実装したメソッドを作成してください。
     * public boolean has(Test1 test1, Test2 test2) {
     *     return super.has(test1, test2);
     * }
     * </pre>
     * @param args - 引数
     * @return {@link boolean} - 要素が存在する場合はtrue
     */
    protected final boolean has(@NotNull Object... args) {
        int hash = Objects.hash(args);
        for (T t : list) {
            if (t.hashCode() == hash) {
                return true;
            }
        }
        return false;
    }

    /**
     * 処理を行った後に要素を保存します。
     * @apiNote
     * <pre>
     * 継承を行わずに、明確な引数を実装したメソッドを作成してください。
     * public boolean action(Consumer&lt;Example&gt; action, Test1 test1, Test2 test2) {
     *     super.action(action, test1, test2);
     * }
     * </pre>
     * @param action - 処理
     * @param args - 引数
     */
    protected final void action(@NotNull Consumer<T> action, @NotNull Object... args) {
        action.accept(load(args));
        saveFile();
    }
}