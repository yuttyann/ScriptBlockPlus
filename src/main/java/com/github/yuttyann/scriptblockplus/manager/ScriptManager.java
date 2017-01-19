package com.github.yuttyann.scriptblockplus.manager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.yuttyann.scriptblockplus.enums.PermType;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.option.Amount;
import com.github.yuttyann.scriptblockplus.option.Cooldown;
import com.github.yuttyann.scriptblockplus.option.Delay;
import com.github.yuttyann.scriptblockplus.option.Group;
import com.github.yuttyann.scriptblockplus.option.ItemCost;
import com.github.yuttyann.scriptblockplus.option.MoneyCost;
import com.github.yuttyann.scriptblockplus.option.OptionPrefix;
import com.github.yuttyann.scriptblockplus.option.Perm;
import com.github.yuttyann.scriptblockplus.utils.BlockLocation;

public class ScriptManager extends OptionPrefix {

	private static final String REGEX = "\\[(.+?)\\]";

	private BlockLocation location;
	private ScriptType scriptType;
	private String command;
	private String player;
	private String server;
	private String say;
	private Perm perm;
	private Perm permADD;
	private Perm permREMOVE;
	private Group group;
	private Group groupADD;
	private Group groupREMOVE;
	private Amount amount;
	private Delay delay;
	private Cooldown cooldown;
	private ItemCost itemCost;
	private MoneyCost moneyCost;
	private boolean isBypass;
	private boolean isSuccess;

	public ScriptManager(BlockLocation location, ScriptType scriptType) {
		this.location = location;
		this.scriptType = scriptType;
	}

	public BlockLocation getBlockLocation() {
		return location;
	}

	public ScriptType getScriptType() {
		return scriptType;
	}

	public String getCommand() {
		return command;
	}

	public String getPlayer() {
		return player;
	}

	public String getServer() {
		return server;
	}

	public String getSay() {
		return say;
	}

	public Perm getPerm() {
		return perm;
	}

	public Perm getPermADD() {
		return permADD;
	}

	public Perm getPermREMOVE() {
		return permREMOVE;
	}

	public Group getGroup() {
		return group;
	}

	public Group getGroupADD() {
		return groupADD;
	}

	public Group getGroupREMOVE() {
		return groupREMOVE;
	}

	public Amount getAmount() {
		return amount;
	}

	public Delay getDelay() {
		return delay;
	}

	public Cooldown getCooldown() {
		return cooldown;
	}

	public ItemCost getItemCost() {
		return itemCost;
	}

	public MoneyCost getMoneyCost() {
		return moneyCost;
	}

	public boolean hasOption() {
		return player != null || server != null || say != null || perm != null || permADD != null || permREMOVE != null
				|| group != null || groupADD != null || groupREMOVE != null || amount != null
				|| delay != null || cooldown != null || moneyCost != null || itemCost != null;
	}

