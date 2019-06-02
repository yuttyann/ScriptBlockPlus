package com.github.yuttyann.scriptblockplus.script;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.text.StrBuilder;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.yaml.YamlConfig;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public final class ScriptData implements Cloneable {

	// Keys
	private static final String KEY_AUTHOR = ".Author";
	private static final String KEY_AMOUNT = ".Amount";
	private static final String KEY_SCRIPTS = ".Scripts";
	private static final String KEY_LASTEDIT = ".LastEdit";

	private String scriptPath;
	private Location location;
	private YamlConfig scriptFile;
	private ScriptType scriptType;
	private boolean isUnmodifiableLocation;

	private ScriptData() {}

	public ScriptData(Location location, ScriptType scriptType) {
		this(location, scriptType, false);
	}

	public ScriptData(Location location, ScriptType scriptType, boolean isUnmodifiableLocation) {
		setLocation(location);
		this.scriptType = scriptType;
		this.scriptFile = Files.getScriptFile(scriptType);
		this.isUnmodifiableLocation = isUnmodifiableLocation;
	}

	public void setLocation(Location location) {
		if (isUnmodifiableLocation) {
			throw new UnsupportedOperationException();
		}
		this.location = location;
		this.scriptPath = location == null ? null : createPath(location);
	}

	public void save() {
		scriptFile.save();
	}

	public boolean checkPath() {
		return scriptPath != null && scriptFile.contains(scriptPath);
	}

	public Location getLocation() {
		return location;
	}

	public YamlConfig getScriptFile() {
		return scriptFile;
	}

	public ScriptType getScriptType() {
		return scriptType;
	}

	public String getAuthor() {
		return scriptFile.getString(scriptPath + KEY_AUTHOR);
	}

	public List<String> getAuthors(boolean isName) {
		String author = getAuthor();
		if (StringUtils.isEmpty(author)) {
			return new ArrayList<>();
		}
		String[] authors = StringUtils.split(author, ",");
		List<String> list = new ArrayList<>(authors.length);
		StreamUtils.forEach(authors, s -> list.add(isName ? Utils.getName(UUID.fromString(s.trim())) : s.trim()));
		return list;
	}

	public String getLastEdit() {
		return scriptFile.getString(scriptPath + KEY_LASTEDIT);
	}

	public int getAmount() {
		return scriptFile.getInt(scriptPath + KEY_AMOUNT, 0);
	}

	public List<String> getScripts() {
		return scriptFile.getStringList(scriptPath + KEY_SCRIPTS);
	}

	public void setAuthor(OfflinePlayer player) {
		setAuthor(player.getUniqueId());
	}

	public void setAuthor(UUID uuid) {
		scriptFile.set(scriptPath + KEY_AUTHOR, uuid.toString());
	}

	public void setAuthor(String author) {
		scriptFile.set(scriptPath + KEY_AUTHOR, author);
	}

	public void addAuthor(OfflinePlayer player) {
		addAuthor(player.getUniqueId());
	}

	public void addAuthor(UUID uuid) {
		List<String> authors = getAuthors(false);
		String uuidToString = uuid.toString();
		if (!authors.contains(uuidToString)) {
			String value = authors.size() > 0 ? getAuthor() + ", " + uuidToString : uuidToString;
			scriptFile.set(scriptPath + KEY_AUTHOR, value);
		}
	}

	public void removeAuthor(OfflinePlayer player) {
		removeAuthor(player.getUniqueId());
	}

	public void removeAuthor(UUID uuid) {
		List<String> authors = getAuthors(false);
		String uuidToString = uuid.toString();
		if (authors.size() > 0 && authors.contains(uuidToString)) {
			authors.remove(uuidToString);
			StrBuilder builder = new StrBuilder();
			for (int i = 0; i < authors.size(); i++) {
				builder.append(authors.get(i)).append(i == (authors.size() - 1) ? "" : ", ");
			}
			scriptFile.set(scriptPath + KEY_AUTHOR, builder.toString());
		}
	}

	public void setLastEdit() {
		setLastEdit(Utils.getFormatTime());
	}

	public void setLastEdit(String time) {
		scriptFile.set(scriptPath + KEY_LASTEDIT, time);
	}

	public void setAmount(int amount) {
		scriptFile.set(scriptPath + KEY_AMOUNT, amount);
	}

	public void addAmount(int amount) {
		scriptFile.set(scriptPath + KEY_AMOUNT, getAmount() + amount);
	}

	public void subtractAmount(int amount) {
		int result = getAmount() - amount;
		scriptFile.set(scriptPath + KEY_AMOUNT, result >= 0 ? result : 0);
	}

	public boolean copyScripts(Location target, boolean overwrite) {
		ScriptData targetData = new ScriptData(target, scriptType);
		if (location.equals(target) || !checkPath() || (targetData.checkPath() && !overwrite)) {
			return false;
		}
		targetData.setAuthor(getAuthor());
		targetData.setLastEdit(getLastEdit());
		targetData.setAmount(getAmount());
		targetData.setScripts(getScripts());
		targetData.save();
		return true;
	}

	public void setScripts(List<String> scripts) {
		scriptFile.set(scriptPath + KEY_SCRIPTS, scripts);
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
		scriptFile.set(scriptPath + KEY_SCRIPTS, scripts);
	}

	public void clearScripts() {
		scriptFile.set(scriptPath + KEY_SCRIPTS, null);
	}

	public void remove() {
		scriptFile.set(scriptPath, null);
	}

	public void reload() {
		ScriptBlock.getInstance().getMapManager().loadScripts(scriptFile, scriptType);
	}

	public ScriptData clone() {
		ScriptData scriptData = new ScriptData();
		if (this.location != null) {
			scriptData.location = this.location.clone();
		}
		scriptData.scriptPath = this.scriptPath;
		scriptData.scriptFile = this.scriptFile;
		scriptData.scriptType = this.scriptType;
		scriptData.isUnmodifiableLocation = this.isUnmodifiableLocation;
		return scriptData;
	}

	private String createPath(Location location) {
		return location.getWorld().getName() + "." + BlockCoords.getCoords(location);
	}
}