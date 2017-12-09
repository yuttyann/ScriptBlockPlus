package com.github.yuttyann.scriptblockplus.commandblock;

import java.util.regex.Pattern;

import com.github.yuttyann.scriptblockplus.enums.PackageType;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public final class FakeCommandBlock {

	private static final Pattern COMMAND_PATTERN = Pattern.compile("^@([parf])(?:\\[([\\w=,!-]*)\\])?$");

	private static final CommandBlockListener LISTENER;

	static {
		CommandBlockListener listener = null;
		if (Utils.isCB18orLater()) {
			listener = new Ref_Vx_x_Rx();
		} else {
			String packageName = PackageType.getName();
			if (packageName.equals("v1_7_R1")) {
				listener = new v1_7_R1();
			} else if (packageName.equals("v1_7_R2")) {
				listener = new v1_7_R2();
			} else if (packageName.equals("v1_7_R3")) {
				listener = new v1_7_R3();
			} else if (packageName.equals("v1_7_R4")) {
				listener = new v1_7_R4();
			}
		}
		LISTENER = listener;
	}

	public static CommandBlockListener getListener() {
		return LISTENER;
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