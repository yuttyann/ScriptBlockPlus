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
package com.github.yuttyann.scriptblockplus.script.option;

import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.script.endprocess.EndProcess;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus Option オプションクラス
 * @author yuttyann44581
 */
public abstract class Option implements Comparable<Option> {

    /**
     * ScriptBlockPlus Result 列挙型
     * @author yuttyann44581
     */
    public enum Result {

        /**
         * 成功({@code 一時データの削除を行います。})
         */
        SUCCESS,

        /**
         * 失敗({@code 一時データの削除を行います。})
         */
        FAILURE,

        /**
         * 停止({@code 一時データを削除しない状態で、停止させます。})
         * <p>
         * また、{@link EndProcess}の{@code 成功処理}や{@code 失敗処理}を行いません。
         */
        STOP
    }

    public static final String PERMISSION_PREFIX = "scriptblockplus.option.";
    public static final String PERMISSION_ALL = PERMISSION_PREFIX + "*";

    private final int length;
    private final String name, syntax;

    private int ordinal = -1;

    protected Option() {
        var optionTag = getClass().getAnnotation(OptionTag.class);
        if (optionTag == null) {
            throw new NullPointerException("Annotation not found @OptionTag()");
        }
        this.name = optionTag.name();
        this.syntax = optionTag.syntax();
        this.length = syntax.length();
    }

    /**
     * オプションの名前を取得します。
     * @return {@link String} - オプションの名前
     */
    @NotNull
    public final String getName() {
        return name;
    }

    /**
     * オプションの構文を取得します。
     * @return {@link String} - オプションの構文
     */
    @NotNull
    public final String getSyntax() {
        return syntax;
    }

    /**
     * 構文の文字列の長さを取得します。
     * @return {@code int} - 文字列の長さ
     */
    public final int length() {
        return length;
    }

    /**
     * パーミッションノードを取得します。
     * @return {@link String} - パーミッションノード
     */
    @NotNull
    public final String getPermissionNode() {
        return PERMISSION_PREFIX + name;
    }

    /**
     * スクリプトからオプションの値を取得します。
     * @param script - スクリプト
     * @return {@link String} - オプションの値
     */
    @NotNull
    public final String getValue(@NotNull String script) {
        return StringUtils.isEmpty(script) ? "" : script.substring(length);
    }

    /**
     * 指定されたスクリプト内のオプションが正常だった場合は{@code true}を返します。
     * @param script - スクリプト
     * @return {@code boolean} - 正常だった場合は{@code true}
     */
    public final boolean isOption(@NotNull String script) {
        return script.length() >= length && script.indexOf(syntax) == 0;
    }

    /**
     * 指定した{@code boolean}に応じて実行結果を返します。
     * <p>
     * 戻り値の記述を短縮することができます。
     * @param value - {@code true}の場合は{@link Result#SUCCESS}、{@code false}の場合は{@link Result#FAILURE}
     * @return {@link Result} - 実行結果
     */
    @NotNull
    public final Result toResult(final boolean value) {
        return value ? Result.SUCCESS : Result.FAILURE;
    }

    /**
     * オプションを呼び出します。
     * @param scriptRead - {@link ScriptRead}
     * @return {@link Result} - 成功した場合は{@link Result#SUCCESS}
     */
    @NotNull
    public abstract Result callOption(@NotNull ScriptRead scriptRead);

    @Override
    public int compareTo(@NotNull Option another) {
        return Integer.compare(this.ordinal, another.ordinal);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Option) {
            var option = (Option) obj;
            return name.equals(option.name) && syntax.equals(option.syntax);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        int prime = 31;
        hash = prime * hash + name.hashCode();
        hash = prime * hash + syntax.hashCode();
        return hash;
    }

    @Override
    @NotNull
    public String toString() {
        return "Option{name=" + name + ", syntax=" + syntax + '}';
    }
}