package com.github.yuttyann.scriptblockplus.enums;

import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public enum TextOption {
	PLAYER("<player>"), WORLD("<world>");

	private String name;

	private TextOption(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String replace(String source, String value) {
		return StringUtils.replace(source, name, value);
	}

	public static String replaceAll(String source, SBPlayer sbPlayer) {
		if (StringUtils.isNotEmpty(source)) {
			for (TextOption option : values()) {
				source = option.replace(source, option == PLAYER ? sbPlayer.getName() : sbPlayer.getWorld().getName());
			}
		}
		return source;
	}
}