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

	private Plugin plugin;
	private SBPlayer sbPlayer;
	private String optionValue;
	private BlockCoords blockCoords;
	private MapManager mapManager;
	private List<String> scripts;
	private ScriptType scriptType;
	private ScriptRead scriptRead;
	private ScriptData scriptData;
	private int scriptIndex;

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
	protected Plugin getPlugin() {
		return plugin;
	}

	/**
	 * プレイヤーを取得する
	 * @return プレイヤー
	 */
	protected Player getPlayer() {
		return sbPlayer.getPlayer();
	}

	/**
	 * SBプレイヤーを取得する
	 * @return SBプレイヤー
	 */
	protected SBPlayer getSBPlayer() {
		return sbPlayer;
	}

	/**
	 * UUIDを取得する
	 * @return UUID
	 */
	protected UUID getUniqueId() {
		return sbPlayer.getUniqueId();
	}

	/**
	 * オプションの値を取得する
	 * @return オプションの値
	 */
	protected String getOptionValue() {
		return optionValue;
	}

	/**
	 * 座標文字列を取得する
	 * @return ワールド名を除いた、文字列(x, y, z)
	 */
	protected String getCoords() {
		return blockCoords.getCoords();
	}

	/**
	 * 座標文字列を取得する
	 * @return ワールド名を含めた、文字列(world, x, y, z)
	 */
	protected String getFullCoords() {
		return blockCoords.getFullCoords();
	}

	/**
	 * 座標を取得する</br>
	 * ※座標変更不可
	 * @return スクリプトの座標
	 */
	protected Location getLocation() {
		return blockCoords;
	}

	/**
	 * マップの管理クラスを取得する
	 * @return マップの管理クラス
	 */
	protected MapManager getMapManager() {
		return mapManager;
	}

	/**
	 * スクリプトの内容を取得する
	 * @return スクリプトの内容
	 */
	protected List<String> getScripts() {
		return scripts;
	}

	/**
	 * スクリプトの種類を取得する
	 * @return スクリプトの種類
	 */
	protected ScriptType getScriptType() {
		return scriptType;
	}

	/**
	 * スクリプトの実行クラスを取得する
	 * @return スクリプトの実行クラス
	 */
	protected ScriptRead getScriptRead() {
		return scriptRead;
	}

	/**
	 * スクリプトの管理クラスを取得する
	 * @return スクリプトの管理クラス
	 */
	protected ScriptData getScriptData() {
		return scriptData;
	}

	/**
	 * スクリプトの進行度を取得する
	 * @return 進行度
	 */
	protected int getScriptIndex() {
		return scriptIndex;
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
		this.plugin = scriptRead.getPlugin();
		this.sbPlayer = scriptRead.getSBPlayer();
		this.optionValue = scriptRead.getOptionValue();
		this.blockCoords = scriptRead.getBlockCoords();
		this.mapManager = scriptRead.getMapManager();
		this.scripts = scriptRead.getScripts();
		this.scriptType = scriptRead.getScriptType();
		this.scriptData = scriptRead.getScriptData();
		this.scriptIndex = scriptRead.getScriptIndex();
		try {
			return isValid();
		} catch (Exception e) {
			e.printStackTrace();
			Utils.sendMessage(sbPlayer, SBConfig.getOptionFailedToExecuteMessage(this, e));
		}
		return false;
	}

	/**
	 * コンソールからコマンドを実行する</br>
	 * @param command
	 * @return 実行が成功したかどうか
	 */
	protected final boolean executeConsoleCommand(String command) {
		return Utils.dispatchCommand(Bukkit.getConsoleSender(), blockCoords.clone(), command);
	}

	/**
	 * プレイヤーからコマンドを実行する</br>
	 * @param player
	 * @param command
	 * @param isBypass
	 * @return 実行が成功したかどうか
	 */
	protected final boolean executeCommand(Player player, String command, boolean isBypass) {
		Location location = new BlockCoords(blockCoords);
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