package com.github.yuttyann.scriptblockplus.option;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class ItemCost {

	private int id;
	private int amount;
	private short damage;
	private String itemName;
	private boolean isSuccess;

	public ItemCost(int id, int amount, short damage, String itemName) {
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

	public boolean payment(Player player) {
		PlayerInventory inventory = player.getInventory();
		ItemStack[] items = inventory.getContents().clone();
		for (int i = 0, j = 0; i < items.length; i++) {
			ItemStack item = items[i];
			if (checkItem(item)) {
				j += item.getAmount();
				int result = item.getAmount() - getAmount();
				if (j > getAmount()) {
					result = j - getAmount();
				}
				items[i] = minusItem(item, result);
			}
			if (j >= getAmount()) {
				inventory.setContents(items);
				Utils.updateInventory(player);
				isSuccess = true;
				break;
			}
		}
		return isSuccess;
	}

	private ItemStack minusItem(ItemStack item, int amount) {
		if (amount > 0) {
			item.setAmount(amount);
		} else {
			item = new ItemStack(Material.AIR);
		}
		return item;
	}

	private boolean checkItem(ItemStack item) {
		if (item == null || item.getType() != getMaterial() || item.getDurability() != getDurability()) {
			return false;
		}
		String itemName = Utils.getItemName(item);
		return getItemName() == null || (itemName != null && itemName.equals(getItemName()));
	}
}