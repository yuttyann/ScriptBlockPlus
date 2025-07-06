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
     * @return プラグインが有効な場合は{@code true}
     */
    public final boolean has() {
        return Bukkit.getPluginManager().isPluginEnabled(getPluginName());
    }
}