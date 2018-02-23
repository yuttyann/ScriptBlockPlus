package com.github.yuttyann.scriptblockplus.script.option;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.manager.MapManager;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.ScriptData;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.utils.Utils;

/**
 * ベースオプション クラス
 * @author yuttyann44581
 */
public abstract class BaseOption extends Option {

	private ScriptRead scriptRead;

	/**
	 * コンストラクタ
	 * @param name
	 * @param syntax
	 */
	public BaseOption(String name, String syntax) {
		super(name, syntax);
	}

	/**
	 * プラグインを取得する
	 * @return プラグイン
	 */
	protected final Plugin getPlugin() {
		return scriptRead.getPlugin();
	}

	/**
	 * プレイヤーを取得する
	 * @return プレイヤー
	 */
	protected final Player getPlayer() {
		return getSBPlayer().getPlayer();
	}

	/**
	 * SBプレイヤーを取得する
	 * @return SBプレイヤー
	 */
	protected final SBPlayer getSBPlayer() {
		return scriptRead.getSBPlayer();
	}

	/**
	 * UUIDを取得する
	 * @return UUID
	 */
	protected final UUID getUniqueId() {
		return getSBPlayer().getUniqueId();
	}

	/**
	 * オプションの値を取得する
	 * @return オプションの値
	 */
	protected final String getOptionValue() {
		return scriptRead.getOptionValue();
	}

	/**
	 * 座標文字列を取得する
	 * @return ワールド名を除いた、文字列(x, y, z)
	 */
	protected final String getCoords() {
		return scriptRead.getCoords();
	}

	/**
	 * 座標文字列を取得する
	 * @return ワールド名を含めた、文字列(world, x, y, z)
	 */
	protected final String getFullCoords() {
		return scriptRead.getFullCoords();
	}

	/**
	 * 座標を取得する</br>
	 * ※座標変更不可
	 * @return スクリプトの座標
	 */
	protected final Location getLocation() {
		return scriptRead.getLocation();
	}

	/**
	 * マップの管理クラスを取得する
	 * @return マップの管理クラス
	 */
	protected final MapManager getMapManager() {
		return scriptRead.getMapManager();
	}

	/**
	 * スクリプトのリストを取得する
	 * @return スクリプトのリスト
	 */
	protected final List<String> getScripts() {
		return scriptRead.getScripts();
	}

	/**
	 * スクリプトの種類を取得する
	 * @return スクリプトの種類
	 */
	protected final ScriptType getScriptType() {
		return scriptRead.getScriptType();
	}

	/**
	 * スクリプトの実行クラスを取得する
	 * @return スクリプトの実行クラス
	 */
	protected final ScriptRead getScriptRead() {
		return scriptRead;
	}

	/**
	 * スクリプトの管理クラスを取得する
	 * @return スクリプトの管理クラス
	 */
	protected final ScriptData getScriptData() {
		return scriptRead.getScriptData();
	}

	/**
	 * スクリプト読み込むの進行度を取得する
	 * @return 進行度
	 */
	protected final int getScriptIndex() {
		return scriptRead.getScriptIndex();
	}

	/**
	 * オプションの処理を実行する
	 * @return 実行が成功したかどうか
	 */
	protected abstract boolean isValid() throws Exception;

	@Override
	@Deprecated
	public final boolean callOption(ScriptRead scriptRead) {
		this.scriptRead = scriptRead;
		try {
			return isValid();
		} catch (Exception e) {
			e.printStackTrace();
			Utils.sendMessage(getSBPlayer(), SBConfig.getOptionFailedToExecuteMessage(this, e));
		}
		return false;
	}

	/**
	 * コンソールからコマンドを実行する</br>
	 * @param command
	 * @return 実行が成功したかどうか
	 */
	protected final boolean executeConsoleCommand(String command) {
		return Utils.dispatchCommand(Bukkit.getConsoleSender(), new BlockCoords(getLocation()), command);
	}

	/**
	 * プレイヤーからコマンドを実行する</br>
	 * @param player
	 * @param command
	 * @param isBypass
	 * @return 実行が成功したかどうか
	 */
	protected final boolean executeCommand(Player player, String command, boolean isBypass) {
		Location location = new BlockCoords(getLocation());
		if (!isBypass || player.isOp()) {
			return Utils.dispatchCommand(player, location, command);
		} else {
			try {
				player.setOp(true);
				return Utils.dispatchCommand(player, location, command);
			} finally {
				player.setOp(false);
			}
		}
	}
}