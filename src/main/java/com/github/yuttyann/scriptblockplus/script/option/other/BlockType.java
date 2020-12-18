package com.github.yuttyann.scriptblockplus.script.option.other;

import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
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
		Block block = getLocation().getBlock();
		for (String type : StringUtils.split(getOptionValue(), ",")) {
			if (equals(block, type)) {
				return true;
			}
		}
		return false;
	}

	private boolean equals(@NotNull Block block, @NotNull String blockType) throws IllegalAccessException {
		if (StringUtils.isEmpty(blockType)) {
			return false;
		}
		String[] array = StringUtils.split(blockType, ":");
		if (Calculation.REALNUMBER_PATTERN.matcher(array[0]).matches()) {
			throw new IllegalAccessException("Numerical values can not be used");
		}
		Material type = Material.getMaterial(array[0]);
		if (type == null || !type.isBlock()) {
			return false;
		}
		byte data = array.length == 2 ? Byte.parseByte(array[1]) : -1;
		return type == block.getType() && (data == -1 || data == getData(block));
	}

	private byte getData(@NotNull Block block) {
		@SuppressWarnings("deprecation")
		byte data = block.getData();
		return data;
	}
}