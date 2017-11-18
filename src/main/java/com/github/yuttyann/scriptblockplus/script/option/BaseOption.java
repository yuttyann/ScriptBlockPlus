package com.github.yuttyann.scriptblockplus.script.option;

import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.manager.MapManager;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.ScriptData;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public abstract class BaseOption extends Option {

	private Plugin plugin;
	private SBPlayer sbPlayer;
	private String optionValue;
	private BlockCoords blockCoords;
	private MapManager mapManager;
	private List<String> scripts;
	private ScriptType scriptType;
	private ScriptRead scriptRead;
	private ScriptData scriptData;
	private int scriptIndex;

	public BaseOption(String name, String syntax) {
		super(name, syntax);
	}

	protected Plugin getPlugin() {
		return plugin;
	}

	protected Player getPlayer() {
		return sbPlayer.getPlayer();
	}

	protected SBPlayer getSBPlayer() {
		return sbPlayer;
	}

	protected UUID getUniqueId() {
		return sbPlayer.getUniqueId();
	}

	protected String getOptionValue() {
		return optionValue;
	}

	protected String getCoords() {
		return blockCoords.getCoords();
	}

	protected String getFullCoords() {
		return blockCoords.getFullCoords();
	}

	protected BlockCoords getBlockCoords() {
		return blockCoords;
	}

	protected MapManager getMapManager() {
		return mapManager;
	}

	protected List<String> getScripts() {
		return scripts;
	}

	protected ScriptType getScriptType() {
		return scriptType;
	}

	protected ScriptRead getScriptRead() {
		return scriptRead;
	}

	protected ScriptData getScriptData() {
		return scriptData;
	}

	protected int getScriptIndex() {
		return scriptIndex;
	}

	protected abstract boolean isValid() throws Exception;

	@Override
	@Deprecated
	public final boolean callOption(ScriptRead scriptRead) {
		this.scriptRead = scriptRead;
		this.plugin = scriptRead.getPlugin();
		this.sbPlayer = scriptRead.getSBPlayer();
		this.optionValue = scriptRead.getOptionValue();
		this.blockCoords = scriptRead.getBlockCoords();
		this.mapManager = scriptRead.getMapManager();
		this.scripts = scriptRead.getScripts();
		this.scriptType = scriptRead.getScriptType();
		this.scriptData = scriptRead.getScriptData();
		this.scriptIndex = scriptRead.getScriptIndex();
		try {
			return isValid();
		} catch (Exception e) {
			e.printStackTrace();
			Utils.sendMessage(sbPlayer, SBConfig.getOptionFailedToExecuteMessage(this, e));
		}
		return false;
	}

	protected final void commandExecute(Player player, String command, boolean isBypass) {
		if (!isBypass || player.isOp()) {
			Utils.dispatchCommand(player, command, blockCoords);
		} else {
			try {
				player.setOp(true);
				Utils.dispatchCommand(player, command, blockCoords);
			} finally {
				player.setOp(false);
			}
		}
	}
}