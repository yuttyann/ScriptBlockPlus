package com.github.yuttyann.scriptblockplus.script.option.other;

import org.bukkit.Material;
import org.bukkit.block.Block;

import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public class BlockType extends BaseOption {

	public BlockType() {
		super("blocktype", "@blocktype:");
	}

	private class BlockData {

		private final Material type;
		private final byte data;

		public BlockData(String type) {
			String[] array = StringUtils.split(type, ":");
			this.type = Material.getMaterial(array[0]);
			this.data = array.length == 2 ? Byte.parseByte(array[1]) : 0;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Block) || type == null || !type.isBlock()) {
				return false;
			}
			Block block = (Block) obj;
			return type == block.getType() && data == getData(block);
		}

		private byte getData(Block block) {
			@SuppressWarnings("deprecation")
			byte data = block.getData();
			return data;
		}
	}

	@Override
	protected boolean isValid() throws Exception {
		String[] array = StringUtils.split(getOptionValue(), ",");
		Block block = getLocation().getBlock();
		return StreamUtils.anyMatch(array, s -> new BlockData(s).equals(block));
	}
}