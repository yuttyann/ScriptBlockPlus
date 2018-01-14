package com.github.yuttyann.scriptblockplus.script.option.other;

import org.bukkit.event.block.Action;

import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.player.PlayerData;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public class ScriptAction extends BaseOption {

	public static final String KEY_CLICK_ACTION = PlayerData.createRandomId("ClickAction");

	public ScriptAction() {
		super("scriptaction", "@scriptaction:");
	}

	@Override
	protected boolean isValid() throws Exception {
		Action action = getSBPlayer().getData(KEY_CLICK_ACTION, null);
		if (action == null) {
			return false;
		}
		String[] array = StringUtils.split(getOptionValue(), ",");
		return StreamUtils.allMatch(array, s -> checkAction(action, s));
	}

	private boolean checkAction(Action action, String value) {
		if (getOptionValue().equalsIgnoreCase("shift")) {
			return getPlayer().isSneaking();
		}
		if (getScriptType() == ScriptType.INTERACT) {
			return action == null ? false : action == getAction(value);
		}
		return false;
	}

	private Action getAction(String value) {
		if (value.equalsIgnoreCase("left")) {
			return Action.LEFT_CLICK_BLOCK;
		}
		if (value.equalsIgnoreCase("right")) {
			return Action.RIGHT_CLICK_BLOCK;
		}
		return null;
	}
}