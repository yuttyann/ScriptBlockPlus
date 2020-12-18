package com.github.yuttyann.scriptblockplus;

import com.github.yuttyann.scriptblockplus.command.BaseCommand;
import com.github.yuttyann.scriptblockplus.command.ScriptBlockPlusCommand;
import com.github.yuttyann.scriptblockplus.file.SBFiles;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.json.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.hook.plugin.VaultEconomy;
import com.github.yuttyann.scriptblockplus.listener.*;
import com.github.yuttyann.scriptblockplus.listener.item.ItemAction;
import com.github.yuttyann.scriptblockplus.listener.item.action.BlockSelector;
import com.github.yuttyann.scriptblockplus.listener.item.action.ScriptEditor;
import com.github.yuttyann.scriptblockplus.listener.item.action.ScriptViewer;
import com.github.yuttyann.scriptblockplus.manager.APIManager;
import com.github.yuttyann.scriptblockplus.player.BaseSBPlayer;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus ScriptBlock メインクラス
 * @author yuttyann44581
 */
public class ScriptBlock extends JavaPlugin {

	private Updater updater;
	private ScriptBlockAPI scriptAPI;

	@Override
	public void onEnable() {
		// 1.9未満のバージョンだった場合はプラグインを無効化
		if (!Utils.isCBXXXorLater("1.9")) {
			Bukkit.getConsoleSender().sendMessage("§cUnsupported Version: v" + Utils.getServerVersion());
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		// 旧ScriptBlockが導入されていた場合は無効化
		Plugin plugin = getServer().getPluginManager().getPlugin("ScriptBlock");
		if (plugin != null) {
			getServer().getPluginManager().disablePlugin(plugin);
		}

		// 全ファイルの読み込み
		SBFiles.reload();

		// Vaultが導入されているのか確認
		if (!VaultEconomy.INSTANCE.has()) {
			SBConfig.NOT_VAULT.send();
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		// アップデート処理
		updater = new Updater(this);
		checkUpdate(Bukkit.getConsoleSender(), false);

		// スクリプトの形式を".yml"から".json"へ移行
		StreamUtils.forEach(ScriptType.values(), BlockScriptJson::convart);

		// ログイン中のプレイヤーの設定をオンラインへ変更
		Bukkit.getOnlinePlayers().forEach(p -> ((BaseSBPlayer) SBPlayer.fromPlayer(p)).setOnline(true));

		// リスナーの登録
		getServer().getPluginManager().registerEvents(new InteractListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		getServer().getPluginManager().registerEvents(new ScriptInteractListener(this), this);
		getServer().getPluginManager().registerEvents(new ScriptBreakListener(this), this);
		getServer().getPluginManager().registerEvents(new ScriptWalkListener(this), this);

		// アイテムアクションの登録
		ItemAction.register(new ScriptViewer());
		ItemAction.register(new ScriptEditor());
		ItemAction.register(new BlockSelector());

		// コマンドの登録
		BaseCommand.register("scriptblockplus", new ScriptBlockPlusCommand(this));
	}

	/**
	 * 最新のバージョンが存在するか確認します。
	 * @param sender 送信先
	 * @param latestMessage trueの場合は送信先にアップデートのメッセージを表示します。
	 */
	public void checkUpdate(@NotNull CommandSender sender, boolean latestMessage) {
		Thread thread = new Thread(() -> {
			try {
				updater.init();
				updater.load();
				if (!updater.run(sender) && latestMessage) {
					SBConfig.NOT_LATEST_PLUGIN.send(sender);
				}
			} catch (Exception e) {
				e.printStackTrace();
				SBConfig.ERROR_UPDATE.send(sender);
			}
		});
		try {
			thread.setName("Update Thread : " + Utils.getPluginName(this));
			thread.setPriority(Thread.MIN_PRIORITY);
			thread.start();
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * APIを取得します。
	 * @return {@link ScriptBlockAPI}
	 */
	@NotNull
	public ScriptBlockAPI getAPI() {
		return scriptAPI == null ? scriptAPI = new APIManager(this) : scriptAPI;
	}

	/**
	 * ScriptBlockのインスタンスを取得します。
	 * @return メインクラスのインスタンス
	 */
	@NotNull
	public static ScriptBlock getInstance() {
		return getPlugin(ScriptBlock.class);
	}
}