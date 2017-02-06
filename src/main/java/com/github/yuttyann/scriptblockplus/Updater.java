package com.github.yuttyann.scriptblockplus;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.PluginYaml;
import com.github.yuttyann.scriptblockplus.file.YamlConfig;
import com.github.yuttyann.scriptblockplus.utils.FileUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class Updater extends FileUtils implements Listener {

	private String pluginName;
	private String pluginVersion;
	private String version;
	private String download;
	private String changelog;
	private String[] details;
	private boolean isEnable;
	private boolean isError;

	public Updater(ScriptBlock plugin) {
		try {
			setup();
			updateCheck();
		} catch (Exception e) {}
	}

	public String getPluginName() {
		return pluginName;
	}

	public String getPluginVersion() {
		return pluginVersion;
	}

	public String getVersion() {
		return version;
	}

	public String getDownloadURL() {
		return download;
	}

	public String getChangeLogURL() {
		return changelog;
	}

	public String[] getDetails() {
		return details;
	}

	public boolean isEnable() {
		return isEnable;
	}

	public boolean isError() {
		return isError;
	}

	private void setup() throws Exception {
		isEnable = false;
		isError = false;
		pluginName = PluginYaml.getName();
		pluginVersion = PluginYaml.getVersion();
		Document document = getDocument(getPluginName());
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
			version = element.getAttribute("version");
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
					changelog = ((Element) updateNode).getAttribute("url");
				}
				if (updateNode.getNodeName().equals("details")) {
					NodeList detailsChildren = updateNode.getChildNodes();
					for(int k = 0, n = 0, l3 = detailsChildren.getLength(); k < l3; k++, n++) {
						Node detailsNode = detailsChildren.item(k);
						if (detailsNode.getNodeType() != Node.ELEMENT_NODE) {
							continue;
						}
						if (details == null) {
							details = new String[l3];
						}
						details[n] = ((Element) detailsNode).getAttribute("info");
					}
				}
			}
		}
	}

	private void updateCheck() {
		YamlConfig config = Files.getConfig();
		if(config.getBoolean("UpdateChecker")
				&& Utils.getVersionInt(getVersion()) > Utils.getVersionInt(getPluginVersion())) {
			isEnable = true;
			boolean first = false;
			File data = config.getDataFolder();
			File changelogFile = new File(data, "更新履歴.txt");
			List<String> changelog = new ArrayList<String>();
			if (changelogFile.exists()) {
				changelog = getFileText(changelogFile);
			}
			sendCheckMessage(Bukkit.getConsoleSender());
			if(config.getBoolean("AutoDownload")) {
				Utils.sendPluginMessage("§6最新のプラグインをダウンロードしています...");
				if (!changelogFile.exists()) {
					first = true;
				}
				File downloadFile = null;
				try {
					downloadFile = new File(data, "Downloads");
					if (!downloadFile.exists()) {
						downloadFile.mkdir();
					}
					downloadFile = new File(data, "Downloads/" + getPluginName() + " v" + getVersion() + ".jar");
					fileDownload(getChangeLogURL(), changelogFile);
					fileDownload(getDownloadURL(), downloadFile);
				} catch (IOException e) {
					e.printStackTrace();
					sendErrorMessage();
				} finally {
					if (!isError()) {
						String prefix = "§6[" + downloadFile.getName() + "]";
						Utils.sendPluginMessage(prefix + " ダウンロードが終了しました。");
						Utils.sendPluginMessage(prefix + " ファイルサイズ: " + getSize(downloadFile.length()));
						Utils.sendPluginMessage(prefix + " ファイルパス: " + downloadFile.getPath().replace("\\", "/"));
					}
				}
			}
			if (config.getBoolean("OpenTextFile") && !isError()) {
				openTextFile(changelogFile, changelog, first);
			}
		}
	}

	private void openTextFile(File file, List<String> changelog, boolean first) {
		if (!first && changelog.equals(FileUtils.getFileText(getChangeLogURL()))) {
			return;
		}
		Desktop desktop = Desktop.getDesktop();
		try {
			desktop.open(new File(file.getPath()));
		} catch (IOException e) {
			e.printStackTrace();
		}
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
			Utils.sendPluginMessage("§cバージョン: v" + getVersion());
			Utils.sendPluginMessage("§c取得ファイル: http://xml.yuttyann44581.net/uploads//" + getPluginName() + ".xml/");
			Utils.sendPluginMessage("§c連絡用ページ: http://file.yuttyann44581.net/contact/");
			Utils.sendPluginMessage("§c解決しない場合は、製作者に連絡してください。");
		}
	}

	private void sendCheckMessage(CommandSender sender) {
		if(isEnable() && !isError()) {
			if (!sender.isOp()) {
				return;
			}
			Utils.sendPluginMessage(sender, "§b最新のバージョンが存在します。v" + getVersion() + "にアップデートしてください。");
			Utils.sendPluginMessage(sender, "§bプラグイン名: " + getPluginName());
			Utils.sendPluginMessage(sender, "§b☆アップデート内容☆");
			for (String content : getDetails()) {
				if (content != null) {
					Utils.sendPluginMessage(sender, "§b- " + content);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	private void onPlayerJoin(PlayerJoinEvent event) {
		sendCheckMessage(event.getPlayer());
	}
}