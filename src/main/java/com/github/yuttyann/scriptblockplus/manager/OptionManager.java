package com.github.yuttyann.scriptblockplus.manager;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
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

public final class OptionManager extends AbstractConstructor<Option> {

	private final static List<Constructor<? extends Option>> CONSTRUCTORS;

	static {
		CONSTRUCTORS = new ArrayList<Constructor<? extends Option>>();
	}

	private static List<Option> options;
	private static boolean isModified;

	@Override
	public void registerDefaults() {
		getConstructors().clear();
		add(ScriptAction.class);
		add(BlockType.class);
		add(Group.class);
		add(Perm.class);
		add(OldCooldown.class);
		add(Cooldown.class);
		add(Delay.class);
		add(Calculation.class);
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
	public List<Constructor<? extends Option>> getConstructors() {
		return CONSTRUCTORS;
	}

	@Override
	public Option[] newInstances() {
		return newInstances(new Option[getConstructors().size()]);
	}

	public List<Option> getTempOptions() {
		if (options == null || (isModified && !(isModified = false))) {
			options = Collections.unmodifiableList(Arrays.asList(newInstances()));
		}
		return options;
	}

	public boolean add(Class<? extends Option> clazz) {
		isModified = true;
		return super.add(clazz);
	}

	public boolean add(int index, Class<? extends Option> clazz) {
		isModified = true;
		return super.add(index, clazz);
	}

	public boolean remove(Class<? extends Option> clazz) {
		isModified = true;
		return super.remove(clazz);
	}

	public boolean remove(int index) {
		isModified = true;
		return super.remove(index);
	}
}