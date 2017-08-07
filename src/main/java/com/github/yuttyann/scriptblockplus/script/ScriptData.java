package com.github.yuttyann.scriptblockplus.script;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.YamlConfig;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class ScriptData implements Cloneable {

	private Location location;
	private String scriptPath;
	private ScriptType scriptType;
	private YamlConfig scriptFile;

	public ScriptData(Location location, ScriptType scriptType) {
		this.location = location;
		this.scriptType = scriptType;
		this.scriptFile = Files.getScriptFile(scriptType);
		this.scriptPath = location != null ? (location.getWorld().getName() + "." + BlockCoords.getCoords(location)) : null;
	}

	public void setLocation(Location location) {
		this.location = location;
		this.scriptPath = location.getWorld().getName() + "." + BlockCoords.getCoords(location);
	}

	public Location getLocation() {
		return location;
	}

	public ScriptType getScriptType() {
		return scriptType;
	}

	public YamlConfig getScriptFile() {
		return scriptFile;
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
		String[] authors = getAuthor().split(",");
		List<String> list = new ArrayList<String>();
		for (String author : authors) {
			author = author.trim();
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
			for (int i = 0; i < authors.size(); i++) {
				builder.append(authors.get(i));
				if (i != (authors.size() - 1)) {
					builder.append(", ");
				}
			}
			scriptFile.set(scriptPath + ".Author", builder.toString());
		}
	}

	public void setLastEdit() {
		setLastEdit(Utils.getDateFormat("yyyy/MM/dd HH:mm:ss"));
	}

	public void setLastEdit(String time) {
		scriptFile.set(scriptPath + ".LastEdit", time);
	}

	public void addAmount(int amount) {
		scriptFile.set(scriptPath + ".Amount", getAmount() + amount);
	}

	public void subtractAmount(int amount) {
		scriptFile.set(scriptPath + ".Amount", getAmount() - amount);
	}

	public void copyScripts(Location target, boolean overwrite) {
		ScriptData targetData = new ScriptData(target, scriptType);
		if (location.equals(target) || !checkPath() || (targetData.checkPath() && overwrite)) {
			return;
		}
		targetData.setAuthor(getAuthor());
		targetData.setLastEdit(getLastEdit());
		targetData.setScripts(getScripts());
		targetData.save();
	}

	public void setScripts(List<String> scripts) {
		scriptFile.set(scriptPath + ".Scripts", scripts);
	}

	public void setScript(int index, String script) {
		List<String> scripts = getScripts();
		scripts.set(index, script);
		setScripts(scripts);
	}

	public void addScript(String script) {
		addScript(getScripts().size(), script);
	}

	public void addScript(int index, String script) {
		List<String> scripts = getScripts();
		scripts.add(index, script);
		setScripts(scripts);
	}

	public void removeScript(String script) {
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
		ScriptBlock plugin = ScriptBlock.getInstance();
		plugin.getMapManager().loadScripts(scriptFile, scriptType);
	}

	@Override
	public ScriptData clone() {
		try {
			return (ScriptData) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new Error(e);
		}
	}
}