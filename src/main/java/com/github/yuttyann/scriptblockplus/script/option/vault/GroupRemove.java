package com.github.yuttyann.scriptblockplus.script.option.vault;

import com.github.yuttyann.scriptblockplus.manager.ScriptManager;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public class GroupRemove extends BaseOption {

	public GroupRemove(ScriptManager scriptManager) {
		super(scriptManager, "groupremove", "@groupREMOVE:");
	}

	@Override
	public boolean isValid() {
		String[] array = StringUtils.split(optionData, "/");
		String world = array.length > 1 ? array[1] : null;
		if (vaultPermission.playerInGroup(world, player,  array[0])) {
			vaultPermission.playerRemoveGroup(world, player,  array[0]);
		}
		return true;
	}
}