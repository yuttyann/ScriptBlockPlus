package com.github.yuttyann.scriptblockplus.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.yuttyann.scriptblockplus.script.ScriptException;

public class StringUtils {

	public static List<String> getScripts(String script) throws ScriptException {
		char[] chars = script.toCharArray();
		if (chars[0] != '[' && chars[chars.length - 1] != ']') {
			return Arrays.asList(script);
		}
		for (int i = 0, j = 0, k = 0; i < chars.length; i++) {
			if (chars[i] == '[') {
				j++;
			} else if (chars[i] == ']') {
				k++;
			}
			if (i == (chars.length - 1) && j != k) {
				throw new ScriptException("Failed to load the script.");
			}
		}
		List<String> result = new ArrayList<String>();
		for (int i = 0, j = 0, k = 0; i < chars.length; i++) {
			if (chars[i] == '[') {
				if (j == 0) {
					k = i;
				}
				j++;
			} else if (chars[i] == ']') {
				j--;
				if (j == 0) {
					result.add(script.substring(k + 1, i));
				}
			}
		}
		return result;
	}

	public static String[] split(String text, String delimiter) {
		List<String> result = new ArrayList<String>();
		int start = 0, end = 0;
		for (int i = 1; i < text.length(); i++) {
			end = text.indexOf(delimiter, start);
			if (end == -1) {
				break;
			}
			result.add(text.substring(start, end));
			start = end + delimiter.length();
		}
		result.add(text.substring(start));
		return result.toArray(new String[result.size()]);
	}

	public static String replace(String text, String search, String replace) {
		int start = 0;
		int end = text.indexOf(search, start);
		if (end == -1) {
			return text;
		}
		int searchLength = search.length();
		StringBuilder builder = new StringBuilder(text.length());
		while (end != -1) {
			builder.append(text.substring(start, end));
			builder.append(replace);
			start = end + searchLength;
			end = text.indexOf(search, start);
		}
		builder.append(text.substring(start));
		return builder.toString();
	}

	public static String createString(String[] args, int start) {
		return createString(args, start, " ");
	}

	public static String createString(String[] args, int start, String glue) {
		StringBuilder builder = new StringBuilder();
		for (int i = start; i < args.length; i++) {
			builder.append(args[i]);
			if (i != (args.length - 1)) {
				builder.append(glue);
			}
		}
		return builder.toString();
	}

	public static String startText(String text, String prefix, String nullCase) {
		if (text.startsWith(prefix)) {
			return text.substring(0, prefix.length());
		}
		return nullCase;
	}

	public static String removeStart(String text, String prefix) {
		if (text.startsWith(prefix)) {
			return text.substring(prefix.length(), text.length());
		}
		return text;
	}
}