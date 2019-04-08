package com.github.yuttyann.scriptblockplus.commandblock.versions;

import java.lang.reflect.Method;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import com.github.yuttyann.scriptblockplus.commandblock.ClassNameList;
import com.github.yuttyann.scriptblockplus.commandblock.CommandListener;
import com.github.yuttyann.scriptblockplus.commandblock.TileEntityCommand;
import com.github.yuttyann.scriptblockplus.enums.reflection.PackageType;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class Vx_x_Rx implements ClassNameList {

	private static final Class<?>[] PARAMS = { getNMSClass(c), CommandSender.class, String.class };

	private final CommandListener listener = new CommandBlock(this);

	public final CommandListener getCommandBlock() {
		return listener;
	}

	private final class CommandBlock implements CommandListener {

		private final Vx_x_Rx vx_x_Rx;

		private CommandBlock(Vx_x_Rx vx_x_Rx) {
			this.vx_x_Rx = vx_x_Rx;
		}

		@Override
		public final boolean executeCommand(CommandSender sender, Location location, String command) {
			try {
				Object iCommandListener = vx_x_Rx.getICommandListener(sender, location);
				return vx_x_Rx.executeCommand(iCommandListener, sender, command) > 0;
			} catch (ReflectiveOperationException e) {
				e.printStackTrace();
			}
			return false;
		}
	}

	protected int executeCommand(Object iSender, CommandSender bSender, String command) throws ReflectiveOperationException {
		if (Utils.isCBXXXorLater("1.13")) {
			Object server = PackageType.CB.invokeMethod(bSender.getServer(), i, "getServer");
			Object wrapper = PackageType.NMS.invokeMethod(iSender, a, "getWrapper");
			Object dispatcher = PackageType.NMS.invokeMethod(server, h, "getCommandDispatcher");
			return (int) PackageType.NMS.invokeMethod(dispatcher, b, "dispatchServerCommand", wrapper, command);
		} else {
			Method executeCommand = PackageType.NMS.getMethod(a, "executeCommand", PARAMS);
			return (int) executeCommand.invoke(null, iSender, bSender, command);
		}
	}

	protected Object getICommandListener(CommandSender sender, Location location) throws ReflectiveOperationException {
		TileEntityCommand tileEntityCommand = new TileEntityCommand();
		tileEntityCommand.setWorld(location.getWorld());
		tileEntityCommand.setLocation(location);
		Object commandListener = tileEntityCommand.getCommandBlock();
		if (sender != null) {
			tileEntityCommand.setName(sender.getName());
		}
		return commandListener;
	}

	private static Class<?> getNMSClass(String className) {
		try {
			return PackageType.NMS.getClass(className);
		} catch (IllegalArgumentException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}