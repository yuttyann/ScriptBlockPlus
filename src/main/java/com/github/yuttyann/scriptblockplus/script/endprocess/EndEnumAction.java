package com.github.yuttyann.scriptblockplus.script.endprocess;

import com.github.yuttyann.scriptblockplus.script.SBRead;
import com.github.yuttyann.scriptblockplus.script.option.other.ScriptAction;

public class EndEnumAction implements EndProcess {

	@Override
	public void success(SBRead sbRead) {
		failed(sbRead);
	}

	@Override
	public void failed(SBRead sbRead) {
		sbRead.removeData(ScriptAction.KEY_ENUM_ACTION);
	}
}