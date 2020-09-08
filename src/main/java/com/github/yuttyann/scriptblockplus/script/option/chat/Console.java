package com.github.yuttyann.scriptblockplus.script.option.chat;

import com.github.yuttyann.scriptblockplus.enums.LogAdmin;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus Console オプションクラス
 * @author yuttyann44581
 */
public class Console extends BaseOption {

	public Console() {
		super("console", "@console ");
	}

	@Override
	@NotNull
	public Option newInstance() {
		return new Console();
	}

	@Override
	protected boolean isValid() throws Exception {
		String command = StringUtils.setColor(getOptionValue(), true);
		return LogAdmin.function(getSBPlayer().getWorld(), w -> Utils.dispatchCommand(Bukkit.getConsoleSender(), command));
	}
}