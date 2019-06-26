package com.github.yuttyann.scriptblockplus.selector.versions;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_14_R1.CraftServer;
import org.bukkit.craftbukkit.v1_14_R1.command.VanillaCommandWrapper;

import net.minecraft.server.v1_14_R1.CommandListenerWrapper;
import net.minecraft.server.v1_14_R1.MinecraftServer;
import net.minecraft.server.v1_14_R1.Vec3D;

public final class v1_14_R1 extends Vx_x_Rx {

	@Override
	public int executeCommand(Object listener, CommandSender bSender, Location location, String command) {
		CommandListenerWrapper wrapper = (CommandListenerWrapper) listener;
		MinecraftServer server = ((CraftServer) bSender.getServer()).getServer();
		Vec3D vec3D = new Vec3D(location.getBlockX() + 0.5D, location.getBlockY() + 0.5D, location.getBlockZ() + 0.5D);
		return server.getCommandDispatcher().dispatchServerCommand(wrapper.a(vec3D), command);
	}

	@Override
	public CommandListenerWrapper getListener(CommandSender sender, Location location) {
		return VanillaCommandWrapper.getListener(sender);
	}
}