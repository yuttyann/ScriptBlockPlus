package com.github.yuttyann.scriptblockplus.script;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.manager.MapManager;
import com.github.yuttyann.scriptblockplus.player.PlayerCount;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.apache.commons.lang.text.StrBuilder;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * ScriptBlockPlus ScriptEdit クラス
 * @author yuttyann44581
 */
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

	public boolean hasPath() {
		return scriptData.hasPath();
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

	public void create(@NotNull SBPlayer sbPlayer, @NotNull Location location) {
		try {
			Player player = Objects.requireNonNull(sbPlayer.getPlayer());
			sbPlayer.getScriptLine().ifPresent(s -> create(player, location, s));
		} finally {
			sbPlayer.setScriptLine(null);
			sbPlayer.setActionType(null);
		}
	}

	public void create(@NotNull Player player, @NotNull Location location, @NotNull String script) {
		scriptData.setLocation(location);
		scriptData.setAuthor(player.getUniqueId());
		scriptData.setLastEdit();
		scriptData.setScripts(Collections.singletonList(script));
		scriptData.initCount(new BlockCoords(location));
		scriptData.save();
		mapManager.addCoords(location, scriptType);
		SBConfig.SCRIPT_CREATE.replace(scriptType).send(player);
		SBConfig.CONSOLE_SCRIPT_CREATE.replace(player.getName(), scriptType, location).console();
	}

	public void add(@NotNull SBPlayer sbPlayer, @NotNull Location location) {
		try {
			Player player = Objects.requireNonNull(sbPlayer.getPlayer());
			sbPlayer.getScriptLine().ifPresent(s -> add(player, location, s));
		} finally {
			sbPlayer.setScriptLine(null);
			sbPlayer.setActionType(null);
		}
	}

	public void add(@NotNull Player player, @NotNull Location location, @NotNull String script) {
		scriptData.setLocation(location);
		if (!scriptData.hasPath()) {
			SBConfig.ERROR_SCRIPT_FILE_CHECK.send(player);
			return;
		}
		scriptData.addAuthor(player.getUniqueId());
		scriptData.setLastEdit();
		scriptData.addScript(script);
		scriptData.save();
		mapManager.removeTimes(location, scriptType);
		SBConfig.SCRIPT_ADD.replace(scriptType).send(player);
		SBConfig.CONSOLE_SCRIPT_ADD.replace(player.getName(), scriptType, location).console();
	}

	public void remove(@NotNull SBPlayer sbPlayer, @NotNull Location location) {
		try {
			remove(Objects.requireNonNull(sbPlayer.getPlayer()), location);
		} finally {
			sbPlayer.setScriptLine(null);
			sbPlayer.setActionType(null);
		}
	}

	public void remove(@NotNull Player player, @NotNull Location location) {
		scriptData.setLocation(location);
		if (!scriptData.hasPath()) {
			SBConfig.ERROR_SCRIPT_FILE_CHECK.send(player);
			return;
		}
		scriptData.initCount(new BlockCoords(location));
		scriptData.remove();
		scriptData.save();
		mapManager.removeCoords(location, scriptType);
		SBConfig.SCRIPT_REMOVE.replace(scriptType).send(player);
		SBConfig.CONSOLE_SCRIPT_REMOVE.replace(player.getName(), scriptType, location).console();
	}

	public boolean lightRemove(@NotNull Location location) {
		scriptData.setLocation(location);
		if (!scriptData.hasPath()) {
			return false;
		}
		scriptData.initCount(new BlockCoords(location));
		scriptData.remove();
		mapManager.removeCoords(location, scriptType);
		return true;
	}

	public void view(@NotNull SBPlayer sbPlayer, @NotNull Location location) {
		try {
			view(Objects.requireNonNull(sbPlayer.getPlayer()), location);
		} finally {
			sbPlayer.setScriptLine(null);
			sbPlayer.setActionType(null);
		}
	}

	public void view(@NotNull Player player, @NotNull Location location) {
		scriptData.setLocation(location);
		if (!scriptData.hasPath()) {
			SBConfig.ERROR_SCRIPT_FILE_CHECK.send(player);
			return;
		}
		PlayerCount playerCount = SBPlayer.fromPlayer(player).getPlayerCount();
		player.sendMessage("Author: " + getAuthors());
		player.sendMessage("LastEdit: " + scriptData.getLastEdit());
		player.sendMessage("Execute: " + playerCount.getInfo(location, getScriptType()).getAmount());
		for (String script : scriptData.getScripts()) {
			player.sendMessage("- " + script);
		}
		SBConfig.CONSOLE_SCRIPT_VIEW.replace(player.getName(), scriptType, location).console();
	}

	@NotNull
	public SBClipboard clipboard(@NotNull SBPlayer sbPlayer, @NotNull Location location) {
		scriptData.setLocation(location);
		return new Clipboard(sbPlayer, scriptData);
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

		@Override
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
		public void save() {
			scriptData.save();
		}

		@Override
		public boolean copy() {
			try {
				if (!scriptData.hasPath()) {
					SBConfig.ERROR_SCRIPT_FILE_CHECK.send(sbPlayer);
					return false;
				}
				sbPlayer.setClipboard(this);
				SBConfig.SCRIPT_COPY.replace(scriptType).send(sbPlayer);
				SBConfig.CONSOLE_SCRIPT_COPY.replace(sbPlayer.getName(), scriptType, scriptData.getLocation()).console();
			} finally {
				sbPlayer.setScriptLine(null);
				sbPlayer.setActionType(null);
			}
			return true;
		}

		@Override
		public boolean paste(@NotNull Location location, boolean overwrite) {
			try {
				scriptData.setLocation(location);
				if (scriptData.hasPath() && !overwrite) {
					return false;
				}
				scriptData.initCount(new BlockCoords(location));
				scriptData.setAuthor(author);
				scriptData.addAuthor(sbPlayer.getOfflinePlayer());
				scriptData.setLastEdit(Utils.getFormatTime());
				if (amount > 0) {
					scriptData.setAmount(amount);
				}
				scriptData.setScripts(scripts);
				scriptData.save();
				mapManager.addCoords(location, scriptType);
				SBConfig.SCRIPT_PASTE.replace(scriptType).send(sbPlayer);
				SBConfig.CONSOLE_SCRIPT_PASTE.replace(sbPlayer.getName(), scriptType, location).console();
			} finally {
				sbPlayer.setClipboard(null);
				sbPlayer.setScriptLine(null);
				sbPlayer.setActionType(null);
			}
			return true;
		}

		@Override
		public boolean lightPaste(@NotNull Location location, boolean overwrite) {
			scriptData.setLocation(location);
			if (scriptData.hasPath() && !overwrite) {
				return false;
			}
			scriptData.initCount(new BlockCoords(location));
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