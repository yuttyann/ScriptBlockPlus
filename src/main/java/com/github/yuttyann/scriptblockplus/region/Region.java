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
package com.github.yuttyann.scriptblockplus.region;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus Region インターフェース
 * @author yuttyann44581
 */
public interface Region {

    /**
     * ワールドを取得します。
     * @return {@link World} - ワールド
     */
    @Nullable
    World getWorld();

    /**
     * ワールド名を取得します。
     * @return {@link String} - ワールド名
     */
    @Nullable
    String getName();

    /**
     * 最小座標を取得します。
     * @return {@link Location} - 最小座標
     */
    @NotNull
    Location getMinimumPoint();

    /**
     * 最大座標を取得します。
     * @return {@link Location} - 最大座標
     */
    @NotNull
    Location getMaximumPoint();

    /**
     * 座標が設定されている場合はtrueを返します。
     * @return {@link Boolean} - 座標が設定されている場合はtrue
     */
    boolean hasPositions();
}