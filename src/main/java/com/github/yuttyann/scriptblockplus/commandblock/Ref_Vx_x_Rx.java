package com.github.yuttyann.scriptblockplus.commandblock;

import java.lang.reflect.Method;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import com.github.yuttyann.scriptblockplus.enums.reflection.PackageType;
import com.github.yuttyann.scriptblockplus.utils.Utils;

class Ref_Vx_x_Rx implements CommandListener {

	private static final Class<?>[] PARAMS_EXECUTE_COMMAND = {
		getClass(PackageType.NMS, "ICommandListener"), CommandSender.class, String.class
	};

	private static final String CLASS_NAME_1 = "CommandBlockListenerAbstract";
	private static final String CLASS_NAME_2 = "TileEntityCommand";

	@Override
	public boolean executeCommand(CommandSender sender, Location location, String command) {
		try {
			Object iCommandListener = getICommandListener(sender, location);
			return executeCommand(iCommandListener, sender, command) > 0;
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
		return false;
	}

	protected int executeCommand(Object sender, CommandSender bSender, String command) throws ReflectiveOperationException {
		Method executeCommand = PackageType.NMS.getMethod(CLASS_NAME_1, "executeCommand", PARAMS_EXECUTE_COMMAND);
		return Integer.class.cast(executeCommand.invoke(null, sender, bSender, command));
	}

	private Object getICommandListener(CommandSender sender, Location location) throws ReflectiveOperationException {
		Object titleEntityCommand = newTileEntityCommand();
		setWorld(titleEntityCommand, location.getWorld());
		setLocation(titleEntityCommand, location);
		Object commandListener = getCommandBlock(titleEntityCommand);
		if (sender != null) {
			setName(commandListener, sender.getName());
		}
		return commandListener;
	}

	private void setName(Object commandListener, String name) throws ReflectiveOperationException {
		String methodName = Utils.isCB1710orLater() ? "setName" : "b";
		PackageType.NMS.invokeMethod(commandListener, CLASS_NAME_1, methodName, name);
	}

	private Object newTileEntityCommand() throws ReflectiveOperationException {
		return PackageType.NMS.newInstance(CLASS_NAME_2, (Object[]) null);
	}

	private void setWorld(Object titleEntityCommand, World world) throws ReflectiveOperationException {
		Object nmsWorld = PackageType.CB.invokeMethod(world, "CraftWorld", "getHandle", (Object[]) null);
		Class<?> nmsWorldClass = PackageType.NMS.getClass("World");
		PackageType.NMS.getMethod(CLASS_NAME_2, "a", nmsWorldClass).invoke(titleEntityCommand, nmsWorld);
	}

	private void setLocation(Object titleEntityCommand, Location location) throws ReflectiveOperationException {
		int x = location.getBlockX();
		int y = location.getBlockY();
		int z = location.getBlockZ();
		if (Utils.isCB18orLater()) {
			String methodName = Utils.isCB110orLater() ? "setPosition" : "a";
			Object instance = PackageType.NMS.newInstance("BlockPosition", x, y, z);
			PackageType.NMS.invokeMethod(titleEntityCommand, CLASS_NAME_2, methodName, instance);
		} else {
			PackageType.NMS.setFieldValue(CLASS_NAME_2, "x", titleEntityCommand, x);
			PackageType.NMS.setFieldValue(CLASS_NAME_2, "y", titleEntityCommand, y);
			PackageType.NMS.setFieldValue(CLASS_NAME_2, "z", titleEntityCommand, z);
		}
	}

	private Object getCommandBlock(Object titleEntityCommand) throws ReflectiveOperationException {
		String methodName = Utils.isCB1710orLater() ? "getCommandBlock" : "a";
		return PackageType.NMS.invokeMethod(titleEntityCommand, CLASS_NAME_2, methodName, (Object[]) null);
	}

	private static Class<?> getClass(PackageType packageType, String className) {
		try {
			return packageType.getClass(className);
		} catch (IllegalArgumentException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}