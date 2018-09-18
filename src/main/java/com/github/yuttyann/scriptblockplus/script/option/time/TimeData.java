package com.github.yuttyann.scriptblockplus.script.option.time;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.github.yuttyann.scriptblockplus.script.ScriptType;

public final class TimeData {

	int index;
	int second;
	boolean isOld;

	UUID uuid;
	String fullCoords;
	ScriptType scriptType;

	TimeData(int index, int second, boolean isOldCooldown) {
		this.second = second;
		this.isOld = isOldCooldown;
	}

	public int getSecond() {
		return second;
	}

	public boolean isOld() {
		return isOld;
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
		map.put("isold", isOld);
		map.put("fullcoords", fullCoords);
		if (!isOld) {
			map.put("uuid", uuid);
			map.put("scripttype", scriptType);
		}
		return map;
	}

	public static void deserialize(Map<String, Object> args) {
		int index = (int) args.get("index");
		int second = (int) args.get("second");
		boolean isOld = (boolean) args.get("isold");
		TimeData timeData = new TimeData(index, second, isOld);
		timeData.fullCoords = (String) args.get("fullcoords");
		if (timeData.isOld) {
			new OldCooldown().deserialize(timeData);
		} else {
			timeData.uuid = (UUID) args.get("uuid");
			timeData.scriptType = (ScriptType) args.get("scripttype");
			new Cooldown().deserialize(timeData);
		}
	}

	@Override
	public int hashCode() {
        if (isOld) {
        	return hashCode(index, fullCoords);
        } else {
        	return hashCode(index, uuid, fullCoords, scriptType);
        }
	}

	static int hashCode(int index, String fullCoords) {
		int hash = 1;
    	hash = hash * 31 + index;
    	hash = hash * 31 + fullCoords.hashCode();
		return hash;
	}

	static int hashCode(int index, UUID uuid, String fullCoords, ScriptType scriptType) {
		int hash = 1;
    	hash = hash * 31 + index;
    	hash = hash * 31 + uuid.hashCode();
    	hash = hash * 31 + fullCoords.hashCode();
    	hash = hash * 31 + scriptType.hashCode();
		return hash;
	}
}