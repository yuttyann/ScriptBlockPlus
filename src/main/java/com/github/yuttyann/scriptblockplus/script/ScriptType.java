package com.github.yuttyann.scriptblockplus.script;

import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ScriptBlockPlus ScriptType クラス
 * @author yuttyann44581
 */
@SuppressWarnings("serial")
public final class ScriptType implements Comparable<ScriptType>, Serializable {

    private static final Map<String, ScriptType> TYPES = new LinkedHashMap<>();

    // デフォルトのキーを生成
    public static final ScriptType INTERACT = new ScriptType("interact");
    public static final ScriptType BREAK = new ScriptType("break");
    public static final ScriptType WALK = new ScriptType("walk");
    public static final ScriptType HIT = new ScriptType("hit");

    private final String type;
    private final String name;
    private final int ordinal;

    /**
     * コンストラクタ
     * @param type - スクリプトの種類名
     */
    public ScriptType(@NotNull String type) {
        this.type = type.toLowerCase();
        this.name = type.toUpperCase();

        var scriptType = TYPES.get(name);
        this.ordinal = scriptType == null ? TYPES.size() : scriptType.ordinal;
        if (scriptType == null) {
            TYPES.put(name, this);
        }
    }

    /**
     * スクリプトの種類名(小文字)を取得します。
     * @return {@link String} - スクリプトの種類名
     */
    @NotNull
    public String type() {
        return type;
    }

    /**
     * スクリプトの種類名(大文字)を取得します。
     * @return {@link String} - スクリプトの種類名
     */
    @NotNull
    public String name() {
        return name;
    }

    /**
     * スクリプトの種類の序数を取得します
     * @return {@link Integer} - 序数
     */
    public int ordinal() {
        return ordinal;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        int prime = 31;
        hash = prime * hash + ordinal;
        hash = prime * hash + type.hashCode();
        hash = prime * hash + name.hashCode();
        return hash;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof ScriptType) {
            var scriptType = (ScriptType) obj;
            return type.equals(scriptType.type) && name.equals(scriptType.name);
        }
        return false;
    }

    @Override
    public int compareTo(@NotNull ScriptType another) {
        return Integer.compare(ordinal, another.ordinal);
    }

    /**
     * スクリプトの種類の数を取得します。
     * @return {@link Integer} - スクリプトの種類の数
     */
    public static int size() {
        return TYPES.size();
    }

    /**
     * スクリプトの種類名(大文字)の配列を作成します。
     * @return {@link String[]} - スクリプトの種類名の配列
     */
    @NotNull
    public static String[] types() {
        return StreamUtils.toArray(TYPES.values(), t -> t.type, String[]::new);
    }

    /**
     * スクリプトの種類名(小文字)の配列を作成します。
     * @return {@link String[]} - スクリプトの種類名の配列
     */
    @NotNull
    public static String[] names() {
        return StreamUtils.toArray(TYPES.values(), t -> t.name, String[]::new);
    }

    /**
     * スクリプトの種類の配列を作成します。
     * @return {@link ScriptType[]} - スクリプトの種類の配列
     */
    @NotNull
    public static ScriptType[] values() {
        return TYPES.values().toArray(ScriptType[]::new);
    }

    /**
     * 指定したスクリプトの種類を取得します。
     * @throws NullPointerException スクリプトの種類が見つからなかったときにスローされます。
     * @param ordinal - 序数
     * @return {@link ScriptType} - スクリプトの種類
     */
    @NotNull
    public static ScriptType valueOf(int ordinal) {
        for (var scriptType : TYPES.values()) {
            if (scriptType.ordinal == ordinal) {
                return scriptType;
            }
        }
        throw new NullPointerException(ordinal + " does not exist");
    }

    /**
     * 指定したスクリプトの種類を取得します。
     * @throws NullPointerException スクリプトの種類が見つからなかったときにスローされます。
     * @param name - スクリプトの種類名
     * @return {@link ScriptType} - スクリプトの種類
     */
    @NotNull
    public static ScriptType valueOf(@Nullable String name) {
        Validate.notNull(name, "Name cannot be null");
        var scriptType = TYPES.get(name.toUpperCase());
        if (scriptType == null) {
            throw new NullPointerException(name + " does not exist");
        }
        return scriptType;
    }
}