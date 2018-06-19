package com.github.yuttyann.scriptblockplus.script.option.time;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import com.github.yuttyann.scriptblockplus.enums.ScriptType;

public final class TimeData {

	int index;
	int second;
	boolean isOldCooldown;

	UUID uuid;
	String fullCoords;
	ScriptType scriptType;

	TimeData(int index, int second, boolean isOldCooldown) {
		this.second = second;
		this.isOldCooldown = isOldCooldown;
	}

	public int getSecond() {
		return second;
	}

	public boolean isOldCooldown() {
		return isOldCooldown;
	}

	public UUID getUniqueId() {
		return uuid;
	}

	public String getFullCoords() {
		return fullCoords;
	}

	public ScriptType getScriptType() {
		return scriptType;
	}

	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("index", index);
		map.put("second", second);
		map.put("isoldcooldown", isOldCooldown);
		map.put("fullcoords", fullCoords);
		if (isOldCooldown) {
			map.put("uuid", uuid);
			map.put("scripttype", scriptType);
		}
		return map;
	}

	public static void deserialize(Map<String, Object> map) {
		int index = (int) map.get("index");
		int second = (int) map.get("second");
		boolean isOldCooldown = (boolean) map.get("isoldcooldown");
		TimeData timeData = new TimeData(index, second, isOldCooldown);
		timeData.fullCoords = (String) map.get("fullcoords");
		if (timeData.isOldCooldown) {
			new OldCooldown().deserialize(timeData);
		} else {
			timeData.uuid = (UUID) map.get("uuid");
			timeData.scriptType = (ScriptType) map.get("scripttype");
			new Cooldown().deserialize(timeData);
		}
	}

	@Override
	public int hashCode() {
		return isOldCooldown ? Objects.hash(index, fullCoords) : Objects.hash(index, uuid, fullCoords, scriptType);
	}
}