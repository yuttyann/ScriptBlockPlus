package com.github.yuttyann.scriptblockplus.script.option;

import com.github.yuttyann.scriptblockplus.enums.IndexType;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus OptionPriority クラス
 * @author yuttyann44581
 */
public class OptionPriority {

    private final IndexType indexType;
    private final OptionTag optionTag;
    private final Class<? extends BaseOption> optionClass;

    /**
     * コンストラクタ
     * @param indexType スクリプトの追加位置
     * @param optionClass オプションの継承クラス
     */
    public OptionPriority(@NotNull IndexType indexType, @NotNull Class<? extends BaseOption> optionClass) {
        this.indexType = indexType;
        this.optionTag = optionClass.getAnnotation(OptionTag.class);
        this.optionClass = optionClass;
    }

    /**
     * オプションの名前を取得します。
     * @return オプションの名前
     */
    @NotNull
    public String getName() {
        return optionTag.name();
    }

    /**
     * オプションの構文を取得します。
     * @return オプションの構文
     */
    @NotNull
    public String getSyntax() {
        return optionTag.syntax();
    }

    /**
     * スクリプトの追加方法を取得します。
     * @return スクリプトの追加方法
     */
    @NotNull
    public IndexType getIndexType() {
        return indexType;
    }

    /**
     * オプションの継承クラスを取得します。
     * @return オプションの継承クラス
     */
    @NotNull
    public Class<? extends BaseOption> getOptionClass() {
        return optionClass;
    }
}