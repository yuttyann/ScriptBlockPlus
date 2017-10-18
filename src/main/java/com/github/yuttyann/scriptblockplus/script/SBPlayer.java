package com.github.yuttyann.scriptblockplus.script;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import com.github.yuttyann.scriptblockplus.script.ScriptEdit.Clipboard;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class SBPlayer implements CommandSender {

	private static final Map<UUID, SBPlayer> players;

	static {
		int length = Bukkit.getOfflinePlayers().length / 2;
		players = new HashMap<UUID, SBPlayer>(length);
	}

	private Player player;
	private UUID uuid;
	private Map<String, Object> objectData;

	private SBPlayer(UUID uuid) {
		this.uuid = uuid;
		this.objectData = new HashMap<String, Object>();
	}

	public static SBPlayer get(CommandSender sender) {
		if (!(sender instanceof Player)) {
			return null;
		}
		Player player = (Player) sender;
		SBPlayer sbPlayer = get(player.getUniqueId());
		sbPlayer.player = player;
		return sbPlayer;
	}

	public static SBPlayer get(UUID uuid) {
		SBPlayer sbPlayer = players.get(uuid);
		if (sbPlayer == null) {
			sbPlayer = new SBPlayer(uuid);
			players.put(uuid, sbPlayer);
		}
		return sbPlayer;
	}

	public void setData(String key, Object value) {
		objectData.put(key, value);
	}

	public <T> T getData(String key) {
		@SuppressWarnings("unchecked")
		T value = (T) objectData.get(key);
		return value;
	}

	public <T> T removeData(String key) {
		@SuppressWarnings("unchecked")
		T value = (T) objectData.remove(key);
		return value;
	}

	public boolean hasData(String key) {
		return getData(key) != null;
	}

	public void clearData() {
		objectData.clear();
	}

	public void setScriptLine(String scriptLine) {
		setData("ScriptLine", scriptLine);
	}

	public String getScriptLine() {
		return getData("ScriptLine");
	}

	public boolean hasScriptLine() {
		return getScriptLine() != null;
	}

	public void setClickAction(String clickAction) {
		setData("ClickAction", clickAction);
	}

	public String getClickAction() {
		return getData("ClickAction");
	}

	public boolean hasClickAction() {
		return getClickAction() != null;
	}

	public void setOldFullCoords(String fullCoords) {
		setData("OldFullCoords", fullCoords);
	}

	public String getOldFullCoords() {
		return getData("OldFullCoords");
	}

	public boolean hasOldFullCoords() {
		return getOldFullCoords() != null;
	}

	public void setClipboard(Clipboard clipboard) {
		setData("Clipboard", clipboard);
	}

	public Clipboard getClipboard() {
		return getData("Clipboard");
	}

	public boolean hasClipboard() {
		return getClipboard() != null;
	}

	@Override
	public Server getServer() {
		return getPlayer().getServer();
	}

	public Player getPlayer() {
		if (player == null) {
			player = Utils.getPlayer(uuid);
		}
		return player;
	}

	public OfflinePlayer getOfflinePlayer() {
		OfflinePlayer player = this.player;
		if (player == null) {
			player = Bukkit.getOfflinePlayer(uuid);
		}
		return player;
	}

	public boolean updatePlayer() {
		return (player = Utils.getPlayer(uuid)) != null;
	}

	@Override
	public String getName() {
		OfflinePlayer player = getOfflinePlayer();
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
		OfflinePlayer player = getOfflinePlayer();
		return player != null && player.isOnline();
	}

	@Override
	public boolean isOp() {
		return getOfflinePlayer().isOp();
	}

	@Override
	public void setOp(boolean value) {
		getOfflinePlayer().setOp(value);
	}

	@Override
	public void sendMessage(String message) {
		getPlayer().sendMessage(message);
	}

	@Override
	public void sendMessage(String[] messages) {
		getPlayer().sendMessage(messages);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin) {
		return getPlayer().addAttachment(plugin);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, int value) {
		return getPlayer().addAttachment(plugin, value);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String permission, boolean value) {
		return getPlayer().addAttachment(plugin, permission, value);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String permission, boolean value, int ticks) {
		return getPlayer().addAttachment(plugin, permission, value, ticks);
	}

	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		return getPlayer().getEffectivePermissions();
	}

	@Override
	public boolean hasPermission(String permission) {
		return getPlayer().hasPermission(permission);
	}

	@Override
	public boolean hasPermission(Permission permission) {
		return getPlayer().hasPermission(permission);
	}

	@Override
	public boolean isPermissionSet(String permission) {
		return getPlayer().isPermissionSet(permission);
	}

	@Override
	public boolean isPermissionSet(Permission permission) {
		return getPlayer().isPermissionSet(permission);
	}

	@Override
	public void recalculatePermissions() {
		getPlayer().recalculatePermissions();
	}

	@Override
	public void removeAttachment(PermissionAttachment attachment) {
		getPlayer().removeAttachment(attachment);
	}
}