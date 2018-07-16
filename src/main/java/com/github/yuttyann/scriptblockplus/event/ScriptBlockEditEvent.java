package com.github.yuttyann.scriptblockplus.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.enums.ActionType;
import com.github.yuttyann.scriptblockplus.script.ScriptType;

public class ScriptBlockEditEvent extends ScriptBlockEvent {

	private String[] actionArray;
	private ScriptType scriptType;
	private ActionType actionType;
	private boolean cancelled;

	public ScriptBlockEditEvent(Player player, Block block, String[] actionArray) {
		super(player, block);
		this.actionArray = actionArray;
		this.scriptType = ScriptType.valueOf(actionArray[0]);
		this.actionType = ActionType.valueOf(actionArray[1]);
	}

	public String[] getArray() {
		return actionArray;
	}

	public ScriptType getScriptType() {
		return scriptType;
	}

	public ActionType getActionType() {
		return actionType;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}
}