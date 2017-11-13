package com.github.yuttyann.scriptblockplus.command;

import java.lang.reflect.Method;
import java.util.Objects;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.command.CommandSender;

final class ProxiedCommandSender {

	private static final Class<?> BUKKIT_PCS_CLASS;

	static {
		Class<?> clazz = null;
		try {
			clazz = Class.forName("org.bukkit.command.ProxiedCommandSender");
		} catch (ClassNotFoundException e) {}
		BUKKIT_PCS_CLASS = clazz;
	}

	private static Method method_Caller;
	private static Method method_Callee;

	private CommandSender sender;

	ProxiedCommandSender(CommandSender sender) {
		this.sender = Objects.requireNonNull(sender);
	}

	public CommandSender getCaller() {
		if (BUKKIT_PCS_CLASS != null) {
			try {
				if (method_Caller == null) {
					method_Caller = BUKKIT_PCS_CLASS.getMethod("getCaller");
				}
				return (CommandSender) method_Caller.invoke(sender, ArrayUtils.EMPTY_OBJECT_ARRAY);
			} catch (ReflectiveOperationException e) {}
		}
		return null;
	}

	public CommandSender getCallee() {
		if (BUKKIT_PCS_CLASS != null) {
			try {
				if (method_Callee == null) {
					method_Callee = BUKKIT_PCS_CLASS.getMethod("getCallee");
				}
				return (CommandSender) method_Callee.invoke(sender, ArrayUtils.EMPTY_OBJECT_ARRAY);
			} catch (ReflectiveOperationException e) {}
		}
		return null;
	}

	public static boolean isPCSClass(CommandSender sender) {
		if (sender == null || isFailed()) {
			return false;
		}
		for (Class<?> senderClass : sender.getClass().getInterfaces()) {
			if (senderClass == BUKKIT_PCS_CLASS) {
				return true;
			}
		}
		return false;
	}

	public static boolean isFailed() {
		return BUKKIT_PCS_CLASS == null;
	}
}