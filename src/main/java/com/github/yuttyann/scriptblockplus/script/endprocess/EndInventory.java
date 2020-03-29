package com.github.yuttyann.scriptblockplus.script.endprocess;

import com.github.yuttyann.scriptblockplus.player.ObjectMap;
import com.github.yuttyann.scriptblockplus.script.SBRead;
import com.github.yuttyann.scriptblockplus.script.option.other.ItemCost;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class EndInventory implements EndProcess {

	@NotNull
	@Override
	public EndProcess newInstance() {
		return new EndInventory();
	}

	@Override
	public void success(@NotNull SBRead sbRead) {
		Player player = sbRead.getSBPlayer().getPlayer();
		if (player.isOnline()) {
			Utils.updateInventory(player);
		}
	}

	@Override
	public void failed(@NotNull SBRead sbRead) {
		Player player = sbRead.getSBPlayer().getPlayer();
		if (sbRead.has(ItemCost.KEY_ITEM)) {
			ItemStack[] items = sbRead.get(ItemCost.KEY_ITEM);
			if (player.isOnline()) {
				player.getInventory().setContents(items);
				Utils.updateInventory(player);
			} else {
				ObjectMap objectMap = sbRead.getSBPlayer().getObjectMap();
				if (!objectMap.has(ItemCost.KEY_ITEM_PLAYER)) {
					objectMap.put(ItemCost.KEY_ITEM_PLAYER, items);
				}
			}
		}
	}
}