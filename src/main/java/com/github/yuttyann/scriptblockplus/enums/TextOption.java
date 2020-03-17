package com.github.yuttyann.scriptblockplus.enums;

import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

public enum TextOption {
	PLAYER("<player>"), WORLD("<world>");

	private String name;

	private TextOption(@NotNull String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@NotNull
	public String replace(@NotNull String source, @NotNull String value) {
		return StringUtils.replace(source, name, value);
	}

	@NotNull
	public static String replaceAll(@NotNull String source, @NotNull SBPlayer sbPlayer) {
		if (StringUtils.isNotEmpty(source)) {
			for (TextOption option : values()) {
				source = option.replace(source, option == PLAYER ? sbPlayer.getName() : sbPlayer.getWorld().getName());
			}
		}
		return source;
	}
}