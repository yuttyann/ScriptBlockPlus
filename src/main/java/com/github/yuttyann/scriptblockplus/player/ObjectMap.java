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
	public byte getByte(String key);

	/**
	 * マップの「キー」に対応した「値」を取得する
	 * @param key キー
	 * @return short
	 */
	public short getShort(String key);

	/**
	 * マップの「キー」に対応した「値」を取得する
	 * @param key キー
	 * @return int
	 */
	public int getInt(String key);

	/**
	 * マップの「キー」に対応した「値」を取得する
	 * @param key キー
	 * @return long
	 */
	public long getLong(String key);

	/**
	 * マップの「キー」に対応した「値」を取得する
	 * @param key キー
	 * @return char
	 */
	public char getChar(String key);

	/**
	 * マップの「キー」に対応した「値」を取得する
	 * @param key キー
	 * @return float
	 */
	public float getFloat(String key);

	/**
	 * マップの「キー」に対応した「値」を取得する
	 * @param key キー
	 * @return double
	 */
	public double getDouble(String key);

	/**
	 * マップの「キー」に対応した「値」を取得する
	 * @param key キー
	 * @return byte
	 */
	public boolean getBoolean(String key);

	/**
	 * マップの「キー」に対応した「値」を取得する
	 * @param key キー
	 * @return String
	 */
	public String getString(String key);

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
	public <T> T get(String key, Class<T> classOfT);

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
	public <T> T remove(String key, Class<T> classOfT);

	/**
	 * 指定された「キー」に要素が存在するのか確認する
	 * @param key キー
	 * @return 要素が存在するかどうか
	 */
	public boolean has(String key);

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