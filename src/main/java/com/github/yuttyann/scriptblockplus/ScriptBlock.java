package com.github.yuttyann.scriptblockplus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.yuttyann.scriptblockplus.command.ScriptBlockPlusCommand;
import com.github.yuttyann.scriptblockplus.command.help.CommandData;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.Messages;
import com.github.yuttyann.scriptblockplus.hook.HookPlugins;
import com.github.yuttyann.scriptblockplus.listener.BlockListener;
import com.github.yuttyann.scriptblockplus.listener.InteractListener;
import com.github.yuttyann.scriptblockplus.listener.PlayerJoinQuitListener;
import com.github.yuttyann.scriptblockplus.listener.PlayerMoveListener;
import com.github.yuttyann.scriptblockplus.manager.MapManager;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class ScriptBlock extends JavaPlugin {

	private static ScriptBlock instance;

	private MapManager mapManager;
	private ScriptBlockPlusCommand scriptBlockPlusCommand;
	private Map<String, List<CommandData>> commandHelp;

	@Override
	public void onEnable() {
		instance = this;
		Files.reload(this);
		if (!HookPlugins.hasVault()) {
			Utils.sendPluginMessage(Messages.notVaultMessage);
			Utils.disablePlugin(this);
			return;
		}
		if (Utils.isPluginEnabled("ScriptBlock")) {
			Utils.disablePlugin(Utils.getPlugin("ScriptBlock"));
		}
		loadMap();
		loadClass();
		loadCommand();
	}

	@Override
	public void onDisable() {
		mapManager.saveCooldown();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equals("scriptblockplus")) {
			return scriptBlockPlusCommand.onCommand(sender, command, label, args);
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> completeList = null;
		if (command.getName().equals("scriptblockplus")) {
			completeList = scriptBlockPlusCommand.onTabComplete(sender, command, label, args);
		}
		if (completeList != null) {
			return completeList;
		}
		return super.onTabComplete(sender, command, label, args);
	}

	public Map<String, List<CommandData>> getCommandHelp() {
		return commandHelp;
	}

	public static ScriptBlock getInstance() {
		return instance;
	}

	public MapManager getMapManager() {
		return mapManager;
	}

	public ScriptBlockAPI getAPI(Location location, ScriptType scriptType) {
		return new ScriptBlockAPI(this, location, scriptType);
	}

	private void loadMap() {
		mapManager = new MapManager(this);
		mapManager.loadAllScripts();
		mapManager.loadCooldown();
	}

	private void loadClass() {
		PluginManager pluginManager = getServer().getPluginManager();
		pluginManager.registerEvents(new InteractListener(this), this);
		pluginManager.registerEvents(new BlockListener(this), this);
		pluginManager.registerEvents(new PlayerMoveListener(this), this);
		pluginManager.registerEvents(new PlayerJoinQuitListener(this), this);
		try {
			pluginManager.registerEvents(new Updater(this), this);
		} catch (Exception e) {}
	}

	private void loadCommand() {
		commandHelp = new HashMap<String, List<CommandData>>();
		scriptBlockPlusCommand = new ScriptBlockPlusCommand(this);
	}
}