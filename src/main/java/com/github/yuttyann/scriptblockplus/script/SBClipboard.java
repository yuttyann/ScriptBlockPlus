package com.github.yuttyann.scriptblockplus.script;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus SBClipboard インターフェース
 * @author yuttyann44581
 */
public interface SBClipboard {

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
	 * 変更を保存します。
	 */
	void save();

	/**
	 * スクリプトをコピーします。
	 * @return コピーに成功した場合はtrue
	 */
	boolean copy();

	/**
	 * スクリプトを指定した座標にペーストします（保存を行います。）
	 * @param location スクリプトの座標
	 * @param overwrite trueの場合は上書きを行い、falseの場合は行いません。
	 * @return ペーストに成功した場合はtrue
	 */
	boolean paste(@NotNull Location location, boolean overwrite);

	/**
	 * スクリプトを指定した座標にペーストします（保存は行いません。）
	 * @param location スクリプトの座標
	 * @param overwrite trueの場合は上書きを行い、falseの場合は行いません。
	 * @return ペーストに成功した場合はtrue
	 */
	boolean lightPaste(@NotNull Location location, boolean overwrite);
}