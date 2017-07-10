package com.github.yuttyann.scriptblockplus.enums;

import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.metadata.PlayerClick;
import com.github.yuttyann.scriptblockplus.metadata.ScriptFile;
import com.github.yuttyann.scriptblockplus.metadata.ScriptText;
import com.github.yuttyann.scriptblockplus.metadata.SimpleMetadata;

public enum Metadata {
	PLAYERCLICK(new PlayerClick(ScriptBlock.getInstance())),
	SCRIPTFILE(new ScriptFile(ScriptBlock.getInstance())),
	SCRIPTTEXT(new ScriptText(ScriptBlock.getInstance()));

	private SimpleMetadata metadata;

	private Metadata(SimpleMetadata metadata) {
		this.metadata = metadata;
	}

	public void set(Player player, String key, Object value) {
		metadata.set(player, key, value);
	}

	public void removeAll(Player player) {
		metadata.removeAll(player);
	}

	public boolean hasAll(Player player) {
		return metadata.hasAll(player);
	}

	public static void removeAll(Player player, Metadata... metadatas) {
		for (Metadata metadata : metadatas) {
			metadata.removeAll(player);
		}
	}

	public static boolean hasAll(Player player, Metadata... metadatas) {
		for (Metadata metadata : metadatas) {
			if (metadata.hasAll(player)) {
				return true;
			}
		}
		return false;
	}

	public static PlayerClick getPlayerClick() {
		return (PlayerClick) Metadata.PLAYERCLICK.toMetadata();
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