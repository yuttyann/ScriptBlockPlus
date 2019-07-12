package com.github.yuttyann.scriptblockplus.script;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import com.github.yuttyann.scriptblockplus.player.ObjectMap;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;

public interface SBRead extends ObjectMap {

	/**
	 * プラグインを取得する
	 * @return プラグイン
	 */
	public Plugin getPlugin();

	/**
	 * SBPlayerを取得する
	 * @return SBPlayer
	 */
	public SBPlayer getSBPlayer();

	/**
	 * スクリプトのリストを取得する
	 * @return スクリプトのリスト
	 */
	public List<String> getScripts();

	/**
	 * オプションの値を取得する
	 * @return オプションの値
	 */
	public String getOptionValue();

	/**
	 * 座標文字列を取得する
	 * @return ワールド名を除いた、文字列(x, y, z)
	 */
	public String getCoords();

	/**
	 * 座標文字列を取得する
	 * @return ワールド名を含めた、文字列(world, x, y, z)
	 */
	public String getFullCoords();

	/**
	 * 座標を取得する</br>
	 * ※座標変更不可
	 * @return スクリプトの座標
	 */
	public Location getLocation();

	/**
	 * スクリプトの種類を取得する
	 * @return スクリプトの種類
	 */
	public ScriptType getScriptType();

	/**
	 * スクリプトの管理クラスを取得する
	 * @return スクリプトの管理クラス
	 */
	public ScriptData getScriptData();

	/**
	 * スクリプト読み込むの進行度を取得する
	 * @return 進行度
	 */
	public int getScriptIndex();

	/**
	 * スクリプトを実行する
	 * @param index 開始位置
	 */
	public boolean read(int index);
}