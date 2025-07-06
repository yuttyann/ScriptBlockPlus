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
package com.github.yuttyann.scriptblockplus.utils.server;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import com.github.yuttyann.scriptblockplus.utils.reflect.SimpleReflection;
import com.github.yuttyann.scriptblockplus.utils.version.McVersion;

/**
 * Corelate-Bukkit CraftBukkit
 * @see McVersion#V_1_21_1
 * @author yuttyann44581
 */
public enum CraftBukkit implements SimpleReflection {
    /** {@code org.bukkit.craftbukkit} */
    CRAFTBUKKIT("org.bukkit.craftbukkit"),
    /** {@code org.bukkit.craftbukkit.advancement} */
    ADVANCEMENT(CRAFTBUKKIT, "advancement"),
    /** {@code org.bukkit.craftbukkit.attribute} */
    ATTRIBUTE(CRAFTBUKKIT, "attribute"),
    /** {@code org.bukkit.craftbukkit.ban} */
    BAN(CRAFTBUKKIT, "ban"),
    /** {@code org.bukkit.craftbukkit.block} */
    BLOCK(CRAFTBUKKIT, "block"),
    /** {@code org.bukkit.craftbukkit.block.banner} */
    BLOCK_BANNER(BLOCK, "banner"),
    /** {@code org.bukkit.craftbukkit.block.data} */
    BLOCK_DATA(BLOCK, "data"),
    /** {@code org.bukkit.craftbukkit.block.data.type} */
    BLOCK_DATA_TYPE(BLOCK_DATA, "type"),
    /** {@code org.bukkit.craftbukkit.block.impl} */
    BLOCK_IMPL(BLOCK, "impl"),
    /** {@code org.bukkit.craftbukkit.block.sign} */
    BLOCK_SIGN(BLOCK, "sign"),
    /** {@code org.bukkit.craftbukkit.boss} */
    BOSS(CRAFTBUKKIT, "boss"),
    /** {@code org.bukkit.craftbukkit.command} */
    COMMAND(CRAFTBUKKIT, "command"),
    /** {@code org.bukkit.craftbukkit.configuration} */
    CONFIGURATION(CRAFTBUKKIT, "configuration"),
    /** {@code org.bukkit.craftbukkit.conversations} */
    CONVERSATIONS(CRAFTBUKKIT, "conversations"),
    /** {@code org.bukkit.craftbukkit.damage} */
    DAMAGE(CRAFTBUKKIT, "damage"),
    /** {@code org.bukkit.craftbukkit.enchantments} */
    ENCHANTMENTS(CRAFTBUKKIT, "enchantments"),
    /** {@code org.bukkit.craftbukkit.entity} */
    ENTITY(CRAFTBUKKIT, "entity"),
    /** {@code org.bukkit.craftbukkit.entity.memory} */
    ENTITY_MEMORY(ENTITY, "memory"),
    /** {@code org.bukkit.craftbukkit.event} */
    EVENT(CRAFTBUKKIT, "event"),
    /** {@code org.bukkit.craftbukkit.generator} */
    GENERATOR(CRAFTBUKKIT, "generator"),
    /** {@code org.bukkit.craftbukkit.generator.structure} */
    GENERATOR_STRUCTURE(GENERATOR, "structure"),
    /** {@code org.bukkit.craftbukkit.help} */
    HELP(CRAFTBUKKIT, "help"),
    /** {@code org.bukkit.craftbukkit.inventory} */
    INVENTORY(CRAFTBUKKIT, "inventory"),
    /** {@code org.bukkit.craftbukkit.inventory.components} */
    INVENTORY_COMPONENTS(INVENTORY, "components"),
    /** {@code org.bukkit.craftbukkit.inventory.tags} */
    INVENTORY_TAGS(INVENTORY, "tags"),
    /** {@code org.bukkit.craftbukkit.inventory.trim} */
    INVENTORY_TRIM(INVENTORY, "trim"),
    /** {@code org.bukkit.craftbukkit.inventory.util} */
    INVENTORY_UTIL(INVENTORY, "util"),
    /** {@code org.bukkit.craftbukkit.inventory.view} */
    INVENTORY_VIEW(INVENTORY, "view"),
    /** {@code org.bukkit.craftbukkit.legacy} */
    LEGACY(CRAFTBUKKIT, "legacy"),
    /** {@code org.bukkit.craftbukkit.legacy.enums} */
    LEGACY_ENUMS(LEGACY, "enums"),
    /** {@code org.bukkit.craftbukkit.legacy.fieldrename} */
    LEGACY_FIELDRENAME(LEGACY, "fieldrename"),
    /** {@code org.bukkit.craftbukkit.legacy.reroute} */
    LEGACY_REROUTE(LEGACY, "reroute"),
    /** {@code org.bukkit.craftbukkit.map} */
    MAP(CRAFTBUKKIT, "map"),
    /** {@code org.bukkit.craftbukkit.metadata} */
    METADATA(CRAFTBUKKIT, "metadata"),
    /** {@code org.bukkit.craftbukkit.packs} */
    PACKS(CRAFTBUKKIT, "packs"),
    /** {@code org.bukkit.craftbukkit.persistence} */
    PERSISTENCE(CRAFTBUKKIT, "persistence"),
    /** {@code org.bukkit.craftbukkit.potion} */
    POTION(CRAFTBUKKIT, "potion"),
    /** {@code org.bukkit.craftbukkit.profile} */
    PROFILE(CRAFTBUKKIT, "profile"),
    /** {@code org.bukkit.craftbukkit.projectiles} */
    PROJECTILES(CRAFTBUKKIT, "projectiles"),
    /** {@code org.bukkit.craftbukkit.scheduler} */
    SCHEDULER(CRAFTBUKKIT, "scheduler"),
    /** {@code org.bukkit.craftbukkit.scoreboard} */
    SCOREBOARD(CRAFTBUKKIT, "scoreboard"),
    /** {@code org.bukkit.craftbukkit.structure} */
    STRUCTURE(CRAFTBUKKIT, "structure"),
    /** {@code org.bukkit.craftbukkit.tag} */
    TAG(CRAFTBUKKIT, "tag"),
    /** {@code org.bukkit.craftbukkit.util} */
    UTIL(CRAFTBUKKIT, "util"),
    /** {@code org.bukkit.craftbukkit.util.permissions} */
    UTIL_PERMISSIONS(UTIL, "permissions");

    private static String PACKAGE_VERSION;
    private static boolean PAPER_REMAPPED;

    private final String path;

    CraftBukkit(@NotNull String path) {
        var packageVersion = getLegacyPackageVersion();
        this.path = packageVersion.isEmpty() ? path : path + "." + packageVersion;
    }

    CraftBukkit(@NotNull CraftBukkit parent, @NotNull String path) {
        this.path = parent + "." + path;
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

    public static boolean isPaperRemapped() {
        return PAPER_REMAPPED;
    }

    @NotNull
    public static String getLegacyPackageVersion() {
        if (PACKAGE_VERSION == null) {
            try {
                Class.forName("org.bukkit.craftbukkit.CraftServer");
                PAPER_REMAPPED = true;
                PACKAGE_VERSION = "";
            } catch (Exception ex) {
                var packageName = Bukkit.getServer().getClass().getPackage().getName();
                PACKAGE_VERSION = packageName.substring(packageName.lastIndexOf('.') + 1);
            }
        }
        return PACKAGE_VERSION;
    }
}