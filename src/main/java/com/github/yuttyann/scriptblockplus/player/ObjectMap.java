package com.github.yuttyann.scriptblockplus.player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus ObjectMap インターフェース
 * @author yuttyann44581
 */
public interface ObjectMap {

	/**
	 * 指定された値と指定されたキーをこのマップで関連付けます。
	 * @param key キー
	 * @param value 値
	 */
	void put(@NotNull String key, @Nullable Object value);

	/**
	 * 指定されたキーがマップされている値を{@link Byte}にキャストして返します。
	 * @param key キー
	 * @return 指定されたキーがマップされている値 ({@link Byte})
	 */
	default byte getByte(@NotNull String key) {
		return get(key, (byte) 0);
	}

	/**
	 * 指定されたキーがマップされている値を{@link Short}にキャストして返します。
	 * @param key キー
	 * @return 指定されたキーがマップされている値 ({@link Short})
	 */
	default short getShort(@NotNull String key) {
		return get(key, (short) 0);
	}

	/**
	 * 指定されたキーがマップされている値を{@link Integer}にキャストして返します。
	 * @param key キー
	 * @return 指定されたキーがマップされている値 ({@link Integer})
	 */
	default int getInt(@NotNull String key) {
		return get(key, 0);
	}

	/**
	 * 指定されたキーがマップされている値を{@link Long}にキャストして返します。
	 * @param key キー
	 * @return 指定されたキーがマップされている値 ({@link Long})
	 */
	default long getLong(@NotNull String key) {
		return get(key, 0L);
	}

	/**
	 * 指定されたキーがマップされている値を{@link Character}にキャストして返します。
	 * @param key キー
	 * @return 指定されたキーがマップされている値 ({@link Character})
	 */
	default char getChar(@NotNull String key) {
		return get(key, '\u0000');
	}

	/**
	 * 指定されたキーがマップされている値を{@link Float}にキャストして返します。
	 * @param key キー
	 * @return 指定されたキーがマップされている値 ({@link Float})
	 */
	default float getFloat(@NotNull String key) {
		return get(key, 0.0F);
	}

	/**
	 * 指定されたキーがマップされている値を{@link Double}にキャストして返します。
	 * @param key キー
	 * @return 指定されたキーがマップされている値 ({@link Double})
	 */
	default double getDouble(@NotNull String key) {
		return get(key, 0.0D);
	}

	/**
	 * 指定されたキーがマップされている値を{@link Boolean}にキャストして返します。
	 * @param key キー
	 * @return 指定されたキーがマップされている値 ({@link Boolean})
	 */
	default boolean getBoolean(@NotNull String key) {
		return get(key, false);
	}

	/**
	 * 指定されたキーがマップされている値を{@link String}にキャストして返します。
	 * @param key キー
	 * @return 指定されたキーがマップされている値 ({@link String})
	 */
	@NotNull
	default String getString(@NotNull String key) {
		return get(key, "");
	}

	/**
	 * 指定されたキーがマップされている値を返します。
	 * <br/>
	 * このマップにそのキーのマッピングが含まれていない場合はnullを返します。
	 * @param key キー
	 * @return 指定されたキーがマップされている値（含まれていない場合はnull）
	 */
	@Nullable
	<T> T get(@NotNull String key);

	/**
	 * 指定されたキーがマップされている値を返します。
	 * <br/>
	 * このマップにそのキーのマッピングが含まれていない場合はotherを返します。
	 * @param key キー
	 * @param other その他
	 * @return 指定されたキーがマップされている値（含まれていない場合はother）
	 */
	@NotNull
	default <T> T get(@NotNull String key, @NotNull T other) {
		T t = get(key);
		return t == null ? other : t;
	}

	/**
	 * このマップからキーのマッピングを削除します。
	 * @param key キー
	 */
	void remove(String key);

	/**
	 * 指定されたキーに値がマップされている場合にtrueを返します。
	 * @param key キー
	 * @return 指定されたキーに値がマップされている場合はtrue
	 */
	default boolean has(@NotNull String key) {
		return get(key) != null;
	}

	/**
	 * マップからマッピングをすべて削除します。
	 */
	void clear();
}