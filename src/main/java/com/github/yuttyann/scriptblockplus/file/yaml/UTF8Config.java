package com.github.yuttyann.scriptblockplus.file.yaml;

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

final class UTF8Config extends YamlConfiguration {

	private final DumperOptions yamlOptions = new DumperOptions();
	private final Representer yamlRepresenter = new YamlRepresenter();
	private final Yaml yaml = new Yaml(new YamlConstructor(), yamlRepresenter, yamlOptions);

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

	public void save(File file) throws IOException {
		Validate.notNull(file, "File cannot be null");
		File parent = file.getParentFile();
		if (!parent.exists()) {
			parent.mkdirs();
		}
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

	public void save(String file) throws IOException {
		Validate.notNull(file, "File cannot be null");
		save(new File(file));
	}

	public void load(File file) throws FileNotFoundException, IOException, InvalidConfigurationException {
		Validate.notNull(file, "File cannot be null");
		load(new BufferedReader(new InputStreamReader(new FileInputStream(file), Charsets.UTF_8)));
	}

	@Deprecated
	public void load(InputStream stream) throws IOException, InvalidConfigurationException {
		Validate.notNull(stream, "Stream cannot be null");
		load(new BufferedReader(new InputStreamReader(stream, Charsets.UTF_8)));
	}

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

	public void load(String file) throws FileNotFoundException, IOException, InvalidConfigurationException {
		Validate.notNull(file, "File cannot be null");
		load(new File(file));
	}

	public static UTF8Config loadConfiguration(File file) {
		Validate.notNull(file, "File cannot be null");
		UTF8Config config = new UTF8Config();
		try {
			config.load(file);
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, e);
		} catch (InvalidConfigurationException e) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, e);
		}
		return config;
	}

	@Deprecated
	public static UTF8Config loadConfiguration(InputStream stream) {
		Validate.notNull(stream, "Stream cannot be null");
		UTF8Config config = new UTF8Config();
		try {
			config.load(stream);
		} catch (IOException e) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot load configuration from stream", e);
		} catch (InvalidConfigurationException e) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot load configuration from stream", e);
		}
		return config;
	}

	public static UTF8Config loadConfiguration(Reader reader) {
		Validate.notNull(reader, "Stream cannot be null");
		UTF8Config config = new UTF8Config();
		try {
			config.load(reader);
		} catch (IOException e) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot load configuration from stream", e);
		} catch (InvalidConfigurationException e) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot load configuration from stream", e);
		}
		return config;
	}
}