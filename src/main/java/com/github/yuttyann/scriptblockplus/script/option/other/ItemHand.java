package com.github.yuttyann.scriptblockplus.script.option.other;

import java.util.Objects;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class ItemHand extends BaseOption {

	public ItemHand() {
		super("itemhand", "@hand:");
	}

	@Override
	protected boolean isValid() throws Exception {
		String[] array = StringUtils.split(getOptionValue(), " ");
		String[] itemData = StringUtils.split(array[0], ":");
		Material type = Material.getMaterial(itemData[0]);
		short damage = itemData.length > 1 ? Short.parseShort(itemData[1]) : 0;
		int amount = Integer.parseInt(array[1]);
		String create = array.length > 2 ? StringUtils.createString(array, 2) : null;
		String itemName = StringUtils.replaceColorCode(create, false);

		Player player = getPlayer();
		ItemStack[] items = Utils.getHandItems(player);
		if (!StreamUtils.anyMatch(items, i -> checkItem(i, itemName, type, amount, damage))) {
			Utils.sendMessage(player, SBConfig.getErrorHandMessage(type, amount, damage, itemName));
			return false;
		}
		return true;
	}

	private boolean checkItem(ItemStack item, String itemName, Material type, int amount, short damage) {
		if (item == null || item.getType() != type || item.getAmount() < amount || item.getDurability() != damage) {
			return false;
		}
		return itemName == null || Objects.equals(Utils.getItemName(item, null), itemName);
	}
}