package com.github.yuttyann.scriptblockplus.manager;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.script.option.chat.Bypass;
import com.github.yuttyann.scriptblockplus.script.option.chat.Command;
import com.github.yuttyann.scriptblockplus.script.option.chat.Say;
import com.github.yuttyann.scriptblockplus.script.option.chat.Server;
import com.github.yuttyann.scriptblockplus.script.option.chat.ToPlayer;
import com.github.yuttyann.scriptblockplus.script.option.other.Amount;
import com.github.yuttyann.scriptblockplus.script.option.other.ItemCost;
import com.github.yuttyann.scriptblockplus.script.option.other.ItemHand;
import com.github.yuttyann.scriptblockplus.script.option.time.Cooldown;
import com.github.yuttyann.scriptblockplus.script.option.time.Delay;
import com.github.yuttyann.scriptblockplus.script.option.vault.Group;
import com.github.yuttyann.scriptblockplus.script.option.vault.GroupAdd;
import com.github.yuttyann.scriptblockplus.script.option.vault.GroupRemove;
import com.github.yuttyann.scriptblockplus.script.option.vault.MoneyCost;
import com.github.yuttyann.scriptblockplus.script.option.vault.Perm;
import com.github.yuttyann.scriptblockplus.script.option.vault.PermAdd;
import com.github.yuttyann.scriptblockplus.script.option.vault.PermRemove;


public class OptionManager {

	private final static Option[] EMPTY_OPTION_ARRAY = {};
	private final static List<Constructor<? extends Option>> CONSTRUCTORS;

	private List<Option> cacheList;

	static {
		CONSTRUCTORS = new LinkedList<Constructor<? extends Option>>();
	}

	public void registerDefaultOptions() {
		CONSTRUCTORS.clear();
		add(MoneyCost.class);
		add(ItemCost.class);
		add(ItemHand.class);
		add(Cooldown.class);
		add(Delay.class);
		add(Group.class);
		add(Perm.class);
		add(GroupAdd.class);
		add(GroupRemove.class);
		add(PermAdd.class);
		add(PermRemove.class);
		add(Amount.class);
		add(ToPlayer.class);
		add(Server.class);
		add(Say.class);
		add(Bypass.class);
		add(Command.class);
	}

	public void add(Class<? extends Option> option) {
		CONSTRUCTORS.add(getConstructor(option));
	}

	public void add(int index, Class<? extends Option> option) {
		CONSTRUCTORS.add(index, getConstructor(option));
	}

	public void remove(Class<? extends Option> option) {
		CONSTRUCTORS.remove(getConstructor(option));
	}

	public void remove(int index) {
		CONSTRUCTORS.remove(index);
	}

	public int indexOf(Class<? extends Option> option) {
		return CONSTRUCTORS.indexOf(getConstructor(option));
	}

	public boolean isEmpty() {
		return CONSTRUCTORS.isEmpty();
	}

	public List<Option> getOptions() {
		if (cacheList == null) {
			newInstances();
		}
		return Collections.unmodifiableList(cacheList);
	}

	public List<Constructor<? extends Option>> getConstructors() {
		return CONSTRUCTORS;
	}

	public Option[] newInstances() {
		return newInstances(new Option[CONSTRUCTORS.size()]);
	}

	public Option[] newInstances(Option[] options) {
		clearCache();
		Option[] instances = options;
		try {
			int i = 0;
			for (Constructor<? extends Option> constructor : CONSTRUCTORS) {
				cacheList.add(instances[i++] = constructor.newInstance());
			}
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
			return EMPTY_OPTION_ARRAY;
		}
		return instances;
	}

	public Option newInstance(Option option) {
		try {
			for (Constructor<? extends Option> constructor : CONSTRUCTORS) {
				if (option.getClass().equals(constructor.getDeclaringClass())) {
					return constructor.newInstance();
				}
			}
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
		return null;
	}

	private <T extends Option> Constructor<T> getConstructor(Class<T> clazz) {
		try {
			Constructor<T> constructor = clazz.getDeclaredConstructor();
			constructor.setAccessible(true);
			return constructor;
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Option> clearCache() {
		if (cacheList == null) {
			cacheList = new ArrayList<Option>(CONSTRUCTORS.size());
		} else {
			cacheList.clear();
		}
		return cacheList;
	}
}