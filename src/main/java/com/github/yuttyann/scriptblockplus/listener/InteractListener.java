package com.github.yuttyann.scriptblockplus.listener;

import java.util.function.Consumer;

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
import org.bukkit.inventory.ItemStack;

import com.github.yuttyann.scriptblockplus.enums.EquipSlot;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.event.BlockInteractEvent;
import com.github.yuttyann.scriptblockplus.event.ScriptBlockEditEvent;
import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.listener.nms.MathHelper;
import com.github.yuttyann.scriptblockplus.listener.nms.MovingPosition;
import com.github.yuttyann.scriptblockplus.listener.nms.NMSWorld;
import com.github.yuttyann.scriptblockplus.listener.nms.Vec3D;
import com.github.yuttyann.scriptblockplus.player.ObjectMap;
import com.github.yuttyann.scriptblockplus.player.PlayerData;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.region.CuboidRegion;
import com.github.yuttyann.scriptblockplus.script.ScriptEdit;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.utils.ItemUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

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
		Location location = player.getLocation();
		double x = location.getX();
		double y = location.getY() + player.getEyeHeight();
		double z = location.getZ();
		float pitch = location.getPitch();
		float yaw = location.getYaw();
		float f1 = MathHelper.cos(-yaw * 0.017453292F - 3.1415927F);
		float f2 = MathHelper.sin(-yaw * 0.017453292F - 3.1415927F);
		float f3 = -MathHelper.cos(-pitch * 0.017453292F);
		float f4 = MathHelper.sin(-pitch * 0.017453292F);
		float f5 = f2 * f3;
		float f6 = f1 * f3;
		Vec3D vec3d1 = new Vec3D(x, y, z);
		Vec3D vec3d2 = vec3d1.add(f5 * 4.5D, f4 * 4.5D, f6 * 4.5D);
		MovingPosition movingPosition = new NMSWorld(player.getWorld()).rayTrace(vec3d1, vec3d2);
		if (movingPosition != null) {
			Action action = Action.LEFT_CLICK_BLOCK;
			BlockFace blockFace = movingPosition.getFace();
			Block block = movingPosition.getBlock(player.getWorld());
			ItemStack item = ItemUtils.getItemInMainHand(player);
			PlayerInteractEvent interactEvent = new PlayerInteractEvent(player, action, item, block, blockFace);
			BlockInteractEvent blockInteractEvent = new BlockInteractEvent(interactEvent, EquipSlot.HAND, true);
			callEvent(action, interactEvent, blockInteractEvent);
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
			if (action == Action.RIGHT_CLICK_BLOCK) {
				ObjectMap objectMap = SBPlayer.fromPlayer(player).getObjectMap();
				if (objectMap.has(KEY_FLAG) && !objectMap.getBoolean(KEY_FLAG)) {
					objectMap.put(KEY_FLAG, true);
				}
			}
		}
		BlockInteractEvent blockInteractEvent = new BlockInteractEvent(event, null, false);
		callEvent(action, event, blockInteractEvent);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerGameModeChangeEvent(PlayerGameModeChangeEvent event) {
		if (event.getNewGameMode() != GameMode.ADVENTURE) {
			SBPlayer.fromPlayer(event.getPlayer()).getObjectMap().put(KEY_FLAG, false);
		}
	}

	private void callEvent(Action action, PlayerInteractEvent interactEvent, BlockInteractEvent blockInteractEvent) {
		Player player = interactEvent.getPlayer();
		boolean actionResult = action(player, action, blockInteractEvent);
		if (actionResult) {
			blockInteractEvent.setCancelled(actionResult);
			Bukkit.getPluginManager().callEvent(blockInteractEvent);
		} else {
			Bukkit.getPluginManager().callEvent(blockInteractEvent);
			interactEvent.setCancelled(blockInteractEvent.isCancelled());
		}
	}

	private boolean action(Player player, Action action, BlockInteractEvent event) {
		if (event.getHand() != EquipSlot.HAND) {
			return false;
		}
		boolean isAIR = isAIR(action);
		boolean isSneaking = player.isSneaking();
		ItemStack item = event.getItem();
		Location location = event.getLocation();
		SBPlayer sbPlayer = SBPlayer.fromPlayer(player);
		if (ItemUtils.isBlockSelector(player, item) && Permission.TOOL_SCRIPTEDITOR.has(player)) {
			CuboidRegion region = ((CuboidRegion) sbPlayer.getRegion());
			toolAction(action, location, left -> {
				if (isSneaking || (!isAIR && !isSneaking)) {
					if (isSneaking) {
						region.setPos1((left = player.getLocation()).toVector());
					} else if (!isAIR && !isSneaking) {
						region.setPos1(left.toVector());
					}
					region.setWorld(left.getWorld());
					Utils.sendMessage(sbPlayer, SBConfig.getSelectorPos1Message(left));
				}
			}, right -> {
				if (isSneaking || (!isAIR && !isSneaking)) {
					if (isSneaking) {
						region.setPos2((right = player.getLocation()).toVector());
					} else if (!isAIR && !isSneaking) {
						region.setPos2(right.toVector());
					}
					region.setWorld(right.getWorld());
					Utils.sendMessage(sbPlayer, SBConfig.getSelectorPos2Message(right));
				}
			});
			return true;
		} else if (ItemUtils.isScriptEditor(player, item) && Permission.TOOL_SCRIPTEDITOR.has(player)) {
			toolAction(action, location, left -> {
				if (!isAIR && isSneaking) {
					new ScriptEdit(getScriptType(item)).remove(sbPlayer, left);
				} else if (!isSneaking) {
					item.setItemMeta(ItemUtils.getScriptEditor(getNextScriptType(item)).getItemMeta());
					Utils.updateInventory(player);
				}
			}, right -> {
				if (!isAIR && isSneaking) {
					if (!sbPlayer.hasClipboard() || !sbPlayer.getClipboard().paste(right, true)) {
						Utils.sendMessage(player, SBConfig.getErrorScriptFileCheckMessage());
					}
				} else if (!isAIR && !isSneaking) {
					new ScriptEdit(getScriptType(item)).copy(sbPlayer, right);
				}
			});
			return true;
		} else if (!isAIR && sbPlayer.hasActionType()) {
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
		return isAIR;
	}

	private void toolAction(Action action, Location location, Consumer<Location> left, Consumer<Location> right) {
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

	private boolean isAIR(Action action) {
		return action.name().endsWith("_CLICK_AIR");
	}

	private ScriptType getNextScriptType(ItemStack item) {
		try {
			return ScriptType.valueOf(getScriptType(item).ordinal() + 1);
		} catch (Exception e) {
			return ScriptType.INTERACT;
		}
	}

	private ScriptType getScriptType(ItemStack item) {
		String name = StringUtils.removeStart(ItemUtils.getName(item, null), "§dScript Editor§6[Mode: ");
		return ScriptType.valueOf(name.substring(0, name.length() - 1));
	}
}