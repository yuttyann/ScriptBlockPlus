package com.github.yuttyann.scriptblockplus.region;

import com.github.yuttyann.scriptblockplus.script.SBClipboard;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus CuboidRegionPaste クラス
 * @author yuttyann44581
 */
public class CuboidRegionPaste {

	private final SBClipboard sbClipboard;
	private final CuboidRegionBlocks cuboidRegionBlocks;

	public CuboidRegionPaste(@NotNull SBClipboard sbClipboard, @NotNull Region region) {
		this.sbClipboard = sbClipboard;
		this.cuboidRegionBlocks = new CuboidRegionBlocks(region);
	}

	@NotNull
	public ScriptType getScriptType() {
		return sbClipboard.getScriptType();
	}

	@NotNull
	public CuboidRegionBlocks getRegionBlocks() {
		return cuboidRegionBlocks;
	}

	public CuboidRegionPaste paste(boolean pasteonair, boolean overwrite) {
		for (Block block : cuboidRegionBlocks.getBlocks()) {
			if (!pasteonair && (block == null || block.getType() == Material.AIR)) {
				continue;
			}
			sbClipboard.lightPaste(block.getLocation(), overwrite);
		}
		sbClipboard.save();
		return this;
	}
}