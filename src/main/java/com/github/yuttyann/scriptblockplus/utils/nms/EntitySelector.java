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
package com.github.yuttyann.scriptblockplus.utils.nms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus EntitySelector クラス
 * <p>
 * NMSを使用せずに"Minecraft 1.12.2"までのセレクターを再現します。
 * @author yuttyann44581, zombiestriker(一部メソッドを引用させていただきました。)
 */
public final class EntitySelector {

    private static final Entity[] EMPTY_ENTITY_ARRAY = new Entity[0];
    private static final TagValue[] EMPTY_TAG_VALUE_ARRAY = new TagValue[0];
    
    private enum SelectorType {
        X("x="),
        Y("y="),
        Z("z="),
        DX("dx="),
        DY("dy="),
        DZ("dz="),
        R("r="),
        RM("rm="),
        RX("rx="),
        RXM("rxm="),
        RY("ry="),
        RYM("rym="),
        C("c="),
        L("l="),
        LM("lm="),
        M("m="),
        TAG("tag="),
        TEAM("team="),
        TYPE("type="),
        NAME("name="),
        SCORE("score_(.*?)="),
        SCORE_MIN("score_(.*?)_min=");
    
        private final String syntax;

        SelectorType(@NotNull String syntax) {
            this.syntax = syntax;
        }

        @NotNull
        public String getValue(@NotNull String source) {
            switch (this) {
                case SCORE:
                case SCORE_MIN:
                    var pattern = Pattern.compile(syntax).matcher(source);
                    if (!pattern.find()) {
                        var syntax = "score_<name>" + (this == SCORE_MIN ? "_min=.." : "=..");
                        throw new IllegalArgumentException("Incorrect grammar Example: " + syntax);
                    }
                    var group = pattern.group(1);
                    var result = "score_" + group + (this == SCORE_MIN ? "_min" : "");
                    return StringUtils.removeStart(source, result) + "*" + group;
                default:
                    return StringUtils.removeStart(source, syntax);
            }
        }

        @NotNull
        public String getSyntax() {
            return syntax;
        }

        public boolean has(@NotNull String source) {
            switch (this) {
                case SCORE:
                case SCORE_MIN:
                    return source.matches("score_(.*?)_min=");
                default:
                    return source.startsWith(syntax);
            }
        }
    }

    private static class SelectorSplit {

        private final String tags;
        private final String selector;

        private SelectorSplit(@NotNull String source) {
            if (source.startsWith("@") && source.indexOf("[") > 0) {
                int end = source.indexOf("[");
                this.tags = source.substring(end + 1, source.length()).trim();
                this.selector = source.substring(0, end).trim();
            } else {
                this.tags = null;
                this.selector = source;
            }
        }

        @NotNull
        public String getSelector() {
            return selector;
        }

        @Nullable
        public TagValue[] getTagValues() {
            if (StringUtils.isEmpty(tags)) {
                return EMPTY_TAG_VALUE_ARRAY;
            }
            var array = StringUtils.split(tags, ',');
            return StreamUtils.toArray(array, TagValue::new, TagValue[]::new);
        }
    }

    private static class TagValue {

        private final String value;
        private final SelectorType selectorType;

        private String cache;

        private TagValue(@NotNull String source) {
            for (var selectorType : SelectorType.values()) {
                if (selectorType.has(source)) {
                    this.value = selectorType.getValue(source);
                    this.selectorType = selectorType;
                    return;
                }
            }
            this.value = null;
            this.selectorType = null;
        }

        public boolean isInverted() {
            return StringUtils.isNotEmpty(value) && value.indexOf("!") == 0;
        }

        @Nullable
        public String getValue() {
            return cache == null ? cache = (isInverted() ? value.substring(1) : value) : cache;
        }

        @NotNull
        public SelectorType getSelectorType() {
            return selectorType;
        }
    }

