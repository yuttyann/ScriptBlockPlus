package com.github.yuttyann.scriptblockplus.script;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.text.StrBuilder;
import org.bukkit.Location;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.manager.MapManager;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public final class ScriptEdit {

	private ScriptType scriptType;
	private ScriptData scriptData;
	private MapManager mapManager;
	private List<String> scripts;

	public ScriptEdit(Location location, ScriptType scriptType) {
		this.scriptType = scriptType;
		this.scriptData = new ScriptData(location, scriptType);
		this.mapManager = ScriptBlock.getInstance().getMapManager();
		this.scripts = scriptData.getScripts();
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

	public void create(SBPlayer sbPlayer, String script) {
		sbPlayer.setScriptLine(null);
		sbPlayer.setClickAction(null);
		scriptData.setAuthor(sbPlayer.getUniqueId().toString());
		scriptData.setLastEdit();
		scriptData.setScripts(Arrays.asList(script));
		scriptData.save();
		mapManager.addCoords(scriptType, scriptData.getLocation());
		Utils.sendMessage(sbPlayer, SBConfig.getScriptCreateMessage(scriptType));
		Utils.sendMessage(SBConfig.getConsoleScriptCreateMessage(sbPlayer.getName(), scriptType, scriptData.getLocation()));
	}

	public void add(SBPlayer sbPlayer, String script) {
		sbPlayer.setScriptLine(null);
		sbPlayer.setClickAction(null);
		if (!scriptData.checkPath()) {
			Utils.sendMessage(sbPlayer, SBConfig.getErrorScriptFileCheckMessage());
			return;
		}
		scriptData.addAuthor(sbPlayer.getUniqueId().toString());
		scriptData.setLastEdit();
		scriptData.addScript(script);
		scriptData.save();
		mapManager.removeTimes(scriptType, scriptData.getLocation());
		Utils.sendMessage(sbPlayer, SBConfig.getScriptAddMessage(scriptType));
		Utils.sendMessage(SBConfig.getConsoleScriptAddMessage(sbPlayer.getName(), scriptType, scriptData.getLocation()));
	}

	public void remove(SBPlayer sbPlayer) {
		sbPlayer.setScriptLine(null);
		sbPlayer.setClickAction(null);
		if (!scriptData.checkPath()) {
			Utils.sendMessage(sbPlayer, SBConfig.getErrorScriptFileCheckMessage());
			return;
		}
		scriptData.remove();
		scriptData.save();
		mapManager.removeCoords(scriptType, scriptData.getLocation());
		Utils.sendMessage(sbPlayer, SBConfig.getScriptRemoveMessage(scriptType));
		Utils.sendMessage(SBConfig.getConsoleScriptRemoveMessage(sbPlayer.getName(), scriptType, scriptData.getLocation()));
	}

	public void view(SBPlayer sbPlayer) {
		sbPlayer.setScriptLine(null);
		sbPlayer.setClickAction(null);
		if (!scriptData.checkPath() || scripts.isEmpty()) {
			Utils.sendMessage(sbPlayer, SBConfig.getErrorScriptFileCheckMessage());
			return;
		}
		Utils.sendMessage(sbPlayer, "Author: " + getAuthors());
		Utils.sendMessage(sbPlayer, "LastEdit: " + scriptData.getLastEdit());
		for (String script : scripts) {
			Utils.sendMessage(sbPlayer, "- " + script);
		}
		Utils.sendMessage(SBConfig.getConsoleScriptViewMessage(sbPlayer.getName(), scriptType, scriptData.getLocation()));
	}

	public boolean copy(SBPlayer sbPlayer) {
		return new Clipboard(scriptData).copy(sbPlayer);
	}

	public boolean weRemove(Location location) {
		setLocation(location);
		if (!scriptData.checkPath()) {
			return false;
		}
		scriptData.remove();
		mapManager.removeCoords(scriptType, location);
		return true;
	}

	private String getAuthors() {
		StrBuilder builder = new StrBuilder();
		List<String> authors = scriptData.getAuthors(true);
		if (authors.size() > 1) {
			builder.append("[");
			for (int i = 0; i < authors.size(); i++) {
				builder.append(authors.get(i));
				if (i == (authors.size() - 1)) {
					builder.append("]");
				} else {
					builder.append(", ");
				}
			}
		} else {
			builder.append(authors.size() == 1 ? authors.get(0) : "null");
		}
		return builder.toString();
	}
}