package com.github.yuttyann.scriptblockplus;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

public final class PluginInstance {

	private static final Map<Class<? extends JavaPlugin>, JavaPlugin> INSTANCES = new HashMap<>();

	private final Class<? extends JavaPlugin> key;
	private final JavaPlugin plugin;

	public PluginInstance(@NotNull Class<? extends JavaPlugin> key, @NotNull JavaPlugin plugin) {
		this.key = Objects.requireNonNull(key);
		this.plugin = Objects.requireNonNull(plugin);
	}

	public void put() {
		INSTANCES.put(key, plugin);
	}

	public void remove() {
		INSTANCES.remove(key);
	}

	@NotNull
	public Set<Entry<Class<? extends JavaPlugin>, JavaPlugin>> entrySet() {
		return INSTANCES.entrySet();
	}

	@SuppressWarnings("unchecked")
	@NotNull
	public static <T extends JavaPlugin> T get(@NotNull Class<? extends JavaPlugin> key) {
		return (T) Objects.requireNonNull(INSTANCES.get(key));
	}
}