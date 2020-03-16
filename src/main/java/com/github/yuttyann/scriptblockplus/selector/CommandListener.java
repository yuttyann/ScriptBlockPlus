package com.github.yuttyann.scriptblockplus.selector;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * コマンド実行 インターフェース
 * 対応: Spigot、CraftBukkit、PaperMC
 * @author yuttyann44581
 */
public interface CommandListener {

	/**
	 * コマンドを実行する
	 * @param sender 送信者
	 * @param location 座標（セレクターの開始地点に使用されます）
	 * @param command コマンド
	 * @return 実行が成功したかどうか
	 */
	public boolean executeCommand(@NotNull CommandSender sender, @NotNull Location location, @NotNull String command);
}