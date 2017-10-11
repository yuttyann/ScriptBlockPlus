package com.github.yuttyann.scriptblockplus.script.option.vault;

import com.github.yuttyann.scriptblockplus.script.hook.VaultPermission;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public class GroupRemove extends BaseOption {

	public GroupRemove() {
		super("groupremove", "@groupREMOVE:");
	}

	@Override
	public boolean isValid() {
		VaultPermission vaultPermission = getVaultPermission();
		if (!vaultPermission.isEnabled()) {
			return false;
		}
		String[] array = StringUtils.split(getOptionValue(), "/");
		String world = array.length > 1 ? array[1] : null;
		if (vaultPermission.playerInGroup(world, getPlayer(),  array[0])) {
			vaultPermission.playerRemoveGroup(world, getPlayer(),  array[0]);
		}
		return true;
	}
}