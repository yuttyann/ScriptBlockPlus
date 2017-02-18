package com.github.yuttyann.scriptblockplus;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.file.ScriptData;
import com.github.yuttyann.scriptblockplus.manager.MapManager;
import com.github.yuttyann.scriptblockplus.manager.ScriptManager;

/**
 * @author ゆっちゃん
 * ScriptBlockPlus APIクラス
 */
public class ScriptBlockAPI {

	private ScriptBlock plugin;
	private MapManager mapManager;
	private ScriptData scriptData;
	private ScriptManager scriptManager;

	/**
	 * コンストラクタ
	 * @param location
	 * @param scriptType
	 */
	protected ScriptBlockAPI(ScriptBlock plugin, Location location, ScriptType scriptType) {
		this.mapManager = plugin.getMapManager();
		this.scriptData = new ScriptData(plugin, BlockLocation.fromLocation(location), scriptType);
		this.scriptManager = new ScriptManager(plugin, scriptData.getBlockLocation(), scriptType);
	}

	/**
	 * 設定されているスクリプトを実行する。
	 * @param player プレイヤー
	 */
	public void scriptExec(Player player) {
		scriptManager.scriptExec(player);
	}

	/**
	 * スクリプトの座標を設定する。
	 * @param location 座標
	 */
	public void setLocation(Location location) {
		scriptData.setBlockLocation(BlockLocation.fromLocation(location));
		scriptManager = new ScriptManager(plugin, scriptData.getBlockLocation(), getScriptType());
	}

	/**
	 * スクリプトの座標を取得する。
	 * @return 座標
	 */
	public Location getLocation() {
		return scriptData.getBlockLocation();
	}

	/**
	 * スクリプトの種類を取得する。
	 * @return 種類
	 */
	public ScriptType getScriptType() {
		return scriptData.getScriptType();
	}

	/**
	 * スクリプトが存在するかチェックする。
	 * @return スクリプトが存在するか。
	 */
	public boolean checkPath() {
		return scriptData.checkPath();
	}

	/**
	 * スクリプトを保存する。
	 */
	public void save() {
		scriptData.save();
	}

	/**
	 * スクリプトの作者を取得する。
	 * @return 作者
	 */
	public String getAuthor() {
		return scriptData.getAuthor();
	}

	/**
	 * スクリプトの作者を取得する。
	 * @param isName UUIDをMinecraftIDに変換するかどうか
	 * @return 作者
	 */
	public List<String> getAuthors(boolean isName) {
		return scriptData.getAuthors(isName);
	}

	/**
	 * スクリプトの編集時刻を取得する。
	 * @return 時刻
	 */
	public String getLastEdit() {
		return scriptData.getLastEdit();
	}

	/**
	 * スクリプトの実行回数を取得する。
	 * @return 実行回数
	 */
	public int getAmount() {
		return scriptData.getAmount();
	}

	/**
	 * スクリプトを取得する。
	 * @return スクリプト
	 */
	public List<String> getScripts() {
		return scriptData.getScripts();
	}

	/**
	 * スクリプトを移動する。
	 * @param target 移動先
	 * @param overwrite 上書きするか
	 */
	public void moveScripts(Location target, boolean overwrite) {
		scriptData.moveScripts(BlockLocation.fromLocation(target), overwrite);
	}

	/**
	 * スクリプトの作者を設定する。
	 * @param player 作者
	 */
	public void setAuthor(Player player) {
		scriptData.setAuthor(player);
	}

	/**
	 * スクリプトの作者を追加する。
	 * 同じ作者は追加されません。
	 * @param player 作者
	 */
	public void addAuthor(Player player) {
		scriptData.addAuthor(player);
	}

	/**
	 * スクリプトの作者を削除する。
	 * @param player 作者
	 */
	public void removeAuthor(Player player) {
		scriptData.removeAuthor(player);
	}

	/**
	 * スクリプトの編集時刻を設定する。
	 */
	public void setLastEdit() {
		scriptData.setLastEdit();
	}

	/**
	 * スクリプトの実行回数を増やす。
	 * @param amount 量
	 */
	public void addAmount(int amount) {
		scriptData.addAmount(amount);
	}

	/**
	 * スクリプトの実行回数を減らす。
	 * @param amount 量
	 */
	public void subtractAmount(int amount) {
		scriptData.subtractAmount(amount);
	}

	/**
	 * スクリプトを設定する。
	 * @param scripts スクリプト
	 */
	public void setScripts(List<String> scripts) {
		scriptData.setScripts(scripts);
		mapManager.addLocation(scriptData.getBlockLocation(), getScriptType());
	}

	/**
	 * 一番最初のスクリプトを設定する。
	 * @param script スクリプト
	 */
	public void setCreateScripts(String script) {
		scriptData.setCreateScripts(script);
		mapManager.addLocation(scriptData.getBlockLocation(), getScriptType());
	}

	/**
	 * スクリプトを追加する。
	 * @param script スクリプト
	 */
	public void addScripts(String script) {
		scriptData.addScripts(script);
		mapManager.removeTimes(scriptData.getBlockLocation().getFullCoords());
	}

	/**
	 * スクリプトを削除する。
	 * @param script スクリプト
	 */
	public void removeScripts(String script) {
		scriptData.removeScripts(script);
		if (scriptData.getScripts().isEmpty()) {
			mapManager.removeLocation(scriptData.getBlockLocation(), getScriptType());
		} else {
			mapManager.removeTimes(scriptData.getBlockLocation().getFullCoords());
		}
	}

	/**
	 * スクリプトを全て削除する。
	 */
	public void clearScripts() {
		scriptData.clearScripts();
		mapManager.removeLocation(scriptData.getBlockLocation(), getScriptType());
	}

	/**
	 * 全ての設定を削除する。
	 */
	public void remove() {
		scriptData.remove();
		mapManager.removeLocation(scriptData.getBlockLocation(), getScriptType());
	}

	/**
	 * スクリプトを再読み込みする。
	 */
	public void reload() {
		scriptData.reload();
	}
}