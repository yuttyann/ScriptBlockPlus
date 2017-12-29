package com.github.yuttyann.scriptblockplus.commandblock;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

/**
 * コマンド実行 インターフェース
 * @author yuttyann44581
 */
public interface CommandListener {

	/**
	 * コマンドを実行する
	 * @param sender 送信者
	 * @param location 座標（[@a,@p]などの開始地点に使用されます）
	 * @param command コマンド
	 * @return 実行が成功したかどうか
	 */
	public boolean executeCommand(CommandSender sender, Location location, String command);
}