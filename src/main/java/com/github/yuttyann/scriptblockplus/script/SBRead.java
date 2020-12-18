package com.github.yuttyann.scriptblockplus.script;

import com.github.yuttyann.scriptblockplus.player.ObjectMap;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * ScriptBlockPlus SBRead インターフェース
 * @author yuttyann44581
 */
public interface SBRead extends ObjectMap {

	/**
	 * プラグインを取得します。
	 * @return プラグイン
	 */
	@NotNull
	Plugin getPlugin();

	/**
	 * ScriptBlockPlusの{@link SBPlayer}を取得します。
	 * @return {@link SBPlayer}
	 */
	@NotNull
	SBPlayer getSBPlayer();

	/**
	 * スクリプトの座標を取得します。
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
	 * スクリプトのリストを取得します。
	 * @return スクリプトのリスト
	 */
	@NotNull
	List<String> getScript();

	/**
	 * オプションの値を取得します。
	 * @return オプションの値
	 */
	@NotNull
	String getOptionValue();

	/**
	 * スクリプトを何番目まで実行したのか取得します。
	 * @return 進行度
	 */
	int getScriptIndex();

	/**
	 * スクリプトを実行します。
	 * @param index 開始位置
	 * @return 実行に成功した場合はtrue
	 */
	boolean read(int index);
}