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
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.github.yuttyann.scriptblockplus.utils.StreamUtils;

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

import static com.github.yuttyann.scriptblockplus.utils.StringUtils.*;

/**
 * ScriptBlockPlus EntitySelector クラス
 * <p>
 * NMSを使用せずに"Minecraft 1.12.2"までのセレクターを再現します。
 * @author yuttyann44581, zombiestriker(一部メソッドを引用させていただきました。)
 */
public final class EntitySelector {

    private static final Entity[] EMPTY_ENTITY_ARRAY = new Entity[0];
    private static final TagValue[] EMPTY_TAG_VALUE_ARRAY = new TagValue[0];
    
    private enum Tag {
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
        SCORE("score_<name>=", "score_", "="),
        SCORE_MIN("score_<name>_min=", "score_", "_min=");
    
        private final String syntax, prefix, suffix;

        Tag(@NotNull String syntax) {
            this(syntax, syntax, syntax);
        }

        Tag(@NotNull String syntax, @NotNull String prefix, @NotNull String suffix) {
            this.prefix = prefix;
            this.suffix = suffix;
            this.syntax = syntax;
        }

        @NotNull
        public String getValue(@NotNull String source) {
            switch (this) {
                case SCORE:
                case SCORE_MIN:
                    var objective = source.substring(prefix.length(), source.lastIndexOf(suffix));
                    return removeStart(source, prefix + objective + suffix) + "*" + objective;
                default:
                    return removeStart(source, syntax);
            }
        }

        public boolean has(@NotNull String source) {
            switch (this) {
                case SCORE:
                    return source.startsWith(prefix) && source.lastIndexOf(SCORE_MIN.suffix) == -1;
                case SCORE_MIN:
                    return source.startsWith(prefix) && source.lastIndexOf(suffix) > 0;
                default:
                    return source.startsWith(syntax);
            }
        }
    }

    private static class TagSplit {

        private final String tags, selector;

        private TagSplit(@NotNull String source) {
            if (source.startsWith("@") && source.indexOf("[") > 0) {
                int start = source.indexOf("[");
                this.tags = source.substring(start + 1, source.length() - 1).trim();
                this.selector = source.substring(0, start).trim();
            } else {
                this.tags = null;
                this.selector = source;
            }
        }

        @NotNull
        public String getSelector() {
            return selector.toLowerCase(Locale.ROOT);
        }

        @Nullable
        public TagValue[] getTagValues() {
            if (isEmpty(tags)) {
                return EMPTY_TAG_VALUE_ARRAY;
            }
            return StreamUtils.toArray(split(tags, ','), TagValue::new, TagValue[]::new);
        }
    }

    private static class TagValue {

        private final Tag tag;
        private final String value;

        private String cacheValue;
        private Boolean cacheInverted;

        private TagValue(@NotNull String source) {
            for (var tag : Tag.values()) {
                if (tag.has(source)) {
                    this.tag = tag;
                    this.value = tag.getValue(source);
                    return;
                }
            }
            this.tag = null;
            this.value = null;
        }

        @NotNull
        public Tag getTag() {
            return tag;
        }

        @Nullable
        public String getValue() {
            if (cacheValue == null) {
                cacheValue = isInverted() ? value.substring(1) : value;
            }
            return cacheValue;
        }

        public boolean isInverted() {
            if (cacheInverted == null) {
                cacheInverted = isNotEmpty(value) && value.indexOf("!") == 0;
            }
            return cacheInverted;
        }
    }

    @NotNull
    public static Entity[] getEntities(@NotNull CommandSender sender, @Nullable Location start, @NotNull String selector) {
        var result = new ArrayList<>();
        var location = copy(sender, start);
        var tagSplit = new TagSplit(selector);
        var tagValues = tagSplit.getTagValues();
        switch (tagSplit.getSelector()) {
            case "@p": {
                var players = location.getWorld().getPlayers();
                if (players.size() == 0) {
                    return EMPTY_ENTITY_ARRAY;
                }
                int count = 0;
                int limit = getLimit(tagValues);
                if (limit >= 0) {
                    players.sort(Comparator.comparing(p -> p.getLocation().distance(location), Comparator.naturalOrder()));
                } else {
                    players.sort(Comparator.comparing(p -> p.getLocation().distance(location), Comparator.reverseOrder()));
                    limit = -limit;
                }
                for (var player : players) {
                    if (!StreamUtils.allMatch(tagValues, t -> canBeAccepted(player, location, t))) {
                        continue;
                    }
                    if (limit <= count) {
                        break;
                    }
                    count++;
                    result.add(player);
                }
                break;
            }
            case "@a": {
                var players = location.getWorld().getPlayers();
                if (players.size() == 0) {
                    return EMPTY_ENTITY_ARRAY;
                }
                int count = 0;
                int limit = players.size();
                if (hasTag(tagValues, Tag.C)) {
                    limit = getLimit(tagValues);
                }
                for (var player : players) {
                    if (!StreamUtils.allMatch(tagValues, t -> canBeAccepted(player, location, t))) {
                        continue;
                    }
                    if (limit <= count) {
                        break;
                    }
                    count++;
                    result.add(player);
                }
                break;
            }
            case "@r": {
                var players = location.getWorld().getPlayers();
                if (players.size() == 0) {
                    return EMPTY_ENTITY_ARRAY;
                }
                int count = 0;
                int limit = getLimit(tagValues);
                var randomInts = IntStream.range(0, players.size()).boxed().collect(Collectors.toList());
                Collections.shuffle(randomInts, new Random());
                for (int value : randomInts) {
                    var player = players.get(value);
                    if (!StreamUtils.allMatch(tagValues, t -> canBeAccepted(player, location, t))) {
                        continue;
                    }
                    if (limit <= count) {
                        break;
                    }
                    count++;
                    result.add(player);
                }
                break;
            }
            case "@e": {
                var entities = location.getWorld().getEntities();
                if (entities.size() == 0) {
                    return EMPTY_ENTITY_ARRAY;
                }
                int count = 0;
                int limit = entities.size();
                if (hasTag(tagValues, Tag.C)) {
                    limit = getLimit(tagValues);
                }
                for (var entity : entities) {
                    if (!StreamUtils.allMatch(tagValues, t -> canBeAccepted(entity, location, t))) {
                        continue;
                    }
                    if (limit <= count) {
                        break;
                    }
                    count++;
                    result.add(entity);
                }
                break;
            }
            case "@s": {
                if (sender instanceof Entity && StreamUtils.allMatch(tagValues, t -> canBeAccepted((Entity) sender, location, t))) {
                    result.add((Entity) sender);
                }
                break;
            }
            default:
                return EMPTY_ENTITY_ARRAY;
        }
        return result.size() > 0 ? result.toArray(Entity[]::new) : EMPTY_ENTITY_ARRAY;
    }

