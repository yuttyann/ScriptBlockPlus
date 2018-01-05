package com.github.yuttyann.scriptblockplus.script.endprocess;

import com.github.yuttyann.scriptblockplus.script.SBRead;
import com.github.yuttyann.scriptblockplus.script.option.other.ClickAction;

public class EndClickAction implements EndProcess {

	@Override
	public void success(SBRead sbRead) {
		failed(sbRead);
	}

	@Override
	public void failed(SBRead sbRead) {
		sbRead.getSBPlayer().removeData(ClickAction.KEY_CLICK_ACTION);
	}
}