package com.github.yuttyann.scriptblockplus.file.yaml;

import com.google.common.base.Charsets;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConstructor;
import org.bukkit.configuration.file.YamlRepresenter;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.util.logging.Level;

public final class UTF8Config extends YamlConfiguration {

	private final DumperOptions yamlOptions = new DumperOptions();
	private final Representer yamlRepresenter = new YamlRepresenter();
	private final Yaml yaml = new Yaml(new YamlConstructor(), yamlRepresenter, yamlOptions);

	@NotNull
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

	public void save(@NotNull File file) throws IOException {
		Validate.notNull(file, "File cannot be null");
		File parent = file.getParentFile();
		if (!parent.exists()) {
			parent.mkdirs();
		}
		String data = saveToString();
		try (FileOutputStream fos = new FileOutputStream(file); OutputStreamWriter writer = new OutputStreamWriter(fos, Charsets.UTF_8)) {
			writer.write(data);
		}
	}

	public void save(@NotNull String file) throws IOException {
		Validate.notNull(file, "File cannot be null");
		save(new File(file));
	}

	public void load(@NotNull File file) throws FileNotFoundException, IOException, InvalidConfigurationException {
		Validate.notNull(file, "File cannot be null");
		load(new BufferedReader(new InputStreamReader(new FileInputStream(file), Charsets.UTF_8)));
	}

	public void load(@NotNull Reader reader) throws IOException, InvalidConfigurationException {
		StringBuilder builder = new StringBuilder();
		try (BufferedReader input = reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader)) {
			String line;
			while ((line = input.readLine()) != null) {
				builder.append(line).append('\n');
			}
		}
		loadFromString(builder.toString());
	}

	public void load(@NotNull String file) throws FileNotFoundException, IOException, InvalidConfigurationException {
		Validate.notNull(file, "File cannot be null");
		load(new File(file));
	}

	@NotNull
	public static UTF8Config loadConfiguration(@NotNull File file) {
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

	@NotNull
	public static UTF8Config loadConfiguration(@NotNull Reader reader) {
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