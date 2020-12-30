package com.github.yuttyann.scriptblockplus.script.option.other;

import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;
import com.github.yuttyann.scriptblockplus.utils.ItemUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * ScriptBlockPlus ItemHand オプションクラス
 * @author yuttyann44581
 */
@OptionTag(name = "itemhand", syntax = "@hand:")
public class ItemHand extends BaseOption {

    @Override
    @NotNull
    public Option newInstance() {
        return new ItemHand();
    }

    @Override
    protected boolean isValid() throws Exception {
        var array = StringUtils.split(getOptionValue(), " ");
        var itemData = StringUtils.split(array[0], ":");
        if (Calculation.REALNUMBER_PATTERN.matcher(itemData[0]).matches()) {
            throw new IllegalAccessException("Numerical values can not be used");
        }
        var type = ItemUtils.getMaterial(itemData[0]);
        int damage = itemData.length > 1 ? Integer.parseInt(itemData[1]) : 0;
        int amount = Integer.parseInt(array[1]);
        var create = array.length > 2 ? StringUtils.createString(array, 2) : null;
        var itemName = StringUtils.setColor(create);

        var player = getPlayer();
        var items = ItemUtils.getHandItems(player);
        if (Arrays.stream(items).noneMatch(i -> equals(i, itemName, type, amount, damage))) {
            SBConfig.ERROR_HAND.replace(type, amount, damage, itemName).send(player);
            return false;
        }
        return true;
    }

    private boolean equals(@Nullable ItemStack item, @NotNull String itemName, @Nullable Material type, int amount, int damage) {
        if (item == null || item.getAmount() < amount || ItemUtils.getDamage(item) != damage) {
            return false;
        }
        return ItemUtils.isItem(item, type, StringUtils.isEmpty(itemName) ? item.getType().name() : itemName);
    }
}