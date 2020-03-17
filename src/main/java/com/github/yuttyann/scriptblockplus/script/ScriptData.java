package com.github.yuttyann.scriptblockplus.script;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.yaml.YamlConfig;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.apache.commons.lang.text.StrBuilder;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

	public ScriptData(@Nullable Location location, @NotNull ScriptType scriptType) {
		this(location, scriptType, false);
	}

	public ScriptData(@Nullable Location location, @NotNull ScriptType scriptType, boolean isUnmodifiableLocation) {
		setLocation(location);
		this.scriptType = scriptType;
		this.scriptFile = Files.getScriptFile(scriptType);
		this.isUnmodifiableLocation = isUnmodifiableLocation;
	}

	public void setLocation(@Nullable Location location) {
		if (isUnmodifiableLocation) {
			throw new UnsupportedOperationException();
		}
		this.location = location;
		this.scriptPath = location == null ? "" : createPath(location);
	}

	public void save() {
		scriptFile.save();
	}

	public boolean checkPath() {
		return scriptPath != null && scriptFile.contains(scriptPath);
	}

	@NotNull
	public String getScriptPath() {
		return scriptPath;
	}

	@Nullable
	public Location getLocation() {
		return location;
	}

	@NotNull
	public YamlConfig getScriptFile() {
		return scriptFile;
	}

	@NotNull
	public ScriptType getScriptType() {
		return scriptType;
	}

	@Nullable
	public String getAuthor() {
		return scriptFile.getString(scriptPath + KEY_AUTHOR);
	}

	@NotNull
	public List<String> getAuthors(boolean isMinecraftID) {
		String author = getAuthor();
		if (StringUtils.isEmpty(author)) {
			return new ArrayList<>();
		}
		String[] authors = StringUtils.split(author, ",");
		List<String> list = new ArrayList<>(authors.length);
		StreamUtils.forEach(authors, s -> list.add(isMinecraftID ? Utils.getName(UUID.fromString(s.trim())) : s.trim()));
		return list;
	}

	@Nullable
	public String getLastEdit() {
		return scriptFile.getString(scriptPath + KEY_LASTEDIT);
	}

	public int getAmount() {
		return scriptFile.getInt(scriptPath + KEY_AMOUNT, 0);
	}

	@NotNull
	public List<String> getScripts() {
		return scriptFile.getStringList(scriptPath + KEY_SCRIPTS);
	}

	public void setAuthor(@NotNull OfflinePlayer player) {
		setAuthor(player.getUniqueId());
	}

	public void setAuthor(@NotNull UUID uuid) {
		scriptFile.set(scriptPath + KEY_AUTHOR, uuid.toString());
	}

	public void setAuthor(@NotNull String author) {
		scriptFile.set(scriptPath + KEY_AUTHOR, author);
	}

	public void addAuthor(@NotNull OfflinePlayer player) {
		addAuthor(player.getUniqueId());
	}

	public void addAuthor(@NotNull UUID uuid) {
		List<String> authors = getAuthors(false);
		String uuidToString = uuid.toString();
		if (!authors.contains(uuidToString)) {
			String value = authors.size() > 0 ? getAuthor() + ", " + uuidToString : uuidToString;
			scriptFile.set(scriptPath + KEY_AUTHOR, value);
		}
	}

	public void removeAuthor(@NotNull OfflinePlayer player) {
		removeAuthor(player.getUniqueId());
	}

	public void removeAuthor(@NotNull UUID uuid) {
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

	public void setLastEdit(@NotNull String time) {
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

	public boolean copyScripts(@NotNull Location target, boolean overwrite) {
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

	public void setScripts(@NotNull List<String> scripts) {
		scriptFile.set(scriptPath + KEY_SCRIPTS, scripts);
	}

	public void setScript(int index, String script) {
		List<String> scripts = getScripts();
		scripts.set(index, script);
		setScripts(scripts);
	}

	public void addScript(@NotNull String script) {
		addScript(getScripts().size(), script);
	}

	public void addScript(int index, String script) {
		List<String> scripts = getScripts();
		scripts.add(index, script);
		setScripts(scripts);
	}

	public void removeScript(@NotNull String script) {
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

	private String createPath(@Nullable Location location) {
		return location.getWorld().getName() + "." + BlockCoords.getCoords(location);
	}
}