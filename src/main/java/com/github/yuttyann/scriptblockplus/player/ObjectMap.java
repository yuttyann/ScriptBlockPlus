package com.github.yuttyann.scriptblockplus.player;

public interface ObjectMap {

	/**
	 * マップに「キー」と、それに対応した「値」を登録する</br>
	 * すでに登録されている場合は要素を上書きします
	 * @param key キー
	 * @param value 値
	 */
	public void setData(String key, Object value);

	public byte getByte(String key);

	public short getShort(String key);

	public int getInt(String key);

	public long getLong(String key);

	public char getChar(String key);

	public float getFloat(String key);

	public double getDouble(String key);

	public boolean getBoolean(String key);

	public String getString(String key);

	public <T> T getData(String key);

	public <T> T getData(String key, Class<T> classOfT);

	public <T> T removeData(String key);

	public <T> T removeData(String key, Class<T> classOfT);

	public boolean hasData(String key);

	public void clearData();
}