package com.github.yuttyann.scriptblockplus.option;

import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.Yaml;
import com.github.yuttyann.scriptblockplus.manager.MapManager;
import com.github.yuttyann.scriptblockplus.manager.OptionManager.ScriptType;
import com.github.yuttyann.scriptblockplus.util.BlockLocation;

public class Amount {

	private ScriptType scriptType;
	private Yaml scriptFile;
	private String scriptPath;
	private String amountPath;
	private BlockLocation location;
	private int amount;

	public Amount(int amount, BlockLocation location, ScriptType scriptType) {
		this.amount = amount;
		this.location = location;
		this.scriptPath = location.getWorld().getName() + "." + location.getCoords(false);
		this.amountPath = scriptPath + ".Amount";
		this.scriptType = scriptType;
		this.scriptFile = Files.getScriptFile(scriptType);
	}

	public Amount(String amount, BlockLocation location, ScriptType scriptType) {
		this.amount = Integer.parseInt(amount);
		this.location = location;
		this.scriptPath = location.getWorld().getName() + "." + location.getCoords(false);
		this.amountPath = scriptPath + ".Amount";
		this.scriptType = scriptType;
		this.scriptFile = Files.getScriptFile(scriptType);
	}

	public int getAmount() {
		return amount;
	}

	public String getScriptPath() {
		return scriptPath;
	}

	public String getAmountPath() {
		return amountPath;
	}

	public BlockLocation getBlockLocation() {
		return location;
	}

	public boolean check() {
		return scriptFile.getInt(amountPath, 0) >= amount;
	}

	public void plus() {
		scriptFile.set(amountPath, scriptFile.getInt(amountPath, 0) + 1);
		scriptFile.save();
	}

	public void remove() {
		scriptFile.set(scriptPath, null);
		scriptFile.save();
		switch (scriptType) {
		case INTERACT:
			MapManager.getInteractCoords().remove(location.getCoords(true));
			break;
		case WALK:
			MapManager.getWalkCoords().remove(location.getCoords(true));
			break;
		}
	}
}
