package com.github.yuttyann.scriptblockplus.metadata;

import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.Metadata;

public class SBMetadata {

	private static PlayerClick playerClick;
	private static ScriptText scriptText;
	private static ScriptFile scriptFile;

	static {
		ScriptBlock plugin = ScriptBlock.getInstance();
		playerClick = new PlayerClick(plugin);
		scriptText = new ScriptText(plugin);
		scriptFile = new ScriptFile(plugin);
	}

	public static PlayerClick getPlayerClick() {
		return playerClick;
	}

	public static ScriptText getScriptText() {
		return scriptText;
	}

	public static ScriptFile getScriptFile() {
		return scriptFile;
	}

	public void set(Player player, Metadata metadata, String key, Object value) {
		switch (metadata) {
		case PLAYERCLICK:
			playerClick.set(player, key, value);
			break;
		case SCRIPTFILE:
			scriptFile.set(player, key, value);
			break;
		case SCRIPTTEXT:
			scriptText.set(player, key, value);
			break;
		}
	}

	@Deprecated
	public static void removeAll(Player player) {
		remove(player, Metadata.PLAYERCLICK, Metadata.SCRIPTFILE, Metadata.SCRIPTTEXT);
	}

	public static void remove(Player player, Metadata... metadatas) {
		for (Metadata metadata : metadatas) {
			switch (metadata) {
			case PLAYERCLICK:
				playerClick.removeAll(player);
				break;
			case SCRIPTFILE:
				scriptFile.removeAll(player);
				break;
			case SCRIPTTEXT:
				scriptText.removeAll(player);
				break;
			}
		}
	}

	@Deprecated
	public static void hasAll(Player player) {
		has(player, Metadata.PLAYERCLICK, Metadata.SCRIPTFILE, Metadata.SCRIPTTEXT);
	}

	public static boolean has(Player player, Metadata... metadatas) {
		for (Metadata metadata : metadatas) {
			switch (metadata) {
			case PLAYERCLICK:
				return playerClick.hasAll(player);
			case SCRIPTFILE:
				return scriptFile.hasAll(player);
			case SCRIPTTEXT:
				return scriptText.hasAll(player);
			}
		}
		return false;
	}
}