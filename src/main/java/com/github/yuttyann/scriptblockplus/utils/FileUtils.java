package com.github.yuttyann.scriptblockplus.utils;

import com.google.common.base.Charsets;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.function.Predicate;

public final class FileUtils {

	@Nullable
	public static InputStream getResource(@NotNull Plugin plugin, @NotNull String filePath) {
		if (StringUtils.isEmpty(filePath)) {
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

	public static void copyFileFromPlugin(@NotNull Plugin plugin, @NotNull File targetFile, @NotNull String sourceFilePath) {
		if (StringUtils.isEmpty(sourceFilePath)) {
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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void copyDirectory(@NotNull File sourceFile, @NotNull File targetFile, @NotNull Predicate<File> filter) {
		if (!isExists(sourceFile) || isEmpty(sourceFile)) {
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
					FileOutputStream fos = new FileOutputStream(copy); OutputStreamWriter osw = new OutputStreamWriter(fos, Charsets.UTF_8);
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
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void write(@NotNull File file, @NotNull String source, @NotNull Charset charset) throws IOException {
		File parent = file.getParentFile();
		if (!parent.exists()) {
			parent.mkdirs();
		}
		try (
				FileOutputStream fos = new FileOutputStream(file);
				OutputStreamWriter osw = new OutputStreamWriter(fos, charset);
				BufferedWriter writer = new BufferedWriter(osw)
        	) {
			writer.write(source);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@NotNull
	public static String readToString(@NotNull File file, @NotNull Charset charset) throws IOException {
		File parent = file.getParentFile();
		if (!parent.exists()) {
			parent.mkdirs();
		}
		try (FileInputStream fis = new FileInputStream(file); BufferedInputStream bis = new BufferedInputStream(fis)) {
			byte[] data = new byte[(int) file.length()];
			bis.read(data);
			return new String(data, charset);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static void fileDownload(@NotNull String url, @NotNull File file) throws IOException {
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
		} catch (IOException e) {
			throw e;
		}
	}

	public static void saveFile(@NotNull File file, @NotNull Object value) throws IOException {
		try (OutputStream os = new FileOutputStream(file); ObjectOutputStream oos = new ObjectOutputStream(os)) {
			oos.writeObject(value);
		} catch (IOException e) {
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	@NotNull
	public static <T> T loadFile(@NotNull File file) throws IOException, ClassNotFoundException {
		try (InputStream is = new FileInputStream(file); ObjectInputStream ois = new ObjectInputStream(is))  {
			return (T) ois.readObject();
		} catch (ClassNotFoundException | IOException e) {
			throw e;
		}
	}

	@Nullable
	public static InputStream getWebFile(@NotNull String url) throws MalformedURLException, IOException {
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

	public static boolean isEmpty(@NotNull File file) {
		String[] array = file.list();
		return array == null || array.length == 0;
	}

	public static boolean isExists(@Nullable File file) {
		return file != null && file.exists();
	}

	@NotNull
	private static String removeBom(@NotNull String source) {
		if (source.startsWith("\uFEFF")) {
			return source.substring(1);
		}
		return source;
	}
}