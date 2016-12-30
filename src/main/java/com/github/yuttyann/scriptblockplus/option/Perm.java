package com.github.yuttyann.scriptblockplus.option;

import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.collplugin.CollPlugins;
import com.github.yuttyann.scriptblockplus.collplugin.VaultPermission;
import com.github.yuttyann.scriptblockplus.collplugin.VaultPermission.PermType;

public class Perm {

	private PermType permType;
	private String perm;
	private VaultPermission permission;

	public Perm(String perm, PermType permType) {
		this.perm = perm;
		this.permType = permType;
		this.permission = CollPlugins.getVaultPermission();
	}

	public String getNode() {
		return perm;
	}

	public boolean playerPerm(Player player) {
		switch (permType) {
		case CHECK:
			return permission.has(player, perm);
		case ADD:
			if (!permission.has(player, perm)) {
				return permission.playerAdd(player, perm);
			}
		case REMOVE:
			if (permission.has(player, perm)) {
				return permission.playerRemove(player, perm);
			}
		default:
			return false;
		}
	}
}
