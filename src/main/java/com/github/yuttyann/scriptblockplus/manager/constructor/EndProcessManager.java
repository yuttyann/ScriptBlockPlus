package com.github.yuttyann.scriptblockplus.manager.constructor;

import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;

import com.github.yuttyann.scriptblockplus.script.ScriptRead.EndProcess;
import com.github.yuttyann.scriptblockplus.script.endprocess.MoneyCostInit;

public class EndProcessManager extends SBConstructor<EndProcess> {

	private final static List<Constructor<? extends EndProcess>> CONSTRUCTORS;

	static {
		CONSTRUCTORS = new LinkedList<Constructor<? extends EndProcess>>();
	}

	public void registerDefaults() {
		CONSTRUCTORS.clear();
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