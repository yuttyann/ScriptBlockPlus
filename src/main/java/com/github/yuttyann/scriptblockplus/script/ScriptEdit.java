package com.github.yuttyann.scriptblockplus.script;

import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.json.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.file.json.PlayerCountJson;
import com.github.yuttyann.scriptblockplus.file.json.element.PlayerCount;
import com.github.yuttyann.scriptblockplus.file.json.element.ScriptParam;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.option.time.TimerOption;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ScriptBlockPlus ScriptEdit クラス
 * @author yuttyann44581
 */
public final class ScriptEdit {

	private final ScriptType scriptType;
	private final BlockScriptJson blockScriptJson;

	public ScriptEdit(@NotNull ScriptType scriptType) {
		this.scriptType = scriptType;
		this.blockScriptJson = new BlockScriptJson(scriptType);
	}

	@NotNull
	public ScriptType getScriptType() {
		return scriptType;
	}

	public boolean exists() {
		return blockScriptJson.exists();
	}

	public void save() {
		blockScriptJson.saveFile();
	}

	@NotNull
	public String getAuthors(@NotNull ScriptParam scriptParam) {
		Set<UUID> author = scriptParam.getAuthor();
		return author.stream().map(Utils::getName).collect(Collectors.joining(", "));
	}

	public void create(@NotNull SBPlayer sbPlayer, @NotNull Location location) {
		try {
			Optional<String> scriptLine = sbPlayer.getScriptLine();
			scriptLine.ifPresent(s -> create(sbPlayer.getPlayer(), location, s));
		} finally {
			sbPlayer.setScriptLine(null);
			sbPlayer.setActionType(null);
		}
	}

	public void create(@NotNull Player player, @NotNull Location location, @NotNull String script) {
		TimerOption.removeAll(location, scriptType);
		PlayerCountJson.clear(location, scriptType);
		ScriptParam scriptParam = blockScriptJson.load().get(location);
		scriptParam.getAuthor().add(player.getUniqueId());
		scriptParam.setScript(Collections.singletonList(script));
		scriptParam.setLastEdit(Utils.getFormatTime());
		blockScriptJson.saveFile();
		SBConfig.SCRIPT_CREATE.replace(scriptType).send(player);
		SBConfig.CONSOLE_SCRIPT_CREATE.replace(player.getName(), location, scriptType).console();
	}

	public void add(@NotNull SBPlayer sbPlayer, @NotNull Location location) {
		try {
			Optional<String> scriptLine = sbPlayer.getScriptLine();
			scriptLine.ifPresent(s -> add(sbPlayer.getPlayer(), location, s));
		} finally {
			sbPlayer.setScriptLine(null);
			sbPlayer.setActionType(null);
		}
	}

	public void add(@NotNull Player player, @NotNull Location location, @NotNull String script) {
		if (!BlockScriptJson.has(location, blockScriptJson)) {
			SBConfig.ERROR_SCRIPT_FILE_CHECK.send(player);
			return;
		}
		TimerOption.removeAll(location, scriptType);
		ScriptParam scriptParam = blockScriptJson.load().get(location);
		scriptParam.getAuthor().add(player.getUniqueId());
		scriptParam.getScript().add(script);
		scriptParam.setLastEdit(Utils.getFormatTime());
		blockScriptJson.saveFile();
		SBConfig.SCRIPT_ADD.replace(scriptType).send(player);
		SBConfig.CONSOLE_SCRIPT_ADD.replace(player.getName(), location, scriptType).console();
	}

	public void remove(@NotNull SBPlayer sbPlayer, @NotNull Location location) {
		try {
			remove(sbPlayer.getPlayer(), location);
		} finally {
			sbPlayer.setScriptLine(null);
			sbPlayer.setActionType(null);
		}
	}

	public void remove(@NotNull Player player, @NotNull Location location) {
		if (!BlockScriptJson.has(location, blockScriptJson)) {
			SBConfig.ERROR_SCRIPT_FILE_CHECK.send(player);
			return;
		}
		TimerOption.removeAll(location, scriptType);
		PlayerCountJson.clear(location, scriptType);
		blockScriptJson.load().remove(location);
		blockScriptJson.saveFile();
		SBConfig.SCRIPT_REMOVE.replace(scriptType).send(player);
		SBConfig.CONSOLE_SCRIPT_REMOVE.replace(player.getName(), location, scriptType).console();
	}

	public boolean lightRemove(@NotNull Location location) {
		if (!BlockScriptJson.has(location, blockScriptJson)) {
			return false;
		}
		PlayerCountJson.clear(location, scriptType);
		TimerOption.removeAll(location, scriptType);
		blockScriptJson.load().remove(location);
		return true;
	}

