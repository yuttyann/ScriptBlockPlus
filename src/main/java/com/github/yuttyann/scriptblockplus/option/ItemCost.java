package com.github.yuttyann.scriptblockplus.option;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.yuttyann.scriptblockplus.util.Utils;

public class ItemCost {

	private int id;
	private int amount;
	private short damage;
	private boolean success;

	@SuppressWarnings("deprecation")
	public ItemCost(ItemStack item) {
		id = item.getType().getId();
		amount = item.getAmount();
		damage = item.getDurability();
	}

	public ItemCost(int id, int amount, short damage) {
		this.id = id;
		this.amount = amount;
		this.damage = damage;
	}

	public int getId() {
		return id;
	}

	public int getAmount() {
		return amount;
	}

	public short getDurability() {
		return damage;
	}

	public boolean isSuccess() {
		return success;
	}

	@SuppressWarnings("deprecation")
	public Material getMaterial() {
		Material material = Material.getMaterial(getId());
		return material != null ? material : Material.AIR;
	}

	public ItemStack getItemStack() {
		return new ItemStack(getMaterial(), getAmount(), getDurability());
	}

	@SuppressWarnings("deprecation")
	public boolean payment(Player player) {
		ItemStack hand = Utils.getItemInHand(player);
		ItemStack item = hand != null ? hand : new ItemStack(Material.AIR, 1);
		if (item.getType() == getMaterial() && item.getAmount() >= getAmount() && item.getDurability() == getDurability()) {
			int amount = item.getAmount() - getAmount();
			if (amount > 0) {
				Utils.setItemInHand(player, new ItemStack(getMaterial(), amount, damage));
			} else {
				Utils.setItemInHand(player, new ItemStack(Material.AIR));
			}
			player.updateInventory();
			success = true;
		} else {
			success = false;
		}
		return success;
	}
}
