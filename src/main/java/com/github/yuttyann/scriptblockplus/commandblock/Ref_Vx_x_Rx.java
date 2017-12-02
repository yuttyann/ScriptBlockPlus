package com.github.yuttyann.scriptblockplus.commandblock;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import com.github.yuttyann.scriptblockplus.enums.PackageType;
import com.github.yuttyann.scriptblockplus.utils.Utils;

final class Ref_Vx_x_Rx implements CommandBlockListener {

	private static final String CLASS_NAME_1 = "CommandBlockListenerAbstract";
	private static final String CLASS_NAME_2 = "TileEntityCommand";

	@Override
	public void executeCommand(CommandSender sender, Location location, String command) {
		try {
			Object commandListener = getCommandListener(sender, location);
			PackageType.NMS.invokeMethod(null, CLASS_NAME_1, "executeCommand", commandListener, sender, command);
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
	}

	private Object getCommandListener(CommandSender sender, Location location) throws ReflectiveOperationException {
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
		PackageType.NMS.invokeMethod(commandListener, CLASS_NAME_1, "setName", name);
	}

	private Object newTileEntityCommand() throws ReflectiveOperationException {
		return PackageType.NMS.newInstance(CLASS_NAME_2, (Object[]) null);
	}

	private void setWorld(Object titleEntityCommand, World world) throws ReflectiveOperationException {
		Object nmsWorld = PackageType.NMS.invokeMethod(world, "CraftWorld", "getHandle", (Object[]) null);
		PackageType.NMS.invokeMethod(titleEntityCommand, CLASS_NAME_2, "a", nmsWorld);
	}

	private void setLocation(Object titleEntityCommand, Location location) throws ReflectiveOperationException {
		int x = location.getBlockX();
		int y = location.getBlockY();
		int z = location.getBlockZ();
		String methodName = Utils.isCB110orLater() ? "setPosition" : "a";
		Object instance = PackageType.NMS.newInstance("BlockPosition", x, y, z);
		PackageType.NMS.invokeMethod(titleEntityCommand, CLASS_NAME_2, methodName, instance);
	}

	private Object getCommandBlock(Object titleEntityCommand) throws ReflectiveOperationException {
		return PackageType.NMS.invokeMethod(titleEntityCommand, CLASS_NAME_2, "getCommandBlock", (Object[]) null);
	}
}