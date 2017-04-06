package com.github.yuttyann.scriptblockplus.option;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class ItemHand {

	private int id;
	private int amount;
	private short damage;
	private String itemName;
	private boolean isSuccess;

	public ItemHand(int id, int amount, short damage, String itemName) {
		this.id = id;
		this.amount = amount;
		this.damage = damage;
		this.itemName = itemName != null ? StringUtils.replace(itemName, "&", "§") : itemName;
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
		return itemName;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	@SuppressWarnings("deprecation")
	public Material getMaterial() {
		return Material.getMaterial(id);
	}

	public ItemStack getItemStack() {
		ItemStack item = new ItemStack(getMaterial(), amount, damage);
		if (itemName != null) {
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(itemName);
			item.setItemMeta(meta);
		}
		return item;
	}

	public boolean check(Player player) {
		ItemStack hand = Utils.getItemInHand(player);
		return isSuccess = checkItem(hand);
	}

	private boolean checkItem(ItemStack item) {
		if (item == null || !(item.getType() == getMaterial() && item.getAmount() >= getAmount() && item.getDurability() == getDurability())) {
			return false;
		}
		String itemName = Utils.getItemName(item);
		return getItemName() == null || (itemName != null && itemName.equals(getItemName()));
	}
}