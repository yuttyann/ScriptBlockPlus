package com.github.yuttyann.scriptblockplus.script.option.other;

import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.event.block.Action;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus ScriptAction オプションクラス
 * @author yuttyann44581
 */
public class ScriptAction extends BaseOption {

	public static final String KEY = Utils.randomUUID();

	public ScriptAction() {
		super("scriptaction", "@scriptaction:");
	}

	@Override
	@NotNull
	public Option newInstance() {
		return new ScriptAction();
	}

	@Override
	protected boolean isValid() throws Exception {
		Action action = getSBRead().get(KEY);
		String[] array = StringUtils.split(getOptionValue(), ",");
		return StreamUtils.allMatch(array, s -> equals(action, s));
	}

	private boolean equals(@Nullable Action action, @NotNull String actionType) {
		if (action == null) {
			return false;
		}
		if (actionType.equalsIgnoreCase("shift")) {
			return getPlayer().isSneaking();
		}
		return ScriptType.INTERACT.equals(getScriptType()) && action == getAction(actionType);
	}

	private Action getAction(@NotNull String actionType) {
		if (actionType.equalsIgnoreCase("left")) {
			return Action.LEFT_CLICK_BLOCK;
		} else if (actionType.equalsIgnoreCase("right")) {
			return Action.RIGHT_CLICK_BLOCK;
		}
		return null;
	}
}