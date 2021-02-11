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
package com.github.yuttyann.scriptblockplus;

import java.util.Objects;

import com.github.yuttyann.scriptblockplus.utils.Utils;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus BlockCoords クラス
 * @author yuttyann44581
 */
public class BlockCoords {

    private final World world;

    private int x;
    private int y;
    private int z;

    /**
     * コンストラクタ
     * @param world - ワールド
     * @param x - X座標
     * @param y - Y座標
     * @param z - Z座標
     */
    protected BlockCoords(@NotNull World world, final int x, final int y, final int z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * ブロックを取得します。
     * @return {@link Block} - ブロック
     */
    @NotNull
    public final Block getBlock() {
        return world.getBlockAt(x, y, z);
    }

    /**
     * ワールドを取得します。
     * @return {@link World} - ワールド
     */
    @NotNull
    public final World getWorld() {
        return world;
    }

    /**
     * Xの座標を設定します。
     * @param x - X座標
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Xの座標を取得します。
     * @return {@link int} - X座標
     */
    public final int getX() {
        return x;
    }

    /**
     * Yの座標を設定します。
     * @param y - Y座標
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Yの座標を取得します。
     * @return {@link int} - Y座標
     */
    public final int getY() {
        return y;
    }

    /**
     * Zの座標を設定します。
     * @param z - Z座標
     */
    public void setZ(int z) {
        this.z = z;
    }

    /**
     * Zの座標を取得します。
     * @return {@link int} - Z座標
     */
    public final int getZ() {
        return z;
    }

    /**
     * 指定した値を加算します。
     * @param x - X座標
     * @param y - Y座標
     * @param z - Z座標
     * @return {@link BlockCoords} - ブロックの座標
     */
    @NotNull
    public BlockCoords add(int x, int y, int z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    /**
     * 指定した値を減算します。
     * @param x - X座標
     * @param y - Y座標
     * @param z - Z座標
     * @return {@link BlockCoords} - ブロックの座標
     */
    @NotNull
    public BlockCoords subtract(int x, int y, int z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    /**
     * 文字列の座標を取得します。
     * @return {@link String} - ワールド名を除いた文字列(x, y, z)
     */
    @NotNull
    public final String getCoords() {
        return x + "," + y + "," + z;
    }

    /**
     * ワールド名を含めた文字列の座標を取得します。
     * @return {@link String} - ワールド名を含めた文字列(world, x, y, z)
     */
    @NotNull
    public final String getFullCoords() {
        return world.getName() + "," + getCoords();
    }

    /**
     * {@link Vector}のインスタンスを生成します。
     * @return {@link Vector} - ベクトル
     */
    @NotNull
    public Vector toVector() {
        return new Vector(x, y, z);
    }

    /**
     * {@link Location}のインスタンスを生成します。
     * @return {@link Location} - 座標
     */
    @NotNull
    public Location toLocation() {
        return new Location(world, x, y, z);
    }

    /**
     * 値が{@code 0}のブロックの座標を取得します。
     * @param world - ワールド
     * @return {@link BlockCoords} - ブロックの座標(world, 0, 0, 0)
     */
    @NotNull
    public static BlockCoords zero(@NotNull World world) {
        return new BlockCoords(world, 0, 0, 0);
    }

    /**
     * ブロックの座標を取得します。
     * @param block - ブロック
     * @return {@link BlockCoords} - ブロックの座標
     */
    @NotNull
    public static BlockCoords of(@NotNull Block block) {
        return new BlockCoords(block.getWorld(), block.getX(), block.getY(), block.getZ());
    }

    /**
     * ブロックの座標を取得します。
     * @param location - 座標
     * @return {@link BlockCoords} - ブロックの座標
     */
    @NotNull
    public static BlockCoords of(@NotNull Location location) {
        return new BlockCoords(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    /**
     * ブロックの座標を取得します。
     * @param world - ワールド
     * @param vector - ベクトル
     * @return {@link BlockCoords} - ブロックの座標
     */
    @NotNull
    public static BlockCoords of(@NotNull World world, @NotNull Vector vector) {
        return new BlockCoords(world, vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
    }

    /**
     * ブロックの座標を取得します。
     * @param world - ワールド
     * @param x - X座標
     * @param y - Y座標
     * @param z - Z座標
     * @return {@link BlockCoords} - ブロックの座標
     */
    @NotNull
    public static BlockCoords of(@NotNull World world, @NotNull final double x, final double y, final double z) {
        return new BlockCoords(world, NumberConversions.floor(x), NumberConversions.floor(y), NumberConversions.floor(z));
    }

    /**
     * ブロックの座標をコピーして取得します。
     * @param blockCoords - ブロックの座標(コピー対象)
     * @return {@link BlockCoords} - ブロックの座標
     */
    @NotNull
    public static BlockCoords copy(@NotNull BlockCoords blockCoords) {
        return new BlockCoords(blockCoords.world, blockCoords.x, blockCoords.y, blockCoords.z);
    }

    /**
     * ワールド名を除いた文字列の座標を取得します。
     * @param location - 座標
     * @return {@link String} - ワールド名を除いた文字列(x, y, z)
     */
    @NotNull
    public static String getCoords(@NotNull Location location) {
        return location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
    }

    /**
     * ワールド名を含めた文字列の座標を取得します。
     * @param location - 座標
     * @return {@link String} - ワールド名を含めた文字列(world, x, y, z)
     */
    @NotNull
    public static String getFullCoords(@NotNull Location location) {
        return location.getWorld().getName() + "," + getCoords(location);
    }

    /**
     * ワールド名を除いた文字列の座標からインスタンスを生成します。
     * @param world - ワールド名
     * @param coords - ワールド名を除いた文字列(x, y, z)
     * @return {@link BlockCoords} - ブロックの座標
     */
    @NotNull
    public static BlockCoords fromString(@NotNull World world, @NotNull String coords) {
        if (coords.indexOf(' ') != -1) {
            coords = StringUtils.remove(coords, ' ');
        }
        int comma1 = coords.indexOf(',', 0);
        int comma2 = coords.indexOf(',', comma1 + 1);
        if (comma2 < 0) {
            throw new IllegalArgumentException("Invalid Coords: " + coords);
        }
        int x = Integer.parseInt(coords, 0, comma1, 10);
        int y = Integer.parseInt(coords, comma1 + 1, comma2, 10);
        int z = Integer.parseInt(coords, comma2 + 1, coords.length(), 10);
        return new BlockCoords(world, x, y, z);
    }

    /**
     * ワールド名を含めた座標の文字列からインスタンスを生成します。
     * @param fullCoords - ワールド名を含めた文字列(world, x, y, z)
     * @return {@link BlockCoords} - ブロックの座標
     */
    @NotNull
    public static BlockCoords fromString(@NotNull String fullCoords) {
        if (fullCoords.indexOf(' ') != -1) {
            fullCoords = StringUtils.remove(fullCoords, ' ');
        }
        int comma1 = fullCoords.indexOf(',', 0);
        int comma2 = fullCoords.indexOf(',', comma1 + 1);
        int comma3 = fullCoords.indexOf(',', comma2 + 1);
        if (comma3 < 0) {
            throw new IllegalArgumentException("Invalid FullCoords: " + fullCoords);
        }
        int x = Integer.parseInt(fullCoords, comma1 + 1, comma2, 10);
        int y = Integer.parseInt(fullCoords, comma2 + 1, comma3, 10);
        int z = Integer.parseInt(fullCoords, comma3 + 1, fullCoords.length(), 10);
        return new BlockCoords(Utils.getWorld(fullCoords.substring(0, comma1)), x, y, z);
    }

    /**
     * 座標を比較します。
     * @param location1 - 座標1
     * @param location2 - 座標2
     * @return {@link boolean} - 座標が一致する場合は{@code true}
     */
    public static boolean compare(@NotNull Location location1, @NotNull Location location2) {
        if (!Objects.equals(location1.getWorld(), location2.getWorld())) {
            return false;
        }
        int blockX1 = location1.getBlockX(), blockY1 = location1.getBlockY(), blockZ1 = location1.getBlockZ();
        int blockX2 = location2.getBlockX(), blockY2 = location2.getBlockY(), blockZ2 = location2.getBlockZ();
        return blockX1 == blockX2 && blockY1 == blockY2 && blockZ1 == blockZ2;
    }

    /**
     * 座標を比較します。
     * @param block - ブロック
     * @return {@link boolean} - 座標が一致する場合は{@code true}
     */
    public boolean compare(@Nullable Block block) {
        if (block == null) {
            return false;
        }
        return compare(block.getX(), block.getY(), block.getZ()) && world.equals(block.getWorld());
    }

    /**
     * 座標を比較します。
     * @param location - 座標
     * @return {@link boolean} - 座標が一致する場合は{@code true}
     */
    public boolean compare(@Nullable Location location) {
        if (location == null) {
            return false;
        }
        return compare(location.getBlockX(), location.getBlockY(), location.getBlockZ()) && world.equals(location.getWorld());
    }

    /**
     * 座標を比較します。
     * @param blockCoords - ブロックの座標
     * @return {@link boolean} - 座標が一致する場合は{@code true}
     */
    public boolean compare(@Nullable BlockCoords blockCoords) {
        if (blockCoords == null) {
            return false;
        }
        return compare(blockCoords.x, blockCoords.y, blockCoords.z) && world.equals(blockCoords.world);
    }

    /**
     * 座標を比較します。
     * @param x - X座標
     * @param y - Y座標
     * @param z - Z座標
     * @return {@link boolean} - 座標が一致する場合は{@code true}
     */
    public boolean compare(int x, int y, int z) {
        return this.x == x && this.y == y && this.z == z;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        return obj instanceof BlockCoords ? compare((BlockCoords) obj) : false;
    }

    @Override
    public int hashCode() {
        return (x ^ (z << 12)) ^ (y << 24) ^ world.hashCode();
    }

    @Override
    public String toString() {
        return getFullCoords();
    }
}