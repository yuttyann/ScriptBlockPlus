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
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.ClickType;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.event.BlockInteractEvent;
import com.github.yuttyann.scriptblockplus.event.ScriptBlockInteractEvent;
import com.github.yuttyann.scriptblockplus.file.Messages;
import com.github.yuttyann.scriptblockplus.manager.MapManager;
import com.github.yuttyann.scriptblockplus.manager.MetadataManager;
import com.github.yuttyann.scriptblockplus.manager.MetadataManager.Click;
import com.github.yuttyann.scriptblockplus.manager.MetadataManager.Script;
import com.github.yuttyann.scriptblockplus.manager.MetadataManager.ScriptFile;
import com.github.yuttyann.scriptblockplus.manager.OptionManager;
import com.github.yuttyann.scriptblockplus.manager.ScriptFileManager;
import com.github.yuttyann.scriptblockplus.utils.BlockLocation;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class InteractListener implements Listener {

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

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockInteract(BlockInteractEvent event) {
		Block block = event.getBlock();
		BlockLocation location = BlockLocation.fromLocation(block.getLocation());
		if (!scriptSetting(event, event.getAction(), block, location)) {
			scriptEvent(event, block, location);
		}
	}

	private boolean scriptSetting(BlockInteractEvent event, Action action, Block block, BlockLocation location) {
		Player player = event.getPlayer();
		if (Utils.checkItem(event.getItem(), Material.BLAZE_ROD, "§dScript Editor")) {
			if (!Permission.has(Permission.SCRIPTBLOCKPLUS_TOOL_SCRIPTEDITOR, player)) {
				Utils.sendPluginMessage(player, Messages.notPermissionMessage);
				return false;
			}
			switch (action) {
			case LEFT_CLICK_BLOCK:
				if (player.isSneaking()) {
					new ScriptFileManager(location, ScriptType.WALK).scriptCopy(player);
				} else {
					new ScriptFileManager(location, ScriptType.INTERACT).scriptCopy(player);
				}
				event.setCancelled(true);
				return true;
			case RIGHT_CLICK_BLOCK:
				if (player.isSneaking()) {
					if (!ScriptFile.hasAllMetadata(player)) {
						Utils.sendPluginMessage(player, Messages.getErrorScriptFileCheckMessage());
						break;
					}
					ScriptFile.getMetadata(player).scriptPaste(player, location);
				} else {
					new ScriptFileManager(location, ScriptType.BREAK).scriptCopy(player);
				}
				event.setCancelled(true);
				return true;
			default:
				MetadataManager.removeAllMetadata(player);
				return false;
			}
		}
		String[] split;
		for (ClickType type : ClickType.values()) {
			if (Click.hasMetadata(player, type)) {
				switch ((split = type.name().split("_"))[0]) {
				case "INTERACT":
					if (clickScript(player, split[1], location, type, ScriptType.INTERACT)) {
						event.setCancelled(true);
						return true;
					}
					return false;
				case "BREAK":
					if (clickScript(player, split[1], location, type, ScriptType.BREAK)) {
						event.setCancelled(true);
						return true;
					}
					return false;
				case "WALK":
					if (clickScript(player, split[1], location, type, ScriptType.WALK)) {
						event.setCancelled(true);
						return true;
					}
					return false;
				}
			}
		}
		return false;
	}

	private boolean clickScript(Player player, String type, BlockLocation location, ClickType clickType, ScriptType scriptType) {
		switch (type) {
		case "CREATE":
			new ScriptFileManager(location, scriptType).scriptCreate(player, Script.getMetadata(player, clickType));
			return true;
		case "ADD":
			new ScriptFileManager(location, scriptType).scriptAdd(player, Script.getMetadata(player, clickType));
			return true;
		case "REMOVE":
			new ScriptFileManager(location, scriptType).scriptRemove(player);
			return true;
		case "VIEW":
			new ScriptFileManager(location, scriptType).scriptView(player);
			return true;
		default:
			return false;
		}
	}

	private void scriptEvent(BlockInteractEvent event, Block block, BlockLocation location) {
		if (Utils.isCB19orLater() && !isSlotHand(event.getHand())) {
			return;
		}
		Player player = event.getPlayer();
		if (MapManager.getInteractCoords().contains(location.getFullCoords())) {
			ScriptBlockInteractEvent scEvent = new ScriptBlockInteractEvent(player, block, event.getItem(), location);
			Utils.callEvent(scEvent);
			if (scEvent.isCancelled()) {
				return;
			}
			if (!scEvent.isLeftClick() && event.getAction() == Action.LEFT_CLICK_BLOCK) {
				return;
			}
			if (!Permission.has(Permission.SCRIPTBLOCKPLUS_INTERACT_USE, player)) {
				Utils.sendPluginMessage(player, Messages.notPermissionMessage);
				return;
			}
			new OptionManager(location, ScriptType.INTERACT).scriptExec(player);
		}
	}

	private boolean isSlotHand(EquipmentSlot hand) {
		return hand != null && hand == EquipmentSlot.HAND;
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