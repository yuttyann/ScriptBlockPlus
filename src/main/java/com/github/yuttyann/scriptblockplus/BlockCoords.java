package com.github.yuttyann.scriptblockplus;

import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * ScriptBlockPlus BlockCoords クラス
 * @author yuttyann44581
 */
public class BlockCoords {

    private final World world;

    private int x;
    private int y;
    private int z;

    public BlockCoords(@NotNull final Location location) {
        this(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public BlockCoords(@Nullable final World world, final int x, final int y, final int z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * ワールドを取得します。
     * @return ワールド
     */
    @NotNull
    public World getWorld() {
        return world;
    }

    /**
     * Xの座標を取得します。
     * @return X座標
     */
    public int getX() {
        return x;
    }

    /**
     * Xの座標を設定します。
     * @param x X座標
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Yの座標を取得します。
     * @return Y座標
     */
    public int getY() {
        return y;
    }

    /**
     * Yの座標を設定します。
     * @param y Y座標
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Zの座標を取得します。
     * @return Z座標
     */
    public int getZ() {
        return z;
    }

    /**
     * Zの座標を設定します。
     * @param z Z座標
     */
    public void setZ(int z) {
        this.z = z;
    }

    /**
     * 指定した値を加算します。
     * @param x X座標
     * @param y Y座標
     * @param z Z座標
     * @return BlockCoords
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
     * @param x X座標
     * @param y Y座標
     * @param z Z座標
     * @return BlockCoords
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
     * @return ワールド名を除いた文字列(x, y, z)
     */
    @NotNull
    public String getCoords() {
        return x + ", " + y + ", " + z;
    }

    /**
     * ワールド名を含めた文字列の座標を取得します。
     * @return ワールド名を含めた文字列(world, x, y, z)
     */
    @NotNull
    public String getFullCoords() {
        return world.getName() + ", " + getCoords();
    }

    /**
     * ワールド名を除いた文字列の座標を取得します。
     * @param location 座標
     * @return ワールド名を除いた文字列(x, y, z)
     */
    @NotNull
    public static String getCoords(@NotNull Location location) {
        return location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ();
    }

    /**
     * ワールド名を含めた文字列の座標を取得します。
     * @param location 座標
     * @return ワールド名を含めた文字列(world, x, y, z)
     */
    @NotNull
    public static String getFullCoords(@NotNull Location location) {
        return Objects.requireNonNull(location.getWorld()).getName() + ", " + getCoords(location);
    }

    /**
     * ワールド名を除いた文字列の座標からインスタンスを生成します。
     * @param world ワールド名
     * @param coords ワールド名を除いた文字列(x, y, z)
     * @return Location
     */
    @NotNull
    public static Location fromString(@NotNull World world, @NotNull String coords) {
        String[] array = StringUtils.split(coords, ",");
        if (array.length != 3) {
            throw new IllegalArgumentException();
        }
        int x = Integer.parseInt(array[0].trim());
        int y = Integer.parseInt(array[1].trim());
        int z = Integer.parseInt(array[2].trim());
        return new Location(world, x, y, z);
    }

    /**
     * ワールド名を含めた座標の文字列からインスタンスを生成します。
     * @param fullCoords ワールド名を含めた文字列(world, x, y, z)
     * @return Location
     */
    @NotNull
    public static Location fromString(@NotNull String fullCoords) {
        String[] array = StringUtils.split(fullCoords, ",");
        if (array.length != 4) {
            throw new IllegalArgumentException();
        }
        World world = Utils.getWorld(array[0].trim());
        int x = Integer.parseInt(array[1].trim());
        int y = Integer.parseInt(array[2].trim());
        int z = Integer.parseInt(array[3].trim());
        return new Location(world, x, y, z);
    }

    /**
     * {@link Location}のインスタンスを生成します。
     * @return {@link Location}
     */
    @NotNull
    public Location toLocation() {
        return new Location(world, x, y, z);
    }

    @Override
    @NotNull
    public String toString() {
        return getFullCoords();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof BlockCoords)) {
            return false;
        }
        BlockCoords blockCoords = (BlockCoords) obj;
        return x == blockCoords.x && y == blockCoords.y && z == blockCoords.z && world.equals(blockCoords.world);
    }

    @Override
    public int hashCode() {
        int hash = 1;
        int prime = 31;
        hash = prime * hash + world.hashCode();
        hash = prime * hash + x;
        hash = prime * hash + y;
        hash = prime * hash + z;
        return hash;
    }
}