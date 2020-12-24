package com.github.yuttyann.scriptblockplus.region;

import com.github.yuttyann.scriptblockplus.file.json.PlayerCountJson;
import com.github.yuttyann.scriptblockplus.script.ScriptAction;
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
    private final CuboidRegionBlocks cuboidRegionBlocks;

    public CuboidRegionRemove(@NotNull Region region) {
        this.scriptTypes = new LinkedHashSet<>();
        this.cuboidRegionBlocks = new CuboidRegionBlocks(region);
    }

    @NotNull
    public Set<ScriptType> getScriptTypes() {
        return scriptTypes;
    }

    @NotNull
    public CuboidRegionBlocks getRegionBlocks() {
        return cuboidRegionBlocks;
    }

    public CuboidRegionRemove remove() {
        scriptTypes.clear();
        Set<Block> blocks = cuboidRegionBlocks.getBlocks();
        Set<Location> locations = new HashSet<>(cuboidRegionBlocks.getCount());
        for (ScriptType scriptType : ScriptType.values()) {
            ScriptAction scriptAction = new ScriptAction(scriptType);
            if (scriptAction.exists()) {
                for (Block block : blocks) {
                    if (scriptAction.lightRemove(locations, block.getLocation())) {
                        scriptTypes.add(scriptType);
                    }
                }
                scriptAction.save();
            }
        }
        for (ScriptType scriptType : scriptTypes) {
            PlayerCountJson.clear(locations, scriptType);
            TimerOption.removeAll(locations, scriptType);
        }
        return this;
    }
}