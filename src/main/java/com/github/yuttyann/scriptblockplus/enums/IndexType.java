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
package com.github.yuttyann.scriptblockplus.enums;

/**
 * ScriptBlockPlus IndexType 列挙型
 * @author yuttyann44581
 */
public enum IndexType {

    /**
     * 先頭に追加
     */
    TOP(-2),

    /**
     * 最後尾に追加
     */
    LAST(-1),

    /**
     * 指定したオプションより一つ前に追加
     */
    BEFORE(0),

    /**
     * 指定したオプションより一つ後に追加
     */
    AFTER(1);

    private final int amount;

    IndexType(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }
}
