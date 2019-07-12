package com.github.yuttyann.scriptblockplus.manager;

import java.util.List;
import java.util.Objects;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.ScriptBlockAPI;
import com.github.yuttyann.scriptblockplus.listener.ScriptListener;
import com.github.yuttyann.scriptblockplus.manager.OptionManager.OptionList;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.ScriptData;
import com.github.yuttyann.scriptblockplus.script.ScriptEdit;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.script.endprocess.EndProcess;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;

public final class APIManager implements ScriptBlockAPI {

	private ScriptBlock plugin;

	public APIManager(ScriptBlock plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean scriptRead(Player player, Location location, ScriptType scriptType, int index) {
		return new ScriptRead(player, location, new ScriptListener(plugin, scriptType)).read(index);
	}

	@Override
	public int indexOfOption(Class<? extends BaseOption> option) {
		return OptionList.getManager().indexOf(option);
	}

	@Override
	public void addOption(Class<? extends BaseOption> option) {
		OptionList.getManager().add(option);
	}

	@Override
	public void addOption(int index, Class<? extends BaseOption> option) {
		OptionList.getManager().add(index, option);
	}

	@Override
	public void removeOption(Class<? extends BaseOption> option) {
		OptionList.getManager().remove(option);
	}

	@Override
	public void removeOption(int index) {
		OptionList.getManager().remove(index);
	}

	@Override
	public int indexOfEndProcess(Class<? extends EndProcess> endProcess) {
		return EndProcessManager.getInstance().indexOf(endProcess);
	}

	@Override
	public void addEndProcess(Class<? extends EndProcess> endProcess) {
		EndProcessManager.getInstance().add(endProcess);
	}

	@Override
	public void addEndProcess(int index, Class<? extends EndProcess> endProcess) {
		EndProcessManager.getInstance().add(index, endProcess);
	}

	@Override
	public void removeEndProcess(Class<? extends EndProcess> endProcess) {
		EndProcessManager.getInstance().remove(endProcess);
	}

	@Override
	public void removeEndProcess(int index) {
		EndProcessManager.getInstance().remove(index);
	}

	@Override
	public SBEdit getSBEdit(Location location, ScriptType scriptType) {
		return new ScEdit(location, scriptType);
	}

	private class ScEdit implements SBEdit {

		private final ScriptEdit scriptEdit;

		public ScEdit(Location location, ScriptType scriptType) {
			this.scriptEdit = new ScriptEdit(scriptType);
			setLocation(location);
		}

		@Override
		public void setLocation(Location location) {
			scriptEdit.setLocation(location);
		}

		@Override
		public void save() {
			scriptEdit.save();
		}

		@Override
		public boolean checkPath() {
			return scriptEdit.checkPath();
		}

		@Override
		public ScriptType getScriptType() {
			return scriptEdit.getScriptType();
		}

		@Override
		public void create(Player player, String script) {
			scriptEdit.create(SBPlayer.fromUUID(Objects.requireNonNull(player).getUniqueId()), null, script);
		}

		@Override
		public void add(Player player, String script) {
			scriptEdit.add(SBPlayer.fromUUID(Objects.requireNonNull(player).getUniqueId()), null, script);
		}

		@Override
		public void remove(Player player) {
			scriptEdit.remove(SBPlayer.fromUUID(Objects.requireNonNull(player).getUniqueId()), null);
		}

		@Override
		public void view(Player player) {
			scriptEdit.view(SBPlayer.fromUUID(Objects.requireNonNull(player).getUniqueId()), null);
		}
	}

	@Override
	public SBFile getSBFile(Location location, ScriptType scriptType) {
		return new ScFile(location, scriptType);
	}

	private class ScFile implements SBFile {

		private final ScriptData scriptData;

		public ScFile(Location location, ScriptType scriptType) {
			this.scriptData = new ScriptData(location, scriptType);
		}

		@Override
		public void setLocation(Location location) {
			scriptData.setLocation(location);
		}

		@Override
		public void save() {
			scriptData.save();
		}

		@Override
		public boolean checkPath() {
			return scriptData.checkPath();
		}

		@Override
		public ScriptType getScriptType() {
			return scriptData.getScriptType();
		}

		@Override
		public Location getLocation() {
			return scriptData.getLocation();
		}

		@Override
		public String getAuthor() {
			return scriptData.getAuthor();
		}

		@Override
		public List<String> getAuthors(boolean isName) {
			return scriptData.getAuthors(isName);
		}

		@Override
		public String getLastEdit() {
			return scriptData.getLastEdit();
		}

		@Override
		public int getAmount() {
			return scriptData.getAmount();
		}

		@Override
		public List<String> getScripts() {
			return scriptData.getScripts();
		}

		@Override
		public boolean copyScripts(Location target, boolean overwrite) {
			return scriptData.copyScripts(target, overwrite);
		}

		@Override
		public void setAuthor(OfflinePlayer player) {
			scriptData.setAuthor(player);
		}

		@Override
		public void addAuthor(OfflinePlayer player) {
			scriptData.addAuthor(player);
		}

		@Override
		public void removeAuthor(OfflinePlayer player) {
			scriptData.removeAuthor(player);
		}

		@Override
		public void setLastEdit() {
			scriptData.setLastEdit();
		}

		@Override
		public void setLastEdit(String time) {
			scriptData.setLastEdit(time);
		}

		@Override
		public void setAmount(int amount) {
			scriptData.setAmount(amount);
		}

		@Override
		public void addAmount(int amount) {
			scriptData.addAmount(amount);
		}

		@Override
		public void subtractAmount(int amount) {
			scriptData.subtractAmount(amount);
		}

		@Override
		public void setScripts(List<String> scripts) {
			scriptData.setScripts(scripts);
		}

		@Override
		public void setScript(int index, String script) {
			scriptData.setScript(index, script);
		}

		@Override
		public void addScript(String script) {
			scriptData.addScript(script);
		}

		@Override
		public void addScript(int index, String script) {
			scriptData.addScript(index, script);
		}

		@Override
		public void removeScript(String script) {
			scriptData.removeScript(script);
		}

		@Override
		public void clearScripts() {
			scriptData.clearScripts();
		}

		@Override
		public void remove() {
			scriptData.remove();
		}

		@Override
		public void reload() {
			scriptData.reload();
		}
	}
}