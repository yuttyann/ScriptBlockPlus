package com.github.yuttyann.scriptblockplus.script;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang.text.StrBuilder;
import org.bukkit.Location;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.manager.MapManager;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public final class ScriptEdit {

	private ScriptType scriptType;
	private ScriptData scriptData;
	private MapManager mapManager;
	private List<String> scripts;

	public ScriptEdit(ScriptType scriptType) {
		this.scriptType = scriptType;
		this.scriptData = new ScriptData(null, scriptType);
		this.mapManager = ScriptBlock.getInstance().getMapManager();
	}

	public Location setLocation(Location location) {
		Location oldLocation = scriptData.getLocation();
		if (oldLocation == null || !oldLocation.equals(location)) {
			scriptData.setLocation(location);
			scripts = scriptData.getScripts();
		}
		return scriptData.getLocation();
	}

	public void save() {
		scriptData.save();
	}

	public boolean checkPath() {
		return scriptData.checkPath();
	}

	public ScriptType getScriptType() {
		return scriptType;
	}

	public void create(SBPlayer sbPlayer, Location location, String script) {
		sbPlayer.setScriptLine(null);
		sbPlayer.setActionType(null);
		location = location == null ? scriptData.getLocation() : setLocation(location);
		scriptData.setAuthor(sbPlayer.getUniqueId());
		scriptData.setLastEdit();
		scriptData.setScripts(Arrays.asList(script));
		scriptData.save();
		mapManager.addCoords(scriptType, location);
		Utils.sendMessage(sbPlayer, SBConfig.getScriptCreateMessage(scriptType));
		Utils.sendMessage(SBConfig.getConsoleScriptCreateMessage(sbPlayer.getName(), scriptType, location));
	}

	public void add(SBPlayer sbPlayer, Location location, String script) {
		sbPlayer.setScriptLine(null);
		sbPlayer.setActionType(null);
		location = location == null ? scriptData.getLocation() : setLocation(location);
		if (!scriptData.checkPath()) {
			Utils.sendMessage(sbPlayer, SBConfig.getErrorScriptFileCheckMessage());
			return;
		}
		scriptData.addAuthor(sbPlayer.getUniqueId());
		scriptData.setLastEdit();
		scriptData.addScript(script);
		scriptData.save();
		mapManager.removeTimes(scriptType, location);
		Utils.sendMessage(sbPlayer, SBConfig.getScriptAddMessage(scriptType));
		Utils.sendMessage(SBConfig.getConsoleScriptAddMessage(sbPlayer.getName(), scriptType, location));
	}

	public void remove(SBPlayer sbPlayer, Location location) {
		sbPlayer.setScriptLine(null);
		sbPlayer.setActionType(null);
		location = location == null ? scriptData.getLocation() : setLocation(location);
		if (!scriptData.checkPath()) {
			Utils.sendMessage(sbPlayer, SBConfig.getErrorScriptFileCheckMessage());
			return;
		}
		scriptData.remove();
		scriptData.save();
		mapManager.removeCoords(scriptType, location);
		Utils.sendMessage(sbPlayer, SBConfig.getScriptRemoveMessage(scriptType));
		Utils.sendMessage(SBConfig.getConsoleScriptRemoveMessage(sbPlayer.getName(), scriptType, location));
	}

	public void view(SBPlayer sbPlayer, Location location) {
		sbPlayer.setScriptLine(null);
		sbPlayer.setActionType(null);
		location = location == null ? scriptData.getLocation() : setLocation(location);
		if (!scriptData.checkPath() || scripts.isEmpty()) {
			Utils.sendMessage(sbPlayer, SBConfig.getErrorScriptFileCheckMessage());
			return;
		}
		Utils.sendMessage(sbPlayer, "Author: " + getAuthors());
		Utils.sendMessage(sbPlayer, "LastEdit: " + scriptData.getLastEdit());
		for (String script : scripts) {
			Utils.sendMessage(sbPlayer, "- " + script);
		}
		Utils.sendMessage(SBConfig.getConsoleScriptViewMessage(sbPlayer.getName(), scriptType, location));
	}

	private String getAuthors() {
		StrBuilder builder = new StrBuilder();
		List<String> authors = scriptData.getAuthors(true);
		if (authors.size() <= 1) {
			builder.append(authors.size() == 1 ? authors.get(0) : "null");
		} else {
			builder.append("[");
			for (int i = 0; i < authors.size(); i++) {
				builder.append(authors.get(i)).append(i == (authors.size() - 1) ? "]" : ", ");
			}
		}
		return builder.toString();
	}

	public boolean copy(SBPlayer sbPlayer, Location location) {
		setLocation(location);
		return new Clipboard(scriptData).copy(sbPlayer);
	}

	public boolean lightRemove(Location location) {
		setLocation(location);
		if (!scriptData.checkPath()) {
			return false;
		}
		scriptData.remove();
		mapManager.removeCoords(scriptType, location);
		return true;
	}

	private class Clipboard implements SBClipboard {

		private final ScriptData scriptData;

		//Datas
		private final int amount;
		private final String author;
		private final List<String> scripts;
		private final ScriptType scriptType;

		private SBPlayer sbPlayer;
		private String lastEdit;

		private Clipboard(ScriptData scriptData) {
			Objects.requireNonNull(scriptData);
			this.scriptData = scriptData.clone();
			this.amount = this.scriptData.getAmount();
			this.author = this.scriptData.getAuthor();
			this.scripts = this.scriptData.getScripts();
			this.scriptType = this.scriptData.getScriptType();
		}

		@Override
		public void save() {
			scriptData.save();
		}

		@Override
		public ScriptType getScriptType() {
			return scriptType;
		}

		@Override
		public boolean copy(SBPlayer sbPlayer) {
			sbPlayer.setScriptLine(null);
			sbPlayer.setActionType(null);
			if (this.sbPlayer != null) {
				return false;
			}
			if (scriptData == null || !scriptData.checkPath()) {
				Utils.sendMessage(sbPlayer, SBConfig.getErrorScriptFileCheckMessage());
				return false;
			}
			this.sbPlayer = sbPlayer;
			sbPlayer.setClipboard(this);
			Utils.sendMessage(sbPlayer, SBConfig.getScriptCopyMessage(scriptType));
			Utils.sendMessage(SBConfig.getConsoleScriptCopyMessage(sbPlayer.getName(), scriptType, scriptData.getLocation()));
			return true;
		}

		@Override
		public boolean paste(Location location, boolean overwrite) {
			if (sbPlayer == null) {
				return false;
			}
			sbPlayer.setClipboard(null);
			sbPlayer.setScriptLine(null);
			sbPlayer.setActionType(null);
			if (scriptData == null) {
				return false;
			}
			scriptData.setLocation(location);
			if (scriptData.checkPath() && !overwrite) {
				return false;
			}
			scriptData.setAuthor(author);
			scriptData.addAuthor(sbPlayer.getOfflinePlayer());
			scriptData.setLastEdit(Utils.getFormatTime());
			if (amount > 0) {
				scriptData.setAmount(amount);
			}
			scriptData.setScripts(new ArrayList<>(scripts));
			scriptData.save();
			ScriptBlock.getInstance().getMapManager().addCoords(scriptType, location);
			Utils.sendMessage(sbPlayer, SBConfig.getScriptPasteMessage(scriptType));
			Utils.sendMessage(SBConfig.getConsoleScriptPasteMessage(sbPlayer.getName(), scriptType, location));
			return true;
		}

		@Override
		public boolean lightPaste(Location location, boolean overwrite, boolean updateTime) {
			if (scriptData == null || sbPlayer == null) {
				return false;
			}
			scriptData.setLocation(location);
			if (scriptData.checkPath() && !overwrite) {
				return false;
			}
			if (lastEdit == null || updateTime) {
				lastEdit = Utils.getFormatTime("yyyy/MM/dd HH:mm:ss");
			}
			scriptData.setAuthor(author);
			scriptData.addAuthor(sbPlayer.getOfflinePlayer());
			if (amount > 0) {
				scriptData.setAmount(amount);
			}
			scriptData.setLastEdit(lastEdit);
			scriptData.setScripts(new ArrayList<>(scripts));
			ScriptBlock.getInstance().getMapManager().addCoords(scriptType, location);
			return true;
		}
	}
}