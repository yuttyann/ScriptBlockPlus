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
package com.github.yuttyann.scriptblockplus.selector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.github.yuttyann.scriptblockplus.enums.splittype.Argument;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus EntitySelector クラス
 * <p>
 * NMSを使用せずに"Minecraft 1.12.2"までのセレクターを再現します。
 * <p>
 * zombiestriker(PsudoCommands)様から一部メソッドを引用させていただきました。
 * @author yuttyann44581
 */
public final class EntitySelector {

    @NotNull
    public static List<Entity> getEntities(@NotNull CommandSender sender, @NotNull Location start, @NotNull String selector) {
        var result = new ArrayList<Entity>();
        var location = setCenter(copy(sender, start));
        var split = new Split(selector, "@", "[", "]");
        var splitValues = split.getValues(Argument.values());
        switch (split.getName()) {
            case "@p": {
                var players = location.getWorld().getPlayers();
                if (players.size() == 0) {
                    return Collections.emptyList();
                }
                int count = 0;
                int limit = sort(getLimit(splitValues, 1), location, players);
                for (var player : players) {
                    if (!StreamUtils.allMatch(splitValues, t -> canBeAccepted(player, location, t))) {
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
                    return Collections.emptyList();
                }
                int count = 0;
                int limit = sort(getLimit(splitValues, players.size()), location, players);
                for (var player : players) {
                    if (!StreamUtils.allMatch(splitValues, t -> canBeAccepted(player, location, t))) {
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
                    return Collections.emptyList();
                }
                int count = 0;
                int limit = getLimit(splitValues, 1);
                var randomInts = IntStream.range(0, players.size()).boxed().collect(Collectors.toList());
                Collections.shuffle(randomInts, new Random());
                for (int value : randomInts) {
                    var player = players.get(value);
                    if (!StreamUtils.allMatch(splitValues, t -> canBeAccepted(player, location, t))) {
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
                    return Collections.emptyList();
                }
                int count = 0;
                int limit = sort(getLimit(splitValues, entities.size()), location, entities);
                for (var entity : entities) {
                    if (!StreamUtils.allMatch(splitValues, t -> canBeAccepted(entity, location, t))) {
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
                if (sender instanceof Entity && StreamUtils.allMatch(splitValues, t -> canBeAccepted((Entity) sender, location, t))) {
                    result.add((Entity) sender);
                }
                break;
            }
            default:
                return Collections.emptyList();
        }
        return result.size() > 0 ? result : Collections.emptyList();
    }

    @NotNull
    private static Location setCenter(@NotNull Location location) {
        location.setX(location.getBlockX() + 0.5D);
        location.setY(location.getBlockY());
        location.setZ(location.getBlockZ() + 0.5D);
        return location;
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

    private static int sort(int limit, @NotNull Location location, @NotNull List<? extends Entity> entities) {
        var order = (Comparator<Double>) null;
        if (limit >= 0) {
            order = Comparator.naturalOrder();
        } else {
            order = Comparator.reverseOrder();
            limit = -limit;
        }
        entities.sort(Comparator.comparing((Entity e) -> e.getLocation().distance(location), order).thenComparing(Entity::getTicksLived));
        return limit;
    }

    private static int getLimit(@NotNull SplitValue[] splitValues, int other) {
        for (var splitValue : splitValues) {
            if (splitValue.getType() == Argument.C) {
                return Integer.parseInt(splitValue.getValue());
            }
        }
        return other;
    }

    private static boolean canBeAccepted(@NotNull Entity entity, @NotNull Location location, @NotNull SplitValue splitValue) {
        if (splitValue.getValue()== null) {
            return false;
        }
        switch ((Argument) splitValue.getType()) {
            case C:
                return true;
            case X:
            case Y:
            case Z:
                return setXYZ(location, splitValue);
            case DX:
            case DY:
            case DZ:
                return isDRange(entity, location, splitValue);
            case R:
            case RM:
                return isR(entity, location, splitValue);
            case RX:
            case RXM:
                return isRX(entity, splitValue);
            case RY:
            case RYM:
                return isRY(entity, splitValue);
            case L:
            case LM:
                return isL(entity, splitValue);
            case M:
                return isM(entity, splitValue);
            case TAG:
                return isTag(entity, splitValue);
            case TEAM:
                return isTeam(entity, splitValue);
            case TYPE:
                return isType(entity, splitValue);
            case NAME:
                return isName(entity, splitValue);
            case SCORE:
            case SCORE_MIN:
                return isScore(entity, splitValue);
            default:
                return false;
        }
    }

    private static boolean setXYZ(@NotNull Location location, @NotNull SplitValue splitValue) {
        switch ((Argument) splitValue.getType()) {
            case X:
                setLocation(location, "x", splitValue.getValue());
                break;
            case Y:
                setLocation(location, "y", splitValue.getValue());
                break;
            case Z:
                setLocation(location, "z", splitValue.getValue());
                break;
            default:
                return false;
        }
        return true;
    }

    public static void setLocation(@NotNull Location location, @NotNull String axes, @NotNull String value) {
        if (value.startsWith("^")) {
            double number = value.length() == 1 ? 0.0D : Double.parseDouble(value.substring(1));
            switch (axes.toLowerCase(Locale.ROOT)) {
                case "x": {
                    var empty = new Location(location.getWorld(), 0.0D, 0.0D, 0.0D);
                    empty.setYaw(normalizeYaw(location.getYaw() - 90.0F));
                    empty.setPitch(location.getPitch());
                    location.add(empty.getDirection().normalize().multiply(number));
                    break;
                }
                case "y": {
                    var empty = new Location(location.getWorld(), 0.0D, 0.0D, 0.0D);
                    empty.setYaw(location.getYaw());
                    empty.setPitch(location.getPitch() - 90.0F);
                    location.add(empty.getDirection().normalize().multiply(number));
                    break;
                }
                case "z":
                    location.add(location.getDirection().normalize().multiply(number));
                    break;
            }
        } else if (value.startsWith("~")) {
            double number = value.length() == 1 ? 0.0D : Double.parseDouble(value.substring(1));
            switch (axes.toLowerCase(Locale.ROOT)) {
                case "x":
                    location.add(number, 0.0D, 0.0D);
                    break;
                case "y":
                    location.add(0.0D, number, 0.0D);
                    break;
                case "z":
                    location.add(0.0D, 0.0D, number);
                    break;
            }
        } else {
            double number = Double.parseDouble(value);
            switch (axes.toLowerCase(Locale.ROOT)) {
                case "x":
                    location.setX(number);
                    break;
                case "y":
                    location.setY(number);
                    break;
                case "z":
                    location.setZ(number);
                    break;
            }
        }
    }

    private static float normalizeYaw(float yaw) {
        yaw %= 360.0F;
        if (yaw >= 180.0F) {
            yaw -= 360.0F;
        } else if (yaw < -180.0F) {
            yaw += 360.0F;
        }
        return yaw;
    }
    
    private static boolean isDRange(@NotNull Entity entity, @NotNull Location location, @NotNull SplitValue splitValue) {
        if (!entity.getWorld().equals(location.getWorld())) {
            return false;
        }
        double base = 0.0D, value = 0.0D;
        switch ((Argument) splitValue.getType()) {
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
        return value > (base - 0.35D) && value < (base + Double.parseDouble(splitValue.getValue()) + 1.35D);
    }

    private static boolean isR(@NotNull Entity entity, @NotNull Location location, @NotNull SplitValue splitValue) {
        if (!entity.getWorld().equals(location.getWorld())) {
            return false;
        }
        if (splitValue.getType() == Argument.R) {
            return isLessThan(splitValue, location.distance(entity.getLocation()));
        }
        return isGreaterThan(splitValue, location.distance(entity.getLocation()));
    }

    private static boolean isRX(@NotNull Entity entity, @NotNull SplitValue splitValue) {
        if (splitValue.getType() == Argument.RX) {
            return isGreaterThan(splitValue, entity.getLocation().getYaw());
        }
        return isLessThan(splitValue, entity.getLocation().getYaw());
    }

    private static boolean isRY(@NotNull Entity entity, @NotNull SplitValue splitValue) {
        if (splitValue.getType() == Argument.RY) {
            return isGreaterThan(splitValue, entity.getLocation().getPitch());
        }
        return isLessThan(splitValue, entity.getLocation().getPitch());
    }

    private static boolean isL(@NotNull Entity entity, @NotNull SplitValue splitValue) {
        if (entity instanceof Player) {
            if (splitValue.getType() == Argument.L) {
                return isLessThan(splitValue, ((Player) entity).getTotalExperience());
            }
            return isGreaterThan(splitValue, ((Player) entity).getTotalExperience());
        }
        return false;
    }

    private static boolean isLessThan(@NotNull SplitValue splitValue, double value) {
        return splitValue.isInverted() != value < Double.parseDouble(splitValue.getValue());
    }

    private static boolean isGreaterThan(@NotNull SplitValue splitValue, double value) {
        return splitValue.isInverted() != value > Double.parseDouble(splitValue.getValue());
    }

    private static boolean isM(@NotNull Entity entity, @NotNull SplitValue splitValue) {
        if (entity instanceof HumanEntity) {
            var value = splitValue.getValue();
            var human = (HumanEntity) entity;
            return splitValue.isInverted() != (getMode(value) == human.getGameMode());
        }
        return false;
    }

    @Nullable
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

    private static boolean isTag(@NotNull Entity entity, @NotNull SplitValue splitValue) {
        return splitValue.isInverted() != entity.getScoreboardTags().contains(splitValue.getValue());
    }

    private static boolean isTeam(@NotNull Entity entity, @NotNull SplitValue splitValue) {
        for (var team : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()) {
            if (!team.getName().equals(splitValue.getValue())) {
                continue;
            }
            var entry = entity instanceof Player ? entity.getName() : entity.getUniqueId().toString();
            if (splitValue.isInverted() != team.getEntries().contains(entry)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isType(@NotNull Entity entity, @NotNull SplitValue splitValue) {
        return splitValue.isInverted() != (entity.getType() == getEntityType(splitValue.getValue()));
    }

    @NotNull
    @SuppressWarnings("deprecation")
    public static EntityType getEntityType(@NotNull String name) {
        name = StringUtils.removeStart(name.replace(' ', '_'), Utils.MINECRAFT);
        for (var entityType : EntityType.values()) {
            if (name.equalsIgnoreCase(entityType.name()) || name.equalsIgnoreCase(entityType.getName())) {
                return entityType;
            }
        }
        return null;
    }

    private static boolean isName(@NotNull Entity entity, @NotNull SplitValue splitValue) {
        if (entity instanceof Player) {
            return splitValue.isInverted() != splitValue.getValue().equals(entity.getName());
        } else {
            return splitValue.isInverted() != splitValue.getValue().equals(entity.getCustomName());
        }
    }

    private static boolean isScore(@NotNull Entity entity, @NotNull SplitValue splitValue) {
        var split = StringUtils.split(splitValue.getValue(), '*');
        var scoreArgument = splitValue.getType() == Argument.SCORE;
        for (var objective : Bukkit.getScoreboardManager().getMainScoreboard().getObjectives()) {
            if (!objective.getName().equals(split.get(0))) {
                continue;
            }
            int score = objective.getScore(entity instanceof Player ? entity.getName() : entity.getUniqueId().toString()).getScore();
            if (splitValue.isInverted() != (scoreArgument ? score <= Integer.parseInt(split.get(0)) : score >= Integer.parseInt(split.get(0)))) {
                return true;
            }
        }
        return false;
    }
}