	public boolean isBypass() {
		return isBypass;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public void resetAll() {
		location = null;
		scriptType = null;
		reset();
	}

	public void reset() {
		command = null;
		player = null;
		server = null;
		say = null;
		group = null;
		groupADD = null;
		groupREMOVE = null;
		amount = null;
		delay = null;
		cooldown = null;
		itemCost = null;
		moneyCost = null;
		isBypass = false;
		isSuccess = true;
	}

	public boolean checkScript(String script) {
		try {
			Pattern pattern = Pattern.compile(REGEX);
			Matcher matcher = pattern.matcher(script);
			if (!matcher.find() || !script.startsWith("[")) {
				if (!check(script) && isSuccess) {
					isSuccess = false;
				}
			} else {
				matcher.reset();
				while (matcher.find()) {
					if (!check(matcher.group(1)) && isSuccess) {
						isSuccess = false;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			isSuccess = false;
		}
		return isSuccess;
	}

	public boolean readScript(String script) {
		try {
			Pattern pattern = Pattern.compile(REGEX);
			Matcher matcher = pattern.matcher(script);
			if (!matcher.find() || !script.startsWith("[")) {
				read(script);
			} else {
				matcher.reset();
				while (matcher.find()) {
					read(matcher.group(1));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			isSuccess = false;
		}
		return isSuccess;
	}

	private boolean check(String script) throws Exception {
		return script.startsWith(COMMAND)
			|| script.startsWith(BYPASS)
			|| script.startsWith(PLAYER)
			|| script.startsWith(SERVER)
			|| script.startsWith(SAY)
			|| script.startsWith(PERM)
			|| script.startsWith(PERM_ADD)
			|| script.startsWith(PERM_REMOVE)
			|| script.startsWith(GROUP)
			|| script.startsWith(GROUP_ADD)
			|| script.startsWith(GROUP_REMOVE)
			|| script.startsWith(AMOUNT)
			|| script.startsWith(DELAY)
			|| script.startsWith(COOLDOWN)
			|| script.startsWith(COST)
			|| script.startsWith(ITEM);
	}

	private void read(String script) throws Exception {
		for (String prefix : PREFIXS) {
			switch (startsWith(script, prefix)) {
			case COMMAND:
				command = removeFirst(script, prefix).trim();
				if (!command.startsWith("/")) {
					command = new StringBuilder(command).insert(0, "/").toString();
				}
				return;
			case BYPASS:
				isBypass = true;
				command = removeFirst(script, prefix).trim();
				if (!command.startsWith("/")) {
					command = new StringBuilder(command).insert(0, "/").toString();
				}
				return;
			case PLAYER:
				player = removeFirst(script, prefix).trim();
				return;
			case SERVER:
				server = removeFirst(script, prefix).trim();
				return;
			case SAY:
				say = removeFirst(script, prefix).trim();
				return;
			case PERM:
				perm = new Perm(removeFirst(script, prefix).trim(), PermType.CHECK);
				return;
			case PERM_ADD:
				permADD = new Perm(removeFirst(script, prefix).trim(), PermType.ADD);
				return;
			case PERM_REMOVE:
				permREMOVE = new Perm(removeFirst(script, prefix).trim(), PermType.REMOVE);
				return;
			case GROUP:
				group = new Group(removeFirst(script, prefix).trim(), PermType.CHECK);
				return;
			case GROUP_ADD:
				groupADD = new Group(removeFirst(script, prefix).trim(), PermType.ADD);
				return;
			case GROUP_REMOVE:
				groupREMOVE = new Group(removeFirst(script, prefix).trim(), PermType.REMOVE);
				return;
			case AMOUNT:
				amount = new Amount(removeFirst(script, prefix).trim(), location, scriptType);
				return;
			case DELAY:
				delay = new Delay(removeFirst(script, prefix).trim());
				return;
			case COOLDOWN:
				cooldown = new Cooldown(removeFirst(script, prefix).trim());
				return;
			case COST:
				moneyCost = new MoneyCost(removeFirst(script, prefix).trim());
				return;
			case ITEM:
				String[] split = removeFirst(script, prefix).trim().split(":");
				switch (split.length) {
				case 1:
					itemCost = new ItemCost(Integer.parseInt(split[0]), 1, (short) 0);
					return;
				case 2:
					itemCost = new ItemCost(Integer.parseInt(split[0]), Integer.parseInt(split[1]), (short) 0);
					return;
				case 3:
					itemCost = new ItemCost(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Short.parseShort(split[2]));
					return;
				}
			}
		}
	}

	private static String startsWith(String strs, String prefix) {
		if (strs.startsWith(prefix)) {
			return strs.substring(0, prefix.length());
		}
		return "null";
	}

	private String removeFirst(String all, String prefix) {
		if (all.startsWith(prefix)) {
			return all.substring(prefix.length(), all.length());
		}
		return all;
	}
}