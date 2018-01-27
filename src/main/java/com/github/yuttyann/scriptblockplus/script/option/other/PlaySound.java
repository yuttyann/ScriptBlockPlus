package com.github.yuttyann.scriptblockplus.script.option.other;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public class PlaySound extends BaseOption {

	public PlaySound() {
		super("sound", "@sound:");
	}

	private class Task extends BukkitRunnable {

		private Sound soundType;
		private int volume;
		private int pitch;
		private boolean isWorldPlay;

		private Task(Sound soundType, int volume, int pitch, boolean isWorldPlay) {
			this.soundType = soundType;
			this.volume = volume;
			this.pitch = pitch;
			this.isWorldPlay = isWorldPlay;
		}

		@Override
		public void run() {
			Location location = getLocation();
			if (isWorldPlay) {
				location.getWorld().playSound(location, soundType, volume, pitch);
			} else {
				if (getSBPlayer().isOnline()) {
					getPlayer().playSound(location, soundType, volume, pitch);
				}
			}
		}
	}

	@Override
	protected boolean isValid() throws Exception {
		String[] array = StringUtils.split(getOptionValue(), "/");
		String[] sound = StringUtils.split(array[0], "-");
		Sound soundType = Sound.valueOf(sound[0].toUpperCase());
		int volume = Integer.valueOf(sound[1]);
		int pitch = Integer.valueOf(sound[2]);
		long delay = sound.length > 3 ? Long.parseLong(sound[3]) : 0;
		boolean isWorldPlay = array.length > 1 ? Boolean.parseBoolean(array[1]) : false;

		if (delay > 0) {
			new Task(soundType, volume, pitch, isWorldPlay).runTaskLater(getPlugin(), delay);
		} else {
			Location location = getLocation();
			if (isWorldPlay) {
				location.getWorld().playSound(location, soundType, volume, pitch);
			} else {
				if (getSBPlayer().isOnline()) {
					getPlayer().playSound(location, soundType, volume, pitch);
				}
			}
		}
		return true;
	}
}