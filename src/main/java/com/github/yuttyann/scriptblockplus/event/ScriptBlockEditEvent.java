package com.github.yuttyann.scriptblockplus.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.github.yuttyann.scriptblockplus.enums.ActionType;
import com.github.yuttyann.scriptblockplus.script.ScriptType;

public class ScriptBlockEditEvent extends ScriptBlockEvent {

	private final ScriptType scriptType;
	private final ActionType actionType;
	private boolean cancelled;

	public ScriptBlockEditEvent(@NotNull final Player player, @NotNull final Block block, @NotNull final String[] actionArray) {
		super(player, block);
		this.scriptType = ScriptType.valueOf(actionArray[0]);
		this.actionType = ActionType.valueOf(actionArray[1]);
	}

	@NotNull
	public ScriptType getScriptType() {
		return scriptType;
	}

	@NotNull
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