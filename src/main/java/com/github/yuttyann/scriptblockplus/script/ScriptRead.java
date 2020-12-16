package com.github.yuttyann.scriptblockplus.script;

import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.json.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.file.json.PlayerCountJson;
import com.github.yuttyann.scriptblockplus.file.json.element.BlockScript;
import com.github.yuttyann.scriptblockplus.file.json.element.PlayerCount;
import com.github.yuttyann.scriptblockplus.hook.plugin.Placeholder;
import com.github.yuttyann.scriptblockplus.listener.ScriptListener;
import com.github.yuttyann.scriptblockplus.manager.EndProcessManager;
import com.github.yuttyann.scriptblockplus.manager.OptionManager;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.endprocess.EndProcess;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.unmodifiable.UnmodifiableLocation;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * ScriptBlockPlus ScriptRead クラス
 * @author yuttyann44581
 */
public class ScriptRead extends ScriptMap implements SBRead {

	protected SBPlayer sbPlayer;
	protected Location location;
	protected BlockScript blockScript;
	
	protected List<String> script;
	protected String optionValue;
	protected int scriptIndex;

	// 継承用
	protected ScriptRead(@NotNull ScriptListener listener) {
		super(listener);
	}

	public ScriptRead(@NotNull Player player, @NotNull Location location, @NotNull ScriptListener listener) {
		super(listener);
		location.setX(location.getBlockX() + 0.5D);
		location.setY(location.getBlockY() + 0.5D);
		location.setZ(location.getBlockZ() + 0.5D);
		this.sbPlayer = SBPlayer.fromPlayer(player);
		this.location = new UnmodifiableLocation(location); // 変更不可
		this.blockScript = new BlockScriptJson(getScriptType()).load();
	}

	@Override
	@NotNull
	public SBPlayer getSBPlayer() {
		return sbPlayer;
	}

	@Override
	@NotNull
	public List<String> getScript() {
		return script;
	}

	@Override
	@NotNull
	public String getOptionValue() {
		return optionValue;
	}

	@Override
	@NotNull
	public Location getLocation() {
		return location;
	}

	@Override
	public int getScriptIndex() {
		return scriptIndex;
	}

	@Override
	public boolean read(int index) {
		Validate.notNull(sbPlayer.getPlayer(), "Player cannot be null");
		if (!blockScript.has(location)) {
			SBConfig.ERROR_SCRIPT_FILE_CHECK.send(sbPlayer);
			return false;
		}
		if (!sort(blockScript.get(location).getScript())) {
			SBConfig.ERROR_SCRIPT_EXECUTE.replace(scriptType).send(sbPlayer);
			SBConfig.CONSOLE_ERROR_SCRIPT_EXECUTE.replace(sbPlayer.getName(), scriptType, location).console();
			return false;
		}
		for (scriptIndex = index; scriptIndex < script.size(); scriptIndex++) {
			if (!sbPlayer.isOnline()) {
				executeEndProcess(e -> e.failed(this));
				return false;
			}
			String script = this.script.get(scriptIndex);
			Option option = OptionManager.get(script).newInstance();
			optionValue = setPlaceholders(getSBPlayer(), option.getValue(script));
			if (!hasPermission(option) || !option.callOption(this)) {
				executeEndProcess(e -> { if (!option.isFailedIgnore()) e.failed(this); });
				return false;
			}
		}
		executeEndProcess(e -> e.success(this));
		new PlayerCountJson(sbPlayer.getUniqueId()).action(PlayerCount::add, location, scriptType);
		SBConfig.CONSOLE_SUCCESS_SCRIPT_EXECUTE.replace(sbPlayer.getName(), scriptType, location).console();
		return true;
	}

	@NotNull
	protected final String setPlaceholders(@NotNull SBPlayer sbPlayer, @NotNull String source) {
		Optional<Player> value = Optional.ofNullable(sbPlayer.getPlayer());
		if (!value.isPresent()) {
			return source;
		}
		Player player = value.get();
		if (Placeholder.INSTANCE.has()) {
			source = Placeholder.INSTANCE.set(player, source);
		}
		source = StringUtils.replace(source, "<player>", player.getName());
		source = StringUtils.replace(source, "<world>", player.getWorld().getName());
		return source;
	}

	protected final void executeEndProcess(@NotNull Consumer<EndProcess> action) {
		try {
			EndProcessManager.forEach(action);
		} finally {
			clear();
		}
	}

	protected final boolean hasPermission(@NotNull Option option) {
		if (!SBConfig.OPTION_PERMISSION.getValue()
				|| Permission.has(sbPlayer, Option.PERMISSION_ALL)
				|| Permission.has(sbPlayer, option.getPermissionNode())) {
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
			this.script = Collections.unmodifiableList(parse);
			return true;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return false;
		}
	}
}