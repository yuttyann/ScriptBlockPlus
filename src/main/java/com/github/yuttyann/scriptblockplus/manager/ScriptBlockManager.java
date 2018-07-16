package com.github.yuttyann.scriptblockplus.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.ScriptBlockAPI;
import com.github.yuttyann.scriptblockplus.listener.IAssist;
import com.github.yuttyann.scriptblockplus.manager.OptionManager.OptionList;
import com.github.yuttyann.scriptblockplus.script.ScriptData;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.script.endprocess.EndProcess;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;

public final class ScriptBlockManager implements ScriptBlockAPI {

	private IAssist iAssist;
	private ScriptData scriptData;
	private Map<ScriptType, List<Location>> timers;
	private Map<Boolean, Map<Location, ScriptType>> scripts;

	public ScriptBlockManager(ScriptBlock plugin, Location location, ScriptType scriptType) {
		this.iAssist = new IAssist(plugin, scriptType);
		this.scriptData = new ScriptData(location, scriptType);
		this.timers = new HashMap<>();
		this.scripts = new HashMap<>();
		setLocation(location);
	}

	@Override
	public boolean scriptRead(Player player) {
		return scriptRead(0, player);
	}

	@Override
	public boolean scriptRead(int index, Player player) {
		if (getScriptType() == null || getLocation() == null) {
			return false;
		}
		return new ScriptRead(iAssist, player, getLocation()).read(index);
	}

	@Override
	public void setLocation(Location location) {
		Location oldLocation = scriptData.getLocation();
		if (oldLocation == null || !oldLocation.equals(location)) {
			if (timers.size() > 0) {
				timers.clear();
			}
			if (scripts.size() > 0) {
				scripts.clear();
			}
			scriptData.setLocation(location);
		}
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
		timers.forEach((s, ll) -> ll.forEach(l -> iAssist.getMapManager().removeTimes(s, l)));
		scripts.forEach((b, m) -> m.forEach((l, s) -> setCoords(iAssist.getMapManager(), s, l, b)));
		if (timers.size() > 0) {
			timers.clear();
		}
		if (scripts.size() > 0) {
			scripts.clear();
		}
	}

	@Override
	public void addOption(Class<? extends BaseOption> option) {
		OptionList.getManager().add(option);
	}

	@Override
	public void addOption(int index, Class<? extends BaseOption> option) {
		OptionList.getManager().add(index, option);
	}

	@Override
	public void removeOption(Class<? extends BaseOption> option) {
		OptionList.getManager().remove(option);
	}

	@Override
	public void removeOption(int scriptIndex) {
		OptionList.getManager().remove(scriptIndex);
	}

	@Override
	public int indexOfOption(Class<? extends BaseOption> option) {
		return OptionList.getManager().indexOf(option);
	}

	@Override
	public void addEndProcess(Class<? extends EndProcess> endProcess) {
		EndProcessManager.getInstance().add(endProcess);
	}

	@Override
	public void addEndProcess(int index, Class<? extends EndProcess> endProcess) {
		EndProcessManager.getInstance().add(index, endProcess);
	}

	@Override
	public void removeEndProcess(Class<? extends EndProcess> endProcess) {
		EndProcessManager.getInstance().remove(endProcess);
	}

	@Override
	public void removeEndProcess(int index) {
		EndProcessManager.getInstance().remove(index);
	}

	@Override
	public int indexOfEndProcess(Class<? extends EndProcess> endProcess) {
		return EndProcessManager.getInstance().indexOf(endProcess);
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

	private void setCoords(MapManager mapManager, ScriptType scriptType, Location location, boolean isAdd) {
		if (isAdd) {
			mapManager.addCoords(scriptType, location);
		} else {
			mapManager.removeCoords(scriptType, location);
		}
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
		Map<Location, ScriptType> value = scripts.get(isAdd);
		if (value == null) {
			value = new HashMap<Location, ScriptType>();
			scripts.put(isAdd, value);
		}
		value.put(location, scriptType);
	}
}