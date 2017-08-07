package com.github.yuttyann.scriptblockplus.manager;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.YamlConfig;
import com.github.yuttyann.scriptblockplus.script.hook.HookPlugins;
import com.github.yuttyann.scriptblockplus.script.hook.VaultEconomy;
import com.github.yuttyann.scriptblockplus.script.hook.VaultPermission;

public class ScriptManager implements Listener {

	protected final JavaPlugin plugin;
	protected final ScriptType scriptType;
	protected final YamlConfig scriptFile;
	protected final MapManager mapManager;
	protected final OptionManager optionManager;
	protected final VaultEconomy vaultEconomy;
	protected final VaultPermission vaultPermission;

	public ScriptManager(ScriptManager scriptManager) {
		this.plugin = scriptManager.getPlugin();
		this.scriptType = scriptManager.getScriptType();
		this.scriptFile = scriptManager.getScriptFile();
		this.mapManager = scriptManager.getMapManager();
		this.optionManager = scriptManager.getOptionManager();
		this.vaultEconomy = scriptManager.getVaultEconomy();
		this.vaultPermission = scriptManager.getVaultPermission();
	}

	public ScriptManager(ScriptBlock plugin, ScriptType scriptType) {
		this.plugin = plugin;
		this.scriptType = scriptType;
		this.scriptFile = Files.getScriptFile(scriptType);
		this.mapManager = plugin.getMapManager();
		this.optionManager = new OptionManager(this);
		this.vaultEconomy = HookPlugins.getVaultEconomy();
		this.vaultPermission = HookPlugins.getVaultPermission();

		if (!optionManager.hasOption()) {
			optionManager.registerOptions();
		}
	}

	public JavaPlugin getPlugin() {
		return plugin;
	}

	public ScriptType getScriptType() {
		return scriptType;
	}

	public YamlConfig getScriptFile() {
		return scriptFile;
	}

	public MapManager getMapManager() {
		return mapManager;
	}

	public OptionManager getOptionManager() {
		return optionManager;
	}

	public VaultEconomy getVaultEconomy() {
		return vaultEconomy;
	}

	public VaultPermission getVaultPermission() {
		return vaultPermission;
	}
}
