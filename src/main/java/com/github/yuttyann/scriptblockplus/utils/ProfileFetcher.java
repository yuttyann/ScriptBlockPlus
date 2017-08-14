package com.github.yuttyann.scriptblockplus.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ProfileFetcher {

	private static final String PROFILE_NAME_URL = "https://api.mojang.com/users/profiles/minecraft/";
	private static final String PROFILE_UUID_URL = "https://api.mojang.com/user/profiles/";

	private static final JSONParser JSON_PARSER = new JSONParser();

	public static UUID getUniqueId(String name) throws Exception {
		String url = PROFILE_NAME_URL + name;
		JSONObject jsonObject = (JSONObject) JSON_PARSER.parse(getJsonString(url));
		return fromString(jsonObject.get("id").toString());
	}

	public static String getName(UUID uuid) throws Exception {
		String url = PROFILE_UUID_URL + StringUtils.replace(uuid.toString(), "-", "") + "/names";
		JSONArray jsonArray = (JSONArray) JSON_PARSER.parse(getJsonString(url));
		return ((JSONObject) JSON_PARSER.parse(jsonArray.get(jsonArray.size() - 1).toString())).get("name").toString();
	}

	private static UUID fromString(String uuid) {
		return UUID.fromString(uuid.substring(0, 8) + "-" + uuid.substring(8, 12) + "-" + uuid.substring(12, 16) + "-" + uuid.substring(16, 20) + "-" + uuid.substring(20, 32));
	}

	private static String getJsonString(String url) {
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