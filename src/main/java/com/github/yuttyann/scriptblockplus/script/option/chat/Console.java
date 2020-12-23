package com.github.yuttyann.scriptblockplus.script.option.chat;

import com.github.yuttyann.scriptblockplus.enums.CommandLog;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus Console オプションクラス
 * @author yuttyann44581
 */
@OptionTag(name = "console", syntax = "@console ")
public class Console extends BaseOption {

    @Override
    @NotNull
    public Option newInstance() {
        return new Console();
    }

    @Override
    protected boolean isValid() throws Exception {
        String command = StringUtils.setColor(getOptionValue());
        return CommandLog.supplier(getSBPlayer().getWorld(), () -> Utils.dispatchCommand(Bukkit.getConsoleSender(), command));
    }
}