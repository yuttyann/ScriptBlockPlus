package com.github.yuttyann.scriptblockplus.script.option.chat;

import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus Server オプションクラス
 * @author yuttyann44581
 */
@OptionTag(name = "server", syntax = "@server ")
public class Server extends BaseOption {

    @Override
    @NotNull
    public Option newInstance() {
        return new Server();
    }

    @Override
    protected boolean isValid() throws Exception {
        Bukkit.broadcastMessage(StringUtils.setColor(getOptionValue()));
        return true;
    }
}