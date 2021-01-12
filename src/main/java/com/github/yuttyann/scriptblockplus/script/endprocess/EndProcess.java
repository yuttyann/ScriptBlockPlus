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
package com.github.yuttyann.scriptblockplus.script.endprocess;

import com.github.yuttyann.scriptblockplus.enums.InstanceType;
import com.github.yuttyann.scriptblockplus.manager.EndProcessManager;
import com.github.yuttyann.scriptblockplus.script.SBInstance;
import com.github.yuttyann.scriptblockplus.script.SBRead;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus EndProcess インターフェース
 * @author yuttyann44581
 */
public interface EndProcess extends SBInstance<EndProcess> {

    /**
     * インスタンスを生成します。
     * @return {@link EndProcess}
     */
    @NotNull
    default EndProcess newInstance() {
        return EndProcessManager.newInstance(this.getClass(), InstanceType.REFLECTION);
    }

    /**
     * スクリプトの実行が最後まで成功した場合に呼び出されます。
     * @param sbRead {@link SBRead}
     */
    void success(@NotNull SBRead sbRead);

    /**
     * スクリプトの実行が途中で失敗した場合に呼び出されます。
     * @param sbRead {@link SBRead}
     */
    void failed(@NotNull SBRead sbRead);
}