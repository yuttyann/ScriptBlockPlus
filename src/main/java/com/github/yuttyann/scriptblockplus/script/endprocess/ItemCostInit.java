package com.github.yuttyann.scriptblockplus.script.endprocess;

import org.bukkit.inventory.ItemStack;

import com.github.yuttyann.scriptblockplus.script.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class ItemCostInit implements EndProcess {

	@Override
	public void success(ScriptRead scriptRead) {}

	@Override
	public void failed(ScriptRead scriptRead) {
		SBPlayer sbPlayer = scriptRead.getSBPlayer();
		ItemStack[] items = sbPlayer.getData("ItemCost");
		if (items != null && sbPlayer.updatePlayer()) {
			sbPlayer.removeData("ItemCost");
			sbPlayer.getPlayer().getInventory().setContents(items);
			Utils.updateInventory(sbPlayer.getPlayer());
		}
	}
}