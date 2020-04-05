package com.github.yuttyann.scriptblockplus.script.option;

import com.github.yuttyann.scriptblockplus.enums.InstanceType;
import com.github.yuttyann.scriptblockplus.manager.OptionManager;
import com.github.yuttyann.scriptblockplus.script.SBInstance;
import com.github.yuttyann.scriptblockplus.script.SBRead;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * ScriptBlockPlus Option オプションクラス
 * @author yuttyann44581
 */
public abstract class Option implements SBInstance<Option>, Comparable<Option> {

	private static final String PERMISSION_PREFIX = "scriptblockplus.option.";

	private final String name;
	private final String syntax;

	private int length;
	private int ordinal = -1;

	/**
	 * コンストラクタ
	 * @param name オプション名　[（例） example]
	 * @param syntax オプション構文　[（例） @example: ]
	 */
	protected Option(@NotNull String name, @NotNull String syntax) {
		this.name = Objects.requireNonNull(name);
		this.syntax = Objects.requireNonNull(syntax);
		this.length = this.syntax.length();
	}

	/**
	 * インスタンスを生成します。
	 * @return Optionのインスタンス
	 */
	@Override
	@NotNull
	public Option newInstance() {
		return OptionManager.newInstance(this.getClass(), InstanceType.REFLECTION);
	}

	/**
	 * オプション名を取得します。
	 * @return オプション名
	 */
	@NotNull
	public final String getName() {
		return name;
	}

	/**
	 * 構文を取得します。
	 * @return 構文
	 */
	@NotNull
	public final String getSyntax() {
		return syntax;
	}

	/**
	 * 構文の文字列の長さを取得します。
	 * @return 構文の文字列の長さ
	 */
	public int length() {
		return length;
	}

	/**
	 * オプションの序数を取得します（オプションの順番により変動）
	 * @return 序数
	 */
	public int ordinal() {
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
		return StringUtils.removeStart(script, syntax);
	}

	/**
	 * スクリプトがオプションなのかどうか確認します。
	 * @param script スクリプト
	 * @return オプションだった場合はtrue
	 */
	public final boolean isOption(@NotNull String script) {
		if (StringUtils.isEmpty(script) || !script.startsWith(syntax) || script.length() < length) {
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
	 * オプションを実行します。
	 * @param sbRead スクリプトの実行クラス
	 * @return 実行に成功した場合はtrue
	 */
	public abstract boolean callOption(@NotNull SBRead sbRead);

	@Override
	public int compareTo(@NotNull Option another) {
		return Integer.compare(this.ordinal, another.ordinal);
	}

	@Override
	public boolean equals(Object obj) {
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
	public String toString() {
		return "Option{name='" + name + '\'' + ", syntax='" + syntax + '\'' + '}';
	}
}