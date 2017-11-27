package com.github.yuttyann.scriptblockplus.manager;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import com.github.yuttyann.scriptblockplus.manager.auxiliary.AbstractConstructor;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.script.option.chat.Bypass;
import com.github.yuttyann.scriptblockplus.script.option.chat.Command;
import com.github.yuttyann.scriptblockplus.script.option.chat.Console;
import com.github.yuttyann.scriptblockplus.script.option.chat.Say;
import com.github.yuttyann.scriptblockplus.script.option.chat.Server;
import com.github.yuttyann.scriptblockplus.script.option.chat.ToPlayer;
import com.github.yuttyann.scriptblockplus.script.option.nms.ActionBar;
import com.github.yuttyann.scriptblockplus.script.option.nms.Title;
import com.github.yuttyann.scriptblockplus.script.option.other.Amount;
import com.github.yuttyann.scriptblockplus.script.option.other.Calculation;
import com.github.yuttyann.scriptblockplus.script.option.other.Execute;
import com.github.yuttyann.scriptblockplus.script.option.other.ItemCost;
import com.github.yuttyann.scriptblockplus.script.option.other.ItemHand;
import com.github.yuttyann.scriptblockplus.script.option.other.Sound;
import com.github.yuttyann.scriptblockplus.script.option.time.Cooldown;
import com.github.yuttyann.scriptblockplus.script.option.time.Delay;
import com.github.yuttyann.scriptblockplus.script.option.time.OldCooldown;
import com.github.yuttyann.scriptblockplus.script.option.vault.Group;
import com.github.yuttyann.scriptblockplus.script.option.vault.GroupAdd;
import com.github.yuttyann.scriptblockplus.script.option.vault.GroupRemove;
import com.github.yuttyann.scriptblockplus.script.option.vault.MoneyCost;
import com.github.yuttyann.scriptblockplus.script.option.vault.Perm;
import com.github.yuttyann.scriptblockplus.script.option.vault.PermAdd;
import com.github.yuttyann.scriptblockplus.script.option.vault.PermRemove;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;

public final class OptionManager extends AbstractConstructor<Option> {

	private final static List<Holder> OPTION_HOLDERS;
	private final static List<Constructor<? extends Option>> CONSTRUCTORS;

	static {
		OPTION_HOLDERS = new ArrayList<Holder>();
		CONSTRUCTORS = new ArrayList<Constructor<? extends Option>>();
	}

	private static Option[] options;
	private static boolean isModified;

	static class Holder {

		private Integer sort;
		private Class<? extends Option> option;

		public Holder(int sort, Class<? extends Option> option) {
			this.sort = sort;
			this.option = option;
		}

		public boolean equals(Option option) {
			return option != null && this.option == option.getClass();
		}

		public boolean equals(Class<? extends Option> option) {
			return option != null && this.option == option;
		}
	}

	@Override
	public void registerDefaults() {
		getConstructors().clear();
		add(new Holder(9, OldCooldown.class));
		add(new Holder(8, Cooldown.class));
		add(new Holder(7, Delay.class));
		add(new Holder(10, Calculation.class));
		add(new Holder(22, ItemCost.class));
		add(new Holder(6, ItemHand.class));
		add(new Holder(23, MoneyCost.class));
		add(new Holder(16, Group.class));
		add(new Holder(19, Perm.class));
		add(new Holder(1, Bypass.class));
		add(new Holder(0, Command.class));
		add(new Holder(2, Console.class));
		add(new Holder(3, Say.class));
		add(new Holder(4, Server.class));
		add(new Holder(5, ToPlayer.class));
		add(new Holder(14, Sound.class));
		add(new Holder(12, Title.class));
		add(new Holder(13, ActionBar.class));
		add(new Holder(17, GroupAdd.class));
		add(new Holder(18, GroupRemove.class));
		add(new Holder(20, PermAdd.class));
		add(new Holder(21, PermRemove.class));
		add(new Holder(11, Execute.class));
		add(new Holder(15, Amount.class));
	}

	@Deprecated
	@Override
	public boolean add(Class<? extends Option> option) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public boolean add(int index, Class<? extends Option> option) {
		throw new UnsupportedOperationException();
	}

	public boolean add(Holder optionData) {
		return add(-1, optionData);
	}

	public boolean add(int index, Holder optionData) {
		Class<? extends Option> option = optionData.option;
		boolean add = index >= 0 ? super.add(index, option) : super.add(option);
		if (add && (isModified = true)) {
			if (optionData.sort == -1) {
				optionData.sort = OPTION_HOLDERS.size() - 1;
				optionData.sort = optionData.sort < 0 ? 0 : optionData.sort;
			}
			OPTION_HOLDERS.add(optionData);
		}
		return add;
	}

	@Override
	public boolean remove(Class<? extends Option> option) {
		boolean remove = super.remove(option);
		if (remove && (isModified = true)) {
			for (int i = 0; i < OPTION_HOLDERS.size(); i++) {
				if (OPTION_HOLDERS.get(i).equals(option)) {
					OPTION_HOLDERS.remove(i);
					break;
				}
			}
		}
		return remove;
	}

	@Override
	public boolean remove(int index) {
		if (index > getConstructors().size()) {
			return false;
		}
		Constructor<? extends Option> constructor = getConstructors().get(index);
		return constructor != null && getConstructors().remove(constructor);
	}

	@Override
	public List<Constructor<? extends Option>> getConstructors() {
		return CONSTRUCTORS;
	}

	@Override
	public Option[] newInstances() {
		return newInstances(new Option[getConstructors().size()]);
	}

	public Option[] newSortOptions() {
		if (isModified) {
			update();
		}
		Option[] options = newInstances();
		List<Option> result = new ArrayList<Option>(options.length);
		OPTION_HOLDERS.forEach(l -> StreamUtils.filterForEach(options, o -> l.equals(o), result::add));
		return result.size() > 0 ? result.toArray(new Option[result.size()]) : new Option[0];
	}

	public Option[] getOptions() {
		if (options == null || isModified) {
			update();
		}
		return options;
	}

	private void update() {
		isModified = false;
		options = newInstances();
		OPTION_HOLDERS.sort((o1, o2) -> o1.sort.compareTo(o2.sort));
	}
}