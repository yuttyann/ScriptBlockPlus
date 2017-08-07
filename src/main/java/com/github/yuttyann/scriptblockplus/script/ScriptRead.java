package com.github.yuttyann.scriptblockplus.script;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.file.Lang;
import com.github.yuttyann.scriptblockplus.manager.ScriptManager;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class ScriptRead extends ScriptManager {

	private Player player;
	private UUID uuid;
	private String optionData;
	private List<String> scripts;
	private ScriptData scriptData;
	private BlockCoords blockCoords;
	private int scriptIndex;

	public ScriptRead(ScriptManager scriptManager, Player player, BlockCoords blockCoords) {
		super(scriptManager);
		this.player = player;
		this.uuid = player.getUniqueId();
		this.blockCoords = blockCoords;
		this.scriptData = new ScriptData(blockCoords, scriptType);
	}

	public Player getPlayer() {
		return player;
	}

	public UUID getUUID() {
		return uuid;
	}

	public String getOptionData() {
		return optionData;
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
			Utils.sendPluginMessage(player, Lang.getErrorScriptFileCheckMessage());
			return false;
		}
		if (!sort(scriptData.getScripts())) {
			Utils.sendPluginMessage(player, Lang.getErrorScriptMessage(scriptType));
			Utils.sendPluginMessage(Lang.getConsoleErrorScriptExecMessage(player, scriptType, blockCoords.getWorld(), blockCoords.getCoords()));
			return false;
		}
		Map<UUID, Double> moneyCosts = mapManager.getMoneyCosts();
		for (int i = index; i < scripts.size(); i++) {
			String script = scripts.get(i);
			for (Option option : mapManager.getOptions()) {
				if (!script.startsWith(option.getPrefix())) {
					continue;
				}
				optionData = getOptionData(script, option);
				if (!option.callOption(this)) {
					Double cost = moneyCosts.get(uuid);
					if (cost != null) {
						moneyCosts.remove(uuid);
						vaultEconomy.depositPlayer(player, cost);
					}
					return false;
				}
			}
		}
		moneyCosts.remove(uuid);
		Utils.sendPluginMessage(Lang.getConsoleSuccScriptExecMessage(player, scriptType, blockCoords.getWorld(), blockCoords.getCoords()));
		return true;
	}

	private boolean sort(List<String> scripts) {
		try {
			List<String> temp = new ArrayList<String>();
			for (String scriptText : scripts) {
				temp.addAll(StringUtils.getScripts(scriptText));
			}
			List<String> result = new ArrayList<String>();
			for (Option option : mapManager.getOptions()) {
				for (String script : temp) {
					if (script.startsWith(option.getPrefix())) {
						result.add(script);
					}
				}
			}
			this.scripts = result;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private String getOptionData(String script, Option option) {
		return StringUtils.removeStart(script, option.getPrefix());
	}
}