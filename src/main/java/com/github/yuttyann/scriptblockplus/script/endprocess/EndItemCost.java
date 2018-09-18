package com.github.yuttyann.scriptblockplus.script.endprocess;

import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.script.SBRead;
import com.github.yuttyann.scriptblockplus.script.option.other.ItemCost;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class EndItemCost implements EndProcess {

	@Override
	public void success(SBRead sbRead) {
		Player player = sbRead.getSBPlayer().getPlayer();
		if (sbRead.has(ItemCost.KEY_ITEM) && player.isOnline()) {
			player.getInventory().setContents(sbRead.get(ItemCost.KEY_ITEM));
			Utils.updateInventory(player);
		}
	}

	@Override
	public void failed(SBRead sbRead) {}
}