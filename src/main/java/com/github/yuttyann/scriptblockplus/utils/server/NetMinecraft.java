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

import org.jetbrains.annotations.NotNull;

import com.github.yuttyann.scriptblockplus.utils.reflect.SimpleReflection;
import com.github.yuttyann.scriptblockplus.utils.version.McVersion;

/**
 * FBS-Bukkit NetMinecraft
 * @see McVersion#V_1_21_1
 * @author yuttyann44581
 */
public enum NetMinecraft implements SimpleReflection {
    /** {@code net.minecraft} */
    NET_MINECRAFT("net.minecraft"),
    /** {@code net.minecraft.advancements} */
    ADVANCEMENTS(NET_MINECRAFT, "advancements"),
    /** {@code net.minecraft.advancements.critereon} */
    ADVANCEMENTS_CRITEREON(ADVANCEMENTS, "critereon"),
    /** {@code net.minecraft.commands} */
    COMMANDS(NET_MINECRAFT, "commands"),
    /** {@code net.minecraft.commands.arguments} */
    COMMANDS_ARGUMENTS(COMMANDS, "arguments"),
    /** {@code net.minecraft.commands.arguments.blocks} */
    COMMANDS_ARGUMENTS_BLOCKS(COMMANDS_ARGUMENTS, "blocks"),
    /** {@code net.minecraft.commands.arguments.coordinates} */
    COMMANDS_ARGUMENTS_COORDINATES(COMMANDS_ARGUMENTS, "coordinates"),
    /** {@code net.minecraft.commands.arguments.item} */
    COMMANDS_ARGUMENTS_ITEM(COMMANDS_ARGUMENTS, "item"),
    /** {@code net.minecraft.commands.arguments.selector} */
    COMMANDS_ARGUMENTS_SELECTOR(COMMANDS_ARGUMENTS, "selector"),
    /** {@code net.minecraft.commands.arguments.selector.options} */
    COMMANDS_ARGUMENTS_SELECTOR_OPTIONS(COMMANDS_ARGUMENTS_SELECTOR, "options"),
    /** {@code net.minecraft.commands.execution} */
    COMMANDS_EXECUTION(COMMANDS, "execution"),
    /** {@code net.minecraft.commands.execution.tasks} */
    COMMANDS_EXECUTION_TASKS(COMMANDS_EXECUTION, "tasks"),
    /** {@code net.minecraft.commands.functions} */
    COMMANDS_FUNCTIONS(COMMANDS, "functions"),
    /** {@code net.minecraft.commands.synchronization} */
    COMMANDS_SYNCHRONIZATION(COMMANDS, "synchronization"),
    /** {@code net.minecraft.commands.synchronization.brigadier} */
    COMMANDS_SYNCHRONIZATION_BRIGADIER(COMMANDS_SYNCHRONIZATION, "brigadier"),
    /** {@code net.minecraft.core} */
    CORE(NET_MINECRAFT, "core"),
    /** {@code net.minecraft.core.cauldron} */
    CORE_CAULDRON(CORE, "cauldron"),
    /** {@code net.minecraft.core.component} */
    CORE_COMPONENT(CORE, "component"),
    /** {@code net.minecraft.core.dispenser} */
    CORE_DISPENSER(CORE, "dispenser"),
    /** {@code net.minecraft.core.particles} */
    CORE_PARTICLES(CORE, "particles"),
    /** {@code net.minecraft.core.registries} */
    CORE_REGISTRIES(CORE, "registries"),
    /** {@code net.minecraft.data} */
    DATA(NET_MINECRAFT, "data"),
    /** {@code net.minecraft.data.registries} */
    DATA_REGISTRIES(DATA, "registries"),
    /** {@code net.minecraft.data.structures} */
    DATA_STRUCTURES(DATA, "structures"),
    /** {@code net.minecraft.data.worldgen} */
    DATA_WORLDGEN(DATA, "worldgen"),
    /** {@code net.minecraft.data.worldgen.biome} */
    DATA_WORLDGEN_BIOME(DATA_WORLDGEN, "biome"),
    /** {@code net.minecraft.data.worldgen.features} */
    DATA_WORLDGEN_FEATURES(DATA_WORLDGEN, "features"),
    /** {@code net.minecraft.data.worldgen.placement} */
    DATA_WORLDGEN_PLACEMENT(DATA_WORLDGEN, "placement"),
    /** {@code net.minecraft.gametest} */
    GAMETEST(NET_MINECRAFT, "gametest"),
    /** {@code net.minecraft.gametest.framework} */
    GAMETEST_FRAMEWORK(GAMETEST, "framework"),
    /** {@code net.minecraft.locale} */
    LOCALE(NET_MINECRAFT, "locale"),
    /** {@code net.minecraft.nbt} */
    NBT(NET_MINECRAFT, "nbt"),
    /** {@code net.minecraft.nbt.visitors} */
    NBT_VISITORS(NBT, "visitors"),
    /** {@code net.minecraft.network} */
    NETWORK(NET_MINECRAFT, "network"),
    /** {@code net.minecraft.network.chat} */
    NETWORK_CHAT(NETWORK, "chat"),
    /** {@code net.minecraft.network.chat.contents} */
    NETWORK_CHAT_CONTENTS(NETWORK_CHAT, "contents"),
    /** {@code net.minecraft.network.chat.numbers} */
    NETWORK_CHAT_NUMBERS(NETWORK_CHAT, "numbers"),
    /** {@code net.minecraft.network.codec} */
    NETWORK_CODEC(NETWORK, "codec"),
    /** {@code net.minecraft.network.protocol} */
    NETWORK_PROTOCOL(NETWORK, "protocol"),
    /** {@code net.minecraft.network.protocol.common} */
    NETWORK_PROTOCOL_COMMON(NETWORK_PROTOCOL, "common"),
    /** {@code net.minecraft.network.protocol.common.custom} */
    NETWORK_PROTOCOL_COMMON_CUSTOM(NETWORK_PROTOCOL_COMMON, "custom"),
    /** {@code net.minecraft.network.protocol.configuration} */
    NETWORK_PROTOCOL_CONFIGURATION(NETWORK_PROTOCOL, "configuration"),
    /** {@code net.minecraft.network.protocol.cookie} */
    NETWORK_PROTOCOL_COOKIE(NETWORK_PROTOCOL, "cookie"),
    /** {@code net.minecraft.network.protocol.game} */
    NETWORK_PROTOCOL_GAME(NETWORK_PROTOCOL, "game"),
    /** {@code net.minecraft.network.protocol.handshake} */
    NETWORK_PROTOCOL_HANDSHAKE(NETWORK_PROTOCOL, "handshake"),
    /** {@code net.minecraft.network.protocol.login} */
    NETWORK_PROTOCOL_LOGIN(NETWORK_PROTOCOL, "login"),
    /** {@code net.minecraft.network.protocol.login.custom} */
    NETWORK_PROTOCOL_LOGIN_CUSTOM(NETWORK_PROTOCOL_LOGIN, "custom"),
    /** {@code net.minecraft.network.protocol.ping} */
    NETWORK_PROTOCOL_PING(NETWORK_PROTOCOL, "ping"),
    /** {@code net.minecraft.network.protocol.status} */
    NETWORK_PROTOCOL_STATUS(NETWORK_PROTOCOL, "status"),
    /** {@code net.minecraft.network.syncher} */
    NETWORK_SYNCHER(NETWORK, "syncher"),
    /** {@code net.minecraft.obfuscate} */
    OBFUSCATE(NET_MINECRAFT, "obfuscate"),
    /** {@code net.minecraft.recipebook} */
    RECIPEBOOK(NET_MINECRAFT, "recipebook"),
    /** {@code net.minecraft.references} */
    REFERENCES(NET_MINECRAFT, "references"),
    /** {@code net.minecraft.resources} */
    RESOURCES(NET_MINECRAFT, "resources"),
    /** {@code net.minecraft.server} */
    SERVER(NET_MINECRAFT, "server"),
    /** {@code net.minecraft.server.advancements} */
    SERVER_ADVANCEMENTS(SERVER, "advancements"),
    /** {@code net.minecraft.server.bossevents} */
    SERVER_BOSSEVENTS(SERVER, "bossevents"),
    /** {@code net.minecraft.server.commands} */
    SERVER_COMMANDS(SERVER, "commands"),
    /** {@code net.minecraft.server.commands.data} */
    SERVER_COMMANDS_DATA(SERVER_COMMANDS, "data"),
    /** {@code net.minecraft.server.dedicated} */
    SERVER_DEDICATED(SERVER, "dedicated"),
    /** {@code net.minecraft.server.gui} */
    SERVER_GUI(SERVER, "gui"),
    /** {@code net.minecraft.server.level} */
    SERVER_LEVEL(SERVER, "level"),
    /** {@code net.minecraft.server.level.progress} */
    SERVER_LEVEL_PROGRESS(SERVER_LEVEL, "progress"),
    /** {@code net.minecraft.server.network} */
    SERVER_NETWORK(SERVER, "network"),
    /** {@code net.minecraft.server.network.config} */
    SERVER_NETWORK_CONFIG(SERVER_NETWORK, "config"),
    /** {@code net.minecraft.server.packs} */
    SERVER_PACKS(SERVER, "packs"),
    /** {@code net.minecraft.server.packs.linkfs} */
    SERVER_PACKS_LINKFS(SERVER_PACKS, "linkfs"),
    /** {@code net.minecraft.server.packs.metadata} */
    SERVER_PACKS_METADATA(SERVER_PACKS, "metadata"),
    /** {@code net.minecraft.server.packs.metadata.pack} */
    SERVER_PACKS_METADATA_PACK(SERVER_PACKS_METADATA, "pack"),
    /** {@code net.minecraft.server.packs.repository} */
    SERVER_PACKS_REPOSITORY(SERVER_PACKS, "repository"),
    /** {@code net.minecraft.server.packs.resources} */
    SERVER_PACKS_RESOURCES(SERVER_PACKS, "resources"),
    /** {@code net.minecraft.server.players} */
    SERVER_PLAYERS(SERVER, "players"),
    /** {@code net.minecraft.server.rcon} */
    SERVER_RCON(SERVER, "rcon"),
    /** {@code net.minecraft.server.rcon.thread} */
    SERVER_RCON_THREAD(SERVER_RCON, "thread"),
    /** {@code net.minecraft.sounds} */
    SOUNDS(NET_MINECRAFT, "sounds"),
    /** {@code net.minecraft.stats} */
    STATS(NET_MINECRAFT, "stats"),
    /** {@code net.minecraft.tags} */
    TAGS(NET_MINECRAFT, "tags"),
    /** {@code net.minecraft.util} */
    UTIL(NET_MINECRAFT, "util"),
    /** {@code net.minecraft.util.datafix} */
    UTIL_DATAFIX(UTIL, "datafix"),
    /** {@code net.minecraft.util.datafix.fixes} */
    UTIL_DATAFIX_FIXES(UTIL_DATAFIX, "fixes"),
    /** {@code net.minecraft.util.datafix.schemas} */
    UTIL_DATAFIX_SCHEMAS(UTIL_DATAFIX, "schemas"),
    /** {@code net.minecraft.util.debugchart} */
    UTIL_DEBUGCHART(UTIL, "debugchart"),
    /** {@code net.minecraft.util.monitoring} */
    UTIL_MONITORING(UTIL, "monitoring"),
    /** {@code net.minecraft.util.monitoring.jmx} */
    UTIL_MONITORING_JMX(UTIL_MONITORING, "jmx"),
    /** {@code net.minecraft.util.parsing} */
    UTIL_PARSING(UTIL, "parsing"),
    /** {@code net.minecraft.util.parsing.packrat} */
    UTIL_PARSING_PACKRAT(UTIL_PARSING, "packrat"),
    /** {@code net.minecraft.util.parsing.packrat.commands} */
    UTIL_PARSING_PACKRAT_COMMANDS(UTIL_PARSING_PACKRAT, "commands"),
    /** {@code net.minecraft.util.profiling} */
    UTIL_PROFILING(UTIL, "profiling"),
    /** {@code net.minecraft.util.profiling.jfr} */
    UTIL_PROFILING_JFR(UTIL_PROFILING, "jfr"),
    /** {@code net.minecraft.util.profiling.jfr.callback} */
    UTIL_PROFILING_JFR_CALLBACK(UTIL_PROFILING_JFR, "callback"),
    /** {@code net.minecraft.util.profiling.jfr.event} */
    UTIL_PROFILING_JFR_EVENT(UTIL_PROFILING_JFR, "event"),
    /** {@code net.minecraft.util.profiling.jfr.parse} */
    UTIL_PROFILING_JFR_PARSE(UTIL_PROFILING_JFR, "parse"),
    /** {@code net.minecraft.util.profiling.jfr.serialize} */
    UTIL_PROFILING_JFR_SERIALIZE(UTIL_PROFILING_JFR, "serialize"),
    /** {@code net.minecraft.util.profiling.jfr.stats} */
    UTIL_PROFILING_JFR_STATS(UTIL_PROFILING_JFR, "stats"),
    /** {@code net.minecraft.util.profiling.metrics} */
    UTIL_PROFILING_METRICS(UTIL_PROFILING, "metrics"),
    /** {@code net.minecraft.util.profiling.metrics.profiling} */
    UTIL_PROFILING_METRICS_PROFILING(UTIL_PROFILING_METRICS, "profiling"),
    /** {@code net.minecraft.util.profiling.metrics.storage} */
    UTIL_PROFILING_METRICS_STORAGE(UTIL_PROFILING_METRICS, "storage"),
    /** {@code net.minecraft.util.random} */
    UTIL_RANDOM(UTIL, "random"),
    /** {@code net.minecraft.util.thread} */
    UTIL_THREAD(UTIL, "thread"),
    /** {@code net.minecraft.util.valueproviders} */
    UTIL_VALUEPROVIDERS(UTIL, "valueproviders"),
    /** {@code net.minecraft.util.worldupdate} */
    UTIL_WORLDUPDATE(UTIL, "worldupdate"),
    /** {@code net.minecraft.world} */
    WORLD(NET_MINECRAFT, "world"),
    /** {@code net.minecraft.world.damagesource} */
    WORLD_DAMAGESOURCE(WORLD, "damagesource"),
    /** {@code net.minecraft.world.effect} */
    WORLD_EFFECT(WORLD, "effect"),
    /** {@code net.minecraft.world.entity} */
    WORLD_ENTITY(WORLD, "entity"),
    /** {@code net.minecraft.world.entity.ai} */
    WORLD_ENTITY_AI(WORLD_ENTITY, "ai"),
    /** {@code net.minecraft.world.entity.ai.attributes} */
    WORLD_ENTITY_AI_ATTRIBUTES(WORLD_ENTITY_AI, "attributes"),
    /** {@code net.minecraft.world.entity.ai.behavior} */
    WORLD_ENTITY_AI_BEHAVIOR(WORLD_ENTITY_AI, "behavior"),
    /** {@code net.minecraft.world.entity.ai.behavior.declarative} */
    WORLD_ENTITY_AI_BEHAVIOR_DECLARATIVE(WORLD_ENTITY_AI_BEHAVIOR, "declarative"),
    /** {@code net.minecraft.world.entity.ai.behavior.warden} */
    WORLD_ENTITY_AI_BEHAVIOR_WARDEN(WORLD_ENTITY_AI_BEHAVIOR, "warden"),
    /** {@code net.minecraft.world.entity.ai.control} */
    WORLD_ENTITY_AI_CONTROL(WORLD_ENTITY_AI, "control"),
    /** {@code net.minecraft.world.entity.ai.goal} */
    WORLD_ENTITY_AI_GOAL(WORLD_ENTITY_AI, "goal"),
    /** {@code net.minecraft.world.entity.ai.goal.target} */
    WORLD_ENTITY_AI_GOAL_TARGET(WORLD_ENTITY_AI_GOAL, "target"),
    /** {@code net.minecraft.world.entity.ai.gossip} */
    WORLD_ENTITY_AI_GOSSIP(WORLD_ENTITY_AI, "gossip"),
    /** {@code net.minecraft.world.entity.ai.memory} */
    WORLD_ENTITY_AI_MEMORY(WORLD_ENTITY_AI, "memory"),
    /** {@code net.minecraft.world.entity.ai.navigation} */
    WORLD_ENTITY_AI_NAVIGATION(WORLD_ENTITY_AI, "navigation"),
    /** {@code net.minecraft.world.entity.ai.sensing} */
    WORLD_ENTITY_AI_SENSING(WORLD_ENTITY_AI, "sensing"),
    /** {@code net.minecraft.world.entity.ai.targeting} */
    WORLD_ENTITY_AI_TARGETING(WORLD_ENTITY_AI, "targeting"),
    /** {@code net.minecraft.world.entity.ai.util} */
    WORLD_ENTITY_AI_UTIL(WORLD_ENTITY_AI, "util"),
    /** {@code net.minecraft.world.entity.ai.village} */
    WORLD_ENTITY_AI_VILLAGE(WORLD_ENTITY_AI, "village"),
    /** {@code net.minecraft.world.entity.ai.village.poi} */
    WORLD_ENTITY_AI_VILLAGE_POI(WORLD_ENTITY_AI_VILLAGE, "poi"),
    /** {@code net.minecraft.world.entity.ambient} */
    WORLD_ENTITY_AMBIENT(WORLD_ENTITY, "ambient"),
    /** {@code net.minecraft.world.entity.animal} */
    WORLD_ENTITY_ANIMAL(WORLD_ENTITY, "animal"),
    /** {@code net.minecraft.world.entity.animal.allay} */
    WORLD_ENTITY_ANIMAL_ALLAY(WORLD_ENTITY_ANIMAL, "allay"),
    /** {@code net.minecraft.world.entity.animal.armadillo} */
    WORLD_ENTITY_ANIMAL_ARMADILLO(WORLD_ENTITY_ANIMAL, "armadillo"),
    /** {@code net.minecraft.world.entity.animal.axolotl} */
    WORLD_ENTITY_ANIMAL_AXOLOTL(WORLD_ENTITY_ANIMAL, "axolotl"),
    /** {@code net.minecraft.world.entity.animal.camel} */
    WORLD_ENTITY_ANIMAL_CAMEL(WORLD_ENTITY_ANIMAL, "camel"),
    /** {@code net.minecraft.world.entity.animal.frog} */
    WORLD_ENTITY_ANIMAL_FROG(WORLD_ENTITY_ANIMAL, "frog"),
    /** {@code net.minecraft.world.entity.animal.goat} */
    WORLD_ENTITY_ANIMAL_GOAT(WORLD_ENTITY_ANIMAL, "goat"),
    /** {@code net.minecraft.world.entity.animal.horse} */
    WORLD_ENTITY_ANIMAL_HORSE(WORLD_ENTITY_ANIMAL, "horse"),
    /** {@code net.minecraft.world.entity.animal.sniffer} */
    WORLD_ENTITY_ANIMAL_SNIFFER(WORLD_ENTITY_ANIMAL, "sniffer"),
    /** {@code net.minecraft.world.entity.boss} */
    WORLD_ENTITY_BOSS(WORLD_ENTITY, "boss"),
    /** {@code net.minecraft.world.entity.boss.enderdragon} */
    WORLD_ENTITY_BOSS_ENDERDRAGON(WORLD_ENTITY_BOSS, "enderdragon"),
    /** {@code net.minecraft.world.entity.boss.enderdragon.phases} */
    WORLD_ENTITY_BOSS_ENDERDRAGON_PHASES(WORLD_ENTITY_BOSS_ENDERDRAGON, "phases"),
    /** {@code net.minecraft.world.entity.boss.wither} */
    WORLD_ENTITY_BOSS_WITHER(WORLD_ENTITY_BOSS, "wither"),
    /** {@code net.minecraft.world.entity.decoration} */
    WORLD_ENTITY_DECORATION(WORLD_ENTITY, "decoration"),
    /** {@code net.minecraft.world.entity.item} */
    WORLD_ENTITY_ITEM(WORLD_ENTITY, "item"),
    /** {@code net.minecraft.world.entity.monster} */
    WORLD_ENTITY_MONSTER(WORLD_ENTITY, "monster"),
    /** {@code net.minecraft.world.entity.monster.breeze} */
    WORLD_ENTITY_MONSTER_BREEZE(WORLD_ENTITY_MONSTER, "breeze"),
    /** {@code net.minecraft.world.entity.monster.hoglin} */
    WORLD_ENTITY_MONSTER_HOGLIN(WORLD_ENTITY_MONSTER, "hoglin"),
    /** {@code net.minecraft.world.entity.monster.piglin} */
    WORLD_ENTITY_MONSTER_PIGLIN(WORLD_ENTITY_MONSTER, "piglin"),
    /** {@code net.minecraft.world.entity.monster.warden} */
    WORLD_ENTITY_MONSTER_WARDEN(WORLD_ENTITY_MONSTER, "warden"),
    /** {@code net.minecraft.world.entity.npc} */
    WORLD_ENTITY_NPC(WORLD_ENTITY, "npc"),
    /** {@code net.minecraft.world.entity.player} */
    WORLD_ENTITY_PLAYER(WORLD_ENTITY, "player"),
    /** {@code net.minecraft.world.entity.projectile} */
    WORLD_ENTITY_PROJECTILE(WORLD_ENTITY, "projectile"),
    /** {@code net.minecraft.world.entity.projectile.windcharge} */
    WORLD_ENTITY_PROJECTILE_WINDCHARGE(WORLD_ENTITY_PROJECTILE, "windcharge"),
    /** {@code net.minecraft.world.entity.raid} */
    WORLD_ENTITY_RAID(WORLD_ENTITY, "raid"),
    /** {@code net.minecraft.world.entity.schedule} */
    WORLD_ENTITY_SCHEDULE(WORLD_ENTITY, "schedule"),
    /** {@code net.minecraft.world.entity.vehicle} */
    WORLD_ENTITY_VEHICLE(WORLD_ENTITY, "vehicle"),
    /** {@code net.minecraft.world.flag} */
    WORLD_FLAG(WORLD, "flag"),
    /** {@code net.minecraft.world.food} */
    WORLD_FOOD(WORLD, "food"),
    /** {@code net.minecraft.world.inventory} */
    WORLD_INVENTORY(WORLD, "inventory"),
    /** {@code net.minecraft.world.inventory.tooltip} */
    WORLD_INVENTORY_TOOLTIP(WORLD_INVENTORY, "tooltip"),
    /** {@code net.minecraft.world.item} */
    WORLD_ITEM(WORLD, "item"),
    /** {@code net.minecraft.world.item.alchemy} */
    WORLD_ITEM_ALCHEMY(WORLD_ITEM, "alchemy"),
    /** {@code net.minecraft.world.item.armortrim} */
    WORLD_ITEM_ARMORTRIM(WORLD_ITEM, "armortrim"),
    /** {@code net.minecraft.world.item.component} */
    WORLD_ITEM_COMPONENT(WORLD_ITEM, "component"),
    /** {@code net.minecraft.world.item.context} */
    WORLD_ITEM_CONTEXT(WORLD_ITEM, "context"),
    /** {@code net.minecraft.world.item.crafting} */
    WORLD_ITEM_CRAFTING(WORLD_ITEM, "crafting"),
    /** {@code net.minecraft.world.item.enchantment} */
    WORLD_ITEM_ENCHANTMENT(WORLD_ITEM, "enchantment"),
    /** {@code net.minecraft.world.item.enchantment.effects} */
    WORLD_ITEM_ENCHANTMENT_EFFECTS(WORLD_ITEM_ENCHANTMENT, "effects"),
    /** {@code net.minecraft.world.item.enchantment.providers} */
    WORLD_ITEM_ENCHANTMENT_PROVIDERS(WORLD_ITEM_ENCHANTMENT, "providers"),
    /** {@code net.minecraft.world.item.trading} */
    WORLD_ITEM_TRADING(WORLD_ITEM, "trading"),
    /** {@code net.minecraft.world.level} */
    WORLD_LEVEL(WORLD, "level"),
    /** {@code net.minecraft.world.level.biome} */
    WORLD_LEVEL_BIOME(WORLD_LEVEL, "biome"),
    /** {@code net.minecraft.world.level.block} */
    WORLD_LEVEL_BLOCK(WORLD_LEVEL, "block"),
    /** {@code net.minecraft.world.level.block.entity} */
    WORLD_LEVEL_BLOCK_ENTITY(WORLD_LEVEL_BLOCK, "entity"),
    /** {@code net.minecraft.world.level.block.entity.trialspawner} */
    WORLD_LEVEL_BLOCK_ENTITY_TRIALSPAWNER(WORLD_LEVEL_BLOCK_ENTITY, "trialspawner"),
    /** {@code net.minecraft.world.level.block.entity.vault} */
    WORLD_LEVEL_BLOCK_ENTITY_VAULT(WORLD_LEVEL_BLOCK_ENTITY, "vault"),
    /** {@code net.minecraft.world.level.block.grower} */
    WORLD_LEVEL_BLOCK_GROWER(WORLD_LEVEL_BLOCK, "grower"),
    /** {@code net.minecraft.world.level.block.piston} */
    WORLD_LEVEL_BLOCK_PISTON(WORLD_LEVEL_BLOCK, "piston"),
    /** {@code net.minecraft.world.level.block.state} */
    WORLD_LEVEL_BLOCK_STATE(WORLD_LEVEL_BLOCK, "state"),
    /** {@code net.minecraft.world.level.block.state.pattern} */
    WORLD_LEVEL_BLOCK_STATE_PATTERN(WORLD_LEVEL_BLOCK_STATE, "pattern"),
    /** {@code net.minecraft.world.level.block.state.predicate} */
    WORLD_LEVEL_BLOCK_STATE_PREDICATE(WORLD_LEVEL_BLOCK_STATE, "predicate"),
    /** {@code net.minecraft.world.level.block.state.properties} */
    WORLD_LEVEL_BLOCK_STATE_PROPERTIES(WORLD_LEVEL_BLOCK_STATE, "properties"),
    /** {@code net.minecraft.world.level.border} */
    WORLD_LEVEL_BORDER(WORLD_LEVEL, "border"),
    /** {@code net.minecraft.world.level.chunk} */
    WORLD_LEVEL_CHUNK(WORLD_LEVEL, "chunk"),
    /** {@code net.minecraft.world.level.chunk.status} */
    WORLD_LEVEL_CHUNK_STATUS(WORLD_LEVEL_CHUNK, "status"),
    /** {@code net.minecraft.world.level.chunk.storage} */
    WORLD_LEVEL_CHUNK_STORAGE(WORLD_LEVEL_CHUNK, "storage"),
    /** {@code net.minecraft.world.level.dimension} */
    WORLD_LEVEL_DIMENSION(WORLD_LEVEL, "dimension"),
    /** {@code net.minecraft.world.level.dimension.end} */
    WORLD_LEVEL_DIMENSION_END(WORLD_LEVEL_DIMENSION, "end"),
    /** {@code net.minecraft.world.level.entity} */
    WORLD_LEVEL_ENTITY(WORLD_LEVEL, "entity"),
    /** {@code net.minecraft.world.level.gameevent} */
    WORLD_LEVEL_GAMEEVENT(WORLD_LEVEL, "gameevent"),
    /** {@code net.minecraft.world.level.gameevent.vibrations} */
    WORLD_LEVEL_GAMEEVENT_VIBRATIONS(WORLD_LEVEL_GAMEEVENT, "vibrations"),
    /** {@code net.minecraft.world.level.levelgen} */
    WORLD_LEVEL_LEVELGEN(WORLD_LEVEL, "levelgen"),
    /** {@code net.minecraft.world.level.levelgen.blending} */
    WORLD_LEVEL_LEVELGEN_BLENDING(WORLD_LEVEL_LEVELGEN, "blending"),
    /** {@code net.minecraft.world.level.levelgen.blockpredicates} */
    WORLD_LEVEL_LEVELGEN_BLOCKPREDICATES(WORLD_LEVEL_LEVELGEN, "blockpredicates"),
    /** {@code net.minecraft.world.level.levelgen.carver} */
    WORLD_LEVEL_LEVELGEN_CARVER(WORLD_LEVEL_LEVELGEN, "carver"),
    /** {@code net.minecraft.world.level.levelgen.feature} */
    WORLD_LEVEL_LEVELGEN_FEATURE(WORLD_LEVEL_LEVELGEN, "feature"),
    /** {@code net.minecraft.world.level.levelgen.feature.configurations} */
    WORLD_LEVEL_LEVELGEN_FEATURE_CONFIGURATIONS(WORLD_LEVEL_LEVELGEN_FEATURE, "configurations"),
    /** {@code net.minecraft.world.level.levelgen.feature.featuresize} */
    WORLD_LEVEL_LEVELGEN_FEATURE_FEATURESIZE(WORLD_LEVEL_LEVELGEN_FEATURE, "featuresize"),
    /** {@code net.minecraft.world.level.levelgen.feature.foliageplacers} */
    WORLD_LEVEL_LEVELGEN_FEATURE_FOLIAGEPLACERS(WORLD_LEVEL_LEVELGEN_FEATURE, "foliageplacers"),
    /** {@code net.minecraft.world.level.levelgen.feature.rootplacers} */
    WORLD_LEVEL_LEVELGEN_FEATURE_ROOTPLACERS(WORLD_LEVEL_LEVELGEN_FEATURE, "rootplacers"),
    /** {@code net.minecraft.world.level.levelgen.feature.stateproviders} */
    WORLD_LEVEL_LEVELGEN_FEATURE_STATEPROVIDERS(WORLD_LEVEL_LEVELGEN_FEATURE, "stateproviders"),
    /** {@code net.minecraft.world.level.levelgen.feature.treedecorators} */
    WORLD_LEVEL_LEVELGEN_FEATURE_TREDECORATORS(WORLD_LEVEL_LEVELGEN_FEATURE, "treedecorators"),
    /** {@code net.minecraft.world.level.levelgen.feature.trunkplacers} */
    WORLD_LEVEL_LEVELGEN_FEATURE_TRUNKPLACERS(WORLD_LEVEL_LEVELGEN_FEATURE, "trunkplacers"),
    /** {@code net.minecraft.world.level.levelgen.flat} */
    WORLD_LEVEL_LEVELGEN_FLAT(WORLD_LEVEL_LEVELGEN, "flat"),
    /** {@code net.minecraft.world.level.levelgen.heightproviders} */
    WORLD_LEVEL_LEVELGEN_HEIGHTPROVIDERS(WORLD_LEVEL_LEVELGEN, "heightproviders"),
    /** {@code net.minecraft.world.level.levelgen.material} */
    WORLD_LEVEL_LEVELGEN_MATERIAL(WORLD_LEVEL_LEVELGEN, "material"),
    /** {@code net.minecraft.world.level.levelgen.placement} */
    WORLD_LEVEL_LEVELGEN_PLACEMENT(WORLD_LEVEL_LEVELGEN, "placement"),
    /** {@code net.minecraft.world.level.levelgen.presets} */
    WORLD_LEVEL_LEVELGEN_PRESETS(WORLD_LEVEL_LEVELGEN, "presets"),
    /** {@code net.minecraft.world.level.levelgen.structure} */
    WORLD_LEVEL_LEVELGEN_STRUCTURE(WORLD_LEVEL_LEVELGEN, "structure"),
    /** {@code net.minecraft.world.level.levelgen.structure.pieces} */
    WORLD_LEVEL_LEVELGEN_STRUCTURE_PIECES(WORLD_LEVEL_LEVELGEN_STRUCTURE, "pieces"),
    /** {@code net.minecraft.world.level.levelgen.structure.placement} */
    WORLD_LEVEL_LEVELGEN_STRUCTURE_PLACEMENT(WORLD_LEVEL_LEVELGEN_STRUCTURE, "placement"),
    /** {@code net.minecraft.world.level.levelgen.structure.pools} */
    WORLD_LEVEL_LEVELGEN_STRUCTURE_POOLS(WORLD_LEVEL_LEVELGEN_STRUCTURE, "pools"),
    /** {@code net.minecraft.world.level.levelgen.structure.pools.alias} */
    WORLD_LEVEL_LEVELGEN_STRUCTURE_POOLS_ALIAS(WORLD_LEVEL_LEVELGEN_STRUCTURE_POOLS, "alias"),
    /** {@code net.minecraft.world.level.levelgen.structure.structures} */
    WORLD_LEVEL_LEVELGEN_STRUCTURE_STRUCTURES(WORLD_LEVEL_LEVELGEN_STRUCTURE, "structures"),
    /** {@code net.minecraft.world.level.levelgen.structure.templatesystem} */
    WORLD_LEVEL_LEVELGEN_STRUCTURE_TEMPLATESYSTEM(WORLD_LEVEL_LEVELGEN_STRUCTURE, "templatesystem"),
    /** {@code net.minecraft.world.level.levelgen.structure.templatesystem.rule} */
    WORLD_LEVEL_LEVELGEN_STRUCTURE_TEMPLATESYSTEM_RULE(WORLD_LEVEL_LEVELGEN_STRUCTURE_TEMPLATESYSTEM, "rule"),
    /** {@code net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity} */
    WORLD_LEVEL_LEVELGEN_STRUCTURE_TEMPLATESYSTEM_RULE_BLOCKENTITY(WORLD_LEVEL_LEVELGEN_STRUCTURE_TEMPLATESYSTEM_RULE, "blockentity"),
    /** {@code net.minecraft.world.level.levelgen.synth} */
    WORLD_LEVEL_LEVELGEN_SYNTH(WORLD_LEVEL_LEVELGEN, "synth"),
    /** {@code net.minecraft.world.level.lighting} */
    WORLD_LEVEL_LIGHTING(WORLD_LEVEL, "lighting"),
    /** {@code net.minecraft.world.level.material} */
    WORLD_LEVEL_MATERIAL(WORLD_LEVEL, "material"),
    /** {@code net.minecraft.world.level.pathfinder} */
    WORLD_LEVEL_PATHFINDER(WORLD_LEVEL, "pathfinder"),
    /** {@code net.minecraft.world.level.portal} */
    WORLD_LEVEL_PORTAL(WORLD_LEVEL, "portal"),
    /** {@code net.minecraft.world.level.redstone} */
    WORLD_LEVEL_REDSTONE(WORLD_LEVEL, "redstone"),
    /** {@code net.minecraft.world.level.saveddata} */
    WORLD_LEVEL_SAVEDDATA(WORLD_LEVEL, "saveddata"),
    /** {@code net.minecraft.world.level.saveddata.maps} */
    WORLD_LEVEL_SAVEDDATA_MAPS(WORLD_LEVEL_SAVEDDATA, "maps"),
    /** {@code net.minecraft.world.level.storage} */
    WORLD_LEVEL_STORAGE(WORLD_LEVEL, "storage"),
    /** {@code net.minecraft.world.level.storage.loot} */
    WORLD_LEVEL_STORAGE_LOOT(WORLD_LEVEL_STORAGE, "loot"),
    /** {@code net.minecraft.world.level.storage.loot.entries} */
    WORLD_LEVEL_STORAGE_LOOT_ENTRIES(WORLD_LEVEL_STORAGE_LOOT, "entries"),
    /** {@code net.minecraft.world.level.storage.loot.functions} */
    WORLD_LEVEL_STORAGE_LOOT_FUNCTIONS(WORLD_LEVEL_STORAGE_LOOT, "functions"),
    /** {@code net.minecraft.world.level.storage.loot.parameters} */
    WORLD_LEVEL_STORAGE_LOOT_PARAMETERS(WORLD_LEVEL_STORAGE_LOOT, "parameters"),
    /** {@code net.minecraft.world.level.storage.loot.predicates} */
    WORLD_LEVEL_STORAGE_LOOT_PREDICATES(WORLD_LEVEL_STORAGE_LOOT, "predicates"),
    /** {@code net.minecraft.world.level.storage.loot.providers} */
    WORLD_LEVEL_STORAGE_LOOT_PROVIDERS(WORLD_LEVEL_STORAGE_LOOT, "providers"),
    /** {@code net.minecraft.world.level.storage.loot.providers.nbt} */
    WORLD_LEVEL_STORAGE_LOOT_PROVIDERS_NBT(WORLD_LEVEL_STORAGE_LOOT_PROVIDERS, "nbt"),
    /** {@code net.minecraft.world.level.storage.loot.providers.number} */
    WORLD_LEVEL_STORAGE_LOOT_PROVIDERS_NUMBER(WORLD_LEVEL_STORAGE_LOOT_PROVIDERS, "number"),
    /** {@code net.minecraft.world.level.storage.loot.providers.score} */
    WORLD_LEVEL_STORAGE_LOOT_PROVIDERS_SCORE(WORLD_LEVEL_STORAGE_LOOT_PROVIDERS, "score"),
    /** {@code net.minecraft.world.level.timers} */
    WORLD_LEVEL_TIMERS(WORLD_LEVEL, "timers"),
    /** {@code net.minecraft.world.level.validation} */
    WORLD_LEVEL_VALIDATION(WORLD_LEVEL, "validation"),
    /** {@code net.minecraft.world.phys} */
    WORLD_PHYS(WORLD, "phys"),
    /** {@code net.minecraft.world.phys.shapes} */
    WORLD_PHYS_SHAPES(WORLD_PHYS, "shapes"),
    /** {@code net.minecraft.world.scores} */
    WORLD_SCORES(WORLD, "scores"),
    /** {@code net.minecraft.world.scores.criteria} */
    WORLD_SCORES_CRITERIA(WORLD_SCORES, "criteria"),
    /** {@code net.minecraft.world.ticks} */
    WORLD_TICKS(WORLD, "ticks"),

    /** {@code net.minecraft.server.vX_XX_RX} */
    LEGACY_PATH(SERVER, CraftBukkit.getLegacyPackageVersion());

    public static final String WARNING_TEXT = "NetMinecraft(" + (McVersion.V_1_17.isSupported() ? NET_MINECRAFT : LEGACY_PATH) + ") not found.";

    private static boolean hasNMS, isLegacy;

    static {
        try {
            Class.forName(LEGACY_PATH + ".Entity");
            hasNMS = isLegacy = true;
        } catch (ClassNotFoundException ignored) { }
        if (!hasNMS) {
            try {
                Class.forName(SERVER + ".Main");
                hasNMS = true;
                isLegacy = false;
            } catch (ClassNotFoundException ignored) { }
        }
    }

    private final String path;

    NetMinecraft(@NotNull String path) {
        this.path = path;
    }

    NetMinecraft(@NotNull NetMinecraft parent, @NotNull String path) {
        this(parent + "." + path);
    }

    @Override
    @NotNull
    public String getPath() {
        return isLegacy ? LEGACY_PATH.path : path;
    }

    @Override
    @NotNull
    public String toString() {
        return getPath();
    }

    public static boolean hasNMS() {
        return hasNMS;
    }

    public static boolean isLegacy() {
        return isLegacy;
    }
}