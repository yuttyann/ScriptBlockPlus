package com.github.yuttyann.scriptblockplus.player;

import java.util.HashMap;
import java.util.Map;

import com.github.yuttyann.scriptblockplus.script.Clipboard;

public abstract class PlayerData implements ObjectData {

	private static final String KEY_CLIPBOARD = "CLIPBOARD";
	private static final String KEY_SCRIPTLINE = "SCRIPTLINE";
	private static final String KEY_CLICKACTION = "CLICKACTION";
	private static final String KEY_OLDFULLCOORDS = "OLDFULLCOORDS";

	private final Map<String, Object> objectData;

	PlayerData() {
		objectData = new HashMap<String, Object>();
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

	public void setClipboard(Clipboard clipboard) {
		setData(KEY_CLIPBOARD, clipboard);
	}

	public Clipboard getClipboard() {
		return getData(KEY_CLIPBOARD, null);
	}

	public boolean hasClipboard() {
		return hasData(KEY_CLIPBOARD);
	}

	public void setScriptLine(String scriptLine) {
		setData(KEY_SCRIPTLINE, scriptLine);
	}

	public String getScriptLine() {
		return getData(KEY_SCRIPTLINE, null);
	}

	public boolean hasScriptLine() {
		return hasData(KEY_SCRIPTLINE);
	}

	public void setClickAction(String clickAction) {
		setData(KEY_CLICKACTION, clickAction);
	}

	public String getClickAction() {
		return getData(KEY_CLICKACTION, null);
	}

	public boolean hasClickAction() {
		return hasData(KEY_CLICKACTION);
	}

	public void setOldFullCoords(String fullCoords) {
		setData(KEY_OLDFULLCOORDS, fullCoords);
	}

	public String getOldFullCoords() {
		return getData(KEY_OLDFULLCOORDS, null);
	}

	public boolean hasOldFullCoords() {
		return hasData(KEY_OLDFULLCOORDS);
	}
}