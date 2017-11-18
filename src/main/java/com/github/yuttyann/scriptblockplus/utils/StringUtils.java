package com.github.yuttyann.scriptblockplus.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.text.StrBuilder;
import org.bukkit.ChatColor;

import com.github.yuttyann.scriptblockplus.script.ScriptException;

public class StringUtils {

	private static final Random RANDOM = new Random();

	public static List<String> getScripts(String scriptLine) throws ScriptException {
		char[] chars = scriptLine.toCharArray();
		if (chars[0] != '[' || chars[chars.length - 1] != ']') {
			return Arrays.asList(scriptLine);
		}
		int start = 0;
		int end = 0;
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == '[') {
				start++;
			} else if (chars[i] == ']') {
				end++;
			}
		}
		if (start != end) {
			throw new ScriptException("Failed to load the script.");
		}
		List<String> result = new ArrayList<String>(start);
		for (int i = 0, j = 0, k = 0; i < chars.length; i++) {
			if (chars[i] == '[') {
				if (j++ == 0) {
					k = i;
				}
			} else if (chars[i] == ']') {
				if (--j == 0) {
					result.add(scriptLine.substring(k + 1, i));
				}
			}
		}
		return result;
	}

	public static String[] split(String source, String delimiter) {
		return split(source, delimiter, 0);
	}

	public static String[] split(String source, String delimiter, int limit) {
		if (isEmpty(source) || isEmpty(delimiter)) {
			return null;
		}
		if (limit == 0) {
			limit = source.length();
		}
		List<String> result = new LinkedList<String>();
		int length = delimiter.length();
		if (limit > 0) {
			int start = 0;
			int end = 0;
			for (int i = 1; i < limit; i++) {
				end = source.indexOf(delimiter, start);
				if (end < 0) {
					break;
				}
				result.add(source.substring(start, end));
				start = end + length;
			}
			result.add(source.substring(start));
		} else {
			int start = 0;
			int end = source.length();
			for (int i = -1; i > limit; i--) {
				start = source.lastIndexOf(delimiter, end - 1);
				if (start < 0) {
					break;
				}
				result.add(source.substring(start + length, end));
				end = start;
			}
			result.add(source.substring(0, end));
		}
		return result.toArray(new String[result.size()]);
	}

	public static String replace(String source, char search, char replace) {
		if (isEmpty(source)) {
			return source;
		}
		return source.replace(search, replace);
	}

	public static String replace(String source, String search, String replace) {
		if (isEmpty(source) || isEmpty(search)) {
			return source;
		}
		int start = 0;
		int end = source.indexOf(search, start);
		if (end == -1) {
			return source;
		}
		if (replace == null) {
			replace = "";
		}
		int searchLength = search.length();
		int replaceLength = source.length() - replace.length();
		replaceLength = (replaceLength < 0 ? 0 : replaceLength);
		StrBuilder builder = new StrBuilder(source.length() + replaceLength);
		while (end != -1) {
			builder.append(source.substring(start, end)).append(replace);
			start = end + searchLength;
			end = source.indexOf(search, start);
		}
		builder.append(source.substring(start));
		return builder.toString();
	}

	public static String replaceColorCode(String source, boolean randomColor) {
		if (isEmpty(source)) {
			return null;
		}
		if (randomColor) {
			source = replaceRandomColor(source, "&rc");
		}
		return ChatColor.translateAlternateColorCodes('&', source);
	}

	public static String replaceRandomColor(String source, String search) {
		if (isEmpty(source) || isEmpty(search)) {
			return source;
		}
		int start = 0;
		int end = source.indexOf(search, start);
		if (end == -1) {
			return source;
		}
		int searchLength = search.length();
		StrBuilder builder = new StrBuilder(source.length() * 2);
		while (end != -1) {
			builder.append(source.substring(start, end));
			builder.append(ChatColor.getByChar(Integer.toHexString(RANDOM.nextInt(16))).toString());
			start = end + searchLength;
			end = source.indexOf(search, start);
		}
		builder.append(source.substring(start));
		return builder.toString();
	}

	public static String getColors(String source) {
		if (isEmpty(source)) {
			return source;
		}
		char[] chars = source.toCharArray();
		StringBuilder builder = new StringBuilder(chars.length);
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] != '§' || (i + 1)  >= chars.length) {
				continue;
			}
			if (ChatColor.getByChar(Character.toLowerCase(chars[i + 1])) != null) {
				builder.append(chars[i++]).append(chars[i]);
			}
		}
		return builder.toString();
	}

	public static String createString(String[] args, int start) {
		if (isEmpty(args)) {
			return null;
		}
		StrBuilder builder = new StrBuilder();
		for (int i = start; i < args.length; i++) {
			builder.append(args[i]);
			if (i != (args.length - 1)) {
				builder.append(" ");
			}
		}
		return builder.toString();
	}

	public static String removeStart(String source, String prefix) {
		if (isEmpty(source) || isEmpty(prefix)) {
			return source;
		}
		return source.startsWith(prefix) ? source.substring(prefix.length(), source.length()) : source;
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