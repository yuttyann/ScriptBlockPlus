package com.github.yuttyann.scriptblockplus;

import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.utils.FileUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.apache.commons.lang.text.StrBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
import java.util.Collections;
import java.util.List;

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
	public String getPluginName() {
		return pluginName;
	}

	@NotNull
	public String getPluginVersion() {
		return pluginVersion;
	}

	@NotNull
	public String getJarName() {
		return pluginName + " v" + latestVersion + ".jar";
	}

	@NotNull
	public String getLatestVersion() {
		return latestVersion;
	}

	@NotNull
	public String getDownloadURL() {
		return downloadURL;
	}

	@NotNull
	public String getChangeLogURL() {
		return changeLogURL;
	}

	@NotNull
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
		load();
		this.isUpperVersion = isUpperVersion;
		if (isError) {
			sendErrorMessage(Bukkit.getConsoleSender());
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

	public boolean execute(@Nullable CommandSender sender) {
		if (SBConfig.UPDATE_CHECKER.toBool() && isUpperVersion) {
			if (sender == null) {
				sender = Bukkit.getConsoleSender();
			}
			sendCheckMessage(sender);
			File dataFolder = Files.getConfig().getDataFolder();
			File logFile = new File(dataFolder, "update/ChangeLog.txt");
			boolean logEquals = !logFile.exists() || !textEquals(changeLogURL, logFile);
			if (SBConfig.AUTO_DOWNLOAD.toBool()) {
				File jarFile = new File(dataFolder, "update/jar/" + getJarName());
				try {
					SBConfig.UPDATE_DOWNLOAD_START.send();
					FileUtils.fileDownload(changeLogURL, logFile);
					FileUtils.fileDownload(downloadURL, jarFile);
				} catch (IOException e) {
					sendErrorMessage(sender);
				} finally {
					if (!isUpdateError && jarFile.exists()) {
						String fileName = jarFile.getName();
						String filePath = StringUtils.replace(jarFile.getPath(), "\\", "/");
						SBConfig.UPDATE_DOWNLOAD_END.replace(fileName, filePath, getSize(jarFile.length())).send();
					}
				}
			}
			if (SBConfig.OPEN_CHANGE_LOG.toBool() && !isUpdateError && logEquals) {
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

	public void sendCheckMessage(@NotNull CommandSender sender) {
		if (isUpperVersion && !isUpdateError && sender.isOp()) {
			StrBuilder builder = new StrBuilder(details.size());
			for (int i = 0; i < details.size(); i++) {
				boolean isTree = details.get(i).startsWith("$");
				String info = StringUtils.removeStart(details.get(i), "$");
				builder.append(isTree ? "  - " : "・").append(info).append(i == (details.size() - 1) ? "" : "|~");
			}
			SBConfig.UPDATE_CHECK.replace(pluginName, latestVersion, builder.toString()).send(sender);
		}
	}

	public void sendErrorMessage(@NotNull CommandSender sender) {
		if (!isUpdateError && (isUpdateError = true)) {
			SBConfig.ERROR_UPDATE.send(sender);
		}
	}

	private String getSize(long length) {
		if (1024 > length) {
			return length + " Byte";
		}
		double size = 1024 * 1024 > length ? length / 1024 : length / 1024 / 1024;
		String unit = 1024 * 1024 > length ? " KB" : " MB";
		return new BigDecimal(size).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue() + unit;
	}

	private boolean textEquals(@NotNull String url, @NotNull File file) {
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

	private Document getDocument(@NotNull String name) throws ParserConfigurationException, SAXException, IOException {
		InputStream is = FileUtils.getWebFile("https://xml.yuttyann44581.net/uploads/" + name + ".xml");
		return is == null ? null : DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
	}
}