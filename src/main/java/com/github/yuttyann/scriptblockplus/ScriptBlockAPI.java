package com.github.yuttyann.scriptblockplus;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.script.endprocess.EndProcess;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;

/**
 * ScriptBlockPlus APIインターフェース
 * @author yuttyann44581
 */
public interface ScriptBlockAPI {

	/**
	 * 指定した位置からスクリプトを実行する
	 * @param player プレイヤー
	 * @param location 座標
	 * @param scripttype スクリプトの種類
	 * @param index 開始位置
	 * @return 実行が成功したかどうか
	 */
	public boolean scriptRead(Player player, Location location, ScriptType scriptType, int index);

	/**
	 * オプションの位置を取得する</br>
	 * 指定するクラスには"BaseOption"を継承してください
	 * @param option オプションクラス
	 * @return 位置
	 */
	public int indexOfOption(Class<? extends BaseOption> option);

	/**
	 * オプションを追加する</br>
	 * 指定するクラスには"BaseOption"を継承してください
	 * @param option オプションクラス
	 */
	public void addOption(Class<? extends BaseOption> option);

	/**
	 * 指定した位置にオプションを追加する
	 * <br>
	 * 指定するクラスには"BaseOption"を継承してください
	 * @param index 位置
	 * @param option オプションクラス
	 */
	public void addOption(int index, Class<? extends BaseOption> option);

	/**
	 * オプションを削除する</br>
	 * 指定するクラスには"BaseOption"を継承してください
	 * @param option オプションクラス
	 */
	public void removeOption(Class<? extends BaseOption> option);

	/**
	 * 指定した位置のオプションを削除する
	 * @param scriptIndex 位置
	 */
	public void removeOption(int scriptIndex);

	/**
	 * スクリプトの終了処理の位置を取得する</br>
	 * 指定するクラスには"EndProcess"を実装してください
	 * @param process 終了処理クラス
	 * @return 位置
	 */
	public int indexOfEndProcess(Class<? extends EndProcess> endProcess);

	/**
	 * スクリプトの終了処理を追加する</br>
	 * 指定するクラスには"EndProcess"を実装してください
	 * @param process 終了処理クラス
	 */
	public void addEndProcess(Class<? extends EndProcess> endProcess);

	/**
	 * 指定した位置にスクリプトの終了処理を追加する</br>
	 * 指定するクラスには"EndProcess"を実装してください
	 * @param index 位置
	 * @param process 終了処理クラス
	 */
	public void addEndProcess(int index, Class<? extends EndProcess> endProcess);

	/**
	 * スクリプトの終了処理を削除する</br>
	 * 指定するクラスには"EndProcess"を実装してください
	 * @param process 終了処理クラス
	 */
	public void removeEndProcess(Class<? extends EndProcess> endProcess);

	/**
	 * 指定した位置のスクリプトの終了処理を削除する
	 * @param index 位置
	 */
	public void removeEndProcess(int index);

	/**
	 * スクリプトの編集クラスを取得する</br>
	 * @param location 座標
	 * @param scripttype スクリプトの種類
	 * @return SBEdit
	 */
	public SBEdit getSBEdit(Location location, ScriptType scriptType);

	public interface SBEdit {

		/**
		 * スクリプトの座標を設定する</br>
		 * ただし、別の座標に切り替える機能であり保存場所を変更するわけではない
		 * @param location 座標
		 */
		public void setLocation(Location location);

		/**
		 * 変更を保存する
		 */
		public void save();

		/**
		 * スクリプトが存在するかチェックする
		 * @return スクリプトが存在するか
		 */
		public boolean checkPath();

		/**
		 * スクリプトの種類を取得する
		 * @return スクリプトの種類
		 */
		public ScriptType getScriptType();

		/**
		 * 指定座標にスクリプトを作成する
		 * @param player プレイヤー
		 * @param script スクリプト
		 */
		public void create(Player player, String script);

		/**
		 * 指定座標にスクリプト追加する
		 * @param player プレイヤー
		 * @param script スクリプト
		 */
		public void add(Player player, String script);

		/**
		 * 指定座標のスクリプトを削除する
		 * @param player プレイヤー
		 */
		public void remove(Player player);

		/**
		 * 指定座標のスクリプトの情報を表示する
		 * @param player プレイヤー
		 */
		public void view(Player player);
	}

	/**
	 * スクリプトのファイルクラスを取得する</br>
	 * @param scripttype スクリプトの種類
	 * @param location 座標
	 * @return SBFile
	 */
	public SBFile getSBFile(Location location, ScriptType scriptType);

	public interface SBFile {

		/**
		 * スクリプトの座標を設定する</br>
		 * ただし、別の座標に切り替える機能であり保存場所を変更するわけではない
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
		 * @return スクリプトの種類
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
		 * @return コピーに成功したか
		 */
		public boolean copyScripts(Location target, boolean overwrite);

		/**
		 * スクリプトの作者を設定する
		 * @param player 作者
		 */
		public void setAuthor(OfflinePlayer player);

		/**
		 * スクリプトの作者を追加する</br>
		 * 同じ作者は追加されません
		 * @param player 作者
		 */
		public void addAuthor(OfflinePlayer player);

		/**
		 * スクリプトの作者を削除する
		 * @param player 作者
		 */
		public void removeAuthor(OfflinePlayer player);

		/**
		 * スクリプトの編集時刻を現在の時刻に設定する
		 */
		public void setLastEdit();

		/**
		 * スクリプトの編集時刻を指定の時刻に設定する
		 * @param time 時間
		 */
		public void setLastEdit(String time);

		/**
		 * スクリプトの実行可能な回数を設定する
		 * @param amount 量
		 */
		public void setAmount(int amount);

		/**
		 * スクリプトの実行可能な回数を増やす
		 * @param amount 量
		 */
		public void addAmount(int amount);

		/**
		 * スクリプトの実行可能な回数を減らす
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
}