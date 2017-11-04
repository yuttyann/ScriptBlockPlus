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
		super("itemhand", "@hand:", 6);
	}

	@Override
	public boolean isValid() {
		String[] array = StringUtils.split(getOptionValue(), " ");
		String[] itemData = StringUtils.split(array[0], ":");
		short damage = itemData.length > 1 ? Short.parseShort(itemData[1]) : 0;
		int id = Integer.parseInt(getId(itemData[0]));
		int amount = Integer.parseInt(array[1]);
		String itemName = StringUtils.replace(array.length > 2 ? StringUtils.createString(array, 2) : null, "&", "§");

		Player player = getPlayer();
		ItemStack[] items = Utils.getHandItems(player);
		if (!StreamUtils.anyMatch(items, i -> checkItem(i, itemName, id, amount, damage))) {
			Utils.sendMessage(player, SBConfig.getErrorHandMessage(getMaterial(id), id, amount, damage, itemName));
			return false;
		}
		return true;
	}

	private boolean checkItem(ItemStack item, String itemName, int id, int amount, short damage) {
		if (item == null || item.getType() != getMaterial(id) || item.getAmount() < amount || item.getDurability() != damage) {
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