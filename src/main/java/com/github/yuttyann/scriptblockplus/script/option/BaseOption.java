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

	public Plugin getPlugin() {
		return plugin;
	}

	public Player getPlayer() {
		return sbPlayer.getPlayer();
	}

	public SBPlayer getSBPlayer() {
		return sbPlayer;
	}

	public UUID getUniqueId() {
		return sbPlayer.getUniqueId();
	}

	public String getOptionValue() {
		return optionValue;
	}

	public String getCoords() {
		return blockCoords.getCoords();
	}

	public String getFullCoords() {
		return blockCoords.getFullCoords();
	}

	public BlockCoords getBlockCoords() {
		return blockCoords;
	}

	public MapManager getMapManager() {
		return mapManager;
	}

	public List<String> getScripts() {
		return scripts;
	}

	public ScriptType getScriptType() {
		return scriptType;
	}

	protected ScriptRead getScriptRead() {
		return scriptRead;
	}

	public ScriptData getScriptData() {
		return scriptData;
	}

	public int getScriptIndex() {
		return scriptIndex;
	}

	public abstract boolean isValid() throws Exception;

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
			Utils.dispatchCommand(player, command, getBlockCoords());
		} else {
			try {
				player.setOp(true);
				Utils.dispatchCommand(player, command, getBlockCoords());
			} finally {
				player.setOp(false);
			}
		}
	}
}