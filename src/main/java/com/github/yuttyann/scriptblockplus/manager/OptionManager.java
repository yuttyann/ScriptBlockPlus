package com.github.yuttyann.scriptblockplus.manager;

import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.yuttyann.scriptblockplus.Main;
import com.github.yuttyann.scriptblockplus.collplugin.CollPlugins;
import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.Messages;
import com.github.yuttyann.scriptblockplus.file.Yaml;
import com.github.yuttyann.scriptblockplus.option.Amount;
import com.github.yuttyann.scriptblockplus.option.Cooldown;
import com.github.yuttyann.scriptblockplus.option.Delay;
import com.github.yuttyann.scriptblockplus.option.Group;
import com.github.yuttyann.scriptblockplus.option.ItemCost;
import com.github.yuttyann.scriptblockplus.option.MoneyCost;
import com.github.yuttyann.scriptblockplus.option.Perm;
import com.github.yuttyann.scriptblockplus.util.BlockLocation;
import com.github.yuttyann.scriptblockplus.util.Utils;

public class OptionManager {

	public enum ScriptType {
		INTERACT("interact"),
		WALK("walk");

		private String type;

		private ScriptType(String type) {
			this.type = type;
		}

		public String getString() {
			return type;
		}
	}

	public static void scriptExec(Player player, BlockLocation location, ScriptType scriptType) {
		UUID uuid = player.getUniqueId();
		String fullcoords = location.getCoords(true);
		if (Delay.contains(fullcoords, uuid)) {
			player.sendMessage(Messages.getActiveDelayMessage());
			return;
		}
		if (Cooldown.contains(fullcoords, uuid)) {
			long[] params = Cooldown.getParams(fullcoords, uuid);
			player.sendMessage(Messages.getActiveCooldownMessage((short) params[0], (byte) params[1], (byte) params[2]));
			return;
		}
		Yaml scripts = Files.getScripts(scriptType);
		String coords = location.getCoords(false);
		String scriptPath = location.getWorld().getName() + "." + coords;
		if (!scripts.getFile().exists() || !scripts.contains(scriptPath + ".Author")) {
			Utils.sendPluginMessage(player, Messages.getErrorScriptFileCheckMessage());
			return;
		}

		boolean isConsoleLog = Files.getConfig().isBoolean("ConsoleLog");
		ScriptManager manager = new ScriptManager().setType(scriptType).setLocation(location);
		List<String> list = scripts.getStringList(scriptPath + ".Scripts");
		String script;
		for (int i = 0, l = list.size(); i < l; i++) {
			script = list.get(i);
			manager.reset();
			if (!manager.readScript(script)) {
				Utils.sendPluginMessage(player, Messages.getErrorScriptMessage(scriptType));
				if (isConsoleLog)
					Utils.sendPluginMessage(Messages.getConsoleErrorScriptExecMessage(player, scriptType, location.getWorld(), coords));
				return;
			}
			if (isConsoleLog)
				Utils.sendPluginMessage(Messages.getConsoleSuccScriptExecMessage(player, scriptType, location.getWorld(), coords));

			if (!manager.hasOption()) {
				dispatchCommand(player, location, manager.getCommand(), manager.getScriptType(), manager.isBypass());
				continue;
			}
			Perm perm = manager.getPerm();
			if (perm != null && !perm.playerPerm(player)) {
				player.sendMessage("§cパーミッションが無いため、実行できません。");
				if (i == 0)
					return;
				else
					continue;
			}
			Group group = manager.getGroup();
			if (group != null && !group.playerGroup(player)) {
				player.sendMessage(Messages.getErrorGroupMessage(group.getName()));
				if (i == 0)
					return;
				else
					continue;
			}
			if (manager.getDelay() == null) {
				scriptOptions(player, uuid, fullcoords, location, manager);
				continue;
			}
			final Player player2 = player;
			final UUID uuid2 = uuid;
			final String fullcoords2 = fullcoords;
			final BlockLocation location2 = location;
			final ScriptManager manager2 = manager;
			Delay.put(fullcoords2, uuid2);
			new BukkitRunnable() {
				@Override
				public void run() {
					Delay.remove(fullcoords2, uuid2);
					scriptOptions(player2, uuid2, fullcoords2, location2, manager2);
				}
			}.runTaskLater(Main.instance, manager.getDelay().getTick());
		}
	}

	private static void scriptOptions(Player player, UUID uuid, String fullcoords, BlockLocation location, ScriptManager manager) {
		Perm permADD = manager.getPermADD();
		if (permADD != null)
			permADD.playerPerm(player);

		Perm permREMOVE = manager.getPermREMOVE();
		if (permREMOVE != null)
			permREMOVE.playerPerm(player);

		Group groupADD = manager.getGroupADD();
		if (groupADD != null)
			groupADD.playerGroup(player);

		Group groupREMOVE = manager.getGroupREMOVE();
		if (groupREMOVE != null)
			groupREMOVE.playerGroup(player);

		Cooldown cooldown = manager.getCooldown();
		if (cooldown != null)
			cooldown.run(uuid, fullcoords);

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
				if (moneyCost != null && moneyCost.isSuccess())
					CollPlugins.getVaultEconomy().depositPlayer(player, moneyCost.getCost());
				Utils.sendPluginMessage(player, Messages.getErrorItemMessage(itemCost.getMaterial(), itemCost.getId(), itemCost.getAmount(), itemCost.getDurability()));
				return;
			}
		}
		Amount amount = manager.getAmount();
		if (amount != null) {
			amount.plus();
			if (amount.check())
				amount.remove();
		}
		if (manager.getPlayer() != null && player.isOnline())
			player.sendMessage(manager.getPlayer().replace("&", "§").replace("<player>", player.getName()));

		if (manager.getSay() != null && player.isOnline())
			dispatchCommand(player, location, "/say " + manager.getSay().replace("<player>", player.getName()), manager.getScriptType(), true);

		if (manager.getCommand() != null && player.isOnline())
			dispatchCommand(player, location, manager.getCommand().replace("<player>", player.getName()), manager.getScriptType(), manager.isBypass());
	}

	private static void dispatchCommand(Player player, BlockLocation location, String command, ScriptType scriptType, boolean isBypass) {
		if (!isBypass) {
			Utils.dispatchCommand(player, location.getLocation(), command);
			return;
		}
		if (player.isOp()) {
			Utils.dispatchCommand(player, location.getLocation(), command);
			return;
		}
		try {
			player.setOp(true);
			Utils.dispatchCommand(player, location.getLocation(), command);
		} finally {
			player.setOp(false);
		}
	}
}
