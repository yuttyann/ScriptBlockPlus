package com.github.yuttyann.scriptblockplus.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Button;
import org.bukkit.material.Door;
import org.bukkit.material.Lever;
import org.bukkit.material.MaterialData;
import org.bukkit.material.TrapDoor;

import com.github.yuttyann.scriptblockplus.BlockLocation;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.ClickType;
import com.github.yuttyann.scriptblockplus.enums.Metadata;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.event.BlockInteractEvent;
import com.github.yuttyann.scriptblockplus.event.ScriptBlockBreakEvent;
import com.github.yuttyann.scriptblockplus.event.ScriptBlockInteractEvent;
import com.github.yuttyann.scriptblockplus.file.Messages;
import com.github.yuttyann.scriptblockplus.manager.MapManager;
import com.github.yuttyann.scriptblockplus.manager.ScriptFileManager;
import com.github.yuttyann.scriptblockplus.manager.ScriptManager;
import com.github.yuttyann.scriptblockplus.metadata.PlayerClick;
import com.github.yuttyann.scriptblockplus.metadata.SBMetadata;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class BlockListener implements Listener {

	private ScriptBlock plugin;
	private MapManager mapManager;

	public BlockListener(ScriptBlock plugin) {
		this.plugin = plugin;
		this.mapManager = plugin.getMapManager();
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		ItemStack item = Utils.getItemInHand(player);
		if (Utils.checkItem(item, Material.BLAZE_ROD, "§dScript Editor") && Permission.has(Permission.SCRIPTBLOCKPLUS_TOOL_SCRIPTEDITOR, player)) {
			event.setCancelled(true);
			return;
		}
		Block block = event.getBlock();
		BlockLocation location = BlockLocation.fromLocation(block.getLocation());
		if (mapManager.containsLocation(location, ScriptType.BREAK)) {
			ScriptBlockBreakEvent breakEvent = new ScriptBlockBreakEvent(player, location.getBlock(), item, location);
			Utils.callEvent(breakEvent);
			if (breakEvent.isCancelled()) {
				return;
			}
			if (!Permission.has(Permission.SCRIPTBLOCKPLUS_BREAK_USE, player)) {
				Utils.sendPluginMessage(player, Messages.notPermissionMessage);
				return;
			}
			new ScriptManager(plugin, location, ScriptType.BREAK).scriptExec(player);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockInteract(BlockInteractEvent event) {
		Block block = event.getBlock();
		BlockLocation location = BlockLocation.fromLocation(block.getLocation());
		if (!scriptSetting(event, event.getAction(), block, location)) {
			if (Utils.isCB19orLater() && (!isHand(event.getHand()))) {
				return;
			}
			Player player = event.getPlayer();
			if (mapManager.containsLocation(location, ScriptType.INTERACT)) {
				ScriptBlockInteractEvent interactEvent = new ScriptBlockInteractEvent(player, block, event.getItem(), location);
				Utils.callEvent(interactEvent);
				if (interactEvent.isCancelled()) {
					return;
				}
				if (!interactEvent.getLeftClick() && event.getAction() == Action.LEFT_CLICK_BLOCK) {
					return;
				}
				if (!Permission.has(Permission.SCRIPTBLOCKPLUS_INTERACT_USE, player)) {
					Utils.sendPluginMessage(player, Messages.notPermissionMessage);
					return;
				}
				MaterialData data = block.getState().getData();
				if (isPowered(data) || isOpen(data)) {
					return;
				}
				new ScriptManager(plugin, location, ScriptType.INTERACT).scriptExec(player);
			}
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
				ScriptType scriptType;
				if (player.isSneaking()) {
					scriptType = ScriptType.WALK;
				} else {
					scriptType = ScriptType.INTERACT;
				}
				new ScriptFileManager(plugin, location, scriptType).scriptCopy(player);
				event.setCancelled(true);
				return true;
			case RIGHT_CLICK_BLOCK:
				if (player.isSneaking()) {
					ScriptFileManager fileManager = SBMetadata.getScriptFile().get(player);
					if (fileManager == null) {
						Utils.sendPluginMessage(player, Messages.getErrorScriptFileCheckMessage());
						break;
					}
					fileManager.scriptPaste(player, location);
				} else {
					new ScriptFileManager(plugin, location, ScriptType.BREAK).scriptCopy(player);
				}
				event.setCancelled(true);
				return true;
			default:
				SBMetadata.removeAll(player, Metadata.PLAYERCLICK, Metadata.SCRIPTTEXT);
				return false;
			}
		}
		PlayerClick playerClick = SBMetadata.getPlayerClick();
		for (ClickType type : ClickType.values()) {
			if (playerClick.has(player, type)) {
				String[] split = StringUtils.split(type.name(), "_");
				if (clickScript(player, split[1], location, type, ScriptType.valueOf(split[0]))) {
					event.setCancelled(true);
					return true;
				}
			}
		}
		return false;
	}

	private boolean clickScript(Player player, String type, BlockLocation location, ClickType clickType, ScriptType scriptType) {
		ScriptFileManager scriptFileManager = new ScriptFileManager(plugin, location, scriptType);
		switch (type) {
		case "CREATE":
			scriptFileManager.scriptCreate(player, SBMetadata.getScriptText().get(player, clickType));
			return true;
		case "ADD":
			scriptFileManager.scriptAdd(player, SBMetadata.getScriptText().get(player, clickType));
			return true;
		case "REMOVE":
			scriptFileManager.scriptRemove(player);
			return true;
		case "VIEW":
			scriptFileManager.scriptView(player);
			return true;
		default:
			return false;
		}
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

	private boolean isHand(Enum<?> equipmentSlot) {
		if (equipmentSlot == null) {
			return false;
		}
		return equipmentSlot.name().equals("HAND");
	}
}