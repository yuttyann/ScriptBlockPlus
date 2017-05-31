package com.github.yuttyann.scriptblockplus.metadata;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.NumberConversions;

public abstract class SimpleMetadata {

	private Plugin plugin;

	public SimpleMetadata(Plugin plugin) {
		this.plugin = plugin;
	}

	public abstract void removeAll(Player player);

	public abstract boolean hasAll(Player player);

	public void set(Metadatable metadatable, String key, Object value) {
		metadatable.setMetadata(key, new FixedMetadataValue(plugin, value));
	}

	public void remove(Metadatable metadatable, String key) {
		metadatable.removeMetadata(key, plugin);
	}

	public boolean has(Metadatable metadatable, String key) {
		return metadatable.hasMetadata(key);
	}

	public Object get(Metadatable metadatable, String key) {
		return get(metadatable, key, null);
	}

	public Object get(Metadatable metadatable, String key, Object def) {
		List<MetadataValue> values = metadatable.getMetadata(key);
		for (MetadataValue value : values) {
			if (value.getOwningPlugin().getName().equals(plugin.getName())) {
				return value.value();
			}
		}
		return def;
	}

	public String getString(Metadatable metadatable, String key) {
		return getString(metadatable, key, null);
	}

	public String getString(Metadatable metadatable, String key, String def) {
		return asString(get(metadatable, key, def));
	}

	public byte getByte(Metadatable metadatable, String key) {
		return getByte(metadatable, key, (byte) 0);
	}

	public byte getByte(Metadatable metadatable, String key, byte def) {
		return asByte(get(metadatable, key, def));
	}

	public short getShort(Metadatable metadatable, String key) {
		return getShort(metadatable, key, (short) 0);
	}

	public short getShort(Metadatable metadatable, String key, short def) {
		return asShort(get(metadatable, key, def));
	}

	public int getInt(Metadatable metadatable, String key) {
		return getInt(metadatable, key, 0);
	}

	public int getInt(Metadatable metadatable, String key, int def) {
		return asInt(get(metadatable, key, def));
	}

	public long getLong(Metadatable metadatable, String key) {
		return getLong(metadatable, key, 0L);
	}

	public long getLong(Metadatable metadatable, String key, long def) {
		return asLong(get(metadatable, key, def));
	}

	public double getDouble(Metadatable metadatable, String key) {
		return getDouble(metadatable, key, 0.0D);
	}

	public double getDouble(Metadatable metadatable, String key, double def) {
		return asDouble(get(metadatable, key, def));
	}

	public float getFloat(Metadatable metadatable, String key) {
		return getFloat(metadatable, key, 0.0F);
	}

	public float getFloat(Metadatable metadatable, String key, float def) {
		return asFloat(get(metadatable, key, def));
	}

	public boolean getBoolean(Metadatable metadatable, String key) {
		return getBoolean(metadatable, key, false);
	}

	public boolean getBoolean(Metadatable metadatable, String key, boolean def) {
		return asBoolean(get(metadatable, key, def));
	}

	private String asString(Object value) {
		if (value == null) {
			return "";
		}
		return value.toString();
	}

	private byte asByte(Object value) {
		return NumberConversions.toByte(value);
	}

	private short asShort(Object value) {
		return NumberConversions.toShort(value);
	}

	private int asInt(Object value) {
		return NumberConversions.toInt(value);
	}

	private long asLong(Object value) {
		return NumberConversions.toLong(value);
	}

	private double asDouble(Object value) {
		return NumberConversions.toDouble(value);
	}

	private float asFloat(Object value) {
		return NumberConversions.toFloat(value);
	}

	private boolean asBoolean(Object value) {
		if (value instanceof Boolean) {
			return (Boolean) value;
		}
		if (value instanceof Number) {
			return ((Number) value).intValue() != 0;
		}
		if (value instanceof String) {
			return Boolean.parseBoolean(value.toString());
		}
		return value != null;
	}
}