package com.github.yuttyann.scriptblockplus.script;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.yaml.YamlConfig;
import com.github.yuttyann.scriptblockplus.player.PlayerCount;
import com.github.yuttyann.scriptblockplus.player.PlayerCountInfo;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * ScriptBlockPlus ScriptData クラス
 * @author yuttyann44581
 */
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

	public ScriptData(@NotNull ScriptType scriptType) {
		this.scriptType = scriptType;
		this.scriptFile = Files.getScriptFile(scriptType);
		this.isUnmodifiableLocation = false;
	}

	public ScriptData(@NotNull Location location, @NotNull ScriptType scriptType) {
		this(location, scriptType, false);
	}

	public ScriptData(@NotNull Location location, @NotNull ScriptType scriptType, boolean isUnmodifiableLocation) {
		setLocation(location);
		this.scriptType = scriptType;
		this.scriptFile = Files.getScriptFile(scriptType);
		this.isUnmodifiableLocation = isUnmodifiableLocation;
	}

	public void setLocation(@NotNull Location location) {
		if (isUnmodifiableLocation) {
			throw new UnsupportedOperationException();
		}
		this.location = location;
		this.scriptPath = createPath(location);
	}

	public void save() {
		scriptFile.save();
	}

	public boolean hasPath() {
		return scriptPath != null && scriptFile.contains(scriptPath);
	}

	@NotNull
	public String getPath() {
		return scriptPath;
	}

	@NotNull
	public Location getLocation() {
		return Objects.requireNonNull(location, "Location cannot be null");
	}

	@NotNull
	public YamlConfig getScriptFile() {
		return scriptFile;
	}

	@NotNull
	public ScriptType getScriptType() {
		return scriptType;
	}

	@NotNull
	public String getAuthor() {
		return Objects.toString(scriptFile.getString(scriptPath + KEY_AUTHOR), "null");
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

	@NotNull
	public String getLastEdit() {
		return Objects.toString(scriptFile.getString(scriptPath + KEY_LASTEDIT), "null");
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
		String id = uuid.toString();
		if (!authors.contains(id)) {
			scriptFile.set(scriptPath + KEY_AUTHOR, authors.size() > 0 ? getAuthor() + ", " + id : id);
		}
	}

	public void removeAuthor(@NotNull OfflinePlayer player) {
		removeAuthor(player.getUniqueId());
	}

	public void removeAuthor(@NotNull UUID uuid) {
		List<String> authors = getAuthors(false);
		String id = uuid.toString();
		if (authors.size() > 0 && authors.contains(id)) {
			authors.remove(id);
			scriptFile.set(scriptPath + KEY_AUTHOR, String.join(", ", authors));
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
		scriptFile.set(scriptPath + KEY_AMOUNT, Math.max(result, 0));
	}

	public boolean copyScripts(@NotNull Location target, boolean overwrite) {
		ScriptData targetData = new ScriptData(target, scriptType);
		if (location.equals(target) || !hasPath() || (targetData.hasPath() && !overwrite)) {
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

	public void initCount(BlockCoords blockCoords) {
		for (OfflinePlayer player : Utils.getAllPlayers()) {
			PlayerCount playerCount = SBPlayer.fromPlayer(player).getPlayerCount();
			PlayerCountInfo playerCountInfo = playerCount.getInfo(blockCoords, scriptType);
			if (playerCountInfo.getAmount() > 0) {
				playerCount.remove(playerCountInfo);
			}
		}
	}

	public void reload() {
		ScriptBlock.getInstance().getMapManager().loadScripts(scriptFile, scriptType);
	}

	@Override
	@NotNull
	public ScriptData clone() {
		try {
			ScriptData scriptData = (ScriptData) super.clone();
			if (this.location != null) {
				scriptData.location = this.location.clone();
			}
			scriptData.scriptPath = this.scriptPath;
			scriptData.scriptFile = this.scriptFile;
			scriptData.scriptType = this.scriptType;
			scriptData.isUnmodifiableLocation = this.isUnmodifiableLocation;
			return scriptData;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return new ScriptData(location, scriptType, isUnmodifiableLocation);
		}
	}

	private String createPath(@NotNull Location location) {
		return Objects.requireNonNull(location.getWorld()).getName() + "." + BlockCoords.getCoords(location);
	}
}