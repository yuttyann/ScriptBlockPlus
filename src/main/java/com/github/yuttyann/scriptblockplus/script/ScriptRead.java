package com.github.yuttyann.scriptblockplus.script;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.manager.ScriptManager;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public final class ScriptRead extends ScriptManager {

	public static interface EndProcess {

		public void success(ScriptRead scriptRead);

		public void failed(ScriptRead scriptRead);
	}

	private Player player;
	private UUID uuid;
	private String optionValue;
	private List<String> scripts;
	private ScriptData scriptData;
	private BlockCoords blockCoords;
	private int scriptIndex;

	public ScriptRead(ScriptManager scriptManager, Player player, Location location) {
		super(scriptManager);
		this.player = player;
		this.uuid = player.getUniqueId();
		this.blockCoords = new BlockCoords(location);
		this.scriptData = new ScriptData(blockCoords, scriptType, true);
	}

	public Player getPlayer() {
		return player;
	}

	public UUID getUniqueId() {
		return uuid;
	}

	public String getOptionValue() {
		return optionValue;
	}

	public List<String> getScripts() {
		return scripts;
	}

	public ScriptData getScriptData() {
		return scriptData;
	}

	public BlockCoords getBlockCoords() {
		return blockCoords;
	}

	public int getScriptIndex() {
		return scriptIndex;
	}

	public boolean read(int index) {
		if (!scriptData.checkPath()) {
			Utils.sendMessage(player, SBConfig.getErrorScriptFileCheckMessage());
			return false;
		}
		if (!sort(scriptData.getScripts(), optionManager.newInstances())) {
			Utils.sendMessage(player, SBConfig.getErrorScriptMessage(scriptType));
			Utils.sendMessage(SBConfig.getConsoleErrorScriptExecMessage(player, scriptType, blockCoords));
			return false;
		}
		List<Option> options = optionManager.getOptions();
		for (int i = index; i < scripts.size(); i++) {
			String script = scripts.get(i);
			for (Option option : options) {
				if (!option.isOption(script)) {
					continue;
				}
				optionValue = option.getValue(script);
				if (!optionManager.newInstance(option).callOption(this)) {
					StreamUtils.forEach(endProcessManager.newInstances(), r -> r.failed(this));
					return false;
				}
			}
		}
		StreamUtils.forEach(endProcessManager.newInstances(), r -> r.success(this));
		Utils.sendMessage(SBConfig.getConsoleSuccScriptExecMessage(player, scriptType, blockCoords));
		return true;
	}

	private boolean sort(List<String> scripts, Option[] options) {
		try {
			List<String> parse = new ArrayList<String>();
			StreamUtils.mapForEach(scripts, this::getScripts, parse::addAll);
			List<String> result = new ArrayList<String>(parse.size());
			StreamUtils.forEach(options, o -> StreamUtils.filterForEach(parse, s -> o.isOption(s), result::add));
			this.scripts = Collections.unmodifiableList(result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private List<String> getScripts(String scriptLine) {
		try {
			return StringUtils.getScripts(scriptLine);
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		return null;
	}
}