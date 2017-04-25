package com.github.yuttyann.scriptblockplus.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
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
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.bukkit.plugin.Plugin;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class FileUtils {

	public static final String UTF8 = "UTF-8";
	public static final String MS932 = "MS932";
	public static final String ASCII = "US-ASCII";

	public static boolean isUTF8(File file) {
		return detectCharset(file, Charset.forName(UTF8)) != null;
	}

	public static boolean isMS932(File file) {
		return detectCharset(file, Charset.forName(MS932)) != null;
	}

	public static boolean isASCII(File file) {
		return detectCharset(file, Charset.forName(ASCII)) != null;
	}

	public static void fileEncode(Plugin plugin, File file) {
		if (!file.exists()) {
			return;
		}
		boolean isUTF8 = isUTF8(file);
		boolean isCB19orLater = Utils.isCB19orLater();
		if (isUTF8 && !isCB19orLater) {
			if (!Utils.isWindows()) {
				return;
			}
			renameToEncode(plugin, file, true);
		} else if (!isUTF8 && isCB19orLater) {
			renameToEncode(plugin, file, false);
		}
	}

	public static void fileEncode(File sourceFile, File targetFile) {
		fileEncode(sourceFile, targetFile, isUTF8(sourceFile));
	}

	public static void fileEncode(File sourceFile, File targetFile, boolean isUTF8) {
		InputStream is = null;
		FileOutputStream fos = null;
		BufferedReader reader = null;
		BufferedWriter writer = null;
		try {
			is = new FileInputStream(sourceFile);
			fos = new FileOutputStream(targetFile);
			reader = new BufferedReader(new InputStreamReader(is, isUTF8 ? UTF8 : MS932));
			if (!Utils.isWindows() || Utils.isCB19orLater()) {
				writer = new BufferedWriter(new OutputStreamWriter(fos, UTF8));
			} else {
				writer = new BufferedWriter(new OutputStreamWriter(fos, MS932));
			}
			String line;
			boolean first = true;
			while ((line = reader.readLine()) != null) {
				if (first && !(first = false)) {
					line = removeBom(line, isUTF8);
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
		}
	}

	public static void copyFileFromJar(File jarFile, File targetFile, String sourceFilePath) {
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
			reader = new BufferedReader(new InputStreamReader(is, UTF8));
			if (!Utils.isWindows() || Utils.isCB19orLater()) {
				writer = new BufferedWriter(new OutputStreamWriter(fos, UTF8));
			} else {
				writer = new BufferedWriter(new OutputStreamWriter(fos, MS932));
			}
			String line;
			boolean first = true;
			while ((line = reader.readLine()) != null) {
				if (first && !(first = false)) {
					line = removeBom(line, true);
				}
				writer.write(line);
				writer.newLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (jar != null) {
				try {
					jar.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
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
		}
	}

	public static void copyFolderFromJar(File jarFile, File targetFilePath, String sourceFilePath) {
		JarFile jar = null;
		if (!targetFilePath.exists()) {
			targetFilePath.mkdirs();
		}
		try {
			jar = new JarFile(jarFile);
			Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				if (!entry.isDirectory() && entry.getName().startsWith(sourceFilePath)) {
					File targetFile = new File(targetFilePath, entry.getName().substring(sourceFilePath.length() + 1));
					if (!targetFile.getParentFile().exists()) {
						targetFile.getParentFile().mkdirs();
					}
					InputStream is = null;
					FileOutputStream fos = null;
					BufferedReader reader = null;
					BufferedWriter writer = null;
					try {
						is = jar.getInputStream(entry);
						fos = new FileOutputStream(targetFile);
						reader = new BufferedReader(new InputStreamReader(is, UTF8));
						if (!Utils.isWindows() || Utils.isCB19orLater()) {
							writer = new BufferedWriter(new OutputStreamWriter(fos, UTF8));
						} else {
							writer = new BufferedWriter(new OutputStreamWriter(fos, MS932));
						}
						String line;
						boolean first = true;
						while ((line = reader.readLine()) != null) {
							if (first && !(first = false)) {
								line = removeBom(line, true);
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
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (jar != null) {
				try {
					jar.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void saveFile(File targetFile, Object value) throws Exception {
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(targetFile));
			oos.writeObject(value);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (oos != null) {
				oos.flush();
				oos.close();
			}
		}
	}

	public static Object loadFile(File targetFile) throws Exception {
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(targetFile));
			return ois.readObject();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ois != null) {
				ois.close();
			}
		}
		return null;
	}

	public static void fileDownload(String url, File targetFile) throws IOException {
		InputStream input = null;
		FileOutputStream output = null;
		try {
			URLConnection urlconn = new URL(url).openConnection();
			HttpURLConnection httpconn = (HttpURLConnection) urlconn;
			httpconn.setAllowUserInteraction(false);
			httpconn.setInstanceFollowRedirects(true);
			httpconn.setRequestMethod("GET");
			httpconn.connect();
			int httpStatusCode = httpconn.getResponseCode();
			if (httpStatusCode != HttpURLConnection.HTTP_OK) {
				httpconn.disconnect();
				return;
			}
			File parent = new File(targetFile.getParent());
			if (!parent.exists()) {
				parent.mkdirs();
			}
			input = httpconn.getInputStream();
			output = new FileOutputStream(targetFile, false);
			byte[] bytes = new byte[4096];
			int length;
			while ((length = input.read(bytes)) != -1) {
				output.write(bytes, 0, length);
			}
		} finally {
			if (output != null) {
				try {
					output.flush();
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static Document getDocument(String name) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		URLConnection urlconn = new URL("http://xml.yuttyann44581.net/uploads//" + name + ".xml").openConnection();
		HttpURLConnection httpconn = (HttpURLConnection) urlconn;
		httpconn.setAllowUserInteraction(false);
		httpconn.setInstanceFollowRedirects(true);
		httpconn.setRequestMethod("GET");
		httpconn.connect();
		int httpStatusCode = httpconn.getResponseCode();
		if (httpStatusCode != HttpURLConnection.HTTP_OK) {
			httpconn.disconnect();
			return null;
		}
		return builder.parse(httpconn.getInputStream());
	}

	public static List<String> getFileText(File file) {
		FileReader fileReader = null;
		BufferedReader buReader = null;
		try {
			fileReader = new FileReader(file);
			buReader = new BufferedReader(fileReader);
			String line;
			List<String> list = new ArrayList<String>();
			while ((line = buReader.readLine()) != null) {
				list.add(line);
			}
			return list;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (buReader != null) {
				try {
					buReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fileReader != null) {
				try {
					fileReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return new ArrayList<String>();
	}

	public static List<String> getFileText(String url) {
		InputStream input = null;
		InputStreamReader inReader = null;
		BufferedReader buReader = null;
		try {
			input = new URL(url).openStream();
			inReader = new InputStreamReader(input);
			buReader = new BufferedReader(inReader);
			String line;
			List<String> list = new ArrayList<String>();
			while ((line = buReader.readLine()) != null) {
				list.add(line);
			}
			return list;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (buReader != null) {
				try {
					buReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (inReader != null) {
				try {
					inReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return new ArrayList<String>();
	}

	public static Charset detectCharset(File file, Charset charset) {
		FileInputStream fis = null;
		BufferedInputStream input = null;
		try {
			fis = new FileInputStream(file);
			input = new BufferedInputStream(fis);
			CharsetDecoder decoder = charset.newDecoder().reset();
			byte[] bytes = new byte[1024];
			boolean identified = false;
			while (input.read(bytes) != -1 && !identified) {
				identified = identify(bytes, decoder);
			}
			if (!identified) {
				return null;
			}
			return charset;
		} catch (Exception e) {
			return null;
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static byte[] readFileToByte(File file) {
		FileInputStream fis = null;
		ByteArrayOutputStream baos = null;
		try {
			fis = new FileInputStream(file);
			baos = new ByteArrayOutputStream();
			byte[] bytes = new byte[1024];
			int length;
			while ((length = fis.read(bytes)) > 0) {
				baos.write(bytes, 0, length);
			}
			return baos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (baos != null) {
				try {
					baos.flush();
					baos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	private static boolean identify(byte[] bytes, CharsetDecoder decoder) {
		try {
			decoder.decode(ByteBuffer.wrap(bytes));
			return true;
		} catch (CharacterCodingException e) {
			return false;
		}
	}

	private static String removeBom(String line, boolean isUTF8) {
		if (isUTF8 && line.startsWith("\uFEFF")) {
			return line.substring(1);
		}
		return line;
	}

	private static void renameToEncode(Plugin plugin, File file, boolean isUTF8) {
		File data = plugin.getDataFolder();
		File temp = new File(data, "✉☜☽☃☀〠☁.yml");
		fileEncode(file, temp, isUTF8);
		file.delete();
		temp.renameTo(file);
	}
}