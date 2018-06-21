package com.github.yuttyann.scriptblockplus.commandblock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;

import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.google.common.base.Joiner;

import net.minecraft.server.v1_8_R1.BlockPosition;
import net.minecraft.server.v1_8_R1.EntityPlayer;
import net.minecraft.server.v1_8_R1.ICommandListener;
import net.minecraft.server.v1_8_R1.MinecraftServer;
import net.minecraft.server.v1_8_R1.PlayerSelector;
import net.minecraft.server.v1_8_R1.WorldServer;

final class v1_8_R1 extends Ref_Vx_x_Rx {

	@Override
	public int executeCommand(Object sender, CommandSender bSender, String command) {
		if (command.charAt(0) == '/') {
			command = command.substring(1);
		}
		ICommandListener iSender = (ICommandListener) sender;
		SimpleCommandMap commandMap = iSender.getWorld().getServer().getCommandMap();
		Joiner joiner = Joiner.on(" ");
		String[] args = StringUtils.split(command, " ");
		List<String[]> commands = new ArrayList<>();
		String cmd = args[0];
		if (cmd.startsWith("minecraft:")) {
			cmd = cmd.substring("minecraft:".length());
		}
		if (cmd.startsWith("bukkit:")) {
			cmd = cmd.substring("bukkit:".length());
		}
		if (cmd.equalsIgnoreCase("stop") || cmd.equalsIgnoreCase("kick") || cmd.equalsIgnoreCase("op")
				|| cmd.equalsIgnoreCase("deop") || cmd.equalsIgnoreCase("ban") || cmd.equalsIgnoreCase("ban-ip")
				|| cmd.equalsIgnoreCase("pardon") || cmd.equalsIgnoreCase("pardon-ip") || cmd.equalsIgnoreCase("reload")
				|| iSender.getWorld().players.isEmpty() || commandMap.getCommand(args[0]) == null) {
			return 0;
		}
		commands.add(args);
		MinecraftServer server = MinecraftServer.getServer();
		WorldServer[] prev = server.worldServer;
		server.worldServer = new WorldServer[server.worlds.size()];
		server.worldServer[0] = (WorldServer) iSender.getWorld();
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
			List<String[]> newCommands = new ArrayList<>();
			for (int i = 0; i < args.length; i++) {
				if (PlayerSelector.isPattern(args[i])) {
					for (int j = 0; j < commands.size(); j++) {
						newCommands.addAll(buildCommands(iSender, commands.get(j), i));
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
				BlockPosition blockPosition = iSender.getChunkCoordinates();
				server.server.getLogger().log(Level.WARNING,
					String.format(message, blockPosition.getX(), blockPosition.getY(), blockPosition.getZ()), exception
				);
			}
		}
		return completed;
	}

	private List<String[]> buildCommands(ICommandListener sender, String[] args, int pos) {
		List<String[]> commands = new ArrayList<>();
		List<EntityPlayer> playerList = PlayerSelector.getPlayers(sender, args[pos], EntityPlayer.class);
		EntityPlayer[] players = playerList.toArray(new EntityPlayer[playerList.size()]);
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