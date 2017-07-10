package com.github.yuttyann.scriptblockplus.script.option.vault;

import com.github.yuttyann.scriptblockplus.manager.ScriptManager;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public class GroupAdd extends BaseOption {

	public GroupAdd(ScriptManager scriptManager) {
		super(scriptManager, "groupadd", "@groupADD:");
	}

	@Override
	public boolean isValid() {
		String[] array = StringUtils.split(optionData, "/");
		String world = array.length > 1 ? array[1] : null;
		vaultPermission.playerAddGroup(world, player, array[0]);
		return true;
	}
}