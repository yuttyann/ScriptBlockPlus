package com.github.yuttyann.scriptblockplus.commandblock;

import java.util.regex.Pattern;

import com.github.yuttyann.scriptblockplus.commandblock.versions.Vx_x_Rx;
import com.github.yuttyann.scriptblockplus.commandblock.versions.v1_8_R1;
import com.github.yuttyann.scriptblockplus.enums.reflection.PackageType;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public final class FakeCommandBlock {

	private static final Pattern COMMAND_PATTERN = Pattern.compile("^@([parf])(?:\\[([\\w=,!-]*)\\])?$");

	private static final CommandListener COMMAND_LISTENER;

	static {
		Vx_x_Rx listener;
		switch (PackageType.getVersionName()) {
		case "v1_8_R1":
			listener = new v1_8_R1();
			break;
		default:
			listener = new Vx_x_Rx();
		}
		COMMAND_LISTENER = listener.getCommandBlock();
	}

	public static CommandListener getListener() {
		return COMMAND_LISTENER;
	}

	public static boolean isCommandPattern(String command) {
		String[] args = StringUtils.split(command, " ");
		for (int i = 1; i < args.length; i++) {
			if (isPattern(args[i])) {
				return true;
			}
		}
		return false;
	}

	public static boolean isPattern(String s) {
		return COMMAND_PATTERN.matcher(s).matches();
	}
}