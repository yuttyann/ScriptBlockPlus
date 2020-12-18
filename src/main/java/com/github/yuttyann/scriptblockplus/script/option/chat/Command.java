package com.github.yuttyann.scriptblockplus.script.option.chat;

import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus Command オプションクラス
 * @author yuttyann44581
 */
@OptionTag(name = "command", syntax = "@command ")
public class Command extends BaseOption {

	@Override
	@NotNull
	public Option newInstance() {
		return new Command();
	}

	@Override
	protected boolean isValid() throws Exception {
		return Utils.dispatchCommand(getSBPlayer(), StringUtils.setColor(getOptionValue()));
	}
}