package com.github.yuttyann.scriptblockplus.script.option;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.manager.MapManager;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.SBRead;
import com.github.yuttyann.scriptblockplus.script.ScriptData;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * ベースオプション クラス
 * @author yuttyann44581
 */
public abstract class BaseOption extends Option {

	private SBRead sbRead;

	/**
	 * コンストラクタ
	 * @param name オプション名　[（例） example]
	 * @param syntax オプション構文　[（例） @example: ]
	 */
	public BaseOption(@NotNull String name, @NotNull String syntax) {
		super(name, syntax);
	}

	/**
	 * プラグインを取得する
	 * @return プラグイン
	 */
	@NotNull
	protected final Plugin getPlugin() {
		return sbRead.getPlugin();
	}

	/**
	 * プレイヤーを取得する
	 * @return プレイヤー
	 */
	@NotNull
	protected final Player getPlayer() {
		return getSBPlayer().getPlayer();
	}

	/**
	 * SBプレイヤーを取得する
	 * @return SBPlayer
	 */
	@NotNull
	protected final SBPlayer getSBPlayer() {
		return sbRead.getSBPlayer();
	}

	/**
	 * UUIDを取得する
	 * @return UUID
	 */
	@NotNull
	protected final UUID getUniqueId() {
		return getSBPlayer().getUniqueId();
	}

	/**
	 * オプションの値を取得する
	 * @return オプションの値
	 */
	@NotNull
	protected final String getOptionValue() {
		return sbRead.getOptionValue();
	}

	/**
	 * 座標文字列を取得する
	 * @return ワールド名を除いた、文字列(x, y, z)
	 */
	@NotNull
	protected final String getCoords() {
		return sbRead.getCoords();
	}

	/**
	 * 座標文字列を取得する
	 * @return ワールド名を含めた、文字列(world, x, y, z)
	 */
	@NotNull
	protected final String getFullCoords() {
		return sbRead.getFullCoords();
	}

	/**
	 * 座標を取得する</br>
	 * ※座標変更不可
	 * @return スクリプトの座標
	 */
	@NotNull
	protected final Location getLocation() {
		return sbRead.getLocation();
	}

	/**
	 * マップの管理クラスを取得する
	 * @return マップの管理クラス
	 */
	@NotNull
	protected final MapManager getMapManager() {
		return ScriptBlock.getInstance().getMapManager();
	}

	/**
	 * スクリプトのリストを取得する
	 * @return スクリプトのリスト
	 */
	@NotNull
	protected final List<String> getScripts() {
		return sbRead.getScripts();
	}

	/**
	 * スクリプトの種類を取得する
	 * @return スクリプトの種類
	 */
	@NotNull
	protected final ScriptType getScriptType() {
		return sbRead.getScriptType();
	}

	/**
	 * スクリプトの実行クラスを取得する
	 * @return スクリプトの実行クラス
	 */
	@NotNull
	protected final SBRead getSBRead() {
		return sbRead;
	}

	/**
	 * スクリプトの管理クラスを取得する
	 * @return スクリプトの管理クラス
	 */
	@NotNull
	protected final ScriptData getScriptData() {
		return sbRead.getScriptData();
	}

	/**
	 * スクリプト読み込むの進行度を取得する
	 * @return 進行度
	 */
	protected final int getScriptIndex() {
		return sbRead.getScriptIndex();
	}

	/**
	 * オプションの処理を実行する
	 * @return 実行が成功したかどうか
	 */
	protected abstract boolean isValid() throws Exception;

	/**
	 * オプションを実行する
	 * @param sbRead スクリプト読み込みクラス
	 * @return 実行が成功したかどうか
	 */
	@Override
	@Deprecated
	public final boolean callOption(@NotNull SBRead sbRead) {
		this.sbRead = sbRead;
		try {
			return isValid();
		} catch (Exception e) {
			e.printStackTrace();
			String cause = e.getClass().getSimpleName() + (e.getMessage() == null ? "" : " \"" + e.getMessage() + "\"");
			SBConfig.OPTION_FAILED_TO_EXECUTE.replace(getName(), cause).send(getSBPlayer());
		}
		return false;
	}

	/**
	 * コンソールからコマンドを実行する
	 * @param command コマンド
	 * @return 実行が成功したかどうか
	 */
	protected final boolean executeConsoleCommand(@NotNull String command) {
		return Utils.dispatchCommand(Bukkit.getConsoleSender(), getLocation(), command);
	}

	/**
	 * プレイヤーからコマンドを実行する
	 * @param player プレイヤー
	 * @param command コマンド
	 * @param isBypass 権限を無視して実行するかどうか
	 * @return 実行が成功したかどうか
	 */
	protected final boolean executeCommand(@NotNull Player player, @NotNull String command, boolean isBypass) {
		Location location = getLocation();
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