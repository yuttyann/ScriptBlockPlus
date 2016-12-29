package com.github.yuttyann.scriptblockplus.manager;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import com.github.yuttyann.scriptblockplus.Main;
import com.github.yuttyann.scriptblockplus.manager.OptionManager.ScriptType;

public class MetadataManager {

	public enum ClickType {
		INTERACT_CREATE("INTERACT_SCRIPTBLOCKPLUS_CREATECLICK"),
		INTERACT_ADD("INTERACT_SCRIPTBLOCKPLUS_ADDCLICK"),
		INTERACT_REMOVE("INTERACT_SCRIPTBLOCKPLUS_REMOVECLICK"),
		INTERACT_VIEW("INTERACT_SCRIPTBLOCKPLUS_VIEWCLICK"),
		WALK_CREATE("WALK_SCRIPTBLOCKPLUS_CREATECLICK"),
		WALK_ADD("WALK_SCRIPTBLOCKPLUS_ADDCLICK"),
		WALK_REMOVE("WALK_SCRIPTBLOCKPLUS_REMOVECLICK"),
		WALK_VIEW("WALK_SCRIPTBLOCKPLUS_VIEWCLICK");

		private String type;

		private ClickType(String type) {
			this.type = type;
		}

		public String getString() {
			return type;
		}
	}

	public static void removeAllMetadata(Player player) {
		Click.removeAllMetadata(player);
		Script.removeAllMetadata(player);
	}

	public static boolean hasAllMetadata(Player player) {
		return Click.hasAllMetadata(player) || Script.hasAllMetadata(player);
	}

	public static class Click {

		public static void setMetadata(Player player, ClickType clickType, boolean value) {
			player.setMetadata(clickType.getString(), new FixedMetadataValue(Main.instance, value));
		}

		public static void removeMetadata(Player player, ClickType clickType) {
			player.removeMetadata(clickType.getString(), Main.instance);
		}

		public static void removeAllMetadata(Player player) {
			for (ClickType type : ClickType.values()) {
				if (hasMetadata(player, type))
					removeMetadata(player, type);
			}
		}

		public static boolean hasMetadata(Player player, ClickType clickType) {
			return player.hasMetadata(clickType.getString());
		}

		public static boolean hasAllMetadata(Player player) {
			for (ClickType clickType : ClickType.values()) {
				if (hasMetadata(player, clickType))
					return true;
			}
			return false;
		}

		public static boolean getMetadata(Player player, ClickType clickType) {
			List<MetadataValue> values = player.getMetadata(clickType.getString());
			for (MetadataValue value : values) {
				if (value.asBoolean())
					return true;
			}
			return false;
		}
	}

	public static class Script {

		public static void setMetadata(Player player, ClickType clickType, String value) {
			player.setMetadata(clickType.getString(), new FixedMetadataValue(Main.instance, value));
		}

		public static void removeMetadata(Player player, ClickType clickType) {
			player.removeMetadata(clickType.getString(), Main.instance);
		}

		public static void removeAllMetadata(Player player) {
			for (ClickType clickType : ClickType.values()) {
				if (hasMetadata(player, clickType))
					removeMetadata(player, clickType);
			}
		}

		public static boolean hasMetadata(Player player, ClickType clickType) {
			return player.hasMetadata(clickType.getString());
		}

		public static boolean hasAllMetadata(Player player) {
			for (ClickType clickType : ClickType.values()) {
				if (hasMetadata(player, clickType))
					return true;
			}
			return false;
		}

		public static String getMetadata(Player player, ClickType clickType) {
			List<MetadataValue> values = player.getMetadata(clickType.getString());
			String script;
			for (MetadataValue value : values) {
				script = value.asString();
				if (script != null)
					return script;
			}
			return null;
		}
	}

	public static class Edit {

		public static void setMetadata(Player player, ScriptType scriptType, EditManager value) {
			player.setMetadata("SCRIPTBLOCKPLUS_" + scriptType.getString(), new FixedMetadataValue(Main.instance, value));
		}

		public static void removeMetadata(Player player, ScriptType scriptType) {
			player.removeMetadata("SCRIPTBLOCKPLUS_" + scriptType.getString(), Main.instance);
		}

		public static void removeAllMetadata(Player player) {
			for (ScriptType scriptType : ScriptType.values()) {
				if (hasMetadata(player, scriptType))
					removeMetadata(player, scriptType);
			}
		}

		public static boolean hasMetadata(Player player, ScriptType scriptType) {
			return player.hasMetadata("SCRIPTBLOCKPLUS_" + scriptType.getString());
		}

		public static boolean hasAllMetadata(Player player) {
			for (ScriptType scriptType : ScriptType.values()) {
				if (hasMetadata(player, scriptType))
					return true;
			}
			return false;
		}

		public static EditManager getMetadata(Player player) {
			for (ScriptType scriptType : ScriptType.values()) {
				List<MetadataValue> values = player.getMetadata("SCRIPTBLOCKPLUS_" + scriptType.getString());
				for (MetadataValue value : values) {
					if (value != null)
						return (EditManager) value.value();
				}
			}
			return null;
		}
	}
}
