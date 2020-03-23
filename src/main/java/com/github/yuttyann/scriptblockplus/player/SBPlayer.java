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

import java.util.Optional;
import java.util.UUID;

public interface SBPlayer extends CommandSender {

	@NotNull
	static SBPlayer fromPlayer(@NotNull OfflinePlayer player) {
		return fromUUID(player.getUniqueId());
	}

	@NotNull
	static SBPlayer fromUUID(@NotNull UUID uuid) {
		return BaseSBPlayer.getSBPlayer(uuid);
	}

	boolean isOnline();

	@Nullable
	Player getPlayer();

	@NotNull
	OfflinePlayer getOfflinePlayer();

	@NotNull
	PlayerInventory getInventory();

	@NotNull
	UUID getUniqueId();

	@NotNull
	World getWorld();

	@NotNull
	Location getLocation();

	@Nullable
	ItemStack getItemInMainHand();

	@Nullable
	ItemStack getItemInOffHand();

	@NotNull
	Region getRegion();

	@NotNull
	PlayerCount getPlayerCount();

	@NotNull
	ObjectMap getObjectMap();

	void setClipboard(@Nullable SBClipboard clipboard);

	void setScriptLine(@Nullable String scriptLine);

	void setActionType(@Nullable String actionType);

	void setOldFullCoords(@Nullable String fullCoords);

	@NotNull
	Optional<SBClipboard> getClipboard();

	@NotNull
	Optional<String> getScriptLine();

	@NotNull
	Optional<String> getActionType();

	@NotNull
	Optional<String> getOldFullCoords();
}