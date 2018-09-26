package com.github.yuttyann.scriptblockplus.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.material.Openable;
import org.bukkit.material.Redstone;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.EquipSlot;
import com.github.yuttyann.scriptblockplus.event.BlockInteractEvent;
import com.github.yuttyann.scriptblockplus.event.ScriptBlockInteractEvent;
import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.script.ScriptType.SBPermission;
import com.github.yuttyann.scriptblockplus.script.option.other.ScriptAction;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class ScriptInteractListener extends IAssist {

	public ScriptInteractListener(ScriptBlock plugin) {
		super(plugin, ScriptType.INTERACT);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockInteractEvent(BlockInteractEvent event) {
		if (event.isInvalid() || event.getHand() != EquipSlot.HAND) {
			return;
		}
		Block block = event.getBlock();
		Location location = block.getLocation();
		if (mapManager.containsCoords(scriptType, location)) {
			Player player = event.getPlayer();
			Action action = event.getAction();
			ScriptBlockInteractEvent interactEvent = new ScriptBlockInteractEvent(player, block, action);
			Bukkit.getPluginManager().callEvent(interactEvent);
			if (interactEvent.isCancelled()
					|| action == Action.LEFT_CLICK_BLOCK && !SBConfig.isLeftClick()
					|| action == Action.RIGHT_CLICK_BLOCK && !SBConfig.isRightClick()
					|| isPowered(block) || isOpen(block)) {
				return;
			}
			if (!SBPermission.has(player, ScriptType.INTERACT, false)) {
				Utils.sendMessage(player, SBConfig.getNotPermissionMessage());
				return;
			}
			ScriptRead scriptRead = new ScriptRead(this, player, location);
			scriptRead.put(ScriptAction.KEY_ENUM_ACTION, action);
			scriptRead.read(0);
		}
	}

	private boolean isPowered(Block block) {
		Object data = block.getState().getData();
		return data instanceof Redstone ? ((Redstone) data).isPowered() : false;
	}

	private boolean isOpen(Block block) {
		Object data = block.getState().getData();
		return data instanceof Openable ? ((Openable) data).isOpen() : false;
	}
}