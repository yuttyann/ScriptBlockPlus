package com.github.yuttyann.scriptblockplus.region;

import com.github.yuttyann.scriptblockplus.script.ScriptAction;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * ScriptBlockPlus CuboidRegionRemove クラス
 * @author yuttyann44581
 */
public class CuboidRegionRemove {

    private final Set<ScriptType> set;
    private final CuboidRegionBlocks cuboidRegionBlocks;

    public CuboidRegionRemove(@NotNull Region region) {
        this.set = new LinkedHashSet<>();
        this.cuboidRegionBlocks = new CuboidRegionBlocks(region);
    }

    @NotNull
    public Set<ScriptType> getScriptTypes() {
        return set;
    }

    @NotNull
    public CuboidRegionBlocks getRegionBlocks() {
        return cuboidRegionBlocks;
    }

    public CuboidRegionRemove remove() {
        set.clear();
        Set<Block> blocks = cuboidRegionBlocks.getBlocks();
        for (ScriptType scriptType : ScriptType.values()) {
            ScriptAction scriptAction = new ScriptAction(scriptType);
            if (scriptAction.exists()) {
                for (Block block : blocks) {
                    if (scriptAction.lightRemove(block.getLocation())) {
                        set.add(scriptType);
                    }
                }
                scriptAction.save();
            }
        }
        return this;
    }
}