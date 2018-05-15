package com.bmtech.utils.restoreable;

import java.util.Set;

public interface ExcludeChecker {

	public boolean hasDone(String name);

	public Set<String> hasDoneSet();

	public int hasDoneNum();

	public void registerFinished(String name);
}
