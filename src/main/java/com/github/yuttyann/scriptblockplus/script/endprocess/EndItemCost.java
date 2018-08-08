package com.github.yuttyann.scriptblockplus.script.endprocess;

import org.bukkit.inventory.ItemStack;

import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.SBRead;
import com.github.yuttyann.scriptblockplus.script.option.other.ItemCost;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class EndItemCost implements EndProcess {

	@Override
	public void success(SBRead sbRead) {
		sbRead.removeData(ItemCost.KEY_ITEM);
		if (sbRead.getSBPlayer().isOnline()) {
			Utils.updateInventory(sbRead.getSBPlayer().getPlayer());
		}
	}

	@Override
	public void failed(SBRead sbRead) {
		if (sbRead.hasData(ItemCost.KEY_ITEM)) {
			ItemStack[] items = sbRead.getData(ItemCost.KEY_ITEM);
			sbRead.removeData(ItemCost.KEY_ITEM);
			SBPlayer sbPlayer = sbRead.getSBPlayer();
			if (sbPlayer.isOnline()) {
				sbPlayer.getInventory().setContents(items);
				Utils.updateInventory(sbPlayer.getPlayer());
			}
		}
	}
}