package com.github.yuttyann.scriptblockplus.script;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.script.ScriptEdit.Clipboard;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class SBPlayer {

	private static final Map<UUID, SBPlayer> players;

	static {
		int length = Bukkit.getOfflinePlayers().length;
		players = new HashMap<UUID, SBPlayer>(length);
	}

	private final UUID uuid;
	private String script;
	private String clickAction;
	private String oldFullCoords;
	private Double moneyCost;
	private Clipboard clipboard;

	private SBPlayer(UUID uuid) {
		this.uuid = uuid;
	}

	public static SBPlayer get(UUID uuid) {
		SBPlayer sbPlayer = players.get(uuid);
		if (sbPlayer == null) {
			sbPlayer = new SBPlayer(uuid);
			players.put(uuid, sbPlayer);
		}
		return sbPlayer;
	}

	public static SBPlayer get(CommandSender sender) {
		if (!(sender instanceof Player)) {
			return null;
		}
		return get(((Player) sender).getUniqueId());
	}

	public Player getPlayer() {
		return Utils.getPlayer(uuid);
	}

	public String getName() {
		Player player = getPlayer();
		return player == null ? null : player.getName();
	}

	public UUID getUniqueId() {
		return uuid;
	}

	public Location getLocation() {
		Player player = getPlayer();
		return player == null ? null : player.getLocation();
	}

	public boolean isOnline() {
		Player player = getPlayer();
		return player != null && player.isOnline();
	}

	public void setScript(String script) {
		this.script = script;
	}

	public String getScript() {
		return script;
	}

	public boolean hasScript() {
		return script != null;
	}

	public void setClickAction(String clickAction) {
		this.clickAction = clickAction;
	}

	public String getClickAction() {
		return clickAction;
	}

	public boolean hasClickAction() {
		return clickAction != null;
	}

	public void setOldFullCoords(Location location) {
		this.oldFullCoords = BlockCoords.getFullCoords(location);
	}

	public String getOldFullCoords() {
		return oldFullCoords;
	}

	public boolean hasOldFullCoords() {
		return oldFullCoords != null;
	}

	public void setMoneyCost(Double cost, boolean isAdd) {
		if (isAdd && moneyCost == null) {
			moneyCost = cost;
		} else {
			moneyCost = cost == null ? null : isAdd ? moneyCost + cost : cost;
		}
	}

	public Double getMoneyCost() {
		return moneyCost;
	}

	public boolean hasMoneyCost() {
		return moneyCost != null;
	}

	public void setClipboard(Clipboard clipboard) {
		this.clipboard = clipboard;
	}

	public Clipboard getClipboard() {
		return clipboard;
	}

	public boolean hasClipboard() {
		return clipboard != null;
	}
}