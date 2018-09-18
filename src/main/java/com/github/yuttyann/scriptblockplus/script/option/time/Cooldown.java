package com.github.yuttyann.scriptblockplus.script.option.time;

import java.util.UUID;

import org.bukkit.scheduler.BukkitRunnable;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class Cooldown extends BaseOption {

	private TimeData timeData;

	public Cooldown() {
		super("cooldown", "@cooldown:");
	}

	private class Task extends BukkitRunnable {

		private Task(TimeData timeData) {
			Cooldown.this.timeData = timeData;
		}

		private Task(int index, int second, UUID uuid, String fullCoords, ScriptType scriptType) {
			timeData = new TimeData(index, second + 1, false);
			timeData.uuid = uuid;
			timeData.fullCoords = fullCoords;
			timeData.scriptType = scriptType;
		}

		public void runTaskTimer() {
			ScriptBlock.getInstance().getMapManager().getCooldowns().put(timeData.hashCode(), timeData);
			runTaskTimer(ScriptBlock.getInstance(), 0, 20L);
		}

		@Override
		public void run() {
			if (--timeData.second <= 0) {
				ScriptBlock.getInstance().getMapManager().getCooldowns().remove(timeData.hashCode());
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
		new Task(getScriptIndex(), second, getUniqueId(), getFullCoords(), getScriptType()).runTaskTimer();
		return true;
	}

	void deserialize(TimeData timeData) {
		new Task(timeData).runTaskTimer();
	}

	private int getSecond() {
		int hash = TimeData.hashCode(getScriptIndex(), getUniqueId(), getFullCoords(), getScriptType());
		TimeData timeData = getMapManager().getCooldowns().get(hash);
		return timeData == null ? -1 : timeData.second;
	}
}