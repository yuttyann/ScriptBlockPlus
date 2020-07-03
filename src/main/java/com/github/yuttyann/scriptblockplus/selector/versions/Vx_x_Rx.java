package com.github.yuttyann.scriptblockplus.selector.versions;

import com.github.yuttyann.scriptblockplus.enums.reflection.PackageType;
import com.github.yuttyann.scriptblockplus.selector.CommandListener;
import com.github.yuttyann.scriptblockplus.selector.TileEntityCommand;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * ScriptBlockPlus Vx_x_Rx NMSクラス
 * @author yuttyann44581
 */
public class Vx_x_Rx {

	private static final Class<?>[] PARAMS;

	static {
		Class<?> iCommandListener = null;
		try {
			iCommandListener = PackageType.NMS.getClass("ICommandListener");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		PARAMS = new Class<?>[] { iCommandListener, CommandSender.class, String.class };
	}

	private final CommandListener listener = new CommandBlock(this);

	@NotNull
	public final CommandListener getCommandBlock() {
		return listener;
	}

	private static final class CommandBlock implements CommandListener {

		private final Vx_x_Rx vx_x_Rx;

		private CommandBlock(@NotNull Vx_x_Rx vx_x_Rx) {
			this.vx_x_Rx = vx_x_Rx;
		}

		@Override
		public final boolean executeCommand(@NotNull CommandSender sender, @NotNull Location location, @NotNull String command) {
			try {
				return vx_x_Rx.executeCommand(vx_x_Rx.getListener(sender, location), sender, location, command) > 0;
			} catch (ReflectiveOperationException e) {
				e.printStackTrace();
			}
			return false;
		}
	}

	protected int executeCommand(@NotNull Object listener, @NotNull CommandSender bSender, @NotNull Location location, @NotNull String command) throws ReflectiveOperationException {
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

	@NotNull
	protected Object getListener(@NotNull CommandSender sender, @NotNull Location location) throws ReflectiveOperationException {
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
			tileEntityCommand.setWorld(Objects.requireNonNull(location.getWorld()));
			tileEntityCommand.setLocation(location);
			Object commandListener = tileEntityCommand.getCommandBlock();
			tileEntityCommand.setName(sender.getName());
			return commandListener;
		}
	}

	@NotNull
	private Object setVec3D(@NotNull Object wrapper, @NotNull Object vec3D) throws ReflectiveOperationException {
		return PackageType.NMS.getMethod("CommandListenerWrapper", "a", PackageType.NMS.getClass("Vec3D")).invoke(wrapper, vec3D);
	}

	@NotNull
	private Object newVec3D(@NotNull Location location) throws ReflectiveOperationException {
		double x = location.getX();
		double y = location.getY();
		double z = location.getZ();
		return PackageType.NMS.newInstance("Vec3D", x, y, z);
	}
}