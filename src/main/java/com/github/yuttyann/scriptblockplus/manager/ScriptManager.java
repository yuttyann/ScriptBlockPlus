package com.github.yuttyann.scriptblockplus.manager;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.script.hook.HookPlugins;
import com.github.yuttyann.scriptblockplus.script.hook.VaultEconomy;
import com.github.yuttyann.scriptblockplus.script.hook.VaultPermission;

public class ScriptManager implements Listener {

	protected final Plugin plugin;
	protected final ScriptType scriptType;
	protected final MapManager mapManager;
	protected final VaultEconomy vaultEconomy;
	protected final VaultPermission vaultPermission;
	protected final OptionManager optionManager;
	protected final EndProcessManager endProcessManager;

	public ScriptManager(ScriptManager scriptManager) {
		this.plugin = scriptManager.getPlugin();
		this.scriptType = scriptManager.getScriptType();
		this.mapManager = scriptManager.getMapManager();
		this.vaultEconomy = scriptManager.getVaultEconomy();
		this.vaultPermission = scriptManager.getVaultPermission();
		this.optionManager = scriptManager.getOptionManager();
		this.endProcessManager = scriptManager.getEndProcessManager();
	}

	public ScriptManager(ScriptBlock plugin, ScriptType scriptType) {
		this.plugin = plugin;
		this.scriptType = scriptType;
		this.mapManager = plugin == null ? null : plugin.getMapManager();
		this.vaultEconomy = HookPlugins.getVaultEconomy();
		this.vaultPermission = HookPlugins.getVaultPermission();
		this.optionManager = new OptionManager();
		if (optionManager.isEmpty()) {
			optionManager.registerDefaults();
		}
		this.endProcessManager = new EndProcessManager();
		if (endProcessManager.isEmpty()) {
			endProcessManager.registerDefaults();
		}
	}

	public Plugin getPlugin() {
		return plugin;
	}

	public ScriptType getScriptType() {
		return scriptType;
	}

	public MapManager getMapManager() {
		return mapManager;
	}

	public VaultEconomy getVaultEconomy() {
		return vaultEconomy;
	}

	public VaultPermission getVaultPermission() {
		return vaultPermission;
	}

	public OptionManager getOptionManager() {
		return optionManager;
	}

	public EndProcessManager getEndProcessManager() {
		return endProcessManager;
	}
}