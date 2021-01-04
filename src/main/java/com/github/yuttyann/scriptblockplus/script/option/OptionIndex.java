package com.github.yuttyann.scriptblockplus.script.option;

import com.github.yuttyann.scriptblockplus.enums.IndexType;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus OptionIndex クラス
 * @author yuttyann44581
 */
public class OptionIndex {

    private final String syntax;
    private final IndexType indexType;

    /**
     * コンストラクタ
     * @param indexType - スクリプトの追加位置
     * @param optionClass - オプションのクラス
     */
    public OptionIndex(@NotNull IndexType indexType, @NotNull Class<? extends BaseOption> optionClass) {
        this.syntax = optionClass.getAnnotation(OptionTag.class).syntax();
        this.indexType = indexType;
    }

    /**
     * オプションの構文を取得します。
     * @return {@link String} - オプションの構文
     */
    @NotNull
    public String getSyntax() {
        return syntax;
    }

    /**
     * スクリプトの追加方法を取得します。
     * @return {@link IndexType} - スクリプトの追加方法
     */
    @NotNull
    public IndexType getIndexType() {
        return indexType;
    }
}