package com.github.yuttyann.scriptblockplus.script.option.vault;

import com.github.yuttyann.scriptblockplus.file.Lang;
import com.github.yuttyann.scriptblockplus.manager.ScriptManager;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class Perm extends BaseOption {

	public Perm(ScriptManager scriptManager) {
		super(scriptManager, "", "@perm:");
	}

	@Override
	public boolean isValid() {
		String[] array = StringUtils.split(optionData, "/");
		String world = array.length > 1 ? array[1] : null;
		if (!vaultPermission.playerHas(world, player, array[0])) {
			Utils.sendPluginMessage(player, Lang.getNotPermissionMessage());
		}
		return true;
	}
}