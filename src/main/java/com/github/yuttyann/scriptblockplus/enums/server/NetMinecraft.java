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
 * ScriptBlockPlus NetMinecraft 列挙型
 * @author yuttyann44581
 */
public enum NetMinecraft implements IReflection {
    NM("net.minecraft"),
    ADVANCEMENTS(NM, "advancements"),
    AD_CRITEREON(ADVANCEMENTS, "critereon"),
    COMMANDS(NM, "commands"),
    CM_ARGUMENTS(COMMANDS, "arguments"),
    CM_AR_BLOCKS(CM_ARGUMENTS, "blocks"),
    CM_AR_COORDINATES(CM_ARGUMENTS, "coordinates"),
    CM_AR_ITEM(CM_ARGUMENTS, "item"),
    CM_AR_SELECTOR(CM_ARGUMENTS, "selector"),
    CM_AR_SL_OPTIONS(CM_AR_SELECTOR, "options"),
    CM_SYNCHRONIZATION(COMMANDS, "synchronization"),
    CM_SY_BRIGADIER(CM_SYNCHRONIZATION, "brigadier"),
    CORE(NM, "core"),
    CR_CAULDRON(CORE, "cauldron"),
    CR_DISPENSER(CORE, "dispenser"),
    CR_PARTICLES(CORE, "particles"),
    DATA(NM, "data"),
    DT_STRUCTURES(DATA, "structures"),
    DT_WORLDGEN(DATA, "worldgen"),
    DT_WG_BIOME(DT_WORLDGEN, "biome"),
    GAMETEST(NM, "gametest"),
    GM_GT_FRAMEWORK(GAMETEST, "framework"),
    LOCALE(NM, "locale"),
    NBT(NM, "nbt"),
    NETWORK(NM, "network"),
    NW_CHAT(NETWORK, "chat"),
    NW_PROTOCOL(NETWORK, "protocol"),
    NW_PR_GAME(NW_PROTOCOL, "game"),
    NW_PR_HANDSHAKE(NW_PROTOCOL, "handshake"),
    NW_PR_LOGIN(NW_PROTOCOL, "login"),
    NW_PR_STATUS(NW_PROTOCOL, "status"),
    NW_SYNCHER(NETWORK, "syncher"),
    OBFUSCATE(NM, "obfuscate"),
    RECIPEBOOK(NM, "recipebook"),
    RESOURCES(NM, "resources"),
    SERVER(NM, "server"),
    SR_BOSSEVENTS(SERVER, "bossevents"),
    SR_COMMANDS(SERVER, "commands"),
    SR_CM_DATA(SR_COMMANDS, "data"),
    SR_DEDICATED(SERVER, "dedicated"),
    SR_GUI(SERVER, "gui"),
    SR_LEVEL(SERVER, "level"),
    SR_LV_PROGRESS(SR_LEVEL, "progress"),
    SR_NETWORK(SERVER, "network"),
    SR_PACKS(SERVER, "packs"),
    SR_PA_METADATA(SR_PACKS, "metadata"),
    SR_PA_MD_PACK(SR_PA_METADATA, "pack"),
    SR_PA_REPOSITORY(SR_PACKS, "repository"),
    SR_PA_RESOURCES(SR_PACKS, "resources"),
    SR_PLAYERS(SERVER, "players"),
    SR_RCON(SERVER, "rcon"),
    SR_RC_THREAD(SR_RCON, "thread"),
    SOUNDS(NM, "sounds"),
    STATS(NM, "stats"),
    TAGS(NM, "tags"),
    UTIL(NM, "util"),
    UT_DATAFIX(UTIL, "datafix"),
    UT_DF_FIXES(UT_DATAFIX, "fixes"),
    UT_DF_SCHEMAS(UT_DATAFIX, "schemas"),
    UT_MONITORING(UTIL, "monitoring"),
    UT_PROFILING(UTIL, "profiling"),
    UT_PR_METRICS(UT_PROFILING, "metrics"),
    UT_PR_MR_PROFILING(UT_PR_METRICS, "profiling"),
    UT_PR_MR_STORAGE(UT_PR_METRICS, "storage"),
    UT_RANDOM(UTIL, "random"),
    UT_THREAD(UTIL, "thread"),
    UT_VALUEPROVIDERS(UTIL, "valueproviders"),
    UT_WORLDUPDATE(UTIL, "worldupdate"),
    WORLD(NM, "world"),
    WR_DAMAGESOURCE(WORLD, "damagesource"),
    WR_EFFECT(WORLD, "effect"),
    WR_ENTITY(WORLD, "entity"),
    WR_EN_AI(WR_ENTITY, "ai"),
    WR_EN_AI_ATTRIBUTES(WR_EN_AI, "attributes"),
    WR_EN_AI_BEHAVIOR(WR_EN_AI, "behavior"),
    WR_EN_AI_CONTROL(WR_EN_AI, "control"),
    WR_EN_AI_GOAL(WR_EN_AI, "goal"),
    WR_EN_AI_GO_TARGET(WR_EN_AI_GOAL, "target"),
    WR_EN_AI_GOSSIP(WR_EN_AI, "gossip"),
    WR_EN_AI_MEMORY(WR_EN_AI, "memory"),
    WR_EN_AI_NAVIGATION(WR_EN_AI, "navigation"),
    WR_EN_AI_SENSING(WR_EN_AI, "sensing"),
    WR_EN_AI_TARGETING(WR_EN_AI, "targeting"),
    WR_EN_AI_UTIL(WR_EN_AI, "util"),
    WR_EN_AI_VILLAGE(WR_EN_AI, "village"),
    WR_EN_AI_VI_POI(WR_EN_AI_VILLAGE, "poi"),
    WR_EN_AMBIENT(WR_ENTITY, "ambient"),
    WR_EN_ANIMAL(WR_ENTITY, "animal"),
    WR_EN_AM_AXOLOTL(WR_EN_ANIMAL, "axolotl"),
    WR_EN_AM_GOAT(WR_EN_ANIMAL, "goat"),
    WR_EN_AM_HORSE(WR_EN_ANIMAL, "horse"),
    WR_EN_BOSS(WR_ENTITY, "boss"),
    WR_EN_BS_ENDERDRAGON(WR_EN_BOSS, "enderdragon"),
    WR_EN_BS_ED_PHASES(WR_EN_BS_ENDERDRAGON, "phases"),
    WR_EN_BS_WITHER(WR_EN_BOSS, "wither"),
    WR_EN_DECORATION(WR_ENTITY, "decoration"),
    WR_EN_ITEM(WR_ENTITY, "item"),
    WR_EN_MONSTER(WR_ENTITY, "monster"),
    WR_EN_MO_HOGLIN(WR_EN_MONSTER, "hoglin"),
    WR_EN_MO_PIGLIN(WR_EN_MONSTER, "piglin"),
    WR_EN_NPC(WR_ENTITY, "npc"),
    WR_EN_PLAYER(WR_ENTITY, "player"),
    WR_EN_PROJECTILE(WR_ENTITY, "projectile"),
    WR_EN_RAID(WR_ENTITY, "raid"),
    WR_EN_SCHEDULE(WR_ENTITY, "schedule"),
    WR_EN_VEHICLE(WR_ENTITY, "vehicle"),
    WR_FOOD(WORLD, "food"),
    WR_INVENTORY(WORLD, "inventory"),
    WR_IN_TOOLTIP(WR_INVENTORY, "tooltip"),
    WR_ITEM(WORLD, "item"),
    WR_IT_ALCHEMY(WR_ITEM, "alchemy"),
    WR_IT_CONTEXT(WR_ITEM, "context"),
    WR_IT_CRAFTING(WR_ITEM, "crafting"),
    WR_IT_ENCHANTMENT(WR_ITEM, "enchantment"),
    WR_IT_TRADING(WR_ITEM, "trading"),
    WR_LEVEL(WORLD, "level"),
    WR_LV_BIOME(WR_LEVEL, "biome"),
    WR_LV_BLOCK(WR_LEVEL, "block"),
    WR_LV_BL_ENTITY(WR_LV_BLOCK, "entity"),
    WR_LV_BL_GROWER(WR_LV_BLOCK, "grower"),
    WR_LV_BL_PISTON(WR_LV_BLOCK, "piston"),
    WR_LV_BL_STATE(WR_LV_BLOCK, "state"),
    WR_LV_BL_ST_PATTERN(WR_LV_BL_STATE, "pattern"),
    WR_LV_BL_ST_PREDICATE(WR_LV_BL_STATE, "predicate"),
    WR_LV_BL_ST_PROPERTIES(WR_LV_BL_STATE, "properties"),
    WR_LV_BORDER(WR_LEVEL, "border"),
    WR_LV_CHUNK(WR_LEVEL, "chunk"),
    WR_LV_CH_STORAGE(WR_LV_CHUNK, "storage"),
    WR_LV_DIMENSION(WR_LEVEL, "dimension"),
    WR_LV_DI_END(WR_LV_DIMENSION, "end"),
    WR_LV_ENTITY(WR_LEVEL, "entity"),
    WR_LV_GAMEEVENT(WR_LEVEL, "gameevent"),
    WR_LV_GE_VIBRATIONS(WR_LV_GAMEEVENT, "vibrations"),
    WR_LV_LEVELGEN(WR_LEVEL, "levelgen"),
    WR_LV_LG_CARVER(WR_LV_LEVELGEN, "carver"),
    WR_LV_LG_FEATURE(WR_LV_LEVELGEN, "feature"),
    WR_LV_LG_FE_BLOCKPLACERS(WR_LV_LG_FEATURE, "blockplacers"),
    WR_LV_LG_FE_CONFIGURATIONS(WR_LV_LG_FEATURE, "configurations"),
    WR_LV_LG_FE_FEATURESIZE(WR_LV_LG_FEATURE, "featuresize"),
    WR_LV_LG_FE_FOLIAGEPLACERS(WR_LV_LG_FEATURE, "foliageplacers"),
    WR_LV_LG_FE_STATEPROVIDERS(WR_LV_LG_FEATURE, "stateproviders"),
    WR_LV_LG_FE_STRUCTURES(WR_LV_LG_FEATURE, "structures"),
    WR_LV_LG_FE_TREEDECORATORS(WR_LV_LG_FEATURE, "treedecorators"),
    WR_LV_LG_FE_TRUNKPLACERS(WR_LV_LG_FEATURE, "trunkplacers"),
    WR_LV_LG_FLAT(WR_LV_LEVELGEN, "flat"),
    WR_LV_LG_HEIGHTPROVIDERS(WR_LV_LEVELGEN, "heightproviders"),
    WR_LV_LG_PLACEMENT(WR_LV_LEVELGEN, "placement"),
    WR_LV_LG_PM_NETHER(WR_LV_LG_PLACEMENT, "nether"),
    WR_LV_LG_STRUCTURE(WR_LV_LEVELGEN, "structure"),
    WR_LV_LG_ST_TEMPLATESYSTEM(WR_LV_LG_STRUCTURE, "templatesystem"),
    WR_LV_LG_SURFACEBUILDERS(WR_LV_LEVELGEN, "surfacebuilders"),
    WR_LV_LG_SYNTH(WR_LV_LEVELGEN, "synth"),
    WR_LV_LIGHT(WR_LEVEL, "lighting"),
    WR_LV_NEWBIOME(WR_LEVEL, "newbiome"),
    WR_LV_NB_AREA(WR_LV_NEWBIOME, "area"),
    WR_LV_NB_CONTEXT(WR_LV_NEWBIOME, "context"),
    WR_LV_NB_LAYER(WR_LV_NEWBIOME, "layer"),
    WR_LV_NB_LA_TRAITS(WR_LV_NB_LAYER, "traits"),
    WR_LV_PATHFINDER(WR_LEVEL, "pathfinder"),
    WR_LV_PORTAL(WR_LEVEL, "portal"),
    WR_LV_SAVEDDATA(WR_LEVEL, "saveddata"),
    WR_LV_SD_MAPS(WR_LV_SAVEDDATA, "maps"),
    WR_LV_STORAGE(WR_LEVEL, "storage"),
    WR_LV_ST_LOOT(WR_LV_STORAGE, "loot"),
    WR_LV_ST_LT_ENTRIES(WR_LV_ST_LOOT, "entries"),
    WR_LV_ST_LT_FUNCTIONS(WR_LV_ST_LOOT, "functions"),
    WR_LV_ST_LT_PARAMETERS(WR_LV_ST_LOOT, "parameters"),
    WR_LV_ST_LT_PREDICATES(WR_LV_ST_LOOT, "predicates"),
    WR_LV_ST_LT_PROVIDERS(WR_LV_ST_LOOT, "providers"),
    WR_LV_ST_LT_PR_NBT(WR_LV_ST_LT_PROVIDERS, "nbt"),
    WR_LV_ST_LT_PR_NUMBER(WR_LV_ST_LT_PROVIDERS, "number"),
    WR_LV_ST_LT_PR_SCORE(WR_LV_ST_LT_PROVIDERS, "score"),
    WR_LV_TIMERS(WR_LEVEL, "timers"),
    WR_PHYS(WORLD, "phys"),
    WR_PH_SHAPES(WR_PHYS, "shapes"),
    WR_SCORES(WORLD, "scores"),
    WR_SC_CRITERIA(WORLD, "criteria"),

    /** {@code 1.17}未満の{@code NetMinecraft} */
    LEGACY(SERVER, Utils.getPackageVersion());

    private final String path;

    private static final boolean HAS_NMS, IS_LEGACY;

    static {
        boolean hasNMS = false, isLegacy = false;
        try {
            Class.forName(LEGACY + ".Entity");
            hasNMS = isLegacy = true;
        } catch (ClassNotFoundException e) { }
        if (!hasNMS) {
            try {
                Class.forName(SERVER + ".Main");
                hasNMS = true;
                isLegacy = false;
            } catch (ClassNotFoundException e) { }
        }
        HAS_NMS = hasNMS;
        IS_LEGACY = isLegacy;
    }

    NetMinecraft(@NotNull String path) {
        this.path = path;
    }

    NetMinecraft(@NotNull NetMinecraft parent, @NotNull String path) {
        this(parent + "." + path);
    }

    public static boolean hasNMS() {
        return HAS_NMS;
    }

    public static boolean isLegacy() {
        return IS_LEGACY;
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