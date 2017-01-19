package com.github.yuttyann.scriptblockplus.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Color;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.manager.FileManager;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import com.google.common.base.Charsets;

public class Yaml extends FileManager {

	private String fileName;
	private File file;
	private YamlConfiguration yaml;

	public Yaml(String fileName) {
		this(fileName, true);
	}

	public Yaml(String fileName, boolean isCreate) {
		File data = ScriptBlock.instance.getDataFolder();
		this.file = new File(data, fileName + ".yml");
		this.fileName = file.getName();
		if (isCreate && !file.exists()) {
			copyFileFromJar(ScriptBlock.instance.getJarFile(), file, this.fileName);
		}
		fileEncode(file);
		this.yaml = YamlConfiguration.loadConfiguration(file);
	}

	@SuppressWarnings("deprecation")
	public void reload() {
		yaml = YamlConfiguration.loadConfiguration(file);
		InputStream defConfigStream = ScriptBlock.instance.getResource(fileName);
		if (defConfigStream != null) {
			YamlConfiguration defConfig;
			if(!Utils.isWindows() || Utils.isCB19orLater()) {
				defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8));
			} else {
				defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			}
			yaml.setDefaults(defConfig);
		}
	}

	public File getFile() {
		return file;
	}

	public String getFileName() {
		return fileName;
	}

	public YamlConfiguration getYamlConfiguration() {
		return yaml;
	}

	public void save() {
		try {
			yaml.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void save(File file) {
		try {
			yaml.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void save(String file) {
		try {
			yaml.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addDefault(String path, Object value) {
		yaml.addDefault(path, value);
	}

	public void addDefault(Configuration defaults) {
		yaml.addDefaults(defaults);
	}

	public void addDefault(Map<String, Object> defaults) {
		yaml.addDefaults(defaults);
	}

	public void set(String path, Object value) {
		yaml.set(path, value);
	}

	public void setDefaults(Configuration defaults) {
		yaml.setDefaults(defaults);
	}

	public ConfigurationSection createSection(String path) {
		return yaml.createSection(path);
	}

	public ConfigurationSection createSection(String path, Map<?, ?> map) {
		return yaml.createSection(path, map);
	}

	public ConfigurationSection getConfigurationSection(String path) {
		return yaml.getConfigurationSection(path);
	}

	public Configuration getDefaults() {
		return yaml.getDefaults();
	}

	public ConfigurationSection getDefaultSection() {
		return yaml.getDefaultSection();
	}

	public ConfigurationSection getParent() {
		return yaml.getParent();
	}

	public Configuration getRoot() {
		return yaml.getRoot();
	}

	public String getCurrentPath() {
		return yaml.getCurrentPath();
	}

	public String getName() {
		return yaml.getName();
	}

	public String getString(String path) {
		return yaml.getString(path);
	}

	public String getString(String path, String def) {
		return yaml.getString(path, def);
	}

	public String saveToString() {
		return yaml.saveToString();
	}

	public Object get(String path) {
		return yaml.get(path);
	}

	public Object get(String path, Object def) {
		return yaml.get(path, def);
	}

	public Color getColor(String path) {
		return yaml.getColor(path);
	}

	public Color getColor(String path, Color def) {
		return yaml.getColor(path, def);
	}

	public ItemStack getItemStack(String path) {
		return yaml.getItemStack(path);
	}

	public ItemStack getItemStack(String path, ItemStack def) {
		return yaml.getItemStack(path, def);
	}

	public Vector getVector(String path) {
		return yaml.getVector(path);
	}

	public Vector getVector(String path, Vector def) {
		return yaml.getVector(path, def);
	}

	public FileConfigurationOptions options(String path) {
		return yaml.options();
	}

	public boolean contains(String path) {
		return yaml.contains(path);
	}

	public boolean getBoolean(String path) {
		return yaml.getBoolean(path);
	}

	public boolean getBoolean(String path, boolean def) {
		return yaml.getBoolean(path, def);
	}

	public boolean isString(String path) {
		return yaml.isString(path);
	}

	public boolean isColor(String path) {
		return yaml.isColor(path);
	}

	public boolean isItemStack(String path) {
		return yaml.isItemStack(path);
	}

	public boolean isVector(String path) {
		return yaml.isVector(path);
	}

	public boolean isBoolean(String path) {
		return yaml.isBoolean(path);
	}

	public boolean isOfflinePlayer(String path) {
		return yaml.isOfflinePlayer(path);
	}

	public boolean isConfigurationSection(String path) {
		return yaml.isConfigurationSection(path);
	}

	public boolean isInt(String path) {
		return yaml.isInt(path);
	}

	public boolean isDouble(String path) {
		return yaml.isDouble(path);
	}

	public boolean isLong(String path) {
		return yaml.isLong(path);
	}

	public boolean isSet(String path) {
		return yaml.isSet(path);
	}

	public boolean isList(String path) {
		return yaml.isList(path);
	}

	public int getInt(String path) {
		return yaml.getInt(path);
	}

	public int getInt(String path, int def) {
		return yaml.getInt(path, def);
	}

	public double getDouble(String path) {
		return yaml.getDouble(path);
	}

	public double getDouble(String path, double def) {
		return yaml.getDouble(path, def);
	}

	public long getLong(String path) {
		return yaml.getLong(path);
	}

	public long getLong(String path, long def) {
		return yaml.getLong(path, def);
	}

	public Map<String, Object> getValues(boolean deep) {
		return yaml.getValues(deep);
	}

	public Set<String> getKeys(boolean deep) {
		return yaml.getKeys(deep);
	}

	public List<?> getList(String path) {
		return yaml.getList(path);
	}

	public List<?> getList(String path, List<?> def) {
		return yaml.getList(path, def);
	}

	public List<Map<?, ?>> getMapList(String path) {
		return yaml.getMapList(path);
	}

	public List<String> getStringList(String path) {
		return yaml.getStringList(path);
	}

	public List<Boolean> getBooleanList(String path) {
		return yaml.getBooleanList(path);
	}

	public List<Character> getCharacterList(String path) {
		return yaml.getCharacterList(path);
	}

	public List<Integer> getIntegerList(String path) {
		return yaml.getIntegerList(path);
	}

	public List<Double> getDoubleList(String path) {
		return yaml.getDoubleList(path);
	}

	public List<Float> getFloatList(String path) {
		return yaml.getFloatList(path);
	}

	public List<Long> getLongList(String path) {
		return yaml.getLongList(path);
	}

	public List<Short> getShortList(String path) {
		return yaml.getShortList(path);
	}

	public List<Byte> getByteList(String path) {
		return yaml.getByteList(path);
	}
}