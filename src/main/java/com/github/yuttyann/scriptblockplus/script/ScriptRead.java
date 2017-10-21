package com.github.yuttyann.scriptblockplus.script;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.manager.ScriptManager;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public final class ScriptRead extends ScriptManager {

	private SBPlayer sbPlayer;
	private String optionValue;
	private List<String> scripts;
	private ScriptData scriptData;
	private BlockCoords blockCoords;
	private int scriptIndex;

	public ScriptRead(ScriptManager scriptManager, Player player, Location location) {
		super(scriptManager);
		this.sbPlayer = SBPlayer.get(player);
		this.scriptData = new ScriptData(location, scriptType, true);
		this.blockCoords = new BlockCoords(location);
	}

	public SBPlayer getSBPlayer() {
		return sbPlayer;
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
		return blockCoords.clone();
	}

	public int getScriptIndex() {
		return scriptIndex;
	}

	public boolean read(int index) {
		if (!sbPlayer.isOnline()) {
			return false;
		}
		if (!scriptData.checkPath()) {
			Utils.sendMessage(sbPlayer, SBConfig.getErrorScriptFileCheckMessage());
			return false;
		}
		Option[] options = optionManager.newInstances();
		if (!sort(scriptData.getScripts(), options)) {
			Utils.sendMessage(sbPlayer, SBConfig.getErrorScriptMessage(scriptType));
			Utils.sendMessage(SBConfig.getConsoleErrorScriptExecMessage(sbPlayer.getName(), scriptType, blockCoords));
			return false;
		}
		for (scriptIndex = index; scriptIndex < scripts.size(); scriptIndex++) {
			String script = scripts.get(scriptIndex);
			for (Option option : options) {
				if (!option.isOption(script)) {
					continue;
				}
				optionValue = option.getValue(script);
				Option instance = optionManager.newInstance(option);
				if (!sbPlayer.isOnline() || !instance.callOption(this)) {
					if (!instance.isFailedIgnore()) {
						StreamUtils.forEach(endProcessManager.newInstances(), r -> r.failed(this));
					}
					return false;
				}
			}
		}
		StreamUtils.forEach(endProcessManager.newInstances(), r -> r.success(this));
		Utils.sendMessage(SBConfig.getConsoleSuccScriptExecMessage(sbPlayer.getName(), scriptType, blockCoords));
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