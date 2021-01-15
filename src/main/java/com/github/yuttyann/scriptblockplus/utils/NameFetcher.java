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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * ScriptBlockPlus NameFetcher クラス
 * @author yuttyann44581
 */
public final class NameFetcher {

    private static final String URL = "https://sessionserver.mojang.com/session/minecraft/profile/";

    private static final Map<UUID, String> CACHE = new HashMap<>();

    public static void clear() {
        CACHE.clear();
    }

    @NotNull
    public static String getName(@NotNull UUID uuid) throws IOException {
        var name = CACHE.get(uuid);
        if (name == null) {
            var json = getJsonObject(URL + StringUtils.replace(uuid.toString(), "-", ""));
            var error = json.get("errorMessage").getAsString();
            if (StringUtils.isNotEmpty(error)) {
                throw new IllegalStateException(error);
            }
            CACHE.put(uuid, name = json.get("name").getAsString());
        }
        return name;
    }

    @Nullable
    public static JsonObject getJsonObject(@NotNull String url) throws IOException {
        var webFile = FileUtils.getWebFile(url);
        if (webFile == null) {
            return null;
        }
        try (var reader = new BufferedReader(new InputStreamReader(webFile))) {
            var line = reader.readLine();
            return StringUtils.isEmpty(line) ? null : new Gson().fromJson(line, JsonObject.class);
        }
    }
}