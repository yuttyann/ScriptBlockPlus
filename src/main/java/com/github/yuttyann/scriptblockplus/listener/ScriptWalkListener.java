package com.github.yuttyann.scriptblockplus.listener;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.event.ScriptBlockWalkEvent;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.json.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus ScriptWalkListener クラス
 * @author yuttyann44581
 */
public class ScriptWalkListener extends ScriptListener {

	public ScriptWalkListener(@NotNull ScriptBlock plugin) {
		super(plugin, ScriptType.WALK);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		SBPlayer sbPlayer = SBPlayer.fromPlayer(event.getPlayer());
		BlockCoords blockCoords = new BlockCoords(sbPlayer.getLocation()).subtract(0, 1, 0);
		if (blockCoords.equals(sbPlayer.getOldBlockCoords().orElse(null))) {
			return;
		} else {
			sbPlayer.setOldBlockCoords(blockCoords);
		}
		Location location = blockCoords.toLocation();
		if (BlockScriptJson.has(location, scriptType)) {
			ScriptBlockWalkEvent walkEvent = new ScriptBlockWalkEvent(sbPlayer.getPlayer(), location.getBlock());
			Bukkit.getPluginManager().callEvent(walkEvent);
			if (walkEvent.isCancelled()) {
				return;
			}
			if (!Permission.has(sbPlayer, ScriptType.WALK, false)) {
				SBConfig.NOT_PERMISSION.send(sbPlayer);
				return;
			}
			new ScriptRead(sbPlayer.getPlayer(), location, this).read(0);
		}
	}
}