package com.github.yuttyann.scriptblockplus.commandblock;

import org.bukkit.command.CommandSender;

import com.github.yuttyann.scriptblockplus.enums.reflection.PackageType;

public interface ClassListener {

	static final String[] CLASS_NAME = {
			"ICommandListener", "CommandBlockListenerAbstract", "TileEntityCommand",
			"CommandDispatcher", "ChatComponentText", "BlockPosition", "CraftServer", "CraftWorld"
	};

	static final Class<?>[] PARAMS_EXECUTE_COMMAND = {
			getClass(PackageType.NMS, CLASS_NAME[0]), CommandSender.class, String.class
	};

	static Class<?> getClass(PackageType packageType, String className) {
		try {
			return packageType.getClass(className);
		} catch (IllegalArgumentException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}