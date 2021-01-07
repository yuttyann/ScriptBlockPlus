package com.github.yuttyann.scriptblockplus.script.option.other;

import java.util.stream.Stream;

import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus BlockType オプションクラス
 * @author yuttyann44581
 */
@OptionTag(name = "blocktype", syntax = "@blocktype:")
public class BlockType extends BaseOption {

    @Override
    @NotNull
    public Option newInstance() {
        return new BlockType();
    }

    @Override
    protected boolean isValid() throws Exception {
        var block = getLocation().getBlock();
        return Stream.of(StringUtils.split(getOptionValue(), ',')).anyMatch(s -> equals(block, s));
    }

    private boolean equals(@NotNull Block block, @NotNull String type) {
        if (StringUtils.isEmpty(type)) {
            return false;
        }
        var array = StringUtils.split(type, ':');
        if (Calculation.REALNUMBER_PATTERN.matcher(array[0]).matches()) {
            Utils.sendColorMessage(getSBPlayer(), "§cNumerical values can not be used");
            return false;
        }
        var material = Material.getMaterial(array[0]);
        if (material == null || !material.isBlock()) {
            return false;
        }
        byte data = array.length == 2 ? Byte.parseByte(array[1]) : -1;
        return material == block.getType() && (data == -1 || data == getData(block));
    }

    private byte getData(@NotNull Block block) {
        @SuppressWarnings("deprecation")
        byte data = block.getData();
        return data;
    }
}