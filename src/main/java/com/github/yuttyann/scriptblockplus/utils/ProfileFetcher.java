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

	private static final String PROFILE_UUID_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";

	private static final JSONParser JSON_PARSER = new JSONParser();

	private static final Map<UUID, String> CACHE = new HashMap<>();

	public static String getName(UUID uuid) throws ProtocolException, ParseException, IOException {
		if (uuid == null) {
			return null;
		}
		String name = CACHE.get(uuid);
		if (name == null) {
			JSONObject json = getJsonObject(PROFILE_UUID_URL + StringUtils.replace(uuid.toString(), "-", ""));
			String errorMessage = (String) json.get("errorMessage");
			if (StringUtils.isNotEmpty(errorMessage)) {
				throw new IllegalStateException(errorMessage);
			}
			CACHE.put(uuid, name = (String) json.get("name"));
		}
		return name;
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