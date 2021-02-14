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
package com.github.yuttyann.scriptblockplus.file;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus SBFile クラス
 * <p>
 * 生成時に環境に合うセパレータに置換します。
 * @author yuttyann44581
 */
public class SBFile extends File {

    private static final long serialVersionUID = -303311616177581242L;

    public SBFile(@NotNull URI uri) {
        super(uri);
    }

    public SBFile(@NotNull String path) {
        super(setSeparator(path));
    }

    public SBFile(@NotNull File parent, @NotNull String child) {
        super(parent, setSeparator(child));
    }

    public SBFile(@NotNull String parent, @NotNull String child) {
        super(setSeparator(parent), setSeparator(child));
    }

    @NotNull
    public static String setSeparator(@NotNull String path) {
        if (separatorChar != '/') {
            return path.replace('/', separatorChar);
        }
        return path;
    }

    @NotNull
    public List<String> listNames(@NotNull FilenameFilter filter) {
        var names = list();
        if (names == null) {
            return Collections.emptyList();
        }
        var list = new ArrayList<String>(names.length);
        for (int i = 0, l = names.length; i < l; i++) {
            var name = names[i];
            if (filter.accept(this, name)) {
                list.add(name);
            }
        }
        return list;
    }
}