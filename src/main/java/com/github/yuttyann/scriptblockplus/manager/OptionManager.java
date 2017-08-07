package com.github.yuttyann.scriptblockplus.manager;

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

	private ScriptManager scriptManager;
	private List<Option> options;

	public OptionManager(ScriptManager scriptManager) {
		this.scriptManager = scriptManager;
		this.options = scriptManager.getMapManager().getOptions();
	}

	public void registerOptions() {
		addOption(new MoneyCost(scriptManager));
		addOption(new ItemCost(scriptManager));
		addOption(new ItemHand(scriptManager));
		addOption(new Cooldown(scriptManager));
		addOption(new Delay(scriptManager));
		addOption(new Group(scriptManager));
		addOption(new Perm(scriptManager));
		addOption(new GroupAdd(scriptManager));
		addOption(new GroupRemove(scriptManager));
		addOption(new PermAdd(scriptManager));
		addOption(new PermRemove(scriptManager));
		addOption(new Amount(scriptManager));
		addOption(new ToPlayer(scriptManager));
		addOption(new Server(scriptManager));
		addOption(new Say(scriptManager));
		addOption(new Bypass(scriptManager));
		addOption(new Command(scriptManager));
	}

	public void addOption(Option option) {
		options.add(option);
	}

	public void addOption(int index, Option option) {
		options.add(index, option);
	}

	public void removeOption(Option option) {
		options.remove(option);
	}

	public void removeOption(int index) {
		options.remove(index);
	}

	public boolean hasOption() {
		return options.size() > 0;
	}

	public List<Option> getOptions() {
		return options;
	}
}