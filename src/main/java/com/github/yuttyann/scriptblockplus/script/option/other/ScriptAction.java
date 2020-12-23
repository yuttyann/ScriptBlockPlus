package com.github.yuttyann.scriptblockplus.script.option.other;

import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.event.block.Action;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * ScriptBlockPlus ScriptAction オプションクラス
 * @author yuttyann44581
 */
@OptionTag(name = "scriptaction", syntax = "@scriptaction:")
public class ScriptAction extends BaseOption {

    public static final String KEY = Utils.randomUUID();

    @Override
    @NotNull
    public Option newInstance() {
        return new ScriptAction();
    }

    @Override
    protected boolean isValid() throws Exception {
        Action action = getTempMap().get(KEY);
        String[] array = StringUtils.split(getOptionValue(), ",");
        return Arrays.stream(array).allMatch(s -> equals(action, s));
    }

    private boolean equals(@Nullable Action action, @NotNull String type) {
        if (action == null) {
            return false;
        }
        if (type.equalsIgnoreCase("shift")) {
            return getPlayer().isSneaking();
        }
        return ScriptType.INTERACT.equals(getScriptType()) && action == getAction(type);
    }

    @Nullable
    private Action getAction(@NotNull String actionType) {
        if (actionType.equalsIgnoreCase("left")) {
            return Action.LEFT_CLICK_BLOCK;
        } else if (actionType.equalsIgnoreCase("right")) {
            return Action.RIGHT_CLICK_BLOCK;
        }
        return null;
    }
}