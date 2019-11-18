package com.github.yuttyann.scriptblockplus.selector.versions;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.ProxiedCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;

import com.github.yuttyann.scriptblockplus.enums.reflection.PackageType;
import com.github.yuttyann.scriptblockplus.selector.CommandListener;
import com.github.yuttyann.scriptblockplus.selector.TileEntityCommand;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class Vx_x_Rx {

	private static final Class<?>[] PARAMS = { getNMSClass("ICommandListener"), CommandSender.class, String.class };

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
				return vx_x_Rx.executeCommand(vx_x_Rx.getListener(sender, location), sender, location, command) > 0;
			} catch (ReflectiveOperationException e) {
				e.printStackTrace();
			}
			return false;
		}
	}

	protected int executeCommand(Object listener, CommandSender bSender, Location location, String command) throws ReflectiveOperationException {
		if (Utils.isCBXXXorLater("1.13")) {
			Object wrapper = setVec3D(listener, newVec3D(location));
			Object server = PackageType.CB.invokeMethod(bSender.getServer(), "CraftServer", "getServer");
			Object dispatcher = PackageType.NMS.invokeMethod(server, "MinecraftServer", "getCommandDispatcher");
			return (int) PackageType.NMS.invokeMethod(dispatcher, "CommandDispatcher", "dispatchServerCommand", wrapper, command);
		} else {
			Method executeCommand = PackageType.NMS.getMethod("CommandBlockListenerAbstract", "executeCommand", PARAMS);
			return (int) executeCommand.invoke(null, listener, bSender, command);
		}
	}

	protected Object getListener(CommandSender sender, Location location) throws ReflectiveOperationException {
		if (Utils.isCBXXXorLater("1.14")) {
			Method m = PackageType.CB_COMMAND.getMethod("VanillaCommandWrapper", "getListener", CommandSender.class);
			return m.invoke(null, sender);
		} else if (Utils.isCBXXXorLater("1.13")) {
			if (sender instanceof Player) {
				Object entity = PackageType.CB_ENTITY.invokeMethod(sender, "CraftPlayer", "getHandle");
				return PackageType.NMS.invokeMethod(entity, "Entity", "getCommandListener");
			} else if (sender instanceof BlockCommandSender) {
				return PackageType.CB_COMMAND.invokeMethod(sender, "CraftBlockCommandSender", "getWrapper");
			} else if (sender instanceof CommandMinecart) {
				Object cart = PackageType.CB_ENTITY.invokeMethod(sender, "CraftMinecartCommand", "getHandle");
				Object command = PackageType.NMS.invokeMethod(cart, "EntityMinecartCommandBlock", "getCommandBlock");
				return PackageType.NMS.invokeMethod(command, "CommandBlockListenerAbstract", "getWrapper");
			} else if (sender instanceof RemoteConsoleCommandSender) {
				Object server = PackageType.NMS.invokeMethod(null, "MinecraftServer", "getServer");
				Object remote = PackageType.NMS.getField("DedicatedServer", "remoteControlCommandListener").get(server);
				return PackageType.NMS.invokeMethod(remote, "RemoteControlCommandListener", "f");
			} else if (sender instanceof ConsoleCommandSender) {
				Object server = PackageType.CB.invokeMethod(sender.getServer(), "CraftServer", "getServer");
				return PackageType.NMS.invokeMethod(server, "MinecraftServer", "getServerCommandListener");
			} else if (sender instanceof ProxiedCommandSender) {
				return PackageType.CB_COMMAND.invokeMethod(sender, "ProxiedNativeCommandSender", "getHandle");
			}
			throw new IllegalArgumentException("Cannot make " + sender + " a vanilla command listener");
		} else {
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

	private Map<Location, Object> cache_Vec3D = new HashMap<>(64);

	private Object setVec3D(Object wrapper, Object vec3D) throws ReflectiveOperationException {
		return PackageType.NMS.getMethod("CommandListenerWrapper", "a", PackageType.NMS.getClass("Vec3D")).invoke(wrapper, vec3D);
	}

	private Object newVec3D(Location location) throws ReflectiveOperationException {
		if (cache_Vec3D.size() > 500) {
			cache_Vec3D = new HashMap<>(64);
		}
		Object vec3D = cache_Vec3D.get(location);
		if (vec3D == null) {
			double x = location.getX(), y = location.getY(), z = location.getZ();
			cache_Vec3D.put(location, vec3D = PackageType.NMS.newInstance("Vec3D", x, y, z));
		}
		return vec3D;
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