package com.github.yuttyann.scriptblockplus.script.option.chat;

import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus ToPlayer オプションクラス
 * @author yuttyann44581
 */
@OptionTag(name = "toplayer", syntax = "@player ")
public class ToPlayer extends BaseOption {

    @Override
    @NotNull
    public Option newInstance() {
        return new ToPlayer();
    }

    @Override
    protected boolean isValid() throws Exception {
        Utils.sendColorMessage(getSBPlayer(), getOptionValue());
        return true;
    }
}