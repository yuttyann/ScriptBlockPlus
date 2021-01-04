package com.github.yuttyann.scriptblockplus.script;

import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus SBInstance インターフェース
 * @param <T> インスタンスの型
 * @author yuttyann44581
 */
public interface SBInstance<T> {

    /**
     * インスタンスを生成します。
     * @return {@link T} - インスタンス
     */
    @NotNull
    T newInstance();
}