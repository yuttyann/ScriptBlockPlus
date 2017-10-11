package com.github.yuttyann.scriptblockplus.script.option.vault;

import com.github.yuttyann.scriptblockplus.script.hook.VaultPermission;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public class PermRemove extends BaseOption {

	public PermRemove() {
		super("permremove", "@permREMOVE:");
	}

	@Override
	public boolean isValid() {
		VaultPermission vaultPermission = getVaultPermission();
		if (!vaultPermission.isEnabled()) {
			return false;
		}
		String[] array = StringUtils.split(getOptionValue(), "/");
		String world = array.length > 1 ? array[1] : null;
		if (vaultPermission.playerHas(world, getPlayer(), array[0])) {
			vaultPermission.playerRemove(world, getPlayer(), array[0]);
		}
		return true;
	}
}