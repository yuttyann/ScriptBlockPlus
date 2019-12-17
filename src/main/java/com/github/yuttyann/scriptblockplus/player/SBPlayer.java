package com.github.yuttyann.scriptblockplus.player;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.github.yuttyann.scriptblockplus.region.Region;
import com.github.yuttyann.scriptblockplus.script.SBClipboard;

public interface SBPlayer extends CommandSender {

	public static SBPlayer fromPlayer(OfflinePlayer player) {
		return player == null ? null : fromUUID(player.getUniqueId());
	}

	public static SBPlayer fromUUID(UUID uuid) {
		return uuid == null ? null : BaseSBPlayer.getSBPlayer(uuid);
	}

	public Player getPlayer();

	public OfflinePlayer getOfflinePlayer();

	public PlayerInventory getInventory();

	public UUID getUniqueId();

	public World getWorld();

	public Location getLocation();

	public ItemStack getItemInMainHand();

	public ItemStack getItemInOffHand();

	public boolean isOnline();

	public Region getRegion();

	public PlayerCount getPlayerCount();

	public ObjectMap getObjectMap();

	public void setClipboard(SBClipboard clipboard);

	public SBClipboard getClipboard();

	public boolean hasClipboard();

	public void setScriptLine(String scriptLine);

	public String getScriptLine();

	public boolean hasScriptLine();

	public void setActionType(String actionType);

	public String getActionType();

	public boolean hasActionType();

	public void setOldFullCoords(String fullCoords);

	public String getOldFullCoords();

	public boolean hasOldFullCoords();
}