package com.github.yuttyann.scriptblockplus.file;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.plugin.Plugin;

import com.github.yuttyann.scriptblockplus.utils.FileUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public abstract class Lang {

	protected final Plugin plugin;
	private final File jarFile;
	private final String langCode;

	public Lang(Plugin plugin) {
		this.plugin = plugin;
		this.jarFile = FileUtils.getJarFile(plugin);
		this.langCode = System.getProperty("user.language").toLowerCase();
	}

	public String getLangCode() {
		return langCode;
	}

	protected YamlConfig load(String filePath, String dirPath) {
		return YamlConfig.load(plugin, getFile(filePath, dirPath), false);
	}

	protected File getFile(String filePath, String dirPath) {
		String path = dirPath + "/" + langCode + ".yml";
		String code = isExists(jarFile, path) ? langCode : "en";
		filePath = StringUtils.replace(filePath, "{lang}", code);
		File file = new File(plugin.getDataFolder(), filePath);
		if (!file.exists()) {
			if (!code.equals(langCode)) {
				path = dirPath + "/" + code + ".yml";
			}
			FileUtils.copyFileFromJar(jarFile, file, path);
		}
		return file;
	}

	protected boolean isExists(File jarFile, String filePath) {
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