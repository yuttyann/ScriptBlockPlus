package com.github.yuttyann.scriptblockplus;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.file.ScriptData;
import com.github.yuttyann.scriptblockplus.manager.MapManager;
import com.github.yuttyann.scriptblockplus.manager.OptionManager;
import com.github.yuttyann.scriptblockplus.type.ScriptType;
import com.github.yuttyann.scriptblockplus.util.BlockLocation;

public class ScriptBlockAPI {

	private ScriptData scriptData;

	/**
	 * コンストラクタ
	 * @param block
	 * @param scriptType
	 */
	public ScriptBlockAPI(Block block, ScriptType scriptType) {
		scriptData = new ScriptData(block, scriptType);
	}

	/**
	 * 設定されているスクリプトを実行する。
	 * @param player プレイヤー
	 */
	public void scriptExec(Player player) {
		new OptionManager(scriptData.getBlockLocation(), getScriptType()).scriptExec(player);
	}

	/**
	 * スクリプトの座標を設定する。
	 * @param location 座標
	 */
	public void setLocation(Location location) {
		scriptData.setBlockLocation(BlockLocation.fromLocation(location));
	}

	/**
	 * スクリプトの座標を取得する。
	 * @return 座標
	 */
	public Location getLocation() {
		return scriptData.getBlockLocation().toLocation();
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
	 * 設定したスクリプトを保存する。
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
	 * @return 作者
	 */
	public List<String> getAuthors() {
		return scriptData.getAuthors();
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
	}

	/**
	 * 一番最初のスクリプトを設定する。
	 * @param script スクリプト
	 */
	public void setCreateScript(String script) {
		scriptData.setCreateScript(script);
	}

	/**
	 * スクリプトを追加する。
	 * @param script スクリプト
	 */
	public void addScripts(String script) {
		scriptData.addScripts(script);
	}

	/**
	 * スクリプトを削除する。
	 * @param script スクリプト
	 */
	public void removeScripts(String script) {
		scriptData.removeScripts(script);
		if (scriptData.getScripts().isEmpty()) {
			MapManager.removeCoords(scriptData.getBlockLocation(), getScriptType());
		}
	}

	/**
	 * スクリプトを全て削除する。
	 */
	public void clearScripts() {
		scriptData.clearScripts();
		MapManager.removeCoords(scriptData.getBlockLocation(), getScriptType());
	}

	/**
	 * 全ての設定を削除する。
	 */
	public void remove() {
		scriptData.remove();
		MapManager.removeCoords(scriptData.getBlockLocation(), getScriptType());
	}

	/**
	 * 全ての設定を再読み込みする。
	 */
	public void reload() {
		MapManager.putAllScripts();
	}
}
