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
package com.github.yuttyann.scriptblockplus.manager;

import com.github.yuttyann.scriptblockplus.script.endprocess.EndInventory;
import com.github.yuttyann.scriptblockplus.script.endprocess.EndMoneyCost;
import com.github.yuttyann.scriptblockplus.script.endprocess.EndProcess;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * ScriptBlockPlus EndProcessManager クラス
 * @author yuttyann44581
 */
public final class EndProcessManager {

    private static final List<SBInstance<EndProcess>> ENDPROCESS_LIST = new ArrayList<>();

    static {
        register(() -> new EndInventory());
        register(() -> new EndMoneyCost());
    }

    public static void register(@NotNull Supplier<EndProcess> newInstance) {
        ENDPROCESS_LIST.add(new SBInstance<>(newInstance));
    }

    public static void forEach(@NotNull Consumer<EndProcess> action) {
        ENDPROCESS_LIST.forEach(c -> action.accept(c.newInstance()));
    }

    public static void forEachFinally(@NotNull Consumer<EndProcess> action, @NotNull Runnable runnable) {
        try {
            forEach(action);
        } finally {
            runnable.run();
        }
    }

    @NotNull
    public static EndProcess newInstance(@NotNull Class<? extends EndProcess> endProcess) {
        for (var sbInstance : ENDPROCESS_LIST) {
            if (sbInstance.getDeclaringClass().equals(endProcess)) {
                return sbInstance.newInstance();
            }
        }
        throw new NullPointerException(endProcess.getName() + " does not exist");
    }
}