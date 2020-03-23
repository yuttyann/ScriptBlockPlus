package com.github.yuttyann.scriptblockplus.script;

import com.github.yuttyann.scriptblockplus.player.ObjectMap;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface SBRead extends ObjectMap {

	/**
	 * プラグインを取得します。
	 * @return プラグイン
	 */
	@NotNull
	Plugin getPlugin();

	/**
	 * SBPlayerを取得します。
	 * @return SBPlayer
	 */
	@NotNull
	SBPlayer getSBPlayer();

	/**
	 * スクリプトのリストを取得します。
	 * @return スクリプトのリスト
	 */
	@NotNull
	List<String> getScripts();

	/**
	 * オプションの値を取得します。
	 * @return オプションの値
	 */
	@NotNull
	String getOptionValue();

	/**
	 * ワールド名を除いた文字列の座標を取得します。
	 * @return ワールド名を除いた文字列(x, y, z)
	 */
	@NotNull
	String getCoords();

	/**
	 * ワールド名を含めた文字列の座標を取得します。
	 * @return ワールド名を含めた文字列(world, x, y, z)
	 */
	@NotNull
	String getFullCoords();

	/**
	 * 編集不可な座標を取得します。
	 * @return スクリプトの座標
	 */
	@NotNull
	Location getLocation();

	/**
	 * スクリプトの種類を取得します。
	 * @return スクリプトの種類
	 */
	@NotNull
	ScriptType getScriptType();

	/**
	 * スクリプトのデータクラスを取得します。
	 * @return スクリプトのデータクラス。
	 */
	@NotNull
	ScriptData getScriptData();

	/**
	 * スクリプトを何番目まで実行したのか取得します。
	 * @return 進行度
	 */
	int getScriptIndex();

	/**
	 * スクリプトを実行します。
	 * @param index 開始位置
	 */
	boolean read(int index);
}