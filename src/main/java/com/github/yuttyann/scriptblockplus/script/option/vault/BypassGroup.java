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

@OptionTag(name = "bypass_group", syntax = "@bypassGROUP:")
public class BypassGroup extends BaseOption {

    @Override
    @NotNull
    public Option newInstance() {
        return new BypassGroup();
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
            String group = array.length > 2 ? array[2] : world;
            if (vaultPermission.playerInGroup(world, player, group)) {
                return Utils.dispatchCommand(player, StringUtils.setColor(array[0]));
            } else {
                try {
                    vaultPermission.playerAddGroup(world, player, group);
                    return Utils.dispatchCommand(player, StringUtils.setColor(array[0]));
                } finally {
                    vaultPermission.playerRemoveGroup(world, player, group);
                }
            }
        });
    }
}