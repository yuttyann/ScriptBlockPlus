package com.github.yuttyann.scriptblockplus.hook;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus HookPlugin クラス
 * @author yuttyann44581
 */
public abstract class HookPlugin {

    /**
     * プラグイン名を取得します。
     * @return プラグイン名
     */
    @NotNull
    public abstract String getPluginName();

    /**
     * プラグインが有効なのか確認します。
     * @return プラグインが有効な場合はtrue
     */
    public final boolean has() {
        return Bukkit.getPluginManager().isPluginEnabled(getPluginName());
    }
}