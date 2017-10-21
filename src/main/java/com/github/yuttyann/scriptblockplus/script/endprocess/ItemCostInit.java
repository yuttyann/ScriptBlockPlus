package com.github.yuttyann.scriptblockplus.script.endprocess;

import org.bukkit.inventory.ItemStack;

import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class ItemCostInit implements EndProcess {

	@Override
	public void success(ScriptRead scriptRead) {
		scriptRead.getSBPlayer().removeData("ItemCost");
	}

	@Override
	public void failed(ScriptRead scriptRead) {
		SBPlayer sbPlayer = scriptRead.getSBPlayer();
		ItemStack[] items = (ItemStack[]) sbPlayer.getData("ItemCost");
		sbPlayer.removeData("ItemCost");
		if (items != null && sbPlayer.isOnline()) {
			sbPlayer.getPlayer().getInventory().setContents(items);
			Utils.updateInventory(sbPlayer.getPlayer());
		}
	}
}