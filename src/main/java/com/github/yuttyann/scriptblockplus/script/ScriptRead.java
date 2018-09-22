package com.github.yuttyann.scriptblockplus.script;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.listener.IAssist;
import com.github.yuttyann.scriptblockplus.manager.EndProcessManager;
import com.github.yuttyann.scriptblockplus.manager.OptionManager.OptionList;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.endprocess.EndProcess;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public final class ScriptRead extends ScriptObjectMap implements SBRead {

	private SBPlayer sbPlayer;
	private List<String> scripts;
	private String optionValue;
	private ScriptData scriptData;
	private BlockCoords blockCoords;
	private int scriptIndex;

	public ScriptRead(IAssist iAssist, Player player, Location location) {
		super(iAssist);
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
	public List<String> getScripts() {
		return scripts;
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
		List<Option> options = OptionList.getList();
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
				Option instance = OptionList.getManager().newInstance(option);
				if (!sbPlayer.isOnline() || !hasPermission(option) || !instance.callOption(this)) {
					executeEndProcess(e -> { if (!instance.isFailedIgnore()) e.failed(this); });
					return false;
				}
			}
		}
		executeEndProcess(e -> e.success(this));
		Utils.sendMessage(SBConfig.getConsoleSuccScriptExecMessage(sbPlayer.getName(), scriptType, blockCoords));
		return true;
	}

	private void executeEndProcess(Consumer<EndProcess> action) {
		try {
			EndProcessManager.getInstance().forEach(e -> action.accept(e));
		} finally {
			clear();
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
			value = StringUtils.replace(value, "<world>", sbPlayer.getWorld().getName());
		}
		return value;
	}

	private boolean sort(List<String> scripts, List<Option> options) {
		try {
			List<String> parse = new ArrayList<>();
			List<String> result = parse;
			StreamUtils.mForEach(scripts, this::getScripts, parse::addAll);
			if (SBConfig.isSortScripts()) {
				List<String> list = (result = new ArrayList<>(parse.size()));
				options.forEach(o -> StreamUtils.fForEach(parse, s -> o.isOption(s), list::add));
			}
			this.scripts = Collections.unmodifiableList(result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private List<String> getScripts(String script) {
		try {
			return StringUtils.getScripts(script);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return null;
	}
}