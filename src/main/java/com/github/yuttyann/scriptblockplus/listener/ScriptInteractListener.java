package com.github.yuttyann.scriptblockplus.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Button;
import org.bukkit.material.Door;
import org.bukkit.material.Lever;
import org.bukkit.material.TrapDoor;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.EquipSlot;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.event.BlockInteractEvent;
import com.github.yuttyann.scriptblockplus.event.ScriptBlockEditEvent;
import com.github.yuttyann.scriptblockplus.event.ScriptBlockInteractEvent;
import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.ScriptEdit;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.script.ScriptType.SBPermission;
import com.github.yuttyann.scriptblockplus.script.hook.HookPlugins;
import com.github.yuttyann.scriptblockplus.script.option.other.ScriptAction;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class ScriptInteractListener extends IAssist {

	public ScriptInteractListener(ScriptBlock plugin) {
		super(plugin, ScriptType.INTERACT);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockInteractEvent(BlockInteractEvent event) {
		if (event.getHand() != EquipSlot.HAND) {
			return;
		}
		Block block = event.getBlock();
		Action action = event.getAction();
		Location location = block.getLocation();
		if (action(event.getPlayer(), action, event.getItem(), location)) {
			event.setCancelled(true);
			return;
		}
		Player player = event.getPlayer();
		if (mapManager.containsCoords(scriptType, location)) {
			ScriptBlockInteractEvent interactEvent = new ScriptBlockInteractEvent(player, block, action);
			Bukkit.getPluginManager().callEvent(interactEvent);
			if (interactEvent.isCancelled()
					|| (action == Action.LEFT_CLICK_BLOCK && !SBConfig.isLeftClick())
					|| (action == Action.RIGHT_CLICK_BLOCK && !SBConfig.isRightClick())
					|| isPowered(block) || isOpen(block)) {
				return;
			}
			if (!SBPermission.has(player, ScriptType.INTERACT, false)) {
				Utils.sendMessage(player, SBConfig.getNotPermissionMessage());
				return;
			}
			ScriptRead scriptRead = new ScriptRead(this, player, location);
			scriptRead.setData(ScriptAction.KEY_ENUM_ACTION, action);
			scriptRead.read(0);
		}
	}

	private boolean action(Player player, Action action, ItemStack item, Location location) {
		if (isWorldEditWand(player, item)) {
			return true;
		}
		SBPlayer sbPlayer = SBPlayer.fromPlayer(player);
		if (isScriptEditor(player, item) && Permission.TOOL_SCRIPTEDITOR.has(player)) {
			switch (action) {
			case LEFT_CLICK_BLOCK:
				if (player.isSneaking()) {
					new ScriptEdit(getScriptType(item)).remove(sbPlayer, location);
				} else {
					ScriptType scriptType = getScriptType(item);
					int ordinal = scriptType.ordinal() < ScriptType.size() ? scriptType.ordinal() : 0;
					item.setItemMeta(Utils.getScriptEditor(ScriptType.valueOf(ordinal)).getItemMeta());
					Utils.updateInventory(player);
				}
				return true;
			case RIGHT_CLICK_BLOCK:
				if (player.isSneaking()) {
					if (!sbPlayer.hasClipboard() || !sbPlayer.getClipboard().paste(location, true)) {
						Utils.sendMessage(player, SBConfig.getErrorScriptFileCheckMessage());
					}
				} else {
					new ScriptEdit(getScriptType(item)).copy(sbPlayer, location);
				}
				return true;
			default:
				sbPlayer.setScriptLine(null);
				sbPlayer.setActionType(null);
				return false;
			}
		}
		if (sbPlayer.hasActionType()) {
			String[] array = StringUtils.split(sbPlayer.getActionType(), "_");
			ScriptBlockEditEvent editEvent = new ScriptBlockEditEvent(player, location.getBlock(), array);
			Bukkit.getPluginManager().callEvent(editEvent);
			if (editEvent.isCancelled()) {
				return false;
			}
			ScriptEdit scriptEdit = new ScriptEdit(editEvent.getScriptType());
			switch (editEvent.getActionType()) {
			case CREATE:
				scriptEdit.create(sbPlayer, location, sbPlayer.getScriptLine());
				return true;
			case ADD:
				scriptEdit.add(sbPlayer, location, sbPlayer.getScriptLine());
				return true;
			case REMOVE:
				scriptEdit.remove(sbPlayer, location);
				return true;
			case VIEW:
				scriptEdit.view(sbPlayer, location);
				return true;
			}
		}
		return false;
	}

	private ScriptType getScriptType(ItemStack item) {
		String name = Utils.getItemName(item, null);
		return ScriptType.valueOf(name.substring(name.indexOf("§dScript Editor§6[Mode: "), name.lastIndexOf("]")));
	}

	private boolean isWorldEditWand(Player player, ItemStack item) {
		if (!HookPlugins.hasWorldEdit() || !Permission.has(player, "worldedit.selection.pos")) {
			return false;
		}
		return item == null ? false : item.getType() == HookPlugins.getWorldEditSelection().getWandType();
	}

	private boolean isScriptEditor(Player player, ItemStack item) {
		String name = Utils.getItemName(item, null);
		return name != null && item.getType() == Material.BLAZE_ROD && name.startsWith("§dScript Editor§6[Mode: ");
	}

	private boolean isPowered(Block block) {
		Object data = block.getState().getData();
		if (data instanceof Button) {
			return ((Button) data).isPowered();
		}
		if (data instanceof Lever) {
			return ((Lever) data).isPowered();
		}
		return false;
	}

	private boolean isOpen(Block block) {
		Object data = block.getState().getData();
		if (data instanceof Door) {
			return ((Door) data).isOpen();
		}
		if (data instanceof TrapDoor) {
			return ((TrapDoor) data).isOpen();
		}
		return false;
	}
}