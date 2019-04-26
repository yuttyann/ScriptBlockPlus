package com.github.yuttyann.scriptblockplus.commandblock.versions;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

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
				return vx_x_Rx.executeCommand(iCommandListener, sender, location, command) > 0;
			} catch (ReflectiveOperationException e) {
				e.printStackTrace();
			}
			return false;
		}
	}

	protected int executeCommand(Object iSender, CommandSender bSender, Location location, String command) throws ReflectiveOperationException {
		if (Utils.isCBXXXorLater("1.13")) {
			Object server = PackageType.CB.invokeMethod(bSender.getServer(), i, "getServer");
			Object wrapper = setVec3D(PackageType.NMS.invokeMethod(iSender, a, "getWrapper"), newVec3D(location));
			Object dispatcher = PackageType.NMS.invokeMethod(server, h, "getCommandDispatcher");
			return (int) PackageType.NMS.invokeMethod(dispatcher, b, "dispatchServerCommand", wrapper, command);
		} else {
			Method executeCommand = PackageType.NMS.getMethod(a, "executeCommand", PARAMS);
			return (int) executeCommand.invoke(null, iSender, bSender, command);
		}
	}

	private Map<Location, Object> cache_Vec3D = new HashMap<>(64);

	private Object setVec3D(Object wrapper, Object vec3D) throws ReflectiveOperationException {
		return PackageType.NMS.getMethod(l, "a", PackageType.NMS.getClass(k)).invoke(wrapper, vec3D);
	}

	private Object newVec3D(Location location) throws ReflectiveOperationException {
		if (cache_Vec3D.size() > 500) {
			cache_Vec3D = new HashMap<>(64);
		}
		Object vec3D = cache_Vec3D.get(location);
		if (vec3D == null) {
			double x = location.getBlockX() + 0.5D;
			double y = location.getBlockY() + 0.5D;
			double z = location.getBlockZ() + 0.5D;
			cache_Vec3D.put(location, vec3D = PackageType.NMS.newInstance(k, x, y, z));
		}
		return vec3D;
	}


	protected Object getICommandListener(CommandSender sender, Location location) throws ReflectiveOperationException {
		TileEntityCommand tileEntityCommand = new TileEntityCommand();
		tileEntityCommand.setWorld(location.getWorld());
		if (!Utils.isCBXXXorLater("1.13")) {
			tileEntityCommand.setLocation(location);
		}
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