package com.github.yuttyann.scriptblockplus.script;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.bukkit.Location;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public final class Clipboard {

	private ScriptData scriptData;
	private String author;
	private String lastEdit;
	private List<String> scripts;
	private ScriptType scriptType;

	Clipboard(ScriptData scriptData) {
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
		sbPlayer.setScriptLine(null);
		sbPlayer.setClickAction(null);
		if (scriptData == null || !scriptData.checkPath()) {
			Utils.sendMessage(sbPlayer, SBConfig.getErrorScriptFileCheckMessage());
			return false;
		}
		sbPlayer.setClipboard(this);
		Utils.sendMessage(sbPlayer, SBConfig.getScriptCopyMessage(scriptType));
		Utils.sendMessage(SBConfig.getConsoleScriptCopyMessage(sbPlayer.getName(), scriptType, scriptData.getLocation()));
		return true;
	}

	public boolean paste(SBPlayer sbPlayer, Location location, boolean overwrite) {
		sbPlayer.setScriptLine(null);
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
		sbPlayer.setClipboard(null);
		ScriptBlock.getInstance().getMapManager().addCoords(clipboard.scriptType, location);
		Utils.sendMessage(sbPlayer, SBConfig.getScriptPasteMessage(clipboard.scriptType));
		Utils.sendMessage(SBConfig.getConsoleScriptPasteMessage(sbPlayer.getName(), clipboard.scriptType, location));
		return true;
	}

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
		ScriptBlock.getInstance().getMapManager().addCoords(clipboard.scriptType, location);
		return true;
	}
}