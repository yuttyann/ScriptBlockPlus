package com.github.yuttyann.scriptblockplus.script.option;

import com.github.yuttyann.scriptblockplus.enums.InstanceType;
import com.github.yuttyann.scriptblockplus.manager.OptionManager;
import com.github.yuttyann.scriptblockplus.script.SBInstance;
import com.github.yuttyann.scriptblockplus.script.SBRead;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus Option オプションクラス
 * @author yuttyann44581
 */
public abstract class Option implements SBInstance<Option>, Comparable<Option> {

	public static final String PERMISSION_PREFIX = "scriptblockplus.option.";
	public static final String PERMISSION_ALL = PERMISSION_PREFIX + "*";

	private final String name;
	private final String syntax;
	private final int length;

	private int ordinal = -1;

	{
		OptionTag optionTag = getClass().getAnnotation(OptionTag.class);
		if (optionTag == null) {
			throw new NullPointerException("Annotation not found [OptionTag]");
		}
		this.name = optionTag.name();
		this.syntax = optionTag.syntax();
		this.length = this.syntax.length();
	}

	/**
	 * インスタンスを生成します。
	 * @return オプションのインスタンス
	 */
	@Override
	@NotNull
	public Option newInstance() {
		return OptionManager.newInstance(getClass(), InstanceType.REFLECTION);
	}

	/**
	 * オプションの名前を取得します。
	 * @return オプションの名前
	 */
	@NotNull
	public final String getName() {
		return name;
	}

	/**
	 * オプションの構文を取得します。
	 * @return オプションの構文
	 */
	@NotNull
	public final String getSyntax() {
		return syntax;
	}

	/**
	 * オプションの構文の文字数を取得します。
	 * @return オプションの構文の文字数
	 */
	public final int length() {
		return length;
	}

	/**
	 * オプションの序数を取得します
	 * <p>
	 * ※オプションの順番により変動
	 * @return 序数
	 */
	public final int ordinal() {
		return ordinal;
	}

	/**
	 * パーミッションノードを取得します。
	 * @return パーミッションノード
	 */
	@NotNull
	public final String getPermissionNode() {
		return PERMISSION_PREFIX + name;
	}

	/**
	 * スクリプトからオプションの値を取得します。
	 * @param script スクリプト
	 * @return オプションの値
	 */
	@NotNull
	public final String getValue(@NotNull String script) {
		return StringUtils.isNotEmpty(script) ? script.substring(length) : "";
	}

	/**
	 * 指定されたスクリプトがオプションなのか判定します。
	 * @param script スクリプト
	 * @return オプションだった場合はtrue
	 */
	public final boolean isOption(@NotNull String script) {
		if (script.length() < length || !script.startsWith(syntax)) {
			return false;
		}
		return script.substring(0, length).equals(syntax);
	}

	/**
	 * 失敗時に終了処理を無視します（デフォルトはfalseです）
	 * @return trueの場合は終了処理を実行し、falseの場合は無視します。
	 */
	public boolean isFailedIgnore() {
		return false;
	}

	/**
	 * オプションを呼び出します。
	 * @param sbRead {@link SBRead}
	 * @return 有効な場合はtrue
	 */
	public abstract boolean callOption(@NotNull SBRead sbRead);

	@Override
	public int compareTo(@NotNull Option another) {
		return Integer.compare(this.ordinal, another.ordinal);
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		if (obj instanceof Option) {
			Option option = (Option) obj;
			return name.equals(option.getName()) && syntax.equals(option.getSyntax());
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 1;
		int prime = 31;
		hash = prime * hash + name.hashCode();
		hash = prime * hash + syntax.hashCode();
		return hash;
	}

	@Override
	@NotNull
	public String toString() {
		return "Option{name=" + name + ", syntax=" + syntax + '}';
	}
}