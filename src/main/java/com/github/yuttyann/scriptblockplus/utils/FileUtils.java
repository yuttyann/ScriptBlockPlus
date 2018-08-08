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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

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
		InputStream is = null;
		BufferedReader reader = null;
		BufferedWriter writer = null;
		try {
			is = getResource(plugin, sourceFilePath);
			if (is == null) {
				return;
			}
			reader = new BufferedReader(new InputStreamReader(is, Charsets.UTF_8));
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFile), Charsets.UTF_8));
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
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

    public static void copyDirectory(File sourceFile, File targetFile) {
    	if (targetFile == null || !isExists(sourceFile) || isEmpty(sourceFile)) {
    		return;
    	}
		if (!targetFile.exists()) {
			targetFile.mkdirs();
		}
		for (File file : sourceFile.listFiles()) {
            BufferedReader reader = null;
            BufferedWriter writer = null;
            try {
            	File copy = new File(targetFile, file.getName());
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charsets.UTF_8));
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(copy), Charsets.UTF_8));
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
			byte[] bytes = new byte[1024];
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
			return (T) ois.readObject();
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