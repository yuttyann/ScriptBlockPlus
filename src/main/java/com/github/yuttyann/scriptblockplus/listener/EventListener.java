package com.github.yuttyann.scriptblockplus.listener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.event.BlockInteractEvent;
import com.github.yuttyann.scriptblockplus.manager.MapManager;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class EventListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerAnimation(PlayerAnimationEvent event) {
		Player player = event.getPlayer();
		if (event.getAnimationType() != PlayerAnimationType.ARM_SWING || player.getGameMode() != GameMode.ADVENTURE) {
			return;
		}
		List<Block> blocks = getLastTwoTargetBlocks(player, 6);
		if (blocks.isEmpty() && blocks.size() > 1) {
			return;
		}
		Block block = blocks.get(1);
		for (Entity entity : getNearbyEntities(block.getLocation(), 5.5D, 5.5D, 5.5D)) {
			if (entity instanceof Player && ((Player) entity) == player) {
				if (MapManager.removeEvents(player.getUniqueId())) {
					return;
				}
				Action action = Action.LEFT_CLICK_BLOCK;
				BlockFace blockFace = block.getFace(blocks.get(0));
				ItemStack item = Utils.getItemInHand(player);
				BlockInteractEvent inEvent = new BlockInteractEvent(
					new PlayerInteractEvent(player, action, item, block, blockFace),
					player, block, item, action, blockFace, true
				);
				Utils.callEvent(inEvent);
				break;
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.useInteractedBlock() == Result.DENY) {
			return;
		}
		Player player = event.getPlayer();
		if (player.getGameMode() == GameMode.ADVENTURE) {
			final UUID uuid = player.getUniqueId();
			MapManager.addEvents(uuid);
			new BukkitRunnable() {
				@Override
				public void run() {
					MapManager.removeEvents(uuid);
				}
			}.runTaskLater(ScriptBlock.instance, 5);
		}
		BlockInteractEvent inEvent = new BlockInteractEvent(
			event, player, event.getClickedBlock(), event.getItem(),
			event.getAction(), event.getBlockFace(), false
		);
		Utils.callEvent(inEvent);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
		Player player = event.getPlayer();
		if (event.getNewGameMode() == GameMode.ADVENTURE) {
			return;
		}
		MapManager.removeEvents(player.getUniqueId());
	}

	@SuppressWarnings("deprecation")
	private List<Block> getLastTwoTargetBlocks(Player player, int distance) {
		if (Utils.isCB18orLater()) {
			return player.getLastTwoTargetBlocks((Set<Material>) null, distance);
		} else {
			return player.getLastTwoTargetBlocks((HashSet<Byte>) null, distance);
		}
	}

	private List<Entity> getNearbyEntities(Location where, double rangeX, double rangeY, double rangeZ) {
		List<Entity> found = new ArrayList<Entity>();
		for (Entity entity : where.getWorld().getEntities()) {
			if (isInBorder(where, entity.getLocation(), rangeX, rangeY, rangeZ)) {
				found.add(entity);
			}
		}
		return found;
	}

	private boolean isInBorder(Location center, Location notCenter, double rangeX, double rangeY, double rangeZ) {
		double x = center.getX(), y = center.getY(), z = center.getZ();
		double x1 = notCenter.getX(), y1 = notCenter.getY(), z1 = notCenter.getZ();
		if (x1 >= (x + rangeX) || y1 >= (y + rangeY) || z1 >= (z + rangeZ)
				|| x1 <= (x - rangeX) || y1 <= (y - rangeY) || z1 <= (z - rangeZ)) {
			return false;
		}
		return true;
	}
}
