package com.github.yuttyann.scriptblockplus.selector;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus CommandListener インターフェース
 * @author yuttyann44581
 */
public interface CommandListener {

	/**
	 * NMS経由でコマンドを実行します。
	 * @param sender 送信者
	 * @param location 座標（セレクターの開始地点に使用されます）
	 * @param command コマンド
	 * @return 実行が成功した場合はtrue
	 */
	boolean executeCommand(@NotNull CommandSender sender, @NotNull Location location, @NotNull String command);
}