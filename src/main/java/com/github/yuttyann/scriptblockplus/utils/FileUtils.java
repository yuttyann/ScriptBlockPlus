package com.github.yuttyann.scriptblockplus.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.function.Predicate;

import org.bukkit.plugin.Plugin;

import com.google.common.base.Charsets;

public final class FileUtils {

	public static InputStream getResource(Plugin plugin, String filePath) {
		if (plugin == null || StringUtils.isEmpty(filePath)) {
			return null;
		}
		try {
			URL url = plugin.getClass().getClassLoader().getResource(filePath);
			if (url == null) {
				return null;
			}
			URLConnection connection = url.openConnection();
			connection.setUseCaches(false);
			return connection.getInputStream();
		} catch (IOException e) {
			return null;
		}
	}

	public static void copyFileFromPlugin(Plugin plugin, File targetFile, String sourceFilePath) {
		if (targetFile == null || StringUtils.isEmpty(sourceFilePath)) {
			return;
		}
		File parent = targetFile.getParentFile();
		if (!parent.exists()) {
			parent.mkdirs();
		}
		try (
				InputStream is = getResource(plugin, sourceFilePath); InputStreamReader isr = new InputStreamReader(is, Charsets.UTF_8);
				OutputStream os = new FileOutputStream(targetFile); OutputStreamWriter osw = new OutputStreamWriter(os, Charsets.UTF_8);
				BufferedReader reader = new BufferedReader(isr); BufferedWriter writer = new BufferedWriter(osw)
			) {
			boolean isFirst = true;
			String line;
			while ((line = reader.readLine()) != null) {
				if (isFirst && !(isFirst = false)) {
					line = removeBom(line);
				}
				writer.write(line);
				writer.newLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void copyDirectory(File sourceFile, File targetFile, Predicate<File> filter) {
		if (targetFile == null || !isExists(sourceFile) || isEmpty(sourceFile)) {
			return;
		}
		if (!targetFile.exists()) {
			targetFile.mkdirs();
		}
		for (File file : sourceFile.listFiles()) {
			if (filter.test(file)) {
				continue;
			}
			File copy = new File(targetFile, file.getName());
			try (
					InputStream is = new FileInputStream(file); InputStreamReader isr = new InputStreamReader(is, Charsets.UTF_8);
					OutputStream os = new FileOutputStream(copy); OutputStreamWriter osw = new OutputStreamWriter(os, Charsets.UTF_8);
					BufferedReader reader = new BufferedReader(isr); BufferedWriter writer = new BufferedWriter(osw)
				) {
				boolean isFirst = true;
				String line;
				while ((line = reader.readLine()) != null) {
					if (isFirst && !(isFirst = false)) {
						line = removeBom(line);
					}
					writer.write(line);
					writer.newLine();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void fileDownload(String url, File file) throws IOException {
		File parent = file.getParentFile();
		if (!parent.exists()) {
			parent.mkdirs();
		}
		try (InputStream is = getWebFile(url); FileOutputStream fos = new FileOutputStream(file)) {
			byte[] bytes = new byte[1024];
			int length;
			while ((length = is.read(bytes)) != -1) {
				fos.write(bytes, 0, length);
			}
		} catch (MalformedURLException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		}
	}

	public static void saveFile(File file, Object value) throws IOException {
		try (OutputStream os = new FileOutputStream(file); ObjectOutputStream oos = new ObjectOutputStream(os)) {
			oos.writeObject(value);
		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		}
	}

	public static <T> T loadFile(File file) throws IOException, ClassNotFoundException {
		try (InputStream is = new FileInputStream(file); ObjectInputStream ois = new ObjectInputStream(is))  {
			return (T) ois.readObject();
		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} catch (ClassNotFoundException e) {
			throw e;
		}
	}

	public static InputStream getWebFile(String url) throws MalformedURLException, IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setRequestMethod("GET");
		connection.setAllowUserInteraction(false);
		connection.setInstanceFollowRedirects(true);
		connection.connect();
		int httpStatusCode = connection.getResponseCode();
		if (httpStatusCode == HttpURLConnection.HTTP_OK) {
			return connection.getInputStream();
		}
		connection.disconnect();
		return null;
	}

	public static boolean isEmpty(File file) {
		String[] array = file.list();
		return array == null || array.length == 0;
	}

	public static boolean isExists(File file) {
		return file != null && file.exists();
	}

	private static String removeBom(String source) {
		if (source.startsWith("\uFEFF")) {
			return source.substring(1);
		}
		return source;
	}
}