package com.github.yuttyann.scriptblockplus.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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
            var error = Objects.requireNonNull(json).get("errorMessage").getAsString();
            if (StringUtils.isNotEmpty(error)) {
                throw new IllegalStateException(error);
            }
            CACHE.put(uuid, name = json.get("name").getAsString());
        }
        return name;
    }

    @Nullable
    public static JsonObject getJsonObject(@NotNull String url) throws IOException {
        InputStream webFile = FileUtils.getWebFile(url);
        if (webFile == null) {
            return null;
        }
        try (var reader = new BufferedReader(new InputStreamReader(webFile))) {
            String line = reader.readLine();
            return StringUtils.isNotEmpty(line) ? new Gson().fromJson(line, JsonObject.class) : null;
        }
    }
}