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
import java.util.ArrayList;
import java.util.Collections;
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
		return Collections.unmodifiableList(details);
	}

	public boolean isUpdateError() {
		return isUpdateError;
	}

	public boolean isUpperVersion() {
		return isUpperVersion;
	}

	public void debug(boolean isUpperVersion, boolean isError) throws Exception {
		try {
			load();
		} catch (Exception e) {
			throw e;
		}
		this.isUpperVersion = isUpperVersion;
		if (isError) {
			sendErrorMessage(Bukkit.getConsoleSender());
		}
		execute(null);
	}

	@Deprecated
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
					if (details == null) {
						details = new ArrayList<String>(detailsChildren.getLength());
					}
					for(int k = 0; k < detailsChildren.getLength(); k++) {
						Node detailsNode = detailsChildren.item(k);
						if (detailsNode.getNodeType() == Node.ELEMENT_NODE) {
							details.add(((Element) detailsNode).getAttribute("info"));
						}
					}
				}
			}
		}
		isUpperVersion = Utils.getVersionInt(latestVersion) > Utils.getVersionInt(pluginVersion);
	}

	public boolean execute(CommandSender sender) {
		if(SBConfig.isUpdateChecker() && isUpperVersion) {
			if (sender == null) {
				sender = Bukkit.getConsoleSender();
			}
			sendCheckMessage(sender);
			File dataFolder = Files.getConfig().getDataFolder();
			File logFile = new File(dataFolder, "update/ChangeLog.txt");
			boolean logEquals = !logFile.exists() || !textEquals(changeLogURL, logFile);
			if(SBConfig.isAutoDownload()) {
				File jarFile = new File(dataFolder, "update/jar/" + getJarName());
				try {
					Utils.sendMessage(SBConfig.getUpdateDownloadStartMessage());
					FileUtils.fileDownload(changeLogURL, logFile);
					FileUtils.fileDownload(downloadURL, jarFile);
				} catch (IOException e) {
					sendErrorMessage(sender);
				} finally {
					if (!isUpdateError && jarFile.exists()) {
						String fileName = jarFile.getName();
						String filePath = StringUtils.replace(jarFile.getPath(), "\\", "/");
						Utils.sendMessage(SBConfig.getUpdateDownloadEndMessage(fileName, filePath, getSize(jarFile.length())));
					}
				}
			}
			if (SBConfig.isOpenChangeLog() && !isUpdateError && logEquals) {
				Desktop desktop = Desktop.getDesktop();
				try {
					desktop.open(logFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return true;
		}
		return false;
	}

	public void sendCheckMessage(CommandSender sender) {
		if(isUpperVersion && !isUpdateError && sender.isOp()) {
			Utils.sendMessage(sender, SBConfig.getUpdateCheckMessages(pluginName, latestVersion, details));
		}
	}

	private void sendErrorMessage(CommandSender sender) {
		if(!isUpdateError && (isUpdateError = true)) {
			Utils.sendMessage(sender, SBConfig.getUpdateErrorMessage());
		}
	}

	private String getSize(long length) {
		if (1024 > length) {
			return length + " Byte";
		}
		double size;
		String unit;
		if (1024 * 1024 > length) {
			size = length / 1024;
			unit = " KB";
		} else {
			size = length / 1024 / 1024;
			unit = " MB";
		}
		int round = BigDecimal.ROUND_HALF_UP;
		return new BigDecimal(size).setScale(1, round).doubleValue() + unit;
	}

	private boolean textEquals(String url, File file) {
		if (!file.exists()) {
			return false;
		}
		BufferedReader reader1 = null;
		BufferedReader reader2 = null;
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
		URL url = new URL("https://xml.yuttyann44581.net/uploads/" + pluginName + ".xml");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setAllowUserInteraction(false);
		connection.setInstanceFollowRedirects(true);
		connection.connect();
		if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			return factory.newDocumentBuilder().parse(connection.getInputStream());
		}
		connection.disconnect();
		return null;
	}
}