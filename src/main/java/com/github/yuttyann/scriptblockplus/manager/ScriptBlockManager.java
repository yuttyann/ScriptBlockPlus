package com.github.yuttyann.scriptblockplus.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.BlockLocation;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.ScriptBlockAPI;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.file.ScriptData;

public class ScriptBlockManager implements ScriptBlockAPI {

	private ScriptBlock plugin;
	private MapManager mapManager;
	private ScriptData scriptData;
	private ScriptManager scriptManager;
	private Map<BlockLocation, ScriptType> timerTemps;
	private Map<Boolean, Map<BlockLocation, ScriptType>> scriptTemps;

	public ScriptBlockManager(ScriptBlock plugin, Location location, ScriptType scriptType) {
		this.mapManager = plugin.getMapManager();
		this.scriptData = new ScriptData(plugin, BlockLocation.fromLocation(location), scriptType);
		this.scriptManager = new ScriptManager(plugin, scriptData.getBlockLocation(), scriptType);
		this.timerTemps = new HashMap<BlockLocation, ScriptType>();
		this.scriptTemps = new HashMap<Boolean, Map<BlockLocation,ScriptType>>();
	}

	@Override
	public void scriptExec(Player player) {
		scriptManager.scriptExec(player);
	}

	@Override
	public void setLocation(Location location) {
		scriptData.setBlockLocation(BlockLocation.fromLocation(location));
		scriptManager = new ScriptManager(plugin, scriptData.getBlockLocation(), getScriptType());
	}

	@Override
	public Location getLocation() {
		return scriptData.getBlockLocation();
	}

	@Override
	public ScriptType getScriptType() {
		return scriptData.getScriptType();
	}

	@Override
	public boolean checkPath() {
		return scriptData.checkPath();
	}

	@Override
	public void save() {
		scriptData.save();
		for (Entry<BlockLocation, ScriptType> timerEntry : timerTemps.entrySet()) {
			mapManager.removeTimes(timerEntry.getKey(), timerEntry.getValue());
		}
		Map<BlockLocation, ScriptType> scriptAdd = scriptTemps.get(true);
		if (scriptAdd != null) {
			for (Entry<BlockLocation, ScriptType> scriptAddEntry : scriptAdd.entrySet()) {
				mapManager.addLocation(scriptAddEntry.getKey(), scriptAddEntry.getValue());
			}
		}
		Map<BlockLocation, ScriptType> scriptRemove = scriptTemps.get(false);
		if (scriptRemove != null) {
			for (Entry<BlockLocation, ScriptType> scriptRemoveEntry : scriptRemove.entrySet()) {
				mapManager.removeLocation(scriptRemoveEntry.getKey(), scriptRemoveEntry.getValue());
			}
		}
		timerTemps.clear();
		scriptTemps.clear();
	}

	@Override
	public String getAuthor() {
		return scriptData.getAuthor();
	}

	@Override
	public List<String> getAuthors(boolean isName) {
		return scriptData.getAuthors(isName);
	}

	@Override
	public String getLastEdit() {
		return scriptData.getLastEdit();
	}

	@Override
	public int getAmount() {
		return scriptData.getAmount();
	}

	@Override
	public List<String> getScripts() {
		return scriptData.getScripts();
	}

	@Override
	public void copyScripts(Location target, boolean overwrite) {
		scriptData.copyScripts(BlockLocation.fromLocation(target), overwrite);
	}

	@Override
	public void setAuthor(Player player) {
		scriptData.setAuthor(player);
	}

	@Override
	public void addAuthor(Player player) {
		scriptData.addAuthor(player);
	}

	@Override
	public void removeAuthor(Player player) {
		scriptData.removeAuthor(player);
	}

	@Override
	public void setLastEdit() {
		scriptData.setLastEdit();
	}

	@Override
	public void addAmount(int amount) {
		scriptData.addAmount(amount);
	}

	@Override
	public void subtractAmount(int amount) {
		scriptData.subtractAmount(amount);
	}

	@Override
	public void setScripts(List<String> scripts) {
		scriptData.setScripts(scripts);
		putScriptMap(true, scriptData.getBlockLocation(), getScriptType());
	}

	@Override
	public void setScript(int index, String script) {
		scriptData.setScript(index, script);
	}

	@Override
	public void addScript(String script) {
		scriptData.addScript(script);
		timerTemps.put(scriptData.getBlockLocation(), getScriptType());
	}

	@Override
	public void addScript(int index, String script) {
		scriptData.addScript(index, script);
		timerTemps.put(scriptData.getBlockLocation(), getScriptType());
	}

	@Override
	public void removeScript(String script) {
		scriptData.removeScript(script);
		if (scriptData.getScripts().isEmpty()) {
			putScriptMap(false, scriptData.getBlockLocation(), getScriptType());
		} else {
			timerTemps.put(scriptData.getBlockLocation(), getScriptType());
		}
	}

	@Override
	public void clearScripts() {
		scriptData.clearScripts();
		putScriptMap(false, scriptData.getBlockLocation(), getScriptType());
	}

	@Override
	public void remove() {
		scriptData.remove();
		putScriptMap(false, scriptData.getBlockLocation(), getScriptType());
	}

	@Override
	public void reload() {
		scriptData.reload();
	}

	private void putScriptMap(boolean key, BlockLocation location, ScriptType scriptType) {
		Map<BlockLocation, ScriptType> value = scriptTemps.get(location);
		if (value == null) {
			value = new HashMap<BlockLocation, ScriptType>();
		}
		value.put(location, scriptType);
		scriptTemps.put(key, value);
	}
}