package com.github.yuttyann.scriptblockplus.manager;

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
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class FileManager {

	private static final String UTF8 = "UTF-8";
	private static final String MS932 = "MS932";

	protected void fileEncode(File file) {
		if (!file.exists()) {
			return;
		}
		boolean isUTF8 = isUTF8(readFileToByte(file));
		boolean isCB19orLater = Utils.isCB19orLater();
		if (isUTF8 && !isCB19orLater) {
			if (!Utils.isWindows()) {
				return;
			}
			renameToEncode(file, true);
		} else if (!isUTF8 && isCB19orLater) {
			renameToEncode(file, false);
		}
	}

	protected void fileEncode(File sourceFile, File targetFile) {
		fileEncode(sourceFile, targetFile, isUTF8(readFileToByte(sourceFile)));
	}

	protected void fileEncode(File sourceFile, File targetFile, boolean isUTF8) {
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

	protected void copyFileFromJar(File jarFile, File targetFile, String sourceFilePath) {
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

	protected void copyFolderFromJar(File jarFile, File targetFilePath, String sourceFilePath) {
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

	protected void fileDownload(String url, File targetFile) throws IOException {
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
			byte[] buf = new byte[4096];
			int readByte = 0;
			while ((readByte = input.read(buf)) != -1) {
				output.write(buf, 0, readByte);
			}
		} catch (FileNotFoundException e) {
			throw e;
		} catch (ProtocolException e) {
			throw e;
		} catch (MalformedURLException e) {
			throw e;
		} catch (IOException e) {
			throw e;
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

	protected ArrayList<String> getTextList(File file) {
		FileReader fileReader = null;
		BufferedReader buReader = null;
		try {
			fileReader = new FileReader(file);
			buReader = new BufferedReader(fileReader);
			ArrayList<String> list = new ArrayList<String>();
			String line;
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

	protected ArrayList<String> getTextList(String url) {
		InputStream input = null;
		InputStreamReader inReader = null;
		BufferedReader buReader = null;
		try {
			input = new URL(url).openStream();
			inReader = new InputStreamReader(input);
			buReader = new BufferedReader(inReader);
			String line;
			ArrayList<String> list = new ArrayList<String>();
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

	protected Document getDocument(String name) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		Document document = null;
		try {
			builder = factory.newDocumentBuilder();
			document = builder.parse(getInputStream(name));
		} catch (ParserConfigurationException e) {
			throw e;
		} catch (SAXException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		}
		return document;
	}

	protected InputStream getInputStream(String name) throws IOException {
		try {
			String url = "http://xml.yuttyann44581.net/uploads//" + name + ".xml";
			URLConnection urlconn = new URL(url).openConnection();
			HttpURLConnection httpconn = (HttpURLConnection) urlconn;
			httpconn.setAllowUserInteraction(false);
			httpconn.setInstanceFollowRedirects(true);
			httpconn.setRequestMethod("GET");
			httpconn.connect();
			int httpStatusCode = httpconn.getResponseCode();
			if (httpStatusCode != HttpURLConnection.HTTP_OK) {
				httpconn.disconnect();
				return null;
			} else {
				return httpconn.getInputStream();
			}
		} catch (FileNotFoundException e) {
			throw e;
		} catch (ProtocolException e) {
			throw e;
		} catch (MalformedURLException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		}
	}

	protected byte[] readFileToByte(File file) {
		FileInputStream fis = null;
		ByteArrayOutputStream baos = null;
		try {
			fis = new FileInputStream(file);
			baos = new ByteArrayOutputStream();
			byte[] b = new byte[1];
			while (fis.read(b) > 0) {
				baos.write(b);
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

	protected boolean isUTF8(byte[] fileByte) {
		try {
			byte[] temp = new String(fileByte, UTF8).getBytes(UTF8);
			return Arrays.equals(temp, fileByte);
		} catch (UnsupportedEncodingException e) {
			return false;
		}
	}

	protected boolean isMS932(byte[] fileByte) {
		try {
			byte[] temp = new String(fileByte, MS932).getBytes(MS932);
			return Arrays.equals(temp, fileByte);
		} catch (UnsupportedEncodingException e) {
			return false;
		}
	}

	protected String removeBom(String line, boolean isUTF8) {
		if (isUTF8 && line.startsWith("\uFEFF")) {
			return line.substring(1);
		}
		return line;
	}

	protected void renameToEncode(File file, boolean isUTF8) {
		File data = ScriptBlock.instance.getDataFolder();
		File temp = new File(data, "☃.yml");
		fileEncode(file, temp, isUTF8);
		file.delete();
		temp.renameTo(file);
	}
}