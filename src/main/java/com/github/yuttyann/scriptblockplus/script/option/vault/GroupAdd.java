package com.github.yuttyann.scriptblockplus.script.option.vault;

import com.github.yuttyann.scriptblockplus.script.hook.HookPlugins;
import com.github.yuttyann.scriptblockplus.script.hook.VaultPermission;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus GroupAdd オプションクラス
 * @author yuttyann44581
 */
public class GroupAdd extends BaseOption {

	public GroupAdd() {
		super("group_add", "@groupADD:");
	}

	@Override
	@NotNull
	public Option newInstance() {
		return new GroupAdd();
	}

	@Override
	protected boolean isValid() throws Exception {
		VaultPermission vaultPermission = HookPlugins.getVaultPermission();
		if (!vaultPermission.isEnabled() || vaultPermission.isSuperPerms()) {
			throw new UnsupportedOperationException();
		}
		String[] array = StringUtils.split(getOptionValue(), "/");
		String world = array.length > 1 ? array[0] : null;
		String group = array.length > 1 ? array[1] : array[0];

		Player player = getPlayer();
		if (!vaultPermission.playerInGroup(world, player, group)) {
			vaultPermission.playerAddGroup(world, player, group);
		}
		return true;
	}
}