package com.github.yuttyann.scriptblockplus.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class NameFetcher implements Callable<String> {

	private static final String PROFILE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";
	private static final JSONParser JSON_PARSER = new JSONParser();

	private final String url;

	private NameFetcher(UUID uuid) {
		this.url = PROFILE_URL + StringUtils.replace(uuid.toString(), "-", null);
	}

	public static String getName(UUID uuid) throws Exception {
		return new NameFetcher(uuid).call();
	}

	@Override
	public String call() throws Exception {
		JSONObject json = (JSONObject) getJson(url);
		String cause = (String) json.get("cause");
		if (cause != null && cause.length() > 0) {
			throw new IllegalStateException((String) json.get("errorMessage"));
		}
		return (String) json.get("name");
	}

	private Object getJson(String url) throws ParseException, IOException {
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