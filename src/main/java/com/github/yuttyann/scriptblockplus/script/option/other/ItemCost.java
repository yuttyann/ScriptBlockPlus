package com.github.yuttyann.scriptblockplus.script.option.other;

import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;
import com.github.yuttyann.scriptblockplus.utils.ItemUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus ItemCost オプションクラス
 * @author yuttyann44581
 */
@OptionTag(name = "itemcost", syntax = "$item:")
public class ItemCost extends BaseOption {

    public static final String KEY_OPTION = Utils.randomUUID();
    public static final String KEY_PLAYER = Utils.randomUUID();

    @Override
    @NotNull
    public Option newInstance() {
        return new ItemCost();
    }

    @Override
    protected boolean isValid() throws Exception {
        var array = StringUtils.split(getOptionValue(), ' ');
        var param = StringUtils.split(ItemUtils.removeKey(array[0]), ':');
        if (Calculation.REALNUMBER_PATTERN.matcher(param[0]).matches()) {
            throw new IllegalAccessException("Numerical values can not be used");
        }
        var material = ItemUtils.getMaterial(param[0]);
        int damage = param.length > 1 ? Integer.parseInt(param[1]) : 0;
        int amount = Integer.parseInt(array[1]);
        var name = StringUtils.setColor(array.length > 2 ? StringUtils.createString(array, 2) : null);

        var player = getPlayer();
        var inventory = player.getInventory();
        if (!getTempMap().has(KEY_OPTION)) {
            getTempMap().put(KEY_OPTION, copyItems(inventory.getContents()));
        }
        var items = inventory.getContents();
        int result = amount;
        for (var item : items) {
            if (equals(item, material, name, damage)) {
                result -= result > 0 ? setAmount(item, item.getAmount() - result) : 0;
            }
        }
        if (result > 0) {
            SBConfig.ERROR_ITEM.replace(material, amount, damage, name).send(player);
            return false;
        }
        inventory.setContents(items);
        return true;
    }

    private int setAmount(@NotNull ItemStack item, int amount) {
        int oldAmount = item.getAmount();
        item.setAmount(amount);
        return oldAmount;
    }

    private boolean equals(@Nullable ItemStack item, @Nullable Material material, @NotNull String name, int damage) {
        if (item == null || ItemUtils.getDamage(item) != damage) {
            return false;
        }
        return ItemUtils.isItem(item, material, StringUtils.isEmpty(name) ? item.getType().name() : name);
    }

    @NotNull
    private ItemStack[] copyItems(ItemStack[] items) {
        var copy = new ItemStack[items.length];
        for (int i = 0; i < copy.length; i++) {
            copy[i] = (items[i] == null ? new ItemStack(Material.AIR) : items[i].clone());
        }
        return copy;
    }
}