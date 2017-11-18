package com.github.yuttyann.scriptblockplus.script.option.vault;

import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.script.hook.HookPlugins;
import com.github.yuttyann.scriptblockplus.script.hook.VaultPermission;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public class PermRemove extends BaseOption {

	public PermRemove() {
		super("perm_remove", "@permREMOVE:");
	}

	@Override
	protected boolean isValid() throws Exception {
		VaultPermission vaultPermission = HookPlugins.getVaultPermission();
		if (!vaultPermission.isEnabled() || vaultPermission.isSuperPerms()) {
			throw new UnsupportedOperationException();
		}
		String[] array = StringUtils.split(getOptionValue(), "/");
		String world = array.length > 1 ? array[0] : null;
		String permission = array.length > 1 ? array[1] : array[0];

		Player player = getPlayer();
		if (has(vaultPermission, world, player, permission)) {
			vaultPermission.playerRemove(world, player, permission);
		}
		return true;
	}

	private boolean has(VaultPermission vaultPermission, String world, Player player, String permission) {
		if (world == null) {
			return vaultPermission.has(player, permission);
		}
		return vaultPermission.playerHas(world, player, permission);
	}
}