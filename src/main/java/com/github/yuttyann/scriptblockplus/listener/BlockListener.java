package com.github.yuttyann.scriptblockplus.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import com.github.yuttyann.scriptblockplus.enums.ClickType;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.event.BlockInteractEvent;
import com.github.yuttyann.scriptblockplus.event.ScriptBlockBreakEvent;
import com.github.yuttyann.scriptblockplus.event.ScriptBlockInteractEvent;
import com.github.yuttyann.scriptblockplus.file.Messages;
import com.github.yuttyann.scriptblockplus.manager.MapManager;
import com.github.yuttyann.scriptblockplus.manager.MetadataManager;
import com.github.yuttyann.scriptblockplus.manager.MetadataManager.Click;
import com.github.yuttyann.scriptblockplus.manager.MetadataManager.Script;
import com.github.yuttyann.scriptblockplus.manager.MetadataManager.ScriptFile;
import com.github.yuttyann.scriptblockplus.manager.OptionManager;
import com.github.yuttyann.scriptblockplus.manager.ScriptFileManager;
import com.github.yuttyann.scriptblockplus.utils.BlockLocation;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class BlockListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		ItemStack item = Utils.getItemInHand(player);
		if (Utils.checkItem(item, Material.BLAZE_ROD, "§dScript Editor")
				&& Permission.has(Permission.SCRIPTBLOCKPLUS_TOOL_SCRIPTEDITOR, player)) {
			event.setCancelled(true);
			return;
		}
		Block block = event.getBlock();
		BlockLocation location = BlockLocation.fromLocation(block.getLocation());
		if (MapManager.getBreakCoords().contains(location.getFullCoords())) {
			ScriptBlockBreakEvent scEvent = new ScriptBlockBreakEvent(player, location.getBlock(), item, location);
			Utils.callEvent(scEvent);
			if (scEvent.isCancelled()) {
				return;
			}
			if (!Permission.has(Permission.SCRIPTBLOCKPLUS_BREAK_USE, player)) {
				Utils.sendPluginMessage(player, Messages.notPermissionMessage);
				return;
			}
			new OptionManager(location, ScriptType.BREAK).scriptExec(player);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockInteract(BlockInteractEvent event) {
		Block block = event.getBlock();
		BlockLocation location = BlockLocation.fromLocation(block.getLocation());
		if (!scriptSetting(event, event.getAction(), block, location)) {
			scriptEvent(event, block, location);
		}
	}

	private boolean scriptSetting(BlockInteractEvent event, Action action, Block block, BlockLocation location) {
		Player player = event.getPlayer();
		if (Utils.checkItem(event.getItem(), Material.BLAZE_ROD, "§dScript Editor")) {
			if (!Permission.has(Permission.SCRIPTBLOCKPLUS_TOOL_SCRIPTEDITOR, player)) {
				Utils.sendPluginMessage(player, Messages.notPermissionMessage);
				return false;
			}
			switch (action) {
			case LEFT_CLICK_BLOCK:
				if (player.isSneaking()) {
					new ScriptFileManager(location, ScriptType.WALK).scriptCopy(player);
				} else {
					new ScriptFileManager(location, ScriptType.INTERACT).scriptCopy(player);
				}
				event.setCancelled(true);
				return true;
			case RIGHT_CLICK_BLOCK:
				if (player.isSneaking()) {
					if (!ScriptFile.hasAllMetadata(player)) {
						Utils.sendPluginMessage(player, Messages.getErrorScriptFileCheckMessage());
						break;
					}
					ScriptFile.getMetadata(player).scriptPaste(player, location);
				} else {
					new ScriptFileManager(location, ScriptType.BREAK).scriptCopy(player);
				}
				event.setCancelled(true);
				return true;
			default:
				MetadataManager.removeAllMetadata(player);
				return false;
			}
		}
		String[] split;
		for (ClickType type : ClickType.values()) {
			if (Click.hasMetadata(player, type)) {
				split = type.name().split("_");
				if (clickScript(player, split[1], location, type, ScriptType.valueOf(split[0]))) {
					event.setCancelled(true);
					return true;
				}
			}
		}
		return false;
	}

	private boolean clickScript(Player player, String type, BlockLocation location, ClickType clickType, ScriptType scriptType) {
		switch (type) {
		case "CREATE":
			new ScriptFileManager(location, scriptType).scriptCreate(player, Script.getMetadata(player, clickType));
			return true;
		case "ADD":
			new ScriptFileManager(location, scriptType).scriptAdd(player, Script.getMetadata(player, clickType));
			return true;
		case "REMOVE":
			new ScriptFileManager(location, scriptType).scriptRemove(player);
			return true;
		case "VIEW":
			new ScriptFileManager(location, scriptType).scriptView(player);
			return true;
		default:
			return false;
		}
	}

	private void scriptEvent(BlockInteractEvent event, Block block, BlockLocation location) {
		if (Utils.isCB19orLater() && !isSlotHand(event.getHand())) {
			return;
		}
		Player player = event.getPlayer();
		if (MapManager.getInteractCoords().contains(location.getFullCoords())) {
			ScriptBlockInteractEvent scEvent = new ScriptBlockInteractEvent(player, block, event.getItem(), location);
			Utils.callEvent(scEvent);
			if (scEvent.isCancelled()) {
				return;
			}
			if (!scEvent.isLeftClick() && event.getAction() == Action.LEFT_CLICK_BLOCK) {
				return;
			}
			if (!Permission.has(Permission.SCRIPTBLOCKPLUS_INTERACT_USE, player)) {
				Utils.sendPluginMessage(player, Messages.notPermissionMessage);
				return;
			}
			new OptionManager(location, ScriptType.INTERACT).scriptExec(player);
		}
	}

	private boolean isSlotHand(EquipmentSlot hand) {
		return hand != null && hand == EquipmentSlot.HAND;
	}
}