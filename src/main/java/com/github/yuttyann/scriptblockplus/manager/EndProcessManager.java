package com.github.yuttyann.scriptblockplus.manager;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import com.github.yuttyann.scriptblockplus.manager.auxiliary.AbstractConstructor;
import com.github.yuttyann.scriptblockplus.script.endprocess.EndInventory;
import com.github.yuttyann.scriptblockplus.script.endprocess.EndMoneyCost;
import com.github.yuttyann.scriptblockplus.script.endprocess.EndProcess;

public final class EndProcessManager extends AbstractConstructor<EndProcess> {

	private final static EndProcessManager END_PROCESS_MANAGER;

	static {
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
	protected List<Constructor<? extends EndProcess>> initialList() {
		return new ArrayList<Constructor<? extends EndProcess>>();
	}

	@Override
	public void registerDefaults() {
		getConstructors().clear();
		add(EndInventory.class);
		add(EndMoneyCost.class);
	}

	@Override
	public EndProcess[] newInstances() {
		return newInstances(new EndProcess[getConstructors().size()]);
	}
}