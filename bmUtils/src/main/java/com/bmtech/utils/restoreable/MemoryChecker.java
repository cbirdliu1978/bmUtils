package com.bmtech.utils.restoreable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MemoryChecker implements ExcludeChecker {

	Set<String> set = Collections.synchronizedSet(new HashSet<>());

	public MemoryChecker() {
	}

	@Override
	public synchronized boolean hasDone(String itemName) {
		return set.contains(itemName);
	}

	@Override
	public synchronized void registerFinished(String name) {
		set.add(name);
	}

	@Override
	public Set<String> hasDoneSet() {
		return set;
	}

	@Override
	public int hasDoneNum() {
		return set.size();
	}

}
