package com.github.yuttyann.scriptblockplus.enums;

import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.metadata.ClickAction;
import com.github.yuttyann.scriptblockplus.metadata.ScriptFile;
import com.github.yuttyann.scriptblockplus.metadata.ScriptText;
import com.github.yuttyann.scriptblockplus.metadata.SimpleMetadata;

public enum Metadata {
	CLICKACTION(new ClickAction(ScriptBlock.getInstance())),
	SCRIPTFILE(new ScriptFile(ScriptBlock.getInstance())),
	SCRIPTTEXT(new ScriptText(ScriptBlock.getInstance()));

	private SimpleMetadata metadata;

	private Metadata(SimpleMetadata metadata) {
		this.metadata = metadata;
	}

	public static void removeAll(Player player, Metadata... metadatas) {
		for (Metadata metadata : metadatas) {
			metadata.toMetadata().removeAll(player);
		}
	}

	public static boolean hasAll(Player player, Metadata... metadatas) {
		for (Metadata metadata : metadatas) {
			if (metadata.toMetadata().hasAll(player)) {
				return true;
			}
		}
		return false;
	}

	public static ClickAction getClickAction() {
		return (ClickAction) Metadata.CLICKACTION.toMetadata();
	}

	public static ScriptFile getScriptFile() {
		return (ScriptFile) Metadata.SCRIPTFILE.toMetadata();
	}

	public static ScriptText getScriptText() {
		return (ScriptText) Metadata.SCRIPTTEXT.toMetadata();
	}

	public SimpleMetadata toMetadata() {
		return metadata;
	}
}