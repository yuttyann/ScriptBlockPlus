package com.github.yuttyann.scriptblockplus.option;

import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.file.ScriptData;
import com.github.yuttyann.scriptblockplus.manager.MapManager;
import com.github.yuttyann.scriptblockplus.utils.BlockLocation;

public class Amount {

	private int amount;
	private ScriptData scriptData;

	public Amount(int amount, BlockLocation location, ScriptType scriptType) {
		this.amount = amount;
		this.scriptData = new ScriptData(location, scriptType);
	}

	public Amount(String amount, BlockLocation location, ScriptType scriptType) {
		this.amount = Integer.parseInt(amount);
		this.scriptData = new ScriptData(location, scriptType);
	}

	public boolean check() {
		return scriptData.getAmount() >= amount;
	}

	public void add() {
		scriptData.addAmount(1);
		scriptData.save();
	}

	public void remove() {
		scriptData.remove();
		scriptData.save();
		MapManager.removeCoords(scriptData.getBlockLocation(), scriptData.getScriptType());
	}
}