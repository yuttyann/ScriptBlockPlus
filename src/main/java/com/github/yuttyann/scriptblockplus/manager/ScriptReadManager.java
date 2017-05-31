package com.github.yuttyann.scriptblockplus.manager;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;

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
	private Location location;
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

	public ScriptReadManager(ScriptBlock plugin, Location location, ScriptType scriptType) {
		this.plugin = plugin;
		this.location = location;
		this.scriptType = scriptType;
	}

	public void init() {
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

	public Location getLocation() {
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
		boolean isSuccess = false;
		try {
			boolean hasOption = script.startsWith("[");
			if (!hasOption && check(script)) {
				isSuccess = true;
			} else if (hasOption) {
				for (String str : getScripts(script)) {
					if (isSuccess || !check(str)) {
						continue;
					}
					isSuccess = true;
				}
			}
		} catch (Exception e) {}
		return this.isSuccess = isSuccess;
	}

	private boolean check(String script) {
		for (String prefix : PREFIXS) {
			if (script.startsWith(prefix)) {
				return true;
			}
		}
		return false;
	}

	public boolean readScript(String script) {
		boolean isSuccess = true;
		try {
			if (!(isSuccess = checkScript(script))) {
				throw new Exception();
			}
			if (!script.startsWith("[")) {
				read(script);
			} else {
				for (String str : getScripts(script)) {
					read(str);
				}
			}
		} catch (Exception e) {
			isSuccess = false;
		}
		return this.isSuccess = isSuccess;
	}

	private List<String> getScripts(String script) throws Exception {
		List<String> result = new ArrayList<String>();
		char[] chars = script.toCharArray();
		int start = 0;
		int end = 0;
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == '[') {
				start++;
			} else if (chars[i] == ']') {
				end++;
			}
		}
		if (start != end) {
			throw new Exception();
		}
		int count = 0;
		int index = 0;
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == '[') {
				if (count == 0) {
					index = i;
				}
				count++;
			} else if (chars[i] == ']') {
				count--;
				if (count == 0) {
					result.add(script.substring(index + 1, i));
				}
			}
		}
		return result;
	}

	private void read(String script) throws Exception {
		for (String prefix : PREFIXS) {
			String optionText = removeStart(script, prefix);
			switch (StringUtils.startText(script, prefix, "")) {
			case COMMAND:
				command = optionText;
				if (!command.startsWith("/")) {
					command = new StringBuilder(command).insert(0, "/").toString();
				}
				return;
			case BYPASS:
				isBypass = true;
				command = optionText;
				if (!command.startsWith("/")) {
					command = new StringBuilder(command).insert(0, "/").toString();
				}
				return;
			case PLAYER:
				player = optionText;
				return;
			case SERVER:
				server = optionText;
				return;
			case SAY:
				say = optionText;
				return;
			case PERM:
				perm = getPerm(optionText, PermType.CHECK);
				return;
			case PERM_ADD:
				permADD = getPerm(optionText, PermType.ADD);
				return;
			case PERM_REMOVE:
				permREMOVE = getPerm(optionText, PermType.REMOVE);
				return;
			case GROUP:
				group = getGroup(optionText, PermType.CHECK);
				return;
			case GROUP_ADD:
				groupADD = getGroup(optionText, PermType.ADD);
				return;
			case GROUP_REMOVE:
				groupREMOVE = getGroup(optionText, PermType.REMOVE);
				return;
			case AMOUNT:
				amount = new Amount(plugin, optionText, location, scriptType);
				return;
			case DELAY:
				delay = new Delay(plugin, optionText);
				return;
			case COOLDOWN:
				cooldown = new Cooldown(plugin, optionText);
				return;
			case HAND:
				itemHand = getItemHand(optionText);
				return;
			case ITEM:
				itemCost = getItemCost(optionText);
				return;
			case COST:
				moneyCost = new MoneyCost(optionText);
				return;
			}
		}
	}

	private Perm getPerm(String permission, PermType permType) {
		String[] array = split(permission, "/");
		if (array.length == 1) {
			return new Perm(array[0], permType);
		} else {
			return new Perm(array[0], array[1], permType);
		}
	}

	private Group getGroup(String group, PermType permType) {
		String[] array = split(group, "/");
		if (array.length == 1) {
			return new Group(array[0], permType);
		} else {
			return new Group(array[0], array[1], permType);
		}
	}

	private ItemHand getItemHand(String itemHand) {
		String[] array = split(itemHand, " ");
		String[] array2 = split(array[0], ":");
		String damage = "0";
		if (array2.length > 1) {
			damage = array2[1];
		}
		return new ItemHand(getId(array2[0]), array[1], damage, array.length > 2 ? createString(array, 2) : null);
	}

	private ItemCost getItemCost(String itemCost) {
		String[] array = split(itemCost, " ");
		String[] array2 = split(array[0], ":");
		String damage = "0";
		if (array2.length > 1) {
			damage = array2[1];
		}
		return new ItemCost(getId(array2[0]), array[1], damage, array.length > 2 ? createString(array, 2) : null);
	}

	@SuppressWarnings("deprecation")
	private String getId(String source) {
		if (isNumber(source)) {
			return source;
		}
		return String.valueOf(Material.getMaterial(source.toUpperCase()).getId());
	}

	private boolean isNumber(String source) {
		try {
			Integer.parseInt(source);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public String[] split(String text, String delimiter) {
		return StringUtils.split(text, delimiter, 0);
	}

	private String removeStart(String text, String prefix) {
		return StringUtils.removeStart(text, prefix).trim();
	}

	public String createString(String[] args, int start) {
		return StringUtils.createString(args, start);
	}
}