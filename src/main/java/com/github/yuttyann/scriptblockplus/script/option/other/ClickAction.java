package com.github.yuttyann.scriptblockplus.script.option.other;

import org.bukkit.event.block.Action;

import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public class ClickAction extends BaseOption {

	public static final String KEY_INTERACTACTION = "Option_ClickAction";

	public ClickAction() {
		super("clickaction", "@clickaction:");
	}

	@Override
	protected boolean isValid() throws Exception {
		SBPlayer sbPlayer = getSBPlayer();
		Action action1 = sbPlayer.getData(KEY_INTERACTACTION, null);
		if (action1 == null || getScriptType() != ScriptType.INTERACT) {
			return false;
		}
		String[] array = StringUtils.split(getOptionValue(), " ");
		Action action2 = getAction(array[0]);
		if (action1 == null || action2 == null || action1 != action2) {
			if (array.length > 1) {
				String message = StringUtils.createString(array, 1);
				message = StringUtils.replaceColorCode(message, true);
				sbPlayer.sendMessage(message);
			}
			return false;
		}
		return true;
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