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

public class NameFetcher {

	private static final String PROFILE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";
	private static final JSONParser JSON_PARSER = new JSONParser();

	private static Map<UUID, String> cacheMap = new HashMap<UUID, String>();

	private final String url;

	private NameFetcher(UUID uuid) {
		this.url = PROFILE_URL + StringUtils.replace(uuid.toString(), '-', '\0');
	}

	public void init() {
		cacheMap = new HashMap<UUID, String>();
	}

	public static String getName(UUID uuid) throws ParseException, IOException {
		String name = cacheMap.get(uuid);
		if (name == null) {
			JSONObject json = (JSONObject) getJson(new NameFetcher(uuid).url);
			String cause = (String) json.get("cause");
			if (cause != null && cause.length() > 0) {
				throw new IllegalStateException((String) json.get("errorMessage"));
			}
			name = (String) json.get("name");
			cacheMap.put(uuid, name);
		}
		return name;
	}

	private static Object getJson(String url) throws ParseException, IOException {
		BufferedReader reader = null;
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("GET");
			connection.setInstanceFollowRedirects(false);
			connection.connect();
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder builder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
			return builder.length() == 0 ? null : JSON_PARSER.parse(builder.toString());
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