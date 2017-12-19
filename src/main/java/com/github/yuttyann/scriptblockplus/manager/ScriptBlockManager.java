package com.github.yuttyann.scriptblockplus.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.ScriptBlockAPI;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.event.ScriptBlockBreakEvent;
import com.github.yuttyann.scriptblockplus.event.ScriptBlockEvent;
import com.github.yuttyann.scriptblockplus.event.ScriptBlockInteractEvent;
import com.github.yuttyann.scriptblockplus.event.ScriptBlockWalkEvent;
import com.github.yuttyann.scriptblockplus.script.ScriptData;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.script.endprocess.EndProcess;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;

public final class ScriptBlockManager implements ScriptBlockAPI {

	private ScriptData scriptData;
	private ScriptManager scriptManager;
	private Map<ScriptType, List<Location>> timers;
	private Map<Boolean, Map<Location, ScriptType>> scripts;

	public ScriptBlockManager(ScriptBlock plugin, Location location, ScriptType scriptType) {
		this.scriptData = new ScriptData(location, scriptType);
		this.scriptManager = new ScriptManager(plugin, scriptType);
		this.timers = new HashMap<ScriptType, List<Location>>();
		this.scripts = new HashMap<Boolean, Map<Location, ScriptType>>();
		setLocation(location);
	}

	@Override
	public boolean scriptRead(Player player) {
		return scriptRead(0, player);
	}

	@Override
	public boolean scriptRead(int index, Player player) {
		try {
			if (getScriptType() == null || getLocation() == null) {
				return false;
			}
			ScriptBlockEvent event = callEvent(player, getScriptType());
			if (event == null || event.isCancelled()) {
				return false;
			}
			return new ScriptRead(scriptManager, player, getLocation()).read(index);
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public void setLocation(Location location) {
		if (timers.size() > 0) {
			timers.clear();
		}
		if (scripts.size() > 0) {
			scripts.clear();
		}
		scriptData.setLocation(location);
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
		timers.forEach((s, ll) -> ll.forEach(l -> scriptManager.getMapManager().removeTimes(s, l)));
		scripts.forEach((b, m) -> m.forEach((l, s) -> scriptManager.getMapManager().setCoords(s, l, b)));
		if (timers.size() > 0) {
			timers.clear();
		}
		if (scripts.size() > 0) {
			scripts.clear();
		}
	}

	@Override
	public void addOption(int sortIndex, Class<? extends BaseOption> option) {
		scriptManager.getOptionManager().add(-1, sortIndex, option);
	}

	@Override
	public void addOption(int scriptIndex, int sortIndex, Class<? extends BaseOption> option) {
		scriptManager.getOptionManager().add(scriptIndex, sortIndex, option);
	}

	@Override
	public void removeOption(Class<? extends BaseOption> option) {
		scriptManager.getOptionManager().remove(option);
	}

	@Override
	public void removeOption(int scriptIndex) {
		scriptManager.getOptionManager().remove(scriptIndex);
	}

	@Override
	public int indexOfOption(Class<? extends BaseOption> option) {
		return scriptManager.getOptionManager().indexOf(option);
	}

	@Override
	public void addEndProcess(Class<? extends EndProcess> endProcess) {
		scriptManager.getEndProcessManager().add(endProcess);
	}

	@Override
	public void addEndProcess(int index, Class<? extends EndProcess> endProcess) {
		scriptManager.getEndProcessManager().add(index, endProcess);
	}

	@Override
	public void removeEndProcess(Class<? extends EndProcess> endProcess) {
		scriptManager.getEndProcessManager().remove(endProcess);
	}

	@Override
	public void removeEndProcess(int index) {
		scriptManager.getEndProcessManager().remove(index);
	}

	@Override
	public int indexOfEndProcess(Class<? extends EndProcess> endProcess) {
		return scriptManager.getEndProcessManager().indexOf(endProcess);
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
	public boolean copyScripts(Location target, boolean overwrite) {
		if (scriptData.copyScripts(target, overwrite)) {
			putScript(getScriptType(), target, true);
			return true;
		}
		return false;
	}

	@Override
	public void setAuthor(OfflinePlayer player) {
		scriptData.setAuthor(player);
	}

	@Override
	public void addAuthor(OfflinePlayer player) {
		scriptData.addAuthor(player);
	}

	@Override
	public void removeAuthor(OfflinePlayer player) {
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
		putScript(getScriptType(), scriptData.getLocation(), true);
	}

	@Override
	public void setScript(int index, String script) {
		scriptData.setScript(index, script);
	}

	@Override
	public void addScript(String script) {
		scriptData.addScript(script);
		putTimer(getScriptType(), scriptData.getLocation());
	}

	@Override
	public void addScript(int index, String script) {
		scriptData.addScript(index, script);
		putTimer(getScriptType(), scriptData.getLocation());
	}

	@Override
	public void removeScript(String script) {
		scriptData.removeScript(script);
		if (scriptData.getScripts().isEmpty()) {
			putScript(getScriptType(), scriptData.getLocation(), false);
		} else {
			putTimer(getScriptType(), scriptData.getLocation());
		}
	}

	@Override
	public void clearScripts() {
		scriptData.clearScripts();
		putScript(getScriptType(), scriptData.getLocation(), false);
	}

	@Override
	public void remove() {
		scriptData.remove();
		putScript(getScriptType(), scriptData.getLocation(), false);
	}

	@Override
	public void reload() {
		scriptData.reload();
	}

	private ScriptBlockEvent callEvent(Player player, ScriptType scriptType) {
		ScriptBlockEvent event = null;
		switch (scriptType) {
		case INTERACT:
			event = new ScriptBlockInteractEvent(player, getLocation().getBlock());
			break;
		case BREAK:
			event = new ScriptBlockBreakEvent(player, getLocation().getBlock());
			break;
		case WALK:
			event = new ScriptBlockWalkEvent(player, getLocation().getBlock());
			break;
		default:
			return null;
		}
		if (event != null) {
			Bukkit.getPluginManager().callEvent(event);
		}
		return event;
	}

	private void putTimer(ScriptType scriptType, Location location) {
		List<Location> value = timers.get(scriptType);
		if (value == null) {
			value = new ArrayList<Location>();
			timers.put(scriptType, value);
		}
		value.add(location);
	}

	private void putScript(ScriptType scriptType, Location location, boolean isAdd) {
		Map<Location, ScriptType> value = scripts.get(location);
		if (value == null) {
			value = new HashMap<Location, ScriptType>();
			scripts.put(isAdd, value);
		}
		value.put(location, scriptType);
	}
}