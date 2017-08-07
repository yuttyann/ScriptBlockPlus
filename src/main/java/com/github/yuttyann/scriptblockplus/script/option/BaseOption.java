package com.github.yuttyann.scriptblockplus.script.option;

import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.file.YamlConfig;
import com.github.yuttyann.scriptblockplus.manager.MapManager;
import com.github.yuttyann.scriptblockplus.manager.ScriptManager;
import com.github.yuttyann.scriptblockplus.script.ScriptData;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.script.hook.VaultEconomy;
import com.github.yuttyann.scriptblockplus.script.hook.VaultPermission;
import com.github.yuttyann.scriptblockplus.utils.Utils;


public abstract class BaseOption extends Option {

	protected Plugin plugin;
	protected Player player;
	protected UUID uuid;
	protected String optionData;
	protected List<String> scripts;
	protected ScriptType scriptType;
	protected ScriptRead scriptRead;
	protected YamlConfig scriptFile;
	protected ScriptData scriptData;
	protected MapManager mapManager;
	protected BlockCoords blockCoords;
	protected VaultEconomy vaultEconomy;
	protected VaultPermission vaultPermission;
	protected int scriptIndex;

	protected BaseOption(ScriptManager scriptManager, String name, String prefix) {
		super(name, prefix);
		this.plugin = scriptManager.getPlugin();
		this.scriptType = scriptManager.getScriptType();
		this.scriptFile = scriptManager.getScriptFile();
		this.mapManager = scriptManager.getMapManager();
		this.vaultEconomy = scriptManager.getVaultEconomy();
		this.vaultPermission = scriptManager.getVaultPermission();
	}

	@Override
	public boolean callOption(ScriptRead scriptRead) {
		this.scriptRead = scriptRead;
		player = scriptRead.getPlayer();
		uuid = scriptRead.getUUID();
		optionData = scriptRead.getOptionData();
		scripts = scriptRead.getScripts();
		scriptData = scriptRead.getScriptData();
		blockCoords = scriptRead.getBlockCoords();
		scriptIndex = scriptRead.getScriptIndex();
		return isValid();
	}

	public abstract boolean isValid();

	protected void commandExec(Player player, String command, boolean isBypass) {
		if (!isBypass || player.isOp()) {
			Utils.dispatchCommand(player, command, blockCoords.getAllCenter());
		} else {
			try {
				player.setOp(true);
				Utils.dispatchCommand(player, command, blockCoords.getAllCenter());
			} finally {
				player.setOp(false);
			}
		}
	}
}