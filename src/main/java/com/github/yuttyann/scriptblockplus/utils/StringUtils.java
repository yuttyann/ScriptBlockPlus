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
package com.github.yuttyann.scriptblockplus.utils;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.IntStream;

/**
 * ScriptBlockPlus StringUtils クラス
 * @author yuttyann44581
 */
public final class StringUtils {

    private static final Random RANDOM = new Random();

    @NotNull
    public static List<String> parseScript(@NotNull String source) throws IllegalArgumentException {
        int length = source.length();
        if (source.charAt(0) != '[' || source.charAt(length - 1) != ']') {
            return Collections.singletonList(source);
        }
        int start = 0, end = 0;
        var chars = source.toCharArray();
        var parse = new ArrayList<String>(4);
        for (int i = 0, j = 0, k = 0; i < length; i++) {
            if (chars[i] == '[') {
                start++;
                if (j++ == 0) {
                    k = i;
                }
            } else if (chars[i] == ']') {
                end++;
                if (--j == 0) {
                    parse.add(source.substring(k + 1, i));
                }
            }
        }
        if (start != end) {
            throw new IllegalArgumentException("Failed to load the script");
        }
        return parse;
    }

    @NotNull
    public static String[] split(@Nullable String source, @NotNull char delimiter) {
        if (isEmpty(source)) {
            return ArrayUtils.EMPTY_STRING_ARRAY;   
        }
        var list = new ArrayList<String>();
        var chars = source.toCharArray();
        var match = false;
        int start = 0;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == delimiter) {
                if (!match) {
                    list.add(source.substring(start, i));
                    match = true;
                }
                start = i + 1;
            } else {
                match = false;
            }
        }
        if (!match) {
            if (list.size() == 0) {
                return new String[] { source };
            }
            list.add(source.substring(start, chars.length));
        }
        return list.toArray(String[]::new);
    }

    @NotNull
    public static String replace(@Nullable String source, @NotNull String search, @Nullable Object replace) {
        return isEmpty(source) ? "" : source.replace(search, replace == null ? "" : replace.toString());
    }

    @NotNull
    public static String setColor(@Nullable String source) {
        source = isEmpty(source) ? "" : ChatColor.translateAlternateColorCodes('&', source);
        return replace(source, "§rc", ChatColor.getByChar(Integer.toHexString(RANDOM.nextInt(16))));
    }

    @NotNull
    public static List<String> setListColor(@NotNull List<String> list) {
        list = new ArrayList<>(list);
        list.replaceAll(StringUtils::setColor);
        return list;
    }

    @NotNull
    public static String getColors(@NotNull String source) {
        var chars = source.toCharArray();
        var builder = new StringBuilder(chars.length);
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] != '§' || (i + 1) >= chars.length) {
                continue;
            }
            if (ChatColor.getByChar(Character.toLowerCase(chars[i + 1])) != null) {
                builder.append(chars[i++]).append(chars[i]);
            }
        }
        return builder.toString();
    }

    @NotNull
    public static String createString(@NotNull String[] args, int start) {
        var joiner = new StringJoiner(" ");
        IntStream.range(start, args.length).forEach(i -> joiner.add(args[i]));
        return joiner.toString();
    }

    @NotNull
    public static String removeStart(@NotNull String source, @NotNull String prefix) {
        if (isNotEmpty(source) && isNotEmpty(prefix) & source.startsWith(prefix)) {
            return source.substring(prefix.length());
        }
        return source;
    }

    public static String removeEnd(@NotNull String source, @NotNull String suffix) {
        if (isNotEmpty(source) && isNotEmpty(suffix) && source.endsWith(suffix)) {
            return source.substring(0, source.length() - suffix.length());
        }
        return source;
    }

    public static boolean isNotEmpty(@Nullable String source) {
        return !isEmpty(source);
    }

    public static boolean isEmpty(@Nullable String source) {
        return source == null || source.length() == 0;
    }
}