package com.github.yuttyann.scriptblockplus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.yuttyann.scriptblockplus.collplugin.CollPlugins;
import com.github.yuttyann.scriptblockplus.command.ScriptBlockCommand;
import com.github.yuttyann.scriptblockplus.command.help.CommandData;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.Messages;
import com.github.yuttyann.scriptblockplus.listener.BlockListener;
import com.github.yuttyann.scriptblockplus.listener.InteractListener;
import com.github.yuttyann.scriptblockplus.listener.PlayerJoinQuitListener;
import com.github.yuttyann.scriptblockplus.listener.PlayerMoveListener;
import com.github.yuttyann.scriptblockplus.manager.MapManager;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class ScriptBlock extends JavaPlugin {

	public static ScriptBlock instance;
	private MapManager mapManager;
	private Map<String, TabExecutor> commands;
	private Map<String, List<CommandData>> commandHelp;

	@Override
	public void onEnable() {
		instance = this;
		Files.reload();
		if (!CollPlugins.hasVault()) {
			Utils.sendPluginMessage(Messages.notVaultMessage);
			Utils.disablePlugin(this);
			return;
		}
		if (Utils.isPluginEnabled("ScriptBlock")) {
			Utils.disablePlugin(Utils.getPlugin("ScriptBlock"));
		}
		loadClass();
		loadCommand();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return commands.get(command.getName()).onCommand(sender, command, label, args);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return commands.get(command.getName()).onTabComplete(sender, command, label, args);
	}

	public Map<String, List<CommandData>> getCommandHelp() {
		return commandHelp;
	}

	public MapManager getMapManager() {
		return mapManager;
	}

	public ScriptBlockAPI getAPI(Block block, ScriptType scriptType) {
		return new ScriptBlockAPI(block, scriptType);
	}

	private void loadClass() {
		mapManager = new MapManager();
		getServer().getPluginManager().registerEvents(new InteractListener(), this);
		getServer().getPluginManager().registerEvents(new BlockListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerMoveListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerJoinQuitListener(), this);
		getServer().getPluginManager().registerEvents(new Updater(this), this);
	}

	private void loadCommand() {
		commandHelp = new HashMap<String, List<CommandData>>();
		commands = new HashMap<String, TabExecutor>();
		commands.put("scriptblockplus", new ScriptBlockCommand());
	}
}