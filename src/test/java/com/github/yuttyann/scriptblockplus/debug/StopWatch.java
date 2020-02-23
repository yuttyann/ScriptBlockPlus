package com.github.yuttyann.scriptblockplus.debug;

public final class Stopwatch {

	private final boolean isNano;

	private long start, end;

	public Stopwatch(boolean isNano) {
		this.isNano = isNano;
	}

	public boolean isNano() {
		return isNano;
	}

	public long getStart() {
		return start;
	}

	public long getEnd() {
		return end;
	}

	public long getResult() {
		long result = end - start;
		return result < 0 ? 0 : result;
	}

	public void init() {
		start = 0L;
		end = 0L;
	}

	public void start() {
		init();
		start = isNano ? System.nanoTime() : System.currentTimeMillis();
	}

	public void end() {
		end = isNano ? System.nanoTime() : System.currentTimeMillis();
	}

	public void resultView() {
		resultView("");
	}

	public void resultView(String prefix) {
		System.out.println(prefix + getResult() + (isNano ? "ns" : "ms"));
	}
}