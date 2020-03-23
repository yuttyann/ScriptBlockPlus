package com.github.yuttyann.scriptblockplus.listener;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.event.BlockInteractEvent;
import com.github.yuttyann.scriptblockplus.event.ScriptBlockEditEvent;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.listener.raytrace.RayResult;
import com.github.yuttyann.scriptblockplus.listener.raytrace.RayTrace;
import com.github.yuttyann.scriptblockplus.player.ObjectMap;
import com.github.yuttyann.scriptblockplus.player.PlayerData;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.region.CuboidRegion;
import com.github.yuttyann.scriptblockplus.script.ScriptEdit;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.utils.ItemUtils;
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
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class InteractListener implements Listener {

	private static final String KEY_FLAG = PlayerData.createRandomId("InteractFlag");

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
			callEvent(interactEvent, new BlockInteractEvent(interactEvent, EquipmentSlot.HAND, true));
		} else {
			Block block = rayResult.getHitBlock();
			BlockFace blockFace = rayResult.getHitBlockFace();
			PlayerInteractEvent interactEvent = new PlayerInteractEvent(player, Action.LEFT_CLICK_BLOCK, item, block, blockFace);
			callEvent(interactEvent, new BlockInteractEvent(interactEvent, EquipmentSlot.HAND, true));
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
		callEvent(event, new BlockInteractEvent(event, null, false));
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerGameModeChangeEvent(PlayerGameModeChangeEvent event) {
		if (event.getNewGameMode() != GameMode.ADVENTURE) {
			SBPlayer.fromPlayer(event.getPlayer()).getObjectMap().put(KEY_FLAG, false);
		}
	}

	private void callEvent(@NotNull PlayerInteractEvent interactEvent, @NotNull BlockInteractEvent blockInteractEvent) {
		Player player = interactEvent.getPlayer();
		ItemStack item = interactEvent.getItem();
		blockInteractEvent.setInvalid(action(player, blockInteractEvent.getAction(), blockInteractEvent));
		Bukkit.getPluginManager().callEvent(blockInteractEvent);
		if (blockInteractEvent.isCancelled()
				|| ItemUtils.isBlockSelector(item) && Permission.TOOL_BLOCKSELECTOR.has(player)
					|| ItemUtils.isScriptEditor(item) && Permission.TOOL_SCRIPTEDITOR.has(player)) {
			interactEvent.setCancelled(true);
		}
	}

	private boolean action(@NotNull Player player, @NotNull Action action, @NotNull BlockInteractEvent event) {
		Location location = event.getLocation();
		if (event.getHand() != EquipmentSlot.HAND || location == null) {
			return false;
		}
		ItemStack item = event.getItem();
		SBPlayer sbPlayer = SBPlayer.fromPlayer(player);
		boolean isAIR = action.name().endsWith("_CLICK_AIR");
		boolean isSneaking = player.isSneaking();
		if (ItemUtils.isBlockSelector(item) && Permission.TOOL_SCRIPTEDITOR.has(player)) {
			CuboidRegion region = ((CuboidRegion) sbPlayer.getRegion());
			tool(action, location
			, left -> {
				if (isSneaking) {
					region.setPos1((left = player.getLocation()).toVector());
				} else if (!isAIR) {
					region.setPos1((left.toVector()));
				}
				if (left != null) {
					region.setWorld(left.getWorld());
					SBConfig.SELECTOR_POS1.replace(region.getName(), BlockCoords.getCoords(left)).send(sbPlayer);
				}
			}, right -> {
				if (isSneaking) {
					region.setPos2((right = player.getLocation()).toVector());
				} else if (!isAIR) {
					region.setPos2((right.toVector()));
				}
				if (right != null) {
					region.setWorld(right.getWorld());
					SBConfig.SELECTOR_POS2.replace(region.getName(), BlockCoords.getCoords(right)).send(sbPlayer);
				}
			});
			return true;
		} else if (ItemUtils.isScriptEditor(item) && Permission.TOOL_SCRIPTEDITOR.has(player)) {
			tool(action, location
			, left -> {
				if (isSneaking && !isAIR) {
					new ScriptEdit(ItemUtils.getScriptType(item)).remove(sbPlayer, left);
				} else if (!isSneaking) {
					item.setItemMeta(ItemUtils.getScriptEditor(getNextScriptType(item)).getItemMeta());
					Utils.updateInventory(player);
				}
			}, right -> {
				if (isSneaking && !isAIR) {
					if (!sbPlayer.getClipboard().isPresent() || !sbPlayer.getClipboard().get().paste(right, true)) {
						SBConfig.ERROR_SCRIPT_FILE_CHECK.send(sbPlayer);
					}
				} else if (!isSneaking && !isAIR) {
					new ScriptEdit(ItemUtils.getScriptType(item)).clipboard(sbPlayer, right).copy();
				}
			});
			return true;
		} else if (!isAIR && sbPlayer.getActionType().isPresent()) {
			String[] array = StringUtils.split(sbPlayer.getActionType().get(), "_");
			ScriptBlockEditEvent editEvent = new ScriptBlockEditEvent(player, location.getBlock(), array);
			Bukkit.getPluginManager().callEvent(editEvent);
			if (editEvent.isCancelled()) {
				return false;
			}
			ScriptEdit scriptEdit = new ScriptEdit(editEvent.getScriptType());
			switch (editEvent.getActionType()) {
			case CREATE:
				scriptEdit.create(sbPlayer, location);
				return true;
			case ADD:
				scriptEdit.add(sbPlayer, location);
				return true;
			case REMOVE:
				scriptEdit.remove(sbPlayer, location);
				return true;
			case VIEW:
				scriptEdit.view(sbPlayer, location);
				return true;
			}
		}
		return isAIR;
	}

	private void tool(@NotNull Action action, @NotNull Location location, @NotNull Consumer<Location> left, @NotNull Consumer<Location> right) {
		switch (action) {
		case LEFT_CLICK_AIR:
		case LEFT_CLICK_BLOCK:
			left.accept(location);
			break;
		case RIGHT_CLICK_AIR:
		case RIGHT_CLICK_BLOCK:
			right.accept(location);
			break;
		default:
		}
	}

	@NotNull
	private ScriptType getNextScriptType(@NotNull ItemStack item) {
		try {
			return ScriptType.valueOf(ItemUtils.getScriptType(item).ordinal() + 1);
		} catch (Exception e) {
			return ScriptType.INTERACT;
		}
	}
}