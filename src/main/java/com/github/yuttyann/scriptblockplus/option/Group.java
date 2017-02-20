package com.github.yuttyann.scriptblockplus.option;

import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.enums.PermType;
import com.github.yuttyann.scriptblockplus.hookplugin.HookPlugins;
import com.github.yuttyann.scriptblockplus.hookplugin.VaultPermission;

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
		this.permission = HookPlugins.getVaultPermission();
	}

	public String getName() {
		return group;
	}

	public boolean playerGroup(Player player) {
		switch (permType) {
		case CHECK:
			if (world != null) {
				return permission.playerInGroup(world, player, getName());
			}
			return permission.playerInGroup(player, getName());
		case ADD:
			if (world != null) {
				return permission.playerAddGroup(world, player, getName());
			}
			return permission.playerAddGroup(player, getName());
		case REMOVE:
			if (world != null && permission.playerInGroup(world, player, getName())) {
				return permission.playerRemoveGroup(world, player, getName());
			} else if (world == null && permission.playerInGroup(player, getName())) {
				return permission.playerRemoveGroup(player, getName());
			}
			break;
		}
		return false;
	}
}