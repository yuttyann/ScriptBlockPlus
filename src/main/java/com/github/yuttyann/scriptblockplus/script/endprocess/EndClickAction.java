package com.github.yuttyann.scriptblockplus.script.endprocess;

import com.github.yuttyann.scriptblockplus.script.SBRead;
import com.github.yuttyann.scriptblockplus.script.option.other.ScriptAction;

public class EndClickAction implements EndProcess {

	@Override
	public void success(SBRead sbRead) {
		failed(sbRead);
	}

	@Override
	public void failed(SBRead sbRead) {
		sbRead.getSBPlayer().removeData(ScriptAction.KEY_CLICK_ACTION);
	}
}