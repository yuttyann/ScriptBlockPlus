package com.github.yuttyann.scriptblockplus.manager;

import java.util.List;
import java.util.Objects;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

	public APIManager(@NotNull ScriptBlock plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean scriptRead(@NotNull Player player, @NotNull Location location, @NotNull ScriptType scriptType, int index) {
		return new ScriptRead(player, location, new ScriptListener(plugin, scriptType)).read(index);
	}

	@Override
	public int indexOfOption(@NotNull Class<? extends BaseOption> option) {
		return OptionList.getManager().indexOf(option);
	}

	@Override
	public void addOption(@NotNull Class<? extends BaseOption> option) {
		OptionList.getManager().add(option);
	}

	@Override
	public void addOption(int index, @NotNull Class<? extends BaseOption> option) {
		OptionList.getManager().add(index, option);
	}

	@Override
	public void removeOption(@NotNull Class<? extends BaseOption> option) {
		OptionList.getManager().remove(option);
	}

	@Override
	public void removeOption(int index) {
		OptionList.getManager().remove(index);
	}

	@Override
	public int indexOfEndProcess(@NotNull Class<? extends EndProcess> endProcess) {
		return EndProcessManager.getInstance().indexOf(endProcess);
	}

	@Override
	public void addEndProcess(@NotNull Class<? extends EndProcess> endProcess) {
		EndProcessManager.getInstance().add(endProcess);
	}

	@Override
	public void addEndProcess(int index, @NotNull Class<? extends EndProcess> endProcess) {
		EndProcessManager.getInstance().add(index, endProcess);
	}

	@Override
	public void removeEndProcess(@NotNull Class<? extends EndProcess> endProcess) {
		EndProcessManager.getInstance().remove(endProcess);
	}

	@Override
	public void removeEndProcess(int index) {
		EndProcessManager.getInstance().remove(index);
	}

	@NotNull
	@Override
	public SBEdit getSBEdit(@NotNull Location location, @NotNull ScriptType scriptType) {
		return new SEdit(location, scriptType);
	}

	private class SEdit implements SBEdit {

		private final ScriptEdit scriptEdit;

		public SEdit(@NotNull Location location, @NotNull ScriptType scriptType) {
			this.scriptEdit = new ScriptEdit(scriptType);
			setLocation(location);
		}

		@Override
		public void setLocation(@NotNull Location location) {
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

		@NotNull
		@Override
		public ScriptType getScriptType() {
			return scriptEdit.getScriptType();
		}

		@Override
		public void create(@NotNull Player player, @NotNull String script) {
			scriptEdit.create(SBPlayer.fromUUID(Objects.requireNonNull(player).getUniqueId()), null, script);
		}

		@Override
		public void add(@NotNull Player player, @NotNull String script) {
			scriptEdit.add(SBPlayer.fromUUID(Objects.requireNonNull(player).getUniqueId()), null, script);
		}

		@Override
		public void remove(@NotNull Player player) {
			scriptEdit.remove(SBPlayer.fromUUID(Objects.requireNonNull(player).getUniqueId()), null);
		}

		@Override
		public void view(@NotNull Player player) {
			scriptEdit.view(SBPlayer.fromUUID(Objects.requireNonNull(player).getUniqueId()), null);
		}
	}

	@NotNull
	@Override
	public SBFile getSBFile(@Nullable Location location, @NotNull ScriptType scriptType) {
		return new SFile(location, scriptType);
	}

	private class SFile implements SBFile {

		private final ScriptData scriptData;

		public SFile(@Nullable Location location, @NotNull ScriptType scriptType) {
			this.scriptData = new ScriptData(location, scriptType);
		}

		@Override
		public void setLocation(@Nullable Location location) {
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

		@NotNull
		@Override
		public String getScriptPath() {
			return scriptData.getScriptPath();
		}

		@NotNull
		@Override
		public ScriptType getScriptType() {
			return scriptData.getScriptType();
		}

		@Nullable
		@Override
		public Location getLocation() {
			return scriptData.getLocation();
		}

		@Nullable
		@Override
		public String getAuthor() {
			return scriptData.getAuthor();
		}

		@NotNull
		@Override
		public List<String> getAuthors(boolean isMinecraftID) {
			return scriptData.getAuthors(isMinecraftID);
		}

		@Nullable
		@Override
		public String getLastEdit() {
			return scriptData.getLastEdit();
		}

		@Override
		public int getAmount() {
			return scriptData.getAmount();
		}

		@NotNull
		@Override
		public List<String> getScripts() {
			return scriptData.getScripts();
		}

		@Override
		public boolean copyScripts(@NotNull Location target, boolean overwrite) {
			return scriptData.copyScripts(target, overwrite);
		}

		@Override
		public void setAuthor(@NotNull OfflinePlayer player) {
			scriptData.setAuthor(player);
		}

		@Override
		public void addAuthor(@NotNull OfflinePlayer player) {
			scriptData.addAuthor(player);
		}

		@Override
		public void removeAuthor(@NotNull OfflinePlayer player) {
			scriptData.removeAuthor(player);
		}

		@Override
		public void setLastEdit() {
			scriptData.setLastEdit();
		}

		@Override
		public void setLastEdit(@NotNull String time) {
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
		public void setScripts(@NotNull List<String> scripts) {
			scriptData.setScripts(scripts);
		}

		@Override
		public void setScript(int index, @NotNull String script) {
			scriptData.setScript(index, script);
		}

		@Override
		public void addScript(@NotNull String script) {
			scriptData.addScript(script);
		}

		@Override
		public void addScript(int index, @NotNull String script) {
			scriptData.addScript(index, script);
		}

		@Override
		public void removeScript(@NotNull String script) {
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