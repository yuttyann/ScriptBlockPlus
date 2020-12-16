package com.github.yuttyann.scriptblockplus.file.json.element;

import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * ScriptBlockPlus PlayerCount クラス
 * @author yuttyann44581
 */
public class PlayerCount {

	@SerializedName("fullcoords")
	@Expose
	private final String fullCoords;

	@SerializedName("scripttype")
	@Expose
	private final ScriptType scriptType;

	@SerializedName("amount")
	@Expose
	private int amount;

	public PlayerCount(@NotNull String fullCoords, @NotNull ScriptType scriptType) {
		this.fullCoords = fullCoords;
		this.scriptType = scriptType;
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
	public ScriptType getScriptType() {
		return scriptType;
	}

	@Override
	public int hashCode() {
		return Objects.hash(fullCoords, scriptType);
	}
}