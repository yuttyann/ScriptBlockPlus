package com.github.yuttyann.scriptblockplus.script.option.other;

import com.github.yuttyann.scriptblockplus.script.ScriptKey;
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
        return Arrays.stream(StringUtils.split(getOptionValue(), ',')).allMatch(s -> equals(action, s));
    }

    private boolean equals(@Nullable Action action, @NotNull String type) {
        if (action == null) {
            return false;
        }
        if (type.equalsIgnoreCase("shift")) {
            return getPlayer().isSneaking();
        }
        return ScriptKey.INTERACT.equals(getScriptKey()) && action == getAction(type);
    }

    @Nullable
    private Action getAction(@NotNull String action) {
        if (action.equalsIgnoreCase("left")) {
            return Action.LEFT_CLICK_BLOCK;
        } else if (action.equalsIgnoreCase("right")) {
            return Action.RIGHT_CLICK_BLOCK;
        }
        return null;
    }
}