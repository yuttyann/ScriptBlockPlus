package com.github.yuttyann.scriptblockplus.listener;

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
import com.github.yuttyann.scriptblockplus.event.BlockInteractEvent;
import com.github.yuttyann.scriptblockplus.listener.nms.MathHelper;
import com.github.yuttyann.scriptblockplus.listener.nms.MovingPosition;
import com.github.yuttyann.scriptblockplus.listener.nms.NMSWorld;
import com.github.yuttyann.scriptblockplus.listener.nms.Vec3D;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class InteractListener implements Listener {

	private static final String KEY_FLAG = "Key_InteractFlag";

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerAnimationEvent(PlayerAnimationEvent event) {
		Player player = event.getPlayer();
		if (event.getAnimationType() != PlayerAnimationType.ARM_SWING || player.getGameMode() != GameMode.ADVENTURE) {
			return;
		}
		SBPlayer sbPlayer = SBPlayer.get(player);
		if (sbPlayer.hasData(KEY_FLAG) ? sbPlayer.getData(KEY_FLAG, Boolean.class) : false) {
			sbPlayer.setData(KEY_FLAG, false);
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
		MovingPosition movingPosition = new NMSWorld(player.getWorld()).rayTrace(vec3d1, vec3d2, false);
		if (movingPosition != null) {
			Action action = Action.LEFT_CLICK_BLOCK;
			BlockFace blockFace = movingPosition.getFace();
			Block block = movingPosition.getBlock(player.getWorld());
			ItemStack item = Utils.getItemInMainHand(player);
			PlayerInteractEvent interactEvent = new PlayerInteractEvent(player, action, item, block, blockFace);
			BlockInteractEvent blockInteractEvent = new BlockInteractEvent(interactEvent, EquipSlot.HAND, true);
			Bukkit.getPluginManager().callEvent(blockInteractEvent);
			if (blockInteractEvent.isCancelled()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		Action action = event.getAction();
		if (action == Action.PHYSICAL || event.getClickedBlock() == null) {
			return;
		}
		Player player = event.getPlayer();
		if (player.getGameMode() == GameMode.ADVENTURE) {
			if (action.name().startsWith("LEFT_CLICK_")) {
				return;
			}
			if (action == Action.RIGHT_CLICK_BLOCK) {
				SBPlayer sbPlayer = SBPlayer.get(player);
				if (sbPlayer.hasData(KEY_FLAG) ? !sbPlayer.getData(KEY_FLAG, Boolean.class) : true) {
					sbPlayer.setData(KEY_FLAG, true);
				}
			}
		}
		BlockInteractEvent interactEvent = new BlockInteractEvent(event, null, false);
		Bukkit.getPluginManager().callEvent(interactEvent);
		if (interactEvent.isCancelled()) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerGameModeChangeEvent(PlayerGameModeChangeEvent event) {
		if (event.getNewGameMode() != GameMode.ADVENTURE) {
			SBPlayer.get(event.getPlayer()).setData(KEY_FLAG, false);
		}
	}
}