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

import com.github.yuttyann.scriptblockplus.utils.Utils;

import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus CraftBukkit 列挙型
 * @author yuttyann44581
 */
public enum CraftBukkit implements IReflection {
    CB("org.bukkit.craftbukkit." + Utils.getPackageVersion()),
    ADVANCEMENT(CB, "advancement"),
    ATTRIBUTE(CB, "attribute"),
    BLOCK(CB, "block"),
    BL_DATA(BLOCK, "data"),
    BL_DT_TYPE(BL_DATA, "type"),
    BL_IMPL(BLOCK, "impl"),
    BOSS(CB, "boss"),
    CHUNKIO(CB, "chunkio"),
    COMMAND(CB, "command"),
    CONVERSATIONS(CB, "conversations"),
    ENCHANTMENS(CB, "enchantments"),
    ENTITY(CB, "entity"),
    EN_MEMORY(ENTITY, "memory"),
    EVENT(CB, "event"),
    GENERATOR(CB, "generator"),
    HELP(CB, "help"),
    INVENTORY(CB, "inventory"),
    IN_TAGS(INVENTORY, "tags"),
    IN_UTIL(INVENTORY, "util"),
    LEGACY(CB, "legacy"),
    MAP(CB, "map"),
    METADATA(CB, "metadata"),
    PERSISTENCE(CB, "persistence"),
    POTION(CB, "potion"),
    PROJECTILES(CB, "projectiles"),
    SCHEDULER(CB, "scheduler"),
    SCOREBOARD(CB, "scoreboard"),
    UPDATER(CB, "updater"),
    TAG(CB, "tag"),
    UTIL(CB, "util"),
    UT_PERMISSIONS(UTIL, "permissions");

    private final String path;

    CraftBukkit(@NotNull String path) {
        this.path = path;
    }

    CraftBukkit(@NotNull CraftBukkit parent, @NotNull String path) {
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