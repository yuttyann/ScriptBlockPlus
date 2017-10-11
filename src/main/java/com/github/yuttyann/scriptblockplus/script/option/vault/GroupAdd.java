package com.github.yuttyann.scriptblockplus.script.option.vault;

import com.github.yuttyann.scriptblockplus.script.hook.VaultPermission;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public class GroupAdd extends BaseOption {

	public GroupAdd() {
		super("groupadd", "@groupADD:");
	}

	@Override
	public boolean isValid() {
		VaultPermission vaultPermission = getVaultPermission();
		if (!vaultPermission.isEnabled()) {
			return false;
		}
		String[] array = StringUtils.split(getOptionValue(), "/");
		String world = array.length > 1 ? array[1] : null;
		vaultPermission.playerAddGroup(world, getPlayer(), array[0]);
		return true;
	}
}