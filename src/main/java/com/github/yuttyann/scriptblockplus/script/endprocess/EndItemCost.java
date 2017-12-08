package com.github.yuttyann.scriptblockplus.script.endprocess;

import org.bukkit.inventory.ItemStack;

import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.option.other.ItemCost;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class EndItemCost implements EndProcess {

	@Override
	public void success(SBRead sbRead) {
		sbRead.getSBPlayer().removeData(ItemCost.KEY_ITEM);
	}

	@Override
	public void failed(SBRead sbRead) {
		SBPlayer sbPlayer = sbRead.getSBPlayer();
		ItemStack[] items = sbPlayer.getData(ItemCost.KEY_ITEM, null);
		sbPlayer.removeData(ItemCost.KEY_ITEM);
		if (items != null && sbPlayer.isOnline()) {
			sbPlayer.getPlayer().getInventory().setContents(items);
			Utils.updateInventory(sbPlayer.getPlayer());
		}
	}
}