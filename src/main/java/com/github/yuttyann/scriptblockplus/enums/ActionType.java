package com.github.yuttyann.scriptblockplus.enums;

import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus ActionType 列挙型
 * @author yuttyann44581
 */
public enum ActionType {

    /**
     * スクリプトの作成
     */
    CREATE("create"),

    /**
     * スクリプトの追加
     */
    ADD("add"),

    /**
     * スクリプトの削除
     */
    REMOVE("remove"),

    /**
     * スクリプトの詳細
     */
    VIEW("view");

    private final String name;

    ActionType(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    public String getName() {
        return name;
    }
}

