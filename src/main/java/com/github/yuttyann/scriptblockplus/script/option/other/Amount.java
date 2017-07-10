package com.github.yuttyann.scriptblockplus.script.option.other;

import com.github.yuttyann.scriptblockplus.manager.ScriptManager;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;

public class Amount extends BaseOption {

	public Amount(ScriptManager scriptManager) {
		super(scriptManager, "amount", "@amount:");
	}

	@Override
	public boolean isValid() {
		scriptData.addAmount(1);
		if (scriptData.getAmount() >= Integer.parseInt(optionData)) {
			scriptData.remove();
			mapManager.removeLocation(blockCoords, scriptType);
		}
		scriptData.save();
		return true;
	}
}