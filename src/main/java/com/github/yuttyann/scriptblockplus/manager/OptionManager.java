package com.github.yuttyann.scriptblockplus.manager;

import com.github.yuttyann.scriptblockplus.enums.InstanceType;
import com.github.yuttyann.scriptblockplus.manager.auxiliary.AbstractConstructor;
import com.github.yuttyann.scriptblockplus.manager.auxiliary.SBConstructor;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.script.option.chat.*;
import com.github.yuttyann.scriptblockplus.script.option.other.*;
import com.github.yuttyann.scriptblockplus.script.option.time.Cooldown;
import com.github.yuttyann.scriptblockplus.script.option.time.Delay;
import com.github.yuttyann.scriptblockplus.script.option.time.OldCooldown;
import com.github.yuttyann.scriptblockplus.script.option.vault.*;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class OptionManager extends AbstractConstructor<Option> {

	private OptionManager() {
		// OptionManager
	}

	public static final class OptionList {

		private static final List<Option> OPTIONS;
		private static final List<Option> UNMOD_OPTIONS;
		private static final OptionManager OPTION_MANAGER;

		static {
			UNMOD_OPTIONS = Collections.unmodifiableList(OPTIONS = new ArrayList<>());
			OPTION_MANAGER = new OptionManager();
			OPTION_MANAGER.registerDefaults();
		}

		@NotNull
		public static OptionManager getManager() {
			return OPTION_MANAGER;
		}

		@NotNull
		public static List<Option> getList() {
			return UNMOD_OPTIONS;
		}

		@NotNull
		public static String[] getNames() {
			return StreamUtils.toArray(OPTIONS, Option::getName, new String[OPTIONS.size()]);
		}

		@NotNull
		public static String[] getSyntaxs() {
			return StreamUtils.toArray(OPTIONS, Option::getSyntax, new String[OPTIONS.size()]);
		}

		@NotNull
		public static Option[] toArray() {
			return OPTIONS.toArray(new Option[OPTIONS.size()]);
		}
	}

	@NotNull
	@Override
	protected List<SBConstructor<? extends Option>> newList() {
		return new ArrayList<>();
	}

	@Override
	public void registerDefaults() {
		getConstructors().clear();
		add(new ScriptAction());
		add(new BlockType());
		add(new Group());
		add(new Perm());
		add(new Calculation());
		add(new OldCooldown());
		add(new Cooldown());
		add(new Delay());
		add(new ItemHand());
		add(new ItemCost());
		add(new MoneyCost());
		add(new Say());
		add(new Server());
		add(new ToPlayer());
		add(new PlaySound());
		add(new Title());
		add(new ActionBar());
		add(new Bypass());
		add(new Command());
		add(new Console());
		add(new GroupAdd());
		add(new GroupRemove());
		add(new PermAdd());
		add(new PermRemove());
		add(new Execute());
		add(new Amount());
	}

	@NotNull
	@Override
	public Option[] newInstances() {
		return newInstances(new Option[getConstructors().size()], InstanceType.SBINSTANCE);
	}

	@Override
	public boolean add(@NotNull SBConstructor<? extends Option> constructor) {
		boolean result = super.add(constructor);
		if (result) {
			OptionList.OPTIONS.add(constructor.getInstance());
		}
		return result;
	}

	@Override
	public boolean add(int index, @NotNull SBConstructor<? extends Option> constructor) {
		boolean result = super.add(index, constructor);
		if (result) {
			OptionList.OPTIONS.add(index, constructor.getInstance());
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