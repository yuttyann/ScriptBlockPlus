package com.github.yuttyann.scriptblockplus.option;

import org.bukkit.Location;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.file.ScriptData;

public class Amount {

	private int amount;
	private ScriptBlock plugin;
	private Location location;
	private ScriptType scriptType;
	private ScriptData scriptData;

	public Amount(ScriptBlock plugin, String amount, Location location, ScriptType scriptType) {
		this.plugin = plugin;
		this.amount = Integer.parseInt(amount);
		this.location = location;
		this.scriptType = scriptType;
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
		plugin.getMapManager().removeLocation(location, scriptType);
	}
}