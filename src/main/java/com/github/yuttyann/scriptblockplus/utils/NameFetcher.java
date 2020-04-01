package com.github.yuttyann.scriptblockplus.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.lang.Validate;
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
		Validate.notNull(uuid, "Command cannot be null");
		String name = CACHE.get(uuid);
		if (name == null) {
			JsonObject json = getJsonObject(URL + StringUtils.replace(uuid.toString(), "-", ""));
			String errorMessage = Objects.requireNonNull(json).get("errorMessage").getAsString();
			if (StringUtils.isNotEmpty(errorMessage)) {
				throw new IllegalStateException(errorMessage);
			}
			CACHE.put(uuid, name = json.get("name").getAsString());
		}
		return name;
	}

	@Nullable
	public static JsonObject getJsonObject(@NotNull String url) throws IOException {
		InputStream is = FileUtils.getWebFile(url);
		if (is == null) {
			return null;
		}
		try (InputStreamReader isr = new InputStreamReader(is); BufferedReader reader = new BufferedReader(isr)) {
			String line = reader.readLine();
			return StringUtils.isNotEmpty(line) ? new Gson().fromJson(line, JsonObject.class) : null;
		}
	}
}