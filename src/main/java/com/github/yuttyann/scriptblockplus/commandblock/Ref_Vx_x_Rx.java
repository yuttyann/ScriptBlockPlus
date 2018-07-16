package com.github.yuttyann.scriptblockplus.commandblock;

import java.lang.reflect.Method;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import com.github.yuttyann.scriptblockplus.enums.reflection.PackageType;
import com.github.yuttyann.scriptblockplus.utils.Utils;

class Ref_Vx_x_Rx implements CommandListener {

	private static final String[] CLASS_NAME = {
			"ICommandListener", "CommandBlockListenerAbstract", "TileEntityCommand",
			"CommandDispatcher", "ChatComponentText", "BlockPosition", "CraftServer", "CraftWorld"
	};

	private static final Class<?>[] PARAMS_EXECUTE_COMMAND = {
			getClass(PackageType.NMS, CLASS_NAME[0]), CommandSender.class, String.class
	};

	@Override
	@Deprecated
	public final boolean executeCommand(CommandSender sender, Location location, String command) {
		try {
			Object iCommandListener = getICommandListener(sender, location);
			return executeCommand(iCommandListener, sender, command) > 0;
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
		return false;
	}

	protected int executeCommand(Object sender, CommandSender bSender, String command) throws ReflectiveOperationException {
		int completed = 0;
		if (Utils.isCBXXXorLater("1.13")) {
			Object wrapper = PackageType.NMS.invokeMethod(sender, CLASS_NAME[1], "getWrapper", (Object[]) null);
			Object server = PackageType.CB.invokeMethod(bSender.getServer(), CLASS_NAME[6], "getServer", (Object[]) null);
			Object dispatcher = PackageType.NMS.invokeMethod(server, CLASS_NAME[6], "getCommandDispatcher", (Object[]) null);
			Object dispatch = PackageType.NMS.invokeMethod(dispatcher, CLASS_NAME[3], "dispatchServerCommand", wrapper, command);
			completed = Integer.class.cast(dispatch).intValue();
		} else {
			Method executeCommand = PackageType.NMS.getMethod(CLASS_NAME[1], "executeCommand", PARAMS_EXECUTE_COMMAND);
			completed = Integer.class.cast(executeCommand.invoke(null, sender, bSender, command)).intValue();
		}
		return completed;
	}

	protected Object getICommandListener(CommandSender sender, Location location) throws ReflectiveOperationException {
		Object titleEntityCommand = newTileEntityCommand();
		setWorld(titleEntityCommand, location.getWorld());
		setLocation(titleEntityCommand, location);
		Object commandListener = getCommandBlock(titleEntityCommand);
		if (sender != null) {
			setName(commandListener, sender.getName());
		}
		return commandListener;
	}

	protected void setName(Object commandListener, String name) throws ReflectiveOperationException {
		String methodName = Utils.isCBXXXorLater("1.7.10") ? "setName" : "b";
		if (Utils.isCBXXXorLater("1.13")) {
			Object cName = PackageType.NMS.newInstance(CLASS_NAME[4], name);
			PackageType.NMS.invokeMethod(commandListener, CLASS_NAME[1], methodName, cName);
		} else {
			PackageType.NMS.invokeMethod(commandListener, CLASS_NAME[1], methodName, name);
		}
	}

	protected Object newTileEntityCommand() throws ReflectiveOperationException {
		return PackageType.NMS.newInstance(CLASS_NAME[1], (Object[]) null);
	}

	protected void setWorld(Object titleEntityCommand, World world) throws ReflectiveOperationException {
		Object nmsWorld = PackageType.CB.invokeMethod(world, CLASS_NAME[7], "getHandle", (Object[]) null);
		Class<?> nmsWorldClass = PackageType.NMS.getClass("World");
		PackageType.NMS.getMethod(CLASS_NAME[2], "a", nmsWorldClass).invoke(titleEntityCommand, nmsWorld);
	}

	protected void setLocation(Object titleEntityCommand, Location location) throws ReflectiveOperationException {
		int x = location.getBlockX();
		int y = location.getBlockY();
		int z = location.getBlockZ();
		if (Utils.isCBXXXorLater("1.8")) {
			String methodName = Utils.isCBXXXorLater("1.10") ? "setPosition" : "a";
			Object instance = PackageType.NMS.newInstance(CLASS_NAME[5], x, y, z);
			PackageType.NMS.invokeMethod(titleEntityCommand, CLASS_NAME[2], methodName, instance);
		} else {
			PackageType.NMS.setFieldValue(CLASS_NAME[2], "x", titleEntityCommand, x);
			PackageType.NMS.setFieldValue(CLASS_NAME[2], "y", titleEntityCommand, y);
			PackageType.NMS.setFieldValue(CLASS_NAME[2], "z", titleEntityCommand, z);
		}
	}

	protected Object getCommandBlock(Object titleEntityCommand) throws ReflectiveOperationException {
		String methodName = Utils.isCBXXXorLater("1.7.10") ? "getCommandBlock" : "a";
		return PackageType.NMS.invokeMethod(titleEntityCommand, CLASS_NAME[2], methodName, (Object[]) null);
	}

	protected static Class<?> getClass(PackageType packageType, String className) {
		try {
			return packageType.getClass(className);
		} catch (IllegalArgumentException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}