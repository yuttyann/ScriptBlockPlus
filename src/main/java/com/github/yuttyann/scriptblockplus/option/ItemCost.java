package com.github.yuttyann.scriptblockplus.option;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class ItemCost {

	private int id;
	private int amount;
	private short damage;
	private String itemName;
	private Material material;
	private boolean isSuccess;

	@SuppressWarnings("deprecation")
	public ItemCost(int id, int amount, short damage, String itemName) {
		this.id = id;
		this.amount = amount;
		this.damage = damage;
		this.itemName = itemName != null ? StringUtils.replace(itemName, "&", "§") : itemName;
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
		return itemName;
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

	public boolean payment(Player player) {
		PlayerInventory inventory = player.getInventory();
		ItemStack[] items = inventory.getContents();
		for (int i = 0; i < items.length; i++) {
			ItemStack item = items[i] != null ? items[i] : new ItemStack(Material.AIR);
			if (item.getType() == getMaterial()
				&& item.getAmount() >= getAmount()
				&& item.getDurability() == getDurability()) {
				String itemName = Utils.getItemName(item);
				if (getItemName() == null || (itemName != null && itemName.equals(getItemName()))) {
					int result = item.getAmount() - getAmount();
					if (result > 0) {
						item.setAmount(result);
					} else {
						items[i] = new ItemStack(Material.AIR);
						inventory.setContents(items);
					}
					Utils.updateInventory(player);
					isSuccess = true;
					break;
				}
			}
		}
		return isSuccess;
	}
}