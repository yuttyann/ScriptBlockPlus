package com.github.yuttyann.scriptblockplus.event;

import java.lang.reflect.Method;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.github.yuttyann.scriptblockplus.enums.EquipmentSlot;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class BlockInteractEvent extends ScriptBlockEvent implements Cancellable {

	private static Method method_Hand;

	private ItemStack item;
	private Action action;
	private BlockFace blockFace;
	private EquipmentSlot hand;
	private boolean isAnimation;
	private boolean cancelled;

	public BlockInteractEvent(PlayerInteractEvent event, ItemStack item, EquipmentSlot hand,  boolean isAnimation) {
		this(event, event.getPlayer(), event.getClickedBlock(), item, event.getAction(), event.getBlockFace(), hand, isAnimation);
	}

	public BlockInteractEvent(PlayerInteractEvent event, Player player, Block block, ItemStack item, Action action, BlockFace blockFace, EquipmentSlot hand, boolean isAnimation) {
		super(player, block);
		this.item = item;
		this.action = action;
		this.blockFace = blockFace;
		this.hand = hand == null ? fromHand(event) : hand;
		this.isAnimation = isAnimation;
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

	public EquipmentSlot getHand() {
		return hand;
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

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}

	private EquipmentSlot fromHand(PlayerInteractEvent event) {
		if (event == null || !Utils.isCB19orLater()) {
			return EquipmentSlot.HAND;
		}
		try {
			if (method_Hand == null) {
				method_Hand = PlayerInteractEvent.class.getMethod("getHand");
			}
			return EquipmentSlot.fromEnum((Enum<?>) method_Hand.invoke(event, ArrayUtils.EMPTY_OBJECT_ARRAY));
		} catch (ReflectiveOperationException e) {
			return EquipmentSlot.NONE;
		}
	}
}