package com.github.yuttyann.scriptblockplus.listener;

import com.github.yuttyann.scriptblockplus.event.BlockClickEvent;
import com.github.yuttyann.scriptblockplus.event.ScriptBlockEditEvent;
import com.github.yuttyann.scriptblockplus.listener.item.ItemAction;
import com.github.yuttyann.scriptblockplus.listener.item.action.BlockSelector;
import com.github.yuttyann.scriptblockplus.listener.item.action.ScriptViewer;
import com.github.yuttyann.scriptblockplus.listener.raytrace.RayResult;
import com.github.yuttyann.scriptblockplus.listener.raytrace.RayTrace;
import com.github.yuttyann.scriptblockplus.player.ObjectMap;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.ScriptEdit;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus InteractListener クラス
 * @author yuttyann44581
 */
public class InteractListener implements Listener {

	private static final String KEY_FLAG = Utils.randomUUID();

	static {
		new ScriptViewer().put();
		new BlockSelector().put();
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerAnimationEvent(PlayerAnimationEvent event) {
		Player player = event.getPlayer();
		if (event.getAnimationType() != PlayerAnimationType.ARM_SWING || player.getGameMode() != GameMode.ADVENTURE) {
			return;
		}
		ObjectMap objectMap = SBPlayer.fromPlayer(player).getObjectMap();
		if (objectMap.has(KEY_FLAG) && objectMap.getBoolean(KEY_FLAG)) {
			objectMap.put(KEY_FLAG, false);
			return;
		}
		ItemStack item = player.getInventory().getItemInMainHand();
		RayResult rayResult = new RayTrace(player.getWorld()).rayTrace(player, 4.5D);
		if (rayResult == null) {
			PlayerInteractEvent interactEvent = new PlayerInteractEvent(player, Action.LEFT_CLICK_AIR, item, null, BlockFace.SOUTH);
			callEvent(interactEvent, new BlockClickEvent(interactEvent, EquipmentSlot.HAND, true));
		} else {
			Block block = rayResult.getHitBlock();
			BlockFace blockFace = rayResult.getHitBlockFace();
			PlayerInteractEvent interactEvent = new PlayerInteractEvent(player, Action.LEFT_CLICK_BLOCK, item, block, blockFace);
			callEvent(interactEvent, new BlockClickEvent(interactEvent, EquipmentSlot.HAND, true));
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		Action action = event.getAction();
		if (action == Action.PHYSICAL) {
			return;
		}
		Player player = event.getPlayer();
		if (player.getGameMode() == GameMode.ADVENTURE) {
			if (action.name().startsWith("LEFT_CLICK_")) {
				return;
			}
			ObjectMap objectMap = SBPlayer.fromPlayer(player).getObjectMap();
			if (action == Action.RIGHT_CLICK_BLOCK && (!objectMap.has(KEY_FLAG) || !objectMap.getBoolean(KEY_FLAG))) {
				objectMap.put(KEY_FLAG, true);
			}
		}
		callEvent(event, new BlockClickEvent(event, null, false));
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.ADVENTURE) {
			SBPlayer.fromPlayer(event.getPlayer()).getObjectMap().put(KEY_FLAG, true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerGameModeChangeEvent(PlayerGameModeChangeEvent event) {
		if (event.getNewGameMode() != GameMode.ADVENTURE) {
			SBPlayer.fromPlayer(event.getPlayer()).getObjectMap().put(KEY_FLAG, false);
		}
	}

	private void callEvent(@NotNull PlayerInteractEvent interactEvent, @NotNull BlockClickEvent blockInteractEvent) {
		Player player = interactEvent.getPlayer();
		boolean action;
		try {
			action = action(player, blockInteractEvent.getAction(), blockInteractEvent);
		} catch (Exception e) {
			action = false;
		}
		blockInteractEvent.setInvalid(action);
		Bukkit.getPluginManager().callEvent(blockInteractEvent);
		if (blockInteractEvent.isCancelled() || ItemAction.has(player, interactEvent.getItem(), true)) {
			interactEvent.setCancelled(true);
		}
	}

	private boolean action(@NotNull Player player, @NotNull Action action, @NotNull BlockClickEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) {
			return false;
		}
		SBPlayer sbPlayer = SBPlayer.fromPlayer(player);
		Location location = event.getLocation();
		boolean isAIR = action.name().endsWith("_CLICK_AIR");
		if(ItemAction.run(event.getItem(), player, action, location, isAIR, player.isSneaking())) {
			return true;
		}
		if (location != null && !isAIR && sbPlayer.getActionType().isPresent()) {
			String[] array = StringUtils.split(sbPlayer.getActionType().get(), "_");
			ScriptBlockEditEvent editEvent = new ScriptBlockEditEvent(player, location.getBlock(), array);
			try {
				Bukkit.getPluginManager().callEvent(editEvent);
				if (editEvent.isCancelled()) {
					return false;
				}
				ScriptEdit scriptEdit = new ScriptEdit(editEvent.getScriptType());
				switch (editEvent.getActionType()) {
					case CREATE:
						scriptEdit.create(sbPlayer, location);
						break;
					case ADD:
						scriptEdit.add(sbPlayer, location);
						break;
					case REMOVE:
						scriptEdit.remove(sbPlayer, location);
						break;
					case VIEW:
						scriptEdit.view(sbPlayer, location);
						break;
				}
				return true;
			} finally {
				sbPlayer.setScriptLine(null);
				sbPlayer.setActionType(null);
			}
		}
		return false;
	}
}