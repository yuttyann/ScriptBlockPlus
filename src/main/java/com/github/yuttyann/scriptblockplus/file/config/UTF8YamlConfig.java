package com.github.yuttyann.scriptblockplus.file.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConstructor;
import org.bukkit.configuration.file.YamlRepresenter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class UTF8YamlConfig extends YamlConfiguration {

	private final DumperOptions yamlOptions = new DumperOptions();
	private final Representer yamlRepresenter = new YamlRepresenter();
	private final Yaml yaml = new Yaml(new YamlConstructor(), yamlRepresenter, yamlOptions);

	@Override
	public String saveToString() {
		yamlOptions.setIndent(options().indent());
		yamlOptions.setDefaultFlowStyle(FlowStyle.BLOCK);
		yamlRepresenter.setDefaultFlowStyle(FlowStyle.BLOCK);
		String header = buildHeader();
		String dump = yaml.dump(getValues(false));
		if (dump.equals(BLANK_CONFIG)) {
			dump = "";
		}
		return header + dump;
	}

	@Override
	public void save(File file) throws IOException {
		Validate.notNull(file, "File cannot be null");
		Files.createParentDirs(file);
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), Charsets.UTF_8));
			writer.write(saveToString());
		} catch (UnsupportedEncodingException e) {
			throw e;
		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			if (writer != null) {
				try {
					writer.flush();
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void save(String file) throws IOException {
		Validate.notNull(file, "File cannot be null");
		save(new File(file));
	}

	@Override
	public void load(File file) throws FileNotFoundException, IOException, InvalidConfigurationException {
		Validate.notNull(file, "File cannot be null");
		load(new BufferedReader(new InputStreamReader(new FileInputStream(file), Charsets.UTF_8)));
	}

	@Deprecated
	public void load(InputStream stream) throws IOException, InvalidConfigurationException {
		Validate.notNull(stream, "Stream cannot be null");
		load(new BufferedReader(new InputStreamReader(stream, Charsets.UTF_8)));
	}

	@Override
	public void load(Reader reader) throws IOException, InvalidConfigurationException {
		BufferedReader input = null;
		try {
			if (reader instanceof BufferedReader) {
				input = (BufferedReader) reader;
			} else {
				input = new BufferedReader(reader);
			}
			StringBuilder builder = new StringBuilder();
			String line;
			while ((line = input.readLine()) != null) {
				builder.append(line).append('\n');
			}
			loadFromString(builder.toString());
		} finally {
			try {
				if (input != null) {
					input.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void load(String file) throws FileNotFoundException, IOException, InvalidConfigurationException {
		Validate.notNull(file, "File cannot be null");
		load(new File(file));
	}

	public static UTF8YamlConfig loadConfiguration(File file) {
		Validate.notNull(file, "File cannot be null");
		UTF8YamlConfig config = new UTF8YamlConfig();
		try {
			config.load(file);
		} catch (FileNotFoundException ex) {
		} catch (IOException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, ex);
		} catch (InvalidConfigurationException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file , ex);
		}
		return config;
	}

	@Deprecated
	public static UTF8YamlConfig loadConfiguration(InputStream stream) {
		Validate.notNull(stream, "Stream cannot be null");
		UTF8YamlConfig config = new UTF8YamlConfig();
		try {
			config.load(stream);
		} catch (IOException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot load configuration from stream", ex);
		} catch (InvalidConfigurationException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot load configuration from stream", ex);
		}
		return config;
	}

	public static UTF8YamlConfig loadConfiguration(Reader reader) {
		Validate.notNull(reader, "Stream cannot be null");
		UTF8YamlConfig config = new UTF8YamlConfig();
		try {
			config.load(reader);
		} catch (IOException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot load configuration from stream", ex);
		} catch (InvalidConfigurationException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot load configuration from stream", ex);
		}
		return config;
	}
}