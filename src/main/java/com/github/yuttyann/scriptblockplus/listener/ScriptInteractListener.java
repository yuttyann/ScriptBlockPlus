package com.github.yuttyann.scriptblockplus.listener;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.event.BlockClickEvent;
import com.github.yuttyann.scriptblockplus.event.ScriptBlockInteractEvent;
import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.script.option.other.ScriptAction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.material.Openable;
import org.bukkit.material.Redstone;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus ScriptInteractListener クラス
 * @author yuttyann44581
 */
public class ScriptInteractListener extends ScriptListener {

	public ScriptInteractListener(@NotNull ScriptBlock plugin) {
		super(plugin, ScriptType.INTERACT);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockClickEvent(BlockClickEvent event) {
		Block block = event.getBlock();
		if (event.isInvalid() || event.getHand() != EquipmentSlot.HAND || block == null) {
			return;
		}
		Location location = block.getLocation();
		if (Files.hasScriptCoords(location, scriptType)) {
			Player player = event.getPlayer();
			Action action = event.getAction();
			ScriptBlockInteractEvent interactEvent = new ScriptBlockInteractEvent(player, block, action);
			Bukkit.getPluginManager().callEvent(interactEvent);
			if (interactEvent.isCancelled()
					|| action == Action.LEFT_CLICK_BLOCK && !SBConfig.ACTIONS_INTERACT_LEFT.getValue()
					|| action == Action.RIGHT_CLICK_BLOCK && !SBConfig.ACTIONS_INTERACT_RIGHT.getValue()
					|| isPowered(block) || isOpen(block)) {
				return;
			}
			if (!Permission.has(player, ScriptType.INTERACT, false)) {
				SBConfig.NOT_PERMISSION.send(player);
				return;
			}
			ScriptRead scriptRead = new ScriptRead(player, location, this);
			scriptRead.put(ScriptAction.KEY, action);
			scriptRead.read(0);
		}
	}

	private boolean isPowered(Block block) {
		Object data = block.getState().getData();
		return data instanceof Redstone && ((Redstone) data).isPowered();
	}

	private boolean isOpen(Block block) {
		Object data = block.getState().getData();
		return data instanceof Openable && ((Openable) data).isOpen();
	}
}