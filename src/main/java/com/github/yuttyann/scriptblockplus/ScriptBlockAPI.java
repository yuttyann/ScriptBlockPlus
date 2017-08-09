package com.github.yuttyann.scriptblockplus;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;

/**
 * @author ゆっちゃん
 * ScriptBlockPlus APIクラス
 */
public interface ScriptBlockAPI {

	/**
	 * スクリプトを実行する
	 * @param player プレイヤー
	 */
	public boolean scriptRead(Player player);

	/**
	 * 指定した位置からスクリプトを実行する
	 * @param index 位置
	 * @param player プレイヤー
	 */
	public boolean scriptRead(int index, Player player);

	/**
	 * スクリプトの座標を設定する
	 * <br>
	 * ただし、別の座標に切り替える機能であり保存場所を変更するわけではないです
	 * @param location 座標
	 */
	public void setLocation(Location location);

	/**
	 * スクリプトの座標を取得する
	 * @return 座標
	 */
	public Location getLocation();

	/**
	 * スクリプトの種類を取得する
	 * @return 種類
	 */
	public ScriptType getScriptType();

	/**
	 * スクリプトが存在するかチェックする
	 * @return スクリプトが存在するか
	 */
	public boolean checkPath();

	/**
	 * 変更を保存する
	 */
	public void save();

	/**
	 * オプションを追加する
	 * <br>
	 * 追加するオプションクラスには"BaseOption"を継承してください
	 * @param option オプションクラス
	 */
	public void addOption(Class<? extends BaseOption> option);

	/**
	 * 指定した位置にオプションを追加する
	 * <br>
	 * 追加するオプションクラスには"BaseOption"を継承してください
	 * @param index 位置
	 * @param option オプションクラス
	 */
	public void addOption(int index, Class<? extends BaseOption> option);

	/**
	 * オプションを削除する
	 * @param option オプションクラス
	 */
	public void removeOption(Class<? extends BaseOption> option);

	/**
	 * 指定した位置のオプションを削除する
	 * @param index 位置
	 */
	public void removeOption(int index);

	/**
	 * スクリプトの作者を取得する
	 * @return 作者
	 */
	public String getAuthor();

	/**
	 * スクリプトの作者を取得する
	 * @param isName UUIDをMinecraftIDに変換するかどうか
	 * @return 作者
	 */
	public List<String> getAuthors(boolean isName);

	/**
	 * スクリプトの編集時刻を取得する
	 * @return 時刻
	 */
	public String getLastEdit();

	/**
	 * スクリプトの実行回数を取得する
	 * @return 実行回数
	 */
	public int getAmount();

	/**
	 * スクリプトを取得する
	 * @return スクリプト
	 */
	public List<String> getScripts();

	/**
	 * スクリプトをコピーする
	 * @param target 保存先
	 * @param overwrite 上書きするか
	 */
	public void copyScripts(Location target, boolean overwrite);

	/**
	 * スクリプトの作者を設定する
	 * @param player 作者
	 */
	public void setAuthor(Player player);

	/**
	 * スクリプトの作者を追加する
	 * <br>
	 * 同じ作者は追加されません
	 * @param player 作者
	 */
	public void addAuthor(Player player);

	/**
	 * スクリプトの作者を削除する
	 * @param player 作者
	 */
	public void removeAuthor(Player player);

	/**
	 * スクリプトの編集時刻を現在の時刻に設定する
	 */
	public void setLastEdit();

	/**
	 * スクリプトの実行回数を増やす
	 * @param amount 量
	 */
	public void addAmount(int amount);

	/**
	 * スクリプトの実行回数を減らす
	 * @param amount 量
	 */
	public void subtractAmount(int amount);

	/**
	 * スクリプトを設定する
	 * @param scripts スクリプト
	 */
	public void setScripts(List<String> scripts);

	/**
	 * 指定した位置のスクリプトを上書きする
	 * @param index 位置
	 * @param script スクリプト
	 */
	public void setScript(int index, String script);

	/**
	 * スクリプトを追加する
	 * @param script スクリプト
	 */
	public void addScript(String script);

	/**
	 * 指定した位置にスクリプトを追加する
	 * @param index 位置
	 * @param script スクリプト
	 */
	public void addScript(int index, String script);

	/**
	 * スクリプトを削除する
	 * @param script スクリプト
	 */
	public void removeScript(String script);

	/**
	 * スクリプトを全て削除する
	 */
	public void clearScripts();

	/**
	 * 全ての設定を削除する
	 */
	public void remove();

	/**
	 * スクリプトを再読み込みする
	 */
	public void reload();
}