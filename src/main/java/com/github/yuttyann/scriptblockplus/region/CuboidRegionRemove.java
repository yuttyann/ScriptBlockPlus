package com.github.yuttyann.scriptblockplus.region;

import com.github.yuttyann.scriptblockplus.file.json.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.file.json.PlayerCountJson;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.script.option.time.TimerOption;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * ScriptBlockPlus CuboidRegionRemove クラス
 * @author yuttyann44581
 */
public class CuboidRegionRemove {

    private final Set<ScriptType> scriptTypes;
    private final CuboidRegionBlocks regionBlocks;

    public CuboidRegionRemove(@NotNull Region region) {
        this.scriptTypes = new LinkedHashSet<>();
        this.regionBlocks = new CuboidRegionBlocks(region);
    }

    @NotNull
    public Set<ScriptType> getScriptTypes() {
        return scriptTypes;
    }

    @NotNull
    public CuboidRegionBlocks getRegionBlocks() {
        return regionBlocks;
    }

    public CuboidRegionRemove remove() {
        scriptTypes.clear();
        Set<Block> blocks = regionBlocks.getBlocks();
        Set<Location> locations = new HashSet<>(regionBlocks.getCount());
        for (ScriptType scriptType : ScriptType.values()) {
            BlockScriptJson blockScriptJson = new BlockScriptJson(scriptType);
            if (blockScriptJson.exists()) {
                for (Block block : blocks) {
                    if (lightRemove(locations, block.getLocation(), blockScriptJson)) {
                        scriptTypes.add(scriptType);
                    }
                }
                blockScriptJson.saveFile();
            }
        }
        for (ScriptType scriptType : scriptTypes) {
            TimerOption.removeAll(locations, scriptType);
            PlayerCountJson.clear(locations, scriptType);
        }
        return this;
    }
    
    private boolean lightRemove(@NotNull Set<Location> locations, @NotNull Location location, @NotNull BlockScriptJson blockScriptJson) {
        if (!BlockScriptJson.has(location, blockScriptJson)) {
            return false;
        }
        blockScriptJson.load().remove(location);
        locations.add(location);
        return true;
    }
}