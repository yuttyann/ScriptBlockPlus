package com.github.yuttyann.scriptblockplus.manager;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;

public class ScriptManager implements Listener {

	protected final Plugin plugin;
	protected final ScriptType scriptType;
	protected final MapManager mapManager;

	public ScriptManager(ScriptManager scriptManager) {
		this.plugin = scriptManager.getPlugin();
		this.scriptType = scriptManager.getScriptType();
		this.mapManager = scriptManager.getMapManager();
	}

	public ScriptManager(ScriptBlock plugin, ScriptType scriptType) {
		this.plugin = plugin;
		this.scriptType = scriptType;
		this.mapManager = plugin == null ? null : plugin.getMapManager();
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
}