package com.github.yuttyann.scriptblockplus.script.option;

import com.github.yuttyann.scriptblockplus.script.ScriptRead;


public abstract class Option {

	private final String name;
	private final String prefix;

	protected Option(String name, String prefix) {
		this.name = name;
		this.prefix = prefix;
	}

	public String getName() {
		return name;
	}

	public String getPrefix() {
		return prefix;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Option)) {
			return false;
		}
		Option option = (Option) obj;
		return name.equals(option.getName()) && prefix.equals(option.getPrefix());
	}

	public abstract boolean callOption(ScriptRead scriptRead);
}