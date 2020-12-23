package com.github.yuttyann.scriptblockplus.utils.unmodifiable;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus UnmodifiableLocation クラス
 * @author yuttyann44581
 */
public class UnmodifiableLocation extends Location {

    public UnmodifiableLocation(@NotNull Location location) {
        super(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    @Override
    public void setWorld(@Nullable World world) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setX(double x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setY(double y) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setZ(double z) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setYaw(float yaw) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setPitch(float pitch) {
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public Location setDirection(@NotNull Vector vector) {
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public Location add(@NotNull Location vec) {
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public Location add(@NotNull Vector vec) {
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public Location add(double x, double y, double z) {
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public Location subtract(@NotNull Location vec) {
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public Location subtract(@NotNull Vector vec) {
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public Location subtract(double x, double y, double z) {
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public Location multiply(double m) {
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public Location zero() {
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public Location clone() {
        return new UnmodifiableLocation(super.clone());
    }
}