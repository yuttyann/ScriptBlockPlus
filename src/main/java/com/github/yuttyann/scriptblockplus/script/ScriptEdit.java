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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class ScriptEdit {

	private ScriptType scriptType;
	private ScriptData scriptData;
	private MapManager mapManager;
	private List<String> scripts;

	public ScriptEdit(@NotNull ScriptType scriptType) {
		this.scriptType = scriptType;
		this.scriptData = new ScriptData(null, scriptType);
		this.mapManager = ScriptBlock.getInstance().getMapManager();
	}

	@Nullable
	public Location setLocation(@Nullable Location location) {
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

	@NotNull
	public ScriptType getScriptType() {
		return scriptType;
	}

	public void create(@NotNull SBPlayer sbPlayer, @Nullable Location location, @NotNull String script) {
		sbPlayer.setScriptLine(null);
		sbPlayer.setActionType(null);
		location = location == null ? scriptData.getLocation() : setLocation(location);
		scriptData.setAuthor(sbPlayer.getUniqueId());
		scriptData.setLastEdit();
		scriptData.setScripts(Arrays.asList(script));
		scriptData.save();
		mapManager.addCoords(location, scriptType);
		SBConfig.SCRIPT_CREATE.replace(scriptType.getType()).send(sbPlayer, true);
		SBConfig.CONSOLE_SCRIPT_CREATE.replace(values(sbPlayer.getName(), scriptType, location)).console(true);
	}

	public void add(@NotNull SBPlayer sbPlayer, @Nullable Location location, @NotNull String script) {
		sbPlayer.setScriptLine(null);
		sbPlayer.setActionType(null);
		location = location == null ? scriptData.getLocation() : setLocation(location);
		if (!scriptData.checkPath()) {
			SBConfig.ERROR_SCRIPT_FILE_CHECK.send(sbPlayer, true);
			return;
		}
		scriptData.addAuthor(sbPlayer.getUniqueId());
		scriptData.setLastEdit();
		scriptData.addScript(script);
		scriptData.save();
		mapManager.removeTimes(location, scriptType);
		SBConfig.SCRIPT_ADD.replace(scriptType.getType()).send(sbPlayer, true);
		SBConfig.CONSOLE_SCRIPT_ADD.replace(values(sbPlayer.getName(), scriptType, location)).console(true);
	}

	public void remove(@NotNull SBPlayer sbPlayer, @Nullable Location location) {
		sbPlayer.setScriptLine(null);
		sbPlayer.setActionType(null);
		location = location == null ? scriptData.getLocation() : setLocation(location);
		if (!scriptData.checkPath()) {
			SBConfig.ERROR_SCRIPT_FILE_CHECK.send(sbPlayer, true);
			return;
		}
		scriptData.remove();
		scriptData.save();
		mapManager.removeCoords(location, scriptType);
		SBConfig.SCRIPT_REMOVE.replace(scriptType.getType()).send(sbPlayer, true);
		SBConfig.CONSOLE_SCRIPT_REMOVE.replace(values(sbPlayer.getName(), scriptType, location)).console(true);
	}

	public void view(@NotNull SBPlayer sbPlayer, @Nullable Location location) {
		sbPlayer.setScriptLine(null);
		sbPlayer.setActionType(null);
		location = location == null ? scriptData.getLocation() : setLocation(location);
		if (!scriptData.checkPath() || scripts.isEmpty()) {
			SBConfig.ERROR_SCRIPT_FILE_CHECK.send(sbPlayer, true);
			return;
		}
		PlayerCountInfo info = sbPlayer.getPlayerCount().getInfo(new BlockCoords(location), getScriptType());
		Utils.sendMessage(sbPlayer, "Author: " + getAuthors());
		Utils.sendMessage(sbPlayer, "LastEdit: " + scriptData.getLastEdit());
		Utils.sendMessage(sbPlayer, "Execute: " + info.getAmount());
		for (String script : scripts) {
			Utils.sendMessage(sbPlayer, "- " + script);
		}
		SBConfig.CONSOLE_SCRIPT_VIEW.replace(values(sbPlayer.getName(), scriptType, location)).console(true);
	}

	@NotNull
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

	public boolean copy(@NotNull SBPlayer sbPlayer, @NotNull Location location) {
		setLocation(location);
		return new Clipboard(scriptData).copy(sbPlayer);
	}

	public boolean lightRemove(@NotNull Location location) {
		setLocation(location);
		if (!scriptData.checkPath()) {
			return false;
		}
		scriptData.remove();
		mapManager.removeCoords(location, scriptType);
		return true;
	}

	@NotNull
	static Object[] values(@NotNull String name, @NotNull ScriptType scriptType, @NotNull Location location) {
		return new Object[] { name, scriptType.getType(), location.getWorld().getName(), BlockCoords.getCoords(location) };
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

		private Clipboard(@NotNull ScriptData scriptData) {
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

		@NotNull
		public Location getLocation() {
			return scriptData.getLocation();
		}

		@NotNull
		@Override
		public ScriptType getScriptType() {
			return scriptType;
		}

		@Override
		public boolean copy(@NotNull SBPlayer sbPlayer) {
			sbPlayer.setScriptLine(null);
			sbPlayer.setActionType(null);
			if (this.sbPlayer != null) {
				return false;
			}
			if (scriptData == null || !scriptData.checkPath()) {
				SBConfig.ERROR_SCRIPT_FILE_CHECK.send(sbPlayer, true);
				return false;
			}
			this.sbPlayer = sbPlayer;
			sbPlayer.setClipboard(this);
			SBConfig.SCRIPT_COPY.replace(scriptType.getType()).send(sbPlayer, true);
			SBConfig.CONSOLE_SCRIPT_REMOVE.replace(values(sbPlayer.getName(), scriptType, scriptData.getLocation())).console(true);
			return true;
		}

		@Override
		public boolean paste(@NotNull Location location, boolean overwrite) {
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
			ScriptBlock.getInstance().getMapManager().addCoords(location, scriptType);
			SBConfig.SCRIPT_PASTE.replace(scriptType.getType()).send(sbPlayer, true);
			SBConfig.CONSOLE_SCRIPT_PASTE.replace(values(sbPlayer.getName(), scriptType, scriptData.getLocation())).console(true);
			return true;
		}

		@Override
		public boolean lightPaste(@NotNull Location location, boolean overwrite, boolean updateTime) {
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
			ScriptBlock.getInstance().getMapManager().addCoords(location, scriptType);
			return true;
		}
	}
}