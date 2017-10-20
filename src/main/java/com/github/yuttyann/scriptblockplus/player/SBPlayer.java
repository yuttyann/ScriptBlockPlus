package com.github.yuttyann.scriptblockplus.player;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.script.Clipboard;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public abstract interface SBPlayer extends ObjectData, CommandSender {

	public static SBPlayer get(CommandSender sender) {
		if (!(sender instanceof Player)) {
			return null;
		}
		Player player = (Player) sender;
		SBPlayer sbPlayer = get(player.getUniqueId());
		sbPlayer.setPlayer(player);
		return sbPlayer;
	}

	public static SBPlayer get(UUID uuid) {
		SBPlayer sbPlayer = BasePlayer.players.get(uuid);
		if (sbPlayer == null && Utils.getOfflinePlayer(uuid) != null) {
			sbPlayer = new BasePlayer(uuid);
			BasePlayer.players.put(uuid, sbPlayer);
		}
		return sbPlayer;
	}

	public abstract UUID getUniqueId();

	public abstract Location getLocation();

	public abstract void setPlayer(Player player);

	public abstract Player getPlayer();

	public abstract OfflinePlayer getOfflinePlayer();

	public abstract boolean updatePlayer();

	public abstract boolean isOnline();

	public abstract void setScriptLine(String scriptLine);

	public abstract String getScriptLine();

	public abstract boolean hasScriptLine();

	public abstract void setClickAction(String clickAction);

	public abstract String getClickAction();

	public abstract boolean hasClickAction();

	public abstract void setOldFullCoords(String fullCoords);

	public abstract String getOldFullCoords();

	public abstract boolean hasOldFullCoords();

	public abstract void setClipboard(Clipboard clipboard);

	public abstract Clipboard getClipboard();

	public abstract boolean hasClipboard();
}