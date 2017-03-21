package com.github.yuttyann.scriptblockplus.manager;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.ClickType;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.utils.SimpleMetadata;

public class MetadataManager {

	private static Click click;
	private static Script script;
	private static ScriptFile scriptFile;

	static {
		Plugin plugin = ScriptBlock.getInstance();
		click = new Click(plugin);
		script = new Script(plugin);
		scriptFile = new ScriptFile(plugin);
	}

	public static Click getClick() {
		return click;
	}

	public static Script getScript() {
		return script;
	}

	public static ScriptFile getScriptFile() {
		return scriptFile;
	}

	public static void removeAll(Player player) {
		click.removeAll(player);
		script.removeAll(player);
	}

	public static boolean hasAll(Player player) {
		return click.hasAll(player) || script.hasAll(player);
	}

	public static class Click extends SimpleMetadata {

		public Click(Plugin plugin) {
			super(plugin);
		}

		public void set(Player player, ClickType clickType, boolean value) {
			set(player, clickType.toString(), value);
		}

		public void remove(Player player, ClickType clickType) {
			remove(player, clickType.toString());
		}

		public void removeAll(Player player) {
			for (ClickType type : ClickType.values()) {
				if (has(player, type)) {
					remove(player, type);
				}
			}
		}

		public boolean has(Player player, ClickType clickType) {
			return has(player, clickType.toString());
		}

		public boolean hasAll(Player player) {
			for (ClickType clickType : ClickType.values()) {
				if (has(player, clickType)) {
					return true;
				}
			}
			return false;
		}

		public boolean get(Player player, ClickType clickType) {
			return getBoolean(player, clickType.toString());
		}
	}

	public static class Script extends SimpleMetadata {

		public Script(Plugin plugin) {
			super(plugin);
		}

		public void set(Player player, ClickType clickType, String value) {
			set(player, clickType.toString(), value);
		}

		public void remove(Player player, ClickType clickType) {
			remove(player, clickType.toString());
		}

		public void removeAll(Player player) {
			for (ClickType clickType : ClickType.values()) {
				if (has(player, clickType)) {
					remove(player, clickType);
				}
			}
		}

		public boolean has(Player player, ClickType clickType) {
			return has(player, clickType.toString());
		}

		public boolean hasAll(Player player) {
			for (ClickType clickType : ClickType.values()) {
				if (has(player, clickType)) {
					return true;
				}
			}
			return false;
		}

		public String get(Player player, ClickType clickType) {
			return getString(player, clickType.toString());
		}
	}

	public static class ScriptFile extends SimpleMetadata {

		public ScriptFile(Plugin plugin) {
			super(plugin);
		}

		public void set(Player player, ScriptType scriptType, ScriptFileManager value) {
			set(player, getType(scriptType), value);
		}

		public void remove(Player player, ScriptType scriptType) {
			remove(player, getType(scriptType));
		}

		public void removeAll(Player player) {
			for (ScriptType scriptType : ScriptType.values()) {
				if (has(player, scriptType)) {
					remove(player, scriptType);
				}
			}
		}

		public boolean has(Player player, ScriptType scriptType) {
			return has(player, getType(scriptType));
		}

		public boolean hasAll(Player player) {
			for (ScriptType scriptType : ScriptType.values()) {
				if (has(player, scriptType)) {
					return true;
				}
			}
			return false;
		}

		public ScriptFileManager get(Player player) {
			for (ScriptType scriptType : ScriptType.values()) {
				Object value = get(player, getType(scriptType));
				if (value != null) {
					return (ScriptFileManager) value;
				}
			}
			return null;
		}

		private String getType(ScriptType scriptType) {
			return "SCRIPTBLOCKPLUS_" + scriptType.toString().toUpperCase();
		}
	}
}