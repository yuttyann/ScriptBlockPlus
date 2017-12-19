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

import com.github.yuttyann.scriptblockplus.enums.EquipSlot;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class BlockInteractEvent extends ScriptBlockEvent implements Cancellable {

	private static Method method_Hand;

	private ItemStack item;
	private Action action;
	private BlockFace blockFace;
	private EquipSlot hand;
	private boolean isAnimation;
	private boolean cancelled;

	public BlockInteractEvent(PlayerInteractEvent event, EquipSlot hand,  boolean isAnimation) {
		this(event, event.getItem(), hand, isAnimation);
	}

	public BlockInteractEvent(PlayerInteractEvent event, ItemStack item, EquipSlot hand,  boolean isAnimation) {
		this(event, event.getPlayer(), event.getClickedBlock(), item, event.getAction(), event.getBlockFace(), hand, isAnimation);
	}

	public BlockInteractEvent(PlayerInteractEvent event, Player player, Block block, ItemStack item, Action action, BlockFace blockFace, EquipSlot hand, boolean isAnimation) {
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

	public EquipSlot getHand() {
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

	private EquipSlot fromHand(PlayerInteractEvent event) {
		if (event == null || !Utils.isCB19orLater()) {
			return EquipSlot.HAND;
		}
		try {
			if (method_Hand == null) {
				method_Hand = event.getClass().getMethod("getHand");
			}
			return EquipSlot.fromEquipmentSlot((Enum<?>) method_Hand.invoke(event, ArrayUtils.EMPTY_OBJECT_ARRAY));
		} catch (ReflectiveOperationException e) {
			return EquipSlot.NONE;
		}
	}
}