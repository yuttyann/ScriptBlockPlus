package com.github.yuttyann.scriptblockplus.manager;

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
import com.github.yuttyann.scriptblockplus.file.ScriptData;
import com.github.yuttyann.scriptblockplus.metadata.ScriptFile;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class ScriptFileManager extends ScriptData {

	private MapManager mapManager;
	private List<String> scripts;

	public ScriptFileManager(ScriptBlock plugin, Location location, ScriptType scriptType) {
		super(plugin, location, scriptType);
		this.mapManager = plugin.getMapManager();
	}

	public void scriptCreate(Player player, String script) {
		Metadata.removeAll(player, Metadata.PLAYERCLICK, Metadata.SCRIPTTEXT);
		setAuthor(player);
		setLastEdit();
		setScripts(Arrays.asList(script));
		save();
		Location location = getLocation();
		ScriptType scriptType = getScriptType();
		mapManager.addLocation(location, scriptType);
		Utils.sendPluginMessage(player, Lang.getScriptCreateMessage(scriptType));
		Utils.sendPluginMessage(Lang.getConsoleScriptCreateMessage(player, scriptType, getLocation().getWorld(), BlockCoords.getCoords(location)));
	}

	public void scriptAdd(Player player, String script) {
		Metadata.removeAll(player, Metadata.PLAYERCLICK, Metadata.SCRIPTTEXT);
		if (!checkPath()) {
			Utils.sendPluginMessage(player, Lang.getErrorScriptFileCheckMessage());
			return;
		}
		addAuthor(player);
		setLastEdit();
		addScript(script);
		save();
		Location location = getLocation();
		ScriptType scriptType = getScriptType();
		mapManager.removeTimes(location, scriptType);
		Utils.sendPluginMessage(player, Lang.getScriptAddMessage(scriptType));
		Utils.sendPluginMessage(Lang.getConsoleScriptAddMessage(player, scriptType, location.getWorld(), BlockCoords.getCoords(location)));
	}

	public void scriptRemove(Player player) {
		Metadata.removeAll(player, Metadata.PLAYERCLICK, Metadata.SCRIPTTEXT);
		if (!checkPath()) {
			Utils.sendPluginMessage(player, Lang.getErrorScriptFileCheckMessage());
			return;
		}
		remove();
		save();
		Location location = getLocation();
		ScriptType scriptType = getScriptType();
		mapManager.removeLocation(location, scriptType);
		Utils.sendPluginMessage(player, Lang.getScriptRemoveMessage(scriptType));
		Utils.sendPluginMessage(Lang.getConsoleScriptRemoveMessage(player, scriptType, location.getWorld(), BlockCoords.getCoords(location)));
	}

	public void scriptView(Player player) {
		Metadata.removeAll(player, Metadata.PLAYERCLICK, Metadata.SCRIPTTEXT);
		if (!checkPath()) {
			Utils.sendPluginMessage(player, Lang.getErrorScriptFileCheckMessage());
			return;
		}
		List<String> scripts = getScripts();
		if (scripts.isEmpty()) {
			return;
		}
		Utils.sendPluginMessage(player, "Author: " + getAuthors());
		Utils.sendPluginMessage(player, "LastEdit: " + getLastEdit());
		for (String script : scripts) {
			Utils.sendPluginMessage(player, "- " + script);
		}
		Location location = getLocation();
		Utils.sendPluginMessage(Lang.getConsoleScriptViewMessage(player, getScriptType(), location.getWorld(), BlockCoords.getCoords(location)));
	}

	public void scriptCopy(Player player) {
		Metadata.removeAll(player, Metadata.PLAYERCLICK, Metadata.SCRIPTTEXT);
		if (!checkPath()) {
			Utils.sendPluginMessage(player, Lang.getErrorScriptFileCheckMessage());
			return;
		}
		scripts = getScripts();
		Location location = getLocation();
		ScriptType scriptType = getScriptType();
		ScriptFile scriptFile = (ScriptFile) Metadata.SCRIPTFILE.toMetadata();
		scriptFile.removeAll(player);
		scriptFile.set(player, scriptType, this);
		Utils.sendPluginMessage(player, Lang.getScriptCopyMessage(scriptType));
		Utils.sendPluginMessage(Lang.getConsoleScriptCopyMessage(player, scriptType, location.getWorld(), BlockCoords.getCoords(location)));
	}

	public void scriptPaste(Player player, Location location) {
		Metadata.removeAll(player, Metadata.PLAYERCLICK, Metadata.SCRIPTTEXT);
		setLocation(location);
		setAuthor(player);
		setLastEdit();
		setScripts(new ArrayList<String>(scripts));
		save();
		ScriptType scriptType = getScriptType();
		mapManager.addLocation(location, scriptType);
		Utils.sendPluginMessage(player, Lang.getScriptPasteMessage(scriptType));
		Utils.sendPluginMessage(Lang.getConsoleScriptPasteMessage(player, scriptType, location.getWorld(), BlockCoords.getCoords(location)));
	}

	//WorldEdit用に軽量化
	public void scriptWERemove(Player player) {
		remove();
		mapManager.removeLocation(getLocation(), getScriptType());
	}

	//WorldEdit用に軽量化
	public void scriptWEPaste(Player player, Location location, boolean overwrite) {
		setLocation(location);
		if (!overwrite && checkPath()) {
			return;
		}
		setAuthor(player);
		setLastEdit();
		setScripts(new ArrayList<String>(scripts));
		mapManager.addLocation(location, getScriptType());
	}

	private String getAuthors() {
		StringBuilder builder = new StringBuilder();
		List<String> authors = getAuthors(true);
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