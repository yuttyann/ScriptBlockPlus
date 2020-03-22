package com.github.yuttyann.scriptblockplus.script.option.time;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class OldCooldown extends BaseOption {

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
		int temp = getSecond();
		if (temp > 0) {
			short hour = (short) (temp / 3600);
			byte minute = (byte) (temp % 3600 / 60);
			byte second = (byte) (temp % 3600 % 60);
			SBConfig.ACTIVE_COOLDOWN.replace(hour, minute, second).send(getSBPlayer());
			return false;
		}
		int second = Integer.parseInt(getOptionValue());
		new Task(second, getScriptIndex(), getFullCoords()).runTaskTimer();
		return true;
	}

	private static class Task extends BukkitRunnable {

		TimeData timeData;

		Task(@NotNull TimeData timeData) {
			this.timeData = timeData;
		}

		Task(int second, int scriptIndex, @NotNull String fullCoords) {
			this.timeData = new TimeData(second + 1, scriptIndex, fullCoords);
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
		return TimeData.getSecond(TimeData.hashCode(getScriptIndex(), true, getFullCoords(), null, null));
	}
}