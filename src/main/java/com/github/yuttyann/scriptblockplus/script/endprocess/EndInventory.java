package com.github.yuttyann.scriptblockplus.script.endprocess;

import com.github.yuttyann.scriptblockplus.script.SBRead;
import com.github.yuttyann.scriptblockplus.script.option.other.ItemCost;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus EndInventory エンドプロセスクラス
 * @author yuttyann44581
 */
public class EndInventory implements EndProcess {

    public static final ItemStack[] EMPTY_ARRAY = new ItemStack[0];

    @Override
    @NotNull
    public EndProcess newInstance() {
        return new EndInventory();
    }

    @Override
    public void success(@NotNull SBRead sbRead) {
        var sbPlayer = sbRead.getSBPlayer();
        StreamUtils.ifAction(sbPlayer.isOnline(), () -> sbPlayer.getPlayer().updateInventory());
    }

    @Override
    public void failed(@NotNull SBRead sbRead) {
        var items = sbRead.get(ItemCost.KEY_OPTION, EMPTY_ARRAY);
        if (items.length > 0) {
            var sbPlayer = sbRead.getSBPlayer();
            if (sbPlayer.isOnline()) {
                try {
                    sbPlayer.getInventory().setContents(items);
                } finally {
                    sbPlayer.getPlayer().updateInventory();
                }
            } else {
                var objectMap = sbRead.getSBPlayer().getObjectMap();
                if (!objectMap.has(ItemCost.KEY_PLAYER)) {
                    objectMap.put(ItemCost.KEY_PLAYER, items);
                }
            }
        }
    }
}