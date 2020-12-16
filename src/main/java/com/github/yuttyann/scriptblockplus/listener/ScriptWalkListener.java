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
import org.bukkit.entity.Player;
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
		Player player = event.getPlayer();
		SBPlayer sbPlayer = SBPlayer.fromPlayer(player);
		Location location = player.getLocation().clone().subtract(0.0D, 1.0D, 0.0D);
		String fullCoords = BlockCoords.getFullCoords(location);
		if (fullCoords.equals(sbPlayer.getOldFullCoords().orElse(null))) {
			return;
		}
		sbPlayer.setOldFullCoords(fullCoords);
		if (BlockScriptJson.has(location, scriptType)) {
			ScriptBlockWalkEvent walkEvent = new ScriptBlockWalkEvent(player, location.getBlock());
			Bukkit.getPluginManager().callEvent(walkEvent);
			if (walkEvent.isCancelled()) {
				return;
			}
			if (!Permission.has(player, ScriptType.WALK, false)) {
				SBConfig.NOT_PERMISSION.send(sbPlayer);
				return;
			}
			new ScriptRead(player, location, this).read(0);
		}
	}
}