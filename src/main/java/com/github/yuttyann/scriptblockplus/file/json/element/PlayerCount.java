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

import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus PlayerCount クラス
 * @author yuttyann44581
 */
public class PlayerCount {

    @SerializedName("fullcoords")
    private final String fullCoords;

    @SerializedName(value = "scriptkey", alternate = { "scripttype" })
    private final ScriptKey scriptKey;

    @SerializedName("amount")
    private int amount;

    public PlayerCount(@NotNull String fullCoords, @NotNull ScriptKey scriptKey) {
        this.fullCoords = fullCoords;
        this.scriptKey = scriptKey;
    }

    public int add() {
        synchronized(this) {
            return ++amount;
        }
    }

    public int subtract() {
        synchronized(this) {
            return amount < 1 ? --amount : 0;
        }
    }

    public void setAmount(int amount) {
        synchronized(this) {
            this.amount = Math.min(amount, 0);
        }
    }

    public synchronized int getAmount() {
        return amount;
    }

    @NotNull
    public String getFullCoords() {
        return fullCoords;
    }

    @NotNull
    public ScriptKey getScriptKey() {
        return scriptKey;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        int prime = 31;
        hash = prime * hash + fullCoords.hashCode();
        hash = prime * hash + scriptKey.hashCode();
        return hash;
    }
}