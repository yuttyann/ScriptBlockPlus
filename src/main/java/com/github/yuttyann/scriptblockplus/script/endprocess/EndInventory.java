package com.github.yuttyann.scriptblockplus.script.endprocess;

import com.github.yuttyann.scriptblockplus.player.ObjectMap;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.SBRead;
import com.github.yuttyann.scriptblockplus.script.option.other.ItemCost;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * ScriptBlockPlus EndInventory エンドプロセスクラス
 * @author yuttyann44581
 */
public class EndInventory implements EndProcess {

	@Override
	@NotNull
	public EndProcess newInstance() {
		return new EndInventory();
	}

	@Override
	public void success(@NotNull SBRead sbRead) {
		SBPlayer sbPlayer = sbRead.getSBPlayer();
		if (sbPlayer.isOnline()) {
			Utils.updateInventory(Objects.requireNonNull(sbPlayer.getPlayer()));
		}
	}

	@Override
	public void failed(@NotNull SBRead sbRead) {
		if (sbRead.has(ItemCost.KEY_OPTION)) {
			ItemStack[] items = sbRead.get(ItemCost.KEY_OPTION);
			SBPlayer sbPlayer = sbRead.getSBPlayer();
			if (sbPlayer.isOnline()) {
				sbPlayer.getInventory().setContents(items);
				Utils.updateInventory(Objects.requireNonNull(sbPlayer.getPlayer()));
			} else {
				ObjectMap objectMap = sbRead.getSBPlayer().getObjectMap();
				if (!objectMap.has(ItemCost.KEY_PLAYER)) {
					objectMap.put(ItemCost.KEY_PLAYER, items);
				}
			}
		}
	}
}