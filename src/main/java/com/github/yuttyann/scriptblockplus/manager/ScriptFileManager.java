package com.github.yuttyann.scriptblockplus.manager;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.Messages;
import com.github.yuttyann.scriptblockplus.file.Yaml;
import com.github.yuttyann.scriptblockplus.manager.OptionManager.ScriptType;
import com.github.yuttyann.scriptblockplus.util.BlockLocation;
import com.github.yuttyann.scriptblockplus.util.Utils;

public class ScriptFileManager {

	private ScriptType scriptType;
	private Yaml scriptFile;
	private String scriptPath;
	private BlockLocation location;

	public ScriptFileManager(BlockLocation location, ScriptType scriptType) {
		this.location = location;
		this.scriptType = scriptType;
		this.scriptFile = Files.getScriptFile(scriptType);
		this.scriptPath = location.getWorld().getName() + "." + location.getCoords();
	}

	public boolean checkPath() {
		return scriptFile.contains(scriptPath + ".Author");
	}

	public void save() {
		scriptFile.save();
	}

	public void scriptCreate(Player player, String script) {
		MetadataManager.removeAllMetadata(player);
		List<String> list = scriptFile.getStringList(scriptPath + ".Scripts");
		try {
			list.set(0, script);
		} catch (IndexOutOfBoundsException e) {
			list.add(script);
		}
		scriptFile.set(scriptPath + ".Author", player.getUniqueId().toString());
		scriptFile.set(scriptPath + ".LastEdit", Utils.getDateFormat("yyyy/MM/dd HH:mm:ss"));
		scriptFile.set(scriptPath + ".Scripts", list);
		scriptFile.save();
		String fullCoords = location.getFullCoords();
		switch (scriptType) {
		case INTERACT:
			if (!MapManager.getInteractCoords().contains(fullCoords)) {
				MapManager.getInteractCoords().add(fullCoords);
			}
			break;
		case WALK:
			if (!MapManager.getWalkCoords().contains(fullCoords)) {
				MapManager.getWalkCoords().add(fullCoords);
			}
			break;
		}
		Utils.sendPluginMessage(player, Messages.getScriptCreateMessage(scriptType));
		Utils.sendPluginMessage(Messages.getConsoleScriptCreateMessage(player, scriptType, location.getWorld(), location.getCoords()));
	}

	public void scriptAdd(Player player, String script) {
		MetadataManager.removeAllMetadata(player);
		if (!checkPath()) {
			Utils.sendPluginMessage(player, Messages.getErrorScriptFileCheckMessage());
			return;
		}
		List<String> list = scriptFile.getStringList(scriptPath + ".Scripts");
		list.add(script);
		StringBuilder builder = new StringBuilder();
		String uuid = player.getUniqueId().toString();
		String author = scriptFile.getString(scriptPath + ".Author");
		String[] split = author.split(", ");
		int length = split.length;
		if (length > 1 && !Arrays.asList(split).contains(uuid)) {
			builder.append(author).append(", ").append(uuid);
		} else {
			builder.append(author);
		}
		scriptFile.set(scriptPath + ".Author", builder.toString());
		scriptFile.set(scriptPath + ".LastEdit", Utils.getDateFormat("yyyy/MM/dd HH:mm:ss"));
		scriptFile.set(scriptPath + ".Scripts", list);
		scriptFile.save();
		Utils.sendPluginMessage(player, Messages.getScriptAddMessage(scriptType));
		Utils.sendPluginMessage(Messages.getConsoleScriptAddMessage(player, scriptType, location.getWorld(), location.getCoords()));
	}

	public void scriptRemove(Player player) {
		MetadataManager.removeAllMetadata(player);
		if (!checkPath()) {
			Utils.sendPluginMessage(player, Messages.getErrorScriptFileCheckMessage());
			return;
		}
		scriptFile.set(scriptPath, null);
		scriptFile.save();
		String fullCoords = location.getFullCoords();
		switch (scriptType) {
		case INTERACT:
			if (MapManager.getInteractCoords().contains(fullCoords)) {
				MapManager.getInteractCoords().remove(fullCoords);
			}
			break;
		case WALK:
			if (MapManager.getWalkCoords().contains(fullCoords)) {
				MapManager.getWalkCoords().remove(fullCoords);
			}
			break;
		}
		Utils.sendPluginMessage(player, Messages.getScriptRemoveMessage(scriptType));
		Utils.sendPluginMessage(Messages.getConsoleScriptRemoveMessage(player, scriptType, location.getWorld(), location.getCoords()));
	}

	//WorldEdit用に軽量化
	public void scriptWERemove(Player player) {
		scriptFile.set(scriptPath, null);
		String fullCoords = location.getFullCoords();
		switch (scriptType) {
		case INTERACT:
			if (MapManager.getInteractCoords().contains(fullCoords)) {
				MapManager.getInteractCoords().remove(fullCoords);
			}
			break;
		case WALK:
			if (MapManager.getWalkCoords().contains(fullCoords)) {
				MapManager.getWalkCoords().remove(fullCoords);
			}
			break;
		}
	}

	public void scriptView(Player player) {
		MetadataManager.removeAllMetadata(player);
		if (!checkPath()) {
			Utils.sendPluginMessage(player, Messages.getErrorScriptFileCheckMessage());
			return;
		}
		List<String> list = scriptFile.getStringList(scriptPath + ".Scripts");
		if (list.isEmpty()) {
			return;
		}
		StringBuilder builder = new StringBuilder();
		String[] authors = scriptFile.getString(scriptPath + ".Author").split(", ");
		int length = authors.length;
		if (length > 1) {
			for (int i = 0 ; i < length; i++) {
				if (i == 0) {
					builder.append("[");
				}
				builder.append(Utils.getName(authors[i]));
				if (i != (length - 1)) {
					builder.append(", ");
				} else {
					builder.append("]");
				}
			}
		} else {
			builder.append(Utils.getName(authors[0]));
		}
		Utils.sendPluginMessage(player, "Author: " + builder.toString());
		Utils.sendPluginMessage(player, "LastEdit: " + scriptFile.getString(scriptPath + ".LastEdit"));
		for (String script : list) {
			builder.setLength(0);
			Utils.sendPluginMessage(player, builder.append("- ").append(script).toString());
		}
		Utils.sendPluginMessage(Messages.getConsoleScriptViewMessage(player, scriptType, location.getWorld(), location.getCoords()));
	}
}
