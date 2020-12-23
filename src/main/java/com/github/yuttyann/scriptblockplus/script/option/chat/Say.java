package com.github.yuttyann.scriptblockplus.script.option.chat;

import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus Say オプションクラス
 * @author yuttyann44581
 */
@OptionTag(name = "say", syntax = "@say ")
public class Say extends BaseOption {

	@Override
	@NotNull
	public Option newInstance() {
		return new Say();
	}

	@Override
	protected boolean isValid() throws Exception {
		String command = "say " + getOptionValue();
		return Utils.tempPerm(getSBPlayer(), Permission.MINECRAFT_COMMAND_SAY, () -> Utils.dispatchCommand(getSBPlayer(), command));
	}
}