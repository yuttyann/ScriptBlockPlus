package com.github.yuttyann.scriptblockplus.script.option.time;

import com.github.yuttyann.scriptblockplus.file.json.Json;
import com.github.yuttyann.scriptblockplus.file.json.PlayerTempJson;
import com.github.yuttyann.scriptblockplus.file.json.element.PlayerTemp;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;

/**
 * ScriptBlockPlus Cooldown オプションクラス
 * @author yuttyann44581
 */
public class Cooldown extends TimerOption {

	public Cooldown() {
		super("cooldown", "@cooldown:");
	}

	@Override
	@NotNull
	public Option newInstance() {
		return new Cooldown();
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
		json.load().getTimerTemp().add(new TimerTemp(params, getFileUniqueId(), getLocation(), getScriptType()));
		json.saveFile();
		return true;
	}

	@Override
	@NotNull
	protected Optional<TimerTemp> getTimerTemp() {
		Set<TimerTemp> timers = new PlayerTempJson(getFileUniqueId()).load().getTimerTemp();
		return get(timers, TimerTemp.hash(getFileUniqueId(), getLocation(), getScriptType()));
	}
}