package com.github.yuttyann.scriptblockplus.enums;

/**
 * ScriptBlockPlus IndexType 列挙型
 * @author yuttyann44581
 */
public enum IndexType {

    /**
     * 先頭に追加
     */
    TOP(-2),

    /**
     * 最後尾に追加
     */
    LAST(-1),

    /**
     * 指定したオプションより一つ前に追加
     */
    BEFORE(0),

    /**
     * 指定したオプションより一つ後に追加
     */
    AFTER(1);

    private final int amount;

    IndexType(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }
}
