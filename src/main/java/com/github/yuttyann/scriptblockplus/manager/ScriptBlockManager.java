package com.github.yuttyann.scriptblockplus.manager;

import java.util.ArrayList;
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

public class ScriptBlockManager extends ScriptData implements ScriptBlockAPI {

	private ScriptBlock plugin;
	private MapManager mapManager;
	private Map<ScriptType, List<BlockLocation>> timerTemps;
	private Map<Boolean, Map<BlockLocation, ScriptType>> scriptTemps;

	public ScriptBlockManager(ScriptBlock plugin, Location location, ScriptType scriptType) {
		super(plugin, BlockLocation.fromLocation(location), scriptType);
		this.mapManager = plugin.getMapManager();
		this.timerTemps = new HashMap<ScriptType, List<BlockLocation>>();
		this.scriptTemps = new HashMap<Boolean, Map<BlockLocation, ScriptType>>();
	}

	@Override
	public void scriptExec(Player player) {
		new ScriptManager(plugin, super.getBlockLocation(), getScriptType()).scriptExec(player);
	}

	@Override
	public void setLocation(Location location) {
		super.setBlockLocation(BlockLocation.fromLocation(location));
	}

	@Override
	public Location getLocation() {
		return super.getBlockLocation();
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
		for (Entry<ScriptType, List<BlockLocation>> timerEntry : timerTemps.entrySet()) {
			ScriptType scriptType = timerEntry.getKey();
			for (BlockLocation blockLocation : timerEntry.getValue()) {
				mapManager.removeTimes(blockLocation, scriptType);
			}
		}
		for (Entry<Boolean, Map<BlockLocation, ScriptType>> scriptEntry : scriptTemps.entrySet()) {
			boolean isAdd = scriptEntry.getKey();
			for (Entry<BlockLocation, ScriptType> scriptEntry2 : scriptEntry.getValue().entrySet()) {
				if (isAdd) {
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
		BlockLocation location = BlockLocation.fromLocation(target);
		super.copyScripts(location, overwrite);
		putScriptMap(true, location, getScriptType());
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
		putScriptMap(true, super.getBlockLocation(), getScriptType());
	}

	@Override
	public void setScript(int index, String script) {
		super.setScript(index, script);
	}

	@Override
	public void addScript(String script) {
		super.addScript(script);
		putTimerMap(super.getBlockLocation(), getScriptType());
	}

	@Override
	public void addScript(int index, String script) {
		super.addScript(index, script);
		putTimerMap(super.getBlockLocation(), getScriptType());
	}

	@Override
	public void removeScript(String script) {
		super.removeScript(script);
		if (super.getScripts().isEmpty()) {
			putScriptMap(false, super.getBlockLocation(), getScriptType());
		} else {
			putTimerMap(super.getBlockLocation(), getScriptType());
		}
	}

	@Override
	public void clearScripts() {
		super.clearScripts();
		putScriptMap(false, super.getBlockLocation(), getScriptType());
	}

	@Override
	public void remove() {
		super.remove();
		putScriptMap(false, super.getBlockLocation(), getScriptType());
	}

	@Override
	public void reload() {
		super.reload();
	}

	private void putTimerMap(BlockLocation location, ScriptType scriptType) {
		List<BlockLocation> value = timerTemps.get(scriptType);
		if (value == null) {
			value = new ArrayList<BlockLocation>();
		}
		value.add(location);
		timerTemps.put(scriptType, value);
	}

	private void putScriptMap(boolean isAdd, BlockLocation location, ScriptType scriptType) {
		Map<BlockLocation, ScriptType> value = scriptTemps.get(location);
		if (value == null) {
			value = new HashMap<BlockLocation, ScriptType>();
		}
		value.put(location, scriptType);
		scriptTemps.put(isAdd, value);
	}
}