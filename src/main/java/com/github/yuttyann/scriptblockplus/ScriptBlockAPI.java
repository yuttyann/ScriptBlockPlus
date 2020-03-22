package com.github.yuttyann.scriptblockplus;

import com.github.yuttyann.scriptblockplus.enums.OptionPriority;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.script.endprocess.EndProcess;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * ScriptBlockPlus APIインターフェース
 * @author yuttyann44581
 */
public interface ScriptBlockAPI {

	/**
	 * 指定した位置からスクリプトを実行する
	 * @param player プレイヤー
	 * @param location 座標
	 * @param scriptType スクリプトの種類
	 * @param index 開始位置
	 * @return 実行が成功したかどうか
	 */
	boolean scriptRead(@NotNull Player player, @NotNull Location location, @NotNull ScriptType scriptType, int index);

	/**
	 * 指定した位置にオプションを追加する<br/>
	 * 指定するクラスには"BaseOption"を継承してください
	 * @param priority 追加優先度
	 * @param option オプションクラス
	 */
	void addOption(@NotNull OptionPriority priority, @NotNull Class<? extends BaseOption> option);

	/**
	 * スクリプトの終了処理を追加する<br/>
	 * 指定するクラスには"EndProcess"を実装してください
	 * @param endProcess 終了処理クラス
	 */
	void addEndProcess(@NotNull Class<? extends EndProcess> endProcess);


	/**
	 * スクリプトの編集クラスを取得する<br/>
	 * @param scriptType スクリプトの種類
	 * @return SBEdit
	 */
	@NotNull
	SBEdit getSBEdit(@NotNull ScriptType scriptType);

	interface SBEdit {

		/**
		 * 変更を保存する
		 */
		void save();

		/**
		 * スクリプトが存在するかチェックする
		 * @return スクリプトが存在するか
		 */
		boolean checkPath();

		/**
		 * スクリプトの種類を取得する
		 * @return スクリプトの種類
		 */
		@NotNull
		ScriptType getScriptType();

		/**
		 * 指定座標にスクリプトを作成する
		 * @param player プレイヤー
		 * @param location 座標
		 * @param script スクリプト
		 */
		void create(@NotNull Player player, @NotNull Location location, @NotNull String script);

		/**
		 * 指定座標にスクリプト追加する
		 * @param player プレイヤー
		 * @param location 座標
		 * @param script スクリプト
		 */
		void add(@NotNull Player player, @NotNull Location location, @NotNull String script);

		/**
		 * 指定座標のスクリプトを削除する
		 * @param player プレイヤー
		 * @param location 座標
		 */
		void remove(@NotNull Player player, @NotNull Location location);

		/**
		 * 指定座標のスクリプトの情報を表示する
		 * @param player プレイヤー
		 * @param location 座標
		 */
		void view(@NotNull Player player, @NotNull Location location);
	}

	/**
	 * スクリプトのファイルクラスを取得する<br/>
	 * @param location 座標
	 * @param scriptType スクリプトの種類
	 * @return SBFile
	 */
	SBFile getSBFile(@NotNull Location location, @NotNull ScriptType scriptType);

	interface SBFile {

		/**
		 * スクリプトの座標を設定する<br/>
		 * ただし、別の座標に切り替える機能であり保存場所を変更するわけではない
		 * @param location 座標
		 */
		void setLocation(@NotNull Location location);

		/**
		 * 変更を保存する
		 */
		void save();

		/**
		 * スクリプトが存在するかチェックする
		 * @return スクリプトが存在するか
		 */
		boolean checkPath();

		/**
		 * スクリプトのパスを取得する
		 * @return スクリプトのパス
		 */
		@NotNull
		String getScriptPath();

		/**
		 * スクリプトの種類を取得する
		 * @return スクリプトの種類
		 */
		@NotNull
		ScriptType getScriptType();

		/**
		 * スクリプトの座標を取得する
		 * @return 座標
		 */
		@Nullable
		Location getLocation();

		/**
		 * スクリプトの作者を取得する
		 * @return 作者
		 */
		@Nullable
		String getAuthor();

		/**
		 * スクリプトの作者を取得する
		 * @param isMinecraft "MinecraftID"を取得するかどうか（それ以外の場合はUUIDが取得される）
		 * @return 作者
		 */
		@NotNull
		List<String> getAuthors(boolean isMinecraft);

		/**
		 * スクリプトの編集時刻を取得する
		 * @return 時刻
		 */
		@Nullable
		String getLastEdit();

		/**
		 * スクリプトの実行回数を取得する
		 * @return 実行回数
		 */
		int getAmount();

		/**
		 * スクリプトを取得する
		 * @return スクリプト
		 */
		@NotNull
		List<String> getScripts();

		/**
		 * スクリプトをコピーする
		 * @param target 保存先
		 * @param overwrite 上書きするか
		 * @return コピーに成功したか
		 */
		boolean copyScripts(@NotNull Location target, boolean overwrite);

		/**
		 * スクリプトの作者を設定する
		 * @param player 作者
		 */
		void setAuthor(@NotNull OfflinePlayer player);

		/**
		 * スクリプトの作者を追加する<br/>
		 * 同じ作者は追加されません
		 * @param player 作者
		 */
		void addAuthor(@NotNull OfflinePlayer player);

		/**
		 * スクリプトの作者を削除する
		 * @param player 作者
		 */
		void removeAuthor(@NotNull OfflinePlayer player);

		/**
		 * スクリプトの編集時刻を現在の時刻に設定する
		 */
		void setLastEdit();

		/**
		 * スクリプトの編集時刻を指定の時刻に設定する
		 * @param time 時間
		 */
		void setLastEdit(@NotNull String time);

		/**
		 * スクリプトの実行可能な回数を設定する
		 * @param amount 量
		 */
		void setAmount(int amount);

		/**
		 * スクリプトの実行可能な回数を増やす
		 * @param amount 量
		 */
		void addAmount(int amount);

		/**
		 * スクリプトの実行可能な回数を減らす
		 * @param amount 量
		 */
		void subtractAmount(int amount);

		/**
		 * スクリプトを設定する
		 * @param scripts スクリプト
		 */
		void setScripts(@NotNull List<String> scripts);

		/**
		 * 指定した位置のスクリプトを上書きする
		 * @param index 位置
		 * @param script スクリプト
		 */
		void setScript(int index, @NotNull String script);

		/**
		 * スクリプトを追加する
		 * @param script スクリプト
		 */
		void addScript(@NotNull String script);

		/**
		 * 指定した位置にスクリプトを追加する
		 * @param index 位置
		 * @param script スクリプト
		 */
		void addScript(int index, @NotNull String script);

		/**
		 * スクリプトを削除する
		 * @param script スクリプト
		 */
		void removeScript(@NotNull String script);

		/**
		 * スクリプトを全て削除する
		 */
		void clearScripts();

		/**
		 * 全ての設定を削除する
		 */
		void remove();

		/**
		 * スクリプトを再読み込みする
		 */
		void reload();
	}
}