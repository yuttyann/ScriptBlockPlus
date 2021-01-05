package com.github.yuttyann.scriptblockplus.file.json.element;

import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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
        return Objects.hash(fullCoords, scriptKey);
    }
}