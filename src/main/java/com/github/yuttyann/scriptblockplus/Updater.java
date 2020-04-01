package com.github.yuttyann.scriptblockplus;

import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.utils.FileUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ScriptBlockPlus Updater クラス
 * @author yuttyann44581
 */
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

	public Updater(@NotNull Plugin plugin) {
		this.plugin = plugin;
		this.pluginName = plugin.getName();
		this.pluginVersion = plugin.getDescription().getVersion();
	}

	@NotNull
	public Plugin getPlugin() {
		return plugin;
	}

	@NotNull
	public String getJarName() {
		return pluginName + " v" + latestVersion + ".jar";
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
		Document document = getDocument(pluginName);
		Element root = document.getDocumentElement();
		NodeList rootChildren = root.getChildNodes();
		for (int i = 0; i < rootChildren.getLength(); i++) {
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
				element = (Element) updateNode;
				switch (element.getNodeName()) {
				case "download":
					downloadURL = element.getAttribute("url");
					break;
				case "changelog":
					changeLogURL = element.getAttribute("url");
					break;
				case "details":
					NodeList detailsChildren = updateNode.getChildNodes();
					details = new ArrayList<>(detailsChildren.getLength());
					for (int k = 0; k < detailsChildren.getLength(); k++) {
						Node detailsNode = detailsChildren.item(k);
						if (detailsNode.getNodeType() == Node.ELEMENT_NODE) {
							element = (Element) detailsNode;
							details.add(element.getAttribute("info"));
						}
					}
				}
			}
		}
		isUpperVersion = Utils.getVersionInt(latestVersion) > Utils.getVersionInt(pluginVersion);
	}

	public boolean execute(@NotNull CommandSender sender) {
		if (SBConfig.UPDATE_CHECKER.getValue() && isUpperVersion) {
			if (!isUpdateError) {
				SBConfig.UPDATE_CHECK.replace(pluginName, latestVersion, details).send(sender);
			}
			File dataFolder = Files.getConfig().getDataFolder();
			File logFile = new File(dataFolder, "update/ChangeLog.txt");
			boolean logEquals = !logFile.exists() || !logEquals(changeLogURL, logFile);
			if (SBConfig.AUTO_DOWNLOAD.getValue()) {
				File jarFile = new File(dataFolder, "update/jar/" + getJarName());
				try {
					SBConfig.UPDATE_DOWNLOAD_START.send(sender);
					FileUtils.fileDownload(changeLogURL, logFile);
					FileUtils.fileDownload(downloadURL, jarFile);
				} catch (IOException e) {
					if (!isUpdateError && (isUpdateError = true)) {
						SBConfig.ERROR_UPDATE.send(sender);
					}
				} finally {
					if (!isUpdateError && jarFile.exists()) {
						String fileName = jarFile.getName();
						String filePath = StringUtils.replace(jarFile.getPath(), "\\", "/");
						SBConfig.UPDATE_DOWNLOAD_END.replace(fileName, filePath, getSize(jarFile.length())).send(sender);
					}
				}
			}
			if (SBConfig.OPEN_CHANGE_LOG.getValue() && !isUpdateError && logEquals) {
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

	private String getSize(long length) {
		double b = 1024D;
		if (b > length) {
			return length + " Byte";
		}
		String unit = b * b > length ? " KB" : " MB";
		double size = unit.endsWith("KB") ? length / b : length / b / b;
		return new BigDecimal(size).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue() + unit;
	}

	private boolean logEquals(@NotNull String url, @NotNull File file) {
		if (!file.exists()) {
			return false;
		}
		try (
				FileReader fr = new FileReader(file);
				InputStream is = new URL(url).openStream(); InputStreamReader isr = new InputStreamReader(is);
				BufferedReader reader1 = new BufferedReader(fr); BufferedReader reader2 = new BufferedReader(isr)
			) {
			while (reader1.ready() && reader2.ready()) {
				if (!reader1.readLine().equals(reader2.readLine())) {
					return false;
				}
			}
			return !(reader1.ready() || reader2.ready());
		} catch (IOException e) {
			return false;
		}
	}

	@NotNull
	private Document getDocument(@NotNull String name) throws ParserConfigurationException, SAXException, IOException {
		InputStream is = FileUtils.getWebFile("https://xml.yuttyann44581.net/uploads/" + name + ".xml");
		return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(Objects.requireNonNull(is));
	}
}