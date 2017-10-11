package com.github.yuttyann.scriptblockplus.script.option.vault;

import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.script.hook.VaultPermission;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class Perm extends BaseOption {

	public Perm() {
		super("perm", "@perm:");
	}

	@Override
	public boolean isValid() {
		VaultPermission vaultPermission = getVaultPermission();
		if (!vaultPermission.isEnabled()) {
			return false;
		}
		String[] array = StringUtils.split(getOptionValue(), "/");
		String world = array.length > 1 ? array[1] : null;
		if (!vaultPermission.playerHas(world, getPlayer(), array[0])) {
			Utils.sendMessage(getPlayer(), SBConfig.getNotPermissionMessage());
			return false;
		}
		return true;
	}
}