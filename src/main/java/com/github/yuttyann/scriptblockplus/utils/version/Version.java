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

import static java.lang.Integer.parseInt;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.startsWithIgnoreCase;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * バージョンを管理し、バージョン間で比較や処理を行うクラスです。<p>
 * メジャー、マイナー、パッチ、及びクオリファイア（例："alpha"、"beta"）で構成されています。
 *
 * @author yuttyann44581
 */
public class Version implements Comparable<Version> {

    /**
     * バージョン文字列を解析して{@link Version}インスタンスを生成します。<p>
     * バージョン文字列は{@code "1.18.1"}、{@code "1.18.1-alpha"}のような形式である必要があります。
     *
     * @param version バージョン文字列
     * @return {@link Version}インスタンス
     * @throws IllegalArgumentException バージョン文字列が無効な場合にスローされます
     */
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

    /**
     * メジャーバージョンのみで{@link Version}インスタンスを生成します。<p>
     * マイナーバージョンとパッチバージョンは、ゼロと見なされます。
     *
     * @param major メジャーバージョン
     * @return {@link Version}インスタンス
     */
    @NotNull
    public static Version of(int major) {
        return of(major, 0, 0);
    }

    /**
     * メジャーバージョン、マイナーバージョンのみで{@link Version}インスタンスを生成します。<p>
     * パッチバージョンは、ゼロと見なされます。
     *
     * @param major メジャーバージョン
     * @return {@link Version}インスタンス
     */
    @NotNull
    public static Version of(int major, int minor) {
        return of(major, minor, 0);
    }

    /**
     * メジャーバージョン、マイナーバージョン、パッチバージョンのインスタンスを生成します。
     *
     * @param major メジャーバージョン
     * @param minor マイナーバージョン
     * @param patch パッチバージョン
     * @return {@link Version}インスタンス
     */
    @NotNull
    public static Version of(int major, int minor, int patch) {
        return of(major, minor, patch, null);
    }

    /**
     * メジャーバージョン、マイナーバージョン、パッチバージョン、クオリファイアのインスタンスを生成します。
     *
     * @param major メジャーバージョン
     * @param minor マイナーバージョン、
     * @param patch パッチバージョン
     * @param qualifier クオリファイア
     * @return {@link Version}インスタンス
     */
    @NotNull
    public static Version of(int major, int minor, int patch, @Nullable String qualifier) {
        return new Version(major, minor, patch, qualifier);
    }

    protected final int major, minor, patch;

    private String qualifier, versionToString;

