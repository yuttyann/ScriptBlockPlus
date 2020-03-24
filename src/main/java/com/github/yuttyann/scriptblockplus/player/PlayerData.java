package com.github.yuttyann.scriptblockplus.player;

import com.github.yuttyann.scriptblockplus.region.CuboidRegion;
import com.github.yuttyann.scriptblockplus.region.Region;
import com.github.yuttyann.scriptblockplus.script.SBClipboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public abstract class PlayerData implements SBPlayer {

	private static final String KEY_REGION = createRandomId("CuboidRegion");
	private static final String KEY_CLIPBOARD = createRandomId("ClipBoard");
	private static final String KEY_SCRIPTLINE = createRandomId("ScriptLine");
	private static final String KEY_CLICKACTION = createRandomId("ClickAction");
	private static final String KEY_PLAYERCOUNT = createRandomId("PlayerCount");
	private static final String KEY_OLDFULLCOORDS = createRandomId("OldFullCoords");

	private final ObjectMap objectMap;

	PlayerData() {
		this.objectMap = new ObjMap();
	}

	@NotNull
	@Override
	public final ObjectMap getObjectMap() {
		return objectMap;
	}

	private static class ObjMap implements ObjectMap {

		private final Map<String, Object> objectMap;

		private ObjMap() {
			this.objectMap = new HashMap<>();
		}

		@Override
		public void put(@NotNull String key, @Nullable Object value) {
			objectMap.put(key, value);
		}

		@SuppressWarnings("unchecked")
		@Override
		@Nullable
		public <T> T get(@NotNull String key) {
			return (T) objectMap.get(key);
		}

		@Override
		public void remove(@NotNull String key) {
			objectMap.remove(key);
		}

		@Override
		public void clear() {
			objectMap.clear();
		}
	}

	@Override
	@NotNull
	public Region getRegion() {
		CuboidRegion region = getObjectMap().get(KEY_REGION);
		if (region == null) {
			getObjectMap().put(KEY_REGION, region = new CuboidRegion());
		}
		return region;
	}

	@Override
	@NotNull
	public PlayerCount getPlayerCount() {
		PlayerCount playerCount = getObjectMap().get(KEY_PLAYERCOUNT);
		if (playerCount == null) {
			getObjectMap().put(KEY_PLAYERCOUNT, playerCount = new PlayerCount(getUniqueId()));
		}
		return playerCount;
	}

	@Override
	public void setClipboard(@Nullable SBClipboard clipboard) {
		getObjectMap().put(KEY_CLIPBOARD, clipboard);
	}

	@Override
	public void setScriptLine(@Nullable String scriptLine) {
		getObjectMap().put(KEY_SCRIPTLINE, scriptLine);
	}

	@Override
	public void setActionType(@Nullable String actionType) {
		getObjectMap().put(KEY_CLICKACTION, actionType);
	}

	@Override
	public void setOldFullCoords(@Nullable String fullCoords) {
		getObjectMap().put(KEY_OLDFULLCOORDS, fullCoords);
	}

	@Override
	@NotNull
	public Optional<SBClipboard> getClipboard() {
		return Optional.ofNullable(getObjectMap().get(KEY_CLIPBOARD));
	}

	@Override
	@NotNull
	public Optional<String> getScriptLine() {
		return Optional.ofNullable(getObjectMap().get(KEY_SCRIPTLINE));
	}

	@Override
	@NotNull
	public Optional<String> getActionType() {
		return Optional.ofNullable(getObjectMap().get(KEY_CLICKACTION));
	}

	@Override
	@NotNull
	public Optional<String> getOldFullCoords() {
		return Optional.ofNullable(getObjectMap().get(KEY_OLDFULLCOORDS));
	}

	public static String createRandomId(@NotNull String key) {
		return key + "_" + UUID.randomUUID();
	}
}