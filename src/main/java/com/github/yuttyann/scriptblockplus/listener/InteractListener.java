package com.github.yuttyann.scriptblockplus.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
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
import org.bukkit.scheduler.BukkitRunnable;

import com.github.yuttyann.scriptblockplus.BlockLocation;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.event.BlockInteractEvent;
import com.github.yuttyann.scriptblockplus.manager.MapManager;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class InteractListener implements Listener {

	private ScriptBlock plugin;
	private MapManager mapManager;

	public InteractListener(ScriptBlock plugin) {
		this.plugin = plugin;
		this.mapManager = plugin.getMapManager();
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerAnimation(PlayerAnimationEvent event) {
		Player player = event.getPlayer();
		if (event.getAnimationType() != PlayerAnimationType.ARM_SWING || player.getGameMode() != GameMode.ADVENTURE) {
			return;
		}
		List<Block> blocks = null;
		try {
			blocks = getLastTwoTargetBlocks(player, 5);
		} catch (IllegalStateException e) {}
		if (blocks == null || blocks.size() < 2) {
			return;
		}
		Block block = blocks.get(1);
		for (Entity entity : getNearbyEntities(block.getLocation(), 5.22D)) {
			if (!(entity instanceof Player) || ((Player) entity) != player) {
				continue;
			}
			if (mapManager.removeEvents(player.getUniqueId())) {
				return;
			}
			Action action = Action.LEFT_CLICK_BLOCK;
			BlockFace blockFace = block.getFace(blocks.get(0));
			ItemStack item = Utils.getItemInHand(player);
			BlockInteractEvent interactEvent = new BlockInteractEvent(
				new PlayerInteractEvent(player, action, item, block, blockFace),
				player, block, item, action, blockFace, true
			);
			Utils.callEvent(interactEvent);
			break;
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Action action = event.getAction();
		boolean isAdventure = player.getGameMode() == GameMode.ADVENTURE;
		if (isAdventure && (action == Action.LEFT_CLICK_BLOCK || action == Action.LEFT_CLICK_AIR)) {
			return;
		}
		if (isAdventure) {
			final UUID uuid = player.getUniqueId();
			mapManager.addEvents(uuid);
			new BukkitRunnable() {
				@Override
				public void run() {
					mapManager.removeEvents(uuid);
				}
			}.runTaskLater(plugin, 5);
		}
		BlockInteractEvent interactEvent = new BlockInteractEvent(
			event, player, event.getClickedBlock(), event.getItem(),
			action, event.getBlockFace(), false
		);
		Utils.callEvent(interactEvent);
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

	private List<Entity> getNearbyEntities(Location location, double radius) {
		List<Entity> entities = new ArrayList<Entity>();
		location = BlockLocation.fromLocation(location).getAllCenter();
		World world = location.getWorld();
		int minX = floor((location.getX() - radius) / 16.0D);
		int minZ = floor((location.getZ() - radius) / 16.0D);
		int maxX = floor((location.getX() + radius) / 16.0D);
		int maxZ = floor((location.getZ() + radius) / 16.0D);
		for (int x = minX; x <= maxX; x++) {
			for (int z = minZ; z <= maxZ; z++) {
				if (!world.isChunkLoaded(x, z)) {
					continue;
				}
				entities.addAll(Arrays.asList(world.getChunkAt(x, z).getEntities()));
			}
		}
		Iterator<Entity> iterator = entities.iterator();
		while (iterator.hasNext()) {
			if (!(iterator.next().getLocation().distanceSquared(location) > radius * radius)) {
				continue;
			}
			iterator.remove();
		}
		return entities;
	}

	private int floor(double paramDouble) {
		int i = (int) paramDouble;
		return paramDouble < i ? i - 1 : i;
	}
}