    /**
     * メジャーバージョン、マイナーバージョン、パッチバージョン、及びクオリファイアを設定します。
     *
     * @param major メジャーバージョン
     * @param minor マイナーバージョン
     * @param patch パッチバージョン
     * @param qualifier クオリファイア
     */
    protected Version(int major, int minor, int patch, @Nullable String qualifier) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.qualifier = isEmpty(qualifier) ? null : qualifier;
    }

    /**
     * メジャーバージョンを取得します。
     *
     * @return メジャーバージョン
     */
    public int getMajor() {
        return major;
    }

    /**
     * マイナーバージョンを取得します。
     *
     * @return マイナーバージョン
     */
    public int getMinor() {
        return minor;
    }

    /**
     * パッチバージョンを取得します。
     *
     * @return パッチバージョン
     */
    public int getPatch() {
        return patch;
    }

    /**
     * バージョンのクオリファイアを取得します。
     *
     * @return クオリファイアまたは{@code null}
     */
    @Nullable
    public String getQualifier() {
        return qualifier;
    }

    /**
     * バージョンが公式版であるかどうかを判定します。<p>
     * クオリファイアが{@code null}の場合、公式版と見なします。
     *
     * @return 公式版の場合は{@code true}です。
     */
    public boolean isOfficial() {
        return qualifier == null;
    }

    /**
     * バージョンがベータ版かどうかを判定します。<p>
     * クオリファイアが{@code "beta"}で始まる場合、ベータ版と見なします。
     *
     * @return ベータ版の場合は{@code true}です。
     */
    public boolean isBeta() {
        return startsWithIgnoreCase(qualifier, "beta");
    }

    /**
     * バージョンがアルファ版かどうかを判定します。<p>
     * クオリファイアが{@code "alpha"}で始まる場合、アルファ版と見なします。
     *
     * @return アルファ版の場合は{@code true}です。
     */
    public boolean isAlpha() {
        return startsWithIgnoreCase(qualifier, "alpha");
    }

    /**
     * バージョンがスナップショット版かどうかを判定します。<p>
     * クオリファイアが{@code "snapshot"}で始まる場合、スナップショット版と見なします。
     *
     * @return スナップショット版の場合は{@code true}です。
     */
    public boolean isSnapshot() {
        return startsWithIgnoreCase(qualifier, "snapshot");
    }

    /**
     * 現在のバージョンが指定されたバージョンと同じ場合に{@code true}を返します。
     *
     * @param otherVersion 比較対象のバージョン
     * @return 現在のバージョンが比較先のバージョンと同じ場合は{@code true}です。
     */
    public boolean isSameVersion(@NotNull Version otherVersion) {
        return compareTo(otherVersion) == 0;
    }

    /**
     * 現在のバージョンが指定されたバージョン以上の場合に{@code true}を返します。
     *
     * @param otherVersion 比較対象のバージョン
     * @return 現在のバージョンが比較先のバージョン以上の場合は{@code true}です。
     */
    public boolean isUpperVersion(@NotNull Version otherVersion) {
        return compareTo(otherVersion) >= 0;
    }

    /**
     * 現在のバージョンが指定されたバージョン未満の場合に{@code true}を返します。
     *
     * @param otherVersion 比較対象のバージョン
     * @return 現在のバージョンが比較先のバージョン未満の場合は{@code true}です。
     */
    public boolean isLowerVersion(@NotNull Version otherVersion) {
        return compareTo(otherVersion) < 0;
    }

    /**
     * 文字列形式で指定された2つのバージョンを比較し、比較元のバージョンが比較先のバージョン以上なら{@code true}を返します。
     *
     * @param versionA 比較元のバージョンの文字列
     * @param versionB 比較先のバージョンの文字列
     * @return 比較元のバージョンが比較先のバージョン以上の場合は{@code true}です。
     */
    public static boolean isUpperVersion(@NotNull String versionA, @NotNull String versionB) {
        return of(versionA).isUpperVersion(of(versionB));
    }

    /**
     * 文字列形式で指定された2つのバージョンを比較し、比較元のバージョンが比較先のバージョン未満なら{@code true}を返します。
     *
     * @param versionA 比較元のバージョンの文字列
     * @param versionB 比較先のバージョンの文字列
     * @return 比較元のバージョンが比較先のバージョン未満の場合は{@code true}です。
     */
    public static boolean isLowerVersion(@NotNull String versionA, @NotNull String versionB) {
        return of(versionA).isLowerVersion(of(versionB));
    }

    /**
     * バージョンの文字列表現を返します。<p>
     * クオリファイアがあれば、それも含めた文字列を返します。
     *
     * @return バージョンの文字列
     */
    @Override
    @NotNull
    public String toString() {
        if (versionToString == null) {
            this.versionToString = patch == 0 ? major + "." + minor : major + "." + minor + "." + patch;
            if (qualifier != null) this.versionToString += "-" + qualifier;
        }
        return versionToString;
    }

    @Override
    public int hashCode() {
        int hash = 31, result = major;
        result = hash * result + minor;
        result = hash * result + patch;
        result = hash * result + Objects.hashCode(qualifier);
        return result;
    }

    @Override
    public int compareTo(@NotNull Version version) {
        var result = Integer.compare(major, version.major);
        if (result != 0) return result;
        result = Integer.compare(minor, version.minor);
        if (result != 0) return result;
        return Integer.compare(patch, version.patch);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Version)) return false;
        var version = (Version) obj;
        return major == version.major && minor == version.minor && patch == version.patch && Objects.equals(qualifier, version.qualifier);
    }
}