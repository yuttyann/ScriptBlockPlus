package com.github.yuttyann.scriptblockplus.player;

import com.github.yuttyann.scriptblockplus.region.Region;
import com.github.yuttyann.scriptblockplus.script.SBClipboard;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface SBPlayer extends CommandSender {

	@NotNull
	public static SBPlayer fromPlayer(@NotNull OfflinePlayer player) {
		return fromUUID(player.getUniqueId());
	}

	@NotNull
	public static SBPlayer fromUUID(@NotNull UUID uuid) {
		return BaseSBPlayer.getSBPlayer(uuid);
	}

	@Nullable
	public Player getPlayer();

	@NotNull
	public OfflinePlayer getOfflinePlayer();

	@Nullable
	public PlayerInventory getInventory();

	@NotNull
	public UUID getUniqueId();

	@Nullable
	public World getWorld();

	@Nullable
	public Location getLocation();

	@Nullable
	public ItemStack getItemInMainHand();

	@Nullable
	public ItemStack getItemInOffHand();

	public boolean isOnline();

	@NotNull
	public Region getRegion();

	@NotNull
	public PlayerCount getPlayerCount();

	@NotNull
	public ObjectMap getObjectMap();

	public void setClipboard(@Nullable SBClipboard clipboard);

	@Nullable
	public SBClipboard getClipboard();

	public boolean hasClipboard();

	public void setScriptLine(@Nullable String scriptLine);

	@Nullable
	public String getScriptLine();

	public boolean hasScriptLine();

	public void setActionType(@Nullable String actionType);

	@Nullable
	public String getActionType();

	public boolean hasActionType();

	public void setOldFullCoords(@Nullable String fullCoords);

	@Nullable
	public String getOldFullCoords();

	public boolean hasOldFullCoords();
}