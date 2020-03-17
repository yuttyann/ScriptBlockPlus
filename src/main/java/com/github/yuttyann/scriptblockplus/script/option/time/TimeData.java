package com.github.yuttyann.scriptblockplus.script.option.time;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class TimeData {

	protected int second;

	private final int scriptIndex;
	private final boolean isOldCooldown;
	private final UUID uuid;
	private final String fullCoords;
	private final ScriptType scriptType;

	TimeData(int second, int scriptIndex, @NotNull String fullCoords) {
		this(second, scriptIndex, true, fullCoords, null, null);
	}

	TimeData(int second, int scriptIndex, @NotNull String fullCoords, @Nullable UUID uuid, @Nullable ScriptType scriptType) {
		this(second, scriptIndex, false, fullCoords, uuid, scriptType);
	}

	private TimeData(int second, int scriptIndex, boolean isOldCooldown, @NotNull String fullCoords, @Nullable UUID uuid, @Nullable ScriptType scriptType) {
		this.second = second;
		this.scriptIndex = scriptIndex;
		this.isOldCooldown = isOldCooldown;
		this.fullCoords = fullCoords;
		this.uuid = uuid;
		this.scriptType = scriptType;
	}

	public int getSecond() {
		return second;
	}

	public int getScriptIndex() {
		return scriptIndex;
	}

	public boolean isOldCooldown() {
		return isOldCooldown;
	}

	@Nullable
	public UUID getUniqueId() {
		return uuid;
	}

	@NotNull
	public String getFullCoords() {
		return fullCoords;
	}

	@Nullable
	public ScriptType getScriptType() {
		return scriptType;
	}

	@NotNull
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("second", second);
		map.put("scriptindex", scriptIndex);
		map.put("isoldcooldown", isOldCooldown);
		map.put("fullcoords", fullCoords);
		if (!isOldCooldown) {
			map.put("uuid", uuid);
			map.put("scripttype", scriptType);
		}
		return map;
	}

	public static void deserialize(@NotNull Map<String, Object> args) {
		int second = (int) args.get("second");
		int scriptIndex = (int) args.get("scriptindex");
		boolean isOldCooldown = (boolean) args.get("isoldcooldown");
		String fullCoords = (String) args.get("fullcoords");
		UUID uuid = (UUID) args.get("uuid");
		ScriptType scriptType = (ScriptType) args.get("scripttype");
		TimeData timeData = new TimeData(second, scriptIndex, isOldCooldown, fullCoords, uuid, scriptType);
		if (timeData.isOldCooldown) {
			new OldCooldown().deserialize(timeData);
		} else {
			new Cooldown().deserialize(timeData);
		}
	}

	@Override
	public int hashCode() {
		return hashCode(scriptIndex, isOldCooldown, fullCoords, uuid, scriptType);
	}

	static int hashCode(int scriptIndex, boolean isOldCooldown, @NotNull String fullCoords, @Nullable UUID uuid, @Nullable ScriptType scriptType) {
		int hash = 1;
		int prime = 31;
		hash = prime * hash + scriptIndex;
		hash = prime * hash + Boolean.hashCode(isOldCooldown);
		hash = prime * hash + Objects.requireNonNull(fullCoords).hashCode();
		hash = uuid == null ? hash : prime * hash + uuid.hashCode();
		hash = scriptType == null ? hash : prime * hash + scriptType.hashCode();
		return hash;
	}

	static int getSecond(int hash) {
		for (TimeData timeData : ScriptBlock.getInstance().getMapManager().getCooldowns()) {
			if (hash == timeData.hashCode()) {
				return timeData.second;
			}
		}
		return -1;
	}
}