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
package com.github.yuttyann.scriptblockplus.utils.version;

import static java.lang.Integer.*;
import static org.apache.commons.lang3.StringUtils.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Corelate-Common Version
 * @author yuttyann44581
 */
public class Version implements Comparable<Version> {

    protected final int major, minor, incremental, versionInt;

    private String qualifier, versionToString;

    protected Version(final int major, final int minor, final int incremental, @Nullable String qualifier) {
        this.major = major;
        this.minor = minor;
        this.incremental = incremental;
        this.versionInt = (major * 100000) + (minor * 1000) + incremental;
        this.qualifier = isEmpty(qualifier) ? null : qualifier;
    }

    @NotNull
    public static Version of(@NotNull String version) {
        if (isEmpty(version)) {
            throw new IllegalArgumentException("Invalid Version: " + version);
        }
        var hyphen = version.indexOf('-');
        var qualifier = "";
        if (hyphen > 0) {
            qualifier = version.substring(hyphen + 1);
            version = version.substring(0, hyphen);
        }
        int dot1 = version.indexOf('.', 0), dot2 = version.indexOf('.', dot1 + 1);
        if (dot1 < 0) {
            throw new IllegalArgumentException("Invalid Version: " + version);
        }
        int part1 = parseInt(version, 0, dot1, 10), part2 = parseInt(version, dot1 + 1, dot2 < 0 ? version.length() : dot2, 10);
        return of(part1, part2, dot2 >= 0 ? parseInt(version, dot2 + 1, version.length(), 10) : 0, qualifier);
    }

    @NotNull
    public static Version of(int major) {
        return of(major, 0, 0);
    }

    @NotNull
    public static Version of(int major, int minor) {
        return of(major, minor, 0);
    }

    @NotNull
    public static Version of(int major, int minor, int incremental) {
        return of(major, minor, incremental, null);
    }

    @NotNull
    public static Version of(int major, int minor, int incremental, @Nullable String qualifier) {
        return new Version(major, minor, incremental, qualifier);
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getIncremental() {
        return incremental;
    }

    public int getVersionInt() {
        return versionInt;
    }

    @Nullable
    public String getQualifier() {
        return qualifier;
    }

    public boolean isOfficial() {
        return qualifier == null;
    }

    public boolean isBeta() {
        return startsWithIgnoreCase(qualifier, "beta");
    }

    public boolean isAlpha() {
        return startsWithIgnoreCase(qualifier, "alpha");
    }

    public boolean isSnapshot() {
        return startsWithIgnoreCase(qualifier, "snapshot");
    }

    @Override
    @NotNull
    public String toString() {
        if (versionToString == null) {
            this.versionToString = major + "." + minor + "." + incremental;
            if (qualifier != null) {
                this.versionToString += "-" + qualifier;
            }
        }
        return versionToString;
    }

    @Override
    public int hashCode() {
        return versionInt;
    }

    @Override
    public int compareTo(@NotNull Version version) {
        return Integer.compare(versionInt, version.versionInt);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return this == obj || obj instanceof Version && ((Version) obj).versionInt == versionInt;
    }

    /**
     * 現在のバージョンが比較先のバージョン以上の場合は{@code true}を返します。
     * @param otherVersion - 比較先のバージョン
     * @return {@code boolean} - 現在のバージョンが比較先のバージョン以上の場合は{@code true}
     */
    public boolean isUpperVersion(@NotNull Version otherVersion) {
        return versionInt >= otherVersion.versionInt;
    }

    /**
     * 現在のバージョンが比較先のバージョン未満の場合は{@code true}を返します。
     * @param otherVersion - 比較先のバージョン
     * @return {@code boolean} - 現在のバージョンが比較先のバージョン未満の場合は{@code true}を返します。
     */
    public boolean isLowerVersion(@NotNull Version otherVersion) {
        return versionInt < otherVersion.versionInt;
    }

    /**
     * 比較元のバージョンが比較先のバージョン以上の場合は{@code true}を返します。
     * <p>
     * {@code isUpperVersion("1.8", "1.8")} -> {@code true}
     * <p>
     * {@code isUpperVersion("1.8.1", "1.8")} -> {@code true}
     * <p>
     * {@code isUpperVersion("1.8", "1.8.1")} -> {@code false}
     * @param versionA - 比較元のバージョン
     * @param versionB - 比較先のバージョン
     * @return {@code boolean} - 比較元のバージョンが比較先のバージョン以上の場合は{@code true}
     */
    public static boolean isUpperVersion(@NotNull String versionA, @NotNull String versionB) {
        return of(versionA).isUpperVersion(of(versionB));
    }

    /**
     * 比較元のバージョンが比較先のバージョン未満の場合は{@code true}を返します。
     * <p>
     * {@code isUpperVersion("1.8", "1.8")} -> {@code false}
     * <p>
     * {@code isUpperVersion("1.8.1", "1.8")} -> {@code false}
     * <p>
     * {@code isUpperVersion("1.8", "1.8.1")} -> {@code true}
     * @param versionA - 比較元のバージョン
     * @param versionB - 比較先のバージョン
     * @return {@code boolean} - 比較元のバージョンが比較先のバージョン未満の場合は{@code true}
     */
    public static boolean isLowerVersion(@NotNull String versionA, @NotNull String versionB) {
        return of(versionA).isLowerVersion(of(versionB));
    }
}