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

import com.github.yuttyann.scriptblockplus.utils.StringUtils;

import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus SplitValue クラス
 * @author yuttyann44581
 */
public class SplitValue {

    private final SplitType type;
    private final String value;

    private String cacheValue;
    private Boolean cacheInverted;

    /**
     * コンストラクタ
     * @param argment - 引数
     * @param types - 検索を行いたい要素の配列(例: {@link Argment}等)
     */
    public SplitValue(@NotNull String argment, @NotNull SplitType[] types) {
        for (var splitType : types) {
            if (splitType.match(argment)) {
                this.type = splitType;
                this.value = splitType.getValue(argment);
                return;
            }
        }
        throw new NullPointerException("SplitType[" + argment + "] not found");
    }

    /**
     * {@link SplitType}を取得します。
     * @return {@link SplitType} - 引数の種類
     */
    @NotNull
    public SplitType getType() {
        return type;
    }

    /**
     * 値を取得します。
     * @return {@link String} - 値
     */
    @NotNull
    public String getValue() {
        if (cacheValue == null && StringUtils.isNotEmpty(value)) {
            cacheValue = isInverted() ? value.substring(1) : value;
        }
        return cacheValue;
    }

    /**
     * 否定演算子が存在するのかどうか。
     * @return {@link boolean} - 存在する場合は{@code true}
     */
    public boolean isInverted() {
        if (cacheInverted == null && StringUtils.isNotEmpty(value)) {
            cacheInverted = value.indexOf("!") == 0;
        }
        return cacheInverted;
    }
}