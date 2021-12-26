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
package com.github.yuttyann.scriptblockplus.enums.server;

import com.github.yuttyann.scriptblockplus.enums.server.reflect.IReflection;

import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus Mojang 列挙型
 * @author yuttyann44581
 */
public enum Mojang implements IReflection {
    MJ("com.mojang"),
    BRIGADIER(MJ, "brigadier"),
    BD_ARGUMENTS(MJ, "arguments"),
    BD_BUILDER(MJ, "builder"),
    BD_CONTEXT(MJ, "context"),
    BD_EXCEPTIONS(MJ, "exceptions"),
    BD_SUGGESTION(MJ, "suggestion"),
    BD_TREE(MJ, "tree");

    private final String path;

    Mojang(@NotNull String path) {
        this.path = path;
    }

    Mojang(@NotNull Mojang parent, @NotNull String path) {
        this(parent + "." + path);
    }

    @Override
    @NotNull
    public String getPath() {
        return path;
    }

    @Override
    @NotNull
    public String toString() {
        return path;
    }
}