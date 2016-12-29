package com.github.yuttyann.scriptblockplus.manager;

import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.Messages;
import com.github.yuttyann.scriptblockplus.file.Yaml;
import com.github.yuttyann.scriptblockplus.manager.MetadataManager.Edit;
import com.github.yuttyann.scriptblockplus.manager.OptionManager.ScriptType;
import com.github.yuttyann.scriptblockplus.util.BlockLocation;
import com.github.yuttyann.scriptblockplus.util.Utils;

public class EditManager {

	private ScriptType scriptType;
	private Yaml scripts;
	private String scriptPath;
	private BlockLocation location;

	public EditManager(Yaml scripts, BlockLocation location) {
		this.scripts = scripts;
		this.location = location;
	}

	public ScriptType getScriptType() {
		if (scriptType == null) {
			if (scripts.getFile().getName().startsWith("interact")) {
				scriptType = ScriptType.INTERACT;
			} else {
				scriptType = ScriptType.WALK;
			}
		}
		return scriptType;
	}

	public String getScriptPath() {
		if (scriptPath == null) {
			scriptPath = location.getWorld().getName() + "." + location.getCoords(false);
		}
		return scriptPath;
	}

	public void scriptCopy(Player player) {
		MetadataManager.removeAllMetadata(player);
		if (!scripts.getFile().exists() || !scripts.contains(getScriptPath() + ".Author")) {
			Utils.sendPluginMessage(player, Messages.getErrorScriptFileCheckMessage());
			return;
		}
		Edit.setMetadata(player, getScriptType(), this);
		Utils.sendPluginMessage(player, Messages.getScriptCopyMessage(scriptType));
		if (Files.getConfig().isBoolean("ConsoleLog"))
			Utils.sendPluginMessage(Messages.getConsoleScriptCopyMessage(player, scriptType, location.getWorld(), location.getCoords(false)));
	}

	public void scriptPaste(Player player, BlockLocation location) {
		MetadataManager.removeAllMetadata(player);
		String coords = location.getCoords(false);
		String scriptPath = location.getWorld().getName() + "." + coords;
		scripts.set(scriptPath + ".Author", player.getUniqueId().toString());
		scripts.set(scriptPath + ".LastEdit", Utils.getTime("yyyy/MM/dd HH:mm:ss"));
		scripts.set(scriptPath + ".Scripts", scripts.getStringList(getScriptPath() + ".Scripts"));
		scripts.save();

		String fullcoords = location.getCoords(true);
		switch (scriptType) {
		case INTERACT:
			if (!MapManager.getInteractCoords().contains(fullcoords))
				MapManager.getInteractCoords().add(fullcoords);
			break;
		case WALK:
			if (!MapManager.getWalkCoords().contains(fullcoords))
				MapManager.getWalkCoords().add(fullcoords);
			break;
		}

		Utils.sendPluginMessage(player, Messages.getScriptPasteMessage(scriptType));
		if (Files.getConfig().isBoolean("ConsoleLog"))
			Utils.sendPluginMessage(Messages.getConsoleScriptPasteMessage(player, scriptType, location.getWorld(), coords));
	}
}
