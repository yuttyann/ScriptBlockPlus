package com.github.yuttyann.scriptblockplus.player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus ObjectMap インターフェース</br>
 * @author yuttyann44581
 */
public interface ObjectMap {

	/**
	 * マップに「キー」と、それに対応した「値」を登録する</br>
	 * すでに登録されている場合は要素を上書きします
	 * @param key キー
	 * @param value 値
	 */
	public void put(@NotNull String key, @Nullable Object value);

	/**
	 * マップの「キー」に対応した「値」を取得する
	 * @param key キー
	 * @return byte
	 */
	public default byte getByte(@NotNull String key) {
		return get(key, (byte) 0);
	}

	/**
	 * マップの「キー」に対応した「値」を取得する
	 * @param key キー
	 * @return short
	 */
	public default short getShort(@NotNull String key) {
		return get(key, (short) 0);
	}

	/**
	 * マップの「キー」に対応した「値」を取得する
	 * @param key キー
	 * @return int
	 */
	public default int getInt(@NotNull String key) {
		return get(key, 0);
	}

	/**
	 * マップの「キー」に対応した「値」を取得する
	 * @param key キー
	 * @return long
	 */
	public default long getLong(@NotNull String key) {
		return get(key, 0L);
	}

	/**
	 * マップの「キー」に対応した「値」を取得する
	 * @param key キー
	 * @return char
	 */
	public default char getChar(@NotNull String key) {
		return get(key, '\u0000');
	}

	/**
	 * マップの「キー」に対応した「値」を取得する
	 * @param key キー
	 * @return float
	 */
	public default float getFloat(@NotNull String key) {
		return get(key, 0.0F);
	}

	/**
	 * マップの「キー」に対応した「値」を取得する
	 * @param key キー
	 * @return double
	 */
	public default double getDouble(@NotNull String key) {
		return get(key, 0.0D);
	}

	/**
	 * マップの「キー」に対応した「値」を取得する
	 * @param key キー
	 * @return byte
	 */
	public default boolean getBoolean(@NotNull String key) {
		return get(key, false);
	}

	/**
	 * マップの「キー」に対応した「値」を取得する
	 * @param key キー
	 * @return String
	 */
	public default String getString(@NotNull String key) {
		return get(key, "");
	}

	/**
	 * マップの「キー」に対応した「値」を取得する
	 * @param key キー
	 * @return Object
	 */
	@Nullable
	public <T> T get(@NotNull String key);

	/**
	 * マップの「キー」に対応した「値」を取得する
	 * @param key キー
	 * @param def デフォルトの値
	 * @return Object
	 */
	@NotNull
	public default <T> T get(@NotNull String key, @NotNull T def) {
		T t = get(key);
		return t == null ? def : t;
	}

	/**
	 * マップの「キー」と、それに対応した「値」を削除する
	 * @param key キー
	 * @return Object
	 */
	@Nullable
	public <T> T remove(String key);

	/**
	 * 指定された「キー」に要素が存在するのか確認する
	 * @param key キー
	 * @return 要素が存在するかどうか
	 */
	public default boolean has(@NotNull String key) {
		return get(key) != null;
	}

	/**
	 * 指定された「キー」が存在するか確認する
	 * @param key キー
	 * @return キー が存在するかどうか
	 */
	public boolean containsKey(@NotNull String key);

	/**
	 * 指定された「値」が存在するか確認する
	 * @param value 値
	 * @return 値 が存在するかどうか
	 */
	public boolean containsValue(@NotNull Object value);

	/**
	 * 全ての要素を削除する
	 */
	public void clear();
}