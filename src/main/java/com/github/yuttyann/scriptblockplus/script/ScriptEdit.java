package com.github.yuttyann.scriptblockplus.script;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.manager.MapManager;
import com.github.yuttyann.scriptblockplus.player.PlayerCountInfo;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.apache.commons.lang.text.StrBuilder;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ScriptEdit {

	private ScriptType scriptType;
	private ScriptData scriptData;
	private MapManager mapManager;

	public ScriptEdit(@NotNull ScriptType scriptType) {
		this.scriptType = scriptType;
		this.scriptData = new ScriptData(scriptType);
		this.mapManager = ScriptBlock.getInstance().getMapManager();
	}

	public void save() {
		scriptData.save();
	}

	public boolean checkPath() {
		return scriptData.checkPath();
	}

	@NotNull
	public ScriptType getScriptType() {
		return scriptType;
	}

	@NotNull
	public String getAuthors() {
		List<String> authors = scriptData.getAuthors(true);
		if (authors.size() < 2) {
			return authors.size() == 1 ? authors.get(0) : "null";
		}
		StrBuilder builder = new StrBuilder().append("[");
		for (int i = 0; i < authors.size(); i++) {
			builder.append(authors.get(i)).append(i == (authors.size() - 1) ? "]" : ", ");
		}
		return builder.toString();
	}

	public void create(@NotNull SBPlayer sbPlayer, @NotNull Location location, @NotNull String script) {
		sbPlayer.setScriptLine(null);
		sbPlayer.setActionType(null);
		scriptData.setLocation(location);
		scriptData.setAuthor(sbPlayer.getUniqueId());
		scriptData.setLastEdit();
		scriptData.setScripts(Collections.singletonList(script));
		scriptData.save();
		mapManager.addCoords(location, scriptType);
		SBConfig.SCRIPT_CREATE.replace(scriptType.getType()).send(sbPlayer);
		SBConfig.CONSOLE_SCRIPT_CREATE.replace(args(sbPlayer.getName(), scriptType, location)).console();
	}

	public void add(@NotNull SBPlayer sbPlayer, @NotNull Location location, @NotNull String script) {
		sbPlayer.setScriptLine(null);
		sbPlayer.setActionType(null);
		scriptData.setLocation(location);
		if (!scriptData.checkPath()) {
			SBConfig.ERROR_SCRIPT_FILE_CHECK.send(sbPlayer);
			return;
		}
		scriptData.addAuthor(sbPlayer.getUniqueId());
		scriptData.setLastEdit();
		scriptData.addScript(script);
		scriptData.save();
		mapManager.removeTimes(location, scriptType);
		SBConfig.SCRIPT_ADD.replace(scriptType.getType()).send(sbPlayer);
		SBConfig.CONSOLE_SCRIPT_ADD.replace(args(sbPlayer.getName(), scriptType, location)).console();
	}

	public void remove(@NotNull SBPlayer sbPlayer, @NotNull Location location) {
		sbPlayer.setScriptLine(null);
		sbPlayer.setActionType(null);
		scriptData.setLocation(location);
		if (!scriptData.checkPath()) {
			SBConfig.ERROR_SCRIPT_FILE_CHECK.send(sbPlayer);
			return;
		}
		scriptData.remove();
		scriptData.save();
		mapManager.removeCoords(location, scriptType);
		SBConfig.SCRIPT_REMOVE.replace(scriptType.getType()).send(sbPlayer);
		SBConfig.CONSOLE_SCRIPT_REMOVE.replace(args(sbPlayer.getName(), scriptType, location)).console();
	}

	public boolean lightRemove(@NotNull Location location) {
		scriptData.setLocation(location);
		if (!scriptData.checkPath()) {
			return false;
		}
		scriptData.remove();
		mapManager.removeCoords(location, scriptType);
		return true;
	}

	public void view(@NotNull SBPlayer sbPlayer, @NotNull Location location) {
		sbPlayer.setScriptLine(null);
		sbPlayer.setActionType(null);
		scriptData.setLocation(location);
		if (!scriptData.checkPath()) {
			SBConfig.ERROR_SCRIPT_FILE_CHECK.send(sbPlayer);
			return;
		}
		PlayerCountInfo info = sbPlayer.getPlayerCount().getInfo(location, getScriptType());
		sbPlayer.sendMessage("Author: " + getAuthors());
		sbPlayer.sendMessage("LastEdit: " + scriptData.getLastEdit());
		sbPlayer.sendMessage("Execute: " + info.getAmount());
		for (String script : scriptData.getScripts()) {
			sbPlayer.sendMessage("- " + script);
		}
		SBConfig.CONSOLE_SCRIPT_VIEW.replace(args(sbPlayer.getName(), scriptType, location)).console();
	}

	@NotNull
	public SBClipboard clipboard(@NotNull SBPlayer sbPlayer, @NotNull Location location) {
		scriptData.setLocation(location);
		return new Clipboard(sbPlayer, scriptData);
	}

	@NotNull
	static Object[] args(@NotNull String name, @NotNull ScriptType scriptType, @NotNull Location location) {
		return new Object[] { name, scriptType.getType(), location.getWorld().getName(), BlockCoords.getCoords(location) };
	}

	private static class Clipboard implements SBClipboard {

		private final ScriptData scriptData;
		private final MapManager mapManager;

		private final int amount;
		private final String author;
		private final List<String> scripts;
		private final ScriptType scriptType;

		private SBPlayer sbPlayer;

		private Clipboard(@NotNull SBPlayer sbPlayer, @NotNull ScriptData scriptData) {
			this.sbPlayer = sbPlayer;
			this.scriptData = scriptData.clone();
			this.mapManager = ScriptBlock.getInstance().getMapManager();
			this.amount = this.scriptData.getAmount();
			this.author = this.scriptData.getAuthor();
			this.scripts = new ArrayList<>(this.scriptData.getScripts());
			this.scriptType = this.scriptData.getScriptType();
		}

		@NotNull
		@Override
		public ScriptType getScriptType() {
			return scriptType;
		}

		@Override
		public void save() {
			scriptData.save();
		}

		@Override
		public boolean copy() {
			sbPlayer.setScriptLine(null);
			sbPlayer.setActionType(null);
			if (!scriptData.checkPath()) {
				SBConfig.ERROR_SCRIPT_FILE_CHECK.send(sbPlayer);
				return false;
			}
			sbPlayer.setClipboard(this);
			SBConfig.SCRIPT_COPY.replace(scriptType.getType()).send(sbPlayer);
			SBConfig.CONSOLE_SCRIPT_REMOVE.replace(args(sbPlayer.getName(), scriptType, scriptData.getLocation())).console();
			return true;
		}

		@Override
		public boolean paste(@NotNull Location location, boolean overwrite) {
			sbPlayer.setClipboard(null);
			sbPlayer.setScriptLine(null);
			sbPlayer.setActionType(null);
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
			scriptData.setScripts(scripts);
			scriptData.save();
			mapManager.addCoords(location, scriptType);
			SBConfig.SCRIPT_PASTE.replace(scriptType.getType()).send(sbPlayer);
			SBConfig.CONSOLE_SCRIPT_PASTE.replace(args(sbPlayer.getName(), scriptType, scriptData.getLocation())).console();
			return true;
		}

		@Override
		public boolean lightPaste(@NotNull Location location, boolean overwrite) {
			scriptData.setLocation(location);
			if (scriptData.checkPath() && !overwrite) {
				return false;
			}
			scriptData.setAuthor(author);
			scriptData.addAuthor(sbPlayer.getUniqueId());
			if (amount > 0) {
				scriptData.setAmount(amount);
			}
			scriptData.setLastEdit(Utils.getFormatTime("yyyy/MM/dd HH:mm:ss"));
			scriptData.setScripts(scripts);
			mapManager.addCoords(location, scriptType);
			return true;
		}
	}
}