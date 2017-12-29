package com.github.yuttyann.scriptblockplus.script.option.other;

import org.bukkit.event.block.Action;

import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;

public class ClickAction extends BaseOption {

	public static final String KEY_ACTION = "Option_ClickAction";

	public ClickAction() {
		super("clickaction", "@clickaction:");
	}

	@Override
	protected boolean isValid() throws Exception {
		SBPlayer sbPlayer = getSBPlayer();
		Action action1 = sbPlayer.getData(KEY_ACTION, null);
		if (action1 == null || getScriptType() != ScriptType.INTERACT) {
			return false;
		}
		Action action2 = getAction(getOptionValue());
		return action1 != null && action2 != null ? action1 == action2 : false;
	}

	private Action getAction(String action) {
		if (action.equalsIgnoreCase("left")) {
			return Action.LEFT_CLICK_BLOCK;
		}
		if (action.equalsIgnoreCase("right")) {
			return Action.RIGHT_CLICK_BLOCK;
		}
		return null;
	}
}