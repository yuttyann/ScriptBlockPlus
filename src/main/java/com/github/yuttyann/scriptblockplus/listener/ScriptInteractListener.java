package com.github.yuttyann.scriptblockplus.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Button;
import org.bukkit.material.Door;
import org.bukkit.material.Lever;
import org.bukkit.material.MaterialData;
import org.bukkit.material.TrapDoor;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.EquipmentSlot;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.event.BlockInteractEvent;
import com.github.yuttyann.scriptblockplus.event.ScriptBlockInteractEvent;
import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.manager.ScriptManager;
import com.github.yuttyann.scriptblockplus.script.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.ScriptEdit;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class ScriptInteractListener extends ScriptManager implements Listener {

	public ScriptInteractListener(ScriptBlock plugin) {
		super(plugin, ScriptType.INTERACT);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockInteract(BlockInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) {
			return;
		}
		Block block = event.getBlock();
		Location location = block.getLocation();
		if (action(event.getPlayer(), event.getAction(), event.getItem(), location)) {
			event.setCancelled(true);
			return;
		}
		Player player = event.getPlayer();
		if (mapManager.containsLocation(scriptType, location)) {
			ScriptBlockInteractEvent interactEvent = new ScriptBlockInteractEvent(player, block);
			Bukkit.getPluginManager().callEvent(interactEvent);
			if (interactEvent.isCancelled()) {
				return;
			}
			if (!interactEvent.isLeftClick() && event.getAction() == Action.LEFT_CLICK_BLOCK) {
				return;
			}
			if (!Permission.INTERACT_USE.has(player)) {
				Utils.sendMessage(player, SBConfig.getNotPermissionMessage());
				return;
			}
			MaterialData data = block.getState().getData();
			if (isPowered(data) || isOpen(data)) {
				return;
			}
			new ScriptRead(this, player, location).read(0);
		}
	}

	private boolean action(Player player, Action action, ItemStack item, Location location) {
		SBPlayer sbPlayer = SBPlayer.get(player);
		if (checkItem(player)) {
			if (!Permission.TOOL_SCRIPTEDITOR.has(player)) {
				Utils.sendMessage(player, SBConfig.getNotPermissionMessage());
				return false;
			}
			switch (action) {
			case LEFT_CLICK_BLOCK:
				ScriptType scriptType;
				if (player.isSneaking()) {
					scriptType = ScriptType.WALK;
				} else {
					scriptType = ScriptType.INTERACT;
				}
				new ScriptEdit(location, scriptType).copy(sbPlayer);
				return true;
			case RIGHT_CLICK_BLOCK:
				if (player.isSneaking()) {
					if (!sbPlayer.hasClipboard() || !sbPlayer.getClipboard().paste(sbPlayer, location, true, true)) {
						Utils.sendMessage(player, SBConfig.getErrorScriptFileCheckMessage());
					}
				} else {
					new ScriptEdit(location, ScriptType.BREAK).copy(sbPlayer);
				}
				return true;
			default:
				sbPlayer.setScript(null);
				sbPlayer.setClickAction(null);
				return false;
			}
		}
		if (sbPlayer.hasClickAction() && clickAction(sbPlayer, location)) {
			return true;
		}
		return false;
	}

	private boolean clickAction(SBPlayer sbPlayer, Location location) {
		Player player = sbPlayer.getPlayer();
		String[] array = StringUtils.split(sbPlayer.getClickAction(), "_");
		ScriptEdit scriptEdit = new ScriptEdit(location, ScriptType.valueOf(array[0]));
		switch (array[1]) {
		case "CREATE":
			scriptEdit.create(player, sbPlayer.getScript());
			return true;
		case "ADD":
			scriptEdit.add(player, sbPlayer.getScript());
			return true;
		case "REMOVE":
			scriptEdit.remove(player);
			return true;
		case "VIEW":
			scriptEdit.view(player);
			return true;
		default:
			return false;
		}
	}

	private boolean checkItem(Player player) {
		ItemStack[] items = Utils.getHandItems(player);
		return StreamUtils.anyMatch(items, i -> Utils.checkItem(i, Material.BLAZE_ROD, "§dScript Editor"));
	}

	private boolean isPowered(MaterialData data) {
		if (data instanceof Button) {
			return ((Button) data).isPowered();
		}
		if (data instanceof Lever) {
			return ((Lever) data).isPowered();
		}
		return false;
	}

	private boolean isOpen(MaterialData data) {
		if (data instanceof Door) {
			return ((Door) data).isOpen();
		}
		if (data instanceof TrapDoor) {
			return ((TrapDoor) data).isOpen();
		}
		return false;
	}
}