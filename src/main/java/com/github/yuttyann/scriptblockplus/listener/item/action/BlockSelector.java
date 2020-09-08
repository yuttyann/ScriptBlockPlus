package com.github.yuttyann.scriptblockplus.listener.item.action;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.listener.item.ItemAction;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.region.CuboidRegion;
import com.github.yuttyann.scriptblockplus.utils.ItemUtils;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus BlockSelector クラス
 * @author yuttyann44581
 */
public class BlockSelector extends ItemAction {

    public BlockSelector() {
        super(ItemUtils.getBlockSelector());
    }

    @Override
    public boolean hasPermission(@NotNull Permissible permissible) {
        return Permission.TOOL_BLOCK_SELECTOR.has(permissible);
    }

    @Override
    public boolean run() {
        SBPlayer sbPlayer = SBPlayer.fromPlayer(player);
        CuboidRegion region = ((CuboidRegion) sbPlayer.getRegion());
        switch (action) {
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
                if (isSneaking) {
                    region.setPos1((location = player.getLocation()).toVector());
                } else if (!isAIR) {
                    region.setPos1(location.toVector());
                }
                if (location != null) {
                    region.setWorld(location.getWorld());
                    SBConfig.SELECTOR_POS1.replace(region.getName(), BlockCoords.getCoords(location)).send(sbPlayer);
                }
                break;
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                if (isSneaking) {
                    region.setPos2((location = player.getLocation()).toVector());
                } else if (!isAIR) {
                    region.setPos2(location.toVector());
                }
                if (location != null) {
                    region.setWorld(location.getWorld());
                    SBConfig.SELECTOR_POS2.replace(region.getName(), BlockCoords.getCoords(location)).send(sbPlayer);
                }
                break;
            default:
        }
        return true;
    }
}