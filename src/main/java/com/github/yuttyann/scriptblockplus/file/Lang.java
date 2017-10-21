package com.github.yuttyann.scriptblockplus.file;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.plugin.Plugin;

import com.github.yuttyann.scriptblockplus.utils.FileUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public final class Lang {

	private final Plugin plugin;
	private final File jarFile;
	private final String language;

	public Lang(Plugin plugin, String language) {
		this.plugin = plugin;
		this.jarFile = FileUtils.getJarFile(plugin);
		this.language = language.toLowerCase();
	}

	public String getLanguage() {
		return language;
	}

	public YamlConfig load(String filePath, String dirPath) {
		return YamlConfig.load(plugin, getFile(filePath, dirPath), false);
	}

	private File getFile(String filePath, String dirPath) {
		String path = dirPath + "/" + language + ".yml";
		String lang = isExists(jarFile, path) ? language : "en";
		filePath = StringUtils.replace(filePath, "{lang}", lang);
		File file = new File(plugin.getDataFolder(), filePath);
		if (!file.exists()) {
			if (!lang.equals(language)) {
				path = dirPath + "/" + lang + ".yml";
			}
			FileUtils.copyFileFromJar(jarFile, file, path);
		}
		return file;
	}

	private boolean isExists(File jarFile, String filePath) {
		if (StringUtils.isEmpty(filePath)) {
			return false;
		}
		JarFile jar = null;
		try {
			jar = new JarFile(jarFile);
			Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				if (!entry.isDirectory() && entry.getName().equals(filePath)) {
					return true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (jar != null) {
				try {
					jar.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}
}