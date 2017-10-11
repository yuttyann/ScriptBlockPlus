package com.github.yuttyann.scriptblockplus.script;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.manager.MapManager;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public final class ScriptEdit {

	public static class Clipboard {

		private final ScriptData scriptData;
		private String author;
		private String lastEdit;
		private List<String> scripts;
		private ScriptType scriptType;

		private Clipboard(ScriptData scriptData) {
			Objects.requireNonNull(scriptData);
			this.scriptData = scriptData.clone();
			this.author = this.scriptData.getAuthor();
			this.lastEdit = this.scriptData.getLastEdit();
			this.scripts = this.scriptData.getScripts();
			this.scriptType = this.scriptData.getScriptType();
		}

		public ScriptType getScriptType() {
			return scriptType;
		}

		public void save() {
			scriptData.save();
		}

		public boolean copy(SBPlayer sbPlayer) {
			sbPlayer.setScript(null);
			sbPlayer.setClickAction(null);
			if (scriptData == null || !scriptData.checkPath()) {
				Utils.sendMessage(sbPlayer, SBConfig.getErrorScriptFileCheckMessage());
				return false;
			}
			sbPlayer.setClipboard(this);
			Utils.sendMessage(sbPlayer, SBConfig.getScriptCopyMessage(scriptType));
			Utils.sendMessage(SBConfig.getConsoleScriptCopyMessage(sbPlayer.getPlayer(), scriptType, scriptData.getLocation()));
			return true;
		}

		public boolean paste(SBPlayer sbPlayer, Location location, boolean overwrite, boolean clear) {
			sbPlayer.setScript(null);
			sbPlayer.setClickAction(null);
			if (scriptData == null || !sbPlayer.hasClipboard()) {
				return false;
			}
			scriptData.setLocation(location);
			if (scriptData.checkPath() && !overwrite) {
				return false;
			}
			Clipboard clipboard = sbPlayer.getClipboard();
			scriptData.setAuthor(clipboard.author);
			scriptData.setLastEdit(clipboard.lastEdit);
			scriptData.setScripts(new ArrayList<String>(clipboard.scripts));
			scriptData.save();
			ScriptBlock.getInstance().getMapManager().addLocation(clipboard.scriptType, location);
			Utils.sendMessage(sbPlayer, SBConfig.getScriptPasteMessage(clipboard.scriptType));
			Utils.sendMessage(SBConfig.getConsoleScriptPasteMessage(sbPlayer.getPlayer(), clipboard.scriptType, location));
			if (clear) {
				sbPlayer.setClipboard(null);
			}
			return true;
		}

		//WorldEdit用に軽量化
		public boolean wePaste(SBPlayer sbPlayer, Location location, boolean overwrite) {
			if (scriptData == null || !sbPlayer.hasClipboard()) {
				return false;
			}
			scriptData.setLocation(location);
			if (scriptData.checkPath() && !overwrite) {
				return false;
			}
			Clipboard clipboard = sbPlayer.getClipboard();
			scriptData.setAuthor(clipboard.author);
			scriptData.setLastEdit(clipboard.lastEdit);
			scriptData.setScripts(new ArrayList<String>(clipboard.scripts));
			ScriptBlock.getInstance().getMapManager().addLocation(clipboard.scriptType, location);
			return true;
		}
	}

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
		SBPlayer sbPlayer = SBPlayer.get(player);
		sbPlayer.setScript(null);
		sbPlayer.setClickAction(null);
		scriptData.setAuthor(player);
		scriptData.setLastEdit();
		scriptData.setScripts(Arrays.asList(script));
		scriptData.save();
		mapManager.addLocation(scriptType, scriptData.getLocation());
		Utils.sendMessage(player, SBConfig.getScriptCreateMessage(scriptType));
		Utils.sendMessage(SBConfig.getConsoleScriptCreateMessage(player, scriptType, scriptData.getLocation()));
	}

	public void add(Player player, String script) {
		SBPlayer sbPlayer = SBPlayer.get(player);
		sbPlayer.setScript(null);
		sbPlayer.setClickAction(null);
		if (!scriptData.checkPath()) {
			Utils.sendMessage(player, SBConfig.getErrorScriptFileCheckMessage());
			return;
		}
		scriptData.addAuthor(player);
		scriptData.setLastEdit();
		scriptData.addScript(script);
		scriptData.save();
		mapManager.removeTimes(scriptType, scriptData.getLocation());
		Utils.sendMessage(player, SBConfig.getScriptAddMessage(scriptType));
		Utils.sendMessage(SBConfig.getConsoleScriptAddMessage(player, scriptType, scriptData.getLocation()));
	}

	public void remove(Player player) {
		SBPlayer sbPlayer = SBPlayer.get(player);
		sbPlayer.setScript(null);
		sbPlayer.setClickAction(null);
		if (!scriptData.checkPath()) {
			Utils.sendMessage(player, SBConfig.getErrorScriptFileCheckMessage());
			return;
		}
		scriptData.remove();
		scriptData.save();
		mapManager.removeLocation(scriptType, scriptData.getLocation());
		Utils.sendMessage(player, SBConfig.getScriptRemoveMessage(scriptType));
		Utils.sendMessage(SBConfig.getConsoleScriptRemoveMessage(player, scriptType, scriptData.getLocation()));
	}

	public void view(Player player) {
		SBPlayer sbPlayer = SBPlayer.get(player);
		sbPlayer.setScript(null);
		sbPlayer.setClickAction(null);
		if (!scriptData.checkPath() || scripts.isEmpty()) {
			Utils.sendMessage(player, SBConfig.getErrorScriptFileCheckMessage());
			return;
		}
		Utils.sendMessage(player, "Author: " + getAuthors());
		Utils.sendMessage(player, "LastEdit: " + scriptData.getLastEdit());
		for (String script : scripts) {
			Utils.sendMessage(player, "- " + script);
		}
		Utils.sendMessage(SBConfig.getConsoleScriptViewMessage(player, scriptType, scriptData.getLocation()));
	}

	public boolean copy(SBPlayer sbPlayer) {
		return new Clipboard(scriptData).copy(sbPlayer);
	}

	//WorldEdit用に軽量化
	public boolean weRemove(Location location) {
		setLocation(location);
		if (!scriptData.checkPath()) {
			return false;
		}
		scriptData.remove();
		mapManager.removeLocation(scriptType, location);
		return true;
	}

	private String getAuthors() {
		StringBuilder builder = new StringBuilder();
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
			try {
				builder.append(authors.get(0));
			} catch (NullPointerException e) {
				builder.append("null");
			}
		}
		return builder.toString();
	}
}