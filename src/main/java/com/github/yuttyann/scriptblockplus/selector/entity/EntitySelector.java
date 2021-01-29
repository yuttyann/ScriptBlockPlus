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
package com.github.yuttyann.scriptblockplus.selector.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.github.yuttyann.scriptblockplus.enums.Argment;
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

    private static final Entity[] EMPTY_ENTITY_ARRAY = new Entity[0];

    @NotNull
    public static Entity[] getEntities(@NotNull CommandSender sender, @Nullable Location start, @NotNull String selector) {
        var result = new ArrayList<Entity>();
        var location = copy(sender, start);
        var argmentSplit = new ArgmentSplit(selector);
        var argmentValues = argmentSplit.getArgmentValues();
        switch (argmentSplit.getSelector()) {
            case "@p": {
                var players = location.getWorld().getPlayers();
                if (players.size() == 0) {
                    return EMPTY_ENTITY_ARRAY;
                }
                int count = 0;
                int limit = sort(getLimit(argmentValues, 1), location, players);
                for (var player : players) {
                    if (!StreamUtils.allMatch(argmentValues, t -> canBeAccepted(player, location, t))) {
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
                int limit = sort(getLimit(argmentValues, players.size()), location, players);
                for (var player : players) {
                    if (!StreamUtils.allMatch(argmentValues, t -> canBeAccepted(player, location, t))) {
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
                int limit = getLimit(argmentValues, 1);
                var randomInts = IntStream.range(0, players.size()).boxed().collect(Collectors.toList());
                Collections.shuffle(randomInts, new Random());
                for (int value : randomInts) {
                    var player = players.get(value);
                    if (!StreamUtils.allMatch(argmentValues, t -> canBeAccepted(player, location, t))) {
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
                int limit = sort(getLimit(argmentValues, entities.size()), location, entities);
                for (var entity : entities) {
                    if (!StreamUtils.allMatch(argmentValues, t -> canBeAccepted(entity, location, t))) {
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
                if (sender instanceof Entity && StreamUtils.allMatch(argmentValues, t -> canBeAccepted((Entity) sender, location, t))) {
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

    private static int getLimit(@NotNull ArgmentValue[] argmentValues, int other) {
        for (var argmentValue : argmentValues) {
            if (argmentValue.getArgment() == Argment.C) {
                return Integer.parseInt(argmentValue.getValue());
            }
        }
        return other;
    }

    private static boolean canBeAccepted(@NotNull Entity entity, @NotNull Location location, @NotNull ArgmentValue argmentValue) {
        if (argmentValue.getValue()== null) {
            return false;
        }
        switch (argmentValue.getArgment()) {
            case C:
                return true;
            case X:
            case Y:
            case Z:
                return setXYZ(location, argmentValue);
            case DX:
            case DY:
            case DZ:
                return isDRange(entity, location, argmentValue);
            case R:
            case RM:
                return isR(entity, location, argmentValue);
            case RX:
            case RXM:
                return isRX(entity, argmentValue);
            case RY:
            case RYM:
                return isRY(entity, argmentValue);
            case L:
            case LM:
                return isL(entity, argmentValue);
            case M:
                return isM(entity, argmentValue);
            case TAG:
                return isTag(entity, argmentValue);
            case TEAM:
                return isTeam(entity, argmentValue);
            case TYPE:
                return isType(entity, argmentValue);
            case NAME:
                return isName(entity, argmentValue);
            case SCORE:
            case SCORE_MIN:
                return isScore(entity, argmentValue);
            default:
                return false;
        }
    }

    private static boolean setXYZ(@NotNull Location location, @NotNull ArgmentValue argmentValue) {
        switch (argmentValue.getArgment()) {
            case X:
                setLocation(location, "x", argmentValue.getValue());
                break;
            case Y:
                setLocation(location, "y", argmentValue.getValue());
                break;
            case Z:
                setLocation(location, "z", argmentValue.getValue());
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
    
    private static boolean isDRange(@NotNull Entity entity, @NotNull Location location, @NotNull ArgmentValue argmentValue) {
        if (!entity.getWorld().equals(location.getWorld())) {
            return false;
        }
        double base = 0.0D, value = 0.0D;
        switch (argmentValue.getArgment()) {
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
        return value > (base - 0.35D) && value < (base + Double.parseDouble(argmentValue.getValue()) + 1.35D);
    }

    private static boolean isR(@NotNull Entity entity, @NotNull Location location, @NotNull ArgmentValue argmentValue) {
        if (!entity.getWorld().equals(location.getWorld())) {
            return false;
        }
        if (argmentValue.getArgment() == Argment.R) {
            return isLessThan(argmentValue, location.distance(entity.getLocation()));
        }
        return isGreaterThan(argmentValue, location.distance(entity.getLocation()));
    }

    private static boolean isRX(@NotNull Entity entity, @NotNull ArgmentValue argmentValue) {
        if (argmentValue.getArgment() == Argment.RX) {
            return isGreaterThan(argmentValue, entity.getLocation().getYaw());
        }
        return isLessThan(argmentValue, entity.getLocation().getYaw());
    }

    private static boolean isRY(@NotNull Entity entity, @NotNull ArgmentValue argmentValue) {
        if (argmentValue.getArgment() == Argment.RY) {
            return isGreaterThan(argmentValue, entity.getLocation().getPitch());
        }
        return isLessThan(argmentValue, entity.getLocation().getPitch());
    }

    private static boolean isL(@NotNull Entity entity, @NotNull ArgmentValue argmentValue) {
        if (entity instanceof Player) {
            if (argmentValue.getArgment() == Argment.L) {
                return isLessThan(argmentValue, ((Player) entity).getTotalExperience());
            }
            return isGreaterThan(argmentValue, ((Player) entity).getTotalExperience());
        }
        return false;
    }

    private static boolean isLessThan(@NotNull ArgmentValue argmentValue, double value) {
        return argmentValue.isInverted() != value < Double.parseDouble(argmentValue.getValue());
    }

    private static boolean isGreaterThan(@NotNull ArgmentValue argmentValue, double value) {
        return argmentValue.isInverted() != value > Double.parseDouble(argmentValue.getValue());
    }

    private static boolean isM(@NotNull Entity entity, @NotNull ArgmentValue argmentValue) {
        if (entity instanceof HumanEntity) {
            var value = argmentValue.getValue();
            var human = (HumanEntity) entity;
            return argmentValue.isInverted() != (getMode(value) == human.getGameMode());
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

    private static boolean isTag(@NotNull Entity entity, @NotNull ArgmentValue argmentValue) {
        return argmentValue.isInverted() != entity.getScoreboardTags().contains(argmentValue.getValue());
    }

    private static boolean isTeam(@NotNull Entity entity, @NotNull ArgmentValue argmentValue) {
        for (var team : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()) {
            if (!team.getName().equals(argmentValue.getValue())) {
                continue;
            }
            var entry = entity instanceof Player ? entity.getName() : entity.getUniqueId().toString();
            if (argmentValue.isInverted() != team.getEntries().contains(entry)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isType(@NotNull Entity entity, @NotNull ArgmentValue argmentValue) {
        return argmentValue.isInverted() != (entity.getType() == getEntityType(argmentValue.getValue()));
    }

    @NotNull
    @SuppressWarnings("deprecation")
    public static EntityType getEntityType(@NotNull String name) {
        name = StringUtils.removeStart(name, Utils.MINECRAFT);
        name = name.replaceAll("\\s+", "_").replaceAll("\\W", "");
        for (var entityType : EntityType.values()) {
            if (name.equalsIgnoreCase(entityType.name()) || name.equalsIgnoreCase(entityType.getName())) {
                return entityType;
            }
        }
        return null;
    }

    private static boolean isName(@NotNull Entity entity, @NotNull ArgmentValue argmentValue) {
        if (entity instanceof Player) {
            return argmentValue.isInverted() != argmentValue.getValue().equals(entity.getName());
        } else {
            return argmentValue.isInverted() != argmentValue.getValue().equals(entity.getCustomName());
        }
    }

    private static boolean isScore(@NotNull Entity entity, @NotNull ArgmentValue argmentValue) {
        var array = StringUtils.split(argmentValue.getValue(), '*');
        boolean isScore = argmentValue.getArgment() == Argment.SCORE;
        for (var objective : Bukkit.getScoreboardManager().getMainScoreboard().getObjectives()) {
            if (!objective.getName().equals(array[1])) {
                continue;
            }
            int score = objective.getScore(entity instanceof Player ? entity.getName() : entity.getUniqueId().toString()).getScore();
            if (argmentValue.isInverted() != (isScore ? score <= Integer.parseInt(array[0]) : score >= Integer.parseInt(array[0]))) {
                return true;
            }
        }
        return false;
    }
}