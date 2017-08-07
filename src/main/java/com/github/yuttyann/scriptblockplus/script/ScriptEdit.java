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

	public ScriptEdit(BlockCoords blockCoords, ScriptType scriptType) {
		this.scriptType = scriptType;
		this.scriptData = new ScriptData(blockCoords, scriptType);
		this.mapManager = ScriptBlock.getInstance().getMapManager();
		this.blockCoords = blockCoords;
		this.scripts = scriptData.getScripts();
	}

	public void setLocation(Location location) {
		scriptData.setLocation(location);
		blockCoords = new BlockCoords(location);
	}

	public void updateScripts() {
		scripts = scriptData.getScripts();
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
		removeAll(player);
		scriptData.setAuthor(player);
		scriptData.setLastEdit();
		scriptData.setScripts(Arrays.asList(script));
		scriptData.save();
		mapManager.addLocation(blockCoords, scriptType);
		Utils.sendPluginMessage(player, Lang.getScriptCreateMessage(scriptType));
		Utils.sendPluginMessage(Lang.getConsoleScriptCreateMessage(player, scriptType, blockCoords.getWorld(), blockCoords.getCoords()));
	}

	public void add(Player player, String script) {
		removeAll(player);
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
		removeAll(player);
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
		removeAll(player);
		if (!scriptData.checkPath() || scripts.isEmpty()) {
			Utils.sendPluginMessage(player, Lang.getErrorScriptFileCheckMessage());
			return;
		}
		Utils.sendPluginMessage(player, "Author: " + getAuthors());
		Utils.sendPluginMessage(player, "LastEdit: " + scriptData.getLastEdit());
		for (String script : scripts) {
			Utils.sendPluginMessage(player, "- " + script);
		}
		Utils.sendPluginMessage(Lang.getConsoleScriptViewMessage(player, scriptType, blockCoords.getWorld(), blockCoords.getCoords()));
	}

	public void copy(Player player) {
		removeAll(player);
		if (!scriptData.checkPath()) {
			Utils.sendPluginMessage(player, Lang.getErrorScriptFileCheckMessage());
			return;
		}
		ScriptFile scriptFile = Metadata.getScriptFile();
		scriptFile.removeAll(player);
		scriptFile.set(player, scriptType, this);
		Utils.sendPluginMessage(player, Lang.getScriptCopyMessage(scriptType));
		Utils.sendPluginMessage(Lang.getConsoleScriptCopyMessage(player, scriptType, blockCoords.getWorld(), blockCoords.getCoords()));
	}

	public void paste(Player player, Location location) {
		removeAll(player);
		setLocation(location);
		scriptData.setAuthor(player);
		scriptData.setLastEdit();
		scriptData.setScripts(new ArrayList<String>(scripts));
		scriptData.save();
		mapManager.addLocation(blockCoords, scriptType);
		Utils.sendPluginMessage(player, Lang.getScriptPasteMessage(scriptType));
		Utils.sendPluginMessage(Lang.getConsoleScriptPasteMessage(player, scriptType, blockCoords.getWorld(), blockCoords.getCoords()));
	}

	//WorldEdit用に軽量化
	public boolean weRemove(Player player, Location location) {
		setLocation(location);
		if (!scriptData.checkPath()) {
			return false;
		}
		scriptData.remove();
		mapManager.removeLocation(blockCoords, scriptType);
		return true;
	}

	//WorldEdit用に軽量化
	public boolean wePaste(Player player, Location location, boolean overwrite) {
		setLocation(location);
		if (!overwrite && scriptData.checkPath()) {
			return false;
		}
		scriptData.setAuthor(player);
		scriptData.setLastEdit();
		scriptData.setScripts(new ArrayList<String>(scripts));
		mapManager.addLocation(blockCoords, scriptType);
		return true;
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

	private void removeAll(Player player) {
		Metadata.removeAll(player, Metadata.CLICKACTION, Metadata.SCRIPTTEXT);
	}
}