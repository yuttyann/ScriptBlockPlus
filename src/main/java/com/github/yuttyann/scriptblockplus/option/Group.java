package com.github.yuttyann.scriptblockplus.option;

import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.collplugin.CollPlugins;
import com.github.yuttyann.scriptblockplus.collplugin.VaultPermission;
import com.github.yuttyann.scriptblockplus.enums.PermType;

public class Group {

	private String world;
	private String group;
	private PermType permType;
	private VaultPermission permission;

	public Group(String group, PermType permType) {
		this(null, group, permType);
	}

	public Group(String world, String group, PermType permType) {
		this.world = world;
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
			if (world != null) {
				return permission.playerInGroup(world, player, group);
			}
			return permission.playerInGroup(player, group);
		case ADD:
			if (world != null) {
				return permission.playerAddGroup(world, player, group);
			}
			return permission.playerAddGroup(player, group);
		case REMOVE:
			if (world != null && permission.playerInGroup(world, player, group)) {
				return permission.playerRemoveGroup(world, player, group);
			} else if (world == null && permission.playerInGroup(player, group)) {
				return permission.playerRemoveGroup(player, group);
			}
			break;
		}
		return false;
	}
}