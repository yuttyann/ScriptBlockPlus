package com.github.yuttyann.scriptblockplus.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.ScriptBlockAPI;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.file.ScriptData;

public class ScriptBlockManager extends ScriptData implements ScriptBlockAPI {

	private ScriptBlock plugin;
	private MapManager mapManager;
	private Map<ScriptType, List<Location>> timerTemps;
	private Map<Boolean, Map<Location, ScriptType>> scriptTemps;

	public ScriptBlockManager(ScriptBlock plugin, Location location, ScriptType scriptType) {
		super(plugin, location, scriptType);
		this.plugin = plugin;
		this.mapManager = plugin.getMapManager();
		this.timerTemps = new HashMap<ScriptType, List<Location>>();
		this.scriptTemps = new HashMap<Boolean, Map<Location, ScriptType>>();
	}

	@Override
	public void scriptExec(Player player) {
		new ScriptManager(plugin, getLocation(), getScriptType()).scriptExec(player);
	}

	@Override
	public void setLocation(Location location) {
		timerTemps.clear();
		scriptTemps.clear();
		super.setLocation(location);
	}

	@Override
	public Location getLocation() {
		return super.getLocation();
	}

	@Override
	public ScriptType getScriptType() {
		return super.getScriptType();
	}

	@Override
	public boolean checkPath() {
		return super.checkPath();
	}

	@Override
	public void save() {
		super.save();
		for (Entry<ScriptType, List<Location>> timerEntry : timerTemps.entrySet()) {
			ScriptType scriptType = timerEntry.getKey();
			for (Location blockLocation : timerEntry.getValue()) {
				mapManager.removeTimes(blockLocation, scriptType);
			}
		}
		for (Entry<Boolean, Map<Location, ScriptType>> scriptEntry : scriptTemps.entrySet()) {
			for (Entry<Location, ScriptType> scriptEntry2 : scriptEntry.getValue().entrySet()) {
				if (scriptEntry.getKey()) {
					mapManager.addLocation(scriptEntry2.getKey(), scriptEntry2.getValue());
				} else {
					mapManager.removeLocation(scriptEntry2.getKey(), scriptEntry2.getValue());
				}
			}
		}
		timerTemps.clear();
		scriptTemps.clear();
	}

	@Override
	public String getAuthor() {
		return super.getAuthor();
	}

	@Override
	public List<String> getAuthors(boolean isName) {
		return super.getAuthors(isName);
	}

	@Override
	public String getLastEdit() {
		return super.getLastEdit();
	}

	@Override
	public int getAmount() {
		return super.getAmount();
	}

	@Override
	public List<String> getScripts() {
		return super.getScripts();
	}

	@Override
	public void copyScripts(Location target, boolean overwrite) {
		super.copyScripts(target, overwrite);
		putScriptMap(true, target, getScriptType());
	}

	@Override
	public void setAuthor(Player player) {
		super.setAuthor(player);
	}

	@Override
	public void addAuthor(Player player) {
		super.addAuthor(player);
	}

	@Override
	public void removeAuthor(Player player) {
		super.removeAuthor(player);
	}

	@Override
	public void setLastEdit() {
		super.setLastEdit();
	}

	@Override
	public void addAmount(int amount) {
		super.addAmount(amount);
	}

	@Override
	public void subtractAmount(int amount) {
		super.subtractAmount(amount);
	}

	@Override
	public void setScripts(List<String> scripts) {
		super.setScripts(scripts);
		putScriptMap(true, super.getLocation(), getScriptType());
	}

	@Override
	public void setScript(int index, String script) {
		super.setScript(index, script);
	}

	@Override
	public void addScript(String script) {
		super.addScript(script);
		putTimerMap(super.getLocation(), getScriptType());
	}

	@Override
	public void addScript(int index, String script) {
		super.addScript(index, script);
		putTimerMap(super.getLocation(), getScriptType());
	}

	@Override
	public void removeScript(String script) {
		super.removeScript(script);
		if (super.getScripts().isEmpty()) {
			putScriptMap(false, super.getLocation(), getScriptType());
		} else {
			putTimerMap(super.getLocation(), getScriptType());
		}
	}

	@Override
	public void clearScripts() {
		super.clearScripts();
		putScriptMap(false, super.getLocation(), getScriptType());
	}

	@Override
	public void remove() {
		super.remove();
		putScriptMap(false, super.getLocation(), getScriptType());
	}

	@Override
	public void reload() {
		super.reload();
	}

	private void putTimerMap(Location location, ScriptType scriptType) {
		List<Location> value = timerTemps.get(scriptType);
		if (value == null) {
			value = new ArrayList<Location>();
		}
		value.add(location);
		timerTemps.put(scriptType, value);
	}

	private void putScriptMap(boolean isAdd, Location location, ScriptType scriptType) {
		Map<Location, ScriptType> value = scriptTemps.get(location);
		if (value == null) {
			value = new HashMap<Location, ScriptType>();
		}
		value.put(location, scriptType);
		scriptTemps.put(isAdd, value);
	}
}