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