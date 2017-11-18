package com.github.yuttyann.scriptblockplus.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class ProfileFetcher {

	private static final String PROFILE_NAME_URL = "https://api.mojang.com/users/profiles/minecraft/";
	private static final String PROFILE_UUID_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";

	private static final Gson GSON = new Gson();

	private static final Map<String, UUID> UUID_CACHE_MAP = new HashMap<String, UUID>();
	private static final Map<UUID, String> NAME_CACHE_MAP = new HashMap<UUID, String>();

	public static UUID getUniqueId(String name) throws Exception {
		UUID uuid = UUID_CACHE_MAP.get(name);
		if (uuid == null) {
			String url = PROFILE_NAME_URL + name;
			JsonObject json = getJsonObject(url);
			String errorMessage = json.get("errorMessage").getAsString();
			if (StringUtils.isNotEmpty(errorMessage)) {
				throw new IllegalStateException(errorMessage);
			}
			uuid = fromString(json.get("id").getAsString());
			UUID_CACHE_MAP.put(name, uuid);
		}
		return uuid;
	}

	public static String getName(UUID uuid) throws Exception {
		String name = NAME_CACHE_MAP.get(uuid);
		if (name == null) {
			String url = PROFILE_UUID_URL + StringUtils.replace(uuid.toString(), "-", "");
			JsonObject json = getJsonObject(url);
			String errorMessage = json.get("errorMessage").getAsString();
			if (StringUtils.isNotEmpty(errorMessage)) {
				throw new IllegalStateException(errorMessage);
			}
			name = json.get("name").getAsString();
			NAME_CACHE_MAP.put(uuid, name);
		}
		return name;
	}

	private static UUID fromString(String uuid) {
		return UUID.fromString(uuid.substring(0, 8) + "-" + uuid.substring(8, 12) + "-" + uuid.substring(12, 16) + "-" + uuid.substring(16, 20) + "-" + uuid.substring(20, 32));
	}

	public static JsonObject getJsonObject(String url) throws JsonSyntaxException, ProtocolException, IOException {
		BufferedReader reader = null;
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("GET");
			connection.setAllowUserInteraction(false);
			connection.setInstanceFollowRedirects(true);
			connection.connect();
			int httpStatusCode = connection.getResponseCode();
			if (httpStatusCode != HttpURLConnection.HTTP_OK) {
				connection.disconnect();
				return null;
			}
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder builder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
			return GSON.fromJson(builder.toString(), JsonObject.class);
		} catch (JsonSyntaxException e) {
			throw e;
		} catch (ProtocolException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}