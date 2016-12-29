package com.github.yuttyann.scriptblockplus.option;

import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.Yaml;
import com.github.yuttyann.scriptblockplus.manager.MapManager;
import com.github.yuttyann.scriptblockplus.manager.OptionManager.ScriptType;
import com.github.yuttyann.scriptblockplus.util.BlockLocation;

public class Amount {

	private ScriptType scriptType;
	private Yaml scripts;
	private String scriptPath;
	private String amountPath;
	private BlockLocation location;
	private int amount;

	public Amount(int amount) {
		this.amount = amount;
	}

	public Amount(String amount) {
		this.amount = Integer.parseInt(amount);
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

	public Amount setYaml(ScriptType scriptType) {
		if (scripts == null)
			this.scriptType = scriptType;
			scripts = Files.getScripts(scriptType);
		return this;
	}

	public Amount setPath(BlockLocation location) {
		if (scriptPath == null) {
			this.location = location;
			scriptPath = location.getWorld().getName() + "." + location.getCoords(false);
			amountPath = scriptPath + ".Amount";
		}
		return this;
	}

	public boolean check() {
		return scripts.getInt(amountPath, 0) >= amount;
	}

	public void plus() {
		scripts.set(amountPath, scripts.getInt(amountPath, 0) + 1);
		scripts.save();
	}

	public void remove() {
		scripts.set(scriptPath, null);
		scripts.save();

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
