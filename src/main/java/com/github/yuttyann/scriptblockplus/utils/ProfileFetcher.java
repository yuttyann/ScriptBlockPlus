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

public class ProfileFetcher {

	private static final String PROFILE_NAME_URL = "https://api.mojang.com/users/profiles/minecraft/";
	private static final String PROFILE_UUID_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";

	private static final JSONParser JSON_PARSER = new JSONParser();

	private static final Map<String, UUID> UUID_CACHE_MAP = new HashMap<String, UUID>();
	private static final Map<UUID, String> NAME_CACHE_MAP = new HashMap<UUID, String>();

	public static UUID getUniqueId(String name) throws Exception {
		UUID uuid = UUID_CACHE_MAP.get(name);
		if (uuid == null) {
			String url = PROFILE_NAME_URL + name;
			JSONObject json = getJsonObject(url);
			String errorMessage = (String) json.get("errorMessage");
			if (StringUtils.isNotEmpty(errorMessage)) {
				throw new IllegalStateException(errorMessage);
			}
			uuid = fromString((String) json.get("id"));
			UUID_CACHE_MAP.put(name, uuid);
		}
		return uuid;
	}

	public static String getName(UUID uuid) throws Exception {
		String name = NAME_CACHE_MAP.get(uuid);
		if (name == null) {
			String url = PROFILE_UUID_URL + StringUtils.replace(uuid.toString(), "-", "");
			JSONObject json = getJsonObject(url);
			String errorMessage = (String) json.get("errorMessage");
			if (StringUtils.isNotEmpty(errorMessage)) {
				throw new IllegalStateException(errorMessage);
			}
			name = (String) json.get("name");
			NAME_CACHE_MAP.put(uuid, name);
		}
		return name;
	}

	private static UUID fromString(String uuid) {
		return UUID.fromString(uuid.substring(0, 8) + "-" + uuid.substring(8, 12) + "-" + uuid.substring(12, 16) + "-" + uuid.substring(16, 20) + "-" + uuid.substring(20, 32));
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
			if (httpStatusCode != HttpURLConnection.HTTP_OK) {
				connection.disconnect();
				return null;
			}
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line = reader.readLine();
			return StringUtils.isNotEmpty(line) ? (JSONObject) JSON_PARSER.parse(line) : null;
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
	}
}