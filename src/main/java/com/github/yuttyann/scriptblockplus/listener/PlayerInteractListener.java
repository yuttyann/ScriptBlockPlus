package com.github.yuttyann.scriptblockplus.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.github.yuttyann.scriptblockplus.Permission;
import com.github.yuttyann.scriptblockplus.event.ScriptBlockInteractEvent;
import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.Messages;
import com.github.yuttyann.scriptblockplus.manager.EditManager;
import com.github.yuttyann.scriptblockplus.manager.MapManager;
import com.github.yuttyann.scriptblockplus.manager.MetadataManager.Click;
import com.github.yuttyann.scriptblockplus.manager.MetadataManager.ClickType;
import com.github.yuttyann.scriptblockplus.manager.MetadataManager.Edit;
import com.github.yuttyann.scriptblockplus.manager.MetadataManager.Script;
import com.github.yuttyann.scriptblockplus.manager.OptionManager;
import com.github.yuttyann.scriptblockplus.manager.OptionManager.ScriptType;
import com.github.yuttyann.scriptblockplus.manager.ScriptFileManager;
import com.github.yuttyann.scriptblockplus.util.BlockLocation;
import com.github.yuttyann.scriptblockplus.util.Utils;

public class PlayerInteractListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Action action = event.getAction();
		if (action == Action.LEFT_CLICK_AIR || action == Action.RIGHT_CLICK_AIR) {
			return;
		}
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		BlockLocation location = new BlockLocation(block.getLocation());
		ScriptBlockInteractEvent scriptEvent = new ScriptBlockInteractEvent(event, player, block, event.getItem(), location);
		Bukkit.getServer().getPluginManager().callEvent(scriptEvent);
		if (!scriptEvent.isCancelled() && MapManager.getInteractCoords().contains(location.getCoords(true))) {
			if (!Files.getConfig().getBoolean("LeftClick") && action == Action.LEFT_CLICK_BLOCK) {
				return;
			}
			if (!Permission.has(Permission.SCRIPTBLOCKPLUS_INTERACT_USE, player)) {
				Utils.sendPluginMessage(player, "§cパーミッションが無いため、実行できません。");
				return;
			}
			OptionManager.scriptExec(player, location, ScriptType.INTERACT);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onScriptBlcokInteract(ScriptBlockInteractEvent event) {
		Player player = event.getPlayer();
		BlockLocation location = event.getBlockLocation();
		ItemStack item = event.getItem();
		if (event.hasItem() && item.getType() == Material.BLAZE_ROD && item.hasItemMeta()
				&& item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equals("§dScript Editor")) {
			event.setCancelled(true);
			event.getPlayerInteractEvent().setCancelled(true);
			if (!Permission.has(Permission.SCRIPTBLOCKPLUS_TOOL_SCRIPTEDITOR, player)) {
				Utils.sendPluginMessage(player, "§cパーミッションが無いため、使用できません。");
				return;
			}
			Action action = event.getPlayerInteractEvent().getAction();
			switch (action) {
			case LEFT_CLICK_BLOCK:
				if (player.isSneaking()) {
					if (!Edit.hasAllMetadata(player)) {
						Utils.sendPluginMessage(player, Messages.getErrorScriptFileCheckMessage());
						break;
					}
					Edit.getMetadata(player).scriptPaste(player, location);
				} else {
					new EditManager(Files.getInteract(), location).scriptCopy(player);
				}
				break;
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
					new EditManager(Files.getWalk(), location).scriptCopy(player);
				}
				break;
			default:
				break;
			}
			return;
		}
		String script = null;
		for (ClickType type : ClickType.values()) {
			if (Click.hasMetadata(player, type)) {
				switch (type) {
				case INTERACT_CREATE:
					script = Script.getMetadata(player, type);
					new ScriptFileManager(location, ScriptType.INTERACT).scriptCreate(player, script);
					event.setCancelled(true);
					return;
				case INTERACT_ADD:
					script = Script.getMetadata(player, type);
					new ScriptFileManager(location, ScriptType.INTERACT).scriptAdd(player, script);
					event.setCancelled(true);
					return;
				case INTERACT_REMOVE:
					new ScriptFileManager(location, ScriptType.INTERACT).scriptRemove(player);
					event.setCancelled(true);
					return;
				case INTERACT_VIEW:
					new ScriptFileManager(location, ScriptType.INTERACT).scriptView(player);
					event.setCancelled(true);
					return;
				case WALK_CREATE:
					script = Script.getMetadata(player, type);
					new ScriptFileManager(location, ScriptType.WALK).scriptCreate(player, script);
					event.setCancelled(true);
					return;
				case WALK_ADD:
					script = Script.getMetadata(player, type);
					new ScriptFileManager(location, ScriptType.WALK).scriptAdd(player, script);
					event.setCancelled(true);
					return;
				case WALK_REMOVE:
					new ScriptFileManager(location, ScriptType.WALK).scriptRemove(player);
					event.setCancelled(true);
					return;
				case WALK_VIEW:
					new ScriptFileManager(location, ScriptType.WALK).scriptView(player);
					event.setCancelled(true);
					return;
				}
			}
		}
	}
}
