package com.github.yuttyann.scriptblockplus.script.option.other;

import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public class BlockType extends BaseOption {

	public BlockType() {
		super("blocktype", "@blocktype:");
	}

	@Override
	protected boolean isValid() throws Exception {
		String[] array = StringUtils.split(getOptionValue(), ",");
		String blockType = getBlockCoords().getBlock().getType().name();
		if (StreamUtils.anyMatch(array, s -> blockType.equalsIgnoreCase(s))) {
			return true;
		}
		array = StringUtils.split(getOptionValue(), " ");
		if (array.length > 1) {
			String message = StringUtils.createString(array, 1);
			message = StringUtils.replaceColorCode(message, true);
			getPlayer().sendMessage(message);
		}
		return false;
	}
}