package com.github.yuttyann.scriptblockplus.manager;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.yuttyann.scriptblockplus.BlockLocation;
import com.github.yuttyann.scriptblockplus.PlayerSelector;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.file.Messages;
import com.github.yuttyann.scriptblockplus.file.ScriptData;
import com.github.yuttyann.scriptblockplus.hook.HookPlugins;
import com.github.yuttyann.scriptblockplus.option.Amount;
import com.github.yuttyann.scriptblockplus.option.Cooldown;
import com.github.yuttyann.scriptblockplus.option.Delay;
import com.github.yuttyann.scriptblockplus.option.Group;
import com.github.yuttyann.scriptblockplus.option.ItemCost;
import com.github.yuttyann.scriptblockplus.option.ItemHand;
import com.github.yuttyann.scriptblockplus.option.MoneyCost;
import com.github.yuttyann.scriptblockplus.option.Perm;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class ScriptManager extends ScriptReadManager {

	private ScriptBlock plugin;
	private BlockLocation location;
	private ScriptType scriptType;
	private ScriptData scriptData;

	public ScriptManager(ScriptBlock plugin, BlockLocation location, ScriptType scriptType) {
		super(plugin, location, scriptType);
		this.plugin = plugin;
		this.location = location;
		this.scriptType = scriptType;
		this.scriptData = new ScriptData(plugin, location, scriptType);
	}

	public void scriptExec(Player player) {
		UUID uuid = player.getUniqueId();
		String fullCoords = location.getFullCoords();
		if (!scriptData.checkPath()) {
			Utils.sendPluginMessage(player, Messages.getErrorScriptFileCheckMessage());
			return;
		}
		String coords = location.getCoords();
		List<String> scripts = scriptData.getScripts();
		for (int i = 0, s = scripts.size(); i < s; i++) {
			init(false);
			if (!readScript(scripts.get(i))) {
				Utils.sendPluginMessage(player, Messages.getErrorScriptMessage(scriptType));
				Utils.sendPluginMessage(Messages.getConsoleErrorScriptExecMessage(player, scriptType, location.getWorld(), coords));
				return;
			}
			if (hasOption()) {
				Delay delay = getDelay();
				if (delay != null && delay.contains(fullCoords, uuid)) {
					Utils.sendPluginMessage(player, Messages.getActiveDelayMessage());
					return;
				}
				Cooldown cooldown = getCooldown();
				if (cooldown != null && cooldown.contains(fullCoords, uuid)) {
					int[] params = cooldown.get(fullCoords, uuid);
					Utils.sendPluginMessage(player, Messages.getActiveCooldownMessage((short) params[0], (byte) params[1], (byte) params[2]));
					return;
				}
				Perm perm = getPerm();
				if (perm != null && !perm.playerPerm(player)) {
					Utils.sendPluginMessage(player, Messages.notPermissionMessage);
					return;
				}
				Group group = getGroup();
				if (group != null && !group.playerGroup(player)) {
					Utils.sendPluginMessage(player, Messages.getErrorGroupMessage(group.getName()));
					return;
				}
				ItemHand itemHand = getItemHand();
				if (itemHand != null) {
					if (!itemHand.check(player)) {
						Utils.sendPluginMessage(player, Messages.getErrorHandMessage(itemHand.getMaterial(), itemHand.getId(), itemHand.getAmount(), itemHand.getDurability(), itemHand.getItemName()));
						return;
					}
				}
				MoneyCost moneyCost = getMoneyCost();
				if (moneyCost != null) {
					if (!moneyCost.payment(player)) {
						Utils.sendPluginMessage(player, Messages.getErrorCostMessage(moneyCost.getCost(), moneyCost.getResult()));
						return;
					}
				}
				ItemCost itemCost = getItemCost();
				if (itemCost != null) {
					if (!itemCost.payment(player)) {
						if (moneyCost != null && moneyCost.isSuccess()) {
							HookPlugins.getVaultEconomy().depositPlayer(player, moneyCost.getCost());
						}
						Utils.sendPluginMessage(player, Messages.getErrorItemMessage(itemCost.getMaterial(), itemCost.getId(), itemCost.getAmount(), itemCost.getDurability(), itemCost.getItemName()));
						return;
					}
				}
				if (delay != null) {
					final Player player2 = player;
					final UUID uuid2 = uuid;
					final Delay delay2 = delay;
					final String fullCoords2 = fullCoords;
					final ScriptReadManager readManager = this;
					delay2.put(fullCoords2, uuid2);
					new BukkitRunnable() {
						@Override
						public void run() {
							scriptOptions(player2, uuid2, fullCoords2, readManager);
							delay2.remove(fullCoords2, uuid2);
						}
					}.runTaskLater(plugin, delay.getTick());
				} else {
					scriptOptions(player, uuid, fullCoords, this);
				}
			} else {
				commandExec(player, replace(player, getCommand(), false), isBypass());
			}
			if (i == (s - 1)) {
				Utils.sendPluginMessage(Messages.getConsoleSuccScriptExecMessage(player, scriptType, location.getWorld(), coords));
			}
		}
	}

	public void scriptOptions(Player player, UUID uuid, String fullCoords, ScriptReadManager readManager) {
		Perm permADD = readManager.getPermADD();
		if (permADD != null) {
			permADD.playerPerm(player);
		}
		Perm permREMOVE = readManager.getPermREMOVE();
		if (permREMOVE != null) {
			permREMOVE.playerPerm(player);
		}
		Group groupADD = readManager.getGroupADD();
		if (groupADD != null) {
			groupADD.playerGroup(player);
		}
		Group groupREMOVE = readManager.getGroupREMOVE();
		if (groupREMOVE != null) {
			groupREMOVE.playerGroup(player);
		}
		Amount amount = readManager.getAmount();
		if (amount != null) {
			amount.add();
			if (amount.check()) {
				amount.remove();
			}
		}
		Cooldown cooldown = readManager.getCooldown();
		if (cooldown != null) {
			cooldown.run(player.getUniqueId(), fullCoords);
		}
		if (readManager.getPlayer() != null && player.isOnline()) {
			player.sendMessage(replace(player, readManager.getPlayer(), true));
		}
		if (readManager.getServer() != null && player.isOnline()) {
			Bukkit.broadcastMessage(replace(player, readManager.getServer(), true));
		}
		if (readManager.getSay() != null && player.isOnline()) {
			commandExec(player, "/say " + replace(player, readManager.getSay(), false), true);
		}
		if (readManager.getCommand() != null && player.isOnline()) {
			commandExec(player, replace(player, readManager.getCommand(), false), readManager.isBypass());
		}
	}

	private String replace(Player player, String text, boolean isColor) {
		text = StringUtils.replace(text, "<player>", player.getName());
		text = StringUtils.replace(text, "<dplayer>", player.getDisplayName());
		if (isColor) {
			text = StringUtils.replace(text, "&rc", Utils.getRandomColor());
			text = StringUtils.replace(text, "&", "§");
			return text;
		}
		return text;
	}

	private void commandExec(Player player, String command, boolean isBypass) {
		if (!isBypass || player.isOp()) {
			dispatchCommand(player, command, location.getAllCenter());
		} else {
			try {
				player.setOp(true);
				dispatchCommand(player, command, location.getAllCenter());
			} finally {
				player.setOp(false);
			}
		}
	}

	private void dispatchCommand(Player player, String command, BlockLocation location) {
		if (command.startsWith("/")) {
			command = command.substring(1);
		}
		String pattern = getCommandBlockPattern(command);
		if (pattern != null) {
			Player[] players = PlayerSelector.getPlayers(location, pattern);
			if (players != null) {
				for (Player p : players) {
					Bukkit.dispatchCommand(player, StringUtils.replace(command, pattern, p.getName()));
				}
			}
		} else {
			Bukkit.dispatchCommand(player, command);
		}
	}

	private String getCommandBlockPattern(String command) {
		String[] arguments = command.split(" ");
		for (int i = 1; i < arguments.length; i++) {
			if (PlayerSelector.isPattern(arguments[i])) {
				return arguments[i];
			}
		}
		return null;
	}
}