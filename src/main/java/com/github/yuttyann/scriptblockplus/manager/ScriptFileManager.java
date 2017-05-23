package com.github.yuttyann.scriptblockplus.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.BlockLocation;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.Metadata;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.file.Lang;
import com.github.yuttyann.scriptblockplus.file.ScriptData;
import com.github.yuttyann.scriptblockplus.metadata.SBMetadata;
import com.github.yuttyann.scriptblockplus.metadata.ScriptFile;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class ScriptFileManager {

	private ScriptBlock plugin;
	private MapManager mapManager;
	private BlockLocation location;
	private ScriptType scriptType;
	private ScriptData scriptData;
	private List<String> scripts;

	public ScriptFileManager(ScriptBlock plugin, BlockLocation location, ScriptType scriptType) {
		this.plugin = plugin;
		this.mapManager = plugin.getMapManager();
		this.location = location;
		this.scriptType = scriptType;
		this.scriptData = new ScriptData(plugin, location, scriptType);
	}

	public void setBlockLocation(BlockLocation location) {
		this.location = location;
		scriptData.setBlockLocation(location);
	}

	public ScriptType getScriptType() {
		return scriptType;
	}

	public boolean checkPath() {
		return scriptData.checkPath();
	}

	public void save() {
		scriptData.save();
	}

	public void scriptCreate(Player player, String script) {
		SBMetadata.remove(player, Metadata.PLAYERCLICK, Metadata.SCRIPTTEXT);
		scriptData.setAuthor(player);
		scriptData.setLastEdit();
		scriptData.setScripts(Arrays.asList(script));
		scriptData.save();
		mapManager.addLocation(location, scriptType);
		Utils.sendPluginMessage(plugin, player, Lang.getScriptCreateMessage(scriptType));
		Utils.sendPluginMessage(plugin, Lang.getConsoleScriptCreateMessage(player, scriptType, location.getWorld(), location.getCoords()));
	}

	public void scriptAdd(Player player, String script) {
		SBMetadata.remove(player, Metadata.PLAYERCLICK, Metadata.SCRIPTTEXT);
		if (!scriptData.checkPath()) {
			Utils.sendPluginMessage(plugin, player, Lang.getErrorScriptFileCheckMessage());
			return;
		}
		scriptData.addAuthor(player);
		scriptData.setLastEdit();
		scriptData.addScript(script);
		scriptData.save();
		mapManager.removeTimes(location, scriptType);
		Utils.sendPluginMessage(plugin, player, Lang.getScriptAddMessage(scriptType));
		Utils.sendPluginMessage(plugin, Lang.getConsoleScriptAddMessage(player, scriptType, location.getWorld(), location.getCoords()));
	}

	public void scriptRemove(Player player) {
		SBMetadata.remove(player, Metadata.PLAYERCLICK, Metadata.SCRIPTTEXT);
		if (!scriptData.checkPath()) {
			Utils.sendPluginMessage(plugin, player, Lang.getErrorScriptFileCheckMessage());
			return;
		}
		scriptData.remove();
		scriptData.save();
		mapManager.removeLocation(location, scriptType);
		Utils.sendPluginMessage(plugin, player, Lang.getScriptRemoveMessage(scriptType));
		Utils.sendPluginMessage(plugin, Lang.getConsoleScriptRemoveMessage(player, scriptType, location.getWorld(), location.getCoords()));
	}

	public void scriptView(Player player) {
		SBMetadata.remove(player, Metadata.PLAYERCLICK, Metadata.SCRIPTTEXT);
		if (!scriptData.checkPath()) {
			Utils.sendPluginMessage(plugin, player, Lang.getErrorScriptFileCheckMessage());
			return;
		}
		List<String> scripts = scriptData.getScripts();
		if (scripts.isEmpty()) {
			return;
		}
		StringBuilder builder = new StringBuilder();
		List<String> authors = scriptData.getAuthors(true);
		int size = authors.size();
		if (size > 1) {
			for (int i = 0; i < size; i++) {
				if (i == 0) {
					builder.append("[");
				}
				builder.append(authors.get(i));
				if (i != (size - 1)) {
					builder.append(", ");
				} else {
					builder.append("]");
				}
			}
		} else {
			try {
				builder.append(authors.get(0));
			} catch (NullPointerException e) {
				builder.append("null");
			}
		}
		Utils.sendPluginMessage(plugin, player, "Author: " + builder.toString());
		Utils.sendPluginMessage(plugin, player, "LastEdit: " + scriptData.getLastEdit());
		for (String script : scripts) {
			Utils.sendPluginMessage(plugin, player, "- " + script);
		}
		Utils.sendPluginMessage(plugin, Lang.getConsoleScriptViewMessage(player, scriptType, location.getWorld(), location.getCoords()));
	}

	public void scriptCopy(Player player) {
		SBMetadata.remove(player, Metadata.PLAYERCLICK, Metadata.SCRIPTTEXT);
		if (!scriptData.checkPath()) {
			Utils.sendPluginMessage(plugin, player, Lang.getErrorScriptFileCheckMessage());
			return;
		}
		scripts = scriptData.getScripts();
		ScriptFile scriptFile = SBMetadata.getScriptFile();
		scriptFile.removeAll(player);
		scriptFile.set(player, scriptType, this);
		Utils.sendPluginMessage(plugin, player, Lang.getScriptCopyMessage(scriptType));
		Utils.sendPluginMessage(plugin, Lang.getConsoleScriptCopyMessage(player, scriptType, location.getWorld(), location.getCoords()));
	}

	public void scriptPaste(Player player, BlockLocation location) {
		SBMetadata.remove(player, Metadata.PLAYERCLICK, Metadata.SCRIPTTEXT);
		scriptData.setBlockLocation(location);
		scriptData.setAuthor(player);
		scriptData.setLastEdit();
		scriptData.setScripts(new ArrayList<String>(scripts));
		scriptData.save();
		mapManager.addLocation(location, scriptType);
		Utils.sendPluginMessage(plugin, player, Lang.getScriptPasteMessage(scriptType));
		Utils.sendPluginMessage(plugin, Lang.getConsoleScriptPasteMessage(player, scriptType, location.getWorld(), location.getCoords()));
	}

	//WorldEdit用に軽量化
	public void scriptWERemove(Player player) {
		scriptData.remove();
		mapManager.removeLocation(location, scriptType);
	}

	//WorldEdit用に軽量化
	public void scriptWEPaste(Player player, BlockLocation location, boolean overwrite) {
		scriptData.setBlockLocation(location);
		if (!overwrite && scriptData.checkPath()) {
			return;
		}
		scriptData.setAuthor(player);
		scriptData.setLastEdit();
		scriptData.setScripts(new ArrayList<String>(scripts));
		mapManager.addLocation(location, scriptType);
	}
}