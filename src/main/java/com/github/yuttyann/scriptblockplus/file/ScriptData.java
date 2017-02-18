package com.github.yuttyann.scriptblockplus.file;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.BlockLocation;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.manager.MapManager;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class ScriptData {

	private ScriptBlock plugin;
	private MapManager mapManager;
	private YamlConfig scriptFile;
	private BlockLocation location;
	private ScriptType scriptType;
	private String scriptPath;

	public ScriptData(ScriptBlock plugin, BlockLocation location, ScriptType scriptType) {
		this.plugin = plugin;
		this.mapManager = plugin.getMapManager();
		this.scriptFile = Files.getScriptFile(scriptType);
		this.location = location;
		this.scriptType = scriptType;
		this.scriptPath = location != null ? (location.getWorld().getName() + "." + location.getCoords()) : null;
	}

	public void setBlockLocation(BlockLocation location) {
		this.location = location;
		this.scriptPath = location.getWorld().getName() + "." + location.getCoords();
	}

	public YamlConfig getScriptFile() {
		return scriptFile;
	}

	public BlockLocation getBlockLocation() {
		return location;
	}

	public ScriptType getScriptType() {
		return scriptType;
	}

	public boolean checkPath() {
		return scriptFile.contains(scriptPath);
	}

	public void save() {
		scriptFile.save();
	}

	public String getAuthor() {
		return scriptFile.getString(scriptPath + ".Author");
	}

	public List<String> getAuthors(boolean isName) {
		String[] authors = getAuthor().split(", ");
		List<String> list = new ArrayList<String>();
		for (String author : authors) {
			list.add(isName ? Utils.getName(author) : author);
		}
		return list;
	}

	public String getLastEdit() {
		return scriptFile.getString(scriptPath + ".LastEdit");
	}

	public int getAmount() {
		return scriptFile.getInt(scriptPath + ".Amount", 0);
	}

	public List<String> getScripts() {
		return scriptFile.getStringList(scriptPath + ".Scripts");
	}

	public void setAuthor(Player player) {
		scriptFile.set(scriptPath + ".Author", player.getUniqueId().toString());
	}

	public void setAuthor(String uuids) {
		scriptFile.set(scriptPath + ".Author", uuids);
	}

	public void addAuthor(Player player) {
		String uuid = player.getUniqueId().toString();
		List<String> authors = getAuthors(false);
		if (authors.size() > 0 && !authors.contains(uuid)) {
			scriptFile.set(scriptPath + ".Author", getAuthor() + ", " + player.getUniqueId().toString());
		}
	}

	public void removeAuthor(Player player) {
		String uuid = player.getUniqueId().toString();
		List<String> authors = getAuthors(false);
		if (authors.size() > 0 && authors.contains(uuid)) {
			authors.remove(uuid);
			StringBuilder builder = new StringBuilder();
			for (int i = 0, s = authors.size(); i < s; i++) {
				builder.append(authors.get(i));
				if (i != (s - 1)) {
					builder.append(", ");
				}
			}
			scriptFile.set(scriptPath + ".Author", builder.toString());
		}
	}

	public void setLastEdit() {
		setLastEdit(Utils.getDateFormat("yyyy/MM/dd HH:mm:ss"));
	}

	public void setLastEdit(String date) {
		scriptFile.set(scriptPath + ".LastEdit", date);
	}

	public void addAmount(int amount) {
		scriptFile.set(scriptPath + ".Amount", getAmount() + amount);
	}

	public void subtractAmount(int amount) {
		scriptFile.set(scriptPath + ".Amount", getAmount() - amount);
	}

	public void moveScripts(BlockLocation target, boolean overwrite) {
		BlockLocation targetLocation = target;
		ScriptData targetData = new ScriptData(plugin, targetLocation, getScriptType());
		if (location.equals(targetLocation)
				|| !checkPath() || (targetData.checkPath() && overwrite)) {
			return;
		}
		mapManager.addLocation(targetLocation, getScriptType());
		mapManager.removeLocation(location, scriptType);
		targetData.setAuthor(getAuthor());
		targetData.setLastEdit(getLastEdit());
		targetData.setScripts(getScripts());
		targetData.save();
		remove();
		save();
		setBlockLocation(targetLocation);
	}

	public void setScripts(List<String> scripts) {
		scriptFile.set(scriptPath + ".Scripts", scripts);
	}

	public void setCreateScripts(String script) {
		List<String> scripts = getScripts();
		try {
			scripts.set(0, script);
		} catch (IndexOutOfBoundsException e) {
			scripts.add(0, script);
		}
		scriptFile.set(scriptPath + ".Scripts", scripts);
	}

	public void addScripts(String script) {
		List<String> scripts = getScripts();
		scripts.add(script);
		scriptFile.set(scriptPath + ".Scripts", scripts);
	}

	public void removeScripts(String script) {
		List<String> scripts = getScripts();
		scripts.remove(script);
		scriptFile.set(scriptPath + ".Scripts", scripts);
	}

	public void clearScripts() {
		scriptFile.set(scriptPath + ".Scripts", null);
	}

	public void remove() {
		scriptFile.set(scriptPath, null);
	}

	public void reload() {
		mapManager.reloadScripts(scriptFile, scriptType);
	}
}