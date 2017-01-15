package com.github.yuttyann.scriptblockplus.listener;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;

import com.github.yuttyann.scriptblockplus.Permission;
import com.github.yuttyann.scriptblockplus.event.ScriptBlockInteractEvent;
import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.Messages;
import com.github.yuttyann.scriptblockplus.manager.EditManager;
import com.github.yuttyann.scriptblockplus.manager.MapManager;
import com.github.yuttyann.scriptblockplus.manager.MetadataManager;
import com.github.yuttyann.scriptblockplus.manager.MetadataManager.Click;
import com.github.yuttyann.scriptblockplus.manager.MetadataManager.Edit;
import com.github.yuttyann.scriptblockplus.manager.MetadataManager.Script;
import com.github.yuttyann.scriptblockplus.manager.OptionManager;
import com.github.yuttyann.scriptblockplus.manager.ScriptFileManager;
import com.github.yuttyann.scriptblockplus.type.ClickType;
import com.github.yuttyann.scriptblockplus.type.ScriptType;
import com.github.yuttyann.scriptblockplus.util.BlockLocation;
import com.github.yuttyann.scriptblockplus.util.Utils;

public class PlayerInteractListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerAnimation(PlayerAnimationEvent event) {
		Player player = event.getPlayer();
		if (event.getAnimationType() != PlayerAnimationType.ARM_SWING || player.getGameMode() != GameMode.ADVENTURE) {
			return;
		}
		Block block = getTargetBlock(player, 5);
		if (block == null) {
			return;
		}
		for (Entity entity : getNearbyEntities(block.getLocation(), 5.5D, 5.5D, 5.5D)) {
			if (entity instanceof Player && ((Player) entity).equals(player)) {
				PlayerInteractEvent inEvent = new PlayerInteractEvent(player, Action.LEFT_CLICK_BLOCK, Utils.getItemInHand(player), block, BlockFace.SELF);
				BlockLocation location = BlockLocation.fromLocation(block.getLocation());
				if (!scriptSetting(inEvent, Action.LEFT_CLICK_BLOCK, block, location)) {
					scriptEvent(inEvent, block, location);
				}
				break;
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Action action = event.getAction();
		if (action == Action.LEFT_CLICK_AIR || action == Action.RIGHT_CLICK_AIR || action == Action.PHYSICAL) {
			return;
		}
		Block block = event.getClickedBlock();
		BlockLocation location = BlockLocation.fromLocation(block.getLocation());
		if (!scriptSetting(event, action, block, location)) {
			scriptEvent(event, block, location);
		}
	}

	private boolean scriptSetting(PlayerInteractEvent event, Action action, Block block, BlockLocation location) {
		Player player = event.getPlayer();
		ItemStack item = event.getItem();
		if (event.hasItem() && item.getType() == Material.BLAZE_ROD
				&& item.hasItemMeta() && item.getItemMeta().hasDisplayName()
				&& item.getItemMeta().getDisplayName().equals("§dScript Editor")) {
			if (!Permission.has(Permission.SCRIPTBLOCKPLUS_TOOL_SCRIPTEDITOR, player)) {
				Utils.sendPluginMessage(player, "§cパーミッションが無いため、使用できません。");
				return false;
			}
			switch (action) {
			case LEFT_CLICK_BLOCK:
				if (player.isSneaking()) {
					if (!Edit.hasAllMetadata(player)) {
						Utils.sendPluginMessage(player, Messages.getErrorScriptFileCheckMessage());
						break;
					}
					Edit.getMetadata(player).scriptPaste(player, location);
				} else {
					new EditManager(location, ScriptType.INTERACT).scriptCopy(player);
				}
				event.setCancelled(true);
				return true;
			case RIGHT_CLICK_BLOCK:
				if (player.isSneaking()) {
					ScriptFileManager interact = new ScriptFileManager(location, ScriptType.INTERACT);
					ScriptFileManager walk = new ScriptFileManager(location, ScriptType.WALK);
					if (interact.checkPath()) {
						interact.scriptRemove(player);
					} else if (walk.checkPath()) {
						walk.scriptRemove(player);
					} else {
						Utils.sendPluginMessage(player, Messages.getErrorScriptFileCheckMessage());
					}
				} else {
					new EditManager(location, ScriptType.WALK).scriptCopy(player);
				}
				event.setCancelled(true);
				return true;
			default:
				MetadataManager.removeAllMetadata(player);
				return false;
			}
		}
		String script = null;
		for (ClickType type : ClickType.values()) {
			if (Click.hasMetadata(player, type)) {
				switch (type) {
				case INTERACT_CREATE:
					script = Script.getMetadata(player, type);
					new ScriptFileManager(location, ScriptType.INTERACT).scriptCreate(player, script);
					event.setCancelled(true);
					return true;
				case INTERACT_ADD:
					script = Script.getMetadata(player, type);
					new ScriptFileManager(location, ScriptType.INTERACT).scriptAdd(player, script);
					event.setCancelled(true);
					return true;
				case INTERACT_REMOVE:
					new ScriptFileManager(location, ScriptType.INTERACT).scriptRemove(player);
					event.setCancelled(true);
					return true;
				case INTERACT_VIEW:
					new ScriptFileManager(location, ScriptType.INTERACT).scriptView(player);
					event.setCancelled(true);
					return true;
				case WALK_CREATE:
					script = Script.getMetadata(player, type);
					new ScriptFileManager(location, ScriptType.WALK).scriptCreate(player, script);
					event.setCancelled(true);
					return true;
				case WALK_ADD:
					script = Script.getMetadata(player, type);
					new ScriptFileManager(location, ScriptType.WALK).scriptAdd(player, script);
					event.setCancelled(true);
					return true;
				case WALK_REMOVE:
					new ScriptFileManager(location, ScriptType.WALK).scriptRemove(player);
					event.setCancelled(true);
					return true;
				case WALK_VIEW:
					new ScriptFileManager(location, ScriptType.WALK).scriptView(player);
					event.setCancelled(true);
					return true;
				}
			}
		}
		return false;
	}

	private void scriptEvent(PlayerInteractEvent event, Block block, BlockLocation location) {
		if (Utils.isUpperVersion_v19() && !isSlotHand(event)) {
			return;
		}
		Player player = event.getPlayer();
		if (MapManager.getInteractCoords().contains(location.getFullCoords())) {
			ScriptBlockInteractEvent scriptEvent = new ScriptBlockInteractEvent(event, player, block, event.getItem(), location);
			Bukkit.getServer().getPluginManager().callEvent(scriptEvent);
			if (scriptEvent.isCancelled()) {
				return;
			}
			if (!Files.getConfig().getBoolean("LeftClick") && event.getAction() == Action.LEFT_CLICK_BLOCK) {
				return;
			}
			if (!Permission.has(Permission.SCRIPTBLOCKPLUS_INTERACT_USE, player)) {
				Utils.sendPluginMessage(player, "§cパーミッションが無いため、実行できません。");
				return;
			}
			new OptionManager(location, ScriptType.INTERACT).scriptExec(player);
		}
	}

	private boolean isSlotHand(PlayerInteractEvent event) {
		EquipmentSlot hand = event.getHand();
		return hand != null && hand == EquipmentSlot.HAND;
	}

	private List<Entity> getNearbyEntities(Location where, double rangex, double rangey, double rangez) {
		List<Entity> found = new ArrayList<Entity>();
		for (Entity entity : where.getWorld().getEntities()) {
			if (isInBorder(where, entity.getLocation(), rangex, rangey, rangez)) {
				found.add(entity);
			}
		}
		return found;
	}

	private boolean isInBorder(Location center, Location notCenter, double rangex, double rangey, double rangez) {
		double x = center.getX(), y = center.getY(), z = center.getZ();
		double x1 = notCenter.getX(), y1 = notCenter.getY(), z1 = notCenter.getZ();
		if (x1 >= (x + rangex) || y1 >= (y + rangey) || z1 >= (z + rangez) || x1 <= (x - rangex) || y1 <= (y - rangey) || z1 <= (z - rangez)) {
			return false;
		}
		return true;
	}

	private Block getTargetBlock(Player player, int distance) {
		BlockIterator iterator = new BlockIterator(player, distance);
		while (iterator.hasNext()) {
			Block block = iterator.next();
			Material type = block.getType();
			if (type != Material.AIR
					&& type != Material.STATIONARY_LAVA
					&& type != Material.STATIONARY_WATER) {
				return block;
			}
		}
		return null;
	}
}
