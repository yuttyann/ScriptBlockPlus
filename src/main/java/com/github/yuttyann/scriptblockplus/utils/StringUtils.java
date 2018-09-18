package com.github.yuttyann.scriptblockplus.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import org.apache.commons.lang.text.StrBuilder;
import org.bukkit.ChatColor;

public final class StringUtils {

	private static final Random RANDOM = new Random();

	public static List<String> getScripts(String script) throws IllegalArgumentException {
		Objects.requireNonNull(script);
		int length = script.length();
		if (script.charAt(0) != '[' || script.charAt(length - 1) != ']') {
			return Arrays.asList(script);
		}
		int start = 0;
		int end = 0;
		for (int i = 0; i < length; i++) {
			if (script.charAt(i) == '[') {
				start++;
			} else if (script.charAt(i) == ']') {
				end++;
			}
		}
		if (start != end) {
			throw new IllegalArgumentException("Failed to load the script");
		}
		List<String> result = new ArrayList<>(start);
		for (int i = 0, j = 0, k = 0; i < length; i++) {
			if (script.charAt(i) == '[' && j++ == 0) {
				k = i;
			} else if (script.charAt(i) == ']' && --j == 0) {
				result.add(script.substring(k + 1, i));
			}
		}
		return result;
	}

	public static String[] split(String source, String delimiter) {
		if (isEmpty(source) || isEmpty(delimiter)) {
			return new String[] { source };
		}
		int start = 0;
		int end = source.indexOf(delimiter, start);
		List<String> result = new LinkedList<>();
		while (end != -1) {
			result.add(source.substring(start, end));
			start = end + delimiter.length();
			end = source.indexOf(delimiter, start);
		}
		result.add(source.substring(start));
		return result.toArray(new String[result.size()]);
	}

	public static String replace(String source, char search, char replace) {
		if (isEmpty(source)) {
			return source;
		}
		return source.replace(search, replace);
	}

	public static String replaceColorCode(String source, boolean randomColor) {
		if (isEmpty(source)) {
			return null;
		}
		if (randomColor) {
			source = replace(source, "&rc", () -> randomColor().toString());
		}
		return ChatColor.translateAlternateColorCodes('&', source);
	}

	private static ChatColor randomColor() {
		return ChatColor.getByChar(Integer.toHexString(RANDOM.nextInt(16)));
	}

	public static String getColors(String source) {
		if (isEmpty(source)) {
			return source;
		}
		char[] chars = source.toCharArray();
		StringBuilder builder = new StringBuilder(chars.length);
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] != '§' || (i + 1) >= chars.length) {
				continue;
			}
			if (ChatColor.getByChar(Character.toLowerCase(chars[i + 1])) != null) {
				builder.append(chars[i++]).append(chars[i]);
			}
		}
		return builder.toString();
	}

	public static String replace(String source, String search, String replace) {
		return replace(source, search, () -> replace);
	}

	public static String replace(String source, String search, ReplaceValue replace) {
		if (isEmpty(source) || isEmpty(search)) {
			return source;
		}
		int start = 0;
		int end = source.indexOf(search, start);
		if (end == -1) {
			return source;
		}
		int searchLength = search.length();
		int replaceLength = source.length() - replace.length();
		replaceLength = replaceLength < 0 ? 0 : replaceLength;
		StrBuilder builder = new StrBuilder(source.length() + replaceLength);
		while (end != -1) {
			builder.append(source.substring(start, end)).append(replace.asString());
			start = end + searchLength;
			end = source.indexOf(search, start);
		}
		builder.append(source.substring(start));
		return builder.toString();
	}

	public interface ReplaceValue {

		Object value();

		default String asString() {
			Object obj = value();
			return obj == null ? "" : obj.toString();
		}

		default int length() {
			return asString().length();
		}
	}

	public static String createString(String[] args, int start) {
		if (isEmpty(args)) {
			return null;
		}
		StrBuilder builder = new StrBuilder();
		for (int i = start; i < args.length; i++) {
			builder.append(args[i]).append(i == (args.length - 1) ? "" : " ");
		}
		return builder.toString();
	}

	public static String removeStart(String source, String prefix) {
		if (isEmpty(source) || isEmpty(prefix)) {
			return source;
		}
		return source.startsWith(prefix) ? source.substring(prefix.length(), source.length()) : source;
	}

	public static boolean startsWith(String source, String prefix) {
		return isEmpty(source) ? false : source.startsWith(prefix);
	}

	public static boolean isNotEmpty(String source) {
		return !isEmpty(source);
	}

	public static boolean isNotEmpty(String[] sources) {
		return !isEmpty(sources);
	}

	public static boolean isEmpty(String source) {
		return source == null || source.length() == 0;
	}

	public static boolean isEmpty(String[] sources) {
		return StreamUtils.anyMatch(sources, StringUtils::isEmpty);
	}
}