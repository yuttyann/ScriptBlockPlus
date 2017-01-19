package com.github.yuttyann.scriptblockplus.manager;

import java.util.List;

import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.file.Messages;
import com.github.yuttyann.scriptblockplus.file.ScriptData;
import com.github.yuttyann.scriptblockplus.utils.BlockLocation;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class ScriptFileManager {

	private ScriptData scriptData;
	private BlockLocation location;
	private ScriptType scriptType;

	public ScriptFileManager(BlockLocation location, ScriptType scriptType) {
		this.scriptData = new ScriptData(location, scriptType);
		this.location = location;
		this.scriptType = scriptType;
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
		MapManager.addCoords(location, scriptType);
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
		MapManager.removeCoords(location, scriptType);
		Utils.sendPluginMessage(player, Messages.getScriptRemoveMessage(scriptType));
		Utils.sendPluginMessage(Messages.getConsoleScriptRemoveMessage(player, scriptType, location.getWorld(), location.getCoords()));
	}

	//WorldEdit用に軽量化
	public void scriptWERemove(Player player) {
		scriptData.remove();
		MapManager.removeCoords(location, scriptType);
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
}