package com.github.yuttyann.scriptblockplus.script;

import org.jetbrains.annotations.NotNull;

public interface SBInstance<T> {

	/**
	 * インスタンスを生成します。
	 * @return インスタンス
	 */
	@NotNull
	T newInstance();
}