package com.github.yuttyann.scriptblockplus.script;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.listener.ScriptListener;
import com.github.yuttyann.scriptblockplus.manager.EndProcessManager;
import com.github.yuttyann.scriptblockplus.manager.OptionManager;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.endprocess.EndProcess;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.script.option.other.Calculation;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class ScriptRead extends ScriptMap implements SBRead {

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
		this.blockCoords = setCenter(((BlockCoords) location)).unmodifiable();
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
		if (!scriptData.hasPath()) {
			SBConfig.ERROR_SCRIPT_FILE_CHECK.send(sbPlayer);
			return false;
		}
		if (!sort(scriptData.getScripts())) {
			SBConfig.ERROR_SCRIPT_EXECUTE.replace(scriptType).send(sbPlayer);
			SBConfig.CONSOLE_ERROR_SCRIPT_EXECUTE.replace(sbPlayer.getName(), scriptType, blockCoords).console();
			return false;
		}
		for (scriptIndex = index; scriptIndex < scripts.size(); scriptIndex++) {
			if (!sbPlayer.isOnline()) {
				executeEndProcess(e -> e.failed(this));
				return false;
			}
			String script = scripts.get(scriptIndex);
			Option option = OptionManager.get(script).newInstance();
			optionValue = setPlaceholders(option.getValue(script), getSBPlayer());
			if (!hasPermission(option) || !option.callOption(this)) {
				executeEndProcess(e -> { if (!option.isFailedIgnore()) e.failed(this); });
				return false;
			}
		}
		executeEndProcess(e -> e.success(this));
		getSBPlayer().getPlayerCount().add(blockCoords, scriptType);
		SBConfig.CONSOLE_SUCCESS_SCRIPT_EXECUTE.replace(sbPlayer.getName(), scriptType, blockCoords).console();
		return true;
	}

	@NotNull
	protected final String setPlaceholders(@NotNull String source, @NotNull SBPlayer sbPlayer) {
		source = StringUtils.replace(source, "<player>", sbPlayer.getName());
		source = StringUtils.replace(source, "<world>", sbPlayer.getWorld().getName());
		return Calculation.setPlaceholders(Objects.requireNonNull(sbPlayer.getPlayer()), source);
	}

	protected final void executeEndProcess(@NotNull Consumer<EndProcess> action) {
		try {
			EndProcessManager.forEach(action);
		} finally {
			clear();
		}
	}

	protected final boolean hasPermission(@NotNull Option option) {
		if (!SBConfig.OPTION_PERMISSION.getValue() || Permission.has(sbPlayer, option.getPermissionNode())) {
			return true;
		}
		SBConfig.NOT_PERMISSION.send(sbPlayer);
		return false;
	}

	protected final boolean sort(@NotNull List<String> scripts) {
		try {
			List<String> parse = new ArrayList<>();
			for (String script : scripts) {
				parse.addAll(StringUtils.getScripts(script));
			}
			SBConfig.SORT_SCRIPTS.ifPresentAndTrue(s -> OptionManager.sort(parse));
			this.scripts = Collections.unmodifiableList(parse);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@NotNull
	private BlockCoords setCenter(@NotNull BlockCoords blockCoords) {
		blockCoords.setX(blockCoords.getBlockX() + 0.5D);
		blockCoords.setY(blockCoords.getBlockY() + 0.5D);
		blockCoords.setZ(blockCoords.getBlockZ() + 0.5D);
		return blockCoords;
	}
}