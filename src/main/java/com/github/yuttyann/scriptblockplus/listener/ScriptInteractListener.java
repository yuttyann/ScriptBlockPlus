package com.github.yuttyann.scriptblockplus.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.material.Button;
import org.bukkit.material.Door;
import org.bukkit.material.Lever;
import org.bukkit.material.MaterialData;
import org.bukkit.material.TrapDoor;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.ClickType;
import com.github.yuttyann.scriptblockplus.enums.Metadata;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.event.BlockInteractEvent;
import com.github.yuttyann.scriptblockplus.event.ScriptBlockInteractEvent;
import com.github.yuttyann.scriptblockplus.file.Lang;
import com.github.yuttyann.scriptblockplus.manager.ScriptManager;
import com.github.yuttyann.scriptblockplus.metadata.PlayerClick;
import com.github.yuttyann.scriptblockplus.script.ScriptEdit;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class ScriptInteractListener extends ScriptManager implements Listener {

	public ScriptInteractListener(ScriptBlock plugin) {
		super(plugin, ScriptType.INTERACT);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockInteract(BlockInteractEvent event) {
		Block block = event.getBlock();
		BlockCoords blockCoords = new BlockCoords(block.getLocation());
		if (!setting(event, blockCoords)) {
			if (Utils.isCB19orLater() && !isHand(event.getHand())) {
				return;
			}
			Player player = event.getPlayer();
			if (mapManager.containsLocation(blockCoords, scriptType)) {
				ScriptBlockInteractEvent interactEvent = new ScriptBlockInteractEvent(player, block, event.getItem(), blockCoords);
				Utils.callEvent(interactEvent);
				if (interactEvent.isCancelled()) {
					return;
				}
				if (!interactEvent.getLeftClick() && event.getAction() == Action.LEFT_CLICK_BLOCK) {
					return;
				}
				if (!Permission.has(Permission.SCRIPTBLOCKPLUS_INTERACT_USE, player)) {
					Utils.sendPluginMessage(player, Lang.getNotPermissionMessage());
					return;
				}
				MaterialData data = block.getState().getData();
				if (isPowered(data) || isOpen(data)) {
					return;
				}
				new ScriptRead(this, player, blockCoords).read(0);
			}
		}
	}

	private boolean setting(BlockInteractEvent event, Location location) {
		Player player = event.getPlayer();
		if (Utils.checkItem(event.getItem(), Material.BLAZE_ROD, Lang.ITEM_SCRIPTEDITOR)) {
			if (!Permission.has(Permission.SCRIPTBLOCKPLUS_TOOL_SCRIPTEDITOR, player)) {
				Utils.sendPluginMessage(player, Lang.getNotPermissionMessage());
				return false;
			}
			switch (event.getAction()) {
			case LEFT_CLICK_BLOCK:
				ScriptType scriptType;
				if (player.isSneaking()) {
					scriptType = ScriptType.WALK;
				} else {
					scriptType = ScriptType.INTERACT;
				}
				new ScriptEdit(location, scriptType).copy(player);
				event.setCancelled(true);
				return true;
			case RIGHT_CLICK_BLOCK:
				if (player.isSneaking()) {
					ScriptEdit scriptEdit = Metadata.getScriptFile().get(player);
					if (scriptEdit == null) {
						Utils.sendPluginMessage(player, Lang.getErrorScriptFileCheckMessage());
						break;
					}
					scriptEdit.paste(player, location);
				} else {
					new ScriptEdit(location, ScriptType.BREAK).copy(player);
				}
				event.setCancelled(true);
				return true;
			default:
				Metadata.removeAll(player, Metadata.PLAYERCLICK, Metadata.SCRIPTTEXT);
				return false;
			}
		}
		PlayerClick playerClick = Metadata.getPlayerClick();
		for (ClickType type : ClickType.values()) {
			if (playerClick.has(player, type)) {
				String[] split = StringUtils.split(type.name(), "_");
				if (click(player, split[1], location, type, ScriptType.getType(split[0]))) {
					event.setCancelled(true);
					return true;
				}
			}
		}
		return false;
	}

	private boolean click(Player player, String type, Location location, ClickType clickType, ScriptType scriptType) {
		ScriptEdit scriptEdit = new ScriptEdit(location, scriptType);
		switch (type) {
		case "CREATE":
			scriptEdit.create(player, Metadata.getScriptText().get(player, clickType));
			return true;
		case "ADD":
			scriptEdit.add(player, Metadata.getScriptText().get(player, clickType));
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