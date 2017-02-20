package com.github.yuttyann.scriptblockplus.manager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.yuttyann.scriptblockplus.BlockLocation;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.PermType;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.option.Amount;
import com.github.yuttyann.scriptblockplus.option.Cooldown;
import com.github.yuttyann.scriptblockplus.option.Delay;
import com.github.yuttyann.scriptblockplus.option.Group;
import com.github.yuttyann.scriptblockplus.option.Hand;
import com.github.yuttyann.scriptblockplus.option.ItemCost;
import com.github.yuttyann.scriptblockplus.option.MoneyCost;
import com.github.yuttyann.scriptblockplus.option.OptionPrefix;
import com.github.yuttyann.scriptblockplus.option.Perm;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public class ScriptReadManager extends OptionPrefix {

	private static final String REGEX = "\\[(.+?)\\]";

	private ScriptBlock plugin;
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
	private Hand hand;
	private ItemCost itemCost;
	private MoneyCost moneyCost;
	private boolean isBypass;
	private boolean isSuccess;

	public ScriptReadManager(ScriptBlock plugin, BlockLocation location, ScriptType scriptType) {
		this.plugin = plugin;
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

	public Hand getHand() {
		return hand;
	}

	public ItemCost getItemCost() {
		return itemCost;
	}

	public MoneyCost getMoneyCost() {
		return moneyCost;
	}

	public boolean hasOption() {
		return player != null || server != null || say != null || perm != null || permADD != null || permREMOVE != null
				|| group != null || groupADD != null || groupREMOVE != null || amount != null || delay != null
				|| cooldown != null || hand != null || moneyCost != null || itemCost != null;
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
		perm = null;
		permADD = null;
		permREMOVE = null;
		group = null;
		groupADD = null;
		groupREMOVE = null;
		amount = null;
		delay = null;
		cooldown = null;
		hand = null;
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

	private boolean check(String script) {
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
				|| script.startsWith(HAND)
				|| script.startsWith(COST)
				|| script.startsWith(ITEM);
	}

	private void read(String script) throws Exception {
		for (String prefix : PREFIXS) {
			switch (StringUtils.startText(script, prefix, "")) {
			case COMMAND:
				command = StringUtils.removeStart(script, prefix).trim();
				if (!command.startsWith("/")) {
					command = new StringBuilder(command).insert(0, "/").toString();
				}
				return;
			case BYPASS:
				isBypass = true;
				command = StringUtils.removeStart(script, prefix).trim();
				if (!command.startsWith("/")) {
					command = new StringBuilder(command).insert(0, "/").toString();
				}
				return;
			case PLAYER:
				player = StringUtils.removeStart(script, prefix).trim();
				return;
			case SERVER:
				server = StringUtils.removeStart(script, prefix).trim();
				return;
			case SAY:
				say = StringUtils.removeStart(script, prefix).trim();
				return;
			case PERM:
				perm = getPerm(StringUtils.removeStart(script, prefix).trim(), PermType.CHECK);
				return;
			case PERM_ADD:
				permADD = getPerm(StringUtils.removeStart(script, prefix).trim(), PermType.ADD);
				return;
			case PERM_REMOVE:
				permREMOVE = getPerm(StringUtils.removeStart(script, prefix).trim(), PermType.REMOVE);
				return;
			case GROUP:
				group = getGroup(StringUtils.removeStart(script, prefix).trim(), PermType.CHECK);
				return;
			case GROUP_ADD:
				groupADD = getGroup(StringUtils.removeStart(script, prefix).trim(), PermType.ADD);
				return;
			case GROUP_REMOVE:
				groupREMOVE = getGroup(StringUtils.removeStart(script, prefix).trim(), PermType.REMOVE);
				return;
			case AMOUNT:
				amount = new Amount(plugin, StringUtils.removeStart(script, prefix).trim(), location, scriptType);
				return;
			case DELAY:
				delay = new Delay(plugin, StringUtils.removeStart(script, prefix).trim());
				return;
			case COOLDOWN:
				cooldown = new Cooldown(plugin, StringUtils.removeStart(script, prefix).trim());
				return;
			case HAND:
				hand = getHand(StringUtils.removeStart(script, prefix).trim());
				return;
			case COST:
				moneyCost = new MoneyCost(StringUtils.removeStart(script, prefix).trim());
				return;
			case ITEM:
				itemCost = getItem(StringUtils.removeStart(script, prefix).trim());
				return;
			default:
				isSuccess = false;
				return;
			}
		}
	}

	private Perm getPerm(String permission, PermType permType) {
		String[] array = StringUtils.split(permission, "/");
		switch (array.length) {
		case 1:
			return new Perm(array[0], permType);
		case 2:
			return new Perm(array[0], array[1], permType);
		}
		return null;
	}

	private Group getGroup(String group, PermType permType) {
		String[] array = StringUtils.split(group, "/");
		switch (array.length) {
		case 1:
			return new Group(array[0], permType);
		case 2:
			return new Group(array[0], array[1], permType);
		}
		return null;
	}

	private Hand getHand(String hand) {
		String[] array = StringUtils.split(hand, ":");
		switch (array.length) {
		case 1:
			return new Hand(parseInt(array[0]), 1, (short) 0, null);
		case 2:
			return new Hand(parseInt(array[0]), parseInt(array[1]), (short) 0, null);
		case 3:
			return new Hand(parseInt(array[0]), parseInt(array[1]), parseShort(array[2]), null);
		case 4:
			String itemName = StringUtils.createString(array, 3);
			return new Hand(parseInt(array[0]), parseInt(array[1]), parseShort(array[2]), itemName);
		}
		return null;
	}

	private ItemCost getItem(String itemCost) {
		String[] array = StringUtils.split(itemCost, ":");
		switch (array.length) {
		case 1:
			return new ItemCost(parseInt(array[0]), 1, (short) 0, null);
		case 2:
			return new ItemCost(parseInt(array[0]), parseInt(array[1]), (short) 0, null);
		case 3:
			return new ItemCost(parseInt(array[0]), parseInt(array[1]), parseShort(array[2]), null);
		case 4:
			String itemName = StringUtils.createString(array, 3);
			return new ItemCost(parseInt(array[0]), parseInt(array[1]), parseShort(array[2]), itemName);
		}
		return null;
	}

	private int parseInt(String source) {
		return Integer.parseInt(source);
	}

	private short parseShort(String source) {
		return Short.parseShort(source);
	}
}