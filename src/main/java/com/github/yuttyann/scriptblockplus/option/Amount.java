package com.github.yuttyann.scriptblockplus.option;

import com.github.yuttyann.scriptblockplus.BlockLocation;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.file.ScriptData;
import com.github.yuttyann.scriptblockplus.manager.MapManager;

public class Amount {

	private int amount;
	private MapManager mapManager;
	private ScriptData scriptData;

	public Amount(ScriptBlock plugin, int amount, BlockLocation location, ScriptType scriptType) {
		this.amount = amount;
		this.mapManager = plugin.getMapManager();
		this.scriptData = new ScriptData(plugin, location, scriptType);
	}

	public Amount(ScriptBlock plugin, String amount, BlockLocation location, ScriptType scriptType) {
		this.amount = Integer.parseInt(amount);
		this.scriptData = new ScriptData(plugin, location, scriptType);
	}

	public int getAmount() {
		return amount;
	}

	public boolean check() {
		return scriptData.getAmount() >= getAmount();
	}

	public void add() {
		scriptData.addAmount(1);
		scriptData.save();
	}

	public void remove() {
		scriptData.remove();
		scriptData.save();
		mapManager.removeLocation(scriptData.getBlockLocation(), scriptData.getScriptType());
	}
}