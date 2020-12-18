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

    private final SBClipboard clipboard;
    private final CuboidRegionBlocks cuboidRegionBlocks;

    public CuboidRegionPaste(@NotNull SBClipboard clipboard, @NotNull Region region) {
        this.clipboard = clipboard;
        this.cuboidRegionBlocks = new CuboidRegionBlocks(region);
    }

    @NotNull
    public ScriptType getScriptType() {
        return clipboard.getScriptType();
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
            clipboard.lightPaste(block.getLocation(), overwrite);
        }
        clipboard.save();
        return this;
    }
}