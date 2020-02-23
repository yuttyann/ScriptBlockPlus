package com.github.yuttyann.scriptblockplus.script.endprocess;

import com.github.yuttyann.scriptblockplus.enums.InstanceType;
import com.github.yuttyann.scriptblockplus.manager.EndProcessManager;
import com.github.yuttyann.scriptblockplus.script.SBInstance;
import com.github.yuttyann.scriptblockplus.script.SBRead;
import org.jetbrains.annotations.NotNull;

public interface EndProcess extends SBInstance<EndProcess> {

	/**
	 * インスタンスを生成する
	 * @return EndProcess
	 */
	@NotNull
	public default EndProcess newInstance() {
		return EndProcessManager.getInstance().newInstance(this, InstanceType.REFLECTION);
	}

	/**
	 * スクリプト実行成功時の処理
	 */
	public void success(@NotNull SBRead sbRead);

	/**
	 * スクリプト実行失敗時の処理
	 */
	public void failed(@NotNull SBRead sbRead);
}