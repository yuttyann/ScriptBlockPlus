package com.github.yuttyann.scriptblockplus.script.option;

import java.util.Objects;

import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

/**
 * オプション クラス
 * @author yuttyann44581
 */
public abstract class Option {

	private static final String PERMISSION_PREFIX = "scriptblockplus.option.";

	private final String name;
	private final String syntax;

	/**
	 * コンストラクタ
	 * @param name
	 * @param syntax
	 */
	protected Option(String name, String syntax) {
		this.name = Objects.requireNonNull(name);
		this.syntax = Objects.requireNonNull(syntax);
	}

	/**
	 * オプション名を取得する
	 * @return オプション名
	 */
	public final String getName() {
		return name;
	}

	/**
	 * 構文を取得する
	 * @return 構文
	 */
	public final String getSyntax() {
		return syntax;
	}

	/**
	 * パーミッションノードを取得する
	 * @return パーミッション
	 */
	public final String getPermissionNode() {
		return PERMISSION_PREFIX + name;
	}

	/**
	 * スクリプトからオプションの値を取得
	 * @return 値
	 */
	public final String getValue(String script) {
		return StringUtils.removeStart(script, syntax);
	}

	/**
	 * スクリプトがオプションなのかどうかチェックする
	 * @return オプションなのかどうか
	 */
	public final boolean isOption(String script) {
		return StringUtils.isNotEmpty(script) && script.startsWith(syntax);
	}

	/**
	 * 失敗時に終了処理を無視するかどうか</br>
	 * 戻り値が true の場合は無視します
	 * @return 無視するかどうか
	 */
	public boolean isFailedIgnore() {
		return false;
	}

	/**
	 * オプションを実行する</br>
	 * このメソッドは ScriptRead.java から呼び出されることを前提としているため、使用しないでください
	 * @param scriptRead
	 * @return 実行が成功したかどうか
	 */
	public abstract boolean callOption(ScriptRead scriptRead);

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
		hash = hash * 31 + name.hashCode();
		hash = hash * 31 + syntax.hashCode();
		return hash;
	}

	@Override
	public String toString() {
		return "Option{name: " + name + ", syntax: " + syntax + "}";
	}
}