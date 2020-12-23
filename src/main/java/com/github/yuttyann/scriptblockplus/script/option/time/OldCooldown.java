package com.github.yuttyann.scriptblockplus.script.option.time;

import com.github.yuttyann.scriptblockplus.file.Json;
import com.github.yuttyann.scriptblockplus.file.json.PlayerTempJson;
import com.github.yuttyann.scriptblockplus.file.json.element.PlayerTemp;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * ScriptBlockPlus OldCooldown オプションクラス
 * @author yuttyann44581
 */
@OptionTag(name = "oldcooldown", syntax = "@oldcooldown:")
public class OldCooldown extends TimerOption {

    public static final UUID UUID_OLDCOOLDOWN = UUID.nameUUIDFromBytes(OldCooldown.class.getName().getBytes());

    @Override
    @NotNull
    public Option newInstance() {
        return new OldCooldown();
    }

    @Override
    protected boolean isValid() throws Exception {
        if (inCooldown()) {
            return false;
        }
        long value = Integer.parseInt(getOptionValue()) * 1000L;
        long[] params = new long[] { System.currentTimeMillis(), value, 0L };
        params[2] = params[0] + params[1];

        Json<PlayerTemp> json = new PlayerTempJson(getFileUniqueId());
        json.load().getTimerTemp().add(new TimerTemp(getLocation(), getScriptType()).set(params));
        json.saveFile();
        return true;
    }

    @Override
    @NotNull
    protected UUID getFileUniqueId() {
        return UUID_OLDCOOLDOWN;
    }

    @Override
    @NotNull
    protected Optional<TimerTemp> getTimerTemp() {
        Set<TimerTemp> timers = new PlayerTempJson(getFileUniqueId()).load().getTimerTemp();
        return get(timers, new TimerTemp(getLocation(), getScriptType()));
    }
}