package com.github.yuttyann.scriptblockplus.script.option.time;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;

import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class Cooldown extends BaseOption implements Runnable {

	private int second;

	public Cooldown() {
		super("cooldown", "@cooldown:");
	}

	@Override
	public boolean isValid() {
		if (inCooldown(getFullCoords())) {
			return false;
		}
		start(0L, 20L);
		return true;
	}

	@Override
	public void run() {
		if (--second <= 0) {
			getMapManager().removeCooldown(getScriptType(), getFullCoords(), getUniqueId());
			return;
		}
		System.out.println(second);
	}

	public final void start(long delay, long period) {
		second = Integer.parseInt(getOptionValue());
		getMapManager().putCooldown(getScriptType(), getFullCoords(), getUniqueId(), this);
		Bukkit.getScheduler().runTaskTimer(getPlugin(), this, delay, period);
	}

	private boolean inCooldown(String fullCoords) {
		int original = getSecond(fullCoords);
		if (original > 0) {
			short hour = (short) (original / 3600);
			byte minute = (byte) (original % 3600 / 60);
			byte second = (byte) (original % 3600 % 60);
			Utils.sendMessage(getPlayer(), SBConfig.getActiveCooldownMessage(hour, minute, second));
			return true;
		}
		return false;
	}

	private int getSecond(String fullCoords) {
		Map<String, Map<UUID, Cooldown>> cooldownMap = getMapManager().getCooldownScripts().get(getScriptType());
		Map<UUID, Cooldown> cooldowns = cooldownMap == null ? null : cooldownMap.get(fullCoords);
		Cooldown cooldown = cooldowns == null ? null : cooldowns.get(getUniqueId());
		return cooldown == null ? -1 : cooldown.second;
	}
}