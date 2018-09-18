package com.github.yuttyann.scriptblockplus.commandblock;

import java.util.regex.Pattern;

import com.github.yuttyann.scriptblockplus.commandblock.versions.Vx_x_Rx;
import com.github.yuttyann.scriptblockplus.enums.reflection.PackageType;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public final class FakeCommandBlock {

	@Deprecated
	private static final Pattern COMMAND_PATTERN = Pattern.compile("^@([parf])(?:\\[([\\w=,!-]*)\\])?$");

	private static final CommandListener COMMAND_LISTENER;

	static {
		Vx_x_Rx nmsVx_x_Rx;
		try {
			String pacageName = Vx_x_Rx.class.getPackage().getName();
			String className = pacageName + "." + PackageType.getVersionName();
			nmsVx_x_Rx = (Vx_x_Rx) Class.forName(className).newInstance();
		} catch (Exception e) {
			nmsVx_x_Rx = new Vx_x_Rx();
		}
		System.out.println(nmsVx_x_Rx.getClass().getSimpleName());
		COMMAND_LISTENER = nmsVx_x_Rx.getCommandBlock();
	}

	public static CommandListener getListener() {
		return COMMAND_LISTENER;
	}

	@Deprecated
	public static boolean isCommandPattern(String command) {
		String[] args = StringUtils.split(command, " ");
		for (int i = 1; i < args.length; i++) {
			if (isPattern(args[i])) {
				return true;
			}
		}
		return false;
	}

	@Deprecated
	public static boolean isPattern(String s) {
		return COMMAND_PATTERN.matcher(s).matches();
	}
}