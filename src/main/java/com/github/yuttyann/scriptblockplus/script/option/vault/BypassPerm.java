package com.github.yuttyann.scriptblockplus.script.option.vault;

import com.github.yuttyann.scriptblockplus.enums.LogAdmin;
import com.github.yuttyann.scriptblockplus.hook.plugin.VaultPermission;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@OptionTag(name = "bypass_perm", syntax = "@bypassPERM:")
public class BypassPerm extends BaseOption {

    @Override
    @NotNull
    public Option newInstance() {
        return new BypassPerm();
    }

    @Override
    protected boolean isValid() throws Exception {
        VaultPermission vaultPermission = VaultPermission.INSTANCE;
        if (!vaultPermission.isEnabled() || vaultPermission.isSuperPerms()) {
            throw new UnsupportedOperationException();
        }
        String[] array = StringUtils.split(getOptionValue(), "/");
        if (array.length < 2) {
            throw new IllegalArgumentException("Insufficient parameters");
        }
        Player player = getSBPlayer().getPlayer();
        return LogAdmin.supplier(player.getWorld(), () -> {
            String world = array[1];
            String permission = array.length > 2 ? array[2] : world;
            if (vaultPermission.playerHas(world, player, permission)) {
                return Utils.dispatchCommand(player, StringUtils.setColor(array[0]));
            } else {
                try {
                    vaultPermission.playerAdd(world, player, permission);
                    return Utils.dispatchCommand(player, StringUtils.setColor(array[0]));
                } finally {
                    vaultPermission.playerRemove(world, player, permission);
                }
            }
        });
    }
}