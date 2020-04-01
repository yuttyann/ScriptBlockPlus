package com.github.yuttyann.scriptblockplus.listener;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.yaml.YamlConfig;
import com.github.yuttyann.scriptblockplus.manager.MapManager;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus ScriptListener クラス
 * @author yuttyann44581
 */
public class ScriptListener implements Listener {

	protected final Plugin plugin;
	protected final ScriptType scriptType;
	protected final MapManager mapManager;

	public ScriptListener(@NotNull ScriptListener listener) {
		this.plugin = listener.getPlugin();
		this.scriptType = listener.getScriptType();
		this.mapManager = listener.getMapManager();
	}

	public ScriptListener(@NotNull Plugin plugin, @NotNull ScriptType scriptType) {
		this.plugin = plugin;
		this.scriptType = scriptType;
		this.mapManager = ScriptBlock.getInstance().getMapManager();
	}

	@NotNull
	public Plugin getPlugin() {
		return plugin;
	}

	@NotNull
	public YamlConfig getScriptFile() {
		return Files.getScriptFile(scriptType);
	}

	@NotNull
	public ScriptType getScriptType() {
		return scriptType;
	}

	@NotNull
	public MapManager getMapManager() {
		return mapManager;
	}
}