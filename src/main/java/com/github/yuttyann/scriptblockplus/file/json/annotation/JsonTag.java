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

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * ScriptBlockPlus JsonTag 注釈
 * @author yuttyann44581
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface JsonTag {

    /**
     * ディレクトリのパスを取得します。
     * <p>
     * 例(json/testフォルダを指定する場合): {@code json/test}
     * @return {@link String} - ディレクトリのパス
     */
    @NotNull
    String path();

    /**
     * プラグインのIDを取得します。
     * <p>
     * 指定したプラグインのフォルダ内にファイルを生成します。
     * <p>
     * 指定しない場合は"ScriptBlockPlus"が選択されます。
     * @return {@link String} - プラグインのID
     */
    @NotNull
    String plugin() default "";

    /**
     * インデントを取得します。
     * <p>
     * 整形を行う際に利用されます。
     * @return {@link String} - インデント
     */
    @NotNull
    String indent() default "  ";

    /**
     * ファイルを保存した時にキャッシュを削除するのかどうか。
     * @return {@link boolean} - 削除を行う場合は{@code true}
     */
    boolean temporary() default false;

    /**
     * ファイルが存在する時のみキャッシュを保存するのかどうか。
     * @return {@link boolean} - ファイルが存在する時のみキャッシュを保存する場合は{@code true}
     */
    boolean cachefileexists() default false;
}