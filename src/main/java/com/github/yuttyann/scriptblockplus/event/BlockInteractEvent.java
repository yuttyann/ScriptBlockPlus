package com.github.yuttyann.scriptblockplus.event;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.github.yuttyann.scriptblockplus.utils.ReflectionUtils;

public class BlockInteractEvent extends ScriptBlockEvent implements Cancellable {

	private PlayerInteractEvent event;
	private Player player;
	private Block block;
	private ItemStack item;
	private Action action;
	private BlockFace blockFace;
	private Location location;
	private boolean isAnimation;

	public BlockInteractEvent(PlayerInteractEvent event, Player player, Block block,
			ItemStack item, Action action, BlockFace blockFace, boolean isAnimation) {
		this.event = event;
		this.player = player;
		this.block = block;
		this.item = item;
		this.action = action;
		this.blockFace = blockFace;
		this.location = block.getLocation();
		this.isAnimation = isAnimation;
	}

	public Player getPlayer() {
		return player;
	}

	public Block getBlock() {
		return block;
	}

	public ItemStack getItem() {
		return item;
	}

	public Material getMaterial() {
		if (!hasItem()) {
			return Material.AIR;
		}
		return item.getType();
	}

	public Action getAction() {
		return action;
	}

	public BlockFace getBlockFace() {
		return blockFace;
	}

	public Enum<?> getHand() {
		try {
			return (Enum<?>) ReflectionUtils.invokeMethod(event, "getHand");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Location getLocation() {
		return location;
	}

	public boolean hasItem() {
		return item != null;
	}

	public boolean isBlockInHand() {
		if (!hasItem()) {
			return false;
		}
		return item.getType().isBlock();
	}

	public boolean isAnimation() {
		return isAnimation;
	}

	public boolean isCancelled() {
		return event.isCancelled();
	}

	public void setCancelled(boolean cancel) {
		event.setCancelled(cancel);
	}
}