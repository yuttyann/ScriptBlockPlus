package com.github.yuttyann.scriptblockplus.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.bukkit.Color;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import com.github.yuttyann.scriptblockplus.utils.FileUtils;
import com.github.yuttyann.scriptblockplus.utils.ReflectionUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class YamlConfig {

	private Plugin plugin;
	private String fileName;
	private File file;
	private YamlConfiguration yaml;

	protected YamlConfig(Plugin plugin, File file, boolean fileCreate) {
		Validate.notNull(file, "File cannot be null");
		this.plugin = plugin;
		this.file = file;
		this.fileName = file.getName();
		if (fileCreate && !file.exists()) {
			FileUtils.copyFileFromJar(getJarFile(), file, fileName);
		}
		try {
			this.yaml = new YamlConfiguration();
			this.yaml.load(file);
		} catch (FileNotFoundException e) {
		} catch (IOException | InvalidConfigurationException e) {
			FileUtils.fileEncode(plugin, file);
			this.yaml = YamlConfiguration.loadConfiguration(file);
		}
	}

	public static YamlConfig load(Plugin plugin, String pathName) {
		return load(plugin, pathName, true);
	}

	public static YamlConfig load(Plugin plugin, String pathName, boolean fileCreate) {
		return load(plugin, new File(plugin.getDataFolder(), pathName), fileCreate);
	}

	public static YamlConfig load(Plugin plugin, File file) {
		return load(plugin, file, true);
	}

	public static YamlConfig load(Plugin plugin, File file, boolean fileCreate) {
		return new YamlConfig(plugin, file, fileCreate);
	}

	public File getFile() {
		return file;
	}

	public boolean exists() {
		return file.exists();
	}

	public File getJarFile() {
		try {
			return (File) ReflectionUtils.invokeMethod((JavaPlugin) plugin, JavaPlugin.class, "getFile");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return null;
	}

	public File getDataFolder() {
		return plugin.getDataFolder();
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

	public UUID getUUID(String path) {
		Object def = getDefault(path);
		return getUUID(path, def != null ? Utils.fromString(def.toString()) : null);
	}

	public UUID getUUID(String path, UUID def) {
		Object val = get(path, def);
		return val != null ? Utils.fromString(val.toString()) : def;
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

	public boolean isUUID(String path) {
		Object val = get(path);
		return Utils.isUUID(val.toString());
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

	public boolean isFloat(String path) {
		Object val = get(path);
		return val instanceof Float;
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

	public float getFloat(String path) {
		Object def = getDefault(path);
		return getFloat(path, def instanceof Number ? NumberConversions.toFloat(def) : 0.0F);
	}

	public float getFloat(String path, float def) {
		Object val = get(path, def);
		return val instanceof Number ? NumberConversions.toFloat(val) : def;
	}

	public long getLong(String path) {
		return yaml.getLong(path);
	}

	public long getLong(String path, long def) {
		return yaml.getLong(path, def);
	}

	public Set<String> getKeys(boolean deep) {
		return yaml.getKeys(deep);
	}

	public Set<String> getKeys(String path, boolean deep) {
		return yaml.getConfigurationSection(path).getKeys(deep);
	}

	public Map<String, Object> getValues(boolean deep) {
		return yaml.getValues(deep);
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

	public List<UUID> getUUIDList(String path) {
		List<?> list = getList(path);
		if (list == null) {
			return new ArrayList<UUID>(0);
		}
		List<UUID> result = new ArrayList<UUID>();
		for (Object object : list) {
			if (object instanceof String || isPrimitiveWrapper(object)) {
				result.add(Utils.fromString(String.valueOf(object)));
			}
		}
		return result;
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

	private Object getDefault(String path) {
		Validate.notNull(path, "Path cannot be null");
		Configuration root = getRoot();
		Configuration defaults = (root != null ? root.getDefaults() : null);
		return defaults != null ? defaults.get(MemorySection.createPath(yaml, path)) : null;
	}

	private boolean isPrimitiveWrapper(Object input) {
		return input instanceof Integer || input instanceof Boolean
				|| input instanceof Character || input instanceof Byte
				|| input instanceof Short || input instanceof Double
				|| input instanceof Long || input instanceof Float;
	}
}