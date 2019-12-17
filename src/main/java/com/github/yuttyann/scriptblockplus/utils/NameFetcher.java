package com.github.yuttyann.scriptblockplus.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ProtocolException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public final class NameFetcher {

	private static final String URL = "https://sessionserver.mojang.com/session/minecraft/profile/";

	private static final Map<UUID, String> CACHE = new HashMap<>();

	public static String getName(UUID uuid) throws ProtocolException, IOException {
		if (uuid == null) {
			return null;
		}
		String name = CACHE.get(uuid);
		if (name == null) {
			JsonObject json = getJsonObject(URL + StringUtils.replace(uuid.toString(), "-", ""));
			String errorMessage = json.get("errorMessage").getAsString();
			if (StringUtils.isNotEmpty(errorMessage)) {
				throw new IllegalStateException(errorMessage);
			}
			CACHE.put(uuid, name = json.get("name").getAsString());
		}
		return name;
	}

	public static JsonObject getJsonObject(String url) throws ProtocolException, IOException {
		try (InputStream is = FileUtils.getWebFile(url); InputStreamReader isr = new InputStreamReader(is); BufferedReader reader = new BufferedReader(isr)) {
			String line = reader.readLine();
			return StringUtils.isNotEmpty(line) ? (JsonObject) new Gson().fromJson(line, JsonObject.class) : null;
		} catch (ProtocolException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		}
	}
}