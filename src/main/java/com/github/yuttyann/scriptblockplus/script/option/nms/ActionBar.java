package com.github.yuttyann.scriptblockplus.script.option.nms;

import org.bukkit.scheduler.BukkitRunnable;

import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.jetbrains.annotations.NotNull;

public class ActionBar extends BaseOption {

	public ActionBar() {
		super("actionbar", "@actionbar:");
	}

	@NotNull
	@Override
	public Option newInstance() {
		return new ActionBar();
	}

	@Override
	protected boolean isValid() throws Exception {
		if (!Utils.isCraftBukkit()) {
			throw new UnsupportedOperationException();
		}
		String[] array = StringUtils.split(getOptionValue(), "/");
		String message = StringUtils.replaceColorCode(array[0], true);

		if (array.length > 1) {
			int stay = Integer.parseInt(array[1]);
			new Task(stay, message).runTaskTimer(getPlugin(), 0, 1);
		} else {
			NMSHelper.sendActionBar(getSBPlayer(), message);
		}
		return true;
	}

	private class Task extends BukkitRunnable {

		final int stay;
		final String message;

		int tick;

		Task(int stay, @NotNull String message) {
			this.tick = 0;
			this.stay = stay + 1;
			this.message = message;
		}

		@Override
		public void run() {
			try {
				if (tick++ >= stay) {
					NMSHelper.sendActionBar(getSBPlayer(), "");
					cancel();
				} else {
					NMSHelper.sendActionBar(getSBPlayer(), message);
				}
			} catch (ReflectiveOperationException e) {
				cancel();
			}
		}
	}
}