package com.github.yuttyann.scriptblockplus.script.option.other;

import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * ScriptBlockPlus PlaySound オプションクラス
 * @author yuttyann44581
 */
public class PlaySound extends BaseOption {

	public PlaySound() {
		super("sound", "@sound:");
	}

	@Override
	@NotNull
	public Option newInstance() {
		return new PlaySound();
	}

	@Override
	protected boolean isValid() throws Exception {
		String[] array = StringUtils.split(getOptionValue(), "/");
		String[] sound = StringUtils.split(array[0], "-");
		Sound soundType = Sound.valueOf(sound[0].toUpperCase());
		int volume = Integer.parseInt(sound[1]);
		int pitch = Integer.parseInt(sound[2]);
		long delay = sound.length > 3 ? Long.parseLong(sound[3]) : 0;
		boolean playWorld = array.length > 1 && Boolean.parseBoolean(array[1]);

		if (delay > 0) {
			new Task(soundType, volume, pitch, playWorld).runTaskLater(getPlugin(), delay);
		} else {
			playSound(soundType, volume, pitch, playWorld);
		}
		return true;
	}

	private class Task extends BukkitRunnable {

		Sound soundType;
		int volume;
		int pitch;
		boolean playWorld;

		Task(@NotNull Sound soundType, int volume, int pitch, boolean playWorld) {
			this.soundType = soundType;
			this.volume = volume;
			this.pitch = pitch;
			this.playWorld = playWorld;
		}

		@Override
		public void run() {
			playSound(soundType, volume, pitch, playWorld);
		}
	}

	private void playSound(@NotNull Sound soundType, int volume, int pitch, boolean sendAllPlayer) {
		Location location = getLocation();
		if (sendAllPlayer) {
			Objects.requireNonNull(location.getWorld()).playSound(location, soundType, volume, pitch);
		} else if (getSBPlayer().isOnline()) {
			getPlayer().playSound(location, soundType, volume, pitch);
		}
	}
}