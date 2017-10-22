package com.github.yuttyann.scriptblockplus.script.option;

import java.util.Objects;

import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public abstract class Option {

	private final String name;
	private final String syntax;
	private final int index;

	protected Option(String name, String syntax, int index) {
		this.name = Objects.requireNonNull(name);
		this.syntax = Objects.requireNonNull(syntax);
		this.index = index;
	}

	public final String getName() {
		return name;
	}

	public final String getSyntax() {
		return syntax;
	}

	public final Integer getIndex() {
		return index;
	}

	public final String getValue(String script) {
		return StringUtils.removeStart(script, syntax);
	}

	public final boolean isOption(String script) {
		return script != null && script.startsWith(syntax);
	}

	public boolean isFailedIgnore() {
		return false;
	}

	public abstract boolean callOption(ScriptRead scriptRead);

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Option)) {
			return false;
		}
		Option option = (Option) obj;
		return name.equals(option.getName()) && syntax.equals(option.getSyntax());
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, syntax);
	}

	@Override
	public String toString() {
		return "[name: " + name + ", syntax: " + syntax + "]";
	}
}