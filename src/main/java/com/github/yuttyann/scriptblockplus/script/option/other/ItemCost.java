package com.github.yuttyann.scriptblockplus.script.option.other;

import java.util.Objects;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.player.PlayerData;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.ItemUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class ItemCost extends BaseOption {

	public static final String KEY_ITEM = PlayerData.createRandomId("ItemCost");

	public ItemCost() {
		super("itemcost", "$item:");
	}

	@Override
	protected boolean isValid() throws Exception {
		String[] array = StringUtils.split(getOptionValue(), " ");
		String[] itemData = StringUtils.split(array[0], ":");
		if (Calculation.REALNUMBER_PATTERN.matcher(itemData[0]).matches()) {
			throw new IllegalAccessException("Numerical values can not be used");
		}
		Material type = Material.getMaterial(itemData[0]);
		int damage = itemData.length > 1 ? Integer.parseInt(itemData[1]) : 0;
		int amount = Integer.parseInt(array[1]);
		String create = array.length > 2 ? StringUtils.createString(array, 2) : null;
		String itemName = StringUtils.replaceColorCode(create, false);

		Player player = getPlayer();
		PlayerInventory inventory = player.getInventory();
		ScriptRead scriptRead = getScriptRead();
		ItemStack[] items = scriptRead.has(KEY_ITEM) ? scriptRead.get(KEY_ITEM) : copyItems(inventory.getContents());
		for (int i = 0, j = amount; i < items.length; i++) {
			if (checkItem(items[i], itemName, type, damage)) {
				j -= j > 0 ? setAmount(items[i], items[i].getAmount() - j) : 0;
			}
			if (i == (items.length - 1) && j > 0) {
				Utils.sendMessage(player, SBConfig.getErrorItemMessage(type, amount, damage, itemName));
				return false;
			}
		}
		getScriptRead().put(KEY_ITEM, items);
		return true;
	}

	private int setAmount(ItemStack item, int amount) {
		int oldAmount = item.getAmount();
		item.setAmount(amount);
		return oldAmount;
	}

	private ItemStack[] copyItems(ItemStack[] items) {
		ItemStack[] copy = new ItemStack[items.length];
		for (int i = 0; i < copy.length; i++) {
			copy[i] = items[i] == null ? null : items[i].clone();
		}
		return copy;
	}

	private boolean checkItem(ItemStack item, String itemName, Material type, int damage) {
		if (item == null || item.getType() != type || ItemUtils.getDamage(item) != damage) {
			return false;
		}
		return itemName == null || Objects.equals(ItemUtils.getName(item, null), itemName);
	}
}