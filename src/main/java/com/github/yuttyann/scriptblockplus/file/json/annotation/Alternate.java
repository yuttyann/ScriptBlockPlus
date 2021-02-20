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
package com.github.yuttyann.scriptblockplus.file.json.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.google.gson.annotations.SerializedName;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * ScriptBlockPlus Alternate 注釈
 * <p>
 * Gsonのバージョンが古い場合に呼び出されます。
 * <p>
 * {@link SerializedName#alternate()}と同等の機能を持っています。
 * @author yuttyann44581
 */
@Target({ FIELD, METHOD })
@Retention(RUNTIME)
public @interface Alternate {

    /**
     * @return {@link String} - デシリアライズされた時の代替名を返します。
     */
    String[] value() default {};
}