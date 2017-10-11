package com.github.yuttyann.scriptblockplus.manager;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.github.yuttyann.scriptblockplus.script.ScriptRead.EndProcess;
import com.github.yuttyann.scriptblockplus.script.endprocess.MoneyCostInit;

public class EndProcessManager {

	private final static EndProcess[] EMPTY_ENDPROCESS_ARRAY = {};
	private final static List<Constructor<? extends EndProcess>> CONSTRUCTORS;

	private List<EndProcess> cacheList;

	static {
		CONSTRUCTORS = new LinkedList<Constructor<? extends EndProcess>>();
	}

	public void registerDefaultProcess() {
		CONSTRUCTORS.clear();
		add(MoneyCostInit.class);
	}

	public void add(Class<? extends EndProcess> endProcess) {
		CONSTRUCTORS.add(getConstructor(endProcess));
	}

	public void add(int index, Class<? extends EndProcess> endProcess) {
		CONSTRUCTORS.add(index, getConstructor(endProcess));
	}

	public void remove(Class<? extends EndProcess> endProcess) {
		CONSTRUCTORS.remove(getConstructor(endProcess));
	}

	public void remove(int index) {
		CONSTRUCTORS.remove(index);
	}

	public int indexOf(Class<? extends EndProcess> endProcess) {
		return CONSTRUCTORS.indexOf(getConstructor(endProcess));
	}

	public boolean isEmpty() {
		return CONSTRUCTORS.isEmpty();
	}

	public List<EndProcess> getEndProcesses() {
		if (cacheList == null) {
			newInstances();
		}
		return Collections.unmodifiableList(cacheList);
	}

	public List<Constructor<? extends EndProcess>> getConstructors() {
		return CONSTRUCTORS;
	}

	public EndProcess[] newInstances() {
		return newInstances(new EndProcess[CONSTRUCTORS.size()]);
	}

	public EndProcess[] newInstances(EndProcess[] endProcesses) {
		EndProcess[] instances = endProcesses;
		try {
			int i = 0;
			for (Constructor<? extends EndProcess> constructor : CONSTRUCTORS) {
				instances[i++] = constructor.newInstance();
			}
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
			return EMPTY_ENDPROCESS_ARRAY;
		}
		return instances;
	}

	public EndProcess newInstance(EndProcess endProcess) {
		try {
			for (Constructor<? extends EndProcess> constructor : CONSTRUCTORS) {
				if (endProcess.getClass().equals(constructor.getDeclaringClass())) {
					return constructor.newInstance();
				}
			}
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
		return null;
	}

	private <T extends EndProcess> Constructor<T> getConstructor(Class<T> clazz) {
		try {
			Constructor<T> constructor = clazz.getDeclaredConstructor();
			constructor.setAccessible(true);
			return constructor;
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<EndProcess> clearCache() {
		if (cacheList == null) {
			cacheList = new ArrayList<EndProcess>(CONSTRUCTORS.size());
		} else {
			cacheList.clear();
		}
		return cacheList;
	}
}