package com.github.yuttyann.scriptblockplus.selector.versions;

import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.ProxiedCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.craftbukkit.v1_13_R1.CraftServer;
import org.bukkit.craftbukkit.v1_13_R1.command.CraftBlockCommandSender;
import org.bukkit.craftbukkit.v1_13_R1.command.ProxiedNativeCommandSender;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftMinecartCommand;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;

import net.minecraft.server.v1_13_R1.CommandListenerWrapper;
import net.minecraft.server.v1_13_R1.DedicatedServer;
import net.minecraft.server.v1_13_R1.MinecraftServer;
import net.minecraft.server.v1_13_R1.Vec3D;

public final class v1_13_R1 extends Vx_x_Rx {

	@Override
	public int executeCommand(Object listener, CommandSender bSender, Location location, String command) {
		CommandListenerWrapper wrapper = (CommandListenerWrapper) listener;
		MinecraftServer server = ((CraftServer) bSender.getServer()).getServer();
		Vec3D vec3D = new Vec3D(location.getBlockX() + 0.5D, location.getBlockY() + 0.5D, location.getBlockZ() + 0.5D);
		return server.getCommandDispatcher().dispatchServerCommand(wrapper.a(vec3D), command);
	}

	@SuppressWarnings("deprecation")
	@Override
	public CommandListenerWrapper getListener(CommandSender sender, Location location) {
		if (sender instanceof Player) {
			return ((CraftPlayer) sender).getHandle().getCommandListener();
		} else if (sender instanceof BlockCommandSender) {
			return ((CraftBlockCommandSender) sender).getWrapper();
		} else if (sender instanceof CommandMinecart) {
			return ((CraftMinecartCommand) sender).getHandle().getCommandBlock().getWrapper();
		} else if (sender instanceof RemoteConsoleCommandSender) {
			return ((DedicatedServer) MinecraftServer.getServer()).remoteControlCommandListener.f();
		} else if (sender instanceof ConsoleCommandSender) {
			return ((CraftServer) sender.getServer()).getServer().getServerCommandListener();
		} else if (sender instanceof ProxiedCommandSender) {
			return ((ProxiedNativeCommandSender) sender).getHandle();
		} else {
			throw new IllegalArgumentException("Cannot make " + sender + " a vanilla command listener");
		}
	}
}