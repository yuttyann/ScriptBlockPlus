package com.github.yuttyann.scriptblockplus.script.endprocess;

import com.github.yuttyann.scriptblockplus.script.ScriptRead;

public interface EndProcess {

	public void success(ScriptRead scriptRead);

	public void failed(ScriptRead scriptRead);
}