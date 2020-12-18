package com.github.yuttyann.scriptblockplus.listener;

import com.github.yuttyann.scriptblockplus.event.BlockClickEvent;
import com.github.yuttyann.scriptblockplus.listener.item.ItemAction;
import com.github.yuttyann.scriptblockplus.listener.raytrace.RayResult;
import com.github.yuttyann.scriptblockplus.listener.raytrace.RayTrace;
import com.github.yuttyann.scriptblockplus.player.ObjectMap;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.ScriptEdit;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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
		RayResult ray = new RayTrace(player.getWorld()).rayTrace(player, 4.5D);
		ItemStack item = player.getInventory().getItemInMainHand();
		EquipmentSlot hand = EquipmentSlot.HAND;
		if (ray == null) {
			callEvent(new PlayerInteractEvent(player, Action.LEFT_CLICK_AIR, item, null, BlockFace.SOUTH, hand), true);
		} else {
			Block block = ray.getHitBlock();
			BlockFace blockFace = ray.getHitBlockFace();
			callEvent(new PlayerInteractEvent(player, Action.LEFT_CLICK_BLOCK, item, block, blockFace, hand), true);
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
			if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
				return;
			}
			ObjectMap objectMap = SBPlayer.fromPlayer(player).getObjectMap();
			if (action == Action.RIGHT_CLICK_BLOCK && (!objectMap.has(KEY_FLAG) || !objectMap.getBoolean(KEY_FLAG))) {
				objectMap.put(KEY_FLAG, true);
			}
		}
		callEvent(event, false);
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

	private void callEvent(@NotNull PlayerInteractEvent interactEvent, final boolean isAnimation) {
		Player player = interactEvent.getPlayer();
		BlockClickEvent blockEvent = new BlockClickEvent(interactEvent, isAnimation);
		if (blockEvent.getHand() == EquipmentSlot.HAND) {
			boolean invalid = false;
			if (ItemAction.callRun(player, blockEvent.getItem(), blockEvent.getLocation(), blockEvent.getAction())) {
				invalid = true;
			} else if (blockEvent.getAction().name().endsWith("_CLICK_BLOCK")) {
				invalid = new ScriptEdit(SBPlayer.fromPlayer(player)).perform(blockEvent.getLocation());
			}
			blockEvent.setInvalid(invalid);
		}
		Bukkit.getPluginManager().callEvent(blockEvent);
		if (blockEvent.isCancelled() || ItemAction.has(player, blockEvent.getItem(), true)) {
			interactEvent.setCancelled(true);
		}
	}
}