package com.github.yuttyann.scriptblockplus.player;

import com.github.yuttyann.scriptblockplus.region.CuboidRegion;
import com.github.yuttyann.scriptblockplus.region.Region;
import com.github.yuttyann.scriptblockplus.script.SBClipboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class PlayerData implements SBPlayer {

	private static final String KEY_REGION = createRandomId("CuboidRegion");
	private static final String KEY_CLIPBOARD = createRandomId("ClipBoard");
	private static final String KEY_SCRIPTLINE = createRandomId("ScriptLine");
	private static final String KEY_CLICKACTION = createRandomId("ClickAction");
	private static final String KEY_PLAYERCOUNT = createRandomId("PlayerCount");
	private static final String KEY_OLDFULLCOORDS = createRandomId("OldFullCoords");


	private final ObjectMap objectMap = new ObjMap();

	PlayerData() { }

	@NotNull
	@Override
	public Region getRegion() {
		CuboidRegion region = getObjectMap().get(KEY_REGION);
		if (region == null) {
			getObjectMap().put(KEY_REGION, region = new CuboidRegion());
		}
		return region;
	}

	@NotNull
	@Override
	public PlayerCount getPlayerCount() {
		PlayerCount playerCount = getObjectMap().get(KEY_PLAYERCOUNT);
		if (playerCount == null) {
			getObjectMap().put(KEY_PLAYERCOUNT, playerCount = new PlayerCount(getUniqueId()));
		}
		return playerCount;
	}

	@NotNull
	@Override
	public ObjectMap getObjectMap() {
		return objectMap;
	}

	private class ObjMap implements ObjectMap {

		private final Map<String, Object> objectMap;

		private ObjMap() {
			this.objectMap = new HashMap<>();
		}

		@Override
		public void put(@NotNull String key, @Nullable Object value) {
			objectMap.put(key, value);
		}

		@Nullable
		@Override
		public <T> T get(@NotNull String key) {
			return (T) objectMap.get(key);
		}

		@Nullable
		@Override
		public <T> T remove(@NotNull String key) {
			return (T) objectMap.remove(key);
		}

		@Override
		public boolean containsKey(@NotNull String key) {
			return objectMap.containsKey(key);
		}

		@Override
		public boolean containsValue(@NotNull Object value) {
			return objectMap.containsValue(value);
		}

		@Override
		public void clear() {
			objectMap.clear();
		}
	}

	@Override
	public void setClipboard(@Nullable SBClipboard clipboard) {
		getObjectMap().put(KEY_CLIPBOARD, clipboard);
	}

	@Nullable
	@Override
	public SBClipboard getClipboard() {
		return getObjectMap().get(KEY_CLIPBOARD);
	}

	@Override
	public boolean hasClipboard() {
		return getObjectMap().has(KEY_CLIPBOARD);
	}

	@Override
	public void setScriptLine(@Nullable String scriptLine) {
		getObjectMap().put(KEY_SCRIPTLINE, scriptLine);
	}

	@Nullable
	@Override
	public String getScriptLine() {
		return getObjectMap().getString(KEY_SCRIPTLINE);
	}

	@Override
	public boolean hasScriptLine() {
		return getObjectMap().has(KEY_SCRIPTLINE);
	}

	@Override
	public void setActionType(@Nullable String actionType) {
		getObjectMap().put(KEY_CLICKACTION, actionType);
	}

	@Nullable
	@Override
	public String getActionType() {
		return getObjectMap().getString(KEY_CLICKACTION);
	}

	@Override
	public boolean hasActionType() {
		return getObjectMap().has(KEY_CLICKACTION);
	}

	@Override
	public void setOldFullCoords(@Nullable String fullCoords) {
		getObjectMap().put(KEY_OLDFULLCOORDS, fullCoords);
	}

	@Nullable
	@Override
	public String getOldFullCoords() {
		return getObjectMap().getString(KEY_OLDFULLCOORDS);
	}

	@Override
	public boolean hasOldFullCoords() {
		return getObjectMap().has(KEY_OLDFULLCOORDS);
	}

	public static String createRandomId(@NotNull String key) {
		return key + "_" + UUID.randomUUID();
	}
}