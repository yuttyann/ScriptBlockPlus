package com.github.yuttyann.scriptblockplus.script.option.other;

import org.bukkit.event.block.Action;

import com.github.yuttyann.scriptblockplus.player.PlayerData;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public class ScriptAction extends BaseOption {

	public static final String KEY_ENUM_ACTION = PlayerData.createRandomId("EnumAction");

	public ScriptAction() {
		super("scriptaction", "@scriptaction:");
	}

	@Override
	public Option newInstance() {
		return new ScriptAction();
	}

	@Override
	protected boolean isValid() throws Exception {
		Action action = getSBRead().get(KEY_ENUM_ACTION);
		String[] array = StringUtils.split(getOptionValue(), ",");
		return StreamUtils.allMatch(array, s -> equals(action, s));
	}

	private boolean equals(Action action, String actionType) {
		if (actionType.equalsIgnoreCase("shift")) {
			return getPlayer().isSneaking();
		}
		if (ScriptType.INTERACT.equals(getScriptType())) {
			return action == null ? false : action == getAction(actionType);
		}
		return false;
	}

	private Action getAction(String actionType) {
		if (actionType.equalsIgnoreCase("left")) {
			return Action.LEFT_CLICK_BLOCK;
		}
		if (actionType.equalsIgnoreCase("right")) {
			return Action.RIGHT_CLICK_BLOCK;
		}
		return null;
	}
}