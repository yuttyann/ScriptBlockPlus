package com.github.yuttyann.scriptblockplus.file;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.event.FileReloadEvent;
import com.github.yuttyann.scriptblockplus.file.config.ConfigKeys;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.yaml.UTF8Config;
import com.github.yuttyann.scriptblockplus.file.yaml.YamlConfig;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.script.option.time.TimerOption;
import com.github.yuttyann.scriptblockplus.utils.FileUtils;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.google.common.base.Charsets;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * ScriptBlockPlus Files クラス
 * @author yuttyann44581
 */
public final class Files {

	private static final Map<String, YamlConfig> FILES = new HashMap<>();
	private static final Map<ScriptType, Set<String>> SCRIPT_COORDS = new HashMap<>();

	public static final String S = File.separator;
	public static final String PATH_CONFIG = "config.yml";
	public static final String PATH_LANGS = "langs" + S + "{code}.yml";

	public static void reload() {
		Plugin plugin = ScriptBlock.getInstance();

		ConfigKeys.clear();
		ConfigKeys.load(loadFile(plugin, PATH_CONFIG, true));
		ConfigKeys.load(loadLang(plugin, PATH_LANGS));

		StreamUtils.forEach(ScriptType.values(), Files::loadScript);
		searchKeys(plugin, PATH_CONFIG, PATH_LANGS);

		Bukkit.getPluginManager().callEvent(new FileReloadEvent());
	}

	public static void searchKeys(@NotNull Plugin plugin, @NotNull String... paths) {
		for (String path : paths) {
			Optional<YamlConfig> yaml = getFile(plugin, path);
			if (yaml.isPresent() && yaml.get().exists()) {
				Optional<String> filePath = Optional.ofNullable(yaml.get().getInnerPath());
				sendNotKeyMessages(plugin, yaml.get(), filePath.orElse(yaml.get().getFileName()));
			}
		}
	}

	public static void sendNotKeyMessages(@NotNull Plugin plugin, @NotNull YamlConfig yaml, @NotNull String path) {
		InputStream is = FileUtils.getResource(plugin, path);
		if (is == null) {
			return;
		}
		YamlConfiguration config = UTF8Config.loadConfiguration(new InputStreamReader(is, Charsets.UTF_8));
		String filePath = plugin.getName() + "/" + StringUtils.replace(yaml.getFolderPath(), S, "/");
		Set<String> keys = yaml.getKeys(true);
		for (String key : config.getKeys(true)) {
			if (!keys.contains(key)) {
				Object value = config.get(key) instanceof MemorySection ? "" : config.get(key);
				Bukkit.getConsoleSender().sendMessage("§c[" + filePath + "] Key not found: §r" + key + ": " + value);
			}
		}
	}

	@NotNull
	public static Map<String, YamlConfig> getFiles() {
		return Collections.unmodifiableMap(FILES);
	}

	public static Optional<YamlConfig> getFile(@NotNull Plugin plugin, @NotNull String filePath) {
		return Optional.ofNullable(FILES.get(plugin.getName() + "_" + filePath));
	}

	@NotNull
	public static YamlConfig getScriptFile(@NotNull ScriptType scriptType) {
		String filePath = "scripts" + S + scriptType.type() + ".yml";
		return getFile(ScriptBlock.getInstance(), filePath).orElseGet(() -> loadScript(scriptType));
	}

	public static void addScriptCoords(@NotNull Location location, @NotNull ScriptType scriptType) {
		String fullCoords = BlockCoords.getFullCoords(location);
		Optional<Set<String>> value = Optional.ofNullable(SCRIPT_COORDS.get(scriptType));
		value.ifPresent(v -> v.add(fullCoords));
		TimerOption.removeAll(fullCoords, scriptType);
	}

	public static void removeScriptCoords(@NotNull Location location, @NotNull ScriptType scriptType) {
		String fullCoords = BlockCoords.getFullCoords(location);
		Optional.ofNullable(SCRIPT_COORDS.get(scriptType)).ifPresent(v -> v.remove(fullCoords));
		TimerOption.removeAll(fullCoords, scriptType);
	}

	public static boolean hasScriptCoords(@NotNull Location location, @NotNull ScriptType scriptType) {
		Optional<Set<String>> value = Optional.ofNullable(SCRIPT_COORDS.get(scriptType));
		return value.isPresent() && value.get().contains(BlockCoords.getFullCoords(location));
	}

	public static void loadAllScripts() {
		try {
			StreamUtils.forEach(ScriptType.values(), s -> loadScripts(Files.getScriptFile(s), s));
		} catch (Exception e) {
			SCRIPT_COORDS.clear();
		}
	}

	public static void loadScripts(@NotNull YamlConfig scriptFile, @NotNull ScriptType scriptType) {
		Set<String> set = new HashSet<>(SCRIPT_COORDS.size());
		scriptFile.getKeys().forEach(w -> scriptFile.getKeys(w).forEach(c -> set.add(w + ", " + c)));
		SCRIPT_COORDS.put(scriptType, set);
	}

	@NotNull
	public static YamlConfig loadFile(@NotNull Plugin plugin, @NotNull String filePath, boolean isCopyFile) {
		return putFile(plugin, filePath, YamlConfig.load(plugin, filePath, isCopyFile));
	}

	@NotNull
	public static YamlConfig loadLang(@NotNull Plugin plugin, @NotNull String filePath) {
		String language = SBConfig.LANGUAGE.getValue();
		if (StringUtils.isEmpty(language) || "default".equalsIgnoreCase(language)) {
			language = Locale.getDefault().getLanguage();
		}
		return putFile(plugin, filePath, new Lang(plugin, language, filePath, "lang").load());
	}

	@NotNull
	private static YamlConfig loadScript(@NotNull ScriptType scriptType) {
		String filePath = "scripts" + S + scriptType.type() + ".yml";
		return loadFile(ScriptBlock.getInstance(), filePath, false);
	}

	@NotNull
	private static YamlConfig putFile(@NotNull Plugin plugin, @NotNull String name, @NotNull YamlConfig yaml) {
		FILES.put(plugin.getName() + "_" + name, yaml);
		return yaml;
	}
}