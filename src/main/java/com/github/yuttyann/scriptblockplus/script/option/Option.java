package com.github.yuttyann.scriptblockplus.script.option;

import com.github.yuttyann.scriptblockplus.script.ScriptRead;

public abstract class Option {

	private String name;
	private String prefix;

	protected Option(String name, String prefix) {
		this.name = name;
		this.prefix = prefix;
	}

	public abstract boolean callOption(ScriptRead scriptRead);

	public String getName() {
		return name;
	}

	public String getPrefix() {
		return prefix;
	}
}