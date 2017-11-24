package com.github.yuttyann.scriptblockplus.listener.interact;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.EquipSlot;
import com.github.yuttyann.scriptblockplus.event.BlockInteractEvent;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class InteractListener implements Listener {

	private static final String FLAG_KEY = "InteractFlag";

	private ScriptBlock plugin;

	public InteractListener(ScriptBlock plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerAnimation(PlayerAnimationEvent event) {
		Player player = event.getPlayer();
		if (event.getAnimationType() != PlayerAnimationType.ARM_SWING || player.getGameMode() != GameMode.ADVENTURE) {
			return;
		}
		SBPlayer sbPlayer = SBPlayer.get(player);
		if (sbPlayer.hasData(FLAG_KEY) ? (boolean) sbPlayer.getData(FLAG_KEY) : false) {
			sbPlayer.setData(FLAG_KEY, false);
			return;
		}
		Location location = player.getLocation();
		double x = location.getX();
		double y = location.getY() + player.getEyeHeight();
		double z = location.getZ();
		float pitch = location.getPitch();
		float yaw = location.getYaw();
		float f1 = (float) Math.cos(-yaw * 0.017453292F - 3.1415927F);
		float f2 = (float) Math.sin(-yaw * 0.017453292F - 3.1415927F);
		float f3 = (float) -Math.cos(-pitch * 0.017453292F);
		float f4 = (float) Math.sin(-pitch * 0.017453292F);
		float f5 = f2 * f3;
		float f6 = f1 * f3;
		Vec3D vec3D1 = new Vec3D(x, y, z);
		Vec3D vec3D2 = vec3D1.add(f5 * 4.5D, f4 * 4.5D, f6 * 4.5D);
		MovingPosition movingPosition = rayTrace(player.getWorld(), vec3D1, vec3D2, false);
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
	public void onPlayerInteract(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		if (block == null || block.getType() == Material.AIR) {
			return;
		}
		Player player = event.getPlayer();
		Action action = event.getAction();
		boolean isAdventure = player.getGameMode() == GameMode.ADVENTURE;
		if (isAdventure && (action == Action.LEFT_CLICK_BLOCK || action == Action.LEFT_CLICK_AIR)) {
			return;
		}
		if (isAdventure) {
			SBPlayer sbPlayer = SBPlayer.get(player);
			sbPlayer.setData(FLAG_KEY, true);
			Bukkit.getScheduler().runTaskLater(plugin, () -> sbPlayer.setData(FLAG_KEY, false), 5L);
		}
		BlockInteractEvent interactEvent = new BlockInteractEvent(event, null, false);
		Bukkit.getPluginManager().callEvent(interactEvent);
		if (interactEvent.isCancelled()) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
		if (event.getNewGameMode() != GameMode.ADVENTURE) {
			SBPlayer.get(event.getPlayer()).setData(FLAG_KEY, false);
		}
	}

	private MovingPosition rayTrace(World world, Vec3D start, Vec3D end, boolean flag) {
		try {
			return new NMSWorld(world).rayTrace(start, end, flag);
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
		return null;
	}
}