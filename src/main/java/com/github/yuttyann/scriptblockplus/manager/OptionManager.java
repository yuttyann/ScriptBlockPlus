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
import com.github.yuttyann.scriptblockplus.script.option.other.Amount;
import com.github.yuttyann.scriptblockplus.script.option.other.ItemCost;
import com.github.yuttyann.scriptblockplus.script.option.other.ItemHand;
import com.github.yuttyann.scriptblockplus.script.option.other.Level;
import com.github.yuttyann.scriptblockplus.script.option.time.Cooldown;
import com.github.yuttyann.scriptblockplus.script.option.time.Delay;
import com.github.yuttyann.scriptblockplus.script.option.vault.Group;
import com.github.yuttyann.scriptblockplus.script.option.vault.GroupAdd;
import com.github.yuttyann.scriptblockplus.script.option.vault.GroupRemove;
import com.github.yuttyann.scriptblockplus.script.option.vault.MoneyCost;
import com.github.yuttyann.scriptblockplus.script.option.vault.Perm;
import com.github.yuttyann.scriptblockplus.script.option.vault.PermAdd;
import com.github.yuttyann.scriptblockplus.script.option.vault.PermRemove;

public final class OptionManager extends AbstractConstructor<Option> {

	private final static List<Constructor<? extends Option>> CONSTRUCTORS;

	static {
		CONSTRUCTORS = new ArrayList<Constructor<? extends Option>>();
	}

	@Override
	public void registerDefaults() {
		getConstructors().clear();
		add(Cooldown.class);
		add(Delay.class);
		add(ItemCost.class);
		add(ItemHand.class);
		add(MoneyCost.class);
		add(Level.class);
		add(Group.class);
		add(Perm.class);
		add(Amount.class);
		add(Bypass.class);
		add(Command.class);
		add(Console.class);
		add(Say.class);
		add(Server.class);
		add(ToPlayer.class);
		add(GroupAdd.class);
		add(GroupRemove.class);
		add(PermAdd.class);
		add(PermRemove.class);
	}

	@Override
	public List<Constructor<? extends Option>> getConstructors() {
		return CONSTRUCTORS;
	}

	@Override
	public Option[] newInstances() {
		return newInstances(new Option[getConstructors().size()]);
	}
}