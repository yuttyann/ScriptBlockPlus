package com.github.yuttyann.scriptblockplus.script.option.time;

import java.util.UUID;

import org.bukkit.scheduler.BukkitRunnable;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Cooldown extends BaseOption {

	public Cooldown() {
		super("cooldown", "@cooldown:");
	}

	@NotNull
	@Override
	public Option newInstance() {
		return new Cooldown();
	}

	@Override
	protected boolean isValid() throws Exception {
		int temp = getSecond();
		if (temp > 0) {
			short hour = (short) (temp / 3600);
			byte minute = (byte) (temp % 3600 / 60);
			byte second = (byte) (temp % 3600 % 60);
			Utils.sendMessage(getPlayer(), SBConfig.getActiveCooldownMessage(hour, minute, second));
			return false;
		}
		int second = Integer.parseInt(getOptionValue());
		new Task(second, getScriptIndex(), getFullCoords(), getUniqueId(), getScriptType()).runTaskTimer();
		return true;
	}

	private class Task extends BukkitRunnable {

		TimeData timeData;

		Task(@NotNull TimeData timeData) {
			this.timeData = timeData;
		}

		Task(int second, int scriptIndex, @NotNull String fullCoords, @NotNull UUID uuid, @NotNull ScriptType scriptType) {
			this.timeData = new TimeData(second + 1, scriptIndex, fullCoords, uuid, scriptType);
		}

		void runTaskTimer() {
			ScriptBlock.getInstance().getMapManager().getCooldowns().add(timeData);
			runTaskTimer(ScriptBlock.getInstance(), 0, 20L);
		}

		@Override
		public void run() {
			if (--timeData.second <= 0) {
				ScriptBlock.getInstance().getMapManager().getCooldowns().remove(timeData);
				cancel();
			}
		}
	}

	void deserialize(@NotNull TimeData timeData) {
		new Task(timeData).runTaskTimer();
	}

	private int getSecond() {
		return TimeData.getSecond(TimeData.hashCode(getScriptIndex(), false, getFullCoords(), getUniqueId(), getScriptType()));
	}
}