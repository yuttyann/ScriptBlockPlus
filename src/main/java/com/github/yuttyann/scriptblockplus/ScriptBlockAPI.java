package com.github.yuttyann.scriptblockplus;

import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.script.endprocess.EndProcess;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.OptionIndex;
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
	 * @return 正常に終了した場合はtrue
	 */
	boolean scriptRead(@NotNull Player player, @NotNull Location location, @NotNull ScriptType scriptType, int index);

	/**
	 * 指定したオプションを登録します。
	 * @param optionIndex オプションの追加位置
	 * @param optionClass 追加するオプションのクラス
	 */
	void registerOption(@NotNull OptionIndex optionIndex, @NotNull Class<? extends BaseOption> optionClass);

	/**
	 * 指定したエンドプロセスを登録します。
	 * @param endProcessClass エンドプロセスの継承クラス
	 */
	void registerEndProcess(@NotNull Class<? extends EndProcess> endProcessClass);


	/**
	 * SBEditを取得します。
	 * @param scriptType スクリプトの種類
	 * @return SBEdit
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
	 * SBFileを取得します。
	 * @param location スクリプトの座標
	 * @param scriptType スクリプトの種類
	 * @return SBFile
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
		 * スクリプトの作者の一覧を取得します。
		 * @return スクリプトの作者の一覧
		 */
		@NotNull
		Set<UUID> getAuthor();

		/**
		 * スクリプトの作者の一覧を設定します。
		 * @param author 作者の一覧
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