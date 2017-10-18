package com.github.yuttyann.scriptblockplus;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
			boolean textEquals = !textFile.exists() || !textEquals(changeLogURL, textFile);
			if(SBConfig.isAutoDownload()) {
				File jarFile = new File(dataFolder, "update/jar/" + getJarName());
				try {
					Utils.sendMessage(SBConfig.getUpdateDownloadStartMessage());
					FileUtils.fileDownload(changeLogURL, textFile);
					FileUtils.fileDownload(downloadURL, jarFile);
				} catch (IOException e) {
					sendErrorMessage();
					return false;
				} finally {
					if (!isUpdateError && jarFile.exists()) {
						String fileName = jarFile.getName();
						String filePath = StringUtils.replace(jarFile.getPath(), "\\", "/");
						Utils.sendMessage(SBConfig.getUpdateDownloadEndMessage(fileName, filePath, getSize(jarFile.length())));
					}
				}
			}
			if (SBConfig.isOpenTextFile() && !isUpdateError && textEquals) {
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

	private boolean textEquals(String url, File file) {
		if (!file.exists()) {
			return false;
		}
		BufferedReader reader1 = null, reader2 = null;
		try {
			reader1 = new BufferedReader(new FileReader(file));
			reader2 = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
			while (reader1.ready() && reader2.ready()) {
				if (!reader1.readLine().equals(reader2.readLine())) {
					return false;
				}
			}
			return !(reader1.ready() || reader2.ready());
		} catch (IOException e) {
			return false;
		} finally {
			if (reader1 != null) {
				try {
					reader1.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (reader2 != null) {
				try {
					reader2.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
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