    @NotNull
    public static Entity[] getEntities(@NotNull CommandSender sender, @Nullable Location start, @NotNull String selector) {
        var split = new SelectorSplit(selector);
        var prefix = split.getSelector();
        var values = split.getTagValues();
        var entities = new ArrayList<>();
        var location = copy(sender, start);
        if (prefix.equals("@p")) {
            int count = 0;
            int limit = getLimit(values);
            var players = location.getWorld().getPlayers();
            players.sort(Comparator.comparing(p -> p.getLocation().distance(location), Comparator.naturalOrder()));
            for (var player : players) {
                if (!StreamUtils.allMatch(values, t -> canBeAccepted(player, location, t))) {
                    continue;
                }
                if (limit == -1) {
                    entities.add(player);
                    break;
                }
                if (limit > count) {
                    break;
                }
                count++;
                entities.add(player);
            }
        }
        if (prefix.equals("@r")) {
            int count = 0;
            int limit = getLimit(values);
            var players = location.getWorld().getPlayers();
            var list = IntStream.of(players.size()).boxed().collect(Collectors.toList());
            for (int i = 0; i < players.size(); i++) {
                Collections.shuffle(list);
                var player = players.get(list.get(0));
                if (!StreamUtils.allMatch(values, t -> canBeAccepted(player, location, t))) {
                    continue;
                }
                if (limit == -1) {
                    entities.add(player);
                    break;
                }
                if (limit > count) {
                    break;
                }
                count++;
                entities.add(player);
            }
        }
        if (prefix.equals("@a")) {
            int count = 0;
            int limit = getLimit(values);
            for (var player : location.getWorld().getPlayers()) {
                if (!StreamUtils.allMatch(values, t -> canBeAccepted(player, location, t))) {
                    continue;
                }
                if (limit != -1 && limit > count) {
                    break;
                }
                count++;
                entities.add(player);
            }
        }
        if (prefix.equals("@e")) {
            int count = 0;
            int limit = getLimit(values);
            for (var entity : location.getWorld().getEntities()) {
                if (!StreamUtils.allMatch(values, t -> canBeAccepted(entity, location, t))) {
                    continue;
                }
                if (limit != -1 && limit > count) {
                    break;
                }
                count++;
                entities.add(entity);
            }
        }
        if (prefix.equals("@s")) {
            if (sender instanceof Entity && StreamUtils.allMatch(values, t -> canBeAccepted((Entity) sender, location, t))) {
                entities.add((Entity) sender);
            }
        }
		return entities.size() > 0 ? entities.toArray(Entity[]::new) : EMPTY_ENTITY_ARRAY;
    }

