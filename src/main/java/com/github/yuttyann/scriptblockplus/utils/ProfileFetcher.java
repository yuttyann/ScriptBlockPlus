package com.github.yuttyann.scriptblockplus.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ProtocolException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public final class ProfileFetcher {

	private static final String PROFILE_UUID_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";

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
		try (InputStream is = FileUtils.getWebFile(url); InputStreamReader isr = new InputStreamReader(is); BufferedReader reader = new BufferedReader(isr)) {
			String line = reader.readLine();
			return StringUtils.isNotEmpty(line) ? (JSONObject) new JSONParser().parse(line) : null;
		} catch (ParseException e) {
			throw e;
		} catch (ProtocolException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		}
	}
}