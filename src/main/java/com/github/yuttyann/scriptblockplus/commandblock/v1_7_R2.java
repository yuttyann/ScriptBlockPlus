package com.github.yuttyann.scriptblockplus.commandblock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.craftbukkit.v1_7_R2.CraftWorld;

import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.google.common.base.Joiner;

import net.minecraft.server.v1_7_R2.ChunkCoordinates;
import net.minecraft.server.v1_7_R2.CommandBlockListenerAbstract;
import net.minecraft.server.v1_7_R2.EntityPlayer;
import net.minecraft.server.v1_7_R2.ICommandListener;
import net.minecraft.server.v1_7_R2.MinecraftServer;
import net.minecraft.server.v1_7_R2.PlayerSelector;
import net.minecraft.server.v1_7_R2.TileEntityCommand;
import net.minecraft.server.v1_7_R2.WorldServer;

final class v1_7_R2 implements CommandListener {

	@Override
	public boolean executeCommand(CommandSender sender, Location location, String command) {
		return executeCommand(getICommandListener(sender, location), sender, command) > 0;
	}

	private ICommandListener getICommandListener(CommandSender sender, Location location) {
		TileEntityCommand titleEntityCommand = new TileEntityCommand();
		titleEntityCommand.a(((CraftWorld) location.getWorld()).getHandle());
		titleEntityCommand.x = location.getBlockX();
		titleEntityCommand.y = location.getBlockY();
		titleEntityCommand.z = location.getBlockZ();
		CommandBlockListenerAbstract commandListener = titleEntityCommand.a();
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
				ChunkCoordinates chunkCoordinates = sender.getChunkCoordinates();
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