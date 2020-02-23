package com.github.yuttyann.scriptblockplus.script.option.other;

import com.github.yuttyann.scriptblockplus.script.ScriptData;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import org.jetbrains.annotations.NotNull;

public class Amount extends BaseOption {

	public Amount() {
		super("amount", "@amount:");
	}

	@NotNull
	@Override
	public Option newInstance() {
		return new Amount();
	}

	@Override
	protected boolean isValid() throws Exception {
		ScriptData scriptData = getScriptData();
		if (!scriptData.getScriptFile().contains("Amount")) {
			scriptData.setAmount(Integer.parseInt(getOptionValue()));;
		}
		scriptData.subtractAmount(1);
		if (scriptData.getAmount() <= 0) {
			scriptData.remove();
			getMapManager().removeCoords(getLocation(), getScriptType());
		}
		scriptData.save();
		return true;
	}
}