    @NotNull
    public static Location copy(@NotNull CommandSender sender, @Nullable Location location) {
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
        switch (tagValue.tag) {
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
    
    private static boolean hasTag(@NotNull TagValue[] tagValues, @NotNull Tag tag) {
        return StreamUtils.anyMatch(tagValues, t -> t.getTag() == tag);
    }

    private static int getLimit(@NotNull TagValue[] tagValues) {
        for (var tagValue : tagValues) {
            if (tagValue.tag == Tag.C) {
                return Integer.parseInt(tagValue.getValue());
            }
        }
        return 1;
    }

    private static boolean setXYZ(@NotNull Location location, @NotNull TagValue tagValue) {
        switch (tagValue.tag) {
            case X:
                location.setX(getRelative(location, "x", tagValue.getValue()));
                break;
            case Y:
                location.setY(getRelative(location, "y", tagValue.getValue()));
                break;
            case Z:
                location.setZ(getRelative(location, "z", tagValue.getValue()));
                break;
            default:
                return false;
        }
        return true;
    }

    public static double getRelative(@NotNull Location location, @NotNull String relative, @NotNull String value) {
        if (value.startsWith("~")) {
            double number = value.length() == 1 ? 0.0D : Double.parseDouble(value.substring(1));
            switch (relative.toLowerCase(Locale.ROOT)) {
                case "x":
                    return location.getX() + number;
                case "y":
                    return location.getY() + number;
                case "z":
                    return location.getZ() + number;
                default:
                    return 0.0D;
            }
        } else if (value.startsWith("^")) {
            var empty = new Location(location.getWorld(), 0, 0, 0);
            double number = value.length() == 1 ? 0.0D : Double.parseDouble(value.substring(1));
            switch (relative.toLowerCase(Locale.ROOT)) {
                case "x":
                    empty.setYaw(Location.normalizeYaw(location.getYaw() - 90));
                    empty.setPitch(location.getPitch());
                    return location.getX() + empty.getDirection().normalize().multiply(number).getX();
                case "y":
                    empty.setYaw(location.getYaw());
                    empty.setPitch(location.getPitch() - 90);
                    return location.getY() + empty.getDirection().normalize().multiply(number).getY();
                case "z":
                    return location.getZ() + location.getDirection().normalize().multiply(number).getZ();
                default:
                    return 0.0D;
            }
        }
        return Double.parseDouble(value);
    }
    
    private static boolean isDRange(@NotNull Entity entity, @NotNull Location location, @NotNull TagValue tagValue) {
        if (!entity.getWorld().equals(location.getWorld())) {
            return false;
        }
        double base = 0.0D, value = 0.0D;
        switch (tagValue.tag) {
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
        return value > (base - 0.35D) && value < (base + Double.parseDouble(tagValue.getValue()) + 1.35D);
    }

    private static boolean isR(@NotNull Entity entity, @NotNull Location location, @NotNull TagValue tagValue) {
        if (!entity.getWorld().equals(location.getWorld())) {
            return false;
        }
        if (tagValue.tag == Tag.R) {
            return isLessThan(tagValue, location.distance(entity.getLocation()));
        }
        return isGreaterThan(tagValue, location.distance(entity.getLocation()));
    }

    private static boolean isRX(@NotNull Entity entity, @NotNull TagValue tagValue) {
        if (tagValue.tag == Tag.RX) {
            return isGreaterThan(tagValue, entity.getLocation().getYaw());
        }
        return isLessThan(tagValue, entity.getLocation().getYaw());
    }

    private static boolean isRY(@NotNull Entity entity, @NotNull TagValue tagValue) {
        if (tagValue.tag == Tag.RY) {
            return isGreaterThan(tagValue, entity.getLocation().getPitch());
        }
        return isLessThan(tagValue, entity.getLocation().getPitch());
    }

    private static boolean isL(@NotNull Entity entity, @NotNull TagValue tagValue) {
        if (entity instanceof Player) {
            if (tagValue.tag == Tag.L) {
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
        var array = split(tagValue.getValue(), '*');
        boolean isScore = tagValue.tag == Tag.SCORE;
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