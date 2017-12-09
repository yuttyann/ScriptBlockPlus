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

	private final static List<Constructor<? extends EndProcess>> CONSTRUCTORS;

	static {
		CONSTRUCTORS = new ArrayList<Constructor<? extends EndProcess>>();
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