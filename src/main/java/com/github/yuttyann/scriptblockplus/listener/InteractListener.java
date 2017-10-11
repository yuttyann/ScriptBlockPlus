package com.github.yuttyann.scriptblockplus.listener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
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

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.EquipmentSlot;
import com.github.yuttyann.scriptblockplus.event.BlockInteractEvent;
import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class InteractListener implements Listener {

	private ScriptBlock plugin;
	private List<UUID> interactEvents;

	public InteractListener(ScriptBlock plugin) {
		this.plugin = plugin;
		this.interactEvents = new ArrayList<UUID>();
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
		} catch (IllegalStateException e) {
			blocks = null;
		} finally {
			if (blocks == null || blocks.size() < 2) {
				return;
			}
		}
		Block block = blocks.get(1);
		if (!isPlayerInRange(player, block.getLocation()) || removeInteractEvent(player.getUniqueId())) {
			return;
		}
		Action action = Action.LEFT_CLICK_BLOCK;
		BlockFace blockFace = block.getFace(blocks.get(0));
		PlayerInteractEvent interactEvent = new PlayerInteractEvent(player, action, null, block, blockFace);
		if (callInteractEvent(player, interactEvent, EquipmentSlot.HAND)
				|| callInteractEvent(player, interactEvent, EquipmentSlot.OFF_HAND)) {
			event.setCancelled(true);
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
			UUID uuid = player.getUniqueId();
			addInteractEvent(uuid);
			Bukkit.getScheduler().runTaskLater(plugin, () -> removeInteractEvent(uuid), 5L);
		}
		Bukkit.getPluginManager().callEvent(new BlockInteractEvent(event, event.getItem(), null, true));
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
		if (event.getNewGameMode() != GameMode.ADVENTURE) {
			removeInteractEvent(event.getPlayer().getUniqueId());
		}
	}

	private List<Block> getLastTwoTargetBlocks(Player player, int distance) {
		if (Utils.isCB18orLater()) {
			return player.getLastTwoTargetBlocks((Set<Material>) null, distance);
		} else {
			@SuppressWarnings("deprecation")
			List<Block> blocks = player.getLastTwoTargetBlocks((HashSet<Byte>) null, distance);
			return blocks;
		}
	}

	private boolean addInteractEvent(UUID uuid) {
		return !interactEvents.contains(uuid) && interactEvents.add(uuid);
	}

	private boolean removeInteractEvent(UUID uuid) {
		return interactEvents.contains(uuid) && interactEvents.remove(uuid);
	}

	private boolean callInteractEvent(Player player, PlayerInteractEvent event, EquipmentSlot hand) {
		if (hand == EquipmentSlot.OFF_HAND && Utils.isCB19orLater()) {
			return false;
		}
		ItemStack item;
		if (hand == EquipmentSlot.HAND) {
			item = Utils.getItemInMainHand(player);
		} else {
			item = Utils.getItemInOffHand(player);
		}
		BlockInteractEvent blockInteractEvent = new BlockInteractEvent(event, item, hand, true);
		Bukkit.getPluginManager().callEvent(blockInteractEvent);
		return blockInteractEvent.isCancelled();
	}

	private boolean isPlayerInRange(Player target, Location location) {
		location = BlockCoords.getAllCenter(location);
		World world = location.getWorld();
		double radius = SBConfig.getLeftArmLength();
		int minX = floor((location.getX() - radius) / 16.0D);
		int minZ = floor((location.getZ() - radius) / 16.0D);
		int maxX = floor((location.getX() + radius) / 16.0D);
		int maxZ = floor((location.getZ() + radius) / 16.0D);
		for (int x = minX; x <= maxX; x++) {
			for (int z = minZ; z <= maxZ; z++) {
				if (!world.isChunkLoaded(x, z)) {
					continue;
				}
				for (Entity entity : world.getChunkAt(x, z).getEntities()) {
					if (!(entity instanceof Player) || ((Player) entity) != target) {
						continue;
					}
					Location entityLocation = entity.getLocation();
					if (entityLocation.getWorld() != location.getWorld()) {
						return false;
					}
					return entityLocation.distanceSquared(location) < radius * radius;
				}
			}
		}
		return false;
	}

	private int floor(double num) {
		int i = (int) num;
		return num < i ? i - 1 : i;
	}
}