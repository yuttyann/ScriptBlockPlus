package com.github.yuttyann.scriptblockplus.script.option.chat;

import com.github.yuttyann.scriptblockplus.enums.CommandLog;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus Bypass オプションクラス
 * @author yuttyann44581
 */
@OptionTag(name = "bypass_op", syntax = "@bypass ")
public class BypassOP extends BaseOption {

    @Override
    @NotNull
    public Option newInstance() {
        return new BypassOP();
    }

    @Override
    protected boolean isValid() throws Exception {
        Player player = getSBPlayer().getPlayer();
        String command = StringUtils.setColor(getOptionValue());
        return CommandLog.supplier(player.getWorld(), () -> {
            if (player.isOp()) {
                return Utils.dispatchCommand(player, command);
            } else {
                try {
                    player.setOp(true);
                    return Utils.dispatchCommand(player, command);
                } finally {
                    player.setOp(false);
                }
            }
        });
    }
}