package com.github.yuttyann.scriptblockplus.raytrace;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus RayResult クラス
 * @author yuttyann44581
 */
public class RayResult {

    private Block block;
    private BlockFace blockFace;

    public RayResult(@NotNull Block block, @Nullable BlockFace blockFace) {
        this.block = block;
        this.blockFace = blockFace == null ? BlockFace.SOUTH : blockFace;
    }

    @NotNull
    public Block getHitBlock() {
        return block;
    }

    @NotNull
    public Block getRelative() {
        return block.getRelative(blockFace);
    }

    @NotNull
    public BlockFace getHitBlockFace() {
        return blockFace;
    }
}