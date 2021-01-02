package com.github.yuttyann.scriptblockplus.region;

import java.util.HashSet;
import java.util.Set;

import com.github.yuttyann.scriptblockplus.file.json.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.file.json.PlayerCountJson;
import com.github.yuttyann.scriptblockplus.script.SBClipboard;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.utils.Utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus CuboidRegionPaste クラス
 * @author yuttyann44581
 */
public class CuboidRegionPaste {

    private final ScriptType scriptType;
    private final SBClipboard sbClipboard;
    private final CuboidRegionBlocks regionBlocks;

    public CuboidRegionPaste(@NotNull SBClipboard sbClipboard, @NotNull Region region) {
        this.scriptType = sbClipboard.getBlockScriptJson().getScriptType();
        this.sbClipboard = sbClipboard;
        this.regionBlocks = new CuboidRegionBlocks(region);
    }

    @NotNull
    public ScriptType getScriptType() {
        return scriptType;
    }

    @NotNull
    public CuboidRegionBlocks getRegionBlocks() {
        return regionBlocks;
    }

    public CuboidRegionPaste paste(boolean pasteonair, boolean overwrite) {
        var locations = new HashSet<Location>(regionBlocks.getCount());
        for (var block : regionBlocks.getBlocks()) {
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
        var scriptJson = sbClipboard.getBlockScriptJson();
        if (BlockScriptJson.has(location, scriptJson) && !overwrite) {
            return false;
        }
        var scriptParam = scriptJson.load().get(location);
        scriptParam.setAuthor(sbClipboard.getAuthor());
        scriptParam.getAuthor().add(sbClipboard.getSBPlayer().getUniqueId());
        scriptParam.setScript(sbClipboard.getScript());
        scriptParam.setLastEdit(Utils.getFormatTime(Utils.DATE_PATTERN));
        scriptParam.setAmount(sbClipboard.getAmount());
        locations.add(location);
        return true;
    }
}