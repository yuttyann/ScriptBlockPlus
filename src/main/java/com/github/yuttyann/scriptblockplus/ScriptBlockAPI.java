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
	 * 指定した位置からスクリプトを実行します。
	 * @param player プレイヤー
	 * @param location 座標
	 * @param scriptType スクリプトの種類
	 * @param index 開始位置
	 * @return 実行が成功した場合はtrue
	 */
	boolean scriptRead(@NotNull Player player, @NotNull Location location, @NotNull ScriptType scriptType, int index);

	/**
	 * 指定した優先位置にオプションを追加します。
	 * <br/>
	 * 指定するクラスには"BaseOption"を継承してください。
	 * @param priority 追加優先度
	 * @param option オプションクラス
	 */
	void addOption(@NotNull OptionPriority priority, @NotNull Class<? extends BaseOption> option);

	/**
	 * スクリプトの終了処理を追加します。
	 * <br/>
	 * 指定するクラスには"EndProcess"を実装してください。
	 * @param endProcess 終了処理クラス
	 */
	void addEndProcess(@NotNull Class<? extends EndProcess> endProcess);


	/**
	 * スクリプトの編集クラスを取得します。
	 * @param scriptType スクリプトの種類
	 * @return スクリプトの編集クラス
	 */
	@NotNull
	SBEdit getSBEdit(@NotNull ScriptType scriptType);

	interface SBEdit {

		/**
		 * 変更を保存します。
		 */
		void save();

		/**
		 * スクリプトが存在するのか確認します。
		 * @return スクリプトが存在する場合はtrue
		 */
		boolean hasPath();

		/**
		 * スクリプトの種類を取得します。
		 * @return スクリプトの種類
		 */
		@NotNull
		ScriptType getScriptType();

		/**
		 * 指定した座標にスクリプトを作成します。
		 * @param player プレイヤー
		 * @param location 座標
		 * @param script スクリプト
		 */
		void create(@NotNull Player player, @NotNull Location location, @NotNull String script);

		/**
		 * 指定した座標にスクリプト追加します。
		 * @param player プレイヤー
		 * @param location 座標
		 * @param script スクリプト
		 */
		void add(@NotNull Player player, @NotNull Location location, @NotNull String script);

		/**
		 * 指定した座標のスクリプトを削除します。
		 * @param player プレイヤー
		 * @param location 座標
		 */
		void remove(@NotNull Player player, @NotNull Location location);

		/**
		 * 指定した座標のスクリプトの情報を表示します。
		 * @param player プレイヤー
		 * @param location 座標
		 */
		void view(@NotNull Player player, @NotNull Location location);
	}

	/**
	 * スクリプトのファイルクラスを取得します。
	 * @param location 座標
	 * @param scriptType スクリプトの種類
	 * @return スクリプトのファイルクラス
	 */
	SBFile getSBFile(@NotNull Location location, @NotNull ScriptType scriptType);

	interface SBFile {

		/**
		 * スクリプトの対象を、指定した座標のスクリプトに切り替えます。
		 * <br/>
		 * 別の座標の情報を参照する機能なので、保存場所を変えるわけではありません。
		 * @param location 座標
		 */
		void setLocation(@NotNull Location location);

		/**
		 * 変更を保存します。
		 */
		void save();

		/**
		 * スクリプトのパスが存在するのか確認します。
		 * @return スクリプトのパスが存在する場合はtrue
		 */
		boolean hasPath();

		/**
		 * スクリプトのパスを取得します。
		 * @return スクリプトのパス
		 */
		@NotNull
		String getPath();

		/**
		 * スクリプトの種類を取得します。
		 * @return スクリプトの種類
		 */
		@NotNull
		ScriptType getScriptType();

		/**
		 * スクリプトの座標を取得します。
		 * @return 座標
		 */
		@Nullable
		Location getLocation();

		/**
		 * スクリプトの作者を取得します。
		 * @return 作者
		 */
		@Nullable
		String getAuthor();

		/**
		 * スクリプトの作者のリストを取得します。
		 * @param isMinecraftID trueの場合はMinecraftIDを取得し、falseの場合はUUIDを取得します。
		 * @return 作者のリスト
		 */
		@NotNull
		List<String> getAuthors(boolean isMinecraftID);

		/**
		 * スクリプトの編集時刻を取得します。
		 * @return 時刻
		 */
		@Nullable
		String getLastEdit();

		/**
		 * スクリプトの実行可能な回数を取得します。
		 * @return 実行可能な回数
		 */
		int getAmount();

		/**
		 * スクリプトを取得します。
		 * @return スクリプト
		 */
		@NotNull
		List<String> getScripts();

		/**
		 * スクリプトをコピーします。
		 * @param target 保存先の座標
		 * @param overwrite trueの場合は上書きを行い、falseの場合は上書きを行いません。
		 * @return コピーに成功した場合はtrue
		 */
		boolean copyScripts(@NotNull Location target, boolean overwrite);

		/**
		 * スクリプトの作者を設定します。
		 * @param player 作者のプレイヤー
		 */
		void setAuthor(@NotNull OfflinePlayer player);

		/**
		 * スクリプトの作者を追加します。
		 * @param player 作者のプレイヤー
		 */
		void addAuthor(@NotNull OfflinePlayer player);

		/**
		 * スクリプトの作者を削除します。
		 * @param player 作者のプレイヤー
		 */
		void removeAuthor(@NotNull OfflinePlayer player);

		/**
		 * スクリプトの編集時刻を現在の時刻に設定します。
		 */
		void setLastEdit();

		/**
		 * スクリプトの編集時刻を指定の時刻に設定します。
		 * @param time 時間
		 */
		void setLastEdit(@NotNull String time);

		/**
		 * スクリプトの実行可能な回数を設定します。
		 * @param amount 実行可能な回数
		 */
		void setAmount(int amount);

		/**
		 * スクリプトの実行可能な回数を増します。
		 * @param amount 増やす回数
		 */
		void addAmount(int amount);

		/**
		 * スクリプトの実行可能な回数を減します。
		 * @param amount 減らす回数
		 */
		void subtractAmount(int amount);

		/**
		 * スクリプトを設定します。
		 * @param scripts スクリプトのリスト
		 */
		void setScripts(@NotNull List<String> scripts);

		/**
		 * 指定した位置のスクリプトを上書きします。
		 * @param index 位置
		 * @param script スクリプト
		 */
		void setScript(int index, @NotNull String script);

		/**
		 * スクリプトを追加します。
		 * @param script スクリプト
		 */
		void addScript(@NotNull String script);

		/**
		 * 指定した位置にスクリプトを追加します。
		 * @param index 位置
		 * @param script スクリプト
		 */
		void addScript(int index, @NotNull String script);

		/**
		 * スクリプトを削除します。
		 * @param script スクリプト
		 */
		void removeScript(@NotNull String script);

		/**
		 * 全てのスクリプトを削除します。
		 */
		void clearScripts();

		/**
		 * 全ての設定を削除します。
		 */
		void remove();

		/**
		 * スクリプトを再読み込みを行います。
		 */
		void reload();
	}
}