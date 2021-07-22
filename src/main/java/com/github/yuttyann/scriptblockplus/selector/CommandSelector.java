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

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.enums.server.NetMinecraft;
import com.github.yuttyann.scriptblockplus.hook.plugin.Placeholder;
import com.github.yuttyann.scriptblockplus.utils.NMSHelper;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import com.google.common.collect.Lists;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ProxiedCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ScriptBlockPlus CommandSelector クラス
 * @author yuttyann44581
 */
public final class CommandSelector {

    private final static String NUMBERS = "+-.0123456789";
    private final static String SELECTOR_SUFFIX = "aeprs";
    private final static String[] SELECTOR_NAMES = { "@a", "@e", "@p", "@r", "@s" };
    private final static String[] SELECTOR_ARGUMENT_NAMES = { "@a[", "@e[", "@p[", "@r[", "@s[" };

    private final static String[] SEARCH_XYZ = { "{x}", "{y}", "{z}" };
    private final static String[] SEARCH_INDEX = { "{0}", "{1}", "{2}", "{3}", "{4}", "{5}", "{6}", "{7}", "{8}", "{9}", "{10}" };

    private static class Index {

        private final int start;
        private int end = 0;

        private Index(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @NotNull
        private String substring(@NotNull String source) {
            return source.substring(start, Math.min(end + 1, source.length()));
        }
    }

    public static boolean has(@NotNull String source) {
        return StreamUtils.anyMatch(SELECTOR_NAMES, source::contains);
    }

    @NotNull
    public static List<String> build(@NotNull CommandSender sender, @Nullable Location start, @NotNull String command) {
        int modCount = 0;
        var indexList = new ArrayList<Index>();
        var commandList = Lists.newArrayList(parse(command, sender, indexList));
        for (int i = 0, l = indexList.size(); i < l; i++) {
            var selector = indexList.get(i).substring(command);
            var entities = getTargets(sender, start, selector);
            if (entities == null || entities.size() == 0) {
                if (StreamUtils.anyMatch(SELECTOR_ARGUMENT_NAMES, selector::startsWith)) {
                    continue;
                } else if (selector.startsWith(SELECTOR_NAMES[2]) && sender instanceof Player) {
                    entities = Collections.singletonList((Entity) sender);
                } else {
                    continue;
                }
            }
            var works = true;
            for (int j = 1, k = entities.size(); j < k; j++) {
                var entity = entities.get(j);
                if (entity == null) {
                    works = false;
                    break;
                }
                commandList.add(StringUtils.replace(commandList.get(0), getReplaceIndex(i), getEntityName(entity)));
            }
            if (!works || entities.size() == 0 || entities.get(0) == null) {
                return Collections.emptyList();
            } else {
                replaceAll(commandList, i, getEntityName(entities.get(0)));
            }
            modCount++;
        }
        if (modCount > 0 && modCount != indexList.size()) {
            var name = sender.getName();
            for (int i = 0, l = indexList.size(); i < l; i++) {
                replaceAll(commandList, i, name);
            }
        }
        return modCount == 0 ? Collections.emptyList() : commandList;
    }

    @NotNull
    private static String parse(@NotNull String source, @NotNull CommandSender sender, @NotNull List<Index> indexList) {
        var chars = source.toCharArray();
        var builder = new StringBuilder(chars.length + 16);
        var tempBuilder = (StringBuilder) null;
        var tempLocation = (Location) null;
        for (int i = 0, j = 0, k = 0; i < chars.length; i++) {
            int one = i + 1, two = one + 1;
            if (chars[i] == '@' && one < chars.length && SELECTOR_SUFFIX.indexOf(chars[one]) > -1) {
                var index = new Index(i, one);
                if (two < chars.length && chars[two] == '[') {
                    for (int l = two, m = 0; l < chars.length; l++) {
                        if (chars[l] == '[') {
                            m++;
                        } else if (chars[l] == ']' && --m == 0) {
                            index.end = l;
                            i += Math.max(index.end - index.start, 0);
                            break;
                        }
                    }
                } else {
                    i++;
                }
                indexList.add(index);
                builder.append('{').append(j++).append('}');
            } else if ((chars[i] == '~' || chars[i] == '^') && (sender instanceof Entity || sender instanceof BlockCommandSender)) {
                if (k > 2) {
                    builder.append(sender.getName());
                } else {
                    if (tempBuilder == null) {
                        tempBuilder = new StringBuilder();
                    } else {
                        tempBuilder.setLength(0);
                    }
                    tempBuilder.append(chars[i]);
                    var axes = k == 0 ? "x" : k == 1 ? "y" : k == 2 ? "z" : "x";
                    for (int l = one; l < chars.length; l++) {
                        if (NUMBERS.indexOf(chars[l]) > -1) {
                            tempBuilder.append(chars[l]);
                            i++;
                        } else {
                            break;
                        }
                    }
                    if (tempLocation == null) {
                        tempLocation = EntitySelector.copy(sender, null);
                    }
                    EntitySelector.setLocation(tempLocation, axes, tempBuilder.toString());
                    builder.append('{').append(axes).append('}');
                    k++;
                }
            } else {
                builder.append(chars[i]);
            }
        }
        var result = builder.toString();
        if (tempLocation != null) {
            double x = tempLocation.getX(), y = tempLocation.getY(), z = tempLocation.getZ();
            var replace = new String[] { Double.toString(x), Double.toString(y), Double.toString(z) };
            result = StringUtils.replaceEach(result, SEARCH_XYZ, replace);
        }
        return result;
    }

    @NotNull
    public static List<Entity> getTargets(@NotNull CommandSender sender, @Nullable Location start, @NotNull String selector) {
        selector = Placeholder.INSTANCE.replace(getWorld(sender, start), selector);
        if (NetMinecraft.hasNMS() && Utils.isCBXXXorLater("1.13")) {
            try {
                return NMSHelper.selectEntities(sender, start, selector);
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        } else if (Utils.isCBXXXorLater("1.13.2")) {
            return Bukkit.selectEntities(sender, selector);
        }
        return EntitySelector.getEntities(sender, start, selector);
    }

    @NotNull
    private static World getWorld(@NotNull CommandSender sender, @Nullable Location location) {
        if (location != null) {
            return location.getWorld();
        }
        var world = (World) null;
        if (sender instanceof ProxiedCommandSender) {
            sender = ((ProxiedCommandSender) sender).getCallee();
        }
        if (sender instanceof Entity) {
            world = ((Entity) sender).getWorld();
        } else if (sender instanceof BlockCommandSender) {
            world = ((BlockCommandSender) sender).getBlock().getWorld();
        }
        return world == null ? BlockCoords.ZERO.getWorld() : world;
    }

    @NotNull
    private static String getEntityName(@NotNull Entity entity) {
        return entity instanceof Player ? entity.getName() : entity.getUniqueId().toString();
    }

    @NotNull
    private static String getReplaceIndex(int index) {
        return index >= SEARCH_INDEX.length ? "{" + index + "}" : SEARCH_INDEX[index];
    }

    private static void replaceAll(@NotNull List<String> commandList, int index, @NotNull String name) {
        for (int i = 0, l = commandList.size(); i < l; i++) {
            commandList.set(i, StringUtils.replace(commandList.get(i), getReplaceIndex(index), name));
        }
    }
}