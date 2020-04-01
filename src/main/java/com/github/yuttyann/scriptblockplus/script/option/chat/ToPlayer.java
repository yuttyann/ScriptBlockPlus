package com.github.yuttyann.scriptblockplus.script.option.chat;

import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus ToPlayer オプションクラス
 * @author yuttyann44581
 */
public class ToPlayer extends BaseOption {

	public ToPlayer() {
		super("toplayer", "@player ");
	}

	@Override
	@NotNull
	public Option newInstance() {
		return new ToPlayer();
	}

	@Override
	protected boolean isValid() throws Exception {
		Utils.sendColorMessage(getPlayer(), getOptionValue());
		return true;
	}
}