package com.github.yuttyann.scriptblockplus;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.utils.FileUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public final class Updater {

	private final Plugin plugin;
	private final String pluginName;
	private final String pluginVersion;
	private String latestVersion;
	private String downloadURL;
	private String changeLogURL;
	private List<String> details;
	private int versionDiff;
	private boolean isUpdateError;
	private boolean isUpperVersion;

	public Updater(Plugin plugin) {
		this.plugin = plugin;
		this.pluginName = plugin.getName();
		this.pluginVersion = plugin.getDescription().getVersion();
	}

	public Plugin getPlugin() {
		return plugin;
	}

	public String getPluginName() {
		return pluginName;
	}

	public String getPluginVersion() {
		return pluginVersion;
	}

	public String getJarName() {
		return pluginName + " v" + latestVersion + ".jar";
	}

	public String getLatestVersion() {
		return latestVersion;
	}

	public String getDownloadURL() {
		return downloadURL;
	}

	public String getChangeLogURL() {
		return changeLogURL;
	}

	public List<String> getDetails() {
		return details;
	}

	public int getVersionDiff() {
		return versionDiff;
	}

	public boolean isUpdateError() {
		return isUpdateError;
	}

	public boolean isUpperVersion() {
		return isUpperVersion;
	}

	public void debug(boolean isUpperVersion, boolean isError) {
		try {
			load();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.isUpperVersion = isUpperVersion;
		if (isError) {
			sendErrorMessage();
		}
		execute(null);
	}

	public void init() {
		latestVersion = null;
		downloadURL = null;
		changeLogURL = null;
		details = null;
		isUpdateError = false;
		isUpperVersion = false;
	}

	public void load() throws Exception {
		init();
		Document document = getDocument(pluginName);
		Element root = document.getDocumentElement();
		NodeList rootChildren = root.getChildNodes();
		for(int i = 0; i < rootChildren.getLength(); i++) {
			Node node = rootChildren.item(i);
			if (node.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			Element element = (Element) node;
			if (element.getNodeName().equals("update")) {
				latestVersion = element.getAttribute("version");
				if (element.hasAttribute("diff")) {
					versionDiff = Integer.valueOf(element.getAttribute("diff"));
				}
			}
			NodeList updateChildren = node.getChildNodes();
			for (int j = 0; j < updateChildren.getLength(); j++) {
				Node updateNode = updateChildren.item(j);
				if (updateNode.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				if (updateNode.getNodeName().equals("download")) {
					downloadURL = ((Element) updateNode).getAttribute("url");
				}
				if (updateNode.getNodeName().equals("changelog")) {
					changeLogURL = ((Element) updateNode).getAttribute("url");
				}
				if (updateNode.getNodeName().equals("details")) {
					NodeList detailsChildren = updateNode.getChildNodes();
					for(int k = 0; k < detailsChildren.getLength(); k++) {
						Node detailsNode = detailsChildren.item(k);
						if (detailsNode.getNodeType() != Node.ELEMENT_NODE) {
							continue;
						}
						if (details == null) {
							details = new ArrayList<String>(detailsChildren.getLength());
						}
						details.add(((Element) detailsNode).getAttribute("info"));
					}
				}
			}
		}
		isUpperVersion = Utils.getVersionInt(latestVersion) > Utils.getVersionInt(pluginVersion);
	}

	public boolean execute(CommandSender sender) {
		if(SBConfig.isUpdateChecker() && isUpperVersion) {
			sendCheckMessage(sender != null ? sender : Bukkit.getConsoleSender());
			File dataFolder = Files.getConfig().getDataFolder();
			File textFile = new File(dataFolder, "update/ChangeLog.txt");
			boolean isNotExists = !textFile.exists();
			List<String> contents = getContents(textFile);
			if(SBConfig.isAutoDownload()) {
				Utils.sendMessage(SBConfig.getUpdateDownloadStartMessage());
				File jarFile = null;
				try {
					jarFile = new File(dataFolder, "update/jar/" + getJarName());
					FileUtils.fileDownload(changeLogURL, textFile);
					FileUtils.fileDownload(downloadURL, jarFile);
					int diff = SBConfig.isOverwritePlugin() ? getDiff() : -1;
					System.out.println(diff + " <= " + versionDiff);
					if (diff != -1 && diff > 0 && diff <= versionDiff) {
						PluginManager pluginManager = Bukkit.getPluginManager();
						try {
							pluginManager.disablePlugin(plugin);
							FileUtils.fileOverwrite(jarFile, FileUtils.getJarFile(plugin));
						} finally {
							pluginManager.enablePlugin(plugin);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
					sendErrorMessage();
					return false;
				} finally {
					if (!isUpdateError && jarFile != null) {
						String fileName = jarFile.getName();
						String filePath = StringUtils.replace(jarFile.getPath(), "\\", "/");
						Utils.sendMessage(SBConfig.getUpdateDownloadEndMessage(fileName, filePath, getSize(jarFile.length())));
					}
				}
			}
			if (SBConfig.isOpenTextFile() && !isUpdateError && (isNotExists || !arrayEquals(contents))) {
				Desktop desktop = Desktop.getDesktop();
				try {
					desktop.open(textFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return true;
		}
		return false;
	}

	public String getSize(long length) {
		if (1024 > length) {
			return length + " Byte";
		}
		int round = BigDecimal.ROUND_HALF_UP;
		if (1024 * 1024 > length) {
			double size = length / 1024;
			double value = new BigDecimal(size).setScale(1, round).doubleValue();
			return value + " KB";
		} else {
			double size = length / 1024 / 1024;
			double value = new BigDecimal(size).setScale(1, round).doubleValue();
			return value + " MB";
		}
	}

	public void sendCheckMessage(CommandSender sender) {
		if(isUpperVersion && !isUpdateError && sender.isOp()) {
			Utils.sendMessage(sender, SBConfig.getUpdateCheckMessages(pluginName, latestVersion, details));
		}
	}

	public void sendErrorMessage() {
		if(!isUpdateError && (isUpdateError = true)) {
			Utils.sendMessage(SBConfig.getUpdateErrorMessage(pluginName, latestVersion));
		}
	}

	private int getDiff() {
		int source = Utils.getVersionInt(pluginVersion);
		int target = Utils.getVersionInt(latestVersion);
		return source < target ? target - source : 0;
	}

	private boolean arrayEquals(List<String> source) {
		if (source == null || StringUtils.isEmpty(changeLogURL)) {
			return false;
		}
		InputStream is = null;
		BufferedReader reader = null;
		List<String> result = new ArrayList<String>();
		try {
			is = new URL(changeLogURL).openStream();
			reader = new BufferedReader(new InputStreamReader(is));
			String line;
			while ((line = reader.readLine()) != null) {
				result.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
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
		return source.equals(result);
	}

	private List<String> getContents(File file) {
		if (file == null || !file.exists()) {
			return new ArrayList<String>();
		}
		BufferedReader reader = null;
		List<String> result = new ArrayList<String>();
		try {
			reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null) {
				result.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	private Document getDocument(String pluginName) throws ParserConfigurationException, SAXException, IOException {
		String url = "https://xml.yuttyann44581.net/uploads/" + pluginName + ".xml";
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
		}
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		return factory.newDocumentBuilder().parse(httpconn.getInputStream());
	}
}