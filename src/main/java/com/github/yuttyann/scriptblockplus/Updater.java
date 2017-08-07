package com.github.yuttyann.scriptblockplus;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.Lang;
import com.github.yuttyann.scriptblockplus.file.YamlConfig;
import com.github.yuttyann.scriptblockplus.utils.FileUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class Updater {

	private String pluginName;
	private String pluginVersion;
	private String latestVersion;
	private String downloadURL;
	private String changeLogURL;
	private List<String> details;
	private boolean isUpperVersion;
	private boolean isError;

	public Updater(Plugin plugin) {
		this.pluginName = plugin.getName();
		this.pluginVersion = plugin.getDescription().getVersion();
	}

	public String getPluginName() {
		return pluginName;
	}

	public String getPluginVersion() {
		return pluginVersion;
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

	public boolean isUpperVersion() {
		return isUpperVersion;
	}

	public boolean isError() {
		return isError;
	}

	public void debug(boolean isUpperVersion, boolean isError) {
		try {
			load();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.isUpperVersion = isUpperVersion;
		this.isError = isError;
		check(null);
	}

	public void init() {
		isError = false;
		details = new ArrayList<String>();
	}

	public void load() throws Exception {
		init();
		Document document = getDocument();
		Element root = document.getDocumentElement();
		NodeList rootChildren = root.getChildNodes();
		for(int i = 0; i < rootChildren.getLength(); i++) {
			Node node = rootChildren.item(i);
			if (node.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			Element element = (Element) node;
			if (!element.getNodeName().equals("update")) {
				continue;
			}
			latestVersion = element.getAttribute("version");
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
						details.add(((Element) detailsNode).getAttribute("info"));
					}
				}
			}
		}
		isUpperVersion = vInt(getLatestVersion()) > vInt(getPluginVersion());
	}

	public boolean check(Player player) {
		YamlConfig config = Files.getConfig();
		if(config.getBoolean("UpdateChecker") && isUpperVersion()) {
			File data = config.getDataFolder();
			File changeLogFile = new File(data, "ChangeLog.txt");
			List<String> changeLog = new ArrayList<String>();
			if (changeLogFile.exists()) {
				changeLog = FileUtils.getFileText(changeLogFile);
			}
			sendCheckMessage(player != null ? player : Bukkit.getConsoleSender());
			boolean first = false;
			if(config.getBoolean("AutoDownload")) {
				Utils.sendPluginMessage(Lang.getUpdateDownloadStartMessage());
				File downloadFile = null;
				try {
					first = !changeLogFile.exists();
					downloadFile = new File(data, "Downloads");
					if (!downloadFile.exists()) {
						downloadFile.mkdir();
					}
					downloadFile = new File(data, "Downloads/" + getPluginName() + " v" + getLatestVersion() + ".jar");
					FileUtils.fileDownload(getChangeLogURL(), changeLogFile);
					FileUtils.fileDownload(getDownloadURL(), downloadFile);
				} catch (IOException e) {
					e.printStackTrace();
					sendErrorMessage();
					return false;
				} finally {
					if (!isError()) {
						String fileName = downloadFile.getName();
						String filePath = StringUtils.replace(downloadFile.getPath(), "\\", "/");
						for (String message : Lang.getUpdateDownloadEndMessages(fileName, filePath, getSize(downloadFile.length()))) {
							Utils.sendPluginMessage(message);
						}
					}
				}
			}
			if (config.getBoolean("OpenTextFile") && !isError()) {
				if (!first && changeLog.equals(fText(getChangeLogURL()))) {
					return true;
				}
				Desktop desktop = Desktop.getDesktop();
				try {
					desktop.open(changeLogFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return true;
		}
		return false;
	}

	private String getSize(long size) {
		if (1024 > size) {
			return size + " Byte";
		} else if (1024 * 1024 > size) {
			double dsize = size / 1024;
			BigDecimal decimal = new BigDecimal(dsize);
			double value = decimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
			return value + " KB";
		} else {
			double dsize = size / 1024 / 1024;
			BigDecimal decimal = new BigDecimal(dsize);
			double value = decimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
			return value + " MB";
		}
	}

	public void sendCheckMessage(CommandSender sender) {
		if(isUpperVersion() && !isError() && sender.isOp()) {
			for (String message : Lang.getUpdateCheckMessages(getPluginName(), getLatestVersion(), getDetails())) {
				Utils.sendPluginMessage(message);
			}
		}
	}

	private void sendErrorMessage() {
		if(!isError()) {
			isError = true;
			for (String message : Lang.getUpdateErrorMessages(getPluginName(), getLatestVersion())) {
				Utils.sendPluginMessage(message);
			}
		}
	}

	private int vInt(String version) {
		return Utils.getVersionInt(version);
	}

	private List<String> fText(String url) {
		return FileUtils.getFileText(url);
	}

	private Document getDocument() throws ParserConfigurationException, SAXException, IOException {
		return FileUtils.getDocument(getPluginName());
	}
}