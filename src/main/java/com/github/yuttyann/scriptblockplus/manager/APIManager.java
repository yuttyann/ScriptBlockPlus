package com.github.yuttyann.scriptblockplus.manager;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.ScriptBlockAPI;
import com.github.yuttyann.scriptblockplus.file.json.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.file.json.PlayerCountJson;
import com.github.yuttyann.scriptblockplus.file.json.element.ScriptParam;
import com.github.yuttyann.scriptblockplus.listener.ScriptListener;
import com.github.yuttyann.scriptblockplus.script.ScriptAction;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.script.endprocess.EndProcess;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.OptionIndex;
import com.github.yuttyann.scriptblockplus.script.option.time.TimerOption;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * ScriptBlockPlus APIManager クラス
 * @author yuttyann44581
 */
public final class APIManager implements ScriptBlockAPI {

	private ScriptBlock plugin;

	public APIManager(@NotNull ScriptBlock plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean scriptRead(@NotNull Player player, @NotNull Location location, @NotNull ScriptType scriptType, int index) {
		if (!BlockScriptJson.has(location, scriptType)) {
			return false;
		}
		return new ScriptRead(player, location, new ScriptListener(plugin, scriptType)).read(index);
	}

	@Override
	public void registerOption(@NotNull OptionIndex optionIndex, @NotNull Class<? extends BaseOption> optionClass) {
		OptionManager.register(optionIndex, optionClass);
	}

	@Override
	public void registerEndProcess(@NotNull Class<? extends EndProcess> endProcessClass) {
		EndProcessManager.register(new SBConstructor<>(endProcessClass));
	}

	@Override
	@NotNull
	public SBEdit getSBEdit(@NotNull ScriptType scriptType) {
		return new SEdit(scriptType);
	}

	private static class SEdit implements SBEdit {

		private final ScriptAction scriptAction;

		public SEdit(@NotNull ScriptType scriptType) {
			this.scriptAction = new ScriptAction(scriptType);
		}

		@Override
		public void save() {
			scriptAction.save();
		}

		@Override
		@NotNull
		public ScriptType getScriptType() {
			return scriptAction.getScriptType();
		}

		@Override
		public void create(@NotNull Player player, @NotNull Location location, @NotNull String script) {
			scriptAction.create(player, location, script);
		}

		@Override
		public void add(@NotNull Player player, @NotNull Location location, @NotNull String script) {
			scriptAction.add(player, location, script);
		}

		@Override
		public void remove(@NotNull Player player, @NotNull Location location) {
			scriptAction.remove(player, location);
		}

		@Override
		public void view(@NotNull Player player, @NotNull Location location) {
			scriptAction.view(player, location);
		}
	}

	@Override
	@NotNull
	public SBFile getSBFile(@NotNull Location location, @NotNull ScriptType scriptType) {
		return new SFile(location, scriptType);
	}

	private static class SFile implements SBFile {

		private final ScriptType scriptType;
		private final Location location;
		private final ScriptParam scriptParam;
		private final BlockScriptJson blockScriotJson;

		public SFile(@NotNull Location location, @NotNull ScriptType scriptType) {
			this.scriptType = scriptType;
			this.location = location;
			this.blockScriotJson = new BlockScriptJson(scriptType);
			this.scriptParam = blockScriotJson.load().get(location);
		}

		@Override
		public void save() {
			blockScriotJson.saveFile();
		}

		@Override
		public boolean has() {
			return blockScriotJson.load().has(location);
		}

		@Override
		@Nullable
		public Location getLocation() {
			return location;
		}

		@Override
		@NotNull
		public ScriptType getScriptType() {
			return scriptType;
		}

		@Override
		@NotNull
		public Set<UUID> getAuthor() {
			return scriptParam.getAuthor();
		}

		@Override
		public void setAuthor(@NotNull Set<UUID> author) {
			scriptParam.setAuthor(author);
		}

		@Override
		@NotNull
		public List<String> getScript() {
			return scriptParam.getScript();
		}

		@Override
		public void setScript(@NotNull List<String> script) {
			scriptParam.setScript(script);
		}

		@Override
		@Nullable
		public String getLastEdit() {
			return scriptParam.getLastEdit();
		}

		@Override
		public void setLastEdit() {
			scriptParam.setLastEdit(Utils.getFormatTime());
		}

		@Override
		public int getAmount() {
			return scriptParam.getAmount();
		}

		@Override
		public void setAmount(int amount) {
			scriptParam.setAmount(amount);
		}

		@Override
		public void addAmount(int amount) {
			scriptParam.addAmount(amount);
		}

		@Override
		public void subtractAmount(int amount) {
			scriptParam.subtractAmount(amount);
		}

		@Override
		public void remove() {
			TimerOption.removeAll(location, scriptType);
			PlayerCountJson.clear(location, scriptType);
			blockScriotJson.load().remove(location);
		}
	}
}