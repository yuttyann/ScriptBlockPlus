package com.github.yuttyann.scriptblockplus.commandblock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import net.minecraft.server.v1_7_R1.ChunkCoordinates;
import net.minecraft.server.v1_7_R1.CommandBlockListenerAbstract;
import net.minecraft.server.v1_7_R1.EntityPlayer;
import net.minecraft.server.v1_7_R1.ICommandListener;
import net.minecraft.server.v1_7_R1.MinecraftServer;
import net.minecraft.server.v1_7_R1.NBTTagCompound;
import net.minecraft.server.v1_7_R1.Packet;
import net.minecraft.server.v1_7_R1.PacketPlayOutTileEntityData;
import net.minecraft.server.v1_7_R1.PlayerSelector;
import net.minecraft.server.v1_7_R1.TileEntity;
import net.minecraft.server.v1_7_R1.World;
import net.minecraft.server.v1_7_R1.WorldServer;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.craftbukkit.v1_7_R1.CraftWorld;

import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.google.common.base.Joiner;

final class v1_7_R1 implements CommandListener {

	private class TileEntityCommand extends TileEntity {

		private final CommandBlockListenerAbstract i = new CommandBlockListenerAbstract() {

			@Override
			public ChunkCoordinates getChunkCoordinates() {
				return new ChunkCoordinates(x, y, z);
			}

			@Override
			public World getWorld() {
				return TileEntityCommand.this.getWorld();
			}

			@Override
			public void a(String s) {
				super.a(s);
				TileEntityCommand.this.update();
			}

			@Override
			public void e() {
				getWorld().notify(x, y, z);
			}
		};

		@Override
		public void b(NBTTagCompound paramNBTTagCompound) {
			super.b(paramNBTTagCompound);
			i.a(paramNBTTagCompound);
		}

		@Override
		public void a(NBTTagCompound paramNBTTagCompound) {
			super.a(paramNBTTagCompound);
			i.b(paramNBTTagCompound);
		}

		@Override
		public Packet getUpdatePacket() {
			NBTTagCompound localNBTTagCompound = new NBTTagCompound();
			b(localNBTTagCompound);
			return new PacketPlayOutTileEntityData(this.x, this.y, this.z, 2, localNBTTagCompound);
		}

		public CommandBlockListenerAbstract getCommandBlock() {
			return i;
		}
	}

	@Override
	public void executeCommand(CommandSender sender, Location location, String command) {
		executeCommand(getICommandListener(sender, location), sender, command);
	}

	private ICommandListener getICommandListener(CommandSender sender, Location location) {
		TileEntityCommand titleEntityCommand = new TileEntityCommand();
		titleEntityCommand.a(((CraftWorld) location.getWorld()).getHandle());
		titleEntityCommand.x = location.getBlockX();
		titleEntityCommand.y = location.getBlockY();
		titleEntityCommand.z = location.getBlockZ();
		CommandBlockListenerAbstract commandListener = titleEntityCommand.getCommandBlock();
		if (sender != null) {
			commandListener.b(sender.getName());
		}
		return commandListener;
	}

	private int executeCommand(ICommandListener sender, CommandSender bSender, String command) {
		SimpleCommandMap commandMap = sender.getWorld().getServer().getCommandMap();
		Joiner joiner = Joiner.on(" ");
		if (command.charAt(0) == '/') {
			command = command.substring(1);
		}
		String[] args = StringUtils.split(command, " ");
		List<String[]> commands = new ArrayList<String[]>();
		String cmd = args[0];
		if (cmd.startsWith("minecraft:")) {
			cmd = cmd.substring("minecraft:".length());
		}
		if (cmd.startsWith("bukkit:")) {
			cmd = cmd.substring("bukkit:".length());
		}
		if (cmd.equalsIgnoreCase("stop") || cmd.equalsIgnoreCase("kick") || cmd.equalsIgnoreCase("op")
				|| cmd.equalsIgnoreCase("deop") || cmd.equalsIgnoreCase("ban") || cmd.equalsIgnoreCase("ban-ip")
				|| cmd.equalsIgnoreCase("pardon") || cmd.equalsIgnoreCase("pardon-ip") || cmd.equalsIgnoreCase("reload")) {
			return 0;
		}
		if (sender.getWorld().players.isEmpty()) {
			return 0;
		}
		if (commandMap.getCommand(args[0]) == null) {
			return 0;
		}
		commands.add(args);
		MinecraftServer server = MinecraftServer.getServer();
		WorldServer[] prev = server.worldServer;
		server.worldServer = new WorldServer[server.worlds.size()];
		server.worldServer[0] = (WorldServer) sender.getWorld();
		int bpos = 0;
		for (int pos = 1; pos < server.worldServer.length; pos++) {
			WorldServer world = server.worlds.get(bpos++);
			if (server.worldServer[0] == world) {
				pos--;
				continue;
			}
			server.worldServer[pos] = world;
		}
		try {
			List<String[]> newCommands = new ArrayList<String[]>();
			for (int i = 0; i < args.length; i++) {
				if (PlayerSelector.isPattern(args[i])) {
					for (int j = 0; j < commands.size(); j++) {
						newCommands.addAll(buildCommands(sender, commands.get(j), i));
					}
					List<String[]> temp = commands;
					commands = newCommands;
					newCommands = temp;
					newCommands.clear();
				}
			}
		} finally {
			server.worldServer = prev;
		}
		int completed = 0;
		for (int i = 0; i < commands.size(); i++) {
			try {
				if (commandMap.dispatch(bSender, joiner.join(Arrays.asList(commands.get(i))))) {
					completed++;
				}
			} catch (Throwable exception) {
				String message = "CommandBlock at (%d,%d,%d) failed to handle command";
				ChunkCoordinates chunkCoordinates = ((CommandBlockListenerAbstract) sender).getChunkCoordinates();
				server.server.getLogger().log(Level.WARNING,
					String.format(message, chunkCoordinates.x, chunkCoordinates.y, chunkCoordinates.z), exception
				);
			}
		}
		return completed;
	}

	private List<String[]> buildCommands(ICommandListener sender, String[] args, int pos) {
		List<String[]> commands = new ArrayList<String[]>();
		EntityPlayer[] players = PlayerSelector.getPlayers(sender, args[pos]);
		if (players != null) {
			for (EntityPlayer player : players) {
				if (player.world != sender.getWorld()) {
					continue;
				}
				String[] command = args.clone();
				command[pos] = player.getName();
				commands.add(command);
			}
		}
		return commands;
	}
}