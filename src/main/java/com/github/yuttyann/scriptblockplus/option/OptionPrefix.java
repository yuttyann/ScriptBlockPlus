package com.github.yuttyann.scriptblockplus.option;

public class OptionPrefix {

	protected static final String COMMAND = "@command ";
	protected static final String BYPASS = "@bypass ";
	protected static final String PLAYER = "@player ";
	protected static final String SERVER = "@server ";
	protected static final String SAY = "@say ";
	protected static final String PERM = "@perm:";
	protected static final String PERM_ADD = "@permADD:";
	protected static final String PERM_REMOVE = "@permREMOVE:";
	protected static final String GROUP = "@group:";
	protected static final String GROUP_ADD = "@groupADD:";
	protected static final String GROUP_REMOVE = "@groupREMOVE:";
	protected static final String AMOUNT = "@amount:";
	protected static final String DELAY = "@delay:";
	protected static final String COOLDOWN = "@cooldown:";
	protected static final String HAND = "@hand:";
	protected static final String ITEM = "$item:";
	protected static final String COST = "$cost:";

	public static final String[] PREFIXS = {
		COMMAND, BYPASS, PLAYER, SERVER, SAY, PERM, PERM_ADD, PERM_REMOVE,
		GROUP, GROUP_ADD, GROUP_REMOVE, AMOUNT, DELAY, COOLDOWN, HAND, ITEM, COST
	};
}