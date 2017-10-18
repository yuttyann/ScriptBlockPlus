package com.github.yuttyann.scriptblockplus.script.option;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.PlayerSelector;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.manager.MapManager;
import com.github.yuttyann.scriptblockplus.manager.OptionManager;
import com.github.yuttyann.scriptblockplus.script.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.ScriptData;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.script.hook.VaultEconomy;
import com.github.yuttyann.scriptblockplus.script.hook.VaultPermission;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public abstract class BaseOption extends Option {

	private Plugin plugin;
	private SBPlayer sbPlayer;
	private String optionValue;
	private List<String> scripts;
	private ScriptType scriptType;
	private ScriptRead scriptRead;
	private ScriptData scriptData;
	private MapManager mapManager;
	private BlockCoords blockCoords;
	private VaultEconomy vaultEconomy;
	private VaultPermission vaultPermission;
	private int scriptIndex;

	public BaseOption(String name, String prefix) {
		this(name, prefix, new OptionManager().size() - 1);
	}

	public BaseOption(String name, String prefix, int index) {
		super(name, prefix, index);
	}

	protected Plugin getPlugin() {
		return plugin;
	}

	protected Player getPlayer() {
		return sbPlayer.getPlayer();
	}

	protected UUID getUniqueId() {
		return sbPlayer.getUniqueId();
	}

	protected SBPlayer getSBPlayer() {
		return sbPlayer;
	}

	protected String getCoords() {
		return blockCoords.getCoords();
	}

	protected String getFullCoords() {
		return blockCoords.getFullCoords();
	}

	protected String getOptionValue() {
		return optionValue;
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

	protected MapManager getMapManager() {
		return mapManager;
	}

	protected BlockCoords getBlockCoords() {
		return blockCoords;
	}

	protected VaultEconomy getVaultEconomy() {
		return vaultEconomy;
	}

	protected VaultPermission getVaultPermission() {
		return vaultPermission;
	}

	protected int getScriptIndex() {
		return scriptIndex;
	}

	public abstract boolean isValid();

	@Override
	@Deprecated
	public final boolean callOption(ScriptRead scriptRead) {
		this.scriptRead = scriptRead;
		this.plugin = scriptRead.getPlugin();
		this.sbPlayer = scriptRead.getSBPlayer();
		this.optionValue = scriptRead.getOptionValue();
		this.scripts = scriptRead.getScripts();
		this.scriptType = scriptRead.getScriptType();
		this.scriptData = scriptRead.getScriptData();
		this.mapManager = scriptRead.getMapManager();
		this.blockCoords = scriptRead.getBlockCoords();
		this.vaultEconomy = scriptRead.getVaultEconomy();
		this.vaultPermission = scriptRead.getVaultPermission();
		this.scriptIndex = scriptRead.getScriptIndex();
		try {
			return isValid();
		} catch (Exception e) {
			Utils.sendMessage(sbPlayer, SBConfig.getOptionFailedToExecuteMessage(this, e));
		}
		return false;
	}

	protected final void commandExecute(Player player, String command, boolean isBypass) {
		if (!isBypass || player.isOp()) {
			dispatchCommand(player, command, blockCoords.getAllCenter());
		} else {
			try {
				player.setOp(true);
				dispatchCommand(player, command, blockCoords.getAllCenter());
			} finally {
				player.setOp(false);
			}
		}
	}

	protected final void dispatchCommand(CommandSender sender, String command, Location location) {
		if (!isOnline(sender) || StringUtils.isEmpty(command)) {
			return;
		}
		if (command.charAt(0) == '/') {
			command = command.substring(1);
		}
		String pattern = getCommandBlockPattern(command);
		if (pattern != null) {
			if (location == null) {
				if (sender instanceof Player) {
					location = ((Player) sender).getLocation().clone();
				} else if (sender instanceof BlockCommandSender) {
					location = ((BlockCommandSender) sender).getBlock().getLocation().clone();
				}
			}
			Player[] players = PlayerSelector.getPlayers(location, pattern);
			if (players != null) {
				for (Player p : players) {
					Bukkit.dispatchCommand(sender, StringUtils.replace(command, pattern, p.getName()));
				}
			}
		} else {
			Bukkit.dispatchCommand(sender, command);
		}
	}

	private String getCommandBlockPattern(String command) {
		String[] array = StringUtils.split(command, " ");
		for (int i = 1; i < array.length; i++) {
			if (PlayerSelector.isPattern(array[i])) {
				return array[i];
			}
		}
		return null;
	}

	private boolean isOnline(CommandSender sender) {
		return sender instanceof Player ? ((Player) sender).isOnline() : true;
	}
}