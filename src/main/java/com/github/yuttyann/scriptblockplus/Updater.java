package com.github.yuttyann.scriptblockplus;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.YamlConfig;
import com.github.yuttyann.scriptblockplus.utils.FileUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class Updater {

	private Plugin plugin;
	private String pluginName;
	private String pluginVersion;
	private String updateVersion;
	private String download;
	private String changeLog;
	private String[] details;
	private boolean isUpperVersion;
	private boolean isEnable;
	private boolean isError;

	public Updater(Plugin plugin) {
		this.plugin = plugin;
	}

	public String getPluginName() {
		return pluginName;
	}

	public String getPluginVersion() {
		return pluginVersion;
	}

	public String getUpdateVersion() {
		return updateVersion;
	}

	public String getDownloadURL() {
		return download;
	}

	public String getChangeLogURL() {
		return changeLog;
	}

	public String[] getDetails() {
		return details;
	}

	public boolean isUpperVersion() {
		return isUpperVersion;
	}

	public boolean isEnable() {
		return isEnable;
	}

	public boolean isError() {
		return isError;
	}

	public void debug() {
		isUpperVersion = true;
	}

	public void load() throws Exception {
		isEnable = false;
		isError = false;
		pluginName = plugin.getName();
		pluginVersion = plugin.getDescription().getVersion();
		Document document = FileUtils.getDocument(getPluginName());
		Element root = document.getDocumentElement();
		NodeList rootChildren = root.getChildNodes();
		for(int i = 0, l = rootChildren.getLength(); i < l; i++) {
			Node node = rootChildren.item(i);
			if (node.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			Element element = (Element) node;
			if (!element.getNodeName().equals("update")) {
				continue;
			}
			updateVersion = element.getAttribute("version");
			NodeList updateChildren = node.getChildNodes();
			for (int j = 0, l2 = updateChildren.getLength(); j < l2; j++) {
				Node updateNode = updateChildren.item(j);
				if (updateNode.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				if (updateNode.getNodeName().equals("download")) {
					download = ((Element) updateNode).getAttribute("url");
				}
				if (updateNode.getNodeName().equals("changelog")) {
					changeLog = ((Element) updateNode).getAttribute("url");
				}
				if (updateNode.getNodeName().equals("details")) {
					NodeList detailsChildren = updateNode.getChildNodes();
					for(int k = 0, n = 0, l3 = detailsChildren.getLength(); k < l3; k++) {
						Node detailsNode = detailsChildren.item(k);
						if (detailsNode.getNodeType() != Node.ELEMENT_NODE) {
							continue;
						}
						if (n == 0) {
							details = new String[l3];
						}
						details[n++] = ((Element) detailsNode).getAttribute("info");
					}
				}
			}
		}
		isUpperVersion = vInt(getUpdateVersion()) > vInt(getPluginVersion());
	}

	public boolean check(Player player) {
		YamlConfig config = Files.getConfig();
		if(config.getBoolean("UpdateChecker") && isUpperVersion()) {
			isEnable = true;
			boolean first = false;
			File data = config.getDataFolder();
			File changeLogFile = new File(data, "更新履歴.txt");
			List<String> changeLog = new ArrayList<String>();
			if (changeLogFile.exists()) {
				changeLog = FileUtils.getFileText(changeLogFile);
			}
			sendCheckMessage(player != null ? player : Bukkit.getConsoleSender());
			if(config.getBoolean("AutoDownload")) {
				Utils.sendPluginMessage("§6最新のプラグインをダウンロードしています...");
				File downloadFile = null;
				try {
					first = !changeLogFile.exists();
					downloadFile = new File(data, "Downloads");
					if (!downloadFile.exists()) {
						downloadFile.mkdir();
					}
					downloadFile = new File(data, "Downloads/" + getPluginName() + " v" + getUpdateVersion() + ".jar");
					FileUtils.fileDownload(getChangeLogURL(), changeLogFile);
					FileUtils.fileDownload(getDownloadURL(), downloadFile);
				} catch (IOException e) {
					e.printStackTrace();
					sendErrorMessage();
				} finally {
					if (!isError()) {
						String prefix = "§6[" + downloadFile.getName() + "]";
						Utils.sendPluginMessage(prefix + " ダウンロードが終了しました。");
						Utils.sendPluginMessage(prefix + " ファイルサイズ: " + getSize(downloadFile.length()));
						Utils.sendPluginMessage(prefix + " ファイルパス: " + StringUtils.replace(downloadFile.getPath(), "\\", "/"));
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

	private void sendErrorMessage() {
		if(!isError()) {
			isError = true;
			Utils.sendPluginMessage("§cプラグイン名: " + getPluginName());
			Utils.sendPluginMessage("§cバージョン: v" + getUpdateVersion());
			Utils.sendPluginMessage("§c取得ファイル: http://xml.yuttyann44581.net/uploads//" + getPluginName() + ".xml/");
			Utils.sendPluginMessage("§c連絡用ページ: http://file.yuttyann44581.net/contact/");
			Utils.sendPluginMessage("§c解決しない場合は、製作者に連絡してください。");
		}
	}

	public void sendCheckMessage(CommandSender sender) {
		if(isUpperVersion() && !isError() && sender.isOp()) {
			Utils.sendPluginMessage(sender, "§b最新のバージョンが存在します。v" + getUpdateVersion() + "にアップデートしてください。");
			Utils.sendPluginMessage(sender, "§bプラグイン名: " + getPluginName());
			Utils.sendPluginMessage(sender, "§b☆アップデート内容☆");
			for (String content : getDetails()) {
				if (content == null) {
					continue;
				}
				Utils.sendPluginMessage(sender, "§b- " + content);
			}
		}
	}

	private int vInt(String version) {
		return Utils.getVersionInt(version);
	}

	private List<String> fText(String url) {
		return FileUtils.getFileText(url);
	}
}