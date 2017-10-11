package com.github.yuttyann.scriptblockplus.script.option;

import java.util.Objects;

import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public abstract class Option {

	private final String name;
	private final String prefix;

	protected Option(String name, String prefix) {
		this.name = Objects.requireNonNull(name);
		this.prefix = Objects.requireNonNull(prefix);
	}

	public final String getName() {
		return name;
	}

	public final String getPrefix() {
		return prefix;
	}

	public final String getValue(String script) {
		return StringUtils.removeStart(script, prefix);
	}

	public final boolean isOption(String script) {
		return script != null && script.startsWith(prefix);
	}

	public abstract boolean callOption(ScriptRead scriptRead);

	@Override
	public final boolean equals(Object obj) {
		if (!(obj instanceof Option)) {
			return false;
		}
		Option option = (Option) obj;
		return name.equals(option.getName()) && prefix.equals(option.getPrefix());
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, prefix);
	}

	@Override
	public String toString() {
		return "[name: " + name + ", prefix: " + prefix + "]";
	}
}