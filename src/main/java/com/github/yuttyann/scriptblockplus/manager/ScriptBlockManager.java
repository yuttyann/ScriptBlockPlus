package com.github.yuttyann.scriptblockplus.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
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

public class ScriptBlockManager extends ScriptManager implements ScriptBlockAPI {

	private ScriptData scriptData;
	private Map<ScriptType, List<Location>> timerTemps;
	private Map<Boolean, Map<Location, ScriptType>> scriptTemps;

	public ScriptBlockManager(ScriptBlock plugin, Location location, ScriptType scriptType) {
		super(plugin, scriptType);
		this.timerTemps = new HashMap<ScriptType, List<Location>>();
		this.scriptTemps = new HashMap<Boolean, Map<Location, ScriptType>>();
		setLocation(location);
	}

	@Override
	public boolean scriptRead(Player player) {
		if (callEvent(player, scriptType)) {
			return new ScriptRead(this, player.getUniqueId(), getLocation()).read(0);
		}
		return false;
	}

	@Override
	public boolean scriptRead(int index, Player player) {
		if (callEvent(player, scriptType)) {
			return new ScriptRead(this, player.getUniqueId(), getLocation()).read(index);
		}
		return false;
	}

	@Override
	public void setLocation(Location location) {
		if (timerTemps.size() > 0) {
			timerTemps.clear();
		}
		if (scriptTemps.size() > 0) {
			scriptTemps.clear();
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
		timerTemps.forEach((s, ll) -> ll.forEach(l -> mapManager.removeTimes(s, l)));
		scriptTemps.forEach((b, m) -> m.forEach((l, s) -> {
			if (b) {
				mapManager.addCoords(s, l);
			} else {
				mapManager.removeCoords(s, l);
			}
		}));
		if (timerTemps.size() > 0) {
			timerTemps.clear();
		}
		if (scriptTemps.size() > 0) {
			scriptTemps.clear();
		}
	}

	@Override
	public void addOption(Class<? extends BaseOption> option) {
		getOptionManager().add(option);
	}

	@Override
	public void addOption(int index, Class<? extends BaseOption> option) {
		getOptionManager().add(index, option);
	}

	@Override
	public void removeOption(Class<? extends BaseOption> option) {
		getOptionManager().remove(option);
	}

	@Override
	public void removeOption(int index) {
		getOptionManager().remove(index);
	}

	@Override
	public int indexOfOption(Class<? extends BaseOption> option) {
		return getOptionManager().indexOf(option);
	}

	@Override
	public void addEndProcess(Class<? extends EndProcess> endProcess) {
		getEndProcessManager().add(endProcess);
	}

	@Override
	public void addEndProcess(int index, Class<? extends EndProcess> endProcess) {
		getEndProcessManager().add(index, endProcess);
	}

	@Override
	public void removeEndProcess(Class<? extends EndProcess> endProcess) {
		getEndProcessManager().remove(endProcess);
	}

	@Override
	public void removeEndProcess(int index) {
		getEndProcessManager().remove(index);
	}

	@Override
	public int indexOfProcess(Class<? extends EndProcess> endProcess) {
		return getEndProcessManager().indexOf(endProcess);
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
			putScriptMap(getScriptType(), target, true);
			return true;
		}
		return false;
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
		putScriptMap(getScriptType(), scriptData.getLocation(), true);
	}

	@Override
	public void setScript(int index, String script) {
		scriptData.setScript(index, script);
	}

	@Override
	public void addScript(String script) {
		scriptData.addScript(script);
		putTimerMap(getScriptType(), scriptData.getLocation());
	}

	@Override
	public void addScript(int index, String script) {
		scriptData.addScript(index, script);
		putTimerMap(getScriptType(), scriptData.getLocation());
	}

	@Override
	public void removeScript(String script) {
		scriptData.removeScript(script);
		if (scriptData.getScripts().isEmpty()) {
			putScriptMap(getScriptType(), scriptData.getLocation(), false);
		} else {
			putTimerMap(getScriptType(), scriptData.getLocation());
		}
	}

	@Override
	public void clearScripts() {
		scriptData.clearScripts();
		putScriptMap(getScriptType(), scriptData.getLocation(), false);
	}

	@Override
	public void remove() {
		scriptData.remove();
		putScriptMap(getScriptType(), scriptData.getLocation(), false);
	}

	@Override
	public void reload() {
		scriptData.reload();
	}

	private void putTimerMap(ScriptType scriptType, Location location) {
		List<Location> value = timerTemps.get(scriptType);
		if (value == null) {
			value = new ArrayList<Location>();
		}
		value.add(location);
		timerTemps.put(scriptType, value);
	}

	private void putScriptMap(ScriptType scriptType, Location location, boolean isAdd) {
		Map<Location, ScriptType> value = scriptTemps.get(location);
		if (value == null) {
			value = new HashMap<Location, ScriptType>();
		}
		value.put(location, scriptType);
		scriptTemps.put(isAdd, value);
	}

	private boolean callEvent(Player player, ScriptType scriptType) {
		ScriptBlockEvent event = null;
		Block block = getLocation().getBlock();
		switch (scriptType) {
		case INTERACT:
			event = new ScriptBlockInteractEvent(player, block);
			break;
		case BREAK:
			event = new ScriptBlockBreakEvent(player, block);
			break;
		case WALK:
			event = new ScriptBlockWalkEvent(player, block);
			break;
		}
		if (event != null) {
			Bukkit.getPluginManager().callEvent(event);
			return event.isCancelled();
		}
		return false;
	}
}