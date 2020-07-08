package com.github.yuttyann.scriptblockplus.script.option;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
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
import java.util.Objects;
import java.util.UUID;

/**
 * ScriptBlockPlus BaseOption オプションクラス
 * @author yuttyann44581
 */
public abstract class BaseOption extends Option {

	private SBRead sbRead;

	/**
	 * コンストラクタ
	 * @param name オプション名 例:"example"
	 * @param syntax オプション構文 例:"@example: "
	 */
	public BaseOption(@NotNull String name, @NotNull String syntax) {
		super(name, syntax);
	}

	/**
	 * プラグインを取得します。
	 * @return プラグイン
	 */
	@NotNull
	protected final Plugin getPlugin() {
		return sbRead.getPlugin();
	}

	/**
	 * プレイヤーを取得します。
	 * @return プレイヤー
	 */
	@NotNull
	protected final Player getPlayer() {
		return Objects.requireNonNull(getSBPlayer().getPlayer());
	}

	/**
	 * SBPlayerを取得します。
	 * @return SBPlayer
	 */
	@NotNull
	protected final SBPlayer getSBPlayer() {
		return sbRead.getSBPlayer();
	}

	/**
	 * UUIDを取得します。
	 * @return プレイヤーのUUID
	 */
	@NotNull
	protected final UUID getUniqueId() {
		return getSBPlayer().getUniqueId();
	}

	/**
	 * オプションの値を取得します。
	 * @return オプションの値
	 */
	@NotNull
	protected final String getOptionValue() {
		return sbRead.getOptionValue();
	}

	/**
	 * ワールド名を除いた文字列の座標を取得します。
	 * @return ワールド名を除いた文字列(x, y, z)
	 */
	@NotNull
	protected final String getCoords() {
		return sbRead.getCoords();
	}

	/**
	 * ワールド名を含めた文字列の座標を取得します。
	 * @return ワールド名を含めた文字列(world, x, y, z)
	 */
	@NotNull
	protected final String getFullCoords() {
		return sbRead.getFullCoords();
	}

	/**
	 * 編集不可な座標を取得します。
	 * @return スクリプトの座標
	 */
	@NotNull
	protected final Location getLocation() {
		return sbRead.getLocation();
	}

	/**
	 * マップの管理クラスを取得します。
	 * @return マップの管理クラス
	 */
	@NotNull
	protected final MapManager getMapManager() {
		return ScriptBlock.getInstance().getMapManager();
	}

	/**
	 * スクリプトのリストを取得します。
	 * @return スクリプトのリスト
	 */
	@NotNull
	protected final List<String> getScripts() {
		return sbRead.getScripts();
	}

	/**
	 * スクリプトの種類を取得します。
	 * @return スクリプトの種類
	 */
	@NotNull
	protected final ScriptType getScriptType() {
		return sbRead.getScriptType();
	}

	/**
	 * スクリプトの実行クラスを取得します。
	 * @return スクリプトの実行クラス
	 */
	@NotNull
	protected final SBRead getSBRead() {
		return sbRead;
	}

	/**
	 * スクリプトのデータクラスを取得します。
	 * @return スクリプトのデータクラス。
	 */
	@NotNull
	protected final ScriptData getScriptData() {
		return sbRead.getScriptData();
	}

	/**
	 * スクリプトを何番目まで実行したのか取得します。
	 * @return 進行度
	 */
	protected final int getScriptIndex() {
		return sbRead.getScriptIndex();
	}

	/**
	 * オプションの処理を実行します
	 * @throws Exception オプションの処理内で例外が発生した時にスローされます。
	 * @return 実行に成功した場合はtrue
	 */
	protected abstract boolean isValid() throws Exception;

	/**
	 * オプションを実行します。
	 * @param sbRead スクリプトの実行クラス
	 * @return 実行に成功した場合はtrue
	 */
	@Override
	@Deprecated
	public final boolean callOption(@NotNull SBRead sbRead) {
		this.sbRead = sbRead;
		try {
			return isValid();
		} catch (Exception e) {
			e.printStackTrace();
			SBConfig.OPTION_FAILED_TO_EXECUTE.replace(this, e).send(getSBPlayer());
		}
		return false;
	}

	/**
	 * コンソールからコマンドを実行します。
	 * @param command コマンド
	 * @return 実行に成功した場合はtrue
	 */
	protected boolean executeConsoleCommand(@NotNull String command) {
		return Utils.dispatchCommand(Bukkit.getConsoleSender(), getLocation(), command);
	}

	/**
	 * プレイヤーからコマンドを実行します。
	 * @param sbPlayer プレイヤー
	 * @param command コマンド
	 * @param isBypass trueの場合は権限を無視し、falseの場合は権限を無視せず実行します。
	 * @return 実行に成功した場合はtrue
	 */
	protected boolean executeCommand(@NotNull SBPlayer sbPlayer, @NotNull String command, boolean isBypass) {
		Location location = getLocation();
		if (!isBypass || sbPlayer.isOp()) {
			return Utils.dispatchCommand(sbPlayer, location, command);
		} else {
			try {
				sbPlayer.setOp(true);
				return Utils.dispatchCommand(sbPlayer, location, command);
			} finally {
				sbPlayer.setOp(false);
			}
		}
	}
}