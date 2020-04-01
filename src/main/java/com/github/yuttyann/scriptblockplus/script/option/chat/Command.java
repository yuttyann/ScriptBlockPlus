package com.github.yuttyann.scriptblockplus.script.option.chat;

import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus Command オプションクラス
 * @author yuttyann44581
 */
public class Command extends BaseOption {

	public Command() {
		super("command", "@command ");
	}

	@Override
	@NotNull
	public Option newInstance() {
		return new Command();
	}

	@Override
	protected boolean isValid() throws Exception {
		return executeCommand(getPlayer(), StringUtils.setColor(getOptionValue(), true), false);
	}
}