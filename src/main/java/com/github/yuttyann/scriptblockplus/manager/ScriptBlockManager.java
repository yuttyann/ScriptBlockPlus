package com.github.yuttyann.scriptblockplus.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.ScriptBlockAPI;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.script.ScriptData;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.script.option.Option;

public class ScriptBlockManager extends ScriptManager implements ScriptBlockAPI {

	private ScriptData scriptData;
	private BlockCoords blockCoords;
	private Map<ScriptType, List<Location>> timerTemps;
	private Map<Boolean, Map<Location, ScriptType>> scriptTemps;

	public ScriptBlockManager(ScriptBlock plugin, Location location, ScriptType scriptType) {
		super(plugin, scriptType);
		this.scriptData = new ScriptData(location, scriptType);
		this.blockCoords = new BlockCoords(scriptData.getLocation());
		this.timerTemps = new HashMap<ScriptType, List<Location>>();
		this.scriptTemps = new HashMap<Boolean, Map<Location, ScriptType>>();
	}

	@Override
	public boolean scriptRead(Player player) {
		return new ScriptRead(this, player, blockCoords).read(0);
	}

	@Override
	public boolean scriptRead(int index, Player player) {
		return new ScriptRead(this, player, blockCoords).read(index);
	}

	@Override
	public void setLocation(Location location) {
		timerTemps.clear();
		scriptTemps.clear();
		scriptData.setLocation(location);
		blockCoords = new BlockCoords(location);
	}

	@Override
	public Location getLocation() {
		return scriptData.getLocation();
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
	public void addOption(Option option) {
		getOptionManager().addOption(option);
	}

	@Override
	public void addOption(int index, Option option) {
		getOptionManager().addOption(index, option);
	}

	@Override
	public void removeOption(Option option) {
		getOptionManager().removeOption(option);
	}

	@Override
	public void removeOption(int index) {
		getOptionManager().removeOption(index);
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
		scriptData.copyScripts(target, overwrite);
		putScriptMap(true, target, getScriptType());
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
		putScriptMap(true, scriptData.getLocation(), getScriptType());
	}

	@Override
	public void setScript(int index, String script) {
		scriptData.setScript(index, script);
	}

	@Override
	public void addScript(String script) {
		scriptData.addScript(script);
		putTimerMap(scriptData.getLocation(), getScriptType());
	}

	@Override
	public void addScript(int index, String script) {
		scriptData.addScript(index, script);
		putTimerMap(scriptData.getLocation(), getScriptType());
	}

	@Override
	public void removeScript(String script) {
		scriptData.removeScript(script);
		if (scriptData.getScripts().isEmpty()) {
			putScriptMap(false, scriptData.getLocation(), getScriptType());
		} else {
			putTimerMap(scriptData.getLocation(), getScriptType());
		}
	}

	@Override
	public void clearScripts() {
		scriptData.clearScripts();
		putScriptMap(false, scriptData.getLocation(), getScriptType());
	}

	@Override
	public void remove() {
		scriptData.remove();
		putScriptMap(false, scriptData.getLocation(), getScriptType());
	}

	@Override
	public void reload() {
		scriptData.reload();
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