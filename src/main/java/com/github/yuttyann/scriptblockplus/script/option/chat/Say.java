package com.github.yuttyann.scriptblockplus.script.option.chat;

import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus Say オプションクラス
 * @author yuttyann44581
 */
public class Say extends BaseOption {

	public Say() {
		super("say", "@say ");
	}

	@Override
	@NotNull
	public Option newInstance() {
		return new Say();
	}

	@Override
	protected boolean isValid() throws Exception {
		return executeCommand(getSBPlayer(), "*say " + getOptionValue(), true);
	}
}