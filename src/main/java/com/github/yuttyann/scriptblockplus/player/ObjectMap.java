package com.github.yuttyann.scriptblockplus.player;

/**
 * ScriptBlockPlus ObjectMap インターフェース</br>
 * Map より出来ることは少ないけど、必要な分の機能は揃っている
 * @author yuttyann44581
 */
public interface ObjectMap {

	/**
	 * マップに「キー」と、それに対応した「値」を登録する</br>
	 * すでに登録されている場合は要素を上書きします
	 * @param key キー
	 * @param value 値
	 */
	public void put(String key, Object value);

	/**
	 * マップの「キー」に対応した「値」を取得する
	 * @param key キー
	 * @return byte
	 */
	public default byte getByte(String key) {
		return get(key, byte.class);
	}

	/**
	 * マップの「キー」に対応した「値」を取得する
	 * @param key キー
	 * @return short
	 */
	public default short getShort(String key) {
		return get(key, short.class);
	}

	/**
	 * マップの「キー」に対応した「値」を取得する
	 * @param key キー
	 * @return int
	 */
	public default int getInt(String key) {
		return get(key, int.class);
	}

	/**
	 * マップの「キー」に対応した「値」を取得する
	 * @param key キー
	 * @return long
	 */
	public default long getLong(String key) {
		return get(key, long.class);
	}

	/**
	 * マップの「キー」に対応した「値」を取得する
	 * @param key キー
	 * @return char
	 */
	public default char getChar(String key) {
		return get(key, char.class);
	}

	/**
	 * マップの「キー」に対応した「値」を取得する
	 * @param key キー
	 * @return float
	 */
	public default float getFloat(String key) {
		return get(key, float.class);
	}

	/**
	 * マップの「キー」に対応した「値」を取得する
	 * @param key キー
	 * @return double
	 */
	public default double getDouble(String key) {
		return get(key, double.class);
	}

	/**
	 * マップの「キー」に対応した「値」を取得する
	 * @param key キー
	 * @return byte
	 */
	public default boolean getBoolean(String key) {
		return get(key, boolean.class);
	}

	/**
	 * マップの「キー」に対応した「値」を取得する
	 * @param key キー
	 * @return String
	 */
	public default String getString(String key) {
		return get(key, String.class);
	}

	/**
	 * マップの「キー」に対応した「値」を取得する
	 * @param key キー
	 * @return Object
	 */
	public <T> T get(String key);

	/**
	 * マップの「キー」に対応した「値」を取得する
	 * @param key キー
	 * @param classOfT キャストするクラス
	 * @return Object
	 */
	public default <T> T get(String key, Class<T> classOfT) {
		return classOfT == null ? get(key) : (T) get(key);
	}

	/**
	 * マップの「キー」と、それに対応した「値」を削除する
	 * @param key キー
	 * @return Object
	 */
	public <T> T remove(String key);

	/**
	 * マップの「キー」と、それに対応した「値」を削除する
	 * @param key キー
	 * @param classOfT キャストするクラス
	 * @return Object
	 */
	public default <T> T remove(String key, Class<T> classOfT) {
		return classOfT == null ? remove(key) : (T) remove(key);
	}

	/**
	 * 指定された「キー」に要素が存在するのか確認する
	 * @param key キー
	 * @return 要素が存在するかどうか
	 */
	public default boolean has(String key) {
		return get(key) != null;
	}

	/**
	 * 指定された「キー」が存在するか確認する
	 * @param key キー
	 * @return キー が存在するかどうか
	 */
	public boolean containsKey(String key);

	/**
	 * 指定された「値」が存在するか確認する
	 * @param value 値
	 * @return 値 が存在するかどうか
	 */
	public boolean containsValue(Object value);

	/**
	 * 全ての要素を削除する
	 */
	public void clear();
}