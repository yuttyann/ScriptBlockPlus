package com.github.yuttyann.scriptblockplus.script.option.other;

import com.github.yuttyann.scriptblockplus.script.ScriptData;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus Amount オプションクラス
 * @author yuttyann44581
 */
public class Amount extends BaseOption {

	public Amount() {
		super("amount", "@amount:");
	}

	@Override
	@NotNull
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