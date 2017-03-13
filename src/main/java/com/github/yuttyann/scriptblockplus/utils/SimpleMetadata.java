package com.github.yuttyann.scriptblockplus.utils;

import java.util.List;

import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;

import com.github.yuttyann.scriptblockplus.file.PluginYaml;

public class SimpleMetadata {

	private Plugin plugin;

	public SimpleMetadata(Plugin plugin) {
		this.plugin = plugin;
	}

	public void set(Metadatable metadatable, String key, Object value) {
		metadatable.setMetadata(key, new FixedMetadataValue(plugin, value));
	}

	public void remove(Metadatable metadatable, String key) {
		metadatable.removeMetadata(key, plugin);
	}

	public boolean has(Metadatable metadatable, String key) {
		return metadatable.hasMetadata(key);
	}

	public Object get(Metadatable metadatable, String key, Object defaultValue) {
		List<MetadataValue> values = metadatable.getMetadata(key);
		for (MetadataValue value : values) {
			if (value.value() != null && value.getOwningPlugin().getName().equals(PluginYaml.getName())) {
				return value.value();
			}
		}
		return defaultValue;
	}

	public String getString(Metadatable metadatable, String key, String defaultValue) {
		Object value = get(metadatable, key, defaultValue);
		return value instanceof String ? (String) value : null;
	}

	public boolean getBoolean(Metadatable metadatable, String key, boolean defaultValue) {
		Object value = get(metadatable, key, defaultValue);
		return value instanceof Boolean ? (boolean) value : false;
	}

	public byte getByte(Metadatable metadatable, String key, byte defaultValue) {
		Object value = get(metadatable, key, defaultValue);
		return value instanceof Byte ? (byte) value : 0;
	}

	public short getShort(Metadatable metadatable, String key, short defaultValue) {
		Object value = get(metadatable, key, defaultValue);
		return value instanceof Short ? (short) value : 0;
	}

	public int getInt(Metadatable metadatable, String key, int defaultValue) {
		Object value = get(metadatable, key, defaultValue);
		return value instanceof Integer ? (int) value : 0;
	}

	public long getLong(Metadatable metadatable, String key, long defaultValue) {
		Object value = get(metadatable, key, defaultValue);
		return value instanceof Long ? (long) value : 0;
	}

	public double getDouble(Metadatable metadatable, String key, double defaultValue) {
		Object value = get(metadatable, key, defaultValue);
		return value instanceof Double ? (double) value : 0.0D;
	}

	public float getFloat(Metadatable metadatable, String key, float defaultValue) {
		Object value = get(metadatable, key, defaultValue);
		return value instanceof Float ? (float) value : 0.0F;
	}
}
