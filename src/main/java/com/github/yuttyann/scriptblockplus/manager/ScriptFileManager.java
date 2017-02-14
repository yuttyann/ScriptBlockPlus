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
	private ScriptData scriptData;
	private BlockLocation location;
	private ScriptType scriptType;
	private List<String> scripts;

	public ScriptFileManager(BlockLocation location, ScriptType scriptType) {
		this.mapManager = ScriptBlock.instance.getMapManager();
		this.scriptData = new ScriptData(location, scriptType);
		this.location = location;
		this.scriptType = scriptType;
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
		MetadataManager.removeAllMetadata(player);
		scriptData.setAuthor(player);
		scriptData.setLastEdit();
		scriptData.setCreateScripts(script);
		scriptData.save();
		mapManager.addCoords(location, scriptType);
		Utils.sendPluginMessage(player, Messages.getScriptCreateMessage(scriptType));
		Utils.sendPluginMessage(Messages.getConsoleScriptCreateMessage(player, scriptType, location.getWorld(), location.getCoords()));
	}

	public void scriptAdd(Player player, String script) {
		MetadataManager.removeAllMetadata(player);
		if (!scriptData.checkPath()) {
			Utils.sendPluginMessage(player, Messages.getErrorScriptFileCheckMessage());
			return;
		}
		scriptData.addAuthor(player);
		scriptData.setLastEdit();
		scriptData.addScripts(script);
		scriptData.save();
		mapManager.removeTimes(location.getFullCoords());
		Utils.sendPluginMessage(player, Messages.getScriptAddMessage(scriptType));
		Utils.sendPluginMessage(Messages.getConsoleScriptAddMessage(player, scriptType, location.getWorld(), location.getCoords()));
	}

	public void scriptRemove(Player player) {
		MetadataManager.removeAllMetadata(player);
		if (!scriptData.checkPath()) {
			Utils.sendPluginMessage(player, Messages.getErrorScriptFileCheckMessage());
			return;
		}
		scriptData.remove();
		scriptData.save();
		mapManager.removeCoords(location, scriptType);
		Utils.sendPluginMessage(player, Messages.getScriptRemoveMessage(scriptType));
		Utils.sendPluginMessage(Messages.getConsoleScriptRemoveMessage(player, scriptType, location.getWorld(), location.getCoords()));
	}

	public void scriptView(Player player) {
		MetadataManager.removeAllMetadata(player);
		if (!scriptData.checkPath()) {
			Utils.sendPluginMessage(player, Messages.getErrorScriptFileCheckMessage());
			return;
		}
		List<String> list = scriptData.getScripts();
		if (list.isEmpty()) {
			return;
		}
		StringBuilder builder = new StringBuilder();
		List<String> authors = scriptData.getAuthors(true);
		int length = authors.size();
		if (length > 1) {
			for (int i = 0 ; i < length; i++) {
				if (i == 0) {
					builder.append("[");
				}
				builder.append(authors.get(i));
				if (i != (length - 1)) {
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
		for (String script : list) {
			builder.setLength(0);
			Utils.sendPluginMessage(player, builder.append("- ").append(script).toString());
		}
		Utils.sendPluginMessage(Messages.getConsoleScriptViewMessage(player, scriptType, location.getWorld(), location.getCoords()));
	}

	public void scriptCopy(Player player) {
		MetadataManager.removeAllMetadata(player);
		if (!checkPath()) {
			Utils.sendPluginMessage(player, Messages.getErrorScriptFileCheckMessage());
			return;
		}
		scripts = scriptData.getScripts();
		ScriptFile.removeAllMetadata(player);
		ScriptFile.setMetadata(player, scriptType, this);
		Utils.sendPluginMessage(player, Messages.getScriptCopyMessage(scriptType));
		Utils.sendPluginMessage(Messages.getConsoleScriptCopyMessage(player, scriptType, location.getWorld(), location.getCoords()));
	}

	public void scriptPaste(Player player, BlockLocation location) {
		MetadataManager.removeAllMetadata(player);
		scriptData.setBlockLocation(location);
		scriptData.setAuthor(player);
		scriptData.setLastEdit();
		scriptData.setScripts(new ArrayList<String>(scripts));
		scriptData.save();
		mapManager.addCoords(location, scriptType);
		Utils.sendPluginMessage(player, Messages.getScriptPasteMessage(scriptType));
		Utils.sendPluginMessage(Messages.getConsoleScriptPasteMessage(player, scriptType, location.getWorld(), location.getCoords()));
	}

	//WorldEdit用に軽量化
	public void scriptWERemove(Player player) {
		scriptData.remove();
		mapManager.removeCoords(location, scriptType);
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
		mapManager.addCoords(location, scriptType);
	}
}