package com.bmtech.utils.restoreable;

import java.util.HashSet;
import java.util.Set;

import com.bmtech.utils.ForEach;
import com.bmtech.utils.bmfs.MDir;

public class MDirChecker implements ExcludeChecker {

	private MDir dir;

	public MDirChecker(MDir dir) throws Exception {
		this.dir = dir;
	}

	@Override
	public synchronized boolean hasDone(String itemName) {
		return dir.getMFileByName(itemName) != null;
	}

	@Override
	public synchronized void registerFinished(String name) {
		if (!hasDone(name)) {
			throw new RuntimeException("register fail! item does not exists in mdir " + dir);
		}
	}

	@Override
	public Set<String> hasDoneSet() {
		Set<String> set = new HashSet<>();
		ForEach.asc(dir.getMFiles(), (mfile, I) -> {
			set.add(mfile.getName());
		});

		return null;
	}

	@Override
	public int hasDoneNum() {
		return this.dir.getMFiles().size();
	}

}
