package com.github.yuttyann.scriptblockplus.player;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.region.CuboidRegion;
import com.github.yuttyann.scriptblockplus.region.Region;
import com.github.yuttyann.scriptblockplus.script.SBClipboard;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * ScriptBlockPlus PlayerData クラス
 * @author yuttyann44581
 */
public abstract class PlayerMap implements SBPlayer {

	private static final String KEY_REGION = Utils.randomUUID();
	private static final String KEY_CLIPBOARD = Utils.randomUUID();
	private static final String KEY_SCRIPTLINE = Utils.randomUUID();
	private static final String KEY_CLICKACTION = Utils.randomUUID();
	private static final String KEY_OLDBLOCKCOORDS = Utils.randomUUID();

	private final ObjectMap objectMap;

	protected PlayerMap() {
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
	public void setOldBlockCoords(@Nullable BlockCoords blockCoords) {
		getObjectMap().put(KEY_OLDBLOCKCOORDS, blockCoords);
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
	public Optional<BlockCoords> getOldBlockCoords() {
		return Optional.ofNullable(getObjectMap().get(KEY_OLDBLOCKCOORDS));
	}
}