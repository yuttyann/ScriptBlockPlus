package com.github.yuttyann.scriptblockplus;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.plugin.java.JavaPlugin;

public final class PluginInstance {

	private static final Map<Class<? extends JavaPlugin>, JavaPlugin> INSTANCES = new HashMap<>();

	private final Class<? extends JavaPlugin> key;
	private final JavaPlugin plugin;


	public PluginInstance(Class<? extends JavaPlugin> key, JavaPlugin plugin) {
		this.key = key;
		this.plugin = plugin;
	}

	public void put() {
		INSTANCES.put(key, plugin);
	}

	public void remove() {
		INSTANCES.remove(key);
	}

	public Set<Entry<Class<? extends JavaPlugin>, JavaPlugin>> entrySet() {
		return INSTANCES.entrySet();
	}

	public static <T extends JavaPlugin> T get(Class<? extends JavaPlugin> key) {
		return (T) INSTANCES.get(key);
	}
}