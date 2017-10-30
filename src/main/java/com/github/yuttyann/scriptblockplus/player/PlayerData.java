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
	public Object removeData(String key) {
		return objectData.remove(key);
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
		return (Clipboard) getData(KEY_CLIPBOARD);
	}

	public boolean hasClipboard() {
		return hasData(KEY_CLIPBOARD);
	}

	public void setScriptLine(String scriptLine) {
		setData(KEY_SCRIPTLINE, scriptLine);
	}

	public String getScriptLine() {
		return (String) getData(KEY_SCRIPTLINE);
	}

	public boolean hasScriptLine() {
		return hasData(KEY_SCRIPTLINE);
	}

	public void setClickAction(String clickAction) {
		setData(KEY_CLICKACTION, clickAction);
	}

	public String getClickAction() {
		return (String) getData(KEY_CLICKACTION);
	}

	public boolean hasClickAction() {
		return hasData(KEY_CLICKACTION);
	}

	public void setOldFullCoords(String fullCoords) {
		setData(KEY_OLDFULLCOORDS, fullCoords);
	}

	public String getOldFullCoords() {
		return (String) getData(KEY_OLDFULLCOORDS);
	}

	public boolean hasOldFullCoords() {
		return hasData(KEY_OLDFULLCOORDS);
	}
}