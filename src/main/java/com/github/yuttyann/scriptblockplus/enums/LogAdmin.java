package com.github.yuttyann.scriptblockplus.enums;

import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;

public enum LogAdmin {
    TRUE(true),
    FALSE(false);

    private boolean value;

    private LogAdmin(boolean value) {
        this.value = value;
    }

    public void set(@NotNull World world) {
        if (Utils.isCBXXXorLater("1.13")) {
            world.setGameRule(GameRule.LOG_ADMIN_COMMANDS, this.value);
        } else {
            world.setGameRuleValue("logAdminCommands", String.valueOf(this.value));
        }
    }

    @NotNull
    public static LogAdmin get(@NotNull World world) {
        Boolean value;
        if (Utils.isCBXXXorLater("1.13")) {
            value = world.getGameRuleValue(GameRule.LOG_ADMIN_COMMANDS);
        } else {
            value = Boolean.valueOf(world.getGameRuleValue("logAdminCommands"));
        }
        return value == null ? FALSE : value ? TRUE : FALSE;
    }

    public static void action(@NotNull World world, @NotNull Consumer<LogAdmin> action) {
        LogAdmin oldValue = LogAdmin.get(world);
        try {
            if (LogAdmin.FALSE != oldValue) {
                LogAdmin.FALSE.set(world);
            }
            action.accept(oldValue);
        } finally {
            oldValue.set(world);
        }
    }

    public static <T> T function(@NotNull World world, @NotNull Function<LogAdmin, T> function) {
        LogAdmin oldValue = LogAdmin.get(world);
        try {
            if (LogAdmin.FALSE != oldValue) {
                LogAdmin.FALSE.set(world);
            }
            return function.apply(oldValue);
        } finally {
            oldValue.set(world);
        }
    }
}