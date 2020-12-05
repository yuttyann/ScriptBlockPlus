package com.github.yuttyann.scriptblockplus.script.option.time;

import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.manager.EndProcessManager;
import com.github.yuttyann.scriptblockplus.manager.auxiliary.SBMap;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * ScriptBlockPlus Delay オプションクラス
 * @author yuttyann44581
 */
public class Delay extends BaseOption implements Runnable {

	private static final SBMap<Set<UUID>> DELAY_MAP = new SBMap<>();

	private boolean delaySave;

	public Delay() {
		super("delay", "@delay:");
	}

	@Override
	@NotNull
	public Option newInstance() {
		return new Delay();
	}

	@Override
	public boolean isFailedIgnore() {
		return true;
	}

	@Override
	protected boolean isValid() throws Exception {
		String[] array = StringUtils.split(getOptionValue(), "/");
		delaySave = array.length <= 1 || Boolean.parseBoolean(array[1]);
		if (delaySave && containsDelay(getUniqueId(), getScriptType(), getFullCoords())) {
			SBConfig.ACTIVE_DELAY.send(getSBPlayer());
		} else {
			if (delaySave) {
				putDelay(getUniqueId(), getScriptType(), getFullCoords());
			}
			Bukkit.getScheduler().runTaskLater(getPlugin(), this, Long.parseLong(array[0]));
		}
		return false;
	}

	@Override
	public void run() {
		if (delaySave) {
			removeDelay(getUniqueId(), getScriptType(), getFullCoords());
		}
		if (getSBPlayer().isOnline()) {
			getSBRead().read(getScriptIndex() + 1);
		} else {
			EndProcessManager.forEach(e -> e.failed(getSBRead()));
		}
	}

	private void putDelay(@NotNull UUID uuid, @NotNull ScriptType scriptType, @NotNull String fullCoords) {
		Set<UUID> set = DELAY_MAP.get(scriptType, fullCoords);
		if (set == null) {
			DELAY_MAP.put(scriptType, fullCoords, set = new HashSet<>());
		}
		set.add(uuid);
	}

	private void removeDelay(@NotNull UUID uuid, @NotNull ScriptType scriptType, @NotNull String fullCoords) {
		Optional.ofNullable(DELAY_MAP.get(scriptType, fullCoords)).ifPresent(v -> v.remove(uuid));
	}

	private boolean containsDelay(@NotNull UUID uuid, @NotNull ScriptType scriptType, @NotNull String fullCoords) {
		Optional<Set<UUID>> value = Optional.ofNullable(DELAY_MAP.get(scriptType, fullCoords));
		return value.isPresent() && value.get().contains(uuid);
	}
}