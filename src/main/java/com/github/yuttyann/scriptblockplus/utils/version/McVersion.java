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

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

/**
 * Minecraftのバージョン管理と比較をサポートするクラスです。<p>
 * また、ゲーム内で利用可能なバージョンの一覧を定義しており、各バージョンがサポートされているかどうかを簡単に確認できます。
 *
 * @author yuttyann44581
 */
public final class McVersion extends Version {

    /**
     * Minecraft 26.x 系列
     */
    public static final McVersion V_26_3 = v(26, 3),  V_26_2 = v(26, 2),
                                  V_26_1_2 = v(26, 1, 2), V_26_1_1 = v(26, 1, 1), V_26_1 = v(26, 1);

    /**
     * Minecraft 1.21.x 系列
     */
    public static final McVersion V_1_21_11 = v(1, 21, 11), V_1_21_10 = v(1, 21, 10), V_1_21_9 = v(1, 21, 9), V_1_21_8 = v(1, 21, 8),
                                  V_1_21_7 = v(1, 21, 7), V_1_21_6 = v(1, 21, 6), V_1_21_5 = v(1, 21, 5), V_1_21_4 = v(1, 21, 4),
                                  V_1_21_3 = v(1, 21, 2), V_1_21_2 = v(1, 21, 2), V_1_21_1 = v(1, 21, 1), V_1_21 = v(1, 21);

    /**
     * Minecraft 1.20.x 系列
     */
    public static final McVersion V_1_20_6 = v(1, 20, 6), V_1_20_5 = v(1, 20, 5), V_1_20_4 = v(1, 20, 4),
                                  V_1_20_3 = v(1, 20, 3), V_1_20_2 = v(1, 20, 2), V_1_20_1 = v(1, 20, 1), V_1_20 = v(1, 20);

    /**
     * Minecraft 1.19.x 系列
     */
    public static final McVersion V_1_19_4 = v(1, 19, 4), V_1_19_3 = v(1, 19, 3),
                                  V_1_19_2 = v(1, 19, 2), V_1_19_1 = v(1, 19, 1), V_1_19 = v(1, 19);

    /**
     * Minecraft 1.18.x 系列
     */
    public static final McVersion V_1_18_2 = v(1, 18, 2), V_1_18_1 = v(1, 18, 1), V_1_18 = v(1, 18);

    /**
     * Minecraft 1.17.x 系列
     */
    public static final McVersion V_1_17_1 = v(1, 17, 1), V_1_17 = v(1, 17);

    /**
     * Minecraft 1.16.x 系列
     */
    public static final McVersion V_1_16_5 = v(1, 16, 5), V_1_16_4 = v(1, 16, 4), V_1_16_3 = v(1, 16, 3),
                                  V_1_16_2 = v(1, 16, 2), V_1_16_1 = v(1, 16, 1), V_1_16 = v(1, 16);

    /**
     * Minecraft 1.15.x 系列
     */
    public static final McVersion V_1_15_2 = v(1, 15, 2), V_1_15_1 = v(1, 15, 1), V_1_15 = v(1, 15);

    /**
     * Minecraft 1.14.x 系列
     */
    public static final McVersion V_1_14_4 = v(1, 14, 4), V_1_14_3 = v(1, 14, 3), V_1_14_2 = v(1, 14, 2),
                                  V_1_14_1 = v(1, 14, 1), V_1_14 = v(1, 14);

    /**
     * Minecraft 1.13.x 系列
     */
    public static final McVersion V_1_13_2 = v(1, 13, 2), V_1_13_1 = v(1, 13, 1), V_1_13 = v(1, 13);

    /**
     * Minecraft 1.12.x 系列
     */
    public static final McVersion V_1_12_2 = v(1, 12, 2), V_1_12_1 = v(1, 12, 1), V_1_12 = v(1, 12);

    /**
     * Minecraft 1.11.x 系列
     */
    public static final McVersion V_1_11_2 = v(1, 11, 2), V_1_11_1 = v(1, 11, 1), V_1_11 = v(1, 11);

    /**
     * Minecraft 1.10.x 系列
     */
    public static final McVersion V_1_10_2 = v(1, 10, 2), V_1_10_1 = v(1, 12, 1), V_1_10 = v(1, 10);

    /**
     * Minecraft 1.9.x 系列
     */
    public static final McVersion V_1_9_4 = v(1, 9, 4), V_1_9_3 = v(1, 9, 3), V_1_9_2 = v(1, 9, 2),
                                  V_1_9_1 = v(1, 9, 1), V_1_9 = v(1, 9);

    /**
     * バージョン情報（メジャー、マイナー、パッチ）を指定して{@link McVersion}インスタンスを生成します。<p>
     * 引数の長さによってメジャー、マイナー、パッチのいずれかを決定します。
     *
     * @param parts メジャー、マイナー、パッチの配列
     * @return {@link McVersion}インスタンス
     * @throws IllegalArgumentException 引数が無効な場合にスローされます
     */
    @NotNull
    public static McVersion v(@NotNull int... parts) {
        if (ArrayUtils.isEmpty(parts)) {
            throw new IllegalArgumentException("empty");
        }
        switch (parts.length) {
            case 1:
                return new McVersion(parts[0], 0, 0);
            case 2:
                return new McVersion(parts[0], parts[1], 0);
            case 3:
                return new McVersion(parts[0], parts[1], parts[2]);
            default:
                throw new IllegalArgumentException(Arrays.toString(parts));
        }
    }

    public static final Version GAME_VERSION = MinecraftVersionResolver.resolve(Bukkit.getServer());

    private Boolean supported;

    /**
     * メジャーバージョン、マイナーバージョン、パッチバージョンを設定します。
     *
     * @param major メジャーバージョン
     * @param minor マイナーバージョン
     * @param patch パッチバージョン
     */
    private McVersion(int major, int minor, int incremental) {
        super(major, minor, incremental, null);
    }

    /**
     * 現在のバージョンがゲームバージョンと一致する場合は{@code true}を返します。
     *
     * @return 現在のバージョンがゲームバージョンと一致する場合は{@code true}です。
     */
    public boolean isGameVersion() {
        return compareTo(GAME_VERSION) == 0;
    }

    /**
     * 現在のバージョンがゲームバージョン以上である場合は{@code true}を返します。
     *
     * @return サポートされているバージョンの場合は{@code true}です。
     */
    public boolean isSupported() {
        if (supported == null) this.supported = GAME_VERSION.isUpperVersion(this);
        return supported;
    }

    /**
     * 現在のバージョンがゲームバージョン未満である場合は{@code true}を返します。
     *
     * @return サポートされていないバージョンの場合は{@code true}です。
     */
    public boolean isUnSupported() {
        if (supported == null) this.supported = GAME_VERSION.isUpperVersion(this);
        return !supported;
    }
}
