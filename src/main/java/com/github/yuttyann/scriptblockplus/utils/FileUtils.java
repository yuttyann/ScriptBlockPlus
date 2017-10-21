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
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class FileUtils {

	private static Method methodGetFile;

	public static void copyFileFromJar(File jarFile, File targetFile, String sourceFilePath) {
		if (!isExists(jarFile)  || isExists(targetFile) || StringUtils.isEmpty(sourceFilePath)) {
			return;
		}
		JarFile jar = null;
		InputStream is = null;
		FileOutputStream fos = null;
		BufferedReader reader = null;
		BufferedWriter writer = null;
		File parent = targetFile.getParentFile();
		if (!parent.exists()) {
			parent.mkdirs();
		}
		try {
			jar = new JarFile(jarFile);
			ZipEntry zipEntry = jar.getEntry(sourceFilePath);
			is = jar.getInputStream(zipEntry);
			fos = new FileOutputStream(targetFile);
			reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			writer = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
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
		} finally {
			if (writer != null) {
				try {
					writer.flush();
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.flush();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (jar != null) {
				try {
					jar.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void fileEncode(File file, String charsetName) {
		if (!isExists(file)) {
			return;
		}
		BufferedReader reader = null;
		BufferedWriter writer = null;
		try {
			if (charsetName == null) {
				charsetName = Charset.defaultCharset().name();
			}
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charsetName));
			List<String> contents = new LinkedList<String>();
			while (reader.ready()) {
				contents.add(reader.readLine());
			}
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
			boolean isFirst = true;
			for (String line : contents) {
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
		} finally {
			if (writer != null) {
				try {
					writer.flush();
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void fileDownload(String url, File file) throws IOException {
		File parent = file.getParentFile();
		if (!parent.exists()) {
			parent.mkdirs();
		}
		InputStream is = null;
		FileOutputStream fos = null;
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("GET");
			connection.setAllowUserInteraction(false);
			connection.setInstanceFollowRedirects(true);
			connection.connect();
			int httpStatusCode = connection.getResponseCode();
			if (httpStatusCode != HttpURLConnection.HTTP_OK) {
				connection.disconnect();
				return;
			}
			is = connection.getInputStream();
			fos = new FileOutputStream(file);
			byte[] bytes = new byte[4096];
			int length;
			while ((length = is.read(bytes)) != -1) {
				fos.write(bytes, 0, length);
			}
		} catch (MalformedURLException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			if (fos != null) {
				try {
					fos.flush();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void saveFile(File file, Object value) {
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(value);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (oos != null) {
				try {
					oos.flush();
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static <T> T loadFile(File file) {
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(file));
			@SuppressWarnings("unchecked")
			T value = (T) ois.readObject();
			return value;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static File getJarFile(Plugin plugin) {
		try {
			if (methodGetFile == null) {
				methodGetFile = JavaPlugin.class.getDeclaredMethod("getFile");
				methodGetFile.setAccessible(true);
			}
			return (File) methodGetFile.invoke(plugin);
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String removeBom(String source) {
		if (source.startsWith("\uFEFF")) {
			return source.substring(1);
		}
		return source;
	}

	private static boolean isExists(File file) {
		return file != null && file.exists();
	}
}