	public void view(@NotNull SBPlayer sbPlayer, @NotNull Location location) {
		try {
			view(sbPlayer.getPlayer(), location);
		} finally {
			sbPlayer.setScriptLine(null);
			sbPlayer.setActionType(null);
		}
	}

	public void view(@NotNull Player player, @NotNull Location location) {
		if (!BlockScriptJson.has(location, blockScriptJson)) {
			SBConfig.ERROR_SCRIPT_FILE_CHECK.send(player);
			return;
		}
		ScriptParam scriptParam = blockScriptJson.load().get(location);
		PlayerCount playerCount = new PlayerCountJson(player.getUniqueId()).load(location, scriptType);
		player.sendMessage("Author: " + getAuthors(scriptParam));
		player.sendMessage("LastEdit: " + scriptParam.getLastEdit());
		player.sendMessage("Execute: " + playerCount.getAmount());
		scriptParam.getScript().forEach(s -> player.sendMessage("- " + s));
		SBConfig.CONSOLE_SCRIPT_VIEW.replace(player.getName(), location, scriptType).console();
	}

	@NotNull
	public SBClipboard clipboard(@NotNull SBPlayer sbPlayer, @NotNull Location location) {
		return new Clipboard(sbPlayer, location, scriptType);
	}

	private static class Clipboard implements SBClipboard {

		private final SBPlayer sbPlayer;
		private final Location location;
		private final ScriptType scriptType;
		private final BlockScriptJson blockScriptJson;

		private final Set<UUID> author;
		private final List<String> script;
		private final int amount;

		private Clipboard(@NotNull SBPlayer sbPlayer, @NotNull Location location, @NotNull ScriptType scriptType) {
			this.sbPlayer = sbPlayer;
			this.location = location;
			this.scriptType = scriptType;
			this.blockScriptJson = new BlockScriptJson(scriptType);

			ScriptParam scriptParam = blockScriptJson.load().get(location);
			this.author = scriptParam.getAuthor();
			this.script = scriptParam.getScript();
			this.amount = scriptParam.getAmount();
		}

		@Override
		@NotNull
		public Location getLocation() {
			return location;
		}

		@Override
		@NotNull
		public ScriptType getScriptType() {
			return scriptType;
		}

		@Override
		public void save() {
			blockScriptJson.saveFile();
		}

		@Override
		public boolean copy() {
			try {
				if (!BlockScriptJson.has(location, blockScriptJson)) {
					SBConfig.ERROR_SCRIPT_FILE_CHECK.send(sbPlayer);
					return false;
				}
				sbPlayer.setClipboard(this);
				SBConfig.SCRIPT_COPY.replace(scriptType).send(sbPlayer);
				SBConfig.CONSOLE_SCRIPT_COPY.replace(sbPlayer.getName(), location, scriptType).console();
			} finally {
				sbPlayer.setScriptLine(null);
				sbPlayer.setActionType(null);
			}
			return true;
		}

		@Override
		public boolean paste(@NotNull Location location, boolean overwrite) {
			try {
				if (BlockScriptJson.has(location, blockScriptJson) && !overwrite) {
					return false;
				}
				PlayerCountJson.clear(location, scriptType);
				ScriptParam scriptParam = blockScriptJson.load().get(location);
				scriptParam.setAuthor(author);
				scriptParam.getAuthor().add(sbPlayer.getUniqueId());
				scriptParam.setScript(script);
				scriptParam.setLastEdit(Utils.getFormatTime());
				scriptParam.setAmount(amount);
				blockScriptJson.saveFile();
				SBConfig.SCRIPT_PASTE.replace(scriptType).send(sbPlayer);
				SBConfig.CONSOLE_SCRIPT_PASTE.replace(sbPlayer.getName(), location, scriptType).console();
			} finally {
				sbPlayer.setClipboard(null);
				sbPlayer.setScriptLine(null);
				sbPlayer.setActionType(null);
			}
			return true;
		}

		@Override
		public boolean lightPaste(@NotNull Location location, boolean overwrite) {
			if (BlockScriptJson.has(location, blockScriptJson) && !overwrite) {
				return false;
			}
			PlayerCountJson.clear(location, scriptType);
			ScriptParam scriptParam = blockScriptJson.load().get(location);
			scriptParam.setAuthor(author);
			scriptParam.getAuthor().add(sbPlayer.getUniqueId());
			scriptParam.setScript(script);
			scriptParam.setLastEdit(Utils.getFormatTime());
			scriptParam.setAmount(amount);
			return true;
		}
	}
}