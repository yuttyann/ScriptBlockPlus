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

    public void set(@NotNull World world) {
        if (Utils.isCBXXXorLater("1.13")) {
            world.setGameRule(GameRule.SEND_COMMAND_FEEDBACK, this.value);
        } else {
            world.setGameRuleValue("sendCommandFeedback", String.valueOf(this.value));
        }
    }

    @NotNull
    public static CommandLog get(@NotNull World world) {
        Boolean value;
        if (Utils.isCBXXXorLater("1.13")) {
            value = world.getGameRuleValue(GameRule.SEND_COMMAND_FEEDBACK);
        } else {
            value = Boolean.valueOf(world.getGameRuleValue("sendCommandFeedback"));
        }
        return value == null ? FALSE : value ? TRUE : FALSE;
    }

    public static void action(@NotNull World world, @NotNull Consumer<CommandLog> action) {
        CommandLog oldValue = CommandLog.get(world);
        try {
            if (CommandLog.FALSE != oldValue) {
                CommandLog.FALSE.set(world);
            }
            action.accept(oldValue);
        } finally {
            oldValue.set(world);
        }
    }

    public static <T> T supplier(@NotNull World world, @NotNull Supplier<T> supplier) {
        CommandLog oldValue = CommandLog.get(world);
        try {
            if (CommandLog.FALSE != oldValue) {
                CommandLog.FALSE.set(world);
            }
            return supplier.get();
        } finally {
            oldValue.set(world);
        }
    }
}