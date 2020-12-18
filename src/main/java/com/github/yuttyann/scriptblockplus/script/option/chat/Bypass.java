package com.github.yuttyann.scriptblockplus.script.option.chat;

import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus Bypass オプションクラス
 * @author yuttyann44581
 */
@OptionTag(name = "bypass", syntax = "@bypass ")
public class Bypass extends BaseOption {

	@Override
	@NotNull
	public Option newInstance() {
		return new Bypass();
	}

	@Override
	protected boolean isValid() throws Exception {
		String command = StringUtils.setColor(getOptionValue());
		return Utils.tempOP(getSBPlayer(), () -> Utils.dispatchCommand(getSBPlayer(), command));
	}
}