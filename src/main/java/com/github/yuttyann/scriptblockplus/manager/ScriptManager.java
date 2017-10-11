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
	protected final OptionManager optionManager;
	protected final EndProcessManager endProcessManager;
	protected final VaultEconomy vaultEconomy;
	protected final VaultPermission vaultPermission;

	public ScriptManager(ScriptManager scriptManager) {
		this.plugin = scriptManager.getPlugin();
		this.scriptType = scriptManager.getScriptType();
		this.mapManager = scriptManager.getMapManager();
		this.optionManager = scriptManager.getOptionManager();
		this.endProcessManager = scriptManager.getEndProcessManager();
		this.vaultEconomy = scriptManager.getVaultEconomy();
		this.vaultPermission = scriptManager.getVaultPermission();
	}

	public ScriptManager(ScriptBlock plugin, ScriptType scriptType) {
		this.plugin = plugin;
		this.scriptType = scriptType;
		this.mapManager = plugin == null ? null : plugin.getMapManager();
		this.optionManager = new OptionManager();
		this.endProcessManager = new EndProcessManager();
		this.vaultEconomy = HookPlugins.getVaultEconomy();
		this.vaultPermission = HookPlugins.getVaultPermission();
		if (optionManager.isEmpty()) {
			optionManager.registerDefaultOptions();
		}
		if (endProcessManager.isEmpty()) {
			endProcessManager.registerDefaultProcess();
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

	public OptionManager getOptionManager() {
		return optionManager;
	}

	public EndProcessManager getEndProcessManager() {
		return endProcessManager;
	}

	public VaultEconomy getVaultEconomy() {
		return vaultEconomy;
	}

	public VaultPermission getVaultPermission() {
		return vaultPermission;
	}
}
