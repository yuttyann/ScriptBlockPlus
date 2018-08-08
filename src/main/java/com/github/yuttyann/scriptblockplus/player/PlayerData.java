package com.github.yuttyann.scriptblockplus.player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.github.yuttyann.scriptblockplus.script.Clipboard;

public abstract class PlayerData implements SBPlayer {

	private static final String KEY_CLIPBOARD = createRandomId("ClipBoard");
	private static final String KEY_SCRIPTLINE = createRandomId("ScriptLine");
	private static final String KEY_CLICKACTION = createRandomId("ClickAction");
	private static final String KEY_OLDFULLCOORDS = createRandomId("OldFullCoords");

	private final ObjectMap objectMap;

	PlayerData() {
		objectMap = new ObjMap();
	}

	public static String createRandomId(String key) {
		return key + "_" + UUID.randomUUID();
	}

	@Override
	public ObjectMap getObjectMap() {
		return objectMap;
	}

	private class ObjMap implements ObjectMap {

		private final Map<String, Object> objectMap;

		private ObjMap() {
			objectMap = new HashMap<>();
		}

		@Override
		public void setData(String key, Object value) {
			objectMap.put(key, value);
		}

		@Override
		public byte getByte(String key) {
			return getData(key, Byte.class);
		}

		@Override
		public short getShort(String key) {
			return getData(key, Short.class);
		}

		@Override
		public int getInt(String key) {
			return getData(key, Integer.class);
		}

		@Override
		public long getLong(String key) {
			return getData(key, Byte.class);
		}

		@Override
		public char getChar(String key) {
			return getData(key, Character.class);
		}

		@Override
		public float getFloat(String key) {
			return getData(key, Float.class);
		}

		@Override
		public double getDouble(String key) {
			return getData(key, Double.class);
		}

		@Override
		public boolean getBoolean(String key) {
			return getData(key, Boolean.class);
		}

		@Override
		public String getString(String key) {
			return getData(key, String.class);
		}

		@Override
		public <T> T getData(String key) {
			return getData(key, null);
		}

		@Override
		public <T> T getData(String key, Class<T> classOfT) {
			return classOfT == null ? (T) objectMap.get(key) : classOfT.cast(objectMap.get(key));
		}

		@Override
		public <T> T removeData(String key) {
			return removeData(key, null);
		}

		@Override
		public <T> T removeData(String key, Class<T> classOfT) {
			return classOfT == null ? (T) objectMap.remove(key) : classOfT.cast(objectMap.remove(key));
		}

		@Override
		public boolean hasData(String key) {
			return getData(key) != null;
		}

		@Override
		public void clearData() {
			objectMap.clear();
		}
	}

	@Override
	public void setClipboard(Clipboard clipboard) {
		getObjectMap().setData(KEY_CLIPBOARD, clipboard);
	}

	@Override
	public Clipboard getClipboard() {
		return getObjectMap().getData(KEY_CLIPBOARD);
	}

	@Override
	public boolean hasClipboard() {
		return getObjectMap().hasData(KEY_CLIPBOARD);
	}

	@Override
	public void setScriptLine(String scriptLine) {
		getObjectMap().setData(KEY_SCRIPTLINE, scriptLine);
	}

	@Override
	public String getScriptLine() {
		return getObjectMap().getString(KEY_SCRIPTLINE);
	}

	@Override
	public boolean hasScriptLine() {
		return getObjectMap().hasData(KEY_SCRIPTLINE);
	}

	@Override
	public void setActionType(String actionType) {
		getObjectMap().setData(KEY_CLICKACTION, actionType);
	}

	@Override
	public String getActionType() {
		return getObjectMap().getString(KEY_CLICKACTION);
	}

	@Override
	public boolean hasActionType() {
		return getObjectMap().hasData(KEY_CLICKACTION);
	}

	@Override
	public void setOldFullCoords(String fullCoords) {
		getObjectMap().setData(KEY_OLDFULLCOORDS, fullCoords);
	}

	@Override
	public String getOldFullCoords() {
		return getObjectMap().getString(KEY_OLDFULLCOORDS);
	}

	@Override
	public boolean hasOldFullCoords() {
		return getObjectMap().hasData(KEY_OLDFULLCOORDS);
	}
}