package com.github.yuttyann.scriptblockplus.manager;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.BlockLocation;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.file.Messages;
import com.github.yuttyann.scriptblockplus.file.ScriptData;
import com.github.yuttyann.scriptblockplus.manager.MetadataManager.ScriptFile;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class ScriptFileManager {

	private MapManager mapManager;
	private BlockLocation location;
	private ScriptType scriptType;
	private ScriptData scriptData;
	private List<String> scripts;

	public ScriptFileManager(ScriptBlock plugin, BlockLocation location, ScriptType scriptType) {
		this.mapManager = plugin.getMapManager();
		this.location = location;
		this.scriptType = scriptType;
		this.scriptData = new ScriptData(plugin, location, scriptType);
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

	public void setBlockLocation(BlockLocation location) {
		scriptData.setBlockLocation(location);;
	}

	public void scriptCreate(Player player, String script) {
		MetadataManager.removeAll(player);
		scriptData.setAuthor(player);
		scriptData.setLastEdit();
		scriptData.setCreateScript(script);
		scriptData.save();
		mapManager.addLocation(location, scriptType);
		Utils.sendPluginMessage(player, Messages.getScriptCreateMessage(scriptType));
		Utils.sendPluginMessage(Messages.getConsoleScriptCreateMessage(player, scriptType, location.getWorld(), location.getCoords()));
	}

	public void scriptAdd(Player player, String script) {
		MetadataManager.removeAll(player);
		if (!scriptData.checkPath()) {
			Utils.sendPluginMessage(player, Messages.getErrorScriptFileCheckMessage());
			return;
		}
		scriptData.addAuthor(player);
		scriptData.setLastEdit();
		scriptData.addScript(script);
		scriptData.save();
		mapManager.removeTimes(location.getFullCoords());
		Utils.sendPluginMessage(player, Messages.getScriptAddMessage(scriptType));
		Utils.sendPluginMessage(Messages.getConsoleScriptAddMessage(player, scriptType, location.getWorld(), location.getCoords()));
	}

	public void scriptRemove(Player player) {
		MetadataManager.removeAll(player);
		if (!scriptData.checkPath()) {
			Utils.sendPluginMessage(player, Messages.getErrorScriptFileCheckMessage());
			return;
		}
		scriptData.remove();
		scriptData.save();
		mapManager.removeLocation(location, scriptType);
		Utils.sendPluginMessage(player, Messages.getScriptRemoveMessage(scriptType));
		Utils.sendPluginMessage(Messages.getConsoleScriptRemoveMessage(player, scriptType, location.getWorld(), location.getCoords()));
	}

	public void scriptView(Player player) {
		MetadataManager.removeAll(player);
		if (!scriptData.checkPath()) {
			Utils.sendPluginMessage(player, Messages.getErrorScriptFileCheckMessage());
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
		Utils.sendPluginMessage(player, "Author: " + builder.toString());
		Utils.sendPluginMessage(player, "LastEdit: " + scriptData.getLastEdit());
		for (String script : scripts) {
			Utils.sendPluginMessage(player, "- " + script);
		}
		Utils.sendPluginMessage(Messages.getConsoleScriptViewMessage(player, scriptType, location.getWorld(), location.getCoords()));
	}

	public void scriptCopy(Player player) {
		MetadataManager.removeAll(player);
		if (!checkPath()) {
			Utils.sendPluginMessage(player, Messages.getErrorScriptFileCheckMessage());
			return;
		}
		scripts = scriptData.getScripts();
		ScriptFile scriptFile = MetadataManager.getScriptFile();
		scriptFile.removeAll(player);
		scriptFile.set(player, scriptType, this);
		Utils.sendPluginMessage(player, Messages.getScriptCopyMessage(scriptType));
		Utils.sendPluginMessage(Messages.getConsoleScriptCopyMessage(player, scriptType, location.getWorld(), location.getCoords()));
	}

	public void scriptPaste(Player player, BlockLocation location) {
		MetadataManager.removeAll(player);
		scriptData.setBlockLocation(location);
		scriptData.setAuthor(player);
		scriptData.setLastEdit();
		scriptData.setScripts(new ArrayList<String>(scripts));
		scriptData.save();
		mapManager.addLocation(location, scriptType);
		Utils.sendPluginMessage(player, Messages.getScriptPasteMessage(scriptType));
		Utils.sendPluginMessage(Messages.getConsoleScriptPasteMessage(player, scriptType, location.getWorld(), location.getCoords()));
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