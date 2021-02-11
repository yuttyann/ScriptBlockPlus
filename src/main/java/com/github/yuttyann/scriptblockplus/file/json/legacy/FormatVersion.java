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
package com.github.yuttyann.scriptblockplus.file.json.legacy;

import java.io.Serializable;

import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus FormatVersion クラス
 * @author yuttyann44581
 */
public final class FormatVersion implements Serializable {

    private static final long serialVersionUID = 1L;

    public static FormatVersion ZERO = new FormatVersion(0.0);
    public static FormatVersion CURRENT = new FormatVersion(2.0);

    private final double version;

    /**
     * コンストラクタ
     */
    public FormatVersion() {
        this(CURRENT.getVersion());
    }

    /**
     * コンストラクタ
     * @param version - バージョン
     */
    private FormatVersion(final double version) {
        this.version = version;
    }

    /**
     * バージョンを取得します。
     * @return {@link double} - バージョン
     */
    public double getVersion() {
        return version;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) {
            return true;
        }
        return obj instanceof FormatVersion ? this.version == ((FormatVersion) obj).getVersion() : false;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(version);
    }
}