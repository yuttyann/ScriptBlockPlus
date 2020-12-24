package com.github.yuttyann.scriptblockplus.region;

import java.util.HashSet;
import java.util.Set;

import com.github.yuttyann.scriptblockplus.file.json.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.file.json.PlayerCountJson;
import com.github.yuttyann.scriptblockplus.file.json.element.ScriptParam;
import com.github.yuttyann.scriptblockplus.script.SBClipboard;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.utils.Utils;

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
        this.scriptType = sbClipboard.getBlockScriptJson().getScriptType();
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
            lightPaste(locations, block.getLocation(), overwrite);
        }
        PlayerCountJson.clear(locations, scriptType);
        sbClipboard.save();
        return this;
    }

    private boolean lightPaste(@NotNull Set<Location> locations, @NotNull Location location, boolean overwrite) {
        BlockScriptJson blockScriptJson = sbClipboard.getBlockScriptJson();
        if (BlockScriptJson.has(location, blockScriptJson) && !overwrite) {
            return false;
        }
        ScriptParam scriptParam = blockScriptJson.load().get(location);
        scriptParam.setAuthor(sbClipboard.getAuthor());
        scriptParam.getAuthor().add(sbClipboard.getSBPlayer().getUniqueId());
        scriptParam.setScript(sbClipboard.getScript());
        scriptParam.setLastEdit(Utils.getFormatTime());
        scriptParam.setAmount(sbClipboard.getAmount());
        locations.add(location);
        return true;
    }
}