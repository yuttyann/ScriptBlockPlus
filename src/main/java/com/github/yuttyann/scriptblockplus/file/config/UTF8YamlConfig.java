package com.github.yuttyann.scriptblockplus.file.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.logging.Level;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.io.Files;

public class UTF8YamlConfig extends YamlConfiguration {

	@Override
	public void load(File file) throws FileNotFoundException, IOException, InvalidConfigurationException {
		Validate.notNull(file, "File cannot be null");
		load(new InputStreamReader(new FileInputStream(file), "UTF-8"));
	}

	@Override
	public void save(File file) throws IOException {
		Validate.notNull(file, "File cannot be null");
		Files.createParentDirs(file);
		String data = saveToString();
		Writer writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
		try {
			writer.write(data);
		} finally {
			writer.close();
		}
	}

	public static UTF8YamlConfig loadConfiguration(File file) {
		Validate.notNull(file, "File cannot be null");
		UTF8YamlConfig config = new UTF8YamlConfig();
		try {
			config.load(file);
		} catch (FileNotFoundException localFileNotFoundException) {
		} catch (IOException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, ex);
		} catch (InvalidConfigurationException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, ex);
		}
		return config;
	}
}
