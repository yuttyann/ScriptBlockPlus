package com.github.yuttyann.scriptblockplus.player;

import java.util.HashMap;
import java.util.Map;

import com.github.yuttyann.scriptblockplus.script.Clipboard;

public abstract class PlayerData implements ObjectData {

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

	public void setScriptLine(String scriptLine) {
		setData("SCRIPTLINE", scriptLine);
	}

	public String getScriptLine() {
		return (String) getData("SCRIPTLINE");
	}

	public boolean hasScriptLine() {
		return getScriptLine() != null;
	}

	public void setClickAction(String clickAction) {
		setData("CLICKACTION", clickAction);
	}

	public String getClickAction() {
		return (String) getData("CLICKACTION");
	}

	public boolean hasClickAction() {
		return getClickAction() != null;
	}

	public void setOldFullCoords(String fullCoords) {
		setData("OLDFULLCOORDS", fullCoords);
	}

	public String getOldFullCoords() {
		return (String) getData("OLDFULLCOORDS");
	}

	public boolean hasOldFullCoords() {
		return getOldFullCoords() != null;
	}

	public void setClipboard(Clipboard clipboard) {
		setData("CLIPBOARD", clipboard);
	}

	public Clipboard getClipboard() {
		return (Clipboard) getData("CLIPBOARD");
	}

	public boolean hasClipboard() {
		return getClipboard() != null;
	}
}