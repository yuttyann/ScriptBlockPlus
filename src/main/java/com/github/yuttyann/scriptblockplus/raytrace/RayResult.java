/**
 * ScriptBlockPlus - Allow you to add script to any blocks.
 * Copyright (C) 2021 yuttyann44581
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */
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