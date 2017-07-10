package com.github.yuttyann.scriptblockplus.script.option.vault;

import com.github.yuttyann.scriptblockplus.manager.ScriptManager;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public class PermRemove extends BaseOption {

	public PermRemove(ScriptManager scriptManager) {
		super(scriptManager, "", "@permREMOVE:");
	}

	@Override
	public boolean isValid() {
		String[] array = StringUtils.split(optionData, "/");
		String world = array.length > 1 ? array[1] : null;
		if (vaultPermission.playerHas(world, player, array[0])) {
			vaultPermission.playerRemove(world, player, array[0]);
		}
		return true;
	}
}