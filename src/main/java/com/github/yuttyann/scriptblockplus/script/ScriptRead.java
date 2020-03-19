package com.github.yuttyann.scriptblockplus.script;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.enums.TextOption;
import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.listener.ScriptListener;
import com.github.yuttyann.scriptblockplus.manager.EndProcessManager;
import com.github.yuttyann.scriptblockplus.manager.OptionManager.OptionList;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.endprocess.EndProcess;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static com.github.yuttyann.scriptblockplus.script.ScriptEdit.args;

public class ScriptRead extends ScriptObjectMap implements SBRead {

	protected SBPlayer sbPlayer;
	protected List<String> scripts;
	protected String optionValue;
	protected ScriptData scriptData;
	protected BlockCoords blockCoords;
	protected int scriptIndex;

	// 継承用
	protected ScriptRead(@NotNull ScriptListener listener) {
		super(listener);
	}

	public ScriptRead(@NotNull Player player, @NotNull Location location, @NotNull ScriptListener listener) {
		super(listener);
		this.sbPlayer = SBPlayer.fromPlayer(player);
		this.scriptData = new ScriptData(location, scriptType, true);
		if (!(location instanceof BlockCoords)) {
			location = new BlockCoords(location);
		}
		setCenter(location);
		this.blockCoords = ((BlockCoords) location).unmodifiable(); // 変更不可に設定
	}

	@NotNull
	@Override
	public SBPlayer getSBPlayer() {
		return sbPlayer;
	}

	@NotNull
	@Override
	public List<String> getScripts() {
		return scripts;
	}
	@NotNull

	@Override
	public String getOptionValue() {
		return optionValue;
	}

	@NotNull
	@Override
	public String getCoords() {
		return blockCoords.getCoords();
	}

	@NotNull
	@Override
	public String getFullCoords() {
		return blockCoords.getFullCoords();
	}

	@NotNull
	@Override
	public Location getLocation() {
		return blockCoords;
	}

	@NotNull
	@Override
	public ScriptData getScriptData() {
		return scriptData;
	}

	@Override
	public int getScriptIndex() {
		return scriptIndex;
	}

	@Override
	public boolean read(int index) {
		Validate.notNull(sbPlayer.getPlayer(), "Player cannot be null");
		if (!scriptData.checkPath()) {
			SBConfig.ERROR_SCRIPT_FILE_CHECK.send(sbPlayer);
			return false;
		}
		List<Option> options = OptionList.getList();
		if (!sort(scriptData.getScripts(), options)) {
			SBConfig.ERROR_SCRIPT_EXECUTE.replace(scriptType.getType()).send(sbPlayer);
			SBConfig.CONSOLE_ERROR_SCRIPT_EXECUTE.replace(args(sbPlayer.getName(), scriptType, blockCoords)).console();
			return false;
		}
		for (scriptIndex = index; scriptIndex < scripts.size(); scriptIndex++) {
			String script = scripts.get(scriptIndex);
			for (Option option : options) {
				if (!option.isOption(script)) {
					continue;
				}
				if (!sbPlayer.isOnline()) {
					executeEndProcess(e -> e.failed(this));
					return false;
				}
				optionValue = TextOption.replaceAll(option.getValue(script), getSBPlayer());
				Option instance = option.newInstance();
				if (!hasPermission(option) || !instance.callOption(this)) {
					executeEndProcess(e -> { if (!instance.isFailedIgnore()) e.failed(this); });
					return false;
				}
			}
		}
		executeEndProcess(e -> e.success(this));
		getSBPlayer().getPlayerCount().add(blockCoords, scriptType);
		SBConfig.CONSOLE_SUCCESS_SCRIPT_EXECUTE.replace(args(sbPlayer.getName(), scriptType, blockCoords)).console();
		return true;
	}

	protected void executeEndProcess(@NotNull Consumer<EndProcess> action) {
		try {
			EndProcessManager.getInstance().forEach(action);
		} finally {
			clear();
		}
	}

	protected boolean hasPermission(@NotNull Option option) {
		if (!SBConfig.OPTION_PERMISSION.toBool() || Permission.has(sbPlayer, option.getPermissionNode())) {
			return true;
		}
		SBConfig.NOT_PERMISSION.send(sbPlayer);
		return false;
	}

	protected boolean sort(@NotNull List<String> scripts, @NotNull List<Option> options) {
		try {
			List<String> parse = new ArrayList<>();
			List<String> result = parse;
			StreamUtils.mForEach(scripts, this::getScripts, parse::addAll);
			if (SBConfig.SORT_SCRIPTS.toBool()) {
				List<String> list = (result = new ArrayList<>(parse.size()));
				options.forEach(o -> StreamUtils.fForEach(parse, o::isOption, list::add));
			}
			this.scripts = Collections.unmodifiableList(result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@NotNull
	protected List<String> getScripts(@NotNull String script) {
		try {
			return StringUtils.getScripts(script);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	private void setCenter(@NotNull Location location) {
		location.setX(location.getBlockX() + 0.5D);
		location.setY(location.getBlockY() + 0.5D);
		location.setZ(location.getBlockZ() + 0.5D);
	}
}