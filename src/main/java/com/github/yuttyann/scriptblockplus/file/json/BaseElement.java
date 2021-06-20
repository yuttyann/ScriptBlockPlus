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

import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus BaseElement クラス
 * @author yuttyann44581
 */
public abstract class BaseElement implements Cloneable {

    /**
     * 引数同士を比較します。
     * @param argument1 - 引数1
     * @param argument2 - 引数2
     * @return {@code boolean} - 引数が一致するのかどうか
     */
    protected boolean compare(@NotNull Object argument1, @NotNull Object argument2) {
        return Objects.equals(argument1, argument2);
    }

    /**
     * エレメントのクラスを取得します。
     * @return {@link Class}&lt;? extends {@link BaseElement}&gt; - エレメントのクラス
     */
    @NotNull
    public Class<? extends BaseElement> getElementType() {
        return BaseElement.class;
    }

    @Override
    public BaseElement clone() throws CloneNotSupportedException {
        return (BaseElement) super.clone();
    }
}