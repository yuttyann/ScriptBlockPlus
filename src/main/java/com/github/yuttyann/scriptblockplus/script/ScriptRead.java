package com.github.yuttyann.scriptblockplus.script;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.manager.ScriptManager;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public final class ScriptRead extends ScriptManager implements SBRead {

	private SBPlayer sbPlayer;
	private String optionValue;
	private List<String> scripts;
	private ScriptData scriptData;
	private BlockCoords blockCoords;
	private int scriptIndex;

	public ScriptRead(ScriptManager scriptManager, Player player, Location location) {
		super(scriptManager);
		this.sbPlayer = SBPlayer.fromPlayer(player);
		this.scriptData = new ScriptData(location, scriptType, true);
		if (!(location instanceof BlockCoords)) {
			location = new BlockCoords(location);
		}
		this.blockCoords = ((BlockCoords) location).unmodifiable(); // 変更不可に設定
	}

	@Override
	public SBPlayer getSBPlayer() {
		return sbPlayer;
	}

	@Override
	public String getOptionValue() {
		return optionValue;
	}

	@Override
	public String getCoords() {
		return blockCoords.getCoords();
	}

	@Override
	public String getFullCoords() {
		return blockCoords.getFullCoords();
	}

	@Override
	public Location getLocation() {
		return blockCoords;
	}

	@Override
	public List<String> getScripts() {
		return scripts;
	}

	@Override
	public ScriptData getScriptData() {
		return scriptData;
	}

	@Override
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
		List<Option> options = optionManager.getTempOptions();
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
				optionValue = replaceValue(option.getValue(script));
				Option instance = optionManager.newInstance(option);
				if (!sbPlayer.isOnline() || !hasPermission(option) || !instance.callOption(this)) {
					if (!instance.isFailedIgnore()) {
						endProcessManager.forEach(e -> e.failed(this), true);
					}
					return false;
				}
			}
		}
		endProcessManager.forEach(e -> e.success(this), true);
		Utils.sendMessage(SBConfig.getConsoleSuccScriptExecMessage(sbPlayer.getName(), scriptType, blockCoords));
		return true;
	}

	private boolean sort(List<String> scripts, List<Option> options) {
		try {
			List<String> parse = new ArrayList<String>();
			List<String> result = parse;
			StreamUtils.mapForEach(scripts, this::getScripts, parse::addAll);
			if (SBConfig.isSortScripts()) {
				List<String> list = (result = new ArrayList<String>(parse.size()));
				options.forEach(o -> StreamUtils.filterForEach(parse, s -> o.isOption(s), list::add));
			}
			this.scripts = Collections.unmodifiableList(result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean hasPermission(Option option) {
		if (!SBConfig.isOptionPermission() || Permission.has(sbPlayer, option.getPermissionNode())) {
			return true;
		}
		Utils.sendMessage(sbPlayer, SBConfig.getNotPermissionMessage());
		return false;
	}

	private String replaceValue(String value) {
		if (sbPlayer.isOnline() && StringUtils.isNotEmpty(value)) {
			value = StringUtils.replace(value, "<player>", sbPlayer.getName());
			value = StringUtils.replace(value, "<world>", sbPlayer.getLocation().getWorld().getName());
		}
		return value;
	}

	private List<String> getScripts(String scriptLine) {
		try {
			return StringUtils.getScripts(scriptLine);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return null;
	}
}