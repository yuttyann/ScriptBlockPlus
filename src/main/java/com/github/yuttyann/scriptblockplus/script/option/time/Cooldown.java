package com.github.yuttyann.scriptblockplus.script.option.time;

import java.util.UUID;

import org.bukkit.scheduler.BukkitRunnable;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class Cooldown extends BaseOption {

	public Cooldown() {
		super("cooldown", "@cooldown:");
	}

	private class Task extends BukkitRunnable {

		private TimeData timeData;

		private Task(TimeData timeData) {
			this.timeData = timeData;
		}

		private Task(int scriptIndex, int second, String fullCoords, UUID uuid, ScriptType scriptType) {
			this.timeData = new TimeData(scriptIndex, second + 1, false, fullCoords, uuid, scriptType);
		}

		private void runTaskTimer() {
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

	@Override
	protected boolean isValid() throws Exception {
		int original = getSecond();
		if (original > 0) {
			short hour = (short) (original / 3600);
			byte minute = (byte) (original % 3600 / 60);
			byte second = (byte) (original % 3600 % 60);
			Utils.sendMessage(getPlayer(), SBConfig.getActiveCooldownMessage(hour, minute, second));
			return false;
		}
		int second = Integer.parseInt(getOptionValue());
		new Task(getScriptIndex(), second, getFullCoords(), getUniqueId(), getScriptType()).runTaskTimer();
		return true;
	}

	void deserialize(TimeData timeData) {
		new Task(timeData).runTaskTimer();
	}

	private int getSecond() {
		return TimeData.getSecond(TimeData.hashCode(getScriptIndex(), true, getFullCoords(), getUniqueId(), getScriptType()));
	}
}