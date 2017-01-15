package com.github.yuttyann.scriptblockplus.manager;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.file.Messages;
import com.github.yuttyann.scriptblockplus.file.ScriptData;
import com.github.yuttyann.scriptblockplus.manager.MetadataManager.Edit;
import com.github.yuttyann.scriptblockplus.type.ScriptType;
import com.github.yuttyann.scriptblockplus.util.BlockLocation;
import com.github.yuttyann.scriptblockplus.util.Utils;

public class EditManager {

	private ScriptData scriptData;
	private BlockLocation location;
	private ScriptType scriptType;
	private List<String> scripts;

	public EditManager(BlockLocation location, ScriptType scriptType) {
		this.scriptData = new ScriptData(location, scriptType);
		this.location = location;
		this.scriptType = scriptType;
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

	public void scriptCopy(Player player) {
		MetadataManager.removeAllMetadata(player);
		if (!checkPath()) {
			Utils.sendPluginMessage(player, Messages.getErrorScriptFileCheckMessage());
			return;
		}
		scripts = scriptData.getScripts();
		Edit.removeAllMetadata(player);
		Edit.setMetadata(player, scriptType, this);
		Utils.sendPluginMessage(player, Messages.getScriptCopyMessage(scriptType));
		Utils.sendPluginMessage(Messages.getConsoleScriptCopyMessage(player, scriptType, location.getWorld(), location.getCoords()));
	}

	public void scriptPaste(Player player, BlockLocation location) {
		MetadataManager.removeAllMetadata(player);
		scriptData.setBlockLocation(location);
		scriptData.setAuthor(player);
		scriptData.setLastEdit();
		scriptData.setScripts(new ArrayList<String>(scripts));
		scriptData.save();
		System.out.println(scriptData.getLastEdit());
		MapManager.addCoords(location, scriptType);
		Utils.sendPluginMessage(player, Messages.getScriptPasteMessage(scriptType));
		Utils.sendPluginMessage(Messages.getConsoleScriptPasteMessage(player, scriptType, location.getWorld(), location.getCoords()));
	}

	//WorldEdit用に軽量化
	public void scriptWEPaste(Player player, BlockLocation location, boolean overwrite) {
		scriptData.setBlockLocation(location);
		if (!overwrite && scriptData.checkPath()) {
			return;
		}
		scriptData.setAuthor(player);
		scriptData.setLastEdit();
		scriptData.setScripts(new ArrayList<String>(scripts));
		MapManager.addCoords(location, scriptType);
	}
}
