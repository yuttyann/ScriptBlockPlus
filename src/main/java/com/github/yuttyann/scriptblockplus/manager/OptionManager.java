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
import com.github.yuttyann.scriptblockplus.script.option.other.BlockType;
import com.github.yuttyann.scriptblockplus.script.option.other.Calculation;
import com.github.yuttyann.scriptblockplus.script.option.other.ClickAction;
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

	private final static List<OptionHolder> OPTIONHOLDERS;
	private final static List<Constructor<? extends Option>> CONSTRUCTORS;

	static {
		OPTIONHOLDERS = new ArrayList<OptionHolder>();
		CONSTRUCTORS = new ArrayList<Constructor<? extends Option>>();
	}

	private static Option[] tempOptions;
	private static boolean isModified;

	final class OptionHolder {

		private int sortIndex;
		private final Class<? extends Option> option;

		public OptionHolder(int sortIndex, Class<? extends Option> option) {
			this.sortIndex = sortIndex;
			this.option = option;
		}

		public int getSortIndex() {
			return sortIndex;
		}

		public Class<? extends Option> getOptionClass() {
			return option;
		}

		private OptionHolder updateSortIndex() {
			if (sortIndex < 0) {
				int lastIndex = OPTIONHOLDERS.size() - 1;
				sortIndex = lastIndex < 0 ? 0 : lastIndex;
			}
			return this;
		}

		private int compareTo(OptionHolder holder) {
			return Integer.compare(this.sortIndex, holder.sortIndex);
		}

		public boolean equals(Option option) {
			return equals(option == null ? null : option.getClass());
		}

		public boolean equals(Class<? extends Option> option) {
			return option == null ? false : option == this.option;
		}
	}

	@Override
	public void registerDefaults() {
		getConstructors().clear();
		add(-1, 0, ClickAction.class);
		add(-1, 0, BlockType.class);
		add(-1, 16, Group.class);
		add(-1, 19, Perm.class);
		add(-1, 9, OldCooldown.class);
		add(-1, 8, Cooldown.class);
		add(-1, 7, Delay.class);
		add(-1, 10, Calculation.class);
		add(-1, 6, ItemHand.class);
		add(-1, 22, ItemCost.class);
		add(-1, 23, MoneyCost.class);
		add(-1, 1, Bypass.class);
		add(-1, 0, Command.class);
		add(-1, 2, Console.class);
		add(-1, 3, Say.class);
		add(-1, 4, Server.class);
		add(-1, 5, ToPlayer.class);
		add(-1, 14, Sound.class);
		add(-1, 12, Title.class);
		add(-1, 13, ActionBar.class);
		add(-1, 17, GroupAdd.class);
		add(-1, 18, GroupRemove.class);
		add(-1, 20, PermAdd.class);
		add(-1, 21, PermRemove.class);
		add(-1, 11, Execute.class);
		add(-1, 15, Amount.class);
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
		OPTIONHOLDERS.forEach(l -> StreamUtils.filterForEach(options, o -> l.equals(o), result::add));
		return result.size() > 0 ? result.toArray(new Option[result.size()]) : new Option[0];
	}

	public Option[] getTempOptions() {
		if (tempOptions == null || isModified) {
			update();
		}
		return tempOptions;
	}

	@Deprecated
	@Override
	public boolean add(Class<? extends Option> option) {
		return add(-1, -1, option);
	}

	@Deprecated
	@Override
	public boolean add(int index, Class<? extends Option> option) {
		return add(index, -1, option);
	}

	public boolean add(int scriptIndex, int sortIndex, Class<? extends Option> option) {
		boolean add = scriptIndex >= 0 ? super.add(scriptIndex, option) : super.add(option);
		if (add && (isModified = true)) {
			OPTIONHOLDERS.add(new OptionHolder(sortIndex, option).updateSortIndex());
		}
		return add;
	}

	@Override
	public boolean remove(int index) {
		if (index > getConstructors().size()) {
			return false;
		}
		return remove(getConstructors().get(index).getDeclaringClass());
	}

	@Override
	public boolean remove(Class<? extends Option> option) {
		boolean remove = super.remove(option);
		if (remove && (isModified = true)) {
			for (int i = 0; i < OPTIONHOLDERS.size(); i++) {
				if (OPTIONHOLDERS.get(i).equals(option)) {
					OPTIONHOLDERS.remove(i);
					return true;
				}
			}
		}
		return remove;
	}

	private void update() {
		isModified = false;
		tempOptions = newInstances();
		OPTIONHOLDERS.sort((o1, o2) -> o1.compareTo(o2));
	}
}