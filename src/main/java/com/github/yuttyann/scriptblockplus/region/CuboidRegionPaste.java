package com.github.yuttyann.scriptblockplus.region;

import java.util.HashSet;
import java.util.Set;

import com.github.yuttyann.scriptblockplus.file.json.PlayerCountJson;
import com.github.yuttyann.scriptblockplus.script.SBClipboard;
import com.github.yuttyann.scriptblockplus.script.ScriptType;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus CuboidRegionPaste クラス
 * @author yuttyann44581
 */
public class CuboidRegionPaste {

    private final ScriptType scriptType;
    private final SBClipboard sbClipboard;
    private final CuboidRegionBlocks cuboidRegionBlocks;

    public CuboidRegionPaste(@NotNull SBClipboard sbClipboard, @NotNull Region region) {
        this.scriptType = sbClipboard.getScriptType();
        this.sbClipboard = sbClipboard;
        this.cuboidRegionBlocks = new CuboidRegionBlocks(region);
    }

    @NotNull
    public ScriptType getScriptType() {
        return scriptType;
    }

    @NotNull
    public CuboidRegionBlocks getRegionBlocks() {
        return cuboidRegionBlocks;
    }

    public CuboidRegionPaste paste(boolean pasteonair, boolean overwrite) {
        Set<Location> locations = new HashSet<>(cuboidRegionBlocks.getCount());
        for (Block block : cuboidRegionBlocks.getBlocks()) {
            if (!pasteonair && (block == null || block.getType() == Material.AIR)) {
                continue;
            }
            sbClipboard.lightPaste(locations, block.getLocation(), overwrite);
        }
        PlayerCountJson.clear(locations, scriptType);
        sbClipboard.save();
        return this;
    }
}