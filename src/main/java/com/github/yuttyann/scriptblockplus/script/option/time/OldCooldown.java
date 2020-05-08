package com.github.yuttyann.scriptblockplus.script.option.time;

import com.github.yuttyann.scriptblockplus.file.json.PlayerTemp;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * ScriptBlockPlus OldCooldown オプションクラス
 * @author yuttyann44581
 */
public class OldCooldown extends TimerOption {

	public OldCooldown() {
		super("oldcooldown", "@oldcooldown:");
	}

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

		PlayerTemp temp = getSBPlayer().getPlayerTemp();
		temp.getInfo().getTimerTemp().add(new TimerTemp(params, getFullCoords(), getScriptType()));
		temp.save();
		return true;
	}

	@Override
	@NotNull
	protected Optional<TimerTemp> getTimerTemp() {
		Set<TimerTemp> set = getSBPlayer().getPlayerTemp().getInfo().getTimerTemp();
		int hash = Objects.hash(true, getFullCoords(), getScriptType());
		return Optional.ofNullable(StreamUtils.fOrElse(set, t -> t.hashCode() == hash, null));
	}
}