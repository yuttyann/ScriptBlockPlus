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

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public final class ProfileFetcher {

	private static final String PROFILE_NAME_URL = "https://api.mojang.com/users/profiles/minecraft/";
	private static final String PROFILE_UUID_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";

	private static final JSONParser JSON_PARSER = new JSONParser();

	private static final Map<String, UUID> UUID_CACHE_MAP = new HashMap<>();
	private static final Map<UUID, String> NAME_CACHE_MAP = new HashMap<>();

	private static final int[] UUID_INDEX = { 8, 12, 16, 20, 32 };

	public static UUID getUniqueId(String name) throws ProtocolException, ParseException, IOException {
		UUID uuid = UUID_CACHE_MAP.get(name);
		if (uuid == null) {
			JSONObject json = getJsonObject(PROFILE_NAME_URL + name);
			String errorMessage = (String) json.get("errorMessage");
			if (StringUtils.isNotEmpty(errorMessage)) {
				throw new IllegalStateException(errorMessage);
			}
			uuid = fromString((String) json.get("id"));
			UUID_CACHE_MAP.put(name, uuid);
		}
		return uuid;
	}

	public static String getName(UUID uuid) throws ProtocolException, ParseException, IOException {
		String name = NAME_CACHE_MAP.get(uuid);
		if (name == null) {
			JSONObject json = getJsonObject(PROFILE_UUID_URL + StringUtils.replace(uuid.toString(), "-", ""));
			String errorMessage = (String) json.get("errorMessage");
			if (StringUtils.isNotEmpty(errorMessage)) {
				throw new IllegalStateException(errorMessage);
			}
			name = (String) json.get("name");
			NAME_CACHE_MAP.put(uuid, name);
		}
		return name;
	}

	public static UUID fromString(String uuid) {
		String result = "";
		for (int i = 0, j = 0; i < UUID_INDEX.length; i++) {
			result += uuid.substring(j, j = UUID_INDEX[i]) + (UUID_INDEX[i] == 32 ? "" : "-");
		}
		return UUID.fromString(result);
	}

	public static JSONObject getJsonObject(String url) throws ParseException, ProtocolException, IOException {
		BufferedReader reader = null;
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("GET");
			connection.setAllowUserInteraction(false);
			connection.setInstanceFollowRedirects(true);
			connection.connect();
			int httpStatusCode = connection.getResponseCode();
			if (httpStatusCode == HttpURLConnection.HTTP_OK) {
				reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String line = reader.readLine();
				return StringUtils.isNotEmpty(line) ? (JSONObject) JSON_PARSER.parse(line) : null;
			}
			connection.disconnect();
		} catch (ParseException e) {
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
		return null;
	}
}