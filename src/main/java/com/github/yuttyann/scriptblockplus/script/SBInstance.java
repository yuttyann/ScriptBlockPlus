package com.github.yuttyann.scriptblockplus.script;

import org.jetbrains.annotations.NotNull;

public interface SBInstance<T> {

	@NotNull
	T newInstance();
}