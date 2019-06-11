package com.github.yuttyann.scriptblockplus.file.yaml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.logging.Level;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConstructor;
import org.bukkit.configuration.file.YamlRepresenter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

import com.google.common.base.Charsets;

public final class UTF8Config extends YamlConfiguration {

	private final DumperOptions yamlOptions = new DumperOptions();
	private final Representer yamlRepresenter = new YamlRepresenter();
	private final Yaml yaml = new Yaml(new YamlConstructor(), yamlRepresenter, yamlOptions);

	public String saveToString() {
        yamlOptions.setIndent(options().indent());
        yamlOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        yamlRepresenter.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
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
		String data = saveToString();
		Writer writer = new OutputStreamWriter(new FileOutputStream(file), Charsets.UTF_8);
		try {
			writer.write(data);
		} finally {
			if (writer != null) {
				writer.flush();
				writer.close();
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

	public void load(Reader reader) throws IOException, InvalidConfigurationException {
		BufferedReader input = reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
		StringBuilder builder = new StringBuilder();
		try {
			String line;
			while ((line = input.readLine()) != null) {
				builder.append(line).append('\n');
			}
		} finally {
			if (input != null) {
				input.close();
			}
		}
		loadFromString(builder.toString());
	}

	public void load(String file) throws FileNotFoundException, IOException, InvalidConfigurationException {
		Validate.notNull(file, "File cannot be null");
		load(new File(file));
	}

	public static UTF8Config loadConfiguration(File file) {
		Validate.notNull(file, "File cannot be null");
		UTF8Config yaml = new UTF8Config();
		try {
			yaml.load(file);
		} catch (FileNotFoundException e) {
		} catch (IOException | InvalidConfigurationException e) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, e);
		}
		return yaml;
	}

	public static UTF8Config loadConfiguration(Reader reader) {
		Validate.notNull(reader, "Stream cannot be null");
		UTF8Config yaml = new UTF8Config();
		try {
			yaml.load(reader);
		} catch (IOException | InvalidConfigurationException e) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot load configuration from stream", e);
		}
		return yaml;
	}
}