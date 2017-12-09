package com.github.yuttyann.scriptblockplus.commandblock;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

public interface CommandBlockListener {

	public void executeCommand(CommandSender sender, Location location, String command);
}