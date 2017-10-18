package com.github.yuttyann.scriptblockplus.script.option.other;

import com.github.yuttyann.scriptblockplus.script.ScriptData;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;

public class Amount extends BaseOption {

	public Amount() {
		super("amount", "@amount:", 10);
	}

	@Override
	public boolean isValid() {
		ScriptData scriptData = getScriptData();
		scriptData.addAmount(1);
		if (scriptData.getAmount() >= Integer.parseInt(getOptionValue())) {
			scriptData.remove();
			getMapManager().removeCoords(getScriptType(), getBlockCoords());
		}
		scriptData.save();
		return true;
	}
}