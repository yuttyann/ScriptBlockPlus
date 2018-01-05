package com.github.yuttyann.scriptblockplus.script.endprocess;

import com.github.yuttyann.scriptblockplus.script.SBRead;

public interface EndProcess {

	public void success(SBRead sbRead);

	public void failed(SBRead sbRead);
}