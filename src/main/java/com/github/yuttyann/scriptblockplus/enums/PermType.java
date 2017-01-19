package com.github.yuttyann.scriptblockplus.enums;

public enum PermType {
	CHECK("check"),
	ADD("add"),
	REMOVE("remove");

	private String type;

	private PermType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return type;
	}
}