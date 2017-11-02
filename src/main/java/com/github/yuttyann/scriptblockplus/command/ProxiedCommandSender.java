package com.github.yuttyann.scriptblockplus.command;

import java.lang.reflect.Method;
import java.util.Objects;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.command.CommandSender;

final class ProxiedCommandSender {

	private static final Class<?> BUKKIT_PS_CLASS;

	static {
		Class<?> clazz = null;
		try {
			clazz = Class.forName("org.bukkit.command.ProxiedCommandSender");
		} catch (ClassNotFoundException e) {}
		BUKKIT_PS_CLASS = clazz;
	}

	private static Method methodGetCaller;
	private static Method methodGetCallee;

	private CommandSender sender;

	ProxiedCommandSender(CommandSender sender) {
		this.sender = Objects.requireNonNull(sender);
	}

	public CommandSender getCaller() {
		if (BUKKIT_PS_CLASS != null) {
			try {
				if (methodGetCaller == null) {
					methodGetCaller = BUKKIT_PS_CLASS.getMethod("getCaller");
				}
				return (CommandSender) methodGetCaller.invoke(sender, ArrayUtils.EMPTY_OBJECT_ARRAY);
			} catch (ReflectiveOperationException e) {}
		}
		return null;
	}

	public CommandSender getCallee() {
		if (BUKKIT_PS_CLASS != null) {
			try {
				if (methodGetCallee == null) {
					methodGetCallee = BUKKIT_PS_CLASS.getMethod("getCallee");
				}
				return (CommandSender) methodGetCallee.invoke(sender, ArrayUtils.EMPTY_OBJECT_ARRAY);
			} catch (ReflectiveOperationException e) {}
		}
		return null;
	}

	public static boolean checkClass(CommandSender sender) {
		if (sender == null || isFailed()) {
			return false;
		}
		for (Class<?> senderClass : sender.getClass().getInterfaces()) {
			if (senderClass == BUKKIT_PS_CLASS) {
				return true;
			}
		}
		return false;
	}

	public static boolean isFailed() {
		return BUKKIT_PS_CLASS == null;
	}
}