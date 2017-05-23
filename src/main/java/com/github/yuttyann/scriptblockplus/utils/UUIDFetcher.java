package com.github.yuttyann.scriptblockplus.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class UUIDFetcher implements Callable<Map<String, UUID>> {

	private static final String PROFILE_URL = "https://api.mojang.com/users/profiles/minecraft/";

	private Map<Object, Object> jsonMap;

	private UUIDFetcher(String name) {
		try {
			this.jsonMap = getJsonMap(getJsonString(PROFILE_URL + name));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Map<String, UUID> call() throws Exception {
		Map<String, UUID> uuidMap = new HashMap<String, UUID>();
		if (jsonMap != null) {
			String id = jsonMap.get("id").toString();
			String name = jsonMap.get("name").toString();
			uuidMap.put(name, Utils.uuidFromString(id));
		}
		return uuidMap;
	}

	public static UUID getUniqueId(String name) throws Exception {
		return new UUIDFetcher(name).call().get(name);
	}

	@SuppressWarnings("unchecked")
	private Map<Object, Object> getJsonMap(String json) throws ParseException {
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = (JSONObject) jsonParser.parse(json);
		Map<Object, Object> objectMap = new HashMap<Object, Object>();
		Set<Entry<Object, Object>> entrySet = jsonObject.entrySet();
		for(Entry<Object, Object> entry : entrySet) {
			objectMap.put(entry.getKey(), entry.getValue());
		}
		return objectMap;
	}

	private String getJsonString(String url) {
		InputStream input = null;
		InputStreamReader inReader = null;
		BufferedReader buReader = null;
		try {
			URLConnection urlconn = new URL(url).openConnection();
			HttpURLConnection httpconn = (HttpURLConnection) urlconn;
			httpconn.setRequestMethod("GET");
			httpconn.setInstanceFollowRedirects(false);
			httpconn.connect();
			input = httpconn.getInputStream();
			inReader = new InputStreamReader(input);
			buReader = new BufferedReader(inReader);
			String line;
			StringBuilder builder = new StringBuilder();
			while ((line = buReader.readLine()) != null) {
				builder.append(line);
			}
			return builder.toString();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (buReader != null) {
				try {
					buReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (inReader != null) {
				try {
					inReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
}