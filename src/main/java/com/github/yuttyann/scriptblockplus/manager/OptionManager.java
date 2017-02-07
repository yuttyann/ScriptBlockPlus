package com.github.yuttyann.scriptblockplus.manager;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.yuttyann.scriptblockplus.BlockLocation;
import com.github.yuttyann.scriptblockplus.PlayerSelector;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.collplugin.CollPlugins;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.file.Messages;
import com.github.yuttyann.scriptblockplus.file.ScriptData;
import com.github.yuttyann.scriptblockplus.option.Amount;
import com.github.yuttyann.scriptblockplus.option.Cooldown;
import com.github.yuttyann.scriptblockplus.option.Delay;
import com.github.yuttyann.scriptblockplus.option.Group;
import com.github.yuttyann.scriptblockplus.option.Hand;
import com.github.yuttyann.scriptblockplus.option.ItemCost;
import com.github.yuttyann.scriptblockplus.option.MoneyCost;
import com.github.yuttyann.scriptblockplus.option.Perm;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class OptionManager {

	private ScriptData scriptData;
	private BlockLocation location;
	private ScriptType scriptType;

	public OptionManager(BlockLocation location, ScriptType scriptType) {
		this.scriptData = new ScriptData(location, scriptType);
		this.location = location;
		this.scriptType = scriptType;
	}

	public void scriptExec(Player player) {
		UUID uuid = player.getUniqueId();
		String fullCoords = location.getFullCoords();
		if (!scriptData.checkPath()) {
			Utils.sendPluginMessage(player, Messages.getErrorScriptFileCheckMessage());
			return;
		}
		String coords = location.getCoords();
		ScriptManager manager = new ScriptManager(location, scriptType);
		for (String script : scriptData.getScripts()) {
			manager.reset();
			if (!manager.readScript(script)) {
				Utils.sendPluginMessage(player, Messages.getErrorScriptMessage(scriptType));
				Utils.sendPluginMessage(Messages.getConsoleErrorScriptExecMessage(player, scriptType, location.getWorld(), coords));
				return;
			}
			if (!manager.hasOption()) {
				commandExec(player, replace(player, manager.getCommand(), false), manager.isBypass());
				Utils.sendPluginMessage(Messages.getConsoleSuccScriptExecMessage(player, scriptType, location.getWorld(), coords));
				return;
			}
			Delay delay = manager.getDelay();
			if (delay != null && delay.contains(fullCoords, uuid)) {
				Utils.sendPluginMessage(player, Messages.getActiveDelayMessage());
				return;
			}
			Cooldown cooldown = manager.getCooldown();
			if (cooldown != null && cooldown.contains(fullCoords, uuid)) {
				int[] params = cooldown.get(fullCoords, uuid);
				Utils.sendPluginMessage(player, Messages.getActiveCooldownMessage((short) params[0], (byte) params[1], (byte) params[2]));
				return;
			}
			Perm perm = manager.getPerm();
			if (perm != null && !perm.playerPerm(player)) {
				Utils.sendPluginMessage(player, Messages.notPermissionMessage);
				return;
			}
			Group group = manager.getGroup();
			if (group != null && !group.playerGroup(player)) {
				Utils.sendPluginMessage(player, Messages.getErrorGroupMessage(group.getName()));
				return;
			}
			if (delay == null) {
				scriptOptions(player, fullCoords, manager);
				continue;
			}
			final Player player2 = player;
			final UUID uuid2 = uuid;
			final Delay delay2 = delay;
			final String fullcoords2 = fullCoords;
			final ScriptManager manager2 = manager;
			delay.put(fullcoords2, uuid2);
			new BukkitRunnable() {
				@Override
				public void run() {
					delay2.remove(fullcoords2, uuid2);
					scriptOptions(player2, fullcoords2, manager2);
				}
			}.runTaskLater(ScriptBlock.instance, manager.getDelay().getTick());
		}
	}

	private void scriptOptions(Player player, String fullCoords, ScriptManager manager) {
		Hand hand = manager.getHand();
		if (hand != null) {
			if (!hand.check(player)) {
				Utils.sendPluginMessage(player, Messages.getErrorHandMessage(hand.getMaterial(), hand.getId(),
					hand.getAmount(), hand.getDurability(), hand.getItemName()));
				return;
			}
		}
		MoneyCost moneyCost = manager.getMoneyCost();
		if (moneyCost != null) {
			if (!moneyCost.payment(player)) {
				Utils.sendPluginMessage(player, Messages.getErrorCostMessage(moneyCost.getCost(), moneyCost.getResult()));
				return;
			}
		}
		ItemCost itemCost = manager.getItemCost();
		if (itemCost != null) {
			if (!itemCost.payment(player)) {
				if (moneyCost != null && moneyCost.isSuccess()) {
					CollPlugins.getVaultEconomy().depositPlayer(player, moneyCost.getCost());
				}
				Utils.sendPluginMessage(player, Messages.getErrorItemMessage(itemCost.getMaterial(), itemCost.getId(),
					itemCost.getAmount(), itemCost.getDurability(), itemCost.getItemName()));
				return;
			}
		}
		Perm permADD = manager.getPermADD();
		if (permADD != null) {
			permADD.playerPerm(player);
		}
		Perm permREMOVE = manager.getPermREMOVE();
		if (permREMOVE != null) {
			permREMOVE.playerPerm(player);
		}
		Group groupADD = manager.getGroupADD();
		if (groupADD != null) {
			groupADD.playerGroup(player);
		}
		Group groupREMOVE = manager.getGroupREMOVE();
		if (groupREMOVE != null) {
			groupREMOVE.playerGroup(player);
		}
		Amount amount = manager.getAmount();
		if (amount != null) {
			amount.add();
			if (amount.check()) {
				amount.remove();
			}
		}
		Cooldown cooldown = manager.getCooldown();
		if (cooldown != null) {
			cooldown.run(player.getUniqueId(), fullCoords);
		}
		if (manager.getPlayer() != null && player.isOnline()) {
			player.sendMessage(replace(player, manager.getPlayer(), true));
		}
		if (manager.getServer() != null && player.isOnline()) {
			Bukkit.broadcastMessage(replace(player, manager.getServer(), true));
		}
		if (manager.getSay() != null && player.isOnline()) {
			commandExec(player, "/say " + replace(player, manager.getSay(), false), true);
		}
		if (manager.getCommand() != null && player.isOnline()) {
			commandExec(player, replace(player, manager.getCommand(), false), manager.isBypass());
		}
		Utils.sendPluginMessage(Messages.getConsoleSuccScriptExecMessage(player, scriptType, location.getWorld(), location.getCoords()));
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
			dispatchCommand(player, command);
			return;
		}
		try {
			player.setOp(true);
			dispatchCommand(player, command);
		} finally {
			player.setOp(false);
		}
	}

	private void dispatchCommand(Player player, String command) {
		if (command.startsWith("/")) {
			command = command.substring(1);
		}
		String pattern = PlayerSelector.getCommandBlockPattern(command);
		Location location = this.location;
		if (pattern != null) {
			if (location == null) {
				location = player.getLocation().clone();
			}
			Player[] players = PlayerSelector.getPlayers(location, pattern);
			if (players != null) {
				for (Player p : players) {
					Bukkit.dispatchCommand(p, StringUtils.replace(command, pattern, p.getName()));
				}
			}
		} else {
			Bukkit.dispatchCommand(player, command);
		}
	}
}