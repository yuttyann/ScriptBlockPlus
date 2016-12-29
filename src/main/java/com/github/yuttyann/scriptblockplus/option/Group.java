package com.github.yuttyann.scriptblockplus.option;

import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.collplugin.CollPlugins;
import com.github.yuttyann.scriptblockplus.collplugin.VaultPermission;
import com.github.yuttyann.scriptblockplus.collplugin.VaultPermission.PermType;

public class Group {

	private PermType permType;
	private String group;
	private VaultPermission permission;

	public Group(String group, PermType permType) {
		this.group = group;
		this.permType = permType;
		this.permission = CollPlugins.getVaultPermission();
	}

	public String getName() {
		return group;
	}

	public boolean playerGroup(Player player) {
		switch (permType) {
		case CHECK:
			return permission.playerInGroup(player, group);
		case ADD:
			if (!permission.playerInGroup(player, group))
				return permission.playerAddGroup(player, group);
		case REMOVE:
			if (permission.playerInGroup(player, group))
				return permission.playerRemoveGroup(player, group);
		default:
			return false;
		}
	}
}
