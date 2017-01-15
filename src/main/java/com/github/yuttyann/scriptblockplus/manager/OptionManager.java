package com.github.yuttyann.scriptblockplus.manager;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.yuttyann.scriptblockplus.PlayerSelector;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.collplugin.CollPlugins;
import com.github.yuttyann.scriptblockplus.file.Messages;
import com.github.yuttyann.scriptblockplus.file.ScriptData;
import com.github.yuttyann.scriptblockplus.option.Amount;
import com.github.yuttyann.scriptblockplus.option.Cooldown;
import com.github.yuttyann.scriptblockplus.option.Delay;
import com.github.yuttyann.scriptblockplus.option.Group;
import com.github.yuttyann.scriptblockplus.option.ItemCost;
import com.github.yuttyann.scriptblockplus.option.MoneyCost;
import com.github.yuttyann.scriptblockplus.option.Perm;
import com.github.yuttyann.scriptblockplus.type.ScriptType;
import com.github.yuttyann.scriptblockplus.util.BlockLocation;
import com.github.yuttyann.scriptblockplus.util.Utils;

public class OptionManager extends PlayerSelector {

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
		String fullcoords = location.getFullCoords();
		if (Delay.contains(fullcoords, uuid)) {
			player.sendMessage(Messages.getActiveDelayMessage());
			return;
		}
		if (Cooldown.contains(fullcoords, uuid)) {
			long[] params = Cooldown.getParams(fullcoords, uuid);
			player.sendMessage(Messages.getActiveCooldownMessage((short) params[0], (byte) params[1], (byte) params[2]));
			return;
		}
		if (!scriptData.checkPath()) {
			Utils.sendPluginMessage(player, Messages.getErrorScriptFileCheckMessage());
			return;
		}
		String script = null;
		String coords = location.getCoords();
		ScriptManager manager = new ScriptManager(location, scriptType);
		List<String> scripts = scriptData.getScripts();
		for (int i = 0, l = scripts.size(); i < l; i++) {
			script = scripts.get(i);
			manager.reset();
			if (!manager.readScript(script)) {
				Utils.sendPluginMessage(player, Messages.getErrorScriptMessage(scriptType));
				Utils.sendPluginMessage(Messages.getConsoleErrorScriptExecMessage(player, scriptType, location.getWorld(), coords));
				return;
			}
			Utils.sendPluginMessage(Messages.getConsoleSuccScriptExecMessage(player, scriptType, location.getWorld(), coords));
			if (!manager.hasOption()) {
				commandExec(player, replace(player, manager.getCommand(), false), manager.isBypass());
				continue;
			}
			Perm perm = manager.getPerm();
			if (perm != null && !perm.playerPerm(player)) {
				player.sendMessage("§cパーミッションが無いため、実行できません。");
				return;
			}
			Group group = manager.getGroup();
			if (group != null && !group.playerGroup(player)) {
				player.sendMessage(Messages.getErrorGroupMessage(group.getName()));
				return;
			}
			if (manager.getDelay() == null) {
				scriptOptions(player, fullcoords, manager);
				continue;
			}
			final Player player2 = player;
			final UUID uuid2 = uuid;
			final String fullcoords2 = fullcoords;
			final ScriptManager manager2 = manager;
			Delay.put(fullcoords2, uuid2);
			new BukkitRunnable() {
				@Override
				public void run() {
					Delay.remove(fullcoords2, uuid2);
					scriptOptions(player2, fullcoords2, manager2);
				}
			}.runTaskLater(ScriptBlock.instance, manager.getDelay().getTick());
		}
	}

	private void scriptOptions(Player player, String fullcoords, ScriptManager manager) {
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
		Cooldown cooldown = manager.getCooldown();
		if (cooldown != null) {
			cooldown.run(player.getUniqueId(), fullcoords);
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
				Utils.sendPluginMessage(player, Messages.getErrorItemMessage(itemCost.getMaterial(), itemCost.getId(), itemCost.getAmount(), itemCost.getDurability()));
				return;
			}
		}
		Amount amount = manager.getAmount();
		if (amount != null) {
			amount.add();
			if (amount.check()) {
				amount.remove();
			}
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
	}

	private String replace(Player player, String text, boolean isColor) {
		text = text.replace("<player>", player.getName()).replace("<dplayer>", player.getDisplayName());
		if (isColor) {
			return text.replace("&rc", Utils.getRandomColor()).replace("&", "§");
		}
		return text;
	}

	private void commandExec(Player player, String command, boolean isBypass) {
		if (!isBypass) {
			dispatchCommand(player, command);
			return;
		}
		if (player.isOp()) {
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

	protected void dispatchCommand(Player player, String command) {
		if (command.startsWith("/")) {
			command = command.substring(1);
		}
		String pattern = getCommandBlockPattern(command);
		Location location = this.location.toLocation();
		if (pattern != null) {
			if (location == null) {
				location = player.getLocation().clone();
			}
			Player[] players = getPlayers(location, pattern);
			if (players != null) {
				for (Player p : players) {
					Bukkit.dispatchCommand(p, command.replace(pattern, player.getName()));
				}
			}
		} else {
			Bukkit.dispatchCommand(player, command);
		}
	}
}
