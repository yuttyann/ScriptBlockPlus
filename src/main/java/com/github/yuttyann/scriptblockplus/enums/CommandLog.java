package com.github.yuttyann.scriptblockplus.enums;

import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * ScriptBlockPlus HideCommandLog 列挙型
 * @author yuttyann44581
 */
@SuppressWarnings("deprecation")
public enum CommandLog {

    /**
     * コマンドログを表示する
     */
    TRUE(true),

    /**
     * コマンドログを非表示にする
     */
    FALSE(false);

    private boolean value;

    CommandLog(boolean value) {
        this.value = value;
    }

    public void setLogAdmin(@NotNull World world) {
        if (Utils.isCBXXXorLater("1.13")) {
            world.setGameRule(GameRule.LOG_ADMIN_COMMANDS, this.value);
        } else {
            world.setGameRuleValue("logAdminCommands", String.valueOf(this.value));
        }
    }

    public void setCommandFeedBack(@NotNull World world) {
        if (Utils.isCBXXXorLater("1.13")) {
            world.setGameRule(GameRule.SEND_COMMAND_FEEDBACK, this.value);
        } else {
            world.setGameRuleValue("sendCommandFeedback", String.valueOf(this.value));
        }
    }

    @NotNull
    private static CommandLog getLogAdmin(@NotNull World world) { 
        if (Utils.isCBXXXorLater("1.13")) {
            return world.getGameRuleValue(GameRule.LOG_ADMIN_COMMANDS) ? TRUE : FALSE;
        } else {
            return Boolean.valueOf(world.getGameRuleValue("logAdminCommands")) ? TRUE : FALSE;
        }
    }

    @NotNull
    public static CommandLog getCommandFeedBack(@NotNull World world) { 
        if (Utils.isCBXXXorLater("1.13")) {
            return world.getGameRuleValue(GameRule.SEND_COMMAND_FEEDBACK) ? TRUE : FALSE;
        } else {
            return Boolean.valueOf(world.getGameRuleValue("sendCommandFeedback")) ? TRUE : FALSE;
        }
    }

    public static void action(@NotNull World world, @NotNull Runnable runnable) {
        var logAdmin = CommandLog.getLogAdmin(world);
        var commandFeedBack = CommandLog.getCommandFeedBack(world);
        try {
            if (FALSE != logAdmin) {
                FALSE.setLogAdmin(world);
            }
            if (FALSE != commandFeedBack) {
                FALSE.setCommandFeedBack(world);
            }
            runnable.run();
        } finally {
            logAdmin.setLogAdmin(world);
            commandFeedBack.setCommandFeedBack(world);
        }
    }

    public static <T> T supplier(@NotNull World world, @NotNull Supplier<T> supplier) {
        var logAdmin = CommandLog.getLogAdmin(world);
        var commandFeedBack = CommandLog.getCommandFeedBack(world);
        try {
            if (FALSE != logAdmin) {
                FALSE.setLogAdmin(world);
            }
            if (FALSE != commandFeedBack) {
                FALSE.setCommandFeedBack(world);
            }
            return supplier.get();
        } finally {
            logAdmin.setLogAdmin(world);
            commandFeedBack.setCommandFeedBack(world);
        }
    }
}