package com.github.yuttyann.scriptblockplus.script.option.vault;

import com.github.yuttyann.scriptblockplus.file.Lang;
import com.github.yuttyann.scriptblockplus.manager.ScriptManager;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class Group extends BaseOption {

	public Group(ScriptManager scriptManager) {
		super(scriptManager, "group", "@group:");
	}

	@Override
	public boolean isValid() {
		String[] array = StringUtils.split(optionData, "/");
		String world = array.length > 1 ? array[1] : null;
		if (!vaultPermission.playerInGroup(world, player, array[0])) {
			Utils.sendPluginMessage(player, Lang.getErrorGroupMessage(array[0]));
		}
		return true;
	}
}