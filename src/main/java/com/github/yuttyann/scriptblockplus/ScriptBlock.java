package com.github.yuttyann.scriptblockplus;

import com.github.yuttyann.scriptblockplus.command.ScriptBlockPlusCommand;
import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.listener.*;
import com.github.yuttyann.scriptblockplus.manager.APIManager;
import com.github.yuttyann.scriptblockplus.manager.MapManager;
import com.github.yuttyann.scriptblockplus.player.BaseSBPlayer;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.hook.HookPlugins;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ScriptBlock extends JavaPlugin {

	private Updater updater;
	private MapManager mapManager;
	private ScriptBlockAPI scriptAPI;
	private ScriptBlockPlusCommand scriptBlockPlusCommand;
	{
		new PluginInstance(ScriptBlock.class, this).put();
	}

	@Override
	public void onEnable() {
		if (!Utils.isCBXXXorLater("1.9")) {
			Bukkit.getConsoleSender().sendMessage("§cUnsupported Version: v" + Utils.getServerVersion());
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		Files.reload();
		Bukkit.getOnlinePlayers().forEach(p -> fromPlayer(p).setPlayer(p).setOnline(true));

		if (!HookPlugins.hasVault()) {
			SBConfig.NOT_VAULT.send();
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		Plugin plugin = getServer().getPluginManager().getPlugin("ScriptBlock");
		if (plugin != null) {
			getServer().getPluginManager().disablePlugin(plugin);
		}

		updater = new Updater(this);
		checkUpdate(Bukkit.getConsoleSender(), false); // 非同期

		mapManager = new MapManager(this);
		mapManager.loadAllScripts();
		mapManager.loadCooldown();
		scriptBlockPlusCommand = new ScriptBlockPlusCommand(this);

		getServer().getPluginManager().registerEvents(new InteractListener(), this);
		getServer().getPluginManager().registerEvents(new JoinQuitListener(this), this);
		getServer().getPluginManager().registerEvents(new ScriptInteractListener(this), this);
		getServer().getPluginManager().registerEvents(new ScriptBreakListener(this), this);
		getServer().getPluginManager().registerEvents(new ScriptWalkListener(this), this);
	}

	@Override
	public void onDisable() {
		if (mapManager != null) {
			mapManager.saveCooldown();
		}
		Bukkit.getOnlinePlayers().forEach(p -> fromPlayer(p).setPlayer(p).setOnline(false));
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (command.getName().equalsIgnoreCase(scriptBlockPlusCommand.getCommandName())) {
			return scriptBlockPlusCommand.onCommand(sender, command, label, args);
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (command.getName().equalsIgnoreCase(scriptBlockPlusCommand.getCommandName())) {
			return scriptBlockPlusCommand.onTabComplete(sender, command, label, args);
		}
		return super.onTabComplete(sender, command, label, args);
	}

	/**
	 * 最新のバージョンが存在するか確認する
	 * @param sender 送信先
	 * @param latestMessage 更新メッセージを表示するかどうか
	 */
	public void checkUpdate(@NotNull CommandSender sender, boolean latestMessage) {
		new Thread(() -> {
			try {
				// updater.debug(true, true);
				updater.init();
				updater.load();
				if (!updater.execute(sender) && latestMessage) {
					SBConfig.NOT_LATEST_PLUGIN.send(sender);
				}
			} catch (Exception e) {
				SBConfig.ERROR_UPDATE.send();
			}
		}).start();
	}

	@NotNull
	public ScriptBlockAPI getAPI() {
		return scriptAPI == null ? scriptAPI = new APIManager(this) : scriptAPI;
	}

	@NotNull
	public MapManager getMapManager() {
		return mapManager;
	}

	@NotNull
	public BaseSBPlayer fromPlayer(@NotNull OfflinePlayer player) {
		return (BaseSBPlayer) SBPlayer.fromPlayer(player);
	}

	@NotNull
	public static ScriptBlock getInstance() {
		return PluginInstance.get(ScriptBlock.class);
	}
}