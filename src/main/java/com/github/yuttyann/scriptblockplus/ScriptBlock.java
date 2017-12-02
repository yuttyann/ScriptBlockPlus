package com.github.yuttyann.scriptblockplus;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.yuttyann.scriptblockplus.command.ScriptBlockPlusCommand;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.listener.InteractListener;
import com.github.yuttyann.scriptblockplus.listener.JoinQuitListener;
import com.github.yuttyann.scriptblockplus.listener.ScriptBreakListener;
import com.github.yuttyann.scriptblockplus.listener.ScriptInteractListener;
import com.github.yuttyann.scriptblockplus.listener.ScriptWalkListener;
import com.github.yuttyann.scriptblockplus.manager.MapManager;
import com.github.yuttyann.scriptblockplus.manager.ScriptBlockManager;
import com.github.yuttyann.scriptblockplus.player.BaseSBPlayer;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.hook.HookPlugins;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class ScriptBlock extends JavaPlugin {

	private static ScriptBlock instance;
	private static ScriptBlockAPI scriptBlockAPI;

	private Updater updater;
	private MapManager mapManager;
	private ScriptBlockPlusCommand scriptBlockPlusCommand;

	@Override
	public void onEnable() {
		for (Player player : Utils.getOnlinePlayers()) {
			((BaseSBPlayer) SBPlayer.get(player)).setPlayer(player);
		}
		Files.reload();

		if (!HookPlugins.hasVault()) {
			Utils.sendMessage(SBConfig.getNotVaultMessage());
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		Plugin plugin = getServer().getPluginManager().getPlugin("ScriptBlock");
		if (plugin != null) {
			getServer().getPluginManager().disablePlugin(plugin);
		}

		updater = new Updater(this);
		try {
			// updater.debug(true, true);
			updater.init();
			updater.load();
			updater.execute(null);
		} catch (Exception e) {
			Utils.sendMessage(SBConfig.getUpdateErrorMessage());
		}

		mapManager = new MapManager(this);
		mapManager.loadAllScripts();
		mapManager.loadCooldown();
		mapManager.loadOldCooldown();

		scriptBlockPlusCommand = new ScriptBlockPlusCommand(this);

		getServer().getPluginManager().registerEvents(new JoinQuitListener(this), this);
		getServer().getPluginManager().registerEvents(new InteractListener(this), this);
		getServer().getPluginManager().registerEvents(new ScriptInteractListener(this), this);
		getServer().getPluginManager().registerEvents(new ScriptBreakListener(this), this);
		getServer().getPluginManager().registerEvents(new ScriptWalkListener(this), this);
	}

	@Override
	public void onDisable() {
		mapManager.saveCooldown();
		mapManager.saveOldCooldown();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equals(scriptBlockPlusCommand.getCommandName())) {
			return scriptBlockPlusCommand.onCommand(sender, command, label, args);
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equals(scriptBlockPlusCommand.getCommandName())) {
			return scriptBlockPlusCommand.onTabComplete(sender, command, label, args);
		}
		return super.onTabComplete(sender, command, label, args);
	}

	public ScriptBlockAPI getAPI() {
		if (scriptBlockAPI == null) {
			scriptBlockAPI = getAPI(null, null);
		}
		return scriptBlockAPI = ((ScriptBlockManager) scriptBlockAPI).empty();
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

	public static ScriptBlock getInstance() {
		if (instance == null) {
			instance = (ScriptBlock) Bukkit.getPluginManager().getPlugin("ScriptBlockPlus");
		}
		return instance;
	}
}