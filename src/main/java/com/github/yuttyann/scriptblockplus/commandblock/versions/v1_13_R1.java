package com.github.yuttyann.scriptblockplus.commandblock.versions;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_13_R1.CraftServer;
import org.bukkit.craftbukkit.v1_13_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_13_R1.ChatComponentText;
import net.minecraft.server.v1_13_R1.CommandBlockListenerAbstract;
import net.minecraft.server.v1_13_R1.CommandListenerWrapper;
import net.minecraft.server.v1_13_R1.Entity;
import net.minecraft.server.v1_13_R1.MinecraftServer;
import net.minecraft.server.v1_13_R1.TileEntityCommand;
import net.minecraft.server.v1_13_R1.Vec3D;

public final class v1_13_R1 extends Vx_x_Rx {

	@Override
	public int executeCommand(NMSSender nSender, CommandSender bSender, Location location, String command) {
		MinecraftServer server = ((CraftServer) bSender.getServer()).getServer();
		CommandListenerWrapper wrapper = (CommandListenerWrapper) nSender.wrapper;
		Vec3D vec3D = new Vec3D(location.getBlockX() + 0.5D, location.getBlockY() + 0.5D, location.getBlockZ() + 0.5D);
		return server.getCommandDispatcher().dispatchServerCommand(wrapper.a(vec3D), command);
	}

	@Override
	public NMSSender getNMSSender(CommandSender sender, Location location) {
		if (sender instanceof Player) {
			Entity entity = ((CraftPlayer) sender).getHandle();
			return new NMSSender(entity, entity.getCommandListener());
		} else {
			TileEntityCommand tileEntityCommand = new TileEntityCommand();
			tileEntityCommand.setWorld(((CraftWorld) location.getWorld()).getHandle());
			CommandBlockListenerAbstract commandListener = tileEntityCommand.getCommandBlock();
			if (sender != null) {
				commandListener.setName(new ChatComponentText(sender.getName()));
			}
			return new NMSSender(commandListener, commandListener.getWrapper());
		}
	}
}