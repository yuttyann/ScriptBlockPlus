package com.github.yuttyann.scriptblockplus.commandblock.versions;

import java.lang.reflect.Method;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import com.github.yuttyann.scriptblockplus.commandblock.ClassListener;
import com.github.yuttyann.scriptblockplus.commandblock.CommandListener;
import com.github.yuttyann.scriptblockplus.commandblock.TileEntityCommand;
import com.github.yuttyann.scriptblockplus.enums.reflection.PackageType;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class Vx_x_Rx implements ClassListener {

	private CommandListener listener;

	public final CommandListener getCommandBlock() {
		return listener == null ? listener = new CommandBlock(this) : listener;
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

	protected int executeCommand(Object sender, CommandSender bSender, String command) throws ReflectiveOperationException {
		int completed = 0;
		if (Utils.isCBXXXorLater("1.13")) {
			Object wrapper = PackageType.NMS.invokeMethod(sender, CLASS_NAME[1], "getWrapper");
			Object server = PackageType.CB.invokeMethod(bSender.getServer(), CLASS_NAME[6], "getServer");
			Object dispatcher = PackageType.NMS.invokeMethod(server, CLASS_NAME[6], "getCommandDispatcher");
			Object dispatch = PackageType.NMS.invokeMethod(dispatcher, CLASS_NAME[3], "dispatchServerCommand", wrapper, command);
			completed = Integer.class.cast(dispatch).intValue();
		} else {
			Method executeCommand = PackageType.NMS.getMethod(CLASS_NAME[1], "executeCommand", PARAMS_EXECUTE_COMMAND);
			completed = Integer.class.cast(executeCommand.invoke(null, sender, bSender, command)).intValue();
		}
		return completed;
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
}