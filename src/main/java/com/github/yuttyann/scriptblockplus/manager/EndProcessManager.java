package com.github.yuttyann.scriptblockplus.manager;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import com.github.yuttyann.scriptblockplus.manager.auxiliary.AbstractConstructor;
import com.github.yuttyann.scriptblockplus.script.endprocess.EndProcess;
import com.github.yuttyann.scriptblockplus.script.endprocess.ItemCostInit;
import com.github.yuttyann.scriptblockplus.script.endprocess.MoneyCostInit;

public class EndProcessManager extends AbstractConstructor<EndProcess> {

	private final static List<Constructor<? extends EndProcess>> CONSTRUCTORS;

	static {
		CONSTRUCTORS = new ArrayList<Constructor<? extends EndProcess>>();
	}

	@Override
	public void registerDefaults() {
		getConstructors().clear();
		add(ItemCostInit.class);
		add(MoneyCostInit.class);
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