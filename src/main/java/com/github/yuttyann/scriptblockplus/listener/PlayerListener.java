package com.github.yuttyann.scriptblockplus.listener;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.listener.item.ItemAction;
import com.github.yuttyann.scriptblockplus.player.BaseSBPlayer;
import com.github.yuttyann.scriptblockplus.player.ObjectMap;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.region.CuboidRegion;
import com.github.yuttyann.scriptblockplus.script.option.other.ItemCost;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/**
 * ScriptBlockPlus JoinQuitListener クラス
 * @author yuttyann44581
 */
public class PlayerListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoin(PlayerJoinEvent event) {
		BaseSBPlayer sbPlayer = (BaseSBPlayer) SBPlayer.fromPlayer(event.getPlayer());
		sbPlayer.setOnline(true);
		if (!sbPlayer.getOldBlockCoords().isPresent()) {
			sbPlayer.setOldBlockCoords(new BlockCoords(sbPlayer.getLocation()).subtract(0, 1, 0));
		}
		if (sbPlayer.isOp()) {
			ScriptBlock.getInstance().checkUpdate(sbPlayer, false);
		}

		// ItemCost アイテム返却
		ObjectMap objectMap = sbPlayer.getObjectMap();
		if (objectMap.has(ItemCost.KEY_PLAYER)) {
			sbPlayer.getInventory().setContents(objectMap.get(ItemCost.KEY_PLAYER, new ItemStack[0]));
			objectMap.remove(ItemCost.KEY_PLAYER);
			Utils.updateInventory(sbPlayer.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerQuit(PlayerQuitEvent event) {
		BaseSBPlayer sbPlayer = (BaseSBPlayer) SBPlayer.fromPlayer(event.getPlayer());
		sbPlayer.setOnline(false);
		sbPlayer.setScriptLine(null);
		sbPlayer.setScriptEdit(null);
		sbPlayer.setSBClipboard(null);

		CuboidRegion region = ((CuboidRegion) sbPlayer.getRegion());
		region.setWorld(null);
		region.setPos1(null);
		region.setPos2(null);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerItemHeld(PlayerItemHeldEvent event) {
		Player player = event.getPlayer();
		Optional<ItemStack> newSlot = Optional.ofNullable(player.getInventory().getItem(event.getNewSlot()));
		Optional<ItemStack> oldSlot = Optional.ofNullable(player.getInventory().getItem(event.getPreviousSlot()));
		newSlot.ifPresent(i -> ItemAction.callSlot(player, i, true));
		oldSlot.ifPresent(i -> ItemAction.callSlot(player, i, false));
	}
}