package com.github.yuttyann.scriptblockplus.script.option.chat;

import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
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
		String[] array = StringUtils.split(getOptionValue(), "/");
		String message = StringUtils.replaceColorCode(array[0], true);

		if (array.length > 1) {
			int stay = Integer.parseInt(array[1]);
			new Task(stay, message).runTaskTimer(getPlugin(), 0, 1);
		} else {
			sendActionBar(getSBPlayer(), message);
		}
		return true;
	}

	private void sendActionBar(@NotNull SBPlayer sbPlayer, @NotNull String message) {
		Player player = sbPlayer.getPlayer();
		executeCommand(player, "title " + player.getName() + " actionbar " + "{\"text\":\"" + message + "\"}", true);
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
				if (!getSBPlayer().isOnline() || tick++ >= stay) {
					sendActionBar(getSBPlayer(), "");
					cancel();
				} else {
					sendActionBar(getSBPlayer(), message);
				}
			} catch (Exception e) {
				cancel();
			}
		}
	}
}