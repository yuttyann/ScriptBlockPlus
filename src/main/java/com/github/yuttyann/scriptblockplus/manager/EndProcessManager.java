package com.github.yuttyann.scriptblockplus.manager;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

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

	@NotNull
	public static EndProcessManager getInstance() {
		return END_PROCESS_MANAGER;
	}

	@NotNull
	@Override
	protected List<SBConstructor<? extends EndProcess>> newList() {
		return new ArrayList<>();
	}

	@Override
	public void registerDefaults() {
		getConstructors().clear();
		add(new EndInventory());
		add(new EndMoneyCost());
	}

	@NotNull
	@Override
	public EndProcess[] newInstances() {
		return newInstances(new EndProcess[getConstructors().size()], InstanceType.SBINSTANCE);
	}
}