package com.github.yuttyann.scriptblockplus.player;

import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus PlayerCountInfo クラス
 * @author yuttyann44581
 */
public class PlayerCountInfo {

	@SerializedName("amount")
	@Expose
	private int amount;

	@SerializedName("fullcoords")
	@Expose
	private String fullCoords;

	@SerializedName("scripttype")
	@Expose
	private ScriptType scriptType;

	public PlayerCountInfo(@NotNull String fullCoords, @NotNull ScriptType scriptType) {
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
		int hash = 1;
		int prime = 31;
		hash = prime * hash + fullCoords.hashCode();
		hash = prime * hash + scriptType.hashCode();
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PlayerCountInfo) {
			PlayerCountInfo info = ((PlayerCountInfo) obj);
			return info.scriptType.equals(scriptType) && info.fullCoords.equals(fullCoords);
		}
		return false;
	}

	@Override
	public String toString() {
		return "[Amount:" + amount + ", FullCoords:"+ fullCoords + ", ScriptType:" + scriptType + "]";
	}
}