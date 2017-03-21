package com.github.yuttyann.scriptblockplus.manager;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

import com.github.yuttyann.scriptblockplus.BlockLocation;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.PermType;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.option.Amount;
import com.github.yuttyann.scriptblockplus.option.Cooldown;
import com.github.yuttyann.scriptblockplus.option.Delay;
import com.github.yuttyann.scriptblockplus.option.Group;
import com.github.yuttyann.scriptblockplus.option.ItemCost;
import com.github.yuttyann.scriptblockplus.option.ItemHand;
import com.github.yuttyann.scriptblockplus.option.MoneyCost;
import com.github.yuttyann.scriptblockplus.option.OptionPrefix;
import com.github.yuttyann.scriptblockplus.option.Perm;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public class ScriptReadManager extends OptionPrefix {

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
	private ItemHand itemHand;
	private ItemCost itemCost;
	private MoneyCost moneyCost;
	private boolean isBypass;
	private boolean isSuccess;

	public ScriptReadManager(ScriptBlock plugin, BlockLocation location, ScriptType scriptType) {
		this.plugin = plugin;
		this.location = location;
		this.scriptType = scriptType;
	}

	public void init(boolean isAll) {
		if (isAll) {
			location = null;
			scriptType = null;
		}
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
		itemHand = null;
		itemCost = null;
		moneyCost = null;
		isBypass = false;
		isSuccess = false;
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

	public ItemHand getItemHand() {
		return itemHand;
	}

	public ItemCost getItemCost() {
		return itemCost;
	}

	public MoneyCost getMoneyCost() {
		return moneyCost;
	}

	public boolean isBypass() {
		return isBypass;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public boolean hasOption() {
		return player != null
				|| server != null
				|| say != null
				|| perm != null
				|| permADD != null
				|| permREMOVE != null
				|| group != null
				|| groupADD != null
				|| groupREMOVE != null
				|| amount != null
				|| delay != null
				|| cooldown != null
				|| itemHand != null
				|| itemCost != null
				|| moneyCost != null;
	}

	public boolean checkScript(String script) {
		try {
			boolean hasOption = script.trim().startsWith("[");
			if (!hasOption && check(script) && !isSuccess) {
				isSuccess = true;
			} else if (hasOption) {
				for (String s : getScripts(script)) {
					if (!check(s) || isSuccess) {
						continue;
					}
					isSuccess = true;
				}
			}
		} catch (Exception e) {
			isSuccess = false;
		}
		return isSuccess;
	}

	public boolean readScript(String script) {
		try {
			if (!checkScript(script)) {
				throw new Exception();
			}
			if (!script.trim().startsWith("[")) {
				read(script);
			} else {
				for (String s : getScripts(script)) {
					read(s);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isSuccess;
	}

	private List<String> getScripts(String script) throws Exception {
		List<String> result = new ArrayList<String>();
		char[] chars = script.toCharArray();
		int start = 0;
		int end = 0;
		for(int i = 0; i < chars.length; i++) {
			if(chars[i] == '[') {
				start++;
			} else if(chars[i] == ']') {
				end++;
			}
		}
		if(start != end) {
			throw new Exception();
		}
		int count = 0;
		int index = 0;
		for(int i = 0; i < chars.length; i++) {
			if(chars[i] == '[') {
				if(count == 0) {
					index = i;
				}
				count++;
			} else if(chars[i] == ']') {
				count--;
				if(count == 0){
					result.add(script.substring(index + 1, i));
				}
			}
		}
		return result;
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
				|| script.startsWith(ITEM)
				|| script.startsWith(COST);
	}

	private void read(String script) throws Exception {
		for (String prefix : PREFIXS) {
			switch (StringUtils.startText(script, prefix, "")) {
			case COMMAND:
				command = removeStart(script, prefix);
				if (!command.startsWith("/")) {
					command = new StringBuilder(command).insert(0, "/").toString();
				}
				return;
			case BYPASS:
				isBypass = true;
				command = removeStart(script, prefix);
				if (!command.startsWith("/")) {
					command = new StringBuilder(command).insert(0, "/").toString();
				}
				return;
			case PLAYER:
				player = removeStart(script, prefix);
				return;
			case SERVER:
				server = removeStart(script, prefix);
				return;
			case SAY:
				say = removeStart(script, prefix);
				return;
			case PERM:
				perm = getPerm(removeStart(script, prefix), PermType.CHECK);
				return;
			case PERM_ADD:
				permADD = getPerm(removeStart(script, prefix), PermType.ADD);
				return;
			case PERM_REMOVE:
				permREMOVE = getPerm(removeStart(script, prefix), PermType.REMOVE);
				return;
			case GROUP:
				group = getGroup(removeStart(script, prefix), PermType.CHECK);
				return;
			case GROUP_ADD:
				groupADD = getGroup(removeStart(script, prefix), PermType.ADD);
				return;
			case GROUP_REMOVE:
				groupREMOVE = getGroup(removeStart(script, prefix), PermType.REMOVE);
				return;
			case AMOUNT:
				amount = new Amount(plugin, removeStart(script, prefix), location, scriptType);
				return;
			case DELAY:
				delay = new Delay(plugin, removeStart(script, prefix));
				return;
			case COOLDOWN:
				cooldown = new Cooldown(plugin, removeStart(script, prefix));
				return;
			case HAND:
				itemHand = getItemHand(removeStart(script, prefix));
				return;
			case ITEM:
				itemCost = getItem(removeStart(script, prefix));
				return;
			case COST:
				moneyCost = new MoneyCost(removeStart(script, prefix));
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

	private ItemHand getItemHand(String hand) {
		String[] array = StringUtils.split(hand, ":");
		switch (array.length) {
		case 1:
			return new ItemHand(getId(array[0]), 1, (short) 0, null);
		case 2:
			return new ItemHand(getId(array[0]), parseInt(array[1]), (short) 0, null);
		case 3:
			return new ItemHand(getId(array[0]), parseInt(array[1]), parseShort(array[2]), null);
		case 4:
			String itemName = StringUtils.createString(array, 3);
			return new ItemHand(getId(array[0]), parseInt(array[1]), parseShort(array[2]), itemName);
		}
		return null;
	}

	private ItemCost getItem(String itemCost) {
		String[] array = StringUtils.split(itemCost, ":");
		switch (array.length) {
		case 1:
			return new ItemCost(getId(array[0]), 1, (short) 0, null);
		case 2:
			return new ItemCost(getId(array[0]), parseInt(array[1]), (short) 0, null);
		case 3:
			return new ItemCost(getId(array[0]), parseInt(array[1]), parseShort(array[2]), null);
		case 4:
			String itemName = StringUtils.createString(array, 3);
			return new ItemCost(getId(array[0]), parseInt(array[1]), parseShort(array[2]), itemName);
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	private int getId(String source) {
		if (isNumber(source)) {
			return parseInt(source);
		}
		return Material.getMaterial(source.toUpperCase()).getId();
	}

	private boolean isNumber(String source) {
		return source.matches("^-?[0-9]{1,9}$");
	}

	private int parseInt(String source) {
		return Integer.parseInt(source);
	}

	private short parseShort(String source) {
		return Short.parseShort(source);
	}

	private String removeStart(String text, String prefix) {
		return StringUtils.removeStart(text, prefix).trim();
	}
}