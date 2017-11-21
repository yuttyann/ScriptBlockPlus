package com.github.yuttyann.scriptblockplus.script.option.other;

import java.util.Objects;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class ItemCost extends BaseOption {

	public ItemCost() {
		super("itemcost", "$item:");
	}

	@Override
	protected boolean isValid() throws Exception {
		String[] array = StringUtils.split(getOptionValue(), " ");
		String[] itemData = StringUtils.split(array[0], ":");
		short damage = itemData.length > 1 ? Short.parseShort(itemData[1]) : 0;
		int id = Integer.parseInt(getId(itemData[0]));
		int amount = Integer.parseInt(array[1]);
		String create = array.length > 2 ? StringUtils.createString(array, 2) : null;
		String itemName = StringUtils.replaceColorCode(create, false);

		Player player = getPlayer();
		PlayerInventory inventory = player.getInventory();
		ItemStack[] items = copyItems(inventory.getContents());
		int allAmount = 0;
		for (int i = 0; i < items.length; i++) {
			ItemStack item = items[i];
			if (checkItem(item, itemName, id, damage)) {
				allAmount += item.getAmount();
				int result = item.getAmount() - amount;
				if (allAmount > amount) {
					result = allAmount - amount;
				}
				items[i] = consume(item, result);
			}
			if (allAmount >= amount) {
				SBPlayer.get(player).setData("ItemCost", copyItems(inventory.getContents()));
				break;
			}
		}
		if (allAmount < amount) {
			Utils.sendMessage(player, SBConfig.getErrorItemMessage(getMaterial(id), id, amount, damage, itemName));
			return false;
		}
		inventory.setContents(items);
		Utils.updateInventory(player);
		return true;
	}

	private ItemStack[] copyItems(ItemStack[] items) {
		ItemStack[] copy = new ItemStack[items.length];
		for (int i = 0; i < copy.length; i++) {
			copy[i] = items[i] == null ? null : items[i].clone();
		}
		return copy;
	}

	private ItemStack consume(ItemStack item, int amount) {
		if (amount > 0) {
			item.setAmount(amount);
		} else {
			item.setType(Material.AIR);
		}
		return item;
	}

	private boolean checkItem(ItemStack item, String itemName, int id, short damage) {
		if (item == null || item.getType() != getMaterial(id) || item.getDurability() != damage) {
			return false;
		}
		return itemName == null || Objects.equals(Utils.getItemName(item, null), itemName);
	}

	private String getId(String source) {
		if (source.matches("\\A[-]?[0-9]+\\z")) {
			return source;
		}
		@SuppressWarnings("deprecation")
		String id = String.valueOf(Material.getMaterial(source.toUpperCase()).getId());
		return id;
	}

	private Material getMaterial(int id) {
		@SuppressWarnings("deprecation")
		Material material = Material.getMaterial(id);
		return material;
	}
}