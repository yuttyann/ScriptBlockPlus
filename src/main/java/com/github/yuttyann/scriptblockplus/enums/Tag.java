package com.github.yuttyann.scriptblockplus.enums;

import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus Tag 列挙型
 * @author yuttyann44581
 */
public enum Tag {
    OP("op="),
    PERM("perm="),
    LIMIT("limit="),
    NONE("");

    private static final Tag[] TAGS = { OP, PERM, LIMIT };

    private final String prefix;

    Tag(@NotNull String prefix) {
        this.prefix = prefix;
    }

    @NotNull
    public static Tag[] getTags() {
        return TAGS;
    }

    @NotNull
    public String getPrefix() {
        return prefix;
    }

    @NotNull
    public String substring(@NotNull String source) {
        return source.substring(prefix.length(), source.length());
    }
}