package com.github.yuttyann.scriptblockplus.script.option.other;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public class Sound extends BaseOption {

	public Sound() {
		super("sound", "@sound:");
	}

	private class Task extends BukkitRunnable {

		private org.bukkit.Sound type;
		private int volume;
		private int pitch;
		private boolean isWorld;

		public Task(org.bukkit.Sound type, int volume, int pitch, boolean isWorld) {
			this.type = type;
			this.volume = volume;
			this.pitch = pitch;
			this.isWorld = isWorld;
		}

		@Override
		public void run() {
			BlockCoords blockCoords = getBlockCoords();
			if (isWorld) {
				blockCoords.getWorld().playSound(blockCoords, type, volume, pitch);
			} else {
				if (getSBPlayer().isOnline()) {
					getPlayer().playSound(blockCoords, type, volume, pitch);
				}
			}
		}
	}

	@Override
	public boolean isValid() throws Exception {
		String[] array = StringUtils.split(getOptionValue(), "/");
		String[] sound = StringUtils.split(array[0], "-");
		org.bukkit.Sound type = org.bukkit.Sound.valueOf(sound[0].toUpperCase());
		int volume = Integer.valueOf(sound[1]);
		int pitch = Integer.valueOf(sound[2]);
		long delay = sound.length > 3 ? Long.parseLong(sound[3]) : 0;
		boolean isWorld = array.length > 1 ? Boolean.parseBoolean(array[1]) : false;

		if (delay > 0) {
			new Task(type, volume, pitch, isWorld).runTaskLater(getPlugin(), delay);
		} else {
			Location location = getBlockCoords();
			if (isWorld) {
				location.getWorld().playSound(location, type, volume, pitch);
			} else {
				getPlayer().playSound(location, type, volume, pitch);
			}
		}
		return true;
	}
}