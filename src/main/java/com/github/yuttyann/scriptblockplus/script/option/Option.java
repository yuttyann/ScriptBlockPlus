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

import com.github.yuttyann.scriptblockplus.enums.InstanceType;
import com.github.yuttyann.scriptblockplus.manager.SBConstructor;
import com.github.yuttyann.scriptblockplus.script.SBInstance;
import com.github.yuttyann.scriptblockplus.script.SBRead;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus Option オプションクラス
 * @author yuttyann44581
 */
public abstract class Option implements SBInstance<Option>, Comparable<Option> {

    public static final String PERMISSION_PREFIX = "scriptblockplus.option.";
    public static final String PERMISSION_ALL = PERMISSION_PREFIX + "*";

    private final int length;
    private final String name, syntax;

    private int ordinal = -1;

    protected Option() {
        var optionTag = getClass().getAnnotation(OptionTag.class);
        if (optionTag == null) {
            throw new NullPointerException("Annotation not found [OptionTag]");
        }
        this.name = optionTag.name();
        this.syntax = optionTag.syntax();
        this.length = syntax.length();
    }

    /**
     * インスタンスを生成します。
     * @return {@link Option} - オプション
     */
    @Override
    @NotNull
    public Option newInstance() {
        return new SBConstructor<>(getClass()).newInstance(InstanceType.REFLECTION);
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
     * @return {@link Integer} - 文字列の長さ
     */
    public final int length() {
        return length;
    }

    /**
     * オプションの序数を取得します
     * <p>
     * ※オプションの順番により変動
     * @return {@link Integer} - 序数
     */
    public final int ordinal() {
        return ordinal;
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
     * 指定されたスクリプト内のオプションが正常なのか確認します。
     * @param script - スクリプト
     * @return {@link String} - 正常だった場合はtrue
     */
    public final boolean isOption(@NotNull String script) {
        return script.length() >= length && script.indexOf(syntax) == 0;
    }

    /**
     * 失敗時に終了処理を無視します（デフォルトはfalseです）
     * @return {@link String} - trueの場合は終了処理を無視し、falseの場合は実行します。
     */
    public boolean isFailedIgnore() {
        return false;
    }

    /**
     * オプションを呼び出します。
     * @param sbRead - {@link SBRead}
     * @return {@link String} - 有効な場合はtrue
     */
    public abstract boolean callOption(@NotNull SBRead sbRead);

    @Override
    public int compareTo(@NotNull Option another) {
        return Integer.compare(this.ordinal, another.ordinal);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
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