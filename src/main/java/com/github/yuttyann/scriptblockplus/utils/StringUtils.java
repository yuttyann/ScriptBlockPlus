package com.github.yuttyann.scriptblockplus.utils;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {

	public static String[] split(String text, String delimiter) {
		return split(text, delimiter, 0);
	}

	public static String[] split(String text, String delimiter, int limit) {
		List<String> result = new ArrayList<String>();
		int delimiterLength = delimiter.length();
		if (limit == 0) {
			limit = text.length();
		}
		if (limit > 0) {
			int start = 0;
			int end = 0;
			for (int i = 1; i < limit; i++) {
				end = text.indexOf(delimiter, start);
				if (end == -1) {
					break;
				}
				result.add(text.substring(start, end));
				start = end + delimiterLength;
			}
			result.add(text.substring(start));
		} else {
			int start = 0;
			int end = text.length();
			for (int i = -1; i > limit; i--) {
				start = text.lastIndexOf(delimiter, end - 1);
				if (start == -1) {
					break;
				}
				result.add(text.substring(start + delimiterLength, end));
				end = start;
			}
			result.add(text.substring(0, end));
		}
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
		for (int i = start, l = args.length; i < l; i++) {
			builder.append(args[i]);
			if (i != (l - 1)) {
				builder.append(glue);
			}
		}
		return builder.toString();
	}

	public static String startText(String text, String prefix) {
		return startText(text, prefix, null);
	}

	public static String startText(String text, String prefix, String nullCase) {
		if (text.startsWith(prefix)) {
			return text.substring(0, prefix.length());
		}
		return nullCase;
	}

	public static String endText(String text, String suffix) {
		return endText(text, suffix, null);
	}

	public static String endText(String text, String suffix, String nullCase) {
		if (text.endsWith(suffix)) {
			return text.substring(text.length() - suffix.length());
		}
		return nullCase;
	}

	public static String removeStart(String text, String prefix) {
		if (text.startsWith(prefix)) {
			return text.substring(prefix.length(), text.length());
		}
		return text;
	}

	public static String removeEnd(String text, String prefix) {
		if (text.endsWith(prefix)) {
			return text.substring(0, text.length() - prefix.length());
		}
		return text;
	}

	public static String remove(String text, String remove) {
		return replace(text, remove, "");
	}

	public static String remove(String text, char remove) {
		char[] array = text.toCharArray();
		int count = 0;
		for (int i = 0; i < array.length; i++) {
			if (array[i] != remove) {
				array[(count++)] = array[i];
			}
		}
		return new String(array, 0, count);
	}

	public static int counter(String text, char search) {
		int count = 0;
		for(char i : text.toCharArray()) {
			if(i == search) {
				count++;
			}
		}
		return count;
	}

	public static int counter(String text, String search) {
		int count = 0;
		int index = 0;
		int searchLength = search.length();
		while ((index = text.indexOf(search, index)) != -1) {
			count++;
			index += searchLength;
		}
		return count;
	}
}