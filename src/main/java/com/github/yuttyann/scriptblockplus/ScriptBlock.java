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
import com.github.yuttyann.scriptblockplus.file.Lang;
import com.github.yuttyann.scriptblockplus.listener.InteractListener;
import com.github.yuttyann.scriptblockplus.listener.JoinQuitListener;
import com.github.yuttyann.scriptblockplus.listener.ScriptBreakListener;
import com.github.yuttyann.scriptblockplus.listener.ScriptInteractListener;
import com.github.yuttyann.scriptblockplus.listener.ScriptWalkListener;
import com.github.yuttyann.scriptblockplus.manager.MapManager;
import com.github.yuttyann.scriptblockplus.manager.ScriptBlockManager;
import com.github.yuttyann.scriptblockplus.script.hook.HookPlugins;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class ScriptBlock extends JavaPlugin {

	private static ScriptBlock instance;

	private Updater updater;
	private MapManager mapManager;
	private ScriptBlockPlusCommand scriptBlockPlusCommand;
	private Map<String, List<CommandData>> commandHelp;

	@Override
	public void onEnable() {
		Files.reload();
		if (!HookPlugins.hasVault()) {
			Utils.sendPluginMessage(Lang.getNotVaultMessage());
			Utils.disablePlugin(this);
			return;
		}
		if (Utils.isPluginEnabled("ScriptBlock")) {
			Utils.disablePlugin(Utils.getPlugin("ScriptBlock"));
		}

		updater = new Updater(this);
		try {
			updater.load();
			updater.check(null);
		} catch (Exception e) {
			Utils.sendPluginMessage(Lang.getUpdateErrorMessage());
		}

		mapManager = new MapManager(this);
		mapManager.loadAllScripts();
		mapManager.loadCooldown();

		commandHelp = new HashMap<String, List<CommandData>>();
		scriptBlockPlusCommand = new ScriptBlockPlusCommand(this);

		PluginManager pluginManager = getServer().getPluginManager();
		pluginManager.registerEvents(new InteractListener(this), this);
		pluginManager.registerEvents(new JoinQuitListener(this), this);
		pluginManager.registerEvents(new ScriptInteractListener(this), this);
		pluginManager.registerEvents(new ScriptBreakListener(this), this);
		pluginManager.registerEvents(new ScriptWalkListener(this), this);
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
		if (command.getName().equals("scriptblockplus")) {
			return scriptBlockPlusCommand.onTabComplete(sender, command, label, args);
		}
		return super.onTabComplete(sender, command, label, args);
	}

	public static ScriptBlock getInstance() {
		if (instance == null) {
			instance = (ScriptBlock) Utils.getPlugin("ScriptBlockPlus");
		}
		return instance;
	}

	public Map<String, List<CommandData>> getCommandHelp() {
		return commandHelp;
	}

	public ScriptBlockAPI getAPI(Location location, ScriptType scriptType) {
		return new ScriptBlockManager(this, location, scriptType);
	}

	public Updater getUpdater() {
		return updater;
	}

	public MapManager getMapManager() {
		return mapManager;
	}
}