package com.github.yuttyann.scriptblockplus.script.option.other;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.github.yuttyann.scriptblockplus.file.Lang;
import com.github.yuttyann.scriptblockplus.manager.ScriptManager;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class ItemCost extends BaseOption {

	public ItemCost(ScriptManager scriptManager) {
		super(scriptManager, "itemcost", "$item:");
	}

	@Override
	public boolean isValid() {
		String[] array = StringUtils.split(optionData, " ");
		String[] array2 = StringUtils.split(array[0], ":");
		short damage = 0;
		if (array2.length > 1) {
			damage = Short.parseShort(array2[1]);
		}
		int id = Integer.parseInt(Utils.getId(array2[0]));
		int amount = Integer.parseInt(array[1]);
		String itemName = array.length > 2 ? StringUtils.createString(array, 2) : null;
		itemName = itemName != null ? StringUtils.replace(itemName, "&", "§") : itemName;

		PlayerInventory inventory = player.getInventory();
		ItemStack[] items = inventory.getContents().clone();
		for (int i = 0, j = 0; i < items.length; i++) {
			ItemStack item = items[i];
			if (checkItem(item, itemName, id, damage)) {
				j += item.getAmount();
				int result = item.getAmount() - amount;
				if (j > amount) {
					result = j - amount;
				}
				items[i] = minus(item, result);
			}
			if (j >= amount) {
				inventory.setContents(items);
				Utils.updateInventory(player);
				break;
			}
			if (i == (items.length - 1) && j < amount) {
				Utils.sendPluginMessage(player, Lang.getErrorItemMessage(getMaterial(id), id, amount, damage, itemName));
				return false;
			}
		}
		return true;
	}

	private ItemStack minus(ItemStack item, int amount) {
		if (amount > 0) {
			item.setAmount(amount);
		} else {
			item = new ItemStack(Material.AIR);
		}
		return item;
	}

	private boolean checkItem(ItemStack item, String itemName, int id, short damage) {
		if (item == null || item.getType() != getMaterial(id) || item.getDurability() != damage) {
			return false;
		}
		String itemName_ = Utils.getItemName(item);
		return itemName == null || (itemName_ != null && itemName_.equals(itemName));
	}

	@SuppressWarnings("deprecation")
	private Material getMaterial(int id) {
		return Material.getMaterial(id);
	}
}