package com.isgk.colorblock.util.string;

public final class StringUtil {

	public static String removeBracket(String key) {
		int begin = 0;
		int end = key.length() - 1;
		boolean flag = true;
		while (flag && key.charAt(begin) == '(' && key.charAt(end) == ')') {
			int bracket = 0;
			int bracketEnd = begin;
			for (int i = begin; i <= end; i++) {
				char ch = key.charAt(i);
				if (ch == '(') {
					bracket++;
				} else if (ch == ')') {
					bracket--;
				}
				if (bracket == 0) {
					bracketEnd = i;
					break;
				}
			}
			if (bracketEnd == end) {
				begin++;
				end--;
			} else {
				break;
			}
		}
		return key.substring(begin, end + 1);
	}

}
