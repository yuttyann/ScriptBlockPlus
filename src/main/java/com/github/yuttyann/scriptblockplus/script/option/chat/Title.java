package com.github.yuttyann.scriptblockplus.script.option.chat;

import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Title extends BaseOption {

	public Title() {
		super("title", "@title:");
	}

	@NotNull
	@Override
	public Option newInstance() {
		return new Title();
	}

	@Override
	protected boolean isValid() throws Exception {
		String[] array = StringUtils.split(getOptionValue(), "/");
		String title = StringUtils.replaceColorCode(array[0], true);
		String subtitle = StringUtils.replaceColorCode(array[1], true);
		int fadeIn = 10;
		int stay = 40;
		int fadeOut = 10;
		if (array.length == 3) {
			String[] times = StringUtils.split(array[2], "-");
			if (times.length == 3) {
				fadeIn = Integer.parseInt(times[0]);
				stay = Integer.parseInt(times[1]);
				fadeOut = Integer.parseInt(times[2]);
			}
		}
		sendTitle(getPlayer(), title, subtitle, fadeIn, stay, fadeOut);
		return true;
	}

	private void sendTitle(@NotNull Player player, @Nullable String title, @Nullable String subtitle, int fadeIn, int stay, int fadeOut) throws ReflectiveOperationException {
		if (Utils.isCBXXXorLater("1.12")) {
			player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
		} else {
			String name = player.getName();
			executeCommand(player, "title " + name + " times " + fadeIn + " " + stay + " " + fadeOut, true);
			executeCommand(player, "title " + name + " subtitle " + "{\"text\":\"" + subtitle + "\"}", true);
			executeCommand(player, "title " + name + " title " + "{\"text\":\"" + title + "\"}", true);
		}
	}
}