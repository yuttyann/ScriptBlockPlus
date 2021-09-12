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
package com.github.yuttyann.scriptblockplus.selector;

import com.github.yuttyann.scriptblockplus.enums.splittype.Argument;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus Split クラス
 * @author yuttyann44581
 */
public final class Split {

    private final String start, end, name, arguments;

    /**
     * コンストラクタ
     * <p>
     * {@code @a[limit=1]}のような文字列を、{@code @a}と{@code limit=1}の様に分割することができます。
     * @param source - 分割したい文字列
     * @param name - 名前(上記の例で表すと、{@code @a}に相当する文字列です。)
     * @param start - 括弧の始め(上記の例で表すと、{@code [}に相当する文字列です。)
     * @param end - 括弧の終わり(上記の例で表すと、{@code ]}に相当する文字列です。)
     */
    public Split(@NotNull String source, @NotNull String name, @NotNull String start, @NotNull String end) {
        this(source, name, 0, start, end, 0);
    }

    /**
     * コンストラクタ
     * <p>
     * {@code @a[limit=1]}のような文字列を、{@code @a}と{@code limit=1}の様に分割することができます。
     * @param source - 分割したい文字列
     * @param name - 名前(上記の例で表すと、{@code @a}に相当する文字列です。)
     * @param allowable - {@code name}を{@code substring}する場合の許容値です。
     * @param start - 括弧の始め(上記の例で表すと、{@code [}に相当する文字列です。)
     * @param end - 括弧の終わり(上記の例で表すと、{@code ]}に相当する文字列です。)
     */
    public Split(@NotNull String source, @NotNull String name, int allowable, @NotNull String start, @NotNull String end) {
        this(source, name, allowable, start, end, 0);
    }

    /**
     * コンストラクタ
     * <p>
     * {@code @a[limit=1]}のような文字列を、{@code @a}と{@code limit=1}の様に分割することができます。
     * @param source - 分割したい文字列
     * @param name - 名前(上記の例で表すと、{@code @a}に相当する文字列です。)
     * @param start - 括弧の始め(上記の例で表すと、{@code [}に相当する文字列です。)
     * @param end - 括弧の終わり(上記の例で表すと、{@code ]}に相当する文字列です。)
     * @param fromIndex - 検索の開始位置
     */
    public Split(@NotNull String source, @NotNull String name, @NotNull String start, @NotNull String end, int fromIndex) {
        this(source, name, 0, start, end, fromIndex);
    }

    /**
     * コンストラクタ
     * <p>
     * {@code @a[limit=1]}のような文字列を、{@code @a}と{@code limit=1}の様に分割することができます。
     * @param source - 分割したい文字列
     * @param name - 名前(上記の例で表すと、{@code @a}に相当する文字列です。)
     * @param allowable - {@code name}を{@code substring}する場合の許容値です。
     * @param start - 括弧の始め(上記の例で表すと、{@code [}に相当する文字列です。)
     * @param end - 括弧の終わり(上記の例で表すと、{@code ]}に相当する文字列です。)
     * @param fromIndex - 検索の開始位置
     */
    public Split(@NotNull String source, @NotNull String name, int allowable, @NotNull String start, @NotNull String end, int fromIndex) {
        int nameIndex = source.indexOf(name, fromIndex), startIndex = source.indexOf(start, nameIndex + 1), endIndex = source.indexOf(end, startIndex + 1);
        if (nameIndex > -1 && startIndex > -1 && endIndex > -1) {
            this.name = source.substring(nameIndex, startIndex).trim();
            this.arguments = source.substring(startIndex + 1, endIndex).trim();
        } else if (nameIndex > -1) {
            this.name = source.substring(nameIndex, nameIndex + name.length() + allowable);
            this.arguments = null;
        } else {
            this.name = arguments = null;
        }
        this.start = start;
        this.end = end;
    }

    /**
     * 名前を取得します。
     * @return {@link String} - 名前
     */
    @Nullable
    public String getName() {
        return name;
    }

    /**
     * 引数を取得します。
     * @return {@link String} - 引数
     */
    @Nullable
    public String getArguments() {
        return arguments;
    }

    /**
     * {@link SplitValue}の配列を生成します。
     * @param types - 要素の配列(例: {@link Argument}[]等)
     * @return {@link SplitValue}[] - 値の配列
     */
    @NotNull
    public SplitValue[] getValues(@NotNull SplitType[] types) {
        if (StringUtils.isEmpty(arguments)) {
            return new SplitValue[0];
        }
        var split = StringUtils.split(arguments, ',');
        return StreamUtils.toArray(split, s -> new SplitValue(s, types), SplitValue[]::new);
    }

    @Override
    @NotNull
    public String toString() {
        return name == null ? "" : StringUtils.isEmpty(arguments) ? name : name + start + arguments + end;
    }

    /**
     * 文字数を取得します。
     * @return {@code int} - 文字数
     */
    public int length() {
        return toString().length();
    }
}