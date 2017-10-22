package com.github.yuttyann.scriptblockplus.player;

public interface ObjectData {

	public void setData(String key, Object value);

	public Object getData(String key);

	public Object removeData(String key);

	public boolean hasData(String key);

	public void clearData();
}