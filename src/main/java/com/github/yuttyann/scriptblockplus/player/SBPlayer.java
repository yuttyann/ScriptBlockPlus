package com.github.yuttyann.scriptblockplus.player;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.script.Clipboard;

public interface SBPlayer extends ObjectData, CommandSender {

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

	public UUID getUniqueId();

	public Location getLocation();

	public void setPlayer(Player player);

	public Player getPlayer();

	public OfflinePlayer getOfflinePlayer();

	public boolean isOnline();

	public void setClipboard(Clipboard clipboard);

	public Clipboard getClipboard();

	public boolean hasClipboard();

	public void setScriptLine(String scriptLine);

	public String getScriptLine();

	public boolean hasScriptLine();

	public void setClickAction(String clickAction);

	public String getClickAction();

	public boolean hasClickAction();

	public void setOldFullCoords(String fullCoords);

	public String getOldFullCoords();

	public boolean hasOldFullCoords();
}