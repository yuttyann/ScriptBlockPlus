package com.github.yuttyann.scriptblockplus.player;

public abstract interface ObjectData {

	public abstract void setData(String key, Object value);

	public abstract Object getData(String key);

	public abstract Object removeData(String key);

	public abstract boolean hasData(String key);

	public abstract void clearData();
}