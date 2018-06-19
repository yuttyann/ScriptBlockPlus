package com.github.yuttyann.scriptblockplus.script;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.bukkit.Location;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public final class Clipboard {

	private final ScriptData scriptData;
	private final String author;
	private final List<String> scripts;
	private final ScriptType scriptType;

	private SBPlayer sbPlayer;
	private String lastEdit;

	Clipboard(ScriptData scriptData) {
		Objects.requireNonNull(scriptData);
		this.scriptData = scriptData.clone();
		this.author = this.scriptData.getAuthor();
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
		sbPlayer.setScriptLine(null);
		sbPlayer.setClickAction(null);
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

	public boolean paste(Location location, boolean overwrite) {
		if (sbPlayer == null) {
			return false;
		}
		sbPlayer.setClipboard(null);
		sbPlayer.setScriptLine(null);
		sbPlayer.setClickAction(null);
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
		scriptData.setScripts(new ArrayList<>(scripts));
		scriptData.save();
		ScriptBlock.getInstance().getMapManager().addCoords(scriptType, location);
		Utils.sendMessage(sbPlayer, SBConfig.getScriptPasteMessage(scriptType));
		Utils.sendMessage(SBConfig.getConsoleScriptPasteMessage(sbPlayer.getName(), scriptType, location));
		return true;
	}

	public boolean wePaste(Location location, boolean overwrite, boolean updateTime) {
		if (scriptData == null || sbPlayer == null) {
			return false;
		}
		scriptData.setLocation(location);
		if (scriptData.checkPath() && !overwrite) {
			return false;
		}
		if (lastEdit == null || updateTime) {
			lastEdit = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
		}
		scriptData.setAuthor(author);
		scriptData.addAuthor(sbPlayer.getOfflinePlayer());
		scriptData.setLastEdit(lastEdit);
		scriptData.setScripts(new ArrayList<>(scripts));
		ScriptBlock.getInstance().getMapManager().addCoords(scriptType, location);
		return true;
	}
}