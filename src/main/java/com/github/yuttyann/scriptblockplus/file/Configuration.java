package com.github.yuttyann.scriptblockplus.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.reader.UnicodeReader;
import org.yaml.snakeyaml.representer.Representer;

import com.github.yuttyann.scriptblockplus.utils.FileUtils;

@SuppressWarnings("unchecked")
public class Configuration {

	private Yaml yaml;
	private File file;
	private Map<String, Object> root;

	public static Configuration loadConfiguration(File file) {
		DumperOptions options = new DumperOptions();
		options.setIndent(4);
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		Configuration config = new Configuration();
		config.yaml = new Yaml(new SafeConstructor(), new Representer(), options);
		config.load(file);
		return config;
	}

	public void load(File file) {
		FileInputStream fis = null;
		try {
			this.file = file;
			fis = new FileInputStream(file);
			read(yaml.load(new UnicodeReader(fis)));
		} catch (IOException e) {
			root = new HashMap<String, Object>();
		} catch (Exception e) {
			root = new HashMap<String, Object>();
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean save() {
		FileOutputStream fos = null;
		File parent = file.getParentFile();
		if (!parent.exists()) {
			parent.mkdirs();
		}
		try {
			fos = new FileOutputStream(file);
			yaml.dump(root, new OutputStreamWriter(fos, FileUtils.UTF8));
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private void read(Object input) throws Exception {
		try {
			root = (Map<String, Object>) input;
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
	}

	public Object getProperty(String path) {
		if (!path.contains(".")) {
			Object val = root.get(path);
			if (val == null) {
				return null;
			}
			return val;
		}
		String[] parts = path.split("\\.");
		Map<String, Object> node = root;
		for (int i = 0; i < parts.length; i++) {
			Object obj = node.get(parts[i]);
			if (obj == null) {
				return null;
			}
			if (i == parts.length - 1) {
				return obj;
			}
			try {
				node = (Map<String, Object>) obj;
			} catch (ClassCastException e) {
				return null;
			}
		}
		return null;
	}

	public void setProperty(String path, Object value) {
		if (!path.contains(".")) {
			root.put(path, value);
			return;
		}
		String[] parts = path.split("\\.");
		Map<String, Object> node = root;
		for (int i = 0; i < parts.length; i++) {
			Object obj = node.get(parts[i]);
			if (i == parts.length - 1) {
				node.put(parts[i], value);
				return;
			}
			if (obj == null || !(obj instanceof Map)) {
				obj = new HashMap<String, Object>();
				node.put(parts[i], obj);
			}
			node = (Map<String, Object>) obj;
		}
	}

	public Set<String> getKeys() {
		return new HashSet<String>(root.keySet());
	}

	public Set<String> getKeys(String path) {
		Object obj = getProperty(path);
		if (obj != null && obj instanceof Map) {
			return new HashSet<String>(((Map<String, Object>) obj).keySet());
		}
		return null;
	}

	public List<Object> getList(String path) {
		Object obj = getProperty(path);
		if (obj != null && obj instanceof List) {
			return (List<Object>) obj;
		}
		return null;
	}

	public List<String> getStringList(String path, List<String> def) {
		List<Object> list = getList(path);
		if (list == null) {
			return def != null ? def : new ArrayList<String>();
		}
		List<String> strList = new ArrayList<String>();
		for (Object obj : list) {
			if (obj == null) {
				continue;
			}
			strList.add(obj.toString());
		}
		return strList;
	}
}