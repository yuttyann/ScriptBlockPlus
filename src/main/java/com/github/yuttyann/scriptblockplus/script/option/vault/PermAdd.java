package com.github.yuttyann.scriptblockplus.script.option.vault;

import com.github.yuttyann.scriptblockplus.manager.ScriptManager;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public class PermAdd extends BaseOption {

	public PermAdd(ScriptManager scriptManager) {
		super(scriptManager, "", "@permADD:");
	}

	@Override
	public boolean isValid() {
		String[] array = StringUtils.split(optionData, "/");
		String world = array.length > 1 ? array[1] : null;
		vaultPermission.playerAdd(world, player, array[0]);
		return true;
	}
}