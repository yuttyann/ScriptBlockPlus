package com.github.yuttyann.scriptblockplus.player;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.script.Clipboard;

public abstract interface SBPlayer extends ObjectData, CommandSender {

	public static SBPlayer get(Player player) {
		return player == null ? null : get(player.getUniqueId());
	}

	public static SBPlayer get(UUID uuid) {
		if (uuid == null) {
			return null;
		}
		BasePlayer player = BasePlayer.players.get(uuid);
		if (player == null) {
			player = new BasePlayer(uuid);
			BasePlayer.players.put(uuid, player);
		}
		return player;
	}

	public abstract UUID getUniqueId();

	public abstract Location getLocation();

	public abstract void setPlayer(Player player);

	public abstract Player getPlayer();

	public abstract OfflinePlayer getOfflinePlayer();

	public abstract boolean isOnline();

	public abstract void setClipboard(Clipboard clipboard);

	public abstract Clipboard getClipboard();

	public abstract boolean hasClipboard();

	public abstract void setScriptLine(String scriptLine);

	public abstract String getScriptLine();

	public abstract boolean hasScriptLine();

	public abstract void setClickAction(String clickAction);

	public abstract String getClickAction();

	public abstract boolean hasClickAction();

	public abstract void setOldFullCoords(String fullCoords);

	public abstract String getOldFullCoords();

	public abstract boolean hasOldFullCoords();
}