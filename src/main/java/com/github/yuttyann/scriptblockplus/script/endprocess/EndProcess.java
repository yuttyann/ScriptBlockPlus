package com.github.yuttyann.scriptblockplus.script.endprocess;

import com.github.yuttyann.scriptblockplus.script.SBRead;

public interface EndProcess {

	/**
	 * スクリプト実行成功時の処理
	 */
	public void success(SBRead sbRead);

	/**
	 * スクリプト実行失敗時の処理
	 */
	public void failed(SBRead sbRead);
}