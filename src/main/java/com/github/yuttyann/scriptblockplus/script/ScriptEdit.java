package com.github.yuttyann.scriptblockplus.script;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.Metadata;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.file.Lang;
import com.github.yuttyann.scriptblockplus.manager.MapManager;
import com.github.yuttyann.scriptblockplus.metadata.ScriptFile;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class ScriptEdit {

	private ScriptType scriptType;
	private ScriptData scriptData;
	private MapManager mapManager;
	private BlockCoords blockCoords;
	private List<String> scripts;

	public ScriptEdit(Location location, ScriptType scriptType) {
		this.scriptType = scriptType;
		this.scriptData = new ScriptData(blockCoords, scriptType);
		this.mapManager = ScriptBlock.getInstance().getMapManager();
		this.blockCoords = new BlockCoords(location);
	}

	public void setLocation(Location location) {
		scriptData.setLocation(location);
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

	public void create(Player player, String script) {
		Metadata.removeAll(player, Metadata.PLAYERCLICK, Metadata.SCRIPTTEXT);
		scriptData.setAuthor(player);
		scriptData.setLastEdit();
		scriptData.setScripts(Arrays.asList(script));
		scriptData.save();
		mapManager.addLocation(blockCoords, scriptType);
		Utils.sendPluginMessage(player, Lang.getScriptCreateMessage(scriptType));
		Utils.sendPluginMessage(Lang.getConsoleScriptCreateMessage(player, scriptType, blockCoords.getWorld(), blockCoords.getCoords()));
	}

	public void add(Player player, String script) {
		Metadata.removeAll(player, Metadata.PLAYERCLICK, Metadata.SCRIPTTEXT);
		if (!scriptData.checkPath()) {
			Utils.sendPluginMessage(player, Lang.getErrorScriptFileCheckMessage());
			return;
		}
		scriptData.addAuthor(player);
		scriptData.setLastEdit();
		scriptData.addScript(script);
		scriptData.save();
		mapManager.removeTimes(blockCoords, scriptType);
		Utils.sendPluginMessage(player, Lang.getScriptAddMessage(scriptType));
		Utils.sendPluginMessage(Lang.getConsoleScriptAddMessage(player, scriptType, blockCoords.getWorld(), blockCoords.getCoords()));
	}

	public void remove(Player player) {
		Metadata.removeAll(player, Metadata.PLAYERCLICK, Metadata.SCRIPTTEXT);
		if (!scriptData.checkPath()) {
			Utils.sendPluginMessage(player, Lang.getErrorScriptFileCheckMessage());
			return;
		}
		scriptData.remove();
		scriptData.save();
		mapManager.removeLocation(blockCoords, scriptType);
		Utils.sendPluginMessage(player, Lang.getScriptRemoveMessage(scriptType));
		Utils.sendPluginMessage(Lang.getConsoleScriptRemoveMessage(player, scriptType, blockCoords.getWorld(), blockCoords.getCoords()));
	}

	public void view(Player player) {
		Metadata.removeAll(player, Metadata.PLAYERCLICK, Metadata.SCRIPTTEXT);
		if (!scriptData.checkPath()) {
			Utils.sendPluginMessage(player, Lang.getErrorScriptFileCheckMessage());
			return;
		}
		List<String> scripts = scriptData.getScripts();
		if (!scripts.isEmpty()) {
			Utils.sendPluginMessage(player, "Author: " + getAuthors());
			Utils.sendPluginMessage(player, "LastEdit: " + scriptData.getLastEdit());
			for (String script : scripts) {
				Utils.sendPluginMessage(player, "- " + script);
			}
			Utils.sendPluginMessage(Lang.getConsoleScriptViewMessage(player, scriptType, blockCoords.getWorld(), blockCoords.getCoords()));
		}
	}

	public void copy(Player player) {
		Metadata.removeAll(player, Metadata.PLAYERCLICK, Metadata.SCRIPTTEXT);
		if (!scriptData.checkPath()) {
			Utils.sendPluginMessage(player, Lang.getErrorScriptFileCheckMessage());
			return;
		}
		scripts = scriptData.getScripts();
		ScriptFile scriptFile = Metadata.getScriptFile();
		scriptFile.removeAll(player);
		scriptFile.set(player, scriptType, this);
		Utils.sendPluginMessage(player, Lang.getScriptCopyMessage(scriptType));
		Utils.sendPluginMessage(Lang.getConsoleScriptCopyMessage(player, scriptType, blockCoords.getWorld(), blockCoords.getCoords()));
	}

	public void paste(Player player, Location location) {
		Metadata.removeAll(player, Metadata.PLAYERCLICK, Metadata.SCRIPTTEXT);
		scriptData.setLocation(location);
		scriptData.setAuthor(player);
		scriptData.setLastEdit();
		scriptData.setScripts(new ArrayList<String>(scripts));
		scriptData.save();
		mapManager.addLocation(scriptData.getLocation(), scriptType);
		Utils.sendPluginMessage(player, Lang.getScriptPasteMessage(scriptType));
		Utils.sendPluginMessage(Lang.getConsoleScriptPasteMessage(player, scriptType, blockCoords.getWorld(), blockCoords.getCoords()));
	}

	//WorldEdit用に軽量化
	public void weRemove(Player player) {
		scriptData.remove();
		mapManager.removeLocation(blockCoords, scriptType);
	}

	//WorldEdit用に軽量化
	public void wePaste(Player player, Location location, boolean overwrite) {
		scriptData.setLocation(blockCoords.getLocation());
		if (!overwrite && scriptData.checkPath()) {
			return;
		}
		scriptData.setAuthor(player);
		scriptData.setLastEdit();
		scriptData.setScripts(new ArrayList<String>(scripts));
		mapManager.addLocation(scriptData.getLocation(), scriptType);
	}

	private String getAuthors() {
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
		return builder.toString();
	}
}