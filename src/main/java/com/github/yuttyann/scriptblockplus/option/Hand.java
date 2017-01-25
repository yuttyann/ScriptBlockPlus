package com.github.yuttyann.scriptblockplus.option;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.yuttyann.scriptblockplus.utils.Utils;

public class Hand {

	private int id;
	private int amount;
	private short damage;
	private String itemName;
	private Material material;
	private boolean isSuccess;

	@SuppressWarnings("deprecation")
	public Hand(ItemStack item) {
		this(item.getType().getId(), item.getAmount(), item.getDurability(), Utils.getItemName(item));
	}

	@SuppressWarnings("deprecation")
	public Hand(int id, int amount, short damage, String itemName) {
		this.id = id;
		this.amount = amount;
		this.damage = damage;
		this.itemName = itemName != null ? itemName.replace("&", "§") : itemName;
		this.material = Material.getMaterial(id);
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

	public String getItemName() {
		return itemName != null ? itemName : "none";
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public Material getMaterial() {
		return material;
	}

	public ItemStack getItemStack() {
		return new ItemStack(material, amount, damage);
	}

	public boolean check(Player player) {
		ItemStack hand = Utils.getItemInHand(player);
		ItemStack item = hand != null ? hand : new ItemStack(Material.AIR, 1);
		if (item.getType() == material
			&& item.getAmount() >= amount
			&& item.getDurability() == damage) {
			String itemName = Utils.getItemName(item);
			if (this.itemName == null || (itemName != null && itemName.equals(this.itemName))) {
				isSuccess = true;
			}
		}
		return isSuccess;
	}
}