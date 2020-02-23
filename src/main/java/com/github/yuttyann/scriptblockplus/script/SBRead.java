package com.github.yuttyann.scriptblockplus.script;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import com.github.yuttyann.scriptblockplus.player.ObjectMap;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import org.jetbrains.annotations.NotNull;

public interface SBRead extends ObjectMap {

	/**
	 * プラグインを取得する
	 * @return プラグイン
	 */
	@NotNull
	public Plugin getPlugin();

	/**
	 * SBPlayerを取得する
	 * @return SBPlayer
	 */
	@NotNull
	public SBPlayer getSBPlayer();

	/**
	 * スクリプトのリストを取得する
	 * @return スクリプトのリスト
	 */
	@NotNull
	public List<String> getScripts();

	/**
	 * オプションの値を取得する
	 * @return オプションの値
	 */
	@NotNull
	public String getOptionValue();

	/**
	 * 座標文字列を取得する
	 * @return ワールド名を除いた文字列(x, y, z)
	 */
	@NotNull
	public String getCoords();

	/**
	 * 座標文字列を取得する
	 * @return ワールド名を含めた文字列(world, x, y, z)
	 */
	@NotNull
	public String getFullCoords();

	/**
	 * 座標を取得する</br>
	 * ※座標変更不可
	 * @return スクリプトの座標
	 */
	@NotNull
	public Location getLocation();

	/**
	 * スクリプトの種類を取得する
	 * @return スクリプトの種類
	 */
	@NotNull
	public ScriptType getScriptType();

	/**
	 * スクリプトの管理クラスを取得する
	 * @return スクリプトの管理クラス
	 */
	@NotNull
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