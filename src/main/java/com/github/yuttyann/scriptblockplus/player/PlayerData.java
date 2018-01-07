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

	private final Map<String, Object> objectData;

	PlayerData() {
		objectData = new HashMap<String, Object>();
	}

	public static String createRandomId(String source) {
		return source + "_" + UUID.randomUUID();
	}

	@Override
	public void setData(String key, Object value) {
		objectData.put(key, value);
	}

	@Override
	public Object getData(String key) {
		return objectData.get(key);
	}

	@Override
	public <T> T getData(String key, Class<T> classOfT) {
		@SuppressWarnings("unchecked")
		T value = classOfT == null ? (T) getData(key) : classOfT.cast(getData(key));
		return value;
	}

	@Override
	public Object removeData(String key) {
		return objectData.remove(key);
	}

	@Override
	public <T> T removeData(String key, Class<T> classOfT) {
		@SuppressWarnings("unchecked")
		T value = classOfT == null ? (T) removeData(key) : classOfT.cast(removeData(key));
		return value;
	}

	@Override
	public boolean hasData(String key) {
		return getData(key) != null;
	}

	@Override
	public void clearData() {
		objectData.clear();
	}

	@Override
	public void setClipboard(Clipboard clipboard) {
		setData(KEY_CLIPBOARD, clipboard);
	}

	@Override
	public Clipboard getClipboard() {
		return getData(KEY_CLIPBOARD, Clipboard.class);
	}

	@Override
	public boolean hasClipboard() {
		return hasData(KEY_CLIPBOARD);
	}

	@Override
	public void setScriptLine(String scriptLine) {
		setData(KEY_SCRIPTLINE, scriptLine);
	}

	@Override
	public String getScriptLine() {
		return getData(KEY_SCRIPTLINE, String.class);
	}

	@Override
	public boolean hasScriptLine() {
		return hasData(KEY_SCRIPTLINE);
	}

	@Override
	public void setClickAction(String clickAction) {
		setData(KEY_CLICKACTION, clickAction);
	}

	@Override
	public String getClickAction() {
		return getData(KEY_CLICKACTION, String.class);
	}

	@Override
	public boolean hasClickAction() {
		return hasData(KEY_CLICKACTION);
	}

	@Override
	public void setOldFullCoords(String fullCoords) {
		setData(KEY_OLDFULLCOORDS, fullCoords);
	}

	@Override
	public String getOldFullCoords() {
		return getData(KEY_OLDFULLCOORDS, String.class);
	}

	@Override
	public boolean hasOldFullCoords() {
		return hasData(KEY_OLDFULLCOORDS);
	}
}