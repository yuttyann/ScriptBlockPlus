package com.github.yuttyann.scriptblockplus.script.option.other;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.github.yuttyann.scriptblockplus.file.Lang;
import com.github.yuttyann.scriptblockplus.manager.ScriptManager;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class ItemHand extends BaseOption {

	public ItemHand(ScriptManager scriptManager) {
		super(scriptManager, "itemhand", "@hand:");
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

		boolean success = false;
		for (ItemStack item : getItems(player)) {
			if (checkItem(item, itemName, id, amount, damage)) {
				success = true;
				break;
			}
		}
		if (!success) {
			Utils.sendPluginMessage(player, Lang.getErrorHandMessage(getMaterial(id), id, amount, damage, itemName));
		}
		return success;
	}

	private boolean checkItem(ItemStack item, String itemName, int id, int amount, short damage) {
		if (item == null || !(item.getType() == getMaterial(id) && item.getAmount() >= amount && item.getDurability() == damage)) {
			return false;
		}
		String itemName_ = Utils.getItemName(item);
		return itemName == null || (itemName_ != null && itemName_.equals(itemName));
	}

	@SuppressWarnings("deprecation")
	private ItemStack[] getItems(Player player) {
		PlayerInventory inventory = player.getInventory();
		if(Utils.isCB19orLater()) {
			return new ItemStack[]{inventory.getItemInMainHand(), inventory.getItemInOffHand()};
		} else {
			return new ItemStack[]{inventory.getItemInHand()};
		}
	}

	@SuppressWarnings("deprecation")
	private Material getMaterial(int id) {
		return Material.getMaterial(id);
	}
}