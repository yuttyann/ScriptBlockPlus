package com.github.yuttyann.scriptblockplus.script.option.vault;

import com.github.yuttyann.scriptblockplus.script.hook.HookPlugins;
import com.github.yuttyann.scriptblockplus.script.hook.VaultPermission;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus PermAdd オプションクラス
 * @author yuttyann44581
 */
public class PermAdd extends BaseOption {

	public PermAdd() {
		super("perm_add", "@permADD:");
	}

	@Override
	@NotNull
	public Option newInstance() {
		return new PermAdd();
	}

	@Override
	protected boolean isValid() throws Exception {
		VaultPermission vaultPermission = HookPlugins.getVaultPermission();
		if (!vaultPermission.isEnabled() || vaultPermission.isSuperPerms()) {
			throw new UnsupportedOperationException();
		}
		String[] array = StringUtils.split(getOptionValue(), "/");
		String world = array.length > 1 ? array[0] : null;
		String permission = array.length > 1 ? array[1] : array[0];

		Player player = getPlayer();
		if (!has(vaultPermission, world, player, permission)) {
			vaultPermission.playerAdd(world, player, permission);
		}
		return true;
	}

	private boolean has(VaultPermission vaultPermission, String world, Player player, String permission) {
		if (world == null) {
			return vaultPermission.has(player, permission);
		}
		return vaultPermission.playerHas(world, player, permission);
	}
}