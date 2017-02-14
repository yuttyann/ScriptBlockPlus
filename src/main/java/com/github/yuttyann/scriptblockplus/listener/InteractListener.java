package com.github.yuttyann.scriptblockplus.listener;

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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.yuttyann.scriptblockplus.BlockLocation;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.event.BlockInteractEvent;
import com.github.yuttyann.scriptblockplus.manager.MapManager;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class InteractListener implements Listener {

	private MapManager mapManager;

	public InteractListener() {
		this.mapManager = ScriptBlock.instance.getMapManager();
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerAnimation(PlayerAnimationEvent event) {
		Player player = event.getPlayer();
		if (event.getAnimationType() != PlayerAnimationType.ARM_SWING || player.getGameMode() != GameMode.ADVENTURE) {
			return;
		}
		List<Block> blocks = getLastTwoTargetBlocks(player, 5);
		if (blocks.size() < 2) {
			return;
		}
		Block block = blocks.get(1);
		for (Entity entity : getNearbyEntities(block.getLocation(), 4.3D)) {
			if (entity instanceof Player && ((Player) entity) == player) {
				if (mapManager.removeEvents(player.getUniqueId())) {
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

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (player.getGameMode() == GameMode.ADVENTURE) {
			final UUID uuid = player.getUniqueId();
			mapManager.addEvents(uuid);
			new BukkitRunnable() {
				@Override
				public void run() {
					mapManager.removeEvents(uuid);
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
		mapManager.removeEvents(player.getUniqueId());
	}

	@SuppressWarnings("deprecation")
	private List<Block> getLastTwoTargetBlocks(Player player, int distance) {
		if (Utils.isCB18orLater()) {
			return player.getLastTwoTargetBlocks((Set<Material>) null, distance);
		} else {
			return player.getLastTwoTargetBlocks((HashSet<Byte>) null, distance);
		}
	}

	//試験的に実装 テレポートさせることで表示されないようにしている。
	//座標は自動的にブロックの中心になる。
	private List<Entity> getNearbyEntities(Location center, double radius) {
		BlockLocation location = BlockLocation.fromLocation(center);
		location.setXYZ(location.getBlockX() + 0.5D, 500.0D, location.getBlockZ() + 0.5D);
		Entity entity = location.getWorld().spawnEntity(location, EntityType.SNOWBALL);
		location.setY(center.getBlockY() + 0.5D);
		entity.teleport(location, TeleportCause.UNKNOWN);
		entity.remove();
		return entity.getNearbyEntities(radius, radius, radius);
	}
}