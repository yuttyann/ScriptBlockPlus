package com.github.yuttyann.scriptblockplus;

import com.github.yuttyann.scriptblockplus.enums.OptionPriority;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.script.endprocess.EndProcess;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * ScriptBlockPlus ScriptBlockAPI インターフェース
 * @author yuttyann44581
 */
public interface ScriptBlockAPI {

	/**
	 * 指定した位置からスクリプトを実行します。
	 * @param player プレイヤー
	 * @param location スクリプトの座標
	 * @param scriptType スクリプトの種類
	 * @param index 開始位置
	 * @return 実行が成功した場合はtrue
	 */
	boolean scriptRead(@NotNull Player player, @NotNull Location location, @NotNull ScriptType scriptType, int index);

	/**
	 * 指定した追加位置にオプションを登録します。
	 * <p>
	 * 指定するクラスには"BaseOption"を継承してください。
	 * @param priority 追加位置の優先度
	 * @param option オプションのクラス
	 */
	void registerOption(@NotNull OptionPriority priority, @NotNull Class<? extends BaseOption> option);

	/**
	 * エンドプロセスを登録します。
	 * <p>
	 * 指定するクラスには"EndProcess"を実装してください。
	 * @param endProcess エンドプロセスのクラス
	 */
	void registerEndProcess(@NotNull Class<? extends EndProcess> endProcess);


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
		 * スクリプトの種類を取得します。
		 * @return スクリプトの種類
		 */
		@NotNull
		ScriptType getScriptType();

		/**
		 * 指定した座標にスクリプトを作成します。
		 * @param player プレイヤー
		 * @param location スクリプトの座標
		 * @param script スクリプト
		 */
		void create(@NotNull Player player, @NotNull Location location, @NotNull String script);

		/**
		 * 指定した座標にスクリプト追加します。
		 * @param player プレイヤー
		 * @param location スクリプトの座標
		 * @param script スクリプト
		 */
		void add(@NotNull Player player, @NotNull Location location, @NotNull String script);

		/**
		 * 指定した座標のスクリプトを削除します。
		 * @param player プレイヤー
		 * @param location スクリプトの座標
		 */
		void remove(@NotNull Player player, @NotNull Location location);

		/**
		 * 指定した座標のスクリプトの情報を表示します。
		 * @param player プレイヤー
		 * @param location スクリプトの座標
		 */
		void view(@NotNull Player player, @NotNull Location location);
	}

	/**
	 * スクリプトのファイルクラスを取得します。
	 * @param location スクリプトの座標
	 * @param scriptType スクリプトの種類
	 * @return スクリプトのファイルクラス
	 */
	SBFile getSBFile(@NotNull Location location, @NotNull ScriptType scriptType);

	interface SBFile {

		/**
		 * 変更を保存します。
		 */
		void save();

		/**
		 * スクリプトが存在するのか確認します。
		 * @return スクリプトが存在する場合はtrue
		 */
		boolean has();

		/**
		 * スクリプトの座標を取得します。
		 * @return スクリプトの座標
		 */
		@Nullable
		Location getLocation();

		/**
		 * スクリプトの種類を取得します。
		 * @return スクリプトの種類
		 */
		@NotNull
		ScriptType getScriptType();

		/**
		 * スクリプトの作者のセットを取得します。
		 * @return 作者のセット
		 */
		@NotNull
		Set<UUID> getAuthor();

		/**
		 * クリプトの作者を設定します。
		 * @param author 作者のセット
		 */
		void setAuthor(@NotNull Set<UUID> author);

		/**
		 * スクリプトを取得します。
		 * @return スクリプト
		 */
		@NotNull
		List<String> getScript();

		/**
		 * スクリプトを設定します。
		 * @param script スクリプトのリスト
		 */
		void setScript(@NotNull List<String> script);

		/**
		 * スクリプトの編集時刻を取得します。
		 * @return 時刻
		 */
		@Nullable
		String getLastEdit();

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
		 * スクリプトの実行可能な回数を取得します。
		 * @return 実行可能な回数
		 */
		int getAmount();

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
		 * 全ての設定を削除します。
		 */
		void remove();
	}
}