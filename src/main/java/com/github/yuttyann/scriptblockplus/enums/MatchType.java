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
 * ScriptBlockPlus MatchType 列挙型
 * @author yuttyann44581
 */
public enum MatchType {

    /**
     * アイテムのID
     */
    TYPE,

    /**
     * アイテムのダメージ値
     */
    META,

    /**
     * アイテムの名前
     */
    NAME,

    /**
     * アイテムの説明文
     */
    LORE,

    /**
     * アイテムの個数
     */
    AMOUNT;
}