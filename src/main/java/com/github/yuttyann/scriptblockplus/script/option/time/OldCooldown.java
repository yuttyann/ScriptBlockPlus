package com.github.yuttyann.scriptblockplus.script.option.time;

import java.util.Objects;

import org.bukkit.scheduler.BukkitRunnable;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class OldCooldown extends BaseOption {

	private TimeData timeData;

	public OldCooldown() {
		super("oldcooldown", "@oldcooldown:");
	}

	private class Task extends BukkitRunnable {

		private Task(TimeData timeData) {
			OldCooldown.this.timeData = timeData;
		}

		private Task(int index, int second, String fullCoords) {
			timeData = new TimeData(getScriptIndex(), second + 1, true);
			timeData.fullCoords = fullCoords;
		}

		public void runTaskTimer() {
			ScriptBlock plugin = ScriptBlock.getInstance();
			plugin.getMapManager().getCooldowns().put(timeData.hashCode(), timeData);
			runTaskTimer(plugin, 0, 20L);
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
		new Task(getScriptIndex(), second, getFullCoords()).runTaskTimer();
		return true;
	}

	void deserialize(TimeData timeData) {
		new Task(timeData).runTaskTimer();
	}

	private int getSecond() {
		int hashKey = Objects.hash(getScriptIndex(), getFullCoords());
		TimeData timeData = getMapManager().getCooldowns().get(hashKey);
		return timeData == null ? -1 : timeData.second;
	}
}