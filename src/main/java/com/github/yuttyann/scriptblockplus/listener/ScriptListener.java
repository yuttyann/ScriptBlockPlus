package com.github.yuttyann.scriptblockplus.listener;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.yaml.YamlConfig;
import com.github.yuttyann.scriptblockplus.manager.MapManager;
import com.github.yuttyann.scriptblockplus.script.ScriptType;

public class ScriptListener implements Listener {

	protected final Plugin plugin;
	protected final ScriptType scriptType;
	protected final MapManager mapManager;

	public ScriptListener(ScriptListener listener) {
		this.plugin = listener.getPlugin();
		this.scriptType = listener.getScriptType();
		this.mapManager = listener.getMapManager();
	}

	public ScriptListener(Plugin plugin, ScriptType scriptType) {
		this.plugin = plugin;
		this.scriptType = scriptType;
		this.mapManager = ScriptBlock.getInstance().getMapManager();
	}

	public Plugin getPlugin() {
		return plugin;
	}

	public YamlConfig getScriptFile() {
		return Files.getScriptFile(scriptType);
	}

	public ScriptType getScriptType() {
		return scriptType;
	}

	public MapManager getMapManager() {
		return mapManager;
	}
}