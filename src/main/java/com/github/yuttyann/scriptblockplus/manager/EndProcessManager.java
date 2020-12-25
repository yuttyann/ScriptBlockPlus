package com.github.yuttyann.scriptblockplus.manager;

import com.github.yuttyann.scriptblockplus.enums.InstanceType;
import com.github.yuttyann.scriptblockplus.script.endprocess.EndInventory;
import com.github.yuttyann.scriptblockplus.script.endprocess.EndMoneyCost;
import com.github.yuttyann.scriptblockplus.script.endprocess.EndProcess;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * ScriptBlockPlus EndProcessManager クラス
 * @author yuttyann44581
 */
public final class EndProcessManager {

    private static final List<SBConstructor<? extends EndProcess>> ENDPROCESS_LIST = new ArrayList<>();

    static {
        register(new SBConstructor<>(new EndInventory()));
        register(new SBConstructor<>(new EndMoneyCost()));
    }

    public static void register(@NotNull SBConstructor<EndProcess> endProcess) {
        ENDPROCESS_LIST.add(endProcess);
    }

    public static void forEach(@NotNull Consumer<EndProcess> action) {
        ENDPROCESS_LIST.forEach(c -> action.accept(c.newInstance(InstanceType.SBINSTANCE)));
    }

    public static void forEachFinally(@NotNull Consumer<EndProcess> action, @NotNull Runnable runnable) {
        try {
            forEach(action);
        } finally {
            runnable.run();
        }
    }

    @NotNull
    public static EndProcess newInstance(@NotNull Class<? extends EndProcess> endProcess, @NotNull InstanceType instanceType) {
        for (SBConstructor<? extends EndProcess> constructor : ENDPROCESS_LIST) {
            if (constructor.getDeclaringClass().equals(endProcess)) {
                return constructor.newInstance(instanceType);
            }
        }
        throw new NullPointerException(endProcess.getName() + " does not exist");
    }
}