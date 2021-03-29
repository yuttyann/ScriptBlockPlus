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

import com.github.yuttyann.scriptblockplus.enums.splittype.Argment;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus Split クラス
 * @author yuttyann44581
 */
public class Split {

    private final String start, end, name, argments;

    /**
     * コンストラクタ
     * <p>
     * {@code @a[limit=1]}のような文字列を、{@code @a}と{@code limit=1}の様に分割することができます。
     * @param source - 分割したい文字列
     * @param name - 名前(上記の例で表すと、{@code @a}に相当する文字列です。)
     * @param start - 括弧の始め(上記の例で表すと、{@code [}に相当する文字列です。)
     * @param end - 括弧の終わり(上記の例で表すと、{@code ]}に相当する文字列です。)
     */
    public Split(@NotNull String source, @NotNull String tag, @NotNull String start, @NotNull String end) {
        this(source, tag, start, end, 0);
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
        int nameIndex = source.indexOf(name, fromIndex), startIndex = source.indexOf(start, nameIndex + 1), endIndex = source.indexOf(end, startIndex + 1);
        if (nameIndex != 1 && startIndex != -1 && endIndex != -1) {
            this.name = source.substring(nameIndex, startIndex).trim();
            this.argments = source.substring(startIndex + 1, endIndex).trim();
        } else {
            this.name = source;
            this.argments = null;
        }
        this.start = start;
        this.end = end;
    }

    /**
     * 名前を取得します。
     * @return {@link String} - 名前
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * 引数を取得します。
     * @return {@link String} - 引数
     */
    @NotNull
    public String getArgments() {
        return argments;
    }

    /**
     * ({@link SplitValue})の配列を生成します。
     * @param types - 検索を行いたい要素の配列(例: {@link Argment}等)
     * @return {@link SplitValue} - 引数の一覧
     */
    @NotNull
    public SplitValue[] getValues(@NotNull SplitType[] types) {
        if (StringUtils.isEmpty(argments)) {
            return new SplitValue[0];
        }
        var split = StringUtils.split(argments, ',');
        return StreamUtils.toArray(split, s -> new SplitValue(s, types), SplitValue[]::new);
    }

    @Override
    @NotNull
    public String toString() {
        return StringUtils.isEmpty(argments) ? name : name + start + argments + end;
    }
}