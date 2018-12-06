package com.github.yuttyann.scriptblockplus.manager;

import java.util.ArrayList;
import java.util.List;

import com.github.yuttyann.scriptblockplus.enums.InstanceType;
import com.github.yuttyann.scriptblockplus.manager.auxiliary.AbstractConstructor;
import com.github.yuttyann.scriptblockplus.manager.auxiliary.SBConstructor;
import com.github.yuttyann.scriptblockplus.script.endprocess.EndInventory;
import com.github.yuttyann.scriptblockplus.script.endprocess.EndMoneyCost;
import com.github.yuttyann.scriptblockplus.script.endprocess.EndProcess;

public final class EndProcessManager extends AbstractConstructor<EndProcess> {

	private static final EndProcessManager END_PROCESS_MANAGER;

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
	protected List<SBConstructor<? extends EndProcess>> initList() {
		return new ArrayList<>();
	}

	@Override
	public void registerDefaults() {
		getConstructors().clear();
		add(new EndInventory());
		add(new EndMoneyCost());
	}

	@Override
	public EndProcess[] newInstances() {
		return newInstances(new EndProcess[getConstructors().size()], InstanceType.SBINSTANCE);
	}
}