package com.github.yuttyann.scriptblockplus.script.option.time;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.manager.EndProcessManager;
import com.github.yuttyann.scriptblockplus.script.SBRead;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * ScriptBlockPlus Delay オプションクラス
 * @author yuttyann44581
 */
@OptionTag(name = "delay", syntax = "@delay:")
public class Delay extends BaseOption implements Runnable {

    public static final Set<TimerTemp> DELAY_SET = new HashSet<>();

    private boolean saveDelay;

    @Override
    @NotNull
    public Option newInstance() {
        return new Delay();
    }

    @Override
    public boolean isFailedIgnore() {
        return true;
    }

    @Override
    protected boolean isValid() throws Exception {
        var array = StringUtils.split(getOptionValue(), "/");
        saveDelay = array.length <= 1 || Boolean.parseBoolean(array[1]);
        if (saveDelay && DELAY_SET.contains(new TimerTemp(getUniqueId(), getLocation(), getScriptType()))) {
            SBConfig.ACTIVE_DELAY.send(getSBPlayer());
        } else {
            if (saveDelay) {
                DELAY_SET.add(new TimerTemp(getUniqueId(), getLocation(), getScriptType()));
            }
            ((SBRead) getTempMap()).setInitialize(false);
            Bukkit.getScheduler().runTaskLater(ScriptBlock.getInstance(), this, Long.parseLong(array[0]));
        }
        return false;
    }

    @Override
    public void run() {
        if (saveDelay) {
            DELAY_SET.remove(new TimerTemp(getUniqueId(), getLocation(), getScriptType()));
        }
        var sbRead = (SBRead) getTempMap();
        if (getSBPlayer().isOnline()) {
            sbRead.setInitialize(true);
            sbRead.read(getScriptIndex() + 1);
        } else {
            EndProcessManager.forEachFinally(e -> e.failed(sbRead), () -> sbRead.clear());
        }
    }
}