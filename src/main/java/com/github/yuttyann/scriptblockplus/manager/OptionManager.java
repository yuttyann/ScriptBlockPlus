package com.github.yuttyann.scriptblockplus.manager;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
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
import com.github.yuttyann.scriptblockplus.script.option.other.Execute;
import com.github.yuttyann.scriptblockplus.script.option.other.ItemCost;
import com.github.yuttyann.scriptblockplus.script.option.other.ItemHand;
import com.github.yuttyann.scriptblockplus.script.option.other.PlaySound;
import com.github.yuttyann.scriptblockplus.script.option.other.ScriptAction;
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

	private OptionManager() {
		// OptionManager
	}

	public static final class OptionList {

		private static final List<Option> OPTIONS;
		private static final OptionManager OPTION_MANAGER;

		static {
			OPTIONS = new ArrayList<>();
			OPTION_MANAGER = new OptionManager();
			OPTION_MANAGER.registerDefaults();
		}

		public static OptionManager getManager() {
			return OPTION_MANAGER;
		}

		public static List<Option> getList() {
			return Collections.unmodifiableList(OPTIONS);
		}

		public static String[] getNames() {
			return StreamUtils.toArray(OPTIONS, o -> o.getName(), new String[OPTIONS.size()]);
		}

		public static String[] getSyntaxs() {
			return StreamUtils.toArray(OPTIONS, o -> o.getSyntax(), new String[OPTIONS.size()]);
		}

		public static Option[] toArray() {
			return OPTIONS.toArray(new Option[OPTIONS.size()]);
		}
	}

	@Override
	protected ArrayList<Constructor<? extends Option>> initialList() {
		return new ArrayList<Constructor<? extends Option>>();
	}

	@Override
	public void registerDefaults() {
		getConstructors().clear();
		add(ScriptAction.class);
		add(BlockType.class);
		add(Group.class);
		add(Perm.class);
		add(Calculation.class);
		add(OldCooldown.class);
		add(Cooldown.class);
		add(Delay.class);
		add(ItemHand.class);
		add(ItemCost.class);
		add(MoneyCost.class);
		add(Bypass.class);
		add(Command.class);
		add(Console.class);
		add(Say.class);
		add(Server.class);
		add(ToPlayer.class);
		add(PlaySound.class);
		add(Title.class);
		add(ActionBar.class);
		add(GroupAdd.class);
		add(GroupRemove.class);
		add(PermAdd.class);
		add(PermRemove.class);
		add(Execute.class);
		add(Amount.class);
	}

	@Override
	public Option[] newInstances() {
		return newInstances(new Option[getConstructors().size()]);
	}

	@Override
	public boolean add(Class<? extends Option> clazz) {
		boolean result = super.add(clazz);
		if (result) {
			OptionList.OPTIONS.add(newInstance(clazz));
		}
		return result;
	}

	@Override
	public boolean add(int index, Class<? extends Option> clazz) {
		boolean result = super.add(index, clazz);
		if (result) {
			OptionList.OPTIONS.add(index, newInstance(clazz));
		}
		return result;
	}

	@Override
	public boolean remove(Class<? extends Option> clazz) {
		boolean result = super.remove(clazz);
		if (result) {
			OptionList.OPTIONS.remove(newInstance(clazz));
		}
		return result;
	}

	@Override
	public boolean remove(int index) {
		boolean result = super.remove(index);
		if (result) {
			OptionList.OPTIONS.remove(index);
		}
		return result;
	}
}