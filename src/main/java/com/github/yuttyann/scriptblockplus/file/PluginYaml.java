package com.github.yuttyann.scriptblockplus.file;

import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginAwareness;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoadOrder;

import com.github.yuttyann.scriptblockplus.ScriptBlock;


public class PluginYaml {

	private static PluginYaml instance;
	private PluginDescriptionFile description;

	static {
		instance = new PluginYaml();
		instance.description = ScriptBlock.instance.getDescription();
	}

	public static String getName() {
		return instance.description.getName();
	}

	public static String getVersion() {
		return instance.description.getVersion();
	}

	public static String getDescription() {
		return instance.description.getDescription();
	}

	public static PluginLoadOrder getLoad() {
		return instance.description.getLoad();
	}

	public static List<String> getAuthors() {
		return instance.description.getAuthors();
	}

	public static String getWebsite() {
		return instance.description.getWebsite();
	}

	public static boolean isDatabaseEnabled() {
		return instance.description.isDatabaseEnabled();
	}

	public static List<String> getDepend() {
		return instance.description.getDepend();
	}

	public static List<String> getSoftDepend() {
		return instance.description.getSoftDepend();
	}

	public static List<String> getLoadBefore() {
		return instance.description.getLoadBefore();
	}

	public static String getPrefix() {
		return instance.description.getPrefix();
	}

	public static Map<String, Map<String, Object>> getCommands() {
		return instance.description.getCommands();
	}

	public static List<Permission> getPermissions() {
		return instance.description.getPermissions();
	}

	public static PermissionDefault getPermissionDefault() {
		return instance.description.getPermissionDefault();
	}

	public static Set<PluginAwareness> getAwareness() {
		return instance.description.getAwareness();
	}

	public static String getFullName() {
		return instance.description.getFullName();
	}

	@Deprecated
	public static String getClassLoaderOf() {
		return instance.description.getClassLoaderOf();
	}

	public static void setDatabaseEnabled(boolean database) {
		instance.description.setDatabaseEnabled(database);
	}

	public static void save(Writer writer) {
		instance.description.save(writer);
	}

	@Deprecated
	public static String getRawName() {
		return instance.description.getRawName();
	}
}