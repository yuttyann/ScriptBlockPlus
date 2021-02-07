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
package com.github.yuttyann.scriptblockplus.file.json.element;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.file.json.annotation.Alternate;
import com.github.yuttyann.scriptblockplus.file.json.multi.TwoJson.TwoElement;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus PlayerCount クラス
 * @author yuttyann44581
 */
public class PlayerCount extends TwoElement<ScriptKey, BlockCoords> {

    @Alternate("scripttype")
    @SerializedName(value = "scriptkey", alternate = { "scripttype" })
    private final ScriptKey scriptKey;

    @Alternate("fullcoords")
    @SerializedName(value = "blockcoords", alternate = { "fullcoords" })
    private final BlockCoords blockCoords;

    @SerializedName("amount")
    private int amount;

    public PlayerCount(@NotNull ScriptKey scriptKey, @NotNull BlockCoords blockCoords) {
        this.scriptKey = scriptKey;
        this.blockCoords = blockCoords;
    }

    @NotNull
    public ScriptKey getScriptKey() {
        return scriptKey;
    }

    @NotNull
    public BlockCoords getBlockCoords() {
        return blockCoords;
    }

    public synchronized void setAmount(int amount) {
        this.amount = Math.min(amount, 0);
    }

    public synchronized int add() {
        return ++amount;
    }

    public synchronized int subtract() {
        return amount > 0 ? --amount : 0;
    }

    public synchronized int getAmount() {
        return amount;
    }

    @Override
    public boolean isElement(@NotNull ScriptKey scriptKey, @NotNull BlockCoords blockCoords) {
        return compare(this.scriptKey, scriptKey) && compare(this.blockCoords, blockCoords);
    }
}