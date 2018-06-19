package com.github.yuttyann.scriptblockplus.manager;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import com.github.yuttyann.scriptblockplus.manager.auxiliary.AbstractConstructor;
import com.github.yuttyann.scriptblockplus.script.endprocess.EndClickAction;
import com.github.yuttyann.scriptblockplus.script.endprocess.EndItemCost;
import com.github.yuttyann.scriptblockplus.script.endprocess.EndMoneyCost;
import com.github.yuttyann.scriptblockplus.script.endprocess.EndProcess;

public final class EndProcessManager extends AbstractConstructor<EndProcess> {

	private final static EndProcessManager END_PROCESS_MANAGER;
	private final static List<Constructor<? extends EndProcess>> CONSTRUCTORS;

	static {
		CONSTRUCTORS = new ArrayList<>();
		END_PROCESS_MANAGER = new EndProcessManager();
		END_PROCESS_MANAGER.registerDefaults();
	}

	private EndProcessManager() {
		// EndProcessManager
	}

	public static EndProcessManager getInstance() {
		return END_PROCESS_MANAGER;
	}

	@Override
	public void registerDefaults() {
		getConstructors().clear();
		add(EndClickAction.class);
		add(EndItemCost.class);
		add(EndMoneyCost.class);
	}

	@Override
	public List<Constructor<? extends EndProcess>> getConstructors() {
		return CONSTRUCTORS;
	}

	@Override
	public EndProcess[] newInstances() {
		return newInstances(new EndProcess[getConstructors().size()]);
	}
}