    @NotNull
    private static Location copy(@NotNull CommandSender sender, @Nullable Location location) {
        if (location == null) {
            if (sender instanceof Entity) {
                location = ((Entity) sender).getLocation();
            } else if (sender instanceof BlockCommandSender) {
                location = ((BlockCommandSender) sender).getBlock().getLocation().add(0.5D, 0.0D, 0.5D);
            }
        }
        Validate.notNull(location, "location");
        return new Location(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    private static boolean canBeAccepted(@NotNull Entity entity, @NotNull Location location, @NotNull TagValue tagValue) {
        if (tagValue.value == null) {
            return false;
        }
        switch (tagValue.selectorType) {
            case C:
                return true;
            case X:
            case Y:
            case Z:
                return setXYZ(location, tagValue);
            case DX:
            case DY:
            case DZ:
                return isDRange(entity, location, tagValue);
            case R:
            case RM:
                return isR(entity, location, tagValue);
            case RX:
            case RXM:
                return isRX(entity, tagValue);
            case RY:
            case RYM:
                return isRY(entity, tagValue);
            case L:
            case LM:
                return isL(entity, tagValue);
            case M:
                return isM(entity, tagValue);
            case TAG:
                return isTag(entity, tagValue);
            case TEAM:
                return isTeam(entity, tagValue);
            case TYPE:
                return isType(entity, tagValue);
            case NAME:
                return isName(entity, tagValue);
            case SCORE:
            case SCORE_MIN:
                return isScore(entity, tagValue);
            default:
                return false;
        }
    }

    private static int getLimit(@NotNull TagValue[] tagValues) {
        for (var tagValue : tagValues) {
            if (tagValue.selectorType == SelectorType.C) {
                return Integer.parseInt(tagValue.getValue());
            }
        }
        return -1;
    }

	private static boolean setXYZ(@NotNull Location location, @NotNull TagValue tagValue) {
        // チルダ文法を使えるようにする
        var value = tagValue.getValue();
        switch (tagValue.selectorType) {
            case X:
                location.setX(Double.parseDouble(value));
                break;
            case Y:
                location.setY(Double.parseDouble(value));
                break;
            case Z:
                location.setZ(Double.parseDouble(value));
                break;
            default:
                return false;
        }
		return true;
    }

    private static boolean isDRange(@NotNull Entity entity, @NotNull Location location, @NotNull TagValue tagValue) {
        if (!entity.getWorld().equals(location.getWorld())) {
            return false;
        }
        var base = (double) 0.0D;
        var value = (double) 0.0D;
        switch (tagValue.selectorType) {
            case DX:
                base = location.getX();
                value = entity.getLocation().getX();
                break;
            case DY:
                base = location.getY();
                value = entity.getLocation().getY();
                break;
            case DZ:
                base = location.getZ();
                value = entity.getLocation().getZ();
                break;
            default:
                return false;
        }
        return value > (base - .35D) && value < (base + Double.parseDouble(tagValue.getValue()) + 1.35D);
    }

    private static boolean isR(@NotNull Entity entity, @NotNull Location location, @NotNull TagValue tagValue) {
        if (!entity.getWorld().equals(location.getWorld())) {
            return false;
        }
        if (tagValue.selectorType == SelectorType.R) {
            return isLessThan(tagValue, location.distance(entity.getLocation()));
        }
        return isGreaterThan(tagValue, location.distance(entity.getLocation()));
    }

    private static boolean isRX(@NotNull Entity entity, @NotNull TagValue tagValue) {
        if (tagValue.selectorType == SelectorType.RX) {
            return isGreaterThan(tagValue, entity.getLocation().getYaw());
        }
        return isLessThan(tagValue, entity.getLocation().getYaw());
    }

    private static boolean isRY(@NotNull Entity entity, @NotNull TagValue tagValue) {
        if (tagValue.selectorType == SelectorType.RY) {
            return isGreaterThan(tagValue, entity.getLocation().getPitch());
        }
        return isLessThan(tagValue, entity.getLocation().getPitch());
    }

    private static boolean isL(@NotNull Entity entity, @NotNull TagValue tagValue) {
        if (entity instanceof Player) {
            if (tagValue.selectorType == SelectorType.L) {
                return isLessThan(tagValue, ((Player) entity).getTotalExperience());
            }
            return isGreaterThan(tagValue, ((Player) entity).getTotalExperience());
        }
        return false;
    }

    private static boolean isLessThan(@NotNull TagValue tagValue, double value) {
        return (value < Double.parseDouble(tagValue.getValue())) != tagValue.isInverted();
    }

    private static boolean isGreaterThan(@NotNull TagValue tagValue, double value) {
        return (value > Double.parseDouble(tagValue.getValue())) != tagValue.isInverted();
    }

    private static boolean isM(@NotNull Entity entity, @NotNull TagValue tagValue) {
        if (entity instanceof HumanEntity) {
            var value = tagValue.getValue();
            var human = (HumanEntity) entity;
            return tagValue.isInverted() != (getMode(value) == human.getGameMode());
        }
        return false;
    }

    private static GameMode getMode(@NotNull String value) {
        if (value.equalsIgnoreCase("0") || value.equalsIgnoreCase("s") || value.equalsIgnoreCase("survival")) {
            return GameMode.SURVIVAL;
        }
        if (value.equalsIgnoreCase("1") || value.equalsIgnoreCase("c") || value.equalsIgnoreCase("creative")) {
            return GameMode.CREATIVE;
        }
        if (value.equalsIgnoreCase("2") || value.equalsIgnoreCase("a") || value.equalsIgnoreCase("adventure")) {
            return GameMode.ADVENTURE;
        }
        if (value.equalsIgnoreCase("3") || value.equalsIgnoreCase("sp") || value.equalsIgnoreCase("spectator")) {
            return GameMode.SPECTATOR;
        }
        return null;
    }

    private static boolean isTag(@NotNull Entity entity, @NotNull TagValue tagValue) {
        return tagValue.isInverted() != entity.getScoreboardTags().contains(tagValue.getValue());
    }

    private static boolean isTeam(@NotNull Entity entity, @NotNull TagValue tagValue) {
        for (var team : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()) {
            if (!team.getName().equals(tagValue.getValue())) {
                continue;
            }
            var entry = entity instanceof Player ? entity.getName() : entity.getUniqueId().toString();
            if (tagValue.isInverted() != team.getEntries().contains(entry)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isType(@NotNull Entity entity, @NotNull TagValue tagValue) {
        return tagValue.isInverted() != entity.getType().name().equalsIgnoreCase(tagValue.getValue());
    }

    private static boolean isName(@NotNull Entity entity, @NotNull TagValue tagValue) {
        if (entity instanceof Player) {
            return tagValue.isInverted() != tagValue.getValue().equals(entity.getName());
        } else {
            return tagValue.isInverted() != tagValue.getValue().equals(entity.getCustomName());
        }
    }

    private static boolean isScore(@NotNull Entity entity, @NotNull TagValue tagValue) {
        var array = StringUtils.split(tagValue.getValue(), '*');
        boolean isScore = tagValue.selectorType == SelectorType.SCORE;
        for (var objective : Bukkit.getScoreboardManager().getMainScoreboard().getObjectives()) {
            if (!objective.getName().equals(array[1])) {
                continue;
            }
            int score = objective.getScore(entity instanceof Player ? entity.getName() : entity.getUniqueId().toString()).getScore();
            if (tagValue.isInverted() != (isScore ? score <= Integer.parseInt(array[0]) : score >= Integer.parseInt(array[0]))) {
                return true;
            }
        }
        return false;
    }
}