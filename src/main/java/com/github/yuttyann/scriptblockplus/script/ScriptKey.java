/**
 * ScriptBlockPlus - Allow you to add script to any blocks.
 * Copyright (C) 2021 yuttyann44581
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package com.github.yuttyann.scriptblockplus.script;

import com.github.yuttyann.scriptblockplus.utils.StreamUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * ScriptBlockPlus ScriptKey クラス
 * @author yuttyann44581
 */
public final class ScriptKey implements Comparable<ScriptKey>, Serializable {

    private static final Map<String, ScriptKey> KEYS = new LinkedHashMap<>();

    // デフォルトのキーを生成
    public static final ScriptKey INTERACT = new ScriptKey("interact");
    public static final ScriptKey BREAK = new ScriptKey("break");
    public static final ScriptKey WALK = new ScriptKey("walk");
    public static final ScriptKey HIT = new ScriptKey("hit");

    private final String lowerName;
    private final String upperName;

    private final int ordinal;
 
    /**
     * コンストラクタ
     * @param name - スクリプトキーの名前
     */
    public ScriptKey(@NotNull String name) {
        this.lowerName = name.toLowerCase(Locale.ROOT);
        this.upperName = name.toUpperCase(Locale.ROOT);

        var scriptKey = KEYS.get(this.upperName);
        this.ordinal = scriptKey == null ? KEYS.size() : scriptKey.ordinal;
        if (scriptKey == null) {
            KEYS.put(this.upperName, this);
        }
    }

    /**
     * スクリプトキーの名前を取得します。
     * @return {@link String} - スクリプトキーの名前
     */
    @NotNull
    public String getName() {
        return lowerName;
    }

    /**
     * スクリプトキーの序数を取得します
     * @return {@link Integer} - スクリプトキーの序数
     */
    public int ordinal() {
        return ordinal;
    }

    /**
     * スクリプトキーの総数を取得します。
     * @return {@link Integer} - スクリプトキーの総数
     */
    public static int size() {
        return KEYS.size();
    }

    /**
     * スクリプトキーの名前の配列を作成します。
     * @return {@link String}[] - スクリプトキーの名前の配列
     */
    @NotNull
    public static String[] types() {
        return StreamUtils.toArray(KEYS.values(), t -> t.upperName, String[]::new);
    }

    /**
     * スクリプトキーの配列を作成します。
     * @return {@link ScriptKey}[] - スクリプトキーの配列
     */
    @NotNull
    public static ScriptKey[] values() {
        return KEYS.values().toArray(ScriptKey[]::new);
    }

    /**
     * スクリプトキーのイテラブルを取得します。
     * @return {@link Iterable}&lt;{@link ScriptKey}&gt; - スクリプトキーのイテラブル
     */
    @NotNull
    public static Iterable<ScriptKey> iterable() {
        return KEYS.values();
    }

    /**
     * 指定したスクリプトキーを取得します。
     * @throws NullPointerException スクリプトキーが見つからなかったときにスローされます。
     * @param ordinal - スクリプトキーの序数
     * @return {@link ScriptKey} - スクリプトキー
     */
    @NotNull
    public static ScriptKey valueOf(final int ordinal) {
        for (var scriptKey : KEYS.values()) {
            if (scriptKey.ordinal == ordinal) {
                return scriptKey;
            }
        }
        throw new NullPointerException(ordinal + " does not exist");
    }

    /**
     * 指定したスクリプトキーを取得します。
     * @throws NullPointerException スクリプトキーが見つからなかったときにスローされます。
     * @param name - スクリプトキー
     * @return {@link ScriptKey} - スクリプトキー
     */
    @NotNull
    public static ScriptKey valueOf(@NotNull String name) {
        return get(name.toUpperCase(Locale.ROOT));
    }

    /**
     * 指定したスクリプトキーを取得します。
     * @throws NullPointerException スクリプトキーが見つからなかったときにスローされます。
     * @param upperName - スクリプトキー(全て大文字)
     * @return {@link ScriptKey} - スクリプトキー
     */
    @NotNull
    public static ScriptKey get(@NotNull String upperName) {
        var scriptKey = KEYS.get(upperName);
        if (scriptKey == null) {
            throw new NullPointerException(upperName + " does not exist");
        }
        return scriptKey;
    }

    @Override
    @NotNull
    public String toString() {
        return upperName;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        int prime = 31;
        hash = prime * hash + (ordinal + 1);
        hash = prime * hash + upperName.hashCode();
        return hash;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ScriptKey) {
            var scriptKey = (ScriptKey) obj;
            return upperName.equals(scriptKey.upperName) && ordinal == scriptKey.ordinal;
        }
        return false;
    }

    @Override
    public int compareTo(@NotNull ScriptKey another) {
        return Integer.compare(ordinal, another.ordinal);
    }
}