package com.github.yuttyann.scriptblockplus.debug;

import java.util.function.Consumer;

public class DebugStream {

	public static <T> void swForEach(T t,  Consumer<T> action, int limit, String prefix, boolean isNano) {
		forEach(t, action, limit); // 最適化
		Stopwatch stopWatch = new Stopwatch(isNano);
		stopWatch.start();
		forEach(t, action, limit);
		stopWatch.end();
		stopWatch.resultView(prefix);
	}

	public static <T> void forEach(T t,  Consumer<T> action, int limit) {
		for (int i = 0; i < limit; i++) action.accept(t);
	}
}