package com.github.yuttyann.scriptblockplus.script.option.vault;

import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.script.hook.HookPlugins;
import com.github.yuttyann.scriptblockplus.script.hook.VaultPermission;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Perm extends BaseOption {

	public Perm() {
		super("permission", "@perm:");
	}

	@Override
	@NotNull
	public Option newInstance() {
		return new Perm();
	}

	@Override
	protected boolean isValid() throws Exception {
		VaultPermission vaultPermission = HookPlugins.getVaultPermission();
		if (!vaultPermission.isEnabled()) {
			throw new UnsupportedOperationException();
		}
		String[] array = StringUtils.split(getOptionValue(), "/");
		String world = array.length > 1 ? array[0] : null;
		String permission = array.length > 1 ? array[1] : array[0];

		Player player = getPlayer();
		if (!has(vaultPermission, world, player, permission)) {
			SBConfig.NOT_PERMISSION.send(player);
			return false;
		}
		return true;
	}

	private boolean has(VaultPermission vaultPermission, String world, Player player, String permission) {
		if (world == null) {
			return vaultPermission.has(player, permission);
		}
		return !vaultPermission.isSuperPerms() && vaultPermission.playerHas(world, player, permission);
	}
}