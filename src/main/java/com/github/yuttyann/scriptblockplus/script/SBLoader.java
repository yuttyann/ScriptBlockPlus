package com.github.yuttyann.scriptblockplus.script;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.file.SBFiles;
import com.github.yuttyann.scriptblockplus.file.config.YamlConfig;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * ScriptBlockPlus ScriptLoader クラス
 * @author yuttyann44581
 */
public final class SBLoader {

	private static final String KEY_AUTHOR = ".Author";
	private static final String KEY_AMOUNT = ".Amount";
	private static final String KEY_SCRIPTS = ".Scripts";
	private static final String KEY_LASTEDIT = ".LastEdit";

	private String path;
	private Location location;
	private YamlConfig scriptFile;

	public SBLoader(@NotNull ScriptType scriptType) {
		String filePath = "scripts" + SBFiles.S + scriptType.type() + ".yml";
		this.scriptFile = YamlConfig.load(ScriptBlock.getInstance(), filePath, false);
	}

	public void forEach(@NotNull Consumer<SBLoader> action) {
		for (String name : scriptFile.getKeys()) {
			World world = Utils.getWorld(name);
			for (String xyz : scriptFile.getKeys(name)) {
				action.accept(createPath(BlockCoords.fromString(world, xyz)));
			}
		}
	}

	@NotNull
	public File getFile() {
		return scriptFile.getFile();
	}

	@NotNull
	public Location getLocation() {
		return location;
	}

	@NotNull
	public List<UUID> getAuthors() {
		String author = scriptFile.getString(path + KEY_AUTHOR, "");
		if (StringUtils.isEmpty(author)) {
			return new ArrayList<>();
		}
		String[] authors = StringUtils.split(author, ",");
		List<UUID> list = new ArrayList<>(authors.length);
		StreamUtils.forEach(authors, s -> list.add(UUID.fromString(s.trim())));
		return list;
	}

	@NotNull
	public List<String> getScripts() {
		return scriptFile.getStringList(path + KEY_SCRIPTS);
	}

	@NotNull
	public String getLastEdit() {
		return scriptFile.getString(path + KEY_LASTEDIT, "null");
	}

	public int getAmount() {
		return scriptFile.getInt(path + KEY_AMOUNT, -1);
	}

	private SBLoader createPath(@NotNull Location location) {
		this.location = location;
		this.path = Objects.requireNonNull(location.getWorld()).getName() + "." + BlockCoords.getCoords(location);
		return this;
	}
}