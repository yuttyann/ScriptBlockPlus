package com.github.yuttyann.scriptblockplus.option;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
	public ItemCost(ItemStack item) {
		this(item.getType().getId(), item.getAmount(), item.getDurability(), Utils.getItemName(item));
	}

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
		for (ItemStack item : player.getInventory().getContents()) {
			item = item != null ? item : new ItemStack(Material.AIR);
			if (item.getType() == getMaterial()
				&& item.getAmount() >= getAmount()
				&& item.getDurability() == getDurability()) {
				String itemName = Utils.getItemName(item);
				if (getItemName() == null || itemName.equals(getItemName())) {
					int result = item.getAmount() - getAmount();
					if (result > 0) {
						item.setAmount(result);
					} else {
						Utils.setItemInHand(player, new ItemStack(Material.